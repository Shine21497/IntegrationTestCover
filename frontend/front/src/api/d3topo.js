import fetch from '@/utils/fetch'

export function getDemoData () {
    return fetch({
        url: '/apiurl/demo',
        method: 'get'
    })
}