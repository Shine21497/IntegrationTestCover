/**
 * @Author: Kris Huang
 * @Date: 2019/06/24
 */
import fetch from '@/utils/fetch'

export function getHelloWorldData () {
    return fetch({
        url: '/apiurl/helloworld',
        method: 'get'
    })
}