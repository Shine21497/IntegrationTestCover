<template>
  <el-form ref="uploadTestData" :model="uploadTestData" label-width="80px">
    <el-form-item label="被测项目">
      <el-select
        v-model="uploadTestData.selectedProject"
        placeholder="请选择项目"
        @visible-change="showfilelist"
      >
        <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
      </el-select>
    </el-form-item>
    <el-upload
      class="upload"
      action="/apiurl/uploadTestCase"
      accept="application/jar"
      :before-upload="onBeforeUploadTestCase"
      ref="uploadTest"
      :file-list="fileList"
      :auto-upload="false"
      :data="uploadTestData"
    >
      <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
      <el-button
        style="margin-left: 10px;"
        size="small"
        type="success"
        @click="submitTestUpload"
      >上传到服务器</el-button>
      <div slot="tip" class="el-upload__tip">只能上传java文件或者zip文件</div>
    </el-upload>
  </el-form>
</template>

<script type="text/ecmascript-6">
import { watch } from "fs";
export default {
  name: "uploadTestCard",
  props: {
    uploadedFiles: Array,
    defaultProject: ""
  },
  data() {
    return {
      fileList: [],
      uploadTestData: {
        selectedProject: ""
      }
    };
  },
  methods: {
    submitTestUpload() {
      this.$refs.uploadTest.submit();
      //   this.selectTestForm.selectedTestProject = "";
      //   this.selectTestForm.selectedTestClass = "";
      //   this.selectTestForm.selectedTestCase = "";
      //   this.testCaseMap = {};
    },
    onBeforeUploadTestCase(file) {
      const isJava = file.name.endsWith(".java");
      const isZip = file.name.endsWith(".zip");
      if (!isJava && !isZip) {
        this.$message.error("只能上传Java文件或者Zip文件!");
        return false;
      } else {
        return true;
      }
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
  mounted() {},
  watch: {
    // 设置默认项目
    defaultProject(val) {
      this.uploadTestData.selectedProject = this.defaultProject;
    }
  }
};
</script>

<style  scoped lang="less">
.upload {
  margin: 25px 0px 25px 0px;
}
</style>