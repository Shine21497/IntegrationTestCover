<template>
    <el-container>
        <el-header>Method Call Relation Graph</el-header>
        <el-container>
            <el-aside width="350px">
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
                        <el-card :body-style="{ padding: '0px' }" class="card">
                            <el-container class="formbody">
                                <el-form ref="form" :model="form" label-width="80px">
                                    <el-form-item label="Jar包选择">
                                        <el-select v-model="form.selectedjar" placeholder="请选择jar包" @visible-change="showfilelist">
                                            <el-option
                                                    v-for="item in uploadedFiles"
                                                    :key="item"
                                                    :label="item"
                                                    :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="包范围">
                                        <el-input type="textarea" v-model="form.packages" placeholder="请输入包范围"></el-input>
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button type="primary" :disabled="form.selectedjar.length == 0" size="small" @click="generateGraph()">立即创建</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-container>
                        </el-card>
                    </el-collapse-item>
                    <el-collapse-item class="titlestyle" name="3" :class="JSON.stringify(relation)=='{}'?'disabled': ''">
                        <template slot="title">
                            <p class="itemname">辅助定位</P>
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
                                    <el-form-item label="Test所属项目选择">
                                        <el-select v-model="uploadTestData.selectedProject" placeholder="请选择项目">
                                            <el-option
                                                    v-for="item in uploadedFiles"
                                                    :key="item"
                                                    :label="item"
                                                    :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-upload class="upload" action="/apiurl/uploadTestCase" accept="application/jar" :before-upload="onBeforeUpload" ref="uploadTest" :file-list="fileList" :auto-upload="false" :data="uploadTestData">
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
                                        <el-select filterable  v-model="selectTestForm.selectedTestProject" placeholder="请选择项目" @change="getTestProject($event)" @visible-change="showTestProjectList">
                                            <el-option
                                                    v-for="item in uploadedFiles"
                                                    :key="item"
                                                    :label="item"
                                                    :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="类选择">
                                        <el-select filterable  v-model="selectTestForm.selectedTestClass" placeholder="请选择测试类" @change="getTestClass($event)">
                                            <el-option
                                                    v-for="item in selectTestForm.allTestClasses"
                                                    :key="item"
                                                    :label="item"
                                                    :value="item">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="方法选择">
                                        <el-select filterable  v-model="selectTestForm.selectedTestCase" placeholder="请选择测试方法">
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
                </el-collapse>
            </el-aside>
            <el-main>
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
            </el-main>
        </el-container>
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
    } from '@/api/methodcallrelationgraph.js'
    import * as d3 from 'd3'
import { setInterval } from 'timers';
    export default {
        components: {
            ElButton,
            ElContainer
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
                    packages:''
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
                    allTestCases: []
                },
                uploadedFiles:[],
                classMethodMap:{"a": ["a","b"], "b": ["a","c"]},
                testCaseMap:{},
                g:{},
                tempTrans: d3.zoomIdentity.translate(0, 0).scale(1),
                activeNames: ['1'],  //加上这个不然控制台老报错
                runTestPercentange:0,
                taskId:'',
                taskType:''  // "many" 和 "one" 
            }
        },
        methods: {
            goToNode() {
                var node = this.findNodeByName(this.adjustForm.selectedClass + ":" + this.adjustForm.selectedMethod)
                var trans = this.tempTrans

                trans.k = 1;
                this.g.attr('transform',trans);
                
                trans.x = (510 - node.x) * trans.k
                trans.y = (300 - node.y) * trans.k
                
                this.g.attr('transform', trans)
            },
            //显示用例测试结果
            showTestResult(TestResult,type){
             //把第一个节点移到中心
                var node = this.findNodeByName(TestResult[0].split(" ")[0])
                                            var trans = this.tempTrans
                                            trans.k = 1;
                                            this.g.attr('transform',trans);
                                            trans.x = (510 - node.x) * trans.k
                                            trans.y = (300 - node.y) * trans.k
                                             this.g.attr('transform', trans)
                for(let index in TestResult){
                    var result=TestResult[index].split(" ");
                    //如果是单个结果
                    if(type === 'one'){
                       //逐个定位节点
                            var node = this.findNodeByName(result[2])
                            var trans = this.tempTrans
                            trans.k = 1;
                            this.g.attr('transform',trans);
                            trans.x = (510 - node.x) * trans.k
                            trans.y = (300 - node.y) * trans.k
                            var temp=this.g;
                            setTimeout(function timer(){
                            temp.attr('transform', trans);
                            }, (index+1)*2000);
                        //线的流动效果和节点效果
                        this.changeSingleLine(result[0],result[2]);
                    }
                    //如果多个结果
                    else{
                        this.changeMultipleLine(result[0],result[2]);
                    }
                    //更改结点样式
                    this.changeNode(result[0]);
                    this.changeNode(result[2]);
                }
            },

           //改变用例测试经过的直线
            changeSingleLine(SourceName,TargetName){
                for(let index in this.relation.links) {
                    if(this.relation.links[index].source.name==SourceName && this.relation.links[index].target.name==TargetName) {
                        var line_id=this.relation.links[index].index
                        d3.select('#eachline' + line_id).classed('edgelabel',false)
                        d3.select('#eachline' + line_id).style('stroke-width',3.5)
                        d3.select('#eachline' + line_id).classed('showsinglepath',true)
                    }
                };
            },

            //多个用例测试结果
            changeMultipleLine(SourceName,TargetName) {
                for(let index in this.relation.links) {
                    if(this.relation.links[index].source.name==SourceName && this.relation.links[index].target.name==TargetName) {
                        var line_id=this.relation.links[index].index
                        d3.select('#eachline' + line_id).classed('edgelabel',false)
                        d3.select('#eachline' + line_id).style('stroke-width',3.5).attr('stroke','#ff7438').attr('filter','url(#f2)')
                    }
               };
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
            //取消结果显示
            cancelShow(TestResult){
                for(let index in TestResult){
                    var result=TestResult[index].split(" ");
                    this.cancelLine(result[0],result[2]);
                    this.cancelNode(result[0]);
                    this.cancelNode(result[2]);
                }
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

            findNodeByName(name) {
                for(let index in this.relation.nodes) {
                    if(this.relation.nodes[index].name == name) {
                        return this.relation.nodes[index]
                    }
                }
            },

            getTestClass(prov) {
                console.log(this.selectTestForm.selectedTestProject)
                console.log(prov)
                this.selectTestForm.allTestCases =  this.testCaseMap[this.selectTestForm.selectedTestProject.split('.')[0]][prov]
                // this.methods =  this.testCaseMap[this.selectTestForm.selectedTestProject][prov]
            },
            getTestProject(prov) {
                // console.log(this.testCaseMap)
                // console.log(prov.split('.'))
                // prov is "demo.jar" but testCaseMap is {"demo":{...}}
                this.selectTestForm.allTestClasses = Object.keys(this.testCaseMap[prov.split('.')[0]])
                // this.selectTestForm.allTestClasses = Object.keys(this.testCaseMap[prov])
            },
            getClass(prov) {
                this.adjustForm.allMethods = this.classMethodMap[prov]
                this.adjustForm.selectedMethod = '';
            },
            showfilelist(open){
                if(open) {
                    let _this = this
                    getUploadedFileList().then(response => {
                        _this.uploadedFiles = response.result
                    })
                }
            },
            generateGraph(){
                let _this = this
                this.$nextTick(() => {
                    getRelationByFileName(this.form.selectedjar, this.form.packages).then(response => {
                        _this.relation.nodes = response.nodes
                        _this.relation.links = response.links
                        _this.adjustForm.allClasses = response.classes
                        _this.classMethodMap = response.classMethodMap
                        _this.showd3()
                    })
                })
            },
            showTestProjectList(open) {
                if(open) {
                    let _this = this
                    getUploadedFileList().then(response => {
                        _this.uploadedFiles = response.result
                    })
                    getTestCaseList().then(response => {
                        //response is {"result":{"demo":{"TestMethod.java":["allMehtods"],"allTestFiles":[],"Test2.java":["allMehtods"]}}}
                        _this.testCaseMap = response.result
                    })
                }

            },
            submitUpload() {
                this.$refs.upload.submit();
            },
            submitTestUpload() {
                this.$refs.uploadTest.submit();
            },
            onBeforeUpload(file) {

            },
            startRunTestCase(file) {
                var projectname  = this.selectTestForm.selectedTestProject;
                var testcasename = this.selectTestForm.selectedTestClass;
                var method       = this.selectTestForm.selectedTestCase;
                if(!projectname || !testcasename || !method){
                    this.showMsg('请选择完整的项目，测试类以及测试方法')
                    return
                }
                let _this = this;
                // 传参数给后端跑测试用例
                runTestCase(projectname, testcasename, method).then(response => {// response 为 ["12123123","many"]
                    _this.taskId = response[0];
                    _this.taskType = response[1];
                    // 开始监听运行进度
                    _this._onTestRunning();
                })
            },
            // 获取测试进度的时候要调用的
            _onTestRunning(){
                let _this = this;
                getTestRunningStatus(_this.taskid).then(response => {  // 这里的response[0] 和 [1]可能要改，看后端数据结构
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
                getInvokingResults(_this.taskid).then(response => {  // 这里的 response 为测试用例的结果，一个 list
                    // 展示测试用例的结果
                    _this.showTestResult(response,_this.taskType)
                });
            },
            // 显示消息
            showMsg(content){
                this.$message({
                    showClose: true,
                    message: content
                });
            },
            showd3 () {
                //获取body高度和宽度
                let height = document.body.clientHeight
                let width = document.body.clientWidth

                //移动端设备横竖屏重新加载页面
                function change () {
                    // window.location.reload()
                }

                window.addEventListener('onorientationchange' in window ? 'orientationchange' : 'resize', change, false)
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
                        return stringToColour(d.name.split(":")[0]);
                    })
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
            }
        },
        created () {
            this.$nextTick(() => {

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
    header{
        font-family: "Arial Black";
        font-size: 25px;
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
            background: #0583f2;
            border: none;
            border-radius: 2px;
            color: #000;
            z-index: 200;
            &:hover {
                 background: #1e82d9;
             }
        }
    }
    .formlabelfont {
        font-size: 12px;
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
            animation: draw 10s;
            animation-fill-mode:forwards;
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
</style>