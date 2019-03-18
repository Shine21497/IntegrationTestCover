import Vue from 'vue'
import Router from 'vue-router'
import D3Demo from './components/D3Demo.vue'
import HelloWorld from './components/HelloWorld.vue'
import D3Topo from './components/D3Topo.vue'
import MethodCallRelationGraph from './components/MethodCallRelationGraph.vue'

Vue.use(Router)

export default new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/',
            name: 'demo',
            component: D3Demo
        },
        {
            path: '/hello',
            name: 'hello',
            component: HelloWorld
        },
        {
            path: '/topo',
            name: 'topo',
            component: D3Topo
        },
        {
            path: '/method',
            name: 'MethodCallRelationGraph',
            component: MethodCallRelationGraph
        },
    ]
})
