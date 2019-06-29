<template>
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
    <el-form-item label="测试用例">
      <el-select
        ref="selectTestMethod"
        filterable
        :disabled="allTestCases.length == 0"
        v-model="selectedTestCase"
        placeholder="选择测试用例"
      >
        <el-option v-for="item in allTestCases" :key="item" :label="item" :value="item"></el-option>
      </el-select>
    </el-form-item>
    <el-progress v-if="isTesting" :percentage="runTestPercentange"></el-progress>
    <el-form-item>
      <el-button type="primary" size="small" @click="startRunTestCase()">执行用例</el-button>
      <el-button
        type="primary"
        size="small"
        @click="showEffect()"
        :disabled="testResult.length === 0"
      >显示效果</el-button>
      <el-button
        type="primary"
        size="small"
        @click="cancelShow()"
        :disabled="testResult.length === 0"
      >取消效果</el-button>
    </el-form-item>
  </el-form>
</template>

<script type="text/ecmascript-6">
import {
  runTestCase, // 执行测试用例，需要三个参数 (projectname, testcasename, method)，返回值为任务 [id<str>,type<str>]
  getTestRunningStatus, // 获取指定任务的执行进度，需要一个参数 (task_id_Key）
  getInvokingResults // 获取测试用例执行结果，需要一个参数 (task_id_Key）
} from "@/api/methodcallrelationgraph.js";

export default {
  name: "runTestCard",
  props: {
    uploadedFiles: Array,
    defaultProject: String,
    testCaseMap: Object
  },
  data() {
    return {
      selectedTestProject: "",
      selectedTestFile: "",
      selectedTestCase: "",
      allTestFiles: [],
      allTestCases: [],
      runTestPercentange: 0,
      isTesting: false,
      testResult: []
    };
  },
  methods: {
    async showTestProjectList(open) {
      if (open) {
        if (!this.uploadedFiles.length) {
          this.$emit("getuploadfiles");
        }
      }
    },
    selectTestProject(prov) {
      this.selectedTestCase = "";
      this.selectedTestFile = "";
      // this.uncoverfullname=[];
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
      this.selectedTestCase = "";
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
        this.isTesting = true;
        // 开始监听运行进度
        this._onTestRunning();
      });
      // if(this.relation.links)
      //     this.setUncover();
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
            this.runTestPercentange = parseInt(
              (response[0] * 100) / response[1]
            );
          });
        });
      } catch (error) {}
      if (this.runTestPercentange != 100) {
        setTimeout(this._onTestRunning, 500);
      } else {
        this.isTesting = false;
        this.$message({
          showClose: true,
          message: "测试用例执行完毕，可以查看效果",
          type: "success"
        });
        this.getTestResult();
      }
    },
    getTestResult() {
      getInvokingResults(this.taskId).then(response => {
        // 这里的 response 为测试用例的结果，一个 list
        // 展示测试用例的结果
        this.testResult = this.uniq(response);
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
      this.$emit("showtestresult", {
        TestResult: this.testResult,
        taskType: this.taskType
      });
    },
    cancelShow() {
        this.$emit("canceltestresult")
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
      if (selectedPrj) {
        this.prepareTestFile(selectedPrj.split(".")[0]);
      }
    }
  }
};
</script>

<style  scoped lang="less">
</style>