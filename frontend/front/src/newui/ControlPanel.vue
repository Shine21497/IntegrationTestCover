<template>
  <!-- <div id="leftSide" class="left-side">
    <h2>
      Method-Call-Relation-Graph
      <i
        id="shrink-icon"
        class="funny el-icon-arrow-left"
        @click="shrink_open()"
      ></i>
    </h2>

    <el-collapse v-model="activeNames">
      <el-collapse-item class="titlestyle" title="上传项目" name="1">
        <uploadCard />
      </el-collapse-item>
      <el-collapse-item class="titlestyle" name="2">
        <template slot="title">
          <p class="itemname">生成调用关系图</p>
          <p class="require-info">（请先上传项目）</p>
        </template>
        <el-container class="formbody">
          <graphCard
            @generateGraph="generateGraph"
            @getuploadfiles="getuploadedFiles"
            :uploadedFiles="uploadedFiles"
          />
        </el-container>
      </el-collapse-item>
      <el-collapse-item
        class="titlestyle"
        name="3"
        :class="JSON.stringify(relation)==='{}'?'disabled': ''"
      >
        <template slot="title">
          <p class="itemname">定位方法</p>
          <p class="require-info">（请先生成调用关系图）</p>
        </template>
        <el-card :body-style="{ padding: '0px' }" class="card">
          <el-container class="formbody">
            <locateCard
              @locatenode="locatenode"
              :classMethodMap="classMethodMap"
              :allClasses="allClasses"
            />
          </el-container>
        </el-card>
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="上传测试用例" name="4">
        <el-card :body-style="{ padding: '0px' }" class="card">
          <el-container class="formbody">
            <uploadTestCard
              :uploadedFiles="uploadedFiles"
              :defaultProject="defaultProject"
              @getuploadfiles="getuploadedFiles"
            />
          </el-container>
        </el-card>
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="运行测试用例" name="5">
        <el-card :body-style="{ padding: '0px' }" class="card">
          <el-container class="formbody">
            <runTestCard
              :defaultProject="defaultProject"
              :uploadedFiles="uploadedFiles"
              :testCaseMap="testCaseMap"
              :relation="relation"
              @getuploadfiles="getuploadedFiles"
              @gettestcasemap="getTestCaseMap"
              @showtestresult="showTestResult"
              @canceltestresult="cancelTestResult"
              @locatenode="locatenode"
            />
          </el-container>
        </el-card>
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="添加新节点" name="7">
        <el-card :body-style="{ padding: '0px' }" class="card">
          <el-container class="formbody">
            <addNodeCard @addnewnode="addNewNode" :classMethodMap="classMethodMap" />
          </el-container>
        </el-card>
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="回归测试" name="8">
        <regressionCard
          :testCaseMap="testCaseMap"
          :defaultProject="defaultProject"
          :uploadedFiles="uploadedFiles"
          @gettestcasemap="getTestCaseMap"
          @getuploadfiles="getuploadedFiles"
        />
      </el-collapse-item>
    </el-collapse>
  </div>-->
  <div>
    <div class="center-message" v-if="JSON.stringify(relation)==='{}'">{{messageTip}}</div>
    <div class="button-holder">
      <div v-if="expanded">
        <el-tooltip class="item" effect="dark" content="收缩状态栏" placement="left">
          <i class="el-icon-arrow-up" @click="expanded = false"></i>
        </el-tooltip>
      </div>
      <div v-else>
        <el-tooltip class="item" effect="dark" content="展开状态栏" placement="left">
          <i class="el-icon-arrow-down" @click="expanded = true"></i>
        </el-tooltip>
      </div>
      <div>
        <el-popover placement="bottom-end" trigger="click">
          <locateCard
            @locatenode="locatenode"
            :classMethodMap="classMethodMap"
            :allClasses="allClasses"
          />
          <el-tooltip slot="reference" class="item" effect="dark" content="定位节点" placement="left">
            <i class="el-icon-location"></i>
          </el-tooltip>
        </el-popover>
      </div>
      <div>
        <el-dropdown trigger="click" @command="showUpload">
          <span class="el-dropdown-link" style="font-size: 36px;">
            <el-tooltip slot="reference" class="item" effect="dark" content="上传" placement="left">
              <i class="el-icon-upload"></i>
            </el-tooltip>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="project">新项目</el-dropdown-item>
            <el-dropdown-item command="testcase">测试用例</el-dropdown-item>
            <el-dropdown-item command="regression">回归测试新版本</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div>
        <el-popover placement="bottom-end" trigger="click">
          <addNodeCard @addnewnode="addNewNode" :classMethodMap="classMethodMap" />
          <el-tooltip slot="reference" class="item" effect="dark" content="增加新节点" placement="left">
            <i class="el-icon-circle-plus-outline"></i>
          </el-tooltip>
        </el-popover>
      </div>
    </div>
    <div class="top-status-bar center-bar" :class="!expanded ? 'shrink-top':''"></div>
    <div class="bottom-control-bar">
      <el-popover placement="top-start" trigger="click">
        <graphCard
          @generateGraph="generateGraph"
          @getuploadfiles="getuploadedFiles"
          :uploadedFiles="uploadedFiles"
        />
        <el-button slot="reference">生成调用关系图</el-button>
      </el-popover>
      <el-popover placement="top" trigger="click">
        <runTestCard
          :defaultProject="defaultProject"
          :uploadedFiles="uploadedFiles"
          :testCaseMap="testCaseMap"
          :relation="relation"
          @getuploadfiles="getuploadedFiles"
          @gettestcasemap="getTestCaseMap"
          @showtestresult="showTestResult"
          @canceltestresult="cancelTestResult"
          @locatenode="locatenode"
        />
        <el-button slot="reference">运行测试用例</el-button>
      </el-popover>
      <el-popover placement="top-end" trigger="click">
        <regressionCard
          :testCaseMap="testCaseMap"
          :defaultProject="defaultProject"
          :uploadedFiles="uploadedFiles"
          @gettestcasemap="getTestCaseMap"
          @getuploadfiles="getuploadedFiles"
          @uploadfile="showUpload"
        />
        <el-button slot="reference">回归测试</el-button>
      </el-popover>
    </div>
    <uploadCard
      ref="uploader"
      :uploadedFiles="uploadedFiles"
      :defaultProject="defaultProject"
      @getuploadfiles="getuploadedFiles"
    />
  </div>
</template>

<script>
// !-- fetch apis
import {
  getRelationByFileName,
  getUploadedFileList,
  getTestCaseList
} from "@/api/methodcallrelationgraph.js";
// -- fetch apis

// !-- custom components
import uploadCard from "@/newui/cards/uploadCard.vue";
import graphCard from "@/newui/cards/graphCard.vue";
import locateCard from "@/newui/cards/locateCard.vue";
import runTestCard from "@/newui/cards/runTestCard.vue";
import addNodeCard from "@/newui/cards/addNodeCard.vue";
import regressionCard from "@/newui/cards/regressionCard.vue";
// -- custom components

export default {
  components: {
    uploadCard,
    graphCard,
    locateCard,
    runTestCard,
    addNodeCard,
    regressionCard
  },
  name: "method-call-relation-graph",
  props: {},
  data() {
    return {
      // ui 部分
      activeNames: [], // 默认展开
      toggle: true, // 控制栏展开时为 true
      expanded: true, // 控制栏展开时为 true
      messageTip: "还未生成调用关系图", // 中间的文字提示

      // data 部分
      uploadedFiles: [], // （已上传的项目）调用关系图 <w/r>，上传测试用例<w/r>，运行测试用例<w/r>
      relation: {}, // 调用关系图<w/r>，未覆盖率<r>
      allClasses: [], // 调用关系图<w/r>，节点定位<r>
      classMethodMap: {}, // 调用关系图<w/r>，节点定位<r>
      defaultProject: "", // 调用关系图<w/r>，上传测试用例<r>，运行测试用例<r>，回归测试<r>
      testCaseMap: {} // 运行测试用例<w/r>，回归测试<w/r>
    };
  },
  methods: {
    showUpload(type, params = {}) {
      // this.upload.uploadProject = true;
      this.$refs.uploader.upload(type, params);
    },
    // 生成图
    generateGraph(params) {
      let _this = this;
      this.$nextTick(() => {
        const { selectedjar, packages, packagesCall } = params;
        getRelationByFileName(selectedjar, packages, packagesCall).then(
          response => {
            _this.relation.nodes = response.nodes;
            _this.relation.links = response.links;
            _this.allClasses = response.classes;
            _this.classMethodMap = response.classMethodMap;

            _this.$emit("generateGraph", _this.relation);

            // 选择默认将项目设为此项目
            this.defaultProject = selectedjar;
          }
        );
      });
    },
    // 获取已上传的项目文件
    getuploadedFiles() {
      getUploadedFileList().then(response => {
        this.uploadedFiles = response.result;
      });
    },
    getTestCaseMap() {
      getTestCaseList().then(response => {
        this.testCaseMap = response.result;
      });
    },
    // 节点定位
    locatenode(name) {
      var node = this.findNodeByName(name);
      this.$emit("locatenode", node);
    },
    findNodeByName(name) {
      for (let index in this.relation.nodes) {
        if (this.relation.nodes[index].name == name) {
          return this.relation.nodes[index];
        }
      }
    },
    showTestResult(result) {
      this.$emit("displayResult", result);
    },
    cancelTestResult() {
      this.$emit("cancelResult");
    },
    addNewNode(params) {
      this.$emit("addNewNode", params);
    },
    // ui
    shrink_open() {
      if (this.toggle) {
        this.actived = this.activeNames;
        this.activeNames = [];
        setTimeout(() => {
          document
            .getElementById("shrink-icon")
            .classList.remove("el-icon-arrow-left");
          document
            .getElementById("shrink-icon")
            .classList.add("el-icon-arrow-right");
          document.getElementById("leftSide").style.transform =
            "translate(-100%, 0)";
          document.getElementById("leftSide").style.overflow = "visible";
        }, 500);
      } else {
        setTimeout(() => {
          document.getElementById("leftSide").style.overflow = "";
          this.activeNames = this.actived;
          document
            .getElementById("shrink-icon")
            .classList.remove("el-icon-arrow-right");
          document
            .getElementById("shrink-icon")
            .classList.add("el-icon-arrow-left");
        }, 500);
        document.getElementById("leftSide").style.transform = "";
      }
      this.toggle = !this.toggle;
    }
  },
  created() {
    this.$nextTick(() => {
      var historyForm = JSON.parse(localStorage.getItem("history"));
      if (historyForm) {
        this.history = historyForm;
      }
      // 生成饼状图调试
      // this.drawPie();
    });
  },
  watch: {
    relation: ""
  }
};
</script>

<style lang="less">
.center-message {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
}

.button-holder {
  position: absolute;
  right: 20px;
  top: 30px;
  font-size: 36px;
}

.button-holder i:hover {
  color: #0583f2;
  cursor: pointer;
}

.center-bar {
  left: 50%;
  transform: translateX(-50%);
  position: fixed;
  transition: transform 0.5s ease-in-out;
}

.top-status-bar {
  top: 0;
  width: 500px;
  height: 100px;
  background-color: antiquewhite;
}

.shrink-top {
  transform: translate(-50%, -100%);
}

.bottom-control-bar {
  bottom: 20px;
  right: 20px;
  position: fixed;
  transition: transform 0.5s ease-in-out;
}

.shrink-bottom {
  transform: translateY(100%);
}

.disabled {
  pointer-events: none;
}

.disabled .itemname {
  color: gray;
}

.disabled .require-info {
  color: #ff0000;
  opacity: 1;
}

.titlestyle {
  font-family: "Microsoft YaHei";
  font-weight: bold;
}

.formbody {
  margin: 20px 0px 0px 0px;
}
.funny {
  position: fixed;
  margin-left: 33px;
  background-color: white;
  border-radius: 0 5px 5px 0;
  box-shadow: lightgrey 5px 0px 5px 2px;
}

.funny:hover {
  cursor: pointer;
  color: #0583f2;
}
.left-side {
  position: absolute;
  width: 350px;
  transition: 0.5s ease;
  background-color: white;
  box-shadow: lightgrey 0px 0px 5px 5px; //边框内阴影
  top: 0;
  left: 0;
  padding: 0 15px;
  max-height: 100%;
  overflow-y: auto;
}

.left-side::-webkit-scrollbar {
  /*滚动条整体样式*/
  width: 4px;
}

.left-side::-webkit-scrollbar-thumb {
  /*滚动条里面小方块*/
  border-radius: 5px;
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  background: rgba(0, 0, 0, 0.2);
}

.left-side::-webkit-scrollbar-track {
  /*滚动条里面轨道*/
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 0;
}
</style>