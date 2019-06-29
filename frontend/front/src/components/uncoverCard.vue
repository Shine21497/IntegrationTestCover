<template>
  <el-form label-width="80px">
    <el-form-item label="未覆盖分支选择">
      <el-select v-model="selectUncoverTest" placeholder="请选择未覆盖的边">
        <el-option v-for="item in uncover" :key="item" :label="item" :value="item"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="gotoUncover()">立即定位</el-button>
    </el-form-item>
    <div>
      <div id="branches">分支总数：{{branchnum}}；</div>
      <div id="usecase">执行用例数：{{usecasenum}}；</div>
      <div id="uncoverbranch">未覆盖分支数：{{uncoverlength}}；</div>
      <div id="coverrate">覆盖率：{{coverrate}}%：</div>-->
      <!-- <svg id="piechart" xmlns="http://www.w3.org/2000/svg" style="height:300px;width:300px">

      </svg>-->
    </div>
  </el-form>
</template>

<script type="text/ecmascript-6">
export default {
  name: "uncoverCard",
  props: {
    relation: Object
  },
  data() {
    return {
      selectUncoverTest: ""
    };
  },
  computed: {
    branchNum: function() {
      return this.relation.links.length;
    }
  },
  methods: {
    //获取未覆盖信息
    setUncover() {
      console.log(this.uncoverfullname);
      this.uncover = [];
      for (let index in this.uncoverfullname) {
        var temp = this.uncoverfullname[index].split(" ");
        var A = temp[0].split(":")[1];
        var B = temp[2].split(":")[1];
        if (this.uncover.indexOf(A + " call " + B) < 0)
          this.uncover.push(A + " call " + B);
      }
    }
  },
  created() {},
  mounted() {}
};
</script>

<style  scoped lang="less">
</style>