<template>
  <div class="replayer" ref="frame">

  </div>
</template>

<script>
import * as rrweb from "rrweb";
import * as rrwebPlayer from "rrweb-player";
import { getRecord } from "@/api/methodcallrelationgraph.js";

export default {
  name: "Player",
  data() {
    return {
      events: [],
      replayer:null,
      currentTime:0,
    };
  },
  methods: {
    togglePlay(isplay){
      if (isplay){
        this.currentTime = this.replayer.timer.timeOffset + this.replayer.getTimeOffset();
        this.replayer.pause();
      }
      else
        this.replayer.resume(this.currentTime);
    }
  },
  mounted() {
    let events = JSON.parse(localStorage.getItem("events"));
    this.replayer = new rrweb.Replayer(events,{
        speed: 1,
        root: this.$refs.frame,
        // skipInactive: true,
        showWarning: true,
    });
    this.replayer.wrapper.style.transform ='scale(0.5) translate(-50%, -50%)';
    this.replayer.play();
  }
};
</script>

<style scoped lang="less">
.replayer{
    height:380px;
    width: 600px;
}
</style>
