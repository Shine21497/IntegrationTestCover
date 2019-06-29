<template>
  <el-form label-width="80px">
    <el-form-item label="类选择">
      <el-select filterable v-model="selectedClass" placeholder="请选择类" @change="getClass($event)">
        <el-option v-for="(item,index) in allClasses" :key="item + index" :label="item" :value="item"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="方法选择">
      <el-select filterable v-model="selectedMethod" placeholder="请选择方法">
        <el-option v-for="(item,index) in allMethods" :key="item + index" :label="item" :value="item"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button
        :disabled="selectedMethod.length == 0"
        type="primary"
        size="small"
        @click="locateNode()"
      >立即定位</el-button>
    </el-form-item>
  </el-form>
</template>

<script type="text/ecmascript-6">
export default {
  name: "locateCard",
  props: {
    allClasses: Array,
    classMethodMap: Object
  },
  data() {
    return {
      selectedClass: "",
      selectedMethod: "",
      allMethods: []
    };
  },
  methods: {
    getClass(prov) {
      this.allMethods = this.classMethodMap[prov];
      this.selectedMethod = "";
    },
    locateNode() {
       let nodename =  this.selectedClass + ":" + this.selectedMethod;
        this.$emit('locatenode',nodename)
    }
  },
  created() {},
  mounted() {}
};
</script>

<style  scoped lang="less">
</style>