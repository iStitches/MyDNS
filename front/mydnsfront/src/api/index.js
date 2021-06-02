import request from '../utils/request'

// 查询域名列表
export function domainNameList(data){
    return request({
        url: '/analysis/domainList',
        method: 'get',
        params: data
    })
}

// 查询每条规则对应IP数目,TOPN
export function ipNumPerRules(data){
    return request({
        url: '/analysis/topIPNum',
        method: 'get',
        params: data
    })
}

// 总查询量、总应答量、上游查询次数统计
export function sumCount(){
    return request({
        url: '/analysis/allQueryNum',
        method: 'get'
    })
}

// 每分钟查询量
export function everyMinQuery(){
    return request({
        url: '/analysis/queryPerMin',
        method: 'get'
    })
}

// 今日查询来源前TOP5
export function originIPTop5(){
    return request({
        url: '/analysis/queryIPAddressTop5',
        method: 'get'
    })
}

// 进入域名访问量前TOP5
export function domainNameTop5() {
    return request({
        url: '/analysis/queryDomainNameTop5',
        method: 'get'
    })
}

// 禁止/启用配置管理
export function domainNameControl(data) {
    return request({
        url: '/manage/setEnable',
        method: 'get',
        params: data
    })
}

// 查询编辑项数据
export function findConcreteItem(data) {
    return request({
        url: '/manage/getDetail',
        method: 'get',
        params: data
    })
}

// 提交编辑数据更新
export function subscribeEdit(data) {
    return request({
        url: '/manage/updateRule',
        method: 'get',
        params: data
    })
}

// 删除数据项
export function deleteRule(data) {
    return request({
        url: '/manage/deleteRule',
        method: 'get',
        params: data
    })
}

// 新增数据项
export function addRule(data) {
    return request({
        url: '/manage/addRule',
        method: 'get',
        params: data
    })
}

