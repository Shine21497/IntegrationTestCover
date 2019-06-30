<template>
  <div>
    <div class="card-head">
      <span>向图中添加新节点</span>
      <el-button
        style="float: right; padding: 3px 0"
        type="text"
        :disabled="newnode.length === 0 || selectednodetype.length === 0 || selectednode.length === 0"
        @click="addNode()"
      >立即添加</el-button>
    </div>

    <el-form label-position="left" label-width="80px">
      <el-form-item label="现有节点">
        <el-cascader
          v-model="cascaderNode"
          :options="cascaderClassMethod"
          @visible-change="praseClassMethod"
          @change="selectNode"
        ></el-cascader>
      </el-form-item>
      <el-form-item label="新节点">
        <el-input type="textarea" v-model="newnode" placeholder="请输入新节点名称"></el-input>
      </el-form-item>
      <el-form-item label="类型选择">
        <el-select
          v-model="selectednodetype"
          filterable
          placeholder="请选择节点类型"
          @change="getnodetype()"
        >
          <el-option label="数据库" value="1"></el-option>
          <el-option label="外设" value="2"></el-option>
          <el-option label="前端" value="3"></el-option>
          <el-option label="其他系统" value="4"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>

<script type="text/ecmascript-6">
export default {
  name: "addNodeCard",
  props: {
    classMethodMap: Object
  },
  data() {
    return {
      // ui
      cascaderClassMethod: [], // 添加节点板块的级联选择，已有节点
      cascaderNode: [], // 级联选择的对象，需要解析成 selectednode

      // data
      selectednode: "", // 新节点添加到这个节点上
      newnode: "", // 新节点
      selectednodetype: ""
    };
  },
  methods: {
    // 把 this.classMethodMap 解析成 cascader 格式的 ClassMethod
    praseClassMethod(open) {
      if (open) {
        // 展开的时候
        if (!this.cascaderClassMethod.length) {
          // 还未生成级联关系
          let classMethodMapEntries = Object.entries(this.classMethodMap);
          if (classMethodMapEntries.length > 0)
            // 已有classMethodMap，已生成关系图
            classMethodMapEntries.forEach(keyValue => {
              this.cascaderClassMethod.push({
                value: keyValue[0],
                label: keyValue[0],
                children: keyValue[1].map(value => {
                  return {
                    value,
                    label: value
                  };
                })
              });
            });
          else
            this.$message({
              showClose: true,
              message: "请先生成调用关系图"
            });
        }
      }
    },
    selectNode() {
      this.selectednode = this.cascaderNode[0] + ":" + this.cascaderNode[1];
    },
    addNode() {
      this.$emit("addnewnode", {
        selectednode: this.selectednode,
        newnode: this.newnode
      });
    },
    getnodetype() {
      console.log("不知道 getnodetype 要干嘛");
    }
  },
  created() {},
  mounted() {}
};
</script>

<style  scoped lang="less">
.card-head {
  padding: 10px 0;
  margin: 0 0 15px;
  border-bottom: 1px solid gray;
}
</style>