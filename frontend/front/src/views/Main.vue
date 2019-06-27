<template>
  <div>
    <Graph ref="graph" :relation="relation"/>
    <ControlPanel/>
  </div>
</template>

<script>
// @ is an alias to /src
import Graph from "@/components/Graph.vue";
import ControlPanel from "@/components/ControlPanel.vue";

export default {
  name: "main",
  components: {
    Graph,
    ControlPanel
  },
  data() {
    return {
      relation: {}
    };
  }
};
</script>
<style scoped  lang="less">
</style>
