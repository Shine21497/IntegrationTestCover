/**
 * @Author: Shine
 * @Date: 2019/3/18
 */

import fetch from '@/utils/fetch'

export function getUploadedFileList () {
    return fetch({
        url: '/apiurl/fileList',
        method: 'get'
    })
}

export function getRelationByFileName (name, packages) {
    return fetch({
        url: '/apiurl/relation',
        method: 'get',
        params: {
            name: name,
            packages: packages
        }
    })
}

export function getTestCaseList () {
    return fetch({
        url: '/apiurl/testCaseList',
        method: 'get'
    })
}