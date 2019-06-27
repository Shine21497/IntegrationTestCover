import Vue from 'vue'
import Router from 'vue-router'
import MethodCallRelationGraph from './components/MethodCallRelationGraph.vue'
import Player from './components/Player.vue'
import Main from './views/Main.vue'

Vue.use(Router)

export default new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: [
        {
          path: '/',
          name: 'main',
          component: Main
        },
        {
            path: '/method',
            name: 'MethodCallRelationGraph',
            component: MethodCallRelationGraph
        },
        {
            path: '/player',
            name: 'Player',
            component: Player
        }
    ]
})
