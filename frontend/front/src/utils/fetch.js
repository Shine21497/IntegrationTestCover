import axios from 'axios'
import store from '@/store'
import { Message } from 'element-ui'

// 创建axios实例
const service = axios.create({
    baseURL: 'http://localhost:8088/',
    validateStatus: function (status) {
        return status < 500
    },
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    },
    withCredentials: false
})


// respone拦截器
service.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        console.log(error)
        if (error.response) {
            console.log(error.response.status)
        }
        console.log('err' + error)// for debug
        Message({
            message: error.message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
