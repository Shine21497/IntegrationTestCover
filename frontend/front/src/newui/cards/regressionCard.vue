<template>
  <!-- <div>
    <el-row :gutter="20" style="margin:10px 0;text-align: left;">
      <el-col :span="6" style="padding:5px;">旧版本</el-col>
      <el-col :span="18" style="padding:0;">
        <el-select
          v-model="regression.info.oldVersion"
          placeholder="请选择进行回归测试的旧版本项目"
          @visible-change="showTestProjectList"
          @change="getVersions($event)"
        >
          <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin:10px 0;text-align: left;">
      <el-col :span="6" style="padding:5px;">新版本</el-col>
      <el-col :span="18" style="padding:0;">
        <el-select v-model="regression.info.newVersion" placeholder="请选择新版本">
          <el-option v-for="item in regression.versions" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin:10px 0;text-align: left;">
      <el-col :span="17" style="padding:5px;">
        <el-input
          type="textarea"
          autosize
          placeholder="请输入包范围"
          v-model="regression.info.packageName"
        ></el-input>
      </el-col>
      <el-col :span="6" style="padding:5px;">
        <el-button size="small" type="success" @click="startRegTest">开始测试</el-button>
      </el-col>
    </el-row>
    <div v-if="oldvsnew.length">
      <div class="list-header">
        <el-row>
          <el-col :span="5">被影响的测试用例</el-col>
          <el-col :span="19">
            <el-checkbox-group v-model="filterList" @change="filterChange">
              <el-checkbox label="未影响"></el-checkbox>
              <el-checkbox label="影响的"></el-checkbox>
            </el-checkbox-group>
          </el-col>
        </el-row>
      </div>
      <div class="list-container">
        <el-tooltip
          v-for="(testcase,index) in showoldvsnew"
          :key="index"
          :content="['(不变)','(有影响)'][testcase.state] + testcase.casename"
          placement="right"
          effect="light"
        >
          <div
            class="hjr-list-item"
            :style="'border-left-color:'+ ['red','green'][testcase.state] + ';'"
          >{{testcase.casename}}</div>
        </el-tooltip>
      </div>
    </div>
    <el-button type="text" @click="regression.uploadNewVersion = true">找不到想测的版本？那就上传吧</el-button>
    <el-card v-if="regression.uploadNewVersion" :body-style="{ padding: '10px 5px' }" class="card">
      <el-upload
        style="margin:10px 0;height:100px"
        ref="uploadjar"
        action="/apiurl/uploadRegressiveJar"
        :file-list="regression.jarFiles"
        :on-success="uploadSucc"
        :limit="3"
        :auto-upload="false"
        :data="{ projectName: regression.info.oldVersion.split('.jar')[0] }"
      >
        <el-button slot="trigger" size="small" type="primary">选择要上传的版本Jar包</el-button>
        <el-button style="margin-left: 10px;" size="small" type="success" @click="UploadJars">上传</el-button>
      </el-upload>
    </el-card>
  </div>-->
  <div>
    <span>
      <div class="card-head">
        受影响的测试用例
      </div>
      <div class="list-container">
        <el-tooltip
          v-for="(testcase,index) in showoldvsnew"
          :key="index"
          :content="['(不变)','(有影响)'][testcase.state] + testcase.casename"
          placement="right"
          effect="light"
        >
          <div
            class="hjr-list-item"
            :style="'border-left-color:'+ ['red','green'][testcase.state] + ';'"
          >{{testcase.casename}}</div>
        </el-tooltip>
      </div>
    </span>
    <span>
      <div class="card-head">
        <span>回归测试</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="startRegTest">开始测试</el-button>
      </div>
      <el-form label-position="left" label-width="80px">
        <el-form-item label="旧版本">
          <el-select
            v-model="regression.info.oldVersion"
            placeholder="请选择进行回归测试的旧版本项目"
            @visible-change="showTestProjectList"
            @change="getVersions($event)"
          >
            <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="新版本">
          <el-select v-model="regression.info.newVersion" placeholder="请选择新版本">
            <el-option v-for="item in regression.versions" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-button
          type="text"
          style="padding: 0;margin-left:144px"
          @click="uploadNewVersion"
        >找不到想测的版本？那就上传吧</el-button>
        <el-form-item label="包范围">
          <el-input
            type="textarea"
            autosize
            placeholder="请输入包范围"
            v-model="regression.info.packageName"
          ></el-input>
        </el-form-item>
      </el-form>
    </span>
  </div>
</template>

<script type="text/ecmascript-6">
import {
  postRegression, // 回归测试上传新旧包，传的参数为 regression.info
  getVersionsofPrj // 获取项目所有的版本
} from "@/api/methodcallrelationgraph.js";
//  <!-- action="https://jsonplaceholder.typicode.com/posts/" -->
export default {
  name: "regressionCard",
  props: {
    testCaseMap: Object,
    defaultProject: String,
    uploadedFiles: Array
  },
  data() {
    return {
      // ui
      showoldvsnew: [], // 存在筛选，所以要一个专门用于展示的
      filterList: ["未影响", "已影响"], // 过滤回归测试结果测试用例

      // data
      regression: {
        // 回归测试用的新旧版本 Jar 包
        jarFiles: [], // 新 Jar 包列表
        project: "", // 进行回归测试的项目
        versions: [], // 绑定下拉框
        info: {
          oldVersion: "",
          newVersion: "",
          packageName: ""
        },
        oldcases: {} // 所有的测试用例
      },
      oldvsnew: [] // 用于新旧版本项目测试用例的对比
    };
  },
  methods: {
    uploadNewVersion() {
      let data = {
        projectName: this.regression.info.oldVersion.split(".jar")[0]
      };
      this.$emit("uploadfile", "regression", data);
    },
    async showTestProjectList(open) {
      if (open) {
        if (!this.uploadedFiles.length) {
          this.$emit("getuploadfiles");
        }
      }
    },
    // 选择要进行 回归测试 的项目
    getVersions(prov) {
      var prjName = prov.split(".")[0];
      // 获取项目已上传的所有版本
      getVersionsofPrj(prjName).then(response => {
        // 绑定下拉框
        this.regression.versions = response;
      });
      // 回归测试时获取旧版本的所有测试用例
      try {
        this.regression.oldcases[prov] = Object.keys(this.testCaseMap[prjName]);
      } catch (error) {
        // 不存在 testcasemap，从后端获取
        this.$emit("gettestcasemap");
      }
    },
    startRegTest() {
      this.analyseJars(this.regression.info);
    },
    uploadSucc(resp, file, filelist) {
      let prjName = regression.info.oldVersion.split(".")[0];
      this.showMsg("上传'" + file.name + "'成功");
    },
    UploadJars() {
      this.$refs.uploadjar.submit();
    },
    // 筛选展示的结果
    filterChange(filters) {
      let filterMap = {
        未影响: 0,
        已影响: 1
      };
      this.showoldvsnew = []; // refresh list to show
      if (filters.length === 2) this.showoldvsnew = this.oldvsnew;
      else
        filters.forEach(fil => {
          this.showoldvsnew = this.showoldvsnew.concat(
            this.oldvsnew.filter(testcase => testcase.state === filterMap[fil])
          );
        });
    },
    // 获取并分析
    analyseJars(para, time = 0) {
      // 检查 oldVersion 这个项目的所有用例是否获取
      if (this.regression.oldcases[para.oldVersion]) {
        postRegression(para).then(response => {
          let newcases = response; // "oldcases" is all testcases
          this.filterList = ["未影响", "已影响"]; // set filter to all
          this.oldvsnew = []; // refresh result

          this.regression.oldcases[para.oldVersion].forEach(testcase => {
            if (newcases.includes(testcase)) {
              // 有影响的
              this.oldvsnew.push({
                state: 1,
                casename: testcase
              });
            }
          });
          this.showoldvsnew = this.oldvsnew;
        });
      } else {
        if (time < 3) {
          let _this = this;
          setTimeout(() => {
            _this.analyseJars(para, time + 1);
          }, 500);
        }
      }
    }
  },
  created() {},
  mounted() {},
  watch: {
    // 设置默认项目
    defaultProject(val) {
      this.regression.info.oldVersion = this.defaultProject;
    },
    // 等待从后端获取 testCaseMap
    testCaseMap(val) {
      let prjName = this.regression.info.oldVersion;
      if (prjName.length) {
        let cases = this.testCaseMap[prjName.split(".")[0]];
        if (cases) {
          this.regression.oldcases[prjName] = Object.keys(cases);
        }
      }
    }
  }
};
</script>

<style  scoped lang="less">
.card-head {
  padding: 10px 0;
  margin: 0 0 15px;
  border-bottom: 1px solid gray;
}

.list-header {
  border-bottom: 1px solid gray;
  margin: 10px 0 6px;
}
.list-container {
  height: 300px;
  overflow: auto;
}

.list-container::-webkit-scrollbar {
  width: 4px;
}

.list-container::-webkit-scrollbar-thumb {
  border-radius: 5px;
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  background: rgba(0, 0, 0, 0.2);
}

.list-container::-webkit-scrollbar-track {
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 0;
}

.hjr-list-item {
  text-align: left;
  padding: 8px;
  cursor: pointer;
  border-width: 0 0 0 4px;
  border-style: solid;
  margin: 2px 0;
}

.hjr-list-item:hover {
  background-color: lightgray;
}
</style>