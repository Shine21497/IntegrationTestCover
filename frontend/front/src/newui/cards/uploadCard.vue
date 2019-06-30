<template>
  <div>
    <el-dialog title="上传项目" :visible.sync="uploadProject">
      <el-upload
        drag
        multiple
        class="upload-demo"
        accept="application/jar"
        action="/apiurl/uploadJar"
        :file-list="fileList"
        :before-upload="checkJar"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将打包好的项目文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" slot="tip">只能上传jar包</div>
      </el-upload>
    </el-dialog>
    <el-dialog :title="versionTitle" :visible.sync="newVersion">
      <el-select
        v-if="Object.entries(data).length === 0"
        v-model="temp"
        placeholder="请选择回归测试的根项目"
        @visible-change="showTestProjectList"
        @change="getVersions($event)"
      >
        <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
      </el-select>
      <el-upload
        drag
        multiple
        class="upload-demo"
        accept="application/jar"
        action="/apiurl/uploadRegressiveJar"
        :limit="3"
        :before-upload="checkData"
        :file-list="fileList"
        :data="data"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将新版本的项目文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" slot="tip">只能上传jar包</div>
      </el-upload>
    </el-dialog>
    <el-dialog :title="testTitle" :visible.sync="testCase">
      <el-select
        v-if="Object.entries(data).length === 0"
        v-model="temp"
        placeholder="请选择测试脚本所属的项目"
        @visible-change="showTestProjectList"
        @change="setTestProject($event)"
      >
        <el-option v-for="item in uploadedFiles" :key="item" :label="item" :value="item"></el-option>
      </el-select>
      <el-upload
        drag
        multiple
        class="upload-demo"
        accept="application/jar"
        action="/apiurl/uploadTestCase"
        :before-upload="checkJarorZip"
        :limit="3"
        :file-list="fileList"
        :data="data"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将包含测试用例的脚本文件文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" slot="tip">只能上传.java文件或者.zip文件</div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "uploadCard",
  props: {
    uploadedFiles: Array,
    defaultProject: ""
  },
  data() {
    return {
      temp: "",
      uploadProject: false,
      newVersion: false,
      testCase: false,
      fileList: [],
      data: {}, //传参
      versionTitle: "",
      testTitle: ""
    };
  },
  methods: {
    async showTestProjectList(open) {
      if (open) {
        if (!this.uploadedFiles.length) {
          this.$emit("getuploadfiles");
        }
      }
    },
    checkJar(file) {
      const isJAR = file.name.endsWith(".jar");
      if (!isJAR) {
        this.$message.error("只能上传.jar文件!");
        return false;
      }
      return isJAR;
    },
    checkJarorZip(file) {
      const isJava = file.name.endsWith(".java");
      const isZip = file.name.endsWith(".zip");
      if (!isJava && !isZip) {
        this.$message.error("只能上传.java文件或者.zip文件!");
        return false;
      } else {
        return true;
      }
    },
    checkData() {
      // 一定选好了根项目
      if (Object.entries(this.data).length > 0) return true;
      this.$message.error("请选择回归测试的根项目");
      return false;
    },
    setTestProject(prov) {
      let selectedProject = prov.split(".")[0];
      this.data = {
        selectedProject
      };
      this.testTitle = "上传" + selectedProject + "的测试脚本文件";
    },
    getVersions(prov) {
      let projectName = prov.split(".")[0];
      this.data = {
        projectName
      };
      this.versionTitle = "上传" + projectName + "的新版本";
    },
    uploadNewSucc() {},
    upload(type, params) {
      if (type === "project") {
        this.uploadProject = true;
        return;
      }
      if (type === "testcase") {
        if (params.selectedProject) {
          this.data = params;
          this.testTitle = "上传" + params.selectedProject + "的测试脚本文件";
        } else {
          this.data = {};
          this.temp = "";
          this.testTitle = "上传某项目的测试脚本文件";
        }
        this.testCase = true;
        return;
      }
      if (type === "regression") {
        if (params.projectName) {
          this.data = params;
          this.versionTitle = "上传" + params.projectName + "的新版本";
        } else {
          this.data = {};
          this.temp = "";
          this.versionTitle = "上传用于回归测试的新版本项目";
        }
        this.newVersion = true;
        return;
      }
    }
  },
  mounted() {},
  watch: {
    // 设置默认项目
    defaultProject(val) {
      let name = this.defaultProject.split(".")[0];
      this.data = {
        selectedProject: name,
        projectName: name
      };
    }
  }
};
</script>

<style scoped lang="less">
.upload {
  margin: 25px 0px 25px 0px;
}
.card {
  margin: 5px 0px 5px 0px;
}
</style>
