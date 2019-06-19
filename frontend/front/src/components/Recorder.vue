<template>
  <div class="act-form">
    <iframe :src="src" class="record_frame"></iframe>
  </div>
</template>

<script>
import * as rrweb from "rrweb";
import * as rrwebPlayer from "rrweb-player";
import { getRecord } from "@/api/methodcallrelationgraph.js";
import { setTimeout } from 'timers';

export default {
  name: "Recorder",
  data() {
    return {
      src: "https://www.baidu.com",
      events:[],            // 脚本录制,
      isRecording:false,    // 是否正在录制
    };
  },
  methods: {
    toggleRecord(){
        this.isRecording = !this.isRecording;
        this.startRecord();
    },
    startRecord() {
        let _this = this;
        let stopRecord = rrweb.record({
            emit(event) {
                if (!_this.isRecording) {
                    _this.save();
                    stopRecord();
                }
                // 将 event 存入 events 数组中
                _this.events.push(event);
            }
        });

    },
    // save 函数用于将 events 发送至后端存入，并重置 events 数组
    save() {
        console.log('save')
        localStorage.setItem('testevents',JSON.stringify(this.events));
    },
  },
  mounted(){
    this.toggleRecord()
    setTimeout(()=>{
        this.toggleRecord();
    },10000)
  }
};
</script>

<style scoped lang="less">
.record_frame{
    position:absolute;
    height: 100%;
    width:100%;
    left:0;
    right:0;
}
</style>
