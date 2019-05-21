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

export function getRelationByFileName (name, packages, packagesToCall) {
    return fetch({
        url: '/apiurl/relation',
        method: 'get',
        params: {
            name: name,
            packages: packages,
            packagesToCall: packagesToCall
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

export function getTestRunningStatus (taskId) {
    return fetch({
        url: '/apiurl/getInvokingProcess',
        method: 'get',
        params: {
            key: taskId
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

export function postRegression (params) {
    return fetch({
        url: '/apiurl/regressiontest',
        method: 'get',
        params: params
    })
}