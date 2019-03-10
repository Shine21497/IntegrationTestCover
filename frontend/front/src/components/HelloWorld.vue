<template>
    <div v-cloak class="hello">
        <h1>{{ msg }}</h1>
    </div>
</template>

<script>
    import { getHelloWorldData } from '@/api/helloworld.js'
    export default {
        name: 'HelloWorld',
        data(){
            return {
                msg: ""
            }
        },
        mounted() {
            let _this = this
            getHelloWorldData().then(response => {
                _this.msg = response
            })
        },
        created() {
        },
        methods: {

        }
    }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less">
    [v-cloak] {
        display: none;
    }

</style>
