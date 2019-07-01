<template>
  <div>
    <el-form label-width="80px">
      <el-form-item label="项目选择">
        <el-select
          filterable
          v-model="selectedTestProject"
          placeholder="请选择项目"
          @visible-change="showTestProjectList"
          @change="selectTestProject($event)"
        >
          <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="测试文件">
        <el-select
          filterable
          :disabled="allTestFiles.length == 0"
          v-model="selectedTestFile"
          placeholder="选择测试文件"
          @change="selectTestFile($event)"
        >
          <el-option v-for="item in allTestFiles" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <div style="margin: 15px 0;">
        <el-button type="primary" size="small" @click="startRunTestCase()">执行用例</el-button>
        <el-button
          type="primary"
          size="small"
          @click="showEffect()"
          :disabled="testResult.length === 0"
          v-if="noeffect"
        >显示效果</el-button>
        <el-button
          type="primary"
          size="small"
          @click="cancelShow()"
          :disabled="testResult.length === 0"
          v-else
        >取消效果</el-button>
        <el-button
          type="primary"
          size="small"
          @click="showReport()"
          :disabled="testResult.length === 0"
        >显示报告图</el-button>
      </div>
      <el-progress :percentage="runTestPercentange"></el-progress>
    </el-form>

    <div>
      <el-row style="margin:10px 0">
        <div>分支总数：{{calculation.branchNum}}</div>
        <div>执行用例数：{{calculation.usecaseNum}}</div>
        <div>未覆盖分支数：{{calculation.uncoverNum}}</div>
        <div>覆盖率：{{calculation.coverRate}}%</div>
      </el-row>
      <svg id="piechart" xmlns="http://www.w3.org/2000/svg" height="150" width="330" />
      <el-row :gutter="20" style="margin:10px 0">
        <el-col :span="18">
          <el-select v-model="selectedUncoverTest" placeholder="请选择未覆盖的边">
            <el-option v-for="item in uncover" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6" style="padding:0;">
          <el-button
            style="padding: 13px 15px;"
            type="primary"
            size="small"
            @click="gotoUncover()"
          >立即定位</el-button>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
import * as d3 from "d3";

import {
  runTestCase, // 执行测试用例，需要三个参数 (projectname, testcasename, method)，返回值为任务 [id<str>,type<str>]
  getTestRunningStatus, // 获取指定任务的执行进度，需要一个参数 (task_id_Key）
  getInvokingResults // 获取测试用例执行结果，需要一个参数 (task_id_Key）
} from "@/api/methodcallrelationgraph.js";
import { truncate } from "fs";

export default {
  name: "runTestCard",
  props: {
    uploadedFiles: Array,
    defaultProject: String,
    testCaseMap: Object,
    relation: Object
  },
  data() {
    return {
      // ui
      runTestPercentange: 0,
      noeffect: true,
      showreport: false,

      // data
      selectedTestProject: "",
      selectedTestFile: "",
      selectedTestCase: "allMethods",
      allTestFiles: [], // 所有的测试脚本文件
      allTestCases: [], // 所有的测试用例（函数）
      testResult: [], // 测试结果
      selectedUncoverTest: "", // 定位未覆盖节点
      uncoverfullname: [], // 获取测试结果时生成的数组  "A CALL B"
      uncover: [], // 未覆盖分支数等于这玩意儿的长度，"A call B"
      calculation: {
        branchNum: 0,
        usecaseNum: 0,
        uncoverNum: 0,
        coverRate: 0
      }
    };
  },
  computed: {},
  methods: {
    async showTestProjectList(open) {
      if (open) {
        if (!this.uploadedFiles.length) {
          this.$emit("getuploadfiles");
        }
      }
    },
    selectTestProject(prov) {
      this.selectedTestFile = "";
      var prjName = prov.split(".")[0];
      // prov is "demo.jar" but testCaseMap is {"demo":{...}}
      this.prepareTestFile(prjName);
    },
    prepareTestFile(prjName) {
      // 已存在 testCaseMap
      if (Object.keys(this.testCaseMap).length > 0) {
        if (this.testCaseMap[prjName])
          this.allTestFiles = Object.keys(this.testCaseMap[prjName]);
        else {
          this.$message.error(
            '不存在项目"' +
              prjName +
              '"的测试用例，请上传该项目的测试用例，目前有以下项目的测试用例 [' +
              Object.keys(this.testCaseMap)
                .toString()
                .slice(0, 30) +
              "]"
          );
          this.allTestFiles = [];
          this.allTestCases = [];
        }
      }
      // 不存在 testCaseMap
      else {
        // 获取 testCaseMap
        this.$emit("gettestcasemap");
      }
    },
    selectTestFile(prov) {
      let Prj = this.selectedTestProject.split(".")[0];
      this.allTestCases = this.testCaseMap[Prj][prov];
    },
    startRunTestCase(file) {
      this.cancelShow();
      var projectname = this.selectedTestProject;
      var testcasename = this.selectedTestFile;
      var method = this.selectedTestCase;
      if (
        !projectname ||
        !testcasename ||
        (testcasename != "allTestFiles" && !method)
      ) {
        this.$message({
          showClose: true,
          message: "请选择完整的项目，测试类以及测试方法"
        });
        return;
      }
      // 传参数给后端跑测试用例
      runTestCase(projectname, testcasename, method).then(response => {
        // response 为 ["12123123","many"]
        if (response[2]) return; // error msg
        this.taskId = response[0];
        this.taskType = response[1];
        // 归零
        this.runTestPercentange = 0;
        // 开始监听运行进度
        this._onTestRunning();
      });
    },
    // 获取测试进度的时候要调用的
    _onTestRunning() {
      try {
        getTestRunningStatus(this.taskId).then(response => {
          if (response[2]) return; // error msg
          if (response[0] === "sorry,no this task~") {
            this.$message({
              showClose: true,
              message: "测试用例未能成功执行，请评审上传的测试用例"
            });
            return;
          }

          this.$nextTick(() => {
            let a = parseInt((response[0] * 100) / response[1]);
            if (a) {
              this.runTestPercentange = a;
            }
          });
        });
      } catch (error) {}
      if (this.runTestPercentange != 100) {
        setTimeout(this._onTestRunning, 500);
      } else {
        this.$message({
          showClose: true,
          message: "测试用例执行完毕，可以查看效果",
          type: "success"
        });
        this.getTestResult();
      }
    },
    getTestResult() {
      this.uncoverfullname = [];
      getInvokingResults(this.taskId).then(response => {
        // 这里的 response 为测试用例的结果，一个 list
        // 展示测试用例的结果
        this.testResult = this.uniq(response);

        // 未覆盖分支数
        if (this.relation.links) {
          for (let index in this.relation.links) {
            var callrelation =
              this.relation.links[index].source.name +
              " CALL " +
              this.relation.links[index].target.name;
            if (this.testResult.indexOf(callrelation) < 0)
              this.uncoverfullname.push(callrelation);
          }

          this.setUncover();
        }
      });
    },
    uniq(array) {
      var temp = []; //一个新的临时数组
      for (var i = 0; i < array.length; i++) {
        if (temp.indexOf(array[i]) == -1) {
          temp.push(array[i]);
        }
      }
      return temp;
    },
    showEffect() {
      this.noeffect = false;
      this.$emit("showtestresult", {
        TestResult: this.testResult,
        taskType: this.taskType
      });
    },
    cancelShow() {
      this.noeffect = true;
      this.$emit("canceltestresult");
    },
    //获取未覆盖信息
    setUncover() {
      this.uncover = [];
      for (let index in this.uncoverfullname) {
        var temp = this.uncoverfullname[index].split(" ");
        var A = temp[0].split(":")[1];
        var B = temp[2].split(":")[1];
        if (this.uncover.indexOf(A + " call " + B) < 0)
          this.uncover.push(A + " call " + B);
      }
    },
    showReport() {
      if (this.relation.links) {
        this.showreport = true;
        this.calculation.branchNum = this.calculate_branchNum();
        this.calculation.usecaseNum = this.calculate_usecaseNum();
        this.calculation.uncoverNum = this.calculate_uncoverNum();
        this.calculation.coverRate = this.calculate_coverRate();
        var data = [
          {
            name: "已覆盖分支数",
            val: this.calculation.branchNum - this.calculation.uncoverNum
          },
          { name: "未覆盖分支数：", val: this.calculation.uncoverNum }
        ];
        this.$nextTick(() => {
          this.drawPie(data);
        });
      } else {
        this.$message({
          showClose: true,
          message: "请先生成调用关系图"
        });
      }
    },
    drawPie(data) {
      var w = 130,
        h = 150,
        r = Math.min(w, h) / 2,
        labelr = r + 30, // radius for label anchor
        color = d3.scaleOrdinal(d3.schemeCategory10),
        donut = d3.pie(),
        arc = d3
          .arc()
          .innerRadius(r * 0.6)
          .outerRadius(r);

      var vis = d3
        .select("#piechart")
        .data([data])
        .attr("width", w + 200)
        .attr("height", h);

      // 移除上一个图（如果有的话）
      if (vis.selectAll("g").size() > 0) {
        vis.selectAll("g").remove();
      }

      var arcs = vis
        .selectAll("g.arc")
        .data(
          donut.value(function(d) {
            return d.val;
          })
        )
        .enter()
        .append("svg:g")
        .attr("class", "arc")
        .attr("transform", "translate(" + (r + 10) + "," + (r + 10) + ")");

      arcs
        .append("svg:path")
        .attr("fill", function(d, i) {
          return color(i);
        })
        .attr("d", arc);

      var legends = vis
        .append("g")
        .attr("transform", "translate(150,10)")
        .selectAll(".legends")
        .data(data);

      var legend = legends
        .enter()
        .append("g")
        .classed("legends", true)
        .attr("transform", function(d, i) {
          return "translate(0," + (i + 1) * 30 + ")";
        });

      legend
        .append("rect")
        .attr("width", 20)
        .attr("height", 20)
        .attr("fill", function(d, i) {
          return color(i);
        });

      legend
        .append("text")
        .text(function(d) {
          return d.name + ":" + d.val;
        })
        .attr("fill", function(d, i) {
          return color(i);
        })
        .attr("x", 30)
        .attr("y", 15);

    },
    calculate_branchNum() {
      if (this.relation.links) {
        return this.relation.links.length;
      }
      return 0;
    },
    calculate_usecaseNum() {
      if (this.selectedTestFile == "allTestFiles") {
        var sum = 0;
        for (let index in this.testCaseMap[
          this.selectedTestProject.split(".")[0]
        ]) {
          sum += this.testCaseMap[this.selectedTestProject.split(".")[0]][index]
            .length;
        }
        return sum;
      } else {
        if (this.selectedTestCase == "allMethods") {
          return this.testCaseMap[this.selectedTestProject.split(".")[0]][
            this.selectedTestFile
          ].length;
        } else {
          return 1;
        }
      }
    },
    calculate_uncoverNum() {
      return this.uncover.length;
    },
    calculate_coverRate() {
      if (this.calculation.branchNum) {
        return (
          (100 * (this.calculation.branchNum - this.calculation.uncoverNum)) /
          this.calculation.branchNum
        );
      }
      return "-";
    },
    //定位到未覆盖边
    gotoUncover() {
      var selectA = this.selectedUncoverTest.split(" ")[0];
      var selectB = this.selectedUncoverTest.split(" ")[2];
      for (let index in this.uncoverfullname) {
        var temp = this.uncoverfullname[index].split(" ");
        var A = temp[0].split(":")[1];
        var B = temp[2].split(":")[1];
        if (selectA == A && selectB == B) {
          this.$emit("locatenode", temp[0]);
        }
      }
    }
  },
  created() {},
  mounted() {},
  watch: {
    // 设置默认项目
    defaultProject(val) {
      this.selectedTestProject = this.defaultProject;
      this.prepareTestFile(this.selectedTestProject.split(".")[0]);
    },
    // 等待从后端获取 testCaseMap
    testCaseMap(val) {
      // 如果已经选了项目，就直接更新
      let selectedPrj = this.selectedTestProject;
      if (selectedPrj.length) {
        this.prepareTestFile(selectedPrj.split(".")[0]);
      }
    }
  }
};
</script>

<style lang="less">
.el-progress .el-progress-bar {
  padding: 0;
}
.el-progress .el-progress__text {
  margin: -30px 0 0 0;
  float: right;
}
</style>