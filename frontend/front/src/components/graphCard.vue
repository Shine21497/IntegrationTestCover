<template>
  <div ref="form" :model="form" style="width:100%;text-align:left">
    <el-row :gutter="20" style="margin:10px 0">
      <el-col :span="6">Jar包选择</el-col>
      <el-col :span="18">
        <el-select v-model="form.selectedjar" placeholder="请选择jar包" @visible-change="showfilelist">
          <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-col>
    </el-row>
    <div>
      <div style="color:darkgray;margin: 0 0 10px 10px;">如果打包时把lib一同打入，一定要输入包的范围</div>
      <el-row :gutter="20" style="margin:10px 0">
        <el-col :span="6">用于生成源节点的包</el-col>
        <el-col :span="18">
          <el-autocomplete
            class="inline-input"
            v-model="form.packages"
            :fetch-suggestions="packagesHistory"
            placeholder="请输入用于生成源节点的包"
          ></el-autocomplete>
        </el-col>
      </el-row>
    </div>
    <div>
      <div style="color:darkgray;margin:0 0 10px 10px;">如果打包时把lib一同打入，一定要输入包的范围</div>
      <el-row :gutter="20" style="margin:10px 0">
        <el-col :span="6">用于生成目标节点的包</el-col>
        <el-col :span="18">
          <el-autocomplete
            class="inline-input"
            v-model="form.packagesCall"
            :fetch-suggestions="packagesCallHistory"
            placeholder="请输入生成目标节点的包"
          ></el-autocomplete>
        </el-col>
      </el-row>
    </div>
    <div style="text-align:center">
      <el-button
        type="primary"
        :disabled="form.selectedjar.length == 0"
        size="small"
        @click="generateGraph"
      >立即创建</el-button>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  name: "graphCard",
  data() {
    return {
      form: {
        selectedjar: "",
        packages: "",
        packagesCall: ""
      },
      uploadedFiles: []
    };
  },
  methods: {
    packagesHistory(queryString, cb) {
      cb(this.history.packages);
    },
    generateGraph(){
        this.$emit("generateGraph");
    }
  },
  created() {}
};
</script>

<style  scoped lang="less">
.container {
  height: 800px;
  .exit {
    position: absolute;
    top: 200px;
    left: 20px;
    width: 60px;
    height: 25px;
    background: #0583f2;
    border: none;
    border-radius: 2px;
    color: #fff;
    z-index: 200;
    &:hover {
      background: #1e82d9;
    }
  }
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

.linetext {
  font-size: 12px;
  font-weight: bold;
  font-family: SimSun;
  fill: #000 !important;
  color: #000;
  fill-opacity: 1;
}

.this_is_a_style_in_D3Topo {
  font: outline;
}

//滑动鼠标显示连线效果，移到网页外连线消失
.edgelabel {
  stroke-width: 6px;
  fill: transparent;
  stroke: #dc143c;
  stroke-dasharray: 85 400;
  stroke-dashoffset: -220;
  transition: 1s all ease;
}

//字体火焰效果
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

.svg {
  position: relative;
  width: 100%;
  height: 300%;
}

.svg:hover .edgelabel {
  stroke-dasharray: 70 0;
  stroke-width: 3px;
  stroke-dashoffset: 0;
  stroke: #ffd700;
}

.node {
  position: relative;
}

.node:hover {
  cursor: pointer;
}
</style>