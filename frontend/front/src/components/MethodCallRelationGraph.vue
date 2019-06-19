<template>
    <el-container>
        <el-main style="padding:0;">
            <div id="container" class="container">
                <svg id="svgCanvas">
                    <defs>
                        <!--节点滤波器-->
                        <filter id="f1">
                            <feGaussianBlur in="SourceGraphic" stdDeviation="5"/>
                        </filter>
                    </defs>
                    <defs>
                        <!--直线滤波器-->
                        <filter id="f2">
                            <feGaussianBlur in="SourceGraphic" stdDeviation="2"/>
                        </filter>
                    </defs>
                </svg>
            </div>
            <!-- <div id="thumb-container">
                <svg id="thumb" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 1000 1000">
                    <use xlink:href="#svgCanvas" />
                </svg>
            </div> -->
        </el-main>

        <div id="leftSide" class="left-side">
            <h2>Method Call Relation Graph
                <i id="shrink-icon" class="funny el-icon-arrow-left" @click="shrink_open()"></i>
            </h2>

            <el-collapse v-model="activeNames">
                <el-collapse-item class="titlestyle" title="上传项目" name="1">
                    <el-card :body-style="{ padding: '0px' }" class="card">
                        <el-upload class="upload" action="/apiurl/uploadJar" accept="application/jar" :before-upload="onBeforeUpload" ref="upload" :file-list="fileList" :auto-upload="false">
                            <el-button slot="trigger" type="primary">选取文件</el-button>
                            <el-button style="margin-left: 10px;" type="success" @click="submitUpload">上传到服务器</el-button>
                            <div slot="tip" class="el-upload__tip">只能上传jar文件</div>
                        </el-upload>
                    </el-card>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" name="2">
                    <template slot="title">
                        <p class="itemname">调用关系图生成</P>
                        <p class="require-info">（请先上传项目）</P>
                    </template>
                    <el-container class="formbody">
                        <div ref="form" :model="form" style="width:100%;text-align:left">
                            <el-row :gutter="20" style="margin:10px 0">
                                <el-col :span="6">Jar包选择</el-col>
                                <el-col :span="18">
                                    <el-select v-model="form.selectedjar" placeholder="请选择jar包" @visible-change="showfilelist">
                                        <el-option
                                                v-for="item in uploadedFiles"
                                                :key="item"
                                                :label="item"
                                                :value="item">
                                        </el-option>
                                    </el-select>
                                </el-col>
                            </el-row>
                            <div>
                                <div style="color:darkgray;margin: 0 0 10px 10px;"> 如果打包时把lib一同打入，一定要输入包的范围 </div>
                                <el-row :gutter="20" style="margin:10px 0">
                                    <el-col :span="6">遍历包范围</el-col>
                                        <el-col :span="18">
                                        <el-autocomplete class="inline-input" v-model="form.packages" :fetch-suggestions="packagesHistory"
                                            placeholder="请输入遍历包范围"
                                        ></el-autocomplete>
                                        </el-col>
                                    </el-row>
                            </div>
                            <div>
                                <div style="color:darkgray;margin:0 0 10px 10px;"> 如果打包时把lib一同打入，一定要输入包的范围 </div>
                                <el-row :gutter="20" style="margin:10px 0">
                                    <el-col :span="6">生成包范围</el-col>
                                    <el-col :span="18">
                                        <el-autocomplete class="inline-input" v-model="form.packagesCall" :fetch-suggestions="packagesCallHistory"
                                            placeholder="请输入遍历包范围"
                                        ></el-autocomplete>
                                    </el-col>
                                </el-row>
                            </div>
                            <div style="text-align:center">
                                <el-button type="primary" :disabled="form.selectedjar.length == 0" size="small" @click="generateGraph()">立即创建</el-button>
                            </div>
                        </div>
                    </el-container>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" name="3" :class="JSON.stringify(relation)=='{}'?'disabled': ''">
                    <template slot="title">
                        <p class="itemname">定位方法</P>
                        <p class="require-info">（请先生成调用关系图）</P>
                    </template>
                    <el-card :body-style="{ padding: '0px' }" class="card">
                        <el-container class="formbody">
                            <el-form ref="adjustForm" :model="adjustForm" label-width="80px">
                                <el-form-item label="类选择">
                                    <el-select filterable  v-model="adjustForm.selectedClass" placeholder="请选择类" @change="getClass($event)">
                                        <el-option
                                                v-for="item in adjustForm.allClasses"
                                                :key="item"
                                                :label="item"
                                                :value="item">
                                        </el-option>
                                    </el-select>
                                </el-form-item>
                                <el-form-item label="方法选择">
                                    <el-select filterable  v-model="adjustForm.selectedMethod" placeholder="请选择方法">
                                        <el-option
                                                v-for="item in adjustForm.allMethods"
                                                :key="item"
                                                :label="item"
                                                :value="item">
                                        </el-option>
                                    </el-select>
                                </el-form-item>
                                <el-form-item>
                                    <el-button :disabled="adjustForm.selectedMethod.length == 0" type="primary" size="small" @click="goToNode()">立即定位</el-button>
                                </el-form-item>
                            </el-form>
                        </el-container>
                    </el-card>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" title="上传测试用例" name="4">
                    <el-card :body-style="{ padding: '0px' }" class="card">
                        <el-container class="formbody">
                            <el-form ref="uploadTestData" :model="uploadTestData" label-width="80px">
                                <el-form-item label="被测项目">
                                    <el-select v-model="uploadTestData.selectedProject" placeholder="请选择项目">
                                        <el-option
                                            v-for="item in uploadedFiles"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                        </el-option>
                                    </el-select>
                                </el-form-item>
                                <el-upload class="upload" action="/apiurl/uploadTestCase" accept="application/jar" :before-upload="onBeforeUploadTestCase" ref="uploadTest" :file-list="fileList" :auto-upload="false" :data="uploadTestData">
                                    <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
                                    <el-button style="margin-left: 10px;" size="small" type="success" @click="submitTestUpload">上传到服务器</el-button>
                                    <div slot="tip" class="el-upload__tip">只能上传java文件或者zip文件</div>
                                </el-upload>
                            </el-form>
                        </el-container>
                    </el-card>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" title="运行测试用例" name="5">
                        <el-card :body-style="{ padding: '0px' }" class="card">
                            <el-container class="formbody">
                                <el-form ref="selectTestForm" :model="selectTestForm" label-width="80px">
                                    <el-form-item label="项目选择">
                                        <el-select ref="selectTestProject" filterable  v-model="selectTestForm.selectedTestProject" placeholder="请选择项目" @change="getTestProject($event)" @visible-change="showTestProjectList">
                                            <el-option
                                                    v-for="item in uploadedFiles"
                                                    :key="item"
                                                    :label="item"
                                                    :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="类选择">
                                        <el-select ref="selectTestClass" filterable  :disabled="Object.entries(testCaseMap).length == 0 || selectTestForm.allTestClasses.length == 0" v-model="selectTestForm.selectedTestClass" placeholder="请选择测试类" @change="getTestClass($event)">
                                            <el-option
                                                    v-for="item in selectTestForm.allTestClasses"
                                                    :key="item"
                                                    :label="item"
                                                    :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="方法选择">
                                        <el-select ref="selectTestMethod" filterable  :disabled="Object.entries(testCaseMap).length == 0 || selectTestForm.allTestCases.length == 0" v-model="selectTestForm.selectedTestCase" placeholder="请选择测试方法">
                                            <el-option
                                                    v-for="item in selectTestForm.allTestCases"
                                                    :key="item"
                                                    :label="item"
                                                    :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-progress :text-inside="true" :stroke-width="18" :percentage="runTestPercentange"></el-progress>
                                    <el-form-item>
                                            <el-button type="primary" size="small" @click="startRunTestCase()">执行用例</el-button>
                                            <el-button type="primary" size="small" @click="getTestResult()" :disabled="runTestPercentange!=100">显示效果</el-button>
                                            <el-button type="primary" size="small" @click="cancelShow()" :disabled="runTestPercentange!=100">取消效果</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-container>
                        </el-card>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" title="未覆盖测试用例" name="6">
                        <el-card :body-style="{ padding: '0px' }" class="card">
                            <el-container class="formbody">
                               <el-form ref="form" :model="selectTestForm" label-width="80px">
                                  <el-form-item label="未覆盖分支选择">
                                        <el-select v-model="selectTestForm.selectUncoverTest" placeholder="请选择未覆盖的边">
                                            <el-option
                                            v-for="item in uncover"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item>
                                        <div id="branches">分支总数：{{branchnum}}；</div>
                                        <div id="usecase">执行用例数：{{usecasenum}}；</div>
                                        <div id="uncoverbranch">未覆盖分支数：{{uncoverlength}}；</div>
                                        <div id="coverrate">覆盖率：{{coverrate}}%：</div>
                                    </el-form-item>
                                    <el-form-item>
                                    <el-button type="primary" @click="gotoUncover()">立即定位</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-container>
                        </el-card>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" title="添加节点" name="7">
                    <el-card :body-style="{ padding: '0px' }" class="card">
                        <el-container class="formbody">
                            <el-form ref="form" :model="selectTestForm" label-width="80px">
                                <el-form-item label="现有节点">
                                    <el-cascader
                                        v-model="cascaderNode"
                                        :options="cascaderClassMethod"
                                        @visible-change="praseClassMethod"
                                        @change="selectNode">
                                    </el-cascader>
                                </el-form-item>
                                <el-form-item label="新节点">
                                    <el-input type="textarea" v-model="newnode" placeholder="请输入新节点名称"></el-input>
                                </el-form-item>
                                <el-form-item label="类型选择">
                                    <el-select v-model="selectednodetype" filterable placeholder="请选择节点类型" @change="getnodetype()">
                                        <el-option label="数据库" value="1"> </el-option>
                                        <el-option label="外设" value="2"> </el-option>
                                        <el-option label="前端" value="3"> </el-option>
                                        <el-option label="其他系统" value="4"> </el-option>
                                    </el-select>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="createNewNode()">立即创建</el-button>
                                </el-form-item>
                            </el-form>
                        </el-container>
                    </el-card>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" title="回归测试" name="8">
                    <el-card :body-style="{ padding: '0px 5px' }" class="card">
                        <el-row :gutter="20" style="margin:10px 0">
                            <el-col :span="6" style="padding:5px 0;">项目选择</el-col>
                            <el-col :span="18" style="padding:0;">
                                <el-select
                                    v-model="regression.info.oldJarName" 
                                    placeholder="请选择进行回归测试的项目" 
                                    @visible-change="showfilelist" 
                                    @change="getRegressionProj($event)">
                                    <el-option
                                        v-for="item in uploadedFiles"
                                        :key="item"
                                        :label="item"
                                        :value="item">
                                    </el-option>
                                </el-select>
                            </el-col>
                        </el-row>
                        <el-input
                            type="textarea"
                            autosize
                            placeholder="请输入包范围"
                            v-model="regression.info.packageName">
                        </el-input>
                        <!-- action="https://jsonplaceholder.typicode.com/posts/" -->
                        <el-upload
                            style="margin:10px 0;height:100px"
                            ref="uploadjar"
                            action="/apiurl/uploadRegressiveJar"
                            :file-list="regression.jarFiles"
                            :on-change="fileListChange"
                            :on-remove="fileListChange"
                            :on-success="uploadSucc"
                            :limit="1"
                            :auto-upload="false">
                            <el-button slot="trigger" size="small" type="primary" :disabled="regression.disable">{{regression.status}}</el-button>
                            <el-button style="margin-left: 10px;" size="small" type="success" @click="UploadJars"  :disabled="!regression.info.newJarName || !regression.info.oldJarName">上传</el-button>
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
                                    :value="item.name">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="6" style="padding:5px 0;">
                            <el-button size="small" type="success" @click="analyseHistory" :disabled="!regression.chosedInfo">分析</el-button>
                        </el-col>
                    </el-row>
                    <div v-if="oldvsnew.length">
                        <div class="list-header">
                            <el-row>
                                <el-col :span="5">
                                    分析结果
                                </el-col>
                                <el-col :span="19">
                                    <el-checkbox-group v-model="filterList" @change="filterChange">
                                        <el-checkbox label="remain"></el-checkbox>
                                        <el-checkbox label="affected"></el-checkbox>
                                    </el-checkbox-group>
                                </el-col>
                            </el-row>
                        </div>
                        <div class="list-container">
                            <el-tooltip v-for="(testcase,index) in showoldvsnew" :key="index" :content="['(不变)','(有影响)'][testcase.state] + testcase.casename" placement="right" effect="light">
                                <div class="hjr-list-item" :style="'border-left-color:'+ ['red','green'][testcase.state] + ';'">{{testcase.casename}}</div>
                            </el-tooltip>
                        </div>
                    </div>
                </el-collapse-item>
                <el-collapse-item class="titlestyle" title="脚本录制" name="9">
                    <!-- 选择框，选择要回放的测试用例 -->
                    <el-button size="small" type="primary" @click="watchReplay">查看回放</el-button>
                    <el-dialog
                        :visible.sync="showReplay"
                        width="60%">
                        <Player ref="replayer"/>
                        <span slot="footer" class="dialog-footer">
                            <el-button type="primary" @click="toggleReplay">{{playandpause}}</el-button>
                        </span>
                    </el-dialog>
                </el-collapse-item>
            </el-collapse>
        </div>
    </el-container>
</template>

<script>
    import ElContainer from "element-ui/packages/container/src/main";
    import ElButton from "element-ui/packages/button/src/button";
    import { 
        getUploadedFileList, 
        getRelationByFileName, 
        getTestCaseList, 
        runTestCase,          // 执行测试用例，需要三个参数 (projectname, testcasename, method)，返回值为任务 [id<str>,type<str>]
        getTestRunningStatus, // 获取指定任务的执行进度，需要一个参数 (task_id_Key）
        getInvokingResults,   // 获取测试用例执行结果，需要一个参数 (task_id_Key）
        postRegression,       // 回归测试上传新旧包，传的参数为 regression.info
    } from '@/api/methodcallrelationgraph.js'
    import * as d3 from 'd3'
    import { setInterval } from 'timers';
    import { Promise } from 'q';
    import * as rrweb from "rrweb";
    import Player from '@/components/Player.vue';

    export default {
        components: {
            ElButton,
            ElContainer,
            Player
        },
        name: "method-call-relation-graph",
        data () {
            return {
                TestResult:[],
                uploadTestData: {
                    selectedProject: ''
                },
                relation: {},
                cname: '',
                fileList:[],
                form: {
                    selectedjar: '',
                    packages:'',
                    packagesCall:''
                },
                adjustForm: {
                    selectedClass: '',
                    selectedMethod: '',
                    allClasses: [],
                    allMethods: [],
                },
                selectTestForm: {
                    selectedTestProject: '',
                    selectedTestClass: '',
                    selectedTestCase: '',
                    allTestClasses: [],
                    allTestCases: [],
                    selectUncoverTest:[],
                },
                uploadedFiles:[],
                uncover:[],
                uncoverlength:0,
                coverrate:'',
                uncoverfullname:[],
                usecasenum:0,
                branchnum:0,
                selectednode:'',
                newnode:'',
                selectednodetype:'',
                classMethodMap:{"a": ["a","b"], "b": ["a","c"]},
                testCaseMap:{},
                g:{},
                tempTrans: d3.zoomIdentity.translate(0, 0).scale(1),
                activeNames: ['1'],  //加上这个不然控制台老报错
                runTestPercentange:0,
                taskId:'',
                taskType:'',      // "many" 和 "one" 
                toggle:true,
                Isfirstnode:true,
                history:{
                    packages:[],
                    packagesCall:[],
                    regressionInfos:[], // store historic regression infos
                },
                regression:{          // 回归测试用的新旧版本 Jar 包
                    jarFiles: [],     // 新 Jar 包列表
                    info:{
                        oldJarName:'',
                        newJarName:'',
                        packageName:'',
                    },
                    status:'选择新版本Jar包', // ui
                    disable:false,           // ui
                    oldcases:[],             // 所有的测试用例
                    chosedInfo:"",
                },
                oldvsnew:[],          // 用于新旧版本项目测试用例的对比
                showoldvsnew:[],      // 存在筛选，所以要一个专门用于展示的
                filterList:["remain", "affected"],    // 过滤回归测试结果测试用例
                events:[],            // 脚本录制,
                showReplay:false,     // 回放
                isRecording:false,    // 是否正在录制
                isplay:true,
                playandpause:'暂停',
                cascaderClassMethod:[],  // 添加节点板块的级联选择，已有节点
                cascaderNode:[],
            }
        },
        methods: {
// --- card 上传项目
            submitUpload() {
                this.$refs.upload.submit();
            },
            onBeforeUpload(file) {
                const isJAR = file.name.endsWith('.jar')
                if (!isJAR) {
                    this.$message.error('只能上传Jar文件!');
                    return false;
                }
                return isJAR
            },
// -- card 上传项目

// --- card 调用关系图的生成
            showfilelist(open){
                if(open) {
                    let _this = this
                    getUploadedFileList().then(response => {
                        _this.uploadedFiles = response.result
                    })
                }
            },
            packagesHistory(queryString, cb) {
                cb(this.history.packages);
            },
            packagesCallHistory(queryString, cb) {
                // cb([{ "value": "asdasd"}]);
                cb(this.history.packagesCall);
            },
            generateGraph(){
                let _this = this
                this.$nextTick(() => {
                    getRelationByFileName(this.form.selectedjar, this.form.packages, this.form.packagesCall).then(response => {
                        _this.relation.nodes = response.nodes
                        _this.relation.links = response.links
                        _this.adjustForm.allClasses = response.classes
                        _this.classMethodMap = response.classMethodMap
                        try {
                            _this.showd3()

                            // 选择默认将项目设为此项目
                            _this.uploadTestData.selectedProject = _this.form.selectedjar;      // 上传测试用例部分
                            _this.selectTestForm.selectedTestProject = _this.form.selectedjar;  // 运行测试用例部分
                        } catch (error) {
                            if (!_this.relation.nodes) {
                                this.$message.error("生成调用图失败，可能的原因是：遍历程序包方法时出错");
                            }
                            if (!_this.relation.links) {
                                this.$message.error("生成调用图失败，可能的原因是：解析调用关系时出错");
                            }
                        }
                    })
                    // 保存历史记录
                    if (this.history.packagesCall.filter(item => item.value == this.form.packagesCall).length == 0) {
                        this.history.packagesCall.push({"value":this.form.packagesCall})
                    }
                    if (this.history.packages.filter(item => item.value == this.form.packages).length == 0) {
                        this.history.packages.push({"value":this.form.packages})
                    }
                    localStorage.setItem('history',JSON.stringify(this.history));
                })
            },
// -- card 调用关系图的生成

// --- card 定位方法
            getClass(prov) {
                this.adjustForm.allMethods = this.classMethodMap[prov]
                this.adjustForm.selectedMethod = '';
            },
            goToNode() {
                var node = this.findNodeByName(this.adjustForm.selectedClass + ":" + this.adjustForm.selectedMethod)
                var trans = this.tempTrans

                trans.k = 1;
                this.g.attr('transform',trans);
                // 根据视野大小定位
                var width = document.getElementById('container').offsetWidth;
                var height = document.getElementById('container').offsetHeight;
                
                trans.x = (Math.round(width/2) - node.x) * trans.k
                trans.y = (Math.round(height/2) - node.y) * trans.k
                
                this.g.attr('transform', trans)
            },
// -- card 定位方法

// --- card 上传测试用例
            submitTestUpload() {
                this.$refs.uploadTest.submit();
                this.selectTestForm.selectedTestProject = ''
                this.selectTestForm.selectedTestClass = ''
                this.selectTestForm.selectedTestCase = ''
                this.testCaseMap = {}
            },
            onBeforeUploadTestCase(file) {
                const isJava = file.name.endsWith('.java')
                const isZip = file.name.endsWith('.zip')
                if (!isJava && !isZip) {
                    this.$message.error('只能上传Java文件或者Zip文件!');
                    return false;
                } else {
                    return true
                }
            },
// -- card 上传测试用例

// --- card 运行测试用例
            getTestProject(prov) {
                this.selectTestForm.selectedTestCase = '';
                this.selectTestForm.selectedTestClass = '';
                this.uncoverfullname=[];
                var prjName = prov.split('.')[0];
                // prov is "demo.jar" but testCaseMap is {"demo":{...}}
                this.showTestClass(prjName,1)
            },
            showTestClass(prjName,time){
                if(this.testCaseMap[prjName])
                    this.selectTestForm.allTestClasses = Object.keys(this.testCaseMap[prjName]);
                else{
                    if (time > 3) {
                        this.$message.error('不存在项目"' + prjName + '"的测试用例，请上传该项目的测试用例，目前有以下项目的测试用例 [' + Object.keys(this.testCaseMap).toString().slice(0,30) + ']');
                        this.selectTestForm.allTestClasses = [];
                        this.selectTestForm.allTestCases = [];
                    }
                    else
                        getTestCaseList().then(response=>{
                            this.testCaseMap = response.result;
                            console.log(this.testCaseMap)
                            this.showTestClass(prjName,time+1);
                        })
                }
            },
            getTestClass(prov) {
                this.selectTestForm.selectedTestCase = '';
                this.selectTestForm.allTestCases =  this.testCaseMap[this.selectTestForm.selectedTestProject.split('.')[0]][prov]
            },
            async showTestProjectList(open) {
                if(open) {
                    const [{ result: uploadedFiles }, { result: testCaseMap }] 
                        = await Promise.all([getUploadedFileList(), getTestCaseList()])
                    //response is {"result":{"demo":{"TestMethod.java":["allMehtods"],"allTestFiles":[],"Test2.java":["allMehtods"]}}}
                    this.$nextTick(() => {
                        this.uploadedFiles = uploadedFiles;
                        this.testCaseMap = testCaseMap;
                        if (this.selectTestForm.selectedTestProject) {
                            this.getTestProject(this.selectTestForm.selectedTestProject)
                        }
                    })
                }
            },
            startRunTestCase(file) {
                this.uncoverfullname=[];
                if(this.TestResult!=null){
                    this.cancelShow(this.TestResult);
                }
                var projectname  = this.selectTestForm.selectedTestProject;
                var testcasename = this.selectTestForm.selectedTestClass;
                var method       = this.selectTestForm.selectedTestCase;
                if(!projectname || !testcasename || (testcasename != 'allTestFiles' && !method)){
                    this.showMsg('请选择完整的项目，测试类以及测试方法')
                    return
                }
                let _this = this;
                // 传参数给后端跑测试用例
                runTestCase(projectname, testcasename, method).then(response => {// response 为 ["12123123","many"]
                    if(response[2]) return; // error msg
                    _this.taskId = response[0];
                    _this.taskType = response[1];
                    // 归零
                    _this.runTestPercentange = 0;
                    // 开始监听运行进度
                    _this._onTestRunning();
                })
                // if(this.relation.links) 
                //     this.setUncover();
            },            // 获取测试进度的时候要调用的
            _onTestRunning(){
                let _this = this;
                getTestRunningStatus(_this.taskId).then(response => {  // 这里的response[0] 和 [1]可能要改，看后端数据结构
                    if(response[2]) return; // error msg
                    if(response[0] === "sorry,no this task~")
                        return _this.showMsg("sorry,no this task~");
                    _this.runTestPercentange = Math.ceil((response[0] / response[1])*100);
                    if (_this.runTestPercentange != 100) {
                        setTimeout(_this._onTestRunning, 500);
                    }
                });
            },
            getTestResult(){
                let _this = this;
                getInvokingResults(_this.taskId).then(response => {  // 这里的 response 为测试用例的结果，一个 list
                    // 展示测试用例的结果
                    //_this.usecasenum = response.length;
                    console.log(response)
                    _this.showTestResult(this.uniq(response), _this.taskType)
                    _this.branchnum=_this.relation.links.length;
                    if(_this.selectTestForm.selectedTestClass == "allTestFiles")
                    {
                        //this.usecasenum=this.testCaseMap[this.selectedTestProject].length;
                         var sum=0;
                         console.log(_this.testCaseMap)
                         console.log(_this.selectTestForm.selectedTestProject.split('.')[0])
                         for(let index in _this.testCaseMap[_this.selectTestForm.selectedTestProject.split('.')[0]])
                         {
                                sum+=_this.testCaseMap[_this.selectTestForm.selectedTestProject.split('.')[0]][index].length;
                         }
                        console.log(_this.usecasenum)
                         _this.usecasenum  =sum;
                    }
                    else
                    {
                        if(_this.selectTestForm.selectedTestCase == "allMethods")
                        {
                             _this.usecasenum = _this.testCaseMap[_this.selectTestForm.selectedTestProject.split('.')[0]][_this.selectTestForm.selectedTestClass].length;
                        } else {
                            _this.usecasenum = 1;
                        }
                    }
                    _this.uncoverlength = _this.uncover.length;
                    _this.coverrate=((_this.branchnum-_this.uncoverlength)/_this.branchnum)*100;
                    console.log(_this.uncoverlength)
                    console.log(_this.coverrate)
                });
            },
            uniq(array){
                var temp = []; //一个新的临时数组
                for(var i = 0; i < array.length; i++){
                    if(temp.indexOf(array[i]) == -1){
                        temp.push(array[i]);
                    }
                }
                return temp;
            },            //显示用例测试结果
            showTestResult(TestResult,type){
                // 记录正在显示的测试结果，cancelshow 的时候根据这个来
                //var type="one";
                //var TestResult=["com.example.demo.controller.Test1:calculate call com.example.demo.controller.Test1:doublevalue"];
                // this.$nextTick(() => {
                //     this.usecasenum = TestResult.length;
                //     console.log(this.usecasenum);
                // })
                console.log("start show ")
                this.TestResult = TestResult;
                for(let index in this.relation.links)
                {
                    var callrelation=this.relation.links[index].source.name+" CALL "+this.relation.links[index].target.name;
                    if(TestResult.indexOf(callrelation)<0)
                        this.uncoverfullname.push(callrelation);
                }
                if(type==='one'){
                    console.log("start one ")
                    for (let index in TestResult)
                    {
                        var result=TestResult[index].split(" ");
                        if(index===0){
                            this.moveFirstnode(result[0]);
                        }
                        this.changeSingleLine(result[0],result[2]);
                    }
                }
                //如果多个结果
                else{
                    console.log("start mouti ")
                    for(let index in TestResult){
                        var result=TestResult[index].split(" ");
                        this.changeMultipleLine(result[0],result[2]);
                    }
                }
                this.setUncover();
            },
            //获取未覆盖信息
            setUncover(){
                console.log(this.uncoverfullname)
                this.uncover=[];
                for(let index in this.uncoverfullname)
                {
                    var temp=this.uncoverfullname[index].split(" ");
                    var A=temp[0].split(":")[1];
                    var B=temp[2].split(":")[1];
                    if(this.uncover.indexOf(A+" call "+B)<0)
                        this.uncover.push(A+" call "+B);
                }
            },
            //多个用例测试结果
            changeMultipleLine(SourceName,TargetName) {
                for(let index in this.relation.links) {
                    if(this.relation.links[index].source.name==SourceName && this.relation.links[index].target.name==TargetName) {
                        var line_id=this.relation.links[index].index
                        d3.select('#eachline' + line_id).classed('edgelabel',false)
                        d3.select('#eachline' + line_id).style('stroke-width',3.5).attr('stroke','#ff7438').attr('filter','url(#f2)')
                        this.changeNode(SourceName);
                        this.changeNode(TargetName);
                    }
                };
            },
            moveFirstnode(name){
                var node = this.findNodeByName(name)
                var trans = this.tempTrans
                trans.k = 1;
                this.g.attr('transform',trans);
                // 根据视野大小定位
                var width = document.getElementById('container').offsetWidth;
                var height = document.getElementById('container').offsetHeight;

                trans.x = (Math.round(width/2) - node.x) * trans.k
                trans.y = (Math.round(height/2) - node.y) * trans.k

                this.g.attr('transform', trans)

            },
            //改变用例测试经过的直线
            changeSingleLine(SourceName,TargetName){
                console.log("change one start ")
                for(let index in this.relation.links) {
                    if(this.relation.links[index].source.name==SourceName && this.relation.links[index].target.name==TargetName) {
                        var line_id=this.relation.links[index].index
                        d3.select('#eachline' + line_id).classed('edgelabel',false)
                        d3.select('#eachline' + line_id).style('stroke-width',3.5)
                        d3.select('#eachline' + line_id).classed('showsinglepath',true)
                        this.changeNode(SourceName);
                        this.changeNode(TargetName);
                    }
                    // var callrelation=this.relation.links[index].source.name+" CALL "+this.relation.links[index].target.name;
                    // if(this.testCaseMap.indexOf(callrelation)<0)
                    //     this.uncoverfullname.push(callrelation);
                };
                console.log("change one end ")
            },
            //取消结果显示
            cancelShow(TestResult){
                for(let index in this.TestResult){
                    var result=this.TestResult[index].split(" ");
                    this.cancelLine(result[0],result[2]);
                    this.cancelNode(result[0]);
                    this.cancelNode(result[2]);

                }
            },
            //给节点加上边界效果
            changeNode(Name) {
                for(let index in this.relation.nodes) {
                    if(this.relation.nodes[index].name==Name) {
                        var node_id=this.relation.nodes[index].index
                        d3.select('#node' + node_id).classed('bling',true)
                        d3.select('#node' + node_id).attr('stroke-width',3).attr('stroke','#FA8072').attr('filter','url(#f1)')
                    }
                };
            },
            //取消连线效果
            cancelLine(SourceName,TargetName){
                for(let index in this.relation.links) {
                    if(this.relation.links[index].source.name==SourceName && this.relation.links[index].target.name==TargetName) {
                       var line_id=this.relation.links[index].index
                    }
                };
                d3.select('#eachline' + line_id).classed('showsinglepath',false).attr('filter','')
                d3.select('#eachline' + line_id).style('stroke-width',2)
                d3.select('#eachline' + line_id).classed('edgelabel',true)
            },
            //取消节点边界显示
            cancelNode(Name){
                for(let index in this.relation.nodes) {
                    if(this.relation.nodes[index].name==Name) {
                        var node_id=this.relation.nodes[index].index
                    }
                };
                d3.select('#node' + node_id).attr('stroke-width',0).attr('stroke','').attr('filter','')
            },
// -- card 运行测试用例

// --- card 未覆盖测试用例
            //定位到未覆盖边
            gotoUncover(){
                var selectA=this.selectTestForm.selectUncoverTest.split(" ")[0];
                var selectB=this.selectTestForm.selectUncoverTest.split(" ")[2];
                for(let index in this.uncoverfullname)
                {
                    var temp=this.uncoverfullname[index].split(" ");
                    var A=temp[0].split(":")[1];
                    var B=temp[2].split(":")[1];
                    if(selectA==A&&selectB==B)
                    {
                        console.log(temp[0]);
                        var node = this.findNodeByName(temp[0]);
                        //console.log(node);
                        var trans = this.tempTrans
                        trans.k = 1;
                        this.g.attr('transform',trans);
                        // 根据视野大小定位
                        var width = document.getElementById('container').offsetWidth;
                        var height = document.getElementById('container').offsetHeight;

                        trans.x = (Math.round(width/2) - node.x) * trans.k
                        trans.y = (Math.round(height/2) - node.y) * trans.k

                        this.g.attr('transform', trans)
                    }
                }
            },
// -- card 未覆盖测试用例

// --- card 添加节点
            //创建新节点并把源节点移到中心
            createNewNode(){
                var node;
                for(let index in this.relation.nodes) {
                    if(this.relation.nodes[index].name==this.selectednode) {
                        node=this.relation.nodes[index];
                        //console.log(node);
                    }
                };
                //var addnode={name:"",type:0,index:7,vx:-0.0034354444444444444,vy:-0.0003758444444444444,x:171.4738754444444,y:45.5769444444444};
                var addnode={name:"",type:1,index:'',vx:'',vy:'',x:'',y:''};
                console.log(node.vx);
                console.log(node.vy);
                addnode.name=this.newnode;
                addnode.vx=node.vx-0.0034354444444444444;
                addnode.vy=node.vy-0.0003758444444444444;
                addnode.x=node.x-1.0000000000000000000;
                addnode.y=node.y-1.0000000000000000000;
                addnode.index=this.relation.nodes.length;
                addnode.type=this.selectednodetype;
                var addline={index:7,source:[],target:[]}
                addline.source=node;
                addline.target=addnode;
                this.relation.nodes.push(addnode);
                this.relation.links.push(addline);
                console.log(this.relation.links);
                this.showd3();
                var node = this.findNodeByName(this.selectednode)
                var trans = this.tempTrans
                trans.k = 1;
                this.g.attr('transform',trans);
                // 根据视野大小定位
                var width = document.getElementById('container').offsetWidth;
                var height = document.getElementById('container').offsetHeight;

                trans.x = (Math.round(width/2) - node.x) * trans.k
                trans.y = (Math.round(height/2) - node.y) * trans.k

                this.g.attr('transform', trans)
            },
            // 把 this.classMethodMap 解析成 cascader 格式的 ClassMethod 
            praseClassMethod(){
                if(!this.cascaderClassMethod.length)
                    Object.entries(this.classMethodMap).forEach(keyValue=>{
                        this.cascaderClassMethod.push({
                            value: keyValue[0],
                            label: keyValue[0],
                            children: keyValue[1].map((value)=>{return {
                                value,
                                label: value,
                            }})
                        })
                    })
            },
            selectNode(){
                this.selectednode = this.cascaderNode[0] + ":" + this.cascaderNode[1];
                console.log(this.selectednode)
            },
// -- card 添加节点

// --- card 回归测试
            // 选择要进行 回归测试 的项目
            getRegressionProj(prov) {
                var prjName = prov.split('.')[0];
                // 回归测试时获取旧版本的所有测试用例
                try {
                    this.regression.oldcases[prov] = Object.keys(this.testCaseMap[prjName]) 
                } catch (error) {
                    getTestCaseList().then(response=>{
                        this.testCaseMap = response.result;
                        this.regression.oldcases[prov] = Object.keys(this.testCaseMap[prjName])
                    })
                }
            },
            fileListChange(file, fileList) {
                if(fileList.length == 1){
                    this.regression.info.newJarName = fileList[0].name;
                    this.regression.status = '已选择';
                    this.regression.disable = true;
                }
                else{
                    this.regression.info.newJarName = '';
                    this.regression.status = '选择新版本Jar包';
                    this.regression.disable = false;
                }
            },
            uploadSucc(resp,file,filelist){
                this.showMsg("上传'" + file.name + "'成功");
                // 保存历史记录
                let option = {
                    oldJarName: this.regression.info.oldJarName,
                    newJarName: this.regression.info.newJarName,
                    packageName:this.regression.info.packageName,
                    name:this.regression.info.oldJarName + " --> " + file.name
                }
                this.$nextTick(()=>{
                    if(this.history.regressionInfos)
                        this.history.regressionInfos.push(option)
                    else
                        this.history.regressionInfos = [option];
                    localStorage.setItem('history',JSON.stringify(this.history));
                })
                // 自动获取分析结果
                this.analyseJars(this.regression.info);
            },
            UploadJars(){
                this.$refs.uploadjar.submit();
            },
            analyseHistory(){
                // 获取已上传的包的分析结果
                let chosedInfo = this.history.regressionInfos.filter(info=>{return info.name === this.regression.chosedInfo})[0]
                this.getRegressionProj(chosedInfo.oldJarName)
                this.analyseJars(chosedInfo);
            },
            // 筛选展示的结果
            filterChange(filters){
                let filterMap = {
                    "remain": 0,
                    "affected": 1
                }
                this.showoldvsnew = [] // refresh list to show
                if(filters.length === 2)
                    this.showoldvsnew = this.oldvsnew;
                else
                    filters.forEach(fil =>{
                        this.showoldvsnew = this.showoldvsnew.concat(this.oldvsnew.filter(testcase => testcase.state === filterMap[fil]));
                    })
            },
            analyseJars(para,time = 0){
                // 检查 oldJarName 这个项目的所有用例是否获取
                if (this.regression.oldcases[para.oldJarName]) {
                    postRegression(para).then(response=> {
                        let newcases = response;                // "oldcases" is all testcases
                        this.filterList = ["remain", "affected"]         // set filter to all
                        this.oldvsnew = [];                              // refresh result

                        this.regression.oldcases[para.oldJarName].forEach(testcase => {

                            if(newcases.includes(testcase)){
                                // 有影响的
                                this.oldvsnew.push({
                                    state:1,
                                    casename:testcase
                                })
                            }
                            else if(testcase != "allTestFiles"){
                                this.oldvsnew.push({
                                    state:0,
                                    casename:testcase
                                })
                            }
                        })
                        this.showoldvsnew = this.oldvsnew;
                    })
                }
                else{
                    if (time < 3) {
                        // 重试三次，确保 testcasemap 一样
                        let _this = this;
                        setTimeout(()=>{
                            _this.analyseJars(para,time + 1);
                        },500)
                    }
                }
            },
// -- card 回归测试

// --- card 脚本录制
            watchReplay(){
                this.showReplay = true;
                this.$refs.replayer.startPlay();
            },
            toggleReplay(){
                this.$refs.replayer.togglePlay(this.isplay);
                if (this.isplay)
                    this.playandpause = '播放';
                else
                    this.playandpause = '暂停';
                this.isplay = !this.isplay;
            },
// -- card 脚本录制

// --- 整体
            showd3 () {
                //获取body高度和宽度
                let height = document.body.clientHeight
                let width = document.body.clientWidth

                //移动端设备横竖屏重新加载页面
                // function change () {
                    // window.location.reload()
                // }

                // window.addEventListener('onorientationchange' in window ? 'orientationchange' : 'resize', change, false)
                //节点大小（圆圈大小）
                const nodeSize = 35
                //初始化时连接线的距离长度
                const linkDistance = 130
                //赋值数据集
                var nodes = this.relation.nodes
                var links = this.relation.links
                
                //  设置画布
                var svg = d3.select('#svgCanvas')
                    .attr('xmlns', 'http://www.w3.org/2000/svg')
                    .attr('version', '2.0')
                    .attr('class', 'svg')//给svg设置了一个class样式，主要作用是长宽设置为100%

                //设置力布局，使用d3 v4版本的力导向布局
                var force = d3.forceSimulation()
                    .force('center', d3.forceCenter(width / 2 - 200, height / 2 -100))//设置力导向布局的中心点，创建一个力中心，设置为画布长宽的一半，所以拓扑图会在画布的中心点
                    .force('charce', d3.forceManyBody().strength(-300))//节点间的作用力
                    .force('collide', d3.forceCollide().radius(() => 60))//使用30的半径创建一个碰撞作用力

                
                // 移除上一个图（如果有的话）
                if(svg.selectAll("g").size() > 0){
                    svg.selectAll("g").remove();
                }

                //svg下嵌套g标签，缩放都在g标签上进行
                var g = svg.append('g')

                this.g = g
                var _this = this
                //d3.zoom是设置缩放，pc端是滚轮进行缩放，在移动端可以通过两指进行缩放
                var zoomObj = d3.zoom()
                    .scaleExtent([0.5, 1.2]) // 设置缩放范围
                    .on('zoom', () => {
                        //监听zoom事件，zoom发生时，调用该方法
                        const transform = d3.event.transform 
                        _this.tempTrans = d3.event.transform
                        g.attr('transform', transform)   // 设置缩放和偏移量 transform对象自带toString()方法
                    })
                    .on('end', () => {
                        //该方法在缩放时间结束后回调
                        // code
                    })
                svg.call(zoomObj).on("dblclick.zoom", null)
                //箭头
                // eslint-disable-next-line no-unused-vars
                var marker =
                    g.append('marker')
                        .attr('id', 'resolved')
                        //.attr("markerUnits","strokeWidth")  //设置为strokeWidth箭头会随着线的粗细发生变化
                        .attr('markerUnits', 'userSpaceOnUse')//用于确定marker是否进行缩放。取值strokeWidth和userSpaceOnUse，
                        .attr('viewBox', '0 -5 10 10')//坐标系的区域
                        .attr('refX', 39)//箭头坐标
                        .attr('refY', 0)
                        .attr('markerWidth', 12)//标识的大小
                        .attr('markerHeight', 12)
                        .attr('orient', 'auto')//绘制方向，可设定为：auto（自动确认方向）和 角度值
                        .attr('stroke-width', 2)//箭头宽度
                        .append('path')
                        .attr('d', 'M0,-5L10,0L0,5')//箭头的路径
                        //.attr('fill', '#ff7438')//箭头颜色


                //设置连线
                var edgesLine = g.selectAll('line')
                    .data(links)
                    .enter()
                    .append('path')
                    .attr('class', 'edgelabel')//添加class样式
                    //.style('stroke', '#ff7438')//添加颜色
                    .attr('id', (d, i) => {
                        //为每条直线设置不同的id
                        return 'eachline' + i
                    })
                    .style('stroke-width', 2)//连接线粗细度
                    .attr('marker-end', 'url(#resolved)')//设置线的末尾为刚刚的箭头

                //设置连接线中间关系文本
                var edgesText = g.selectAll('.linetext')
                    .data(links)
                    .enter()
                    .append('text')
                    .attr('class', 'linetext')
                    .text((d) => {
                        //设置关系文本
                        return d.relation
                    })
                //设置拖拽
                var drag = d3.drag()
                    .on('start', (d, i) => {
                        if (!d3.event.active) {
                            //拖拽开始回调
                            force.alphaTarget(0.1).restart() // 这个方法可以用在在交互时重新启动仿真，比如拖拽了某个节点，重新进行布局。这个必须要进行设置不然会拖动不了。
                        }
                        //d.fixed = true //偏移后固定不动
                        if(!d.fixed)
                        {
                            d.fx = null//记录当前默认位置（x - 节点当前的 x-位置，如果要为某个节点设置默认的位置，则需要为该节点设置如下两个属性:fx =x位置）
                            d.fy = null
                        }
                    })
                    .on('drag', (d, i) => {
                        //拖动时，设置拖动后默认位置的x，y
                        d.fx = d3.event.x
                        d.fy = d3.event.y
                    })
                    .on('end', (d, i) => {
                        //拖动结束后
                        if (!d3.event.active) {
                            force.alphaTarget(0)
                        }
                        d.fixed = true;
                    })

                //设置节点
                var node = g.selectAll('circle')
                    .data(nodes)
                    .enter()
                    .append('circle')
                    .attr('r', nodeSize)
                    .attr('class','node')
                    .attr('fill',(d,i) => {
                        var color = d3.rgb(stringToColour(d.name.split(":")[0]));
                        return color.brighter(0.7)
                    })
                    .attr('stroke',(d,i) => {
                        return stringToColour(d.name.split(":")[0])
                    })
                    .attr('stroke-width',4)
                    .attr('id', (d, i) => {
                        //为每个节点设置不同的id
                        return 'node' + i
                    })
                    .on('touchmove', (d, i) => {
                        //设置鼠标监听时间，当移动端手指移动时,设置关系文本透明度
                        edgesText.style('fill-opacity', function (edge) {
                            if (edge.source === d || edge.target === d) {
                                return 1.0
                            } else {
                                return 0
                            }
                        })
                    })
                    .on('touchend', (d, i) => {
                    //手指移开后，所有关系文本设置透明度为1
                        edgesText.style('fill-opacity', 1.0);
                    })
                    .on('click', (d, i) => {
                        edgesText.style('fill-opacity', function (edge) {
                            if (edge.source === d || edge.target === d) {
                                return 1.0
                            } else {
                                return 0
                            }
                        })
                        d3.select('#node' + i).raise()
                        d3.select('#nodetext' + i).raise()
                    })
                    .on('mouseout', (d, i) => {
                        edgesText.style('fill-opacity',1.0);
                        d3.select('#nodetext' + i)
                        .classed('highlighted',false)
                        .text(function () {
                            var subs = d.name.split(":");
                            return subs[subs.length - 1];
                        });
                    })
                    .on('dblclick', (d, i) => {
                        d.fx = null
                        d.fy = null
                    })
                    .on('mouseover',(d,i) => {
                        d3.select('#nodetext' + i)
                        .classed('highlighted',true)
                        .text(function () {
                            return d.name;
                        });
                    })
                    .call(drag)//监听拖动事件
                console.log(nodes);
                // 节点文字
                var nodeText = g.selectAll('.nodetext')
                    .data(nodes)
                    .enter()
                    .append('text')
                    .attr('text-anchor', 'middle')
                    .attr('class', 'nodetext')
                    .attr('id', (d, i) => {
                        return 'nodetext' + i
                    })
                    .attr('x', function (d, i) {
                        d3.select(this).append('tspan')
                        .attr('class', 'nodetext')
                        .attr('fill', '#ff7438')
                        .text(function () {
                            var subs = d.name.split(":");
                            return subs[subs.length - 1];
                        })
                    });

                //设置node和edge
                force.nodes(nodes)
                    .force('link', d3.forceLink(links).distance(linkDistance).strength(0.1))
                    .restart()

                //tick 表示当运动进行中每更新一帧时
                force.on('tick', function () {
                    //更新连接线的位置
                    edgesLine.attr('d', function (d) {
                        var path = 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y
                        return path
                    })
                    //更新连接线上文字的位置
                    edgesText.attr('x', function (d) {
                        return (d.source.x + d.target.x) / 2
                    })
                    edgesText.attr('y', function (d) { return (d.source.y + d.target.y) / 2 })

                    //更新结点图片和文字
                    node.attr('cx', function (d) {
                        return d.x
                    })
                    node.attr('cy', function (d) { return d.y })

                    nodeText.attr('x', function (d) { return d.x })
                    nodeText.attr('y', function (d) { return d.y })
                    //动态更新sptan 的x的坐标
                    nodeText.selectAll('tspan')
                        .attr('x', function (d) {
                            return d.x
                        })
                })
            },
            findNodeByName(name) {
                for(let index in this.relation.nodes) {
                    if(this.relation.nodes[index].name == name) {
                        return this.relation.nodes[index]
                    }
                }
            },
            // 显示消息
            showMsg(content){
                this.$message({
                    showClose: true,
                    message: content
                });
            },
            shrink_open(){
                if (this.toggle) {
                    this.actived = this.activeNames;
                    this.activeNames = []
                    setTimeout(() => {
                        document.getElementById("shrink-icon").classList.remove("el-icon-arrow-left");
                        document.getElementById("shrink-icon").classList.add("el-icon-arrow-right");
                        document.getElementById("leftSide").style.transform = "translate(-100%, 0)";
                        document.getElementById("leftSide").style.overflow = "visible";
                    }, 500);
                }
                else{
                    setTimeout(() => {
                        document.getElementById("leftSide").style.overflow = "";
                        this.activeNames = this.actived;
                        document.getElementById("shrink-icon").classList.remove("el-icon-arrow-right");
                        document.getElementById("shrink-icon").classList.add("el-icon-arrow-left");
                    }, 500);
                    document.getElementById("leftSide").style.transform = "";
                }
                this.toggle = !this.toggle;
            },
// -- 整体
        },
        created () {
            this.$nextTick(() => {
                var historyForm = JSON.parse(localStorage.getItem('history'))
                if(historyForm){
                    this.history = historyForm
                }
            })
        }
    }

    function stringToColour(str) {
        var hash = 0;
        for (var i = 0; i < str.length; i++) {
            hash = str.charCodeAt(i) + ((hash << 5) - hash);
        }
        var colour = '#';
        for (var i = 0; i < 3; i++) {
            var value = (hash >> (i * 8)) & 0xFF;
            colour += ('00' + value.toString(16)).substr(-2);
        }
        return colour;
    }
</script>

<style lang="less">
    .el-cascader .el-input {
        width: 240px;
    }

    .el-select .el-input {
        width: 240px;
    }

    .el-autocomplete .el-input {
        width: 240px;
    }

    header{
        font-family: "Arial Black";
        font-size: 25px;
    }
    .funny {
        position: fixed;
        margin-left: 33px;
        background-color: white;
        border-radius: 0 5px 5px 0;
        box-shadow: lightgrey 5px 0px 5px 2px;
    }

    .funny:hover{
        cursor: pointer;
        color: #0583f2
    }

    .list-header{
        border-bottom: 1px solid gray;
        margin: 10px 0 6px;
    }

    .list-container{
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

    .left-side{
        position: absolute;
        width: 350px;
        transition: .5s ease;
        background-color: white;
        box-shadow: lightgrey 0px 0px 5px 5px;//边框内阴影
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

    #thumb {
        width: 160px;
        height: 160px;
        pointer-events: none;
    }

    #thumb-container {
        position: absolute;
        right: 0;
        bottom: 0;
        margin: 20px;
        border: 1px solid silver;
        background-color: white;
        box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
    }
    .titlestyle{
        font-family: "Microsoft YaHei";
        font-weight:bold;
    }
    .formbody {
        margin:20px 0px 0px 0px;
    }
    .card {
        margin:5px 0px 5px 0px;
    }
    .upload {
        margin:25px 0px 25px 0px;
    }
    .container {
        height: 600px;
        .exit {
            position: absolute;
            top: 20px;
            left: 20px;
            width: 60px;
            height: 25px;
            border: none;
            border-radius: 2px;
            color: #000;
            z-index: 200;
        }
        overflow-y: scroll;
    }
    .formlabelfont {
        font-size: 12px;
    }

    .container::-webkit-scrollbar { 
        display: none;
    }

    .labeltext {
        font-size: 16px;
        font-family: SimSun;
        fill: #ff7438;
    }

    .nodetext {
        font-size: 12px;
        font-family: SimSun;
        fill: #000;
        position: relative;
        pointer-events: none;
    }

    //箭头颜色
    #resolved{
        fill:#FFD700;
    }

    //滑动鼠标显示连线效果，移到网页外连线消失
    /*.edgelabel{
        stroke-width: 6px;
        fill: transparent;
        stroke:#DC143C;
        stroke-dasharray: 85 400;
        stroke-dashoffset: -220;
        transition: 1s all ease
    }

    svg:hover .edgelabel {
        stroke-dasharray: 70 0;
        stroke-width: 3px;
        stroke-dashoffset: 0;
        stroke:#FFD700;
    }*/
    .edgelabel{
    stroke:#FFD700;
    }

    //    字体火焰效果
    .highlighted {
        font-style:italic;
        font-weight:bold;
        font-size: 18px;
        font-family:sans-serif;
        fill:#483D8B;
        text-shadow: 0 -5px 4px #FFFF00,2px -10px 6px #FFA500,-2px -15px 11px #FF6347,2px -25px 18px #FF0000;
        transition: 1s;
    }
    .showsinglepath{
         stroke:#FA8072;
         stroke-dasharray: 1000;
         stroke-dashoffset: 1000;
         -webkit-animation: draw 3s infinite ease-in-out;
    }
   @keyframes draw{
        0%{
            stroke-dashoffset: 1000;
        }
        100%{
            stroke-dashoffset: 0;
        }
    }

    .linetext {
        font-size: 12px;
        font-weight: bold;
        font-family: SimSun;
        fill: #000 !important;
        color: #000;
        fill-opacity: 1;
    }

    /*.bling{ animation: alarm 0.4s  ease-in  infinite ; fill:yellow; font-weight: bold;}
    @keyframes alarm {
        0%{ fill:#FF9966;}
        50%{ fill:#FF3333;}
        100%{ fill:#FF9966;}
    }*/

    .svg {
        position: relative;
        width: 100%;
        height: 100%;
    }

    .node{
        position: relative;
    }

    .node:hover{
        cursor: pointer;
    }

    .disabled{
        pointer-events: none;
    }

    .disabled .itemname{
        color: gray;
    }

    .disabled .require-info{
        color: #FF0000;
        opacity: 1;
    }

    .require-info{
        opacity: 0;
    }
    #branches,#usecase,#uncoverbranch,#coverrate{
        text-align:center;
    }

    .hjr-list-item{
        text-align: left;
        padding: 8px;
        cursor: pointer;
        border-width: 0 0 0 4px;
        border-style: solid;
        margin: 2px 0;
    }

    .hjr-list-item:hover{
        background-color: lightgray;
    }
</style>