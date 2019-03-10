/**
 * @Author: Shine
 * @Date: 2019/3/9
 */
import fetch from '@/utils/fetch'

export function getHelloWorldData () {
    return fetch({
        url: '/apiurl/helloworld',
        method: 'get'
    })
}