<template>
  <div>
    <Graph ref="graph" />
    <ControlPanel
      @generateGraph="generateGraph"
      @locatenode="locatenode"
      @displayResult="displayResult"
      @cancelResult="cancelResult"
    />
  </div>
</template>

<script>
// @ is an alias to /src
import Graph from "@/components/Graph.vue";
import ControlPanel from "@/components/ControlPanel.vue";

export default {
  name: "MainStage",
  components: {
    Graph,
    ControlPanel
  },
  data() {
    return {};
  },
  methods: {
    generateGraph(relation) {
      this.$refs.graph.createGraph(relation);
    },
    locatenode(node) {
      this.$refs.graph.locatenode(node);
    },
    displayResult(result) {
      this.$refs.graph.showEffect(result);
    },
    cancelResult() {
      this.$refs.graph.cancelShow();
    }
  }
};
</script>
<style scoped  lang="less">
</style>
