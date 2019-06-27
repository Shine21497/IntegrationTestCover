<template>
  <div>
    <el-card :body-style="{ padding: '0px' }" class="card">
      <el-upload
        class="upload"
        action="/apiurl/uploadJar"
        accept="application/jar"
        :before-upload="onBeforeUpload"
        ref="upload"
        :file-list="fileList"
        :auto-upload="false"
      >
        <el-button slot="trigger" type="primary">选取文件</el-button>
        <el-button style="margin-left: 10px;" type="success" @click="submitUpload">上传到服务器</el-button>
        <div slot="tip" class="el-upload__tip">只能上传jar文件</div>
      </el-upload>
    </el-card>
  </div>
</template>

<script>


export default {
  name: "uploadCard",
  data() {
    return {
      fileList: []
    };
  },
  methods: {
    submitUpload() {
      this.$refs.upload.submit();
    },
    onBeforeUpload(file) {
      const isJAR = file.name.endsWith(".jar");
      if (!isJAR) {
        this.$message.error("只能上传Jar文件!");
        return false;
      }
      return isJAR;
    }
  },
  mounted() {}
};
</script>

<style scoped lang="less">
.upload {
  margin: 25px 0px 25px 0px;
}
</style>
