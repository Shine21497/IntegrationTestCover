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

export function runTestCase (projectname, testcasename, method) {
    return fetch({
        url: '/apiurl/runTestCase',
        method: 'get',
        params: {
            projectname: projectname,
            testcasename: testcasename,
            method: method
        }
    })
}

export function getTestRunningStatus (task_id_Key) {
    return fetch({
        url: '/apiurl/getInvokingProcess',
        method: 'get',
        params: {
            key: task_id_Key
        }
    })
}

export function getInvokingResults (task_id_Key) {
    return fetch({
        url: '/apiurl/getInvokingResults',
        method: 'get',
        params: {
            key: task_id_Key
        }
    })
}