<template>
  <div class="background-graph">
    <div id="svg-container" class="svg-container">
      <svg id="svgCanvas">
        <defs>
          <!--节点滤波器-->
          <filter id="f1">
            <feGaussianBlur in="SourceGraphic" stdDeviation="5" />
          </filter>
        </defs>
        <defs>
          <!--直线滤波器-->
          <filter id="f2">
            <feGaussianBlur in="SourceGraphic" stdDeviation="2" />
          </filter>
        </defs>
        <defs>
          <pattern id="smallGrid" width="8" height="8" patternUnits="userSpaceOnUse">
            <path d="M 8 0 L 0 0 0 8" fill="none" stroke="gray" stroke-width="0.5" />
          </pattern>
          <pattern id="grid" width="80" height="80" patternUnits="userSpaceOnUse">
            <rect width="80" height="80" fill="url(#smallGrid)" />
            <path d="M 80 0 L 0 0 0 80" fill="none" stroke="gray" stroke-width="1" />
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#grid)" />
      </svg>
    </div>
    <div
      class="tipsArea"
      v-if="hoveredNode.className.length"
      :style="'border-left-color:' + hoveredNode.color"
    >
      <span style="font-weight:bold; padding-right:3px">{{hoveredNode.className}}</span>
      <span>{{hoveredNode.methodName}}</span>
    </div>
    <!--<div id="thumb-svg-container">
      <svg id="thumb" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 1000 1000">
        <use xlink:href="#svgCanvas" />
      </svg>
    </div>-->
  </div>
</template>

<script>
import * as d3 from "d3";

export default {
  name: "Graph",
  props: {},
  data() {
    return {
      relation: {},
      isok: false,
      tempTrans: d3.zoomIdentity.translate(0, 0).scale(1),
      g: {},
      TestResult: [],
      hoveredNode: {
        className: "",
        methodName: "",
        color: ""
      },
      whq: false,
    };
  },
  methods: {
    //创建新节点并把源节点移到中心
    createNewNode(params) {
      const { selectednode, newnode } = params;
      var node;
      for (let index in this.relation.nodes) {
        if (this.relation.nodes[index].name == selectednode) {
          node = this.relation.nodes[index];
          //console.log(node);
        }
      }
      //var addnode={name:"",type:0,index:7,vx:-0.0034354444444444444,vy:-0.0003758444444444444,x:171.4738754444444,y:45.5769444444444};
      var addnode = {
        name: "",
        type: 1,
        index: "",
        vx: "",
        vy: "",
        x: "",
        y: ""
      };
      addnode.name = newnode;
      addnode.vx = node.vx - 0.0034354444444444444;
      addnode.vy = node.vy - 0.0003758444444444444;
      addnode.x = node.x - 1.0;
      addnode.y = node.y - 1.0;
      addnode.index = this.relation.nodes.length;
      addnode.type = this.selectednodetype;
      var addline = { index: 7, source: [], target: [] };
      addline.source = node;
      addline.target = addnode;
      this.relation.nodes.push(addnode);
      this.relation.links.push(addline);
      this.show(this.relation);
      var node = this.findNodeByName(selectednode);
      var trans = this.tempTrans;
      trans.k = 1;
      this.g.attr("transform", trans);
      // 根据视野大小定位
      var width = document.getElementById("svg-container").offsetWidth;
      var height = document.getElementById("svg-container").offsetHeight;

      trans.x = (Math.round(width / 2) - node.x) * trans.k;
      trans.y = (Math.round(height / 2) - node.y) * trans.k;

      this.g.attr("transform", trans);
    },
    showEffect(result) {
      if (!this.isok) {
        this.$message({
          showClose: true,
          message: "请先生成调用关系图"
        });
        return;
      }
      const { TestResult, taskType } = result;
      console.log("start show ");
      this.TestResult = TestResult;

      for (let index in TestResult) {
        var result = TestResult[index].split(" ");
        if (index === 0) {
          this.moveFirstnode(result[0]);
        }
        this.changeSingleLine(result[0], result[2]);
      }

      /*if (taskType === "one") {
        console.log("start one ");
        for (let index in TestResult) {
          var result = TestResult[index].split(" ");
          if (index === 0) {
            this.moveFirstnode(result[0]);
          }
          this.changeSingleLine(result[0], result[2]);
        }
      }*/
      //如果多个结果
      /*else {
        console.log("start mouti ");
        for (let index in TestResult) {
          var result = TestResult[index].split(" ");
          this.changeMultipleLine(result[0], result[2]);
        }
      }*/
    },
    moveFirstnode(name) {
      var node = this.findNodeByName(name);
      var trans = this.tempTrans;
      trans.k = 1;
      this.g.attr("transform", trans);
      // 根据视野大小定位
      var width = document.getElementById("svg-container").offsetWidth;
      var height = document.getElementById("svg-container").offsetHeight;

      trans.x = (Math.round(width / 2) - node.x) * trans.k;
      trans.y = (Math.round(height / 2) - node.y) * trans.k;

      this.g.attr("transform", trans);
    },
    //多个用例测试结果
    changeMultipleLine(SourceName, TargetName) {
      for (let index in this.relation.links) {
        if (
          this.relation.links[index].source.name == SourceName &&
          this.relation.links[index].target.name == TargetName
        ) {
          var line_id = this.relation.links[index].index;
          d3.select("#eachline" + line_id).classed("edgelabel", false);
          d3.select("#eachline" + line_id)
            .style("stroke-width", 3.5)
            .attr("stroke", "#ff7438")
            .attr("filter", "url(#f2)");
          this.changeNode(SourceName);
          this.changeNode(TargetName);
        }
      }
    },
    //改变用例测试经过的直线
    changeSingleLine(SourceName, TargetName) {
      console.log("change one start ");
      for (let index in this.relation.links) {
        if (
          this.relation.links[index].source.name == SourceName &&
          this.relation.links[index].target.name == TargetName
        ) {
          var line_id = this.relation.links[index].index;
          d3.select("#eachline" + line_id).classed("edgelabel", false);
          d3.select("#eachline" + line_id).style("stroke-width", 5);
          d3.select("#eachline" + line_id).classed("showsinglepath", true);
          d3.select("#eachline" + line_id).style("stroke","darkslateblue")
          this.changeNode(SourceName);
          this.changeNode(TargetName);
        }
      }
      console.log("change one end ");
    },
    //取消结果显示
    cancelShow() {
      for (let index in this.TestResult) {
        var result = this.TestResult[index].split(" ");
        this.cancelLine(result[0], result[2]);
        this.cancelNode(result[0]);
        this.cancelNode(result[2]);
      }
    },
    //给节点加上边界效果
    changeNode(Name) {
      for (let index in this.relation.nodes) {
        if (this.relation.nodes[index].name == Name) {
          var node_id = this.relation.nodes[index].index;
          // d3.select("#node" + node_id).classed("bling", true);
          d3.select("#node" + node_id)
            .attr("stroke-width", 3)
            .attr("stroke", "#FA8072")
            .attr("filter", "url(#f1)");
        }
      }
    },
    //取消连线效果
    cancelLine(SourceName, TargetName) {
      for (let index in this.relation.links) {
        if (
          this.relation.links[index].source.name == SourceName &&
          this.relation.links[index].target.name == TargetName
        ) {
          var line_id = this.relation.links[index].index;
        }
      }
      d3.select("#eachline" + line_id)
        .classed("showsinglepath", false)
        .attr("filter", "");

      d3.select("#eachline" + line_id).style("stroke","#ffd700")
      d3.select("#eachline" + line_id).style("stroke-width", 2);
      d3.select("#eachline" + line_id).classed("edgelabel", true);
    },
    //取消节点边界显示
    cancelNode(Name) {
      for (let index in this.relation.nodes) {
        if (this.relation.nodes[index].name == Name) {
          var node_id = this.relation.nodes[index].index;
        }
      }
      d3.select("#node" + node_id)
        .attr("stroke-width", 0)
        .attr("stroke", "")
        .attr("filter", "");
    },
    locatenode(node) {
      var trans = this.tempTrans;

      trans.k = 1;
      this.g.attr("transform", trans);
      // 根据视野大小定位
      var width = document.getElementById("svg-container").offsetWidth;
      var height = document.getElementById("svg-container").offsetHeight;

      trans.x = (Math.round(width / 2) - node.x) * trans.k;
      trans.y = (Math.round(height / 2) - node.y) * trans.k;

      this.g.attr("transform", trans);
    },
    createGraph(relation) {
      try {
        this.show(relation);
        this.relation = relation;
        this.isok = true;
      } catch (error) {
        if (!relation.nodes) {
          this.$message.error(
            "生成调用图失败，可能的原因是：遍历程序包方法时出错"
          );
        }
        if (!relation.links) {
          this.$message.error(
            "生成调用图失败，可能的原因是：解析调用关系时出错"
          );
        }
      }
    },
    show(relation) {
      //获取body高度和宽度
      let height = document.body.clientHeight;
      let width = document.body.clientWidth;

      //节点大小（圆圈大小）
      const nodeSize = 25;
      //节点边界的宽度
      const nodeStrokeWidth = 4;
      //初始化时连接线的距离长度
      const linkDistance = 130;
      //赋值数据集
      var nodes = relation.nodes;
      var links = relation.links;

      //  设置画布
      var svg = d3
        .select("#svgCanvas")
        .attr("xmlns", "http://www.w3.org/2000/svg")
        .attr("version", "2.0")
        .attr("class", "svg"); //给svg设置了一个class样式，主要作用是长宽设置为100%

      //设置力布局，使用d3 v4版本的力导向布局
      var force = d3
        .forceSimulation()
        .force("center", d3.forceCenter(width / 2 - 200, height / 2 - 100)) //设置力导向布局的中心点，创建一个力中心，设置为画布长宽的一半，所以拓扑图会在画布的中心点
        .force("charce", d3.forceManyBody().strength(-300)) //节点间的作用力
        .force("collide", d3.forceCollide().radius(() => 60)); //使用30的半径创建一个碰撞作用力

      // 移除上一个图（如果有的话）
      if (svg.selectAll("g").size() > 0) {
        svg.selectAll("g").remove();
      }

      //svg下嵌套g标签，缩放都在g标签上进行
      var g = svg.append("g");

      this.g = g;
      var _this = this;
      //d3.zoom是设置缩放，pc端是滚轮进行缩放，在移动端可以通过两指进行缩放
      var zoomObj = d3
        .zoom()
        .scaleExtent([0.2, 1.2]) // 设置缩放范围
        .on("zoom", () => {
          //监听zoom事件，zoom发生时，调用该方法
          const transform = d3.event.transform;
          _this.tempTrans = d3.event.transform;
          g.attr("transform", transform); // 设置缩放和偏移量 transform对象自带toString()方法
        })
        .on("end", () => {
          //该方法在缩放时间结束后回调
          // code
        });
      svg.call(zoomObj).on("dblclick.zoom", null);
      //箭头
      // eslint-disable-next-line no-unused-vars
      var marker = g
        .append("marker")
        .attr("id", "resolved")
        //.attr("markerUnits","strokeWidth")  //设置为strokeWidth箭头会随着线的粗细发生变化
        .attr("markerUnits", "userSpaceOnUse") //用于确定marker是否进行缩放。取值strokeWidth和userSpaceOnUse，
        .attr("viewBox", "0 -5 10 10") //坐标系的区域
        .attr("refX", nodeSize + nodeStrokeWidth + 2) //箭头坐标
        .attr("refY", 0)
        .attr("markerWidth", 12) //标识的大小
        .attr("markerHeight", 12)
        .attr("orient", "auto") //绘制方向，可设定为：auto（自动确认方向）和 角度值
        .attr("stroke-width", 2) //箭头宽度
        .append("path")
        .attr("d", "M0,-5L10,0L0,5") //箭头的路径
        .attr("fill", "#ff7438"); //箭头颜色

      //设置连线
      var edgesLine = g
        .selectAll("line")
        .data(links)
        .enter()
        .append("path")
        .attr("class", "edgelabel") //添加class样式
        //.style('stroke', '#ff7438')//添加颜色
        .attr("id", (d, i) => {
          //为每条直线设置不同的id
          return "eachline" + i;
        })
        .style("stroke-width", 2) //连接线粗细度
        .attr("marker-end", "url(#resolved)"); //设置线的末尾为刚刚的箭头

      //设置拖拽
      var drag = d3
        .drag()
        .on("start", (d, i) => {
          if (!d3.event.active) {
            //拖拽开始回调
            force.alphaTarget(0.1).restart(); // 这个方法可以用在在交互时重新启动仿真，比如拖拽了某个节点，重新进行布局。这个必须要进行设置不然会拖动不了。
          }
          //d.fixed = true //偏移后固定不动
          if (!d.fixed) {
            d.fx = null; //记录当前默认位置（x - 节点当前的 x-位置，如果要为某个节点设置默认的位置，则需要为该节点设置如下两个属性:fx =x位置）
            d.fy = null;
          }
        })
        .on("drag", (d, i) => {
          //拖动时，设置拖动后默认位置的x，y
          d.fx = d3.event.x;
          d.fy = d3.event.y;
        })
        .on("end", (d, i) => {
          //拖动结束后
          if (!d3.event.active) {
            force.alphaTarget(0);
          }
          d.fixed = true;
        });
      let vueCom = this;
      //设置节点
      var node = g
        .selectAll("circle")
        .data(nodes)
        .enter()
        .append("circle")
        .attr("r", nodeSize)
        .attr("class", "node")
        .attr("fill", (d, i) => {
          var color = d3.rgb(this.stringToColour(d.name.split(":")[0]));
          return color.brighter(0.7);
        })
        .attr("stroke", (d, i) => {
          return this.stringToColour(d.name.split(":")[0]);
        })
        .attr("stroke-width", nodeStrokeWidth)
        .attr("id", (d, i) => {
          //为每个节点设置不同的id
          return "node" + i;
        })
        .on("click", (d, i) => {
          vueCom.whq = !vueCom.whq;
            edgesLine.style("stroke", function (edge) {
              if (edge.source === d || edge.target === d) {
                if(vueCom.whq) {
                  d3.select(this).style("stroke-width", nodeStrokeWidth);
                  return "#ff7438";
                }
                else{
                  d3.select(this).style("stroke-width", 2);
                  return "#ffd700";
                }
              } else {
                d3.select(this).style("stroke-width", 2);
                return "#ffd700";
              }
            });
        })
        .on("mouseout", (d, i) => {
          //   edgesText.style("fill-opacity", 1.0);
          d3.select("#nodetext" + i)
            .classed("node-text-show", false)
            .text(function() {
              var subs = d.name.split(":");
              // 设置提示的内容
              (vueCom.hoveredNode.className = ""),
                (vueCom.hoveredNode.methodName = ""),
                (vueCom.hoveredNode.color = "");
              return subs[subs.length - 1];
            });
        })
        .on("dblclick", (d, i) => {
          d.fx = null;
          d.fy = null;
        })
        .on("mouseover", (d, i) => {
          d3.select("#nodetext" + i)
            .classed("node-text-show", true)
            .text(function() {
              var subs = d.name.split(":");
              (vueCom.hoveredNode.className = subs[0]),
                (vueCom.hoveredNode.methodName = subs[subs.length - 1]),
                (vueCom.hoveredNode.color = vueCom.stringToColour(subs[0]));
              return d.name;
            });
        })
        .call(drag); //监听拖动事件

      // 节点文字
      var nodeText = g
        .selectAll(".node-text")
        .data(nodes)
        .enter()
        .append("text")
        .attr("text-anchor", "middle")
        .attr("class", "node-text")
        .attr("id", (d, i) => {
          return "nodetext" + i;
        })
        .text(function(d) {
          var subs = d.name.split(":");
          return subs[subs.length - 1];
        });

      //设置node和edge
      force
        .nodes(nodes)
        .force(
          "link",
          d3
            .forceLink(links)
            .distance(linkDistance)
            .strength(0.8)
        )
        .restart();

      //tick 表示当运动进行中每更新一帧时
      force.on("tick", function() {
        //更新连接线的位置
        edgesLine.attr("d", function(d) {
          var path =
            "M " +
            d.source.x +
            " " +
            d.source.y +
            " L " +
            d.target.x +
            " " +
            d.target.y;
          return path;
        });

        //更新结点图片和文字
        node.attr("cx", function(d) {
          return d.x;
        });
        node.attr("cy", function(d) {
          return d.y;
        });

        nodeText.attr("x", function(d) {
          return d.x;
        });
        nodeText.attr("y", function(d) {
          return d.y + nodeSize + 16;
        });

        //动态更新sptan 的x的坐标
        nodeText.selectAll("tspan").attr("x", function(d) {
          return d.x;
        });
      });
    },
    stringToColour(str) {
      var hash = 0;
      for (var i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
      }
      var colour = "#";
      for (var i = 0; i < 3; i++) {
        var value = (hash >> (i * 8)) & 0xff;
        colour += ("00" + value.toString(16)).substr(-2);
      }
      return colour;
    },
    findNodeByName(name) {
      for (let index in this.relation.nodes) {
        if (this.relation.nodes[index].name == name) {
          return this.relation.nodes[index];
        }
      }
    }
  },
  mounted() {},
  created() {}
};
</script>

<style lang="less">
// 全局样式
.node-text {
  fill: #000;
  position: relative;
  pointer-events: none;
}

// 字体高亮
.node-text-show {
  font-weight: bold;
  background-color: whitesmoke;
  border-radius: 2px;
}

.edgelabel {
  stroke: #ffd700;
}

.node {
  position: relative;
}

.node:hover {
  cursor: pointer;
}

.showsinglepath {
  stroke: darkslateblue;
  stroke-dasharray: 1000;
  stroke-dashoffset: 1000;
  transition: draw 3s infinite ease-in-out;
  /* -webkit-animation: draw 3s infinite ease-in-out; */
}

// 字体火焰效果
.highlighted {
  font-style: italic;
  font-weight: bold;
  font-size: 18px;
  font-family: sans-serif;
  fill: #483d8b;
  text-shadow: 0 -5px 4px #ffff00, 2px -10px 6px #ffa500,
    -2px -15px 11px #ff6347, 2px -25px 18px #ff0000;
  transition: 1s;
}

@keyframes draw {
  0% {
    stroke-dashoffset: 1000;
  }
  100% {
    stroke-dashoffset: 0;
  }
}
</style>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped  lang="less">
.tipsArea {
  top: 6px;
  right: 0;
  position: fixed;
  background-color: white;
  padding: 2px 3px;
  border-left-style: solid;
  border-left-width: 5px;
}

.background-graph {
  position: absolute;
  border-width: 2px;
  border-style: dashed;
  position: absolute;
  border-width: 2px;
  border-style: solid;
  border-color: whitesmoke;
  top: 3px;
  left: 3px;
  right: 3px;
  bottom: 3px;
}

[v-cloak] {
  display: none;
}

.svg-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.svg-container::-webkit-scrollbar {
  display: none;
}

#svgCanvas {
  width: 100%;
  height: 100%;
}

#thumb {
  width: 160px;
  height: 160px;
  pointer-events: none;
}

#thumb-svg-container {
  position: absolute;
  left: 10px;
  bottom: 20px;
  border: 1px solid silver;
  background-color: white;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
}

/*.bling{ animation: alarm 0.4s  ease-in  infinite ; fill:yellow; font-weight: bold;}
    @keyframes alarm {
        0%{ fill:#FF9966;}
        50%{ fill:#FF3333;}
        100%{ fill:#FF9966;}
    }*/
</style>
