<template>
  <div>
    <div class="card-head">
      <span>生成调用关系图</span>
      <el-button
        style="float: right; padding: 3px 0"
        type="text"
        :disabled="form.selectedjar.length == 0"
        size="small"
        @click="generateGraph"
      >立即创建</el-button>
    </div>

    <el-form label-position="left" label-width="100px">
      <el-form-item label="选择项目">
        <el-select v-model="form.selectedjar" placeholder="请选择jar包" @visible-change="showfilelist">
          <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="源节点范围">
        <el-autocomplete
          class="inline-input"
          v-model="form.packages"
          :fetch-suggestions="packagesHistory"
          placeholder="请输入用于生成源节点的包"
        ></el-autocomplete>
      </el-form-item>
      <el-form-item label="目标节点范围">
        <el-autocomplete
          class="inline-input"
          v-model="form.packagesCall"
          :fetch-suggestions="packagesCallHistory"
          placeholder="请输入生成目标节点的包"
        ></el-autocomplete>
      </el-form-item>
    </el-form>
  </div>
</template>

<script type="text/ecmascript-6">
import { getUploadedFileList } from "@/api/methodcallrelationgraph.js";
export default {
  name: "graphCard",
  props: {
    uploadedFiles: Array
  },
  data() {
    return {
      form: {
        selectedjar: "",
        packages: "",
        packagesCall: ""
      },
      history: {
        packages: [],
        packagesCall: []
      }
    };
  },
  methods: {
    packagesHistory(queryString, cb) {
      cb(this.history.packages);
    },
    generateGraph() {
      this.$emit("generateGraph", this.form);
      // 保存历史记录
      if (
        this.history.packagesCall.filter(
          item => item.value == this.form.packagesCall
        ).length == 0
      ) {
        this.history.packagesCall.push({ value: this.form.packagesCall });
      }
      if (
        this.history.packages.filter(item => item.value == this.form.packages)
          .length == 0
      ) {
        this.history.packages.push({ value: this.form.packages });
      }
      localStorage.setItem("history", JSON.stringify(this.history));
    },
    packagesCallHistory(queryString, cb) {
      // cb([{ "value": "asdasd"}]);
      cb(this.history.packagesCall);
    },
    showfilelist(open) {
      if (open) {
        if (!this.uploadedFiles.length) {
          this.$emit("getuploadfiles");
        }
      }
    }
  },
  created() {},
  mounted() {
    var historyForm = JSON.parse(localStorage.getItem("history"));
    if (historyForm) {
      this.history = historyForm;
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

</style>