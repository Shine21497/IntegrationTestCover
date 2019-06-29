<template>
  <div id="leftSide" class="left-side">
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
              @getuploadfiles="getuploadedFiles"
              @gettestcasemap="getTestCaseMap"
              @showtestresult="showTestResult"
              @canceltestresult="cancelTestResult"
            />
          </el-container>
        </el-card>
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="未覆盖测试用例" name="6">
        <el-card :body-style="{ padding: '0px' }" class="card">
          <el-container class="formbody">
            <uncoverCard />
          </el-container>
        </el-card>
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="添加节点" name="7">
        <el-card :body-style="{ padding: '0px' }" class="card">
          <el-container class="formbody">
            <!-- <el-form ref="form" :model="selectTestForm" label-width="80px">
              <el-form-item label="现有节点">
                <el-cascader
                  v-model="cascaderNode"
                  :options="cascaderClassMethod"
                  @visible-change="praseClassMethod"
                  @change="selectNode"
                ></el-cascader>
              </el-form-item>
              <el-form-item label="新节点">
                <el-input type="textarea" v-model="newnode" placeholder="请输入新节点名称"></el-input>
              </el-form-item>
              <el-form-item label="类型选择">
                <el-select
                  v-model="selectednodetype"
                  filterable
                  placeholder="请选择节点类型"
                  @change="getnodetype()"
                >
                  <el-option label="数据库" value="1"></el-option>
                  <el-option label="外设" value="2"></el-option>
                  <el-option label="前端" value="3"></el-option>
                  <el-option label="其他系统" value="4"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="createNewNode()">立即创建</el-button>
              </el-form-item>
            </el-form>-->
          </el-container>
        </el-card>
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="回归测试" name="8">
        <!-- <el-card :body-style="{ padding: '0px 5px' }" class="card">
          <el-row :gutter="20" style="margin:10px 0">
            <el-col :span="6" style="padding:5px 0;">项目选择</el-col>
            <el-col :span="18" style="padding:0;">
              <el-select
                v-model="regression.info.oldJarName"
                placeholder="请选择进行回归测试的项目"
                @visible-change="showfilelist"
                @change="getRegressionProj($event)"
              >
                <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-input
            type="textarea"
            autosize
            placeholder="请输入包范围"
            v-model="regression.info.packageName"
        ></el-input>-->
        <!-- action="https://jsonplaceholder.typicode.com/posts/" -->
        <!-- <el-upload
            style="margin:10px 0;height:100px"
            ref="uploadjar"
            action="/apiurl/uploadRegressiveJar"
            :file-list="regression.jarFiles"
            :on-change="fileListChange"
            :on-remove="fileListChange"
            :on-success="uploadSucc"
            :limit="1"
            :auto-upload="false"
          >
            <el-button
              slot="trigger"
              size="small"
              type="primary"
              :disabled="regression.disable"
            >{{regression.status}}</el-button>
            <el-button
              style="margin-left: 10px;"
              size="small"
              type="success"
              @click="UploadJars"
              :disabled="!regression.info.newJarName || !regression.info.oldJarName"
            >上传</el-button>
            <div slot="tip" class="el-upload__tip">请上传新版本的Jar包</div>
          </el-upload>
        </el-card>
        <el-row :gutter="20" style="margin:10px 0">
          <el-col :span="18" style="padding:0;text-align:left">
            <el-select v-model="regression.chosedInfo" placeholder="做过的回归测试">
              <el-option
                v-for="item in history.regressionInfos"
                :key="item.name"
                :label="item.name"
                :value="item.name"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="6" style="padding:5px 0;">
            <el-button
              size="small"
              type="success"
              @click="analyseHistory"
              :disabled="!regression.chosedInfo"
            >分析</el-button>
          </el-col>
        </el-row>
        <div v-if="oldvsnew.length">
          <div class="list-header">
            <el-row>
              <el-col :span="5">分析结果</el-col>
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
        </div>-->
      </el-collapse-item>
      <el-collapse-item class="titlestyle" title="脚本录制" name="9">
        <!-- 选择框，选择要回放的测试用例 -->
        <!-- <el-button size="small" type="primary" @click="prepareRecord">准备录制</el-button>
        <el-button size="small" type="primary" @click="watchReplay">查看回放</el-button>
        <el-dialog :visible.sync="preparerecord" width="80%">
          <iframe
            ref="frame"
            src="https://www.baidu.com"
            width="1024"
            height="768"
            style="-webkit-transform:scale(0.8);-moz-transform-scale(0.8);"
          ></iframe>

          <span slot="footer" class="dialog-footer">
            <el-button size="small" type="primary" @click="startRecord">开始录制</el-button>
          </span>
        </el-dialog>
        <el-dialog :visible.sync="showReplay" width="60%">
          <Player ref="replayer" />
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="toggleReplay">{{playandpause}}</el-button>
          </span>
        </el-dialog>-->
      </el-collapse-item>
    </el-collapse>
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
import uploadCard from "@/components/uploadCard.vue";
import graphCard from "@/components/graphCard.vue";
import locateCard from "@/components/locateCard.vue";
import uploadTestCard from "@/components/uploadTestCard.vue";
import runTestCard from "@/components/runTestCard.vue";
import uncoverCard from "@/components/uncoverCard.vue";
// -- custom components

export default {
  components: {
    uploadCard,
    graphCard,
    locateCard,
    uploadTestCard,
    runTestCard,
    uncoverCard
  },
  name: "method-call-relation-graph",
  props: {},
  data() {
    return {
      // ui 部分
      activeNames: [], // 默认展开
      toggle: true, // 收缩控制栏

      // data 部分
      uploadedFiles: [], // （已上传的项目）调用关系图 <w/r>，上传测试用例<w/r>，运行测试用例<w/r>
      relation: {}, // 调用关系图<w/r>
      allClasses: [], // 调用关系图<w/r>，节点定位<r>
      classMethodMap: {}, // 调用关系图<w/r>，节点定位<r>
      defaultProject: "", // 调用关系图<w/r>，上传测试用例<r>，运行测试用例<r>
      testCaseMap: {} // 运行测试用例<w/r>
    };
  },
  methods: {
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
            //   _this.regression.oldVersion = selectedjar; // 回归测试部分
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
        console.log(this.testCaseMap);
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