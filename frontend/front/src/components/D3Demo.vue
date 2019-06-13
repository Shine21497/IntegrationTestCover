<template>
  <div>
    rap rap rap
    <input placeholder="123123">
    <svg></svg>
  </div>
</template>

<script>
import * as d3 from "d3";
import * as rrweb from "rrweb";
import { uploadRecord } from "@/api/methodcallrelationgraph.js";

export default {
  name: "d3-demo",
  data() {
    return {
      events: []
    };
  },
  methods: {
    // save 函数用于将 events 发送至后端存入，并重置 events 数组
    save() {
      localStorage.setItem('events',JSON.stringify(this.events));
    }
  },
  mounted() {
    let _this = this;
    rrweb.record({
      emit(event) {
        // 将 event 存入 events 数组中
        _this.events.push(event);
      }
    });

    // 每 10 秒调用一次 save 方法，避免请求过多
    setInterval(this.save, 10 * 1000);
  }
};
</script>

<style scoped lang="less">
</style>
