<template>
   <div id="monitor">
       <side-bar>
         <template v-slot:pageContent>
            <div style="background:#EFF0F0">
               <el-row :gutter="20" style="padding: 10px 20px">
                    <el-col :span="8">
                        <!-- 用户信息 -->
                        <el-card shadow="hover" class="cardItem" style="height:252px">
                              <div class="user-info">
                                  <img src="../assets/img/header.jpg" class="user-avator" alt />
                                  <div class="user-info-content">
                                      <h1 style="color: #000"><i class="el-icon-data-line"></i>  流量监控</h1>
                                  </div>
                              </div>
                              <div class="user-info-list" style="font-size: 18px; font-weight:600; color: #408ED6">
                                  可实时监控域名访问量和IP来源请求量
                              </div>
                              <div class="user-info-list" style="margin-top: 8px; font-size: 18px; font-weight:600; color:#D33556">
                                  通过数据可视化图形展示更加直观,数据分析更明确方便
                              </div>
                        </el-card>
                        <!-- 域名查询频率排行 -->
                        <el-card shadow="hover" class="cardItem" style="height:250px;margin-bottom:10px">
                               <div class="top_title">
                                   <i class="el-icon-star-off"></i>
                                   <span class="span1">热门域名</span>
                                   <span class="span2">HOT_DOMAIN_NAME</span>
                               </div>
                              <div v-for="(item,index) in domainList" :key="index" style="margin-top:15px">
                                <span>{{item.name}}</span>
                                <el-progress :percentage="item.percentage" :color="domain_color_list[index]"></el-progress>
                              </div>
                        </el-card>
                    </el-col>
                    <el-col :span="16">
                        <!-- 实时数据统计 -->
                        <el-row :gutter="20" class="cardItem">
                            <el-col :span="8">
                                <el-card shadow="hover" class="monitor-tab" style="background:#D95043">
                                    <div class="monitor-tab-info">
                                        <i class="el-icon-search"></i>
                                        <div class="tab-info-right">
                                            <div class="tab-num">{{allQuery.sumQuery}}</div>
                                            <div class="tab-name">总查询量</div>
                                        </div>  
                                    </div>
                                </el-card>
                            </el-col>
                            <el-col :span="8">
                                <el-card shadow="hover" class="monitor-tab" style="background:#26C281">
                                    <div class="monitor-tab-info">
                                        <i class="el-icon-s-data"></i>
                                        <div class="tab-info-right">
                                            <div class="tab-num">{{allQuery.sumAnswer}}</div>
                                            <div class="tab-name">总应答量</div>
                                        </div>  
                                    </div>
                                </el-card>
                            </el-col>
                            <el-col :span="8">
                                <el-card shadow="hover" class="monitor-tab" style="background:#57889C">
                                    <div class="monitor-tab-info">
                                        <i class="el-icon-upload"></i>
                                        <div class="tab-info-right">
                                            <div class="tab-num">{{allQuery.sumUpStream}}</div>
                                            <div class="tab-name">上游查询次数</div>
                                        </div>  
                                    </div>
                                </el-card>
                            </el-col>
                        </el-row>
                        <el-row>
                          <!-- 常用域名列举介绍,直连 -->
                            <el-card shadow="hover" class="cardItem" style="height:371px;margin-bottom:10px">
                               <div class="top_title">
                                   <i class="el-icon-monitor"></i>
                                   <span class="span1">域名直连</span>
                                   <span class="span2">DNS_DOMAIN_NAME</span>
                               </div>
                               <div class="middle_cont">
                                   <div class="cont-img">
                                       <img src="../assets/img/bg.jpg" alt="常用的域名,直接点击就能进入了！！！">
                                   </div>
                                   <div class="cont-list">
                                       <ul>
                                         <li>
                                           <span class="fl">
                                             <a href="https://appengine.google.com">GoogleAppengine 谷歌云计算平台</a>
                                             <a href="https://www.google.com/analytics/web/">Google Analyse 谷歌数据分析平台</a>
                                           </span>
                                         </li>
                                         <li>
                                           <span class="fl">
                                             <a href="http://commons.apache.org/">Apache Commons 阿帕奇开源平台</a>
                                             <a href="http://neo4j.org/">neo4j NOSQL 图形数据库</a>
                                           </span>
                                         </li>
                                         <li>
                                           <span class="fl">
                                             <a href="https://github.com/">Github 世界上最大的代码托管平台</a>
                                             <a href="https://gitee.com/">Gitee 国内快速的代码托管平台</a>
                                           </span>
                                         </li>
                                         <li>
                                           <span class="fl">
                                             <a href="https://juejin.cn/">掘金 高质量博客阅读平台</a>
                                             <a href="https://stackoverflow.com/">Stack Overflow 高效解决问题平台</a>
                                           </span>
                                         </li>
                                         <li>
                                           <span class="fl">
                                             <a href="https://skills.bugbank.cn/">BUG Bank 网络安全漏洞银行</a>
                                             <a href="https://www.aqniu.com/">AQ NIU  网络安全安全牛</a>
                                           </span>
                                         </li>
                                       </ul>
                                   </div>
                               </div>
                            </el-card>
                        </el-row>
                    </el-col>
                </el-row>
                <!-- 图表 -->
                <el-row :gutter="10" style="padding: 0px 15px;">
                     <!-- 查询来源占比 -->
                     <el-col :span="6">
                         <el-card shadow="hover">
                            <!-- <my-charts :option="originOption" :height="'350px'" :width="'500px'" :time="time"></my-charts> -->
                            <div class="sortBar" style="background:#F3F3F3">
                                <div class="title">
                                    <span>查询来源TOP8</span>
                                </div>
                                <div class="body-box" style="border-radius:30px">
                                    <dv-scroll-board :config="ipConfig" style="width:300px;height:300px;border-radius:50px;color:black;font-size:16px;font-weight:700" />
                                </div>
                            </div>
                         </el-card>
                     </el-col>
                     <!-- 查询域名总计排行 -->
                     <el-col :span="12">
                         <el-card shadow="hover">
                             <my-charts :options="originOption" :time="time" :height="'190px'" :width="'500px'"></my-charts>
                             <my-charts :options="domainOption" :time="time" :height="'190px'" :width="'500px'"></my-charts>
                         </el-card>
                     </el-col>
                     <!-- 查询域名占比 -->
                     <el-col :span="6">
                         <el-card shadow="hover">
                            <div class="sortBar" style="background:#F3F3F3">
                                <div class="title">
                                    <span>访问域名TOP8</span>
                                </div>
                                <div class="body-box" style="border-radius:30px">
                                    <dv-scroll-board :config="domainConfig" style="width:300px;height:300px;border-radius:50px;color:black;font-size:16px;font-weight:700" />
                                </div>
                            </div>
                         </el-card>
                     </el-col>
                </el-row> 
                <!-- 每分钟查询量 -->
                <el-row :gutter="10" style="padding: 0px 20px;margin-top:15px">
                     <el-col :span="24">
                         <el-card shadow="hover">
                             <my-charts :options="everyMinOption" :height="'350px'" :width="'1200px'" :time="time"></my-charts>
                         </el-card>
                     </el-col>
                </el-row>
            </div>
         </template>
       </side-bar>
   </div>
</template>

<script>
import SideBar from '../components/SideBar.vue'
import MyCharts from '../components/MyCharts.vue'
import { sumCount } from '../api/index.js'
import { everyMinQuery } from '../api/index.js'
import { originIPTop5 } from '../api/index.js'
import { domainNameTop5 } from '../api/index.js'
import { v4 as uuidv4 } from 'uuid'

export default {
  name:'',
  data(){
   return {
      domain_color_list:['#42b983','#f1e05a','#f56c6c'],
      // 查询频率数据
      domainList:[
        {name:'www.github.com',percentage: 77.7},
        {name:'www.juejin.cn',percentage: 60.2},
        {name:'www.baidu.com',percentage: 80.3},
      ],
      // 每分钟查询量
      everyMinOption: {
            visualMap: [{
                show: false,
                type: 'continuous',
                seriesIndex: 0,
                min: 0,
                max: 400
            }, {
                show: false,
                type: 'continuous',
                seriesIndex: 1,
                dimension: 0,
                min: 0,
                max: 7
            }],
            title: [{
                left: 'center',
                top: '15%',
                text: '每分钟查询量'
            }],
            tooltip: {
                trigger: 'axis'
            },
            xAxis: [{
                data: ["04:12","05:12","06:12","07:12","08:12","09:12","10:12","11:12"]
            }, {
                data: ["04:12","05:12","06:12","07:12","08:12","09:12","10:12","11:12"],
                gridIndex: 1
            }],
            yAxis: [{
            }, {
                gridIndex: 1
            }],
            grid: [{
                bottom: '0%'
            }, {
                top: '35%'
            }],
            series: [{
                type: 'line',
                showSymbol: false,
                data: [116,113,56,89,44,56,20,30],
                xAxisIndex: 1,
                yAxisIndex: 1,
                areaStyle: {
                    normal: {
                        color: '#275F82' //改变区域颜色
                    }
                },
                itemStyle: {
                    normal : { 
                        color:'#275F82', //改变折线点的颜色
                        lineStyle:{ 
                            color:'#253A5D' //改变折线颜色
                        } 
                    }    
                }
            }]
      },
      // 查询来源IP
      originOption: {
              title: {
                    text: '查询来源IP',
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                legend: {
                    data: ['2011年']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                yAxis: {
                    type: 'category',
                    data: ['巴西', '印尼', '美国', '印度']
                },
                series: [
                    {
                        name: '2011年',
                        type: 'bar',
                        data: [
                                {
                                    value:200,
                                    itemStyle:{
                                    normal:{color:'#5470C6'}
                                }
                                }, 
                                {
                                    value:300,
                                    itemStyle:{
                                    normal:{color:'#5470C6'}
                                }
                                },
                                {
                                    value:1500,
                                    itemStyle:{
                                    normal:{color:'#5470C6'}
                                }
                                },
                                {
                                    value:300,
                                    itemStyle:{
                                    normal:{color:'#5470C6'}
                                }
                                }]
                    }
                ]
      },
      // 域名查询量
      domainOption: {
              title: {
                    text: '查询域名',
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                legend: {
                    data: ['2011年']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                yAxis: {
                    type: 'category',
                    data: ['巴西', '印尼', '美国', '印度']
                },
                series: [
                    {
                        name: '2011年',
                        type: 'bar',
                        data: [
                                {
                                    value:200,
                                    itemStyle:{
                                    normal:{color:'#91CC75'}
                                }
                                }, 
                                {
                                    value:300,
                                    itemStyle:{
                                    normal:{color:'#91CC75'}
                                }
                                },
                                {
                                    value:1500,
                                    itemStyle:{
                                    normal:{color:'#91CC75'}
                                }
                                },
                                {
                                    value:300,
                                    itemStyle:{
                                    normal:{color:'#91CC75'}
                                }
                                }
                        ]
                    }
                ]
      },
      time: '',
      timer: null,
      // 总查询量、上游总访问量、总应答量
      allQuery: {
          "sumQuery": 760,
          "sumAnswer": 759,
          "sumUpStream": 613
      },
      // 排行榜
      ipConfig: {
        header: ["IP名称", "今日访问量"],
        data: [
                [ '行1列2', '行1列3'],
                [ '行2列2', '行2列3'],
                [ '行3列2', '行3列3'],
                [ '行4列2', '行4列3'],
                [ '行5列2', '行5列3'],
                [ '行6列2', '行6列3'],
                [ '行7列2', '行7列3'],
                [ '行8列2', '行8列3'],
                [ '行9列2', '行9列3'],
                [ '行10列2', '行10列3']
        ],
        rowNum: 6, //表格行数
        headerHeight: 35,
        headerBGC: "#F3F3F3", //表头
        oddRowBGC: "#FFFFFF", //奇数行
        evenRowBGC: "#F3F3F3", //偶数行
        // index: true,
        columnWidth: [160],
        align: ["center"]
      },
      domainConfig: {
        header: ["域名名称", "今日查询量"],
        data: [],
        rowNum: 5, //表格行数
        headerHeight: 35,
        headerBGC: "#F3F3F3", //表头
        oddRowBGC: "#FFFFFF", //奇数行
        evenRowBGC: "#F3F3F3", //偶数行
        index: true,
        columnWidth: [50],
        align: ["center"]
      },
   }
  },
  components:{
    SideBar,
    MyCharts
  },
  methods: {
    //   工具函数：分钟转化为 小时：分钟
    ChangeHourMinutestr(str) {
        if (str !== "0" && str !== "" && str !== null) {
            return ((Math.floor(str / 60)).toString().length < 2 ? "0" + (Math.floor(str / 60)).toString() : 
            (Math.floor(str / 60)).toString()) + ":" + ((str % 60).toString().length < 2 ? "0" + (str % 60).toString() : (str % 60).toString());
            }
        else
            {
            return "";
            }
    },
    //   函数：渲染每分钟查询量
    paintEveryMinQuery(res) {
        var date = []
        for(var i=1;i<res.data.data.length;i++){
            var hourAndMin = this.ChangeHourMinutestr(i+"").split(":")
            date.push([hourAndMin[0]+":"+hourAndMin[1],res.data.data[i]])
        }
        this.everyMinOption.xAxis[0].data = date.map(function (item) {
            return item[0];
        })
        this.everyMinOption.xAxis[1].data = date.map(function (item) {
            return item[0];
        })
        this.everyMinOption.series[0].data = date.map(function (item) {
            return item[1];
        })
        this.everyMinOption.visualMap[1].max = date.length-1
        console.log(this.everyMinOption)
    },
    //   函数：渲染查询总量、应答量
    paintSumQuery() {
        sumCount().then(res=>{
            this.allQuery = {
                "sumQuery": res.data.data.dns_sumUpstream_query,
                "sumAnswer": res.data.data.dns_sum_answer,
                "sumUpStream": res.data.data.dns_sum_query
            }
        })
    }
  },
  mounted() {
      everyMinQuery().then(res=>{
          this.paintEveryMinQuery(res)
          this.time = uuidv4()
      })
      originIPTop5().then(res=>{
          var tmp = []
          var ipList = []
          var countList = []
          for(var i=0;i<res.data.data.length;i++){
              tmp.push([res.data.data[i].serverIP,"<span  class='colorRed'>"+res.data.data[i].queryCount+"</span>"])
              ipList.push(res.data.data[i].serverIP)
              countList.push({
                                value:res.data.data[i].queryCount,
                                itemStyle:{
                                    normal:{color:'#5470C6'}
                                }
                             })
          }
          this.ipConfig = {
                header: ["IP名称", "今日访问量"],
                data: tmp,
                rowNum: 3, //表格行数
                headerHeight: 35,
                headerBGC: "#F3F3F3", //表头
                oddRowBGC: "#FFFFFF", //奇数行
                evenRowBGC: "#F3F3F3", //偶数行
                // index: true,
                columnWidth: [160],
                align: ["center"],
                hoverPause: true
          }
          this.originOption.yAxis.data = ipList
          this.originOption.series[0].data = countList
          this.time = uuidv4()
      })
      domainNameTop5().then(res=>{
          var tmp = []
          var domainNameList = []
          var countList = []
          for(var i=0;i<res.data.data.length;i++){
              tmp.push([res.data.data[i].domainName,"<span  class='colorRed'>"+res.data.data[i].queryCount+"</span>"])
              domainNameList.push(res.data.data[i].domainName)
              countList.push({
                                value:res.data.data[i].queryCount,
                                itemStyle:{
                                    normal:{color:'#91CC75'}
                                }
                             })
          }
          this.domainConfig = {
                header: ["域名名称", "今日查询量"],
                data: tmp,
                rowNum: 3, //表格行数
                headerHeight: 35,
                headerBGC: "#F3F3F3", //表头
                oddRowBGC: "#FFFFFF", //奇数行
                evenRowBGC: "#F3F3F3", //偶数行
                // index: true,
                columnWidth: [180],
                align: ["center"]
            }
          this.domainOption.yAxis.data = domainNameList
          this.domainOption.series[0].data = countList
          this.time = uuidv4()
      })
      this.paintSumQuery()
  }
}
</script>

<style scoped>
.user-avator {
    width: 120px;
    height: 120px;
    border-radius: 50%;
}
.cardItem{
    margin-bottom: 20px;
}
.cardItem .user-info{
    display: flex;
    align-items: center;
    border-bottom: 2px solid #ccc;
    padding-bottom: 20px;
    margin-bottom: 20px;
}
.user-info .user-info-content{
    font-size: 14px;
    color: #999;
    padding-left: 50px;
    flex: 1;
}
.user-info-cont div:first-child {
    font-size: 30px;
    color: #222;
}
.monitor-tab{
    height: 130px;
    display: flex;
    align-items: center;
    justify-content: center;
}
.monitor-tab .monitor-tab-info{
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    width: 250px;
}
.monitor-tab .monitor-tab-info i{
    font-size: 60px;
    color: #C9F0DF;
    font-weight: 600;
}
.monitor-tab-info .tab-info-right div:first-child{
    font-weight: 600;
    font-size: 30px;
    color: white;
    margin-bottom: 10px;
}
.monitor-tab-info .tab-info-right div:last-child{
    font-weight: 600;
    color: white;
}
.top_title{
    display: flex;
    align-items: center;
}
.top_title i{
    font-size: 50px;
    font-weight: 600;
    color: #4692D7;
}
.top_title .span1{
    font-size: 30px;
    color: black;
    padding: 4px 10px 0 15px;
}
.top_title .span2{
    border-bottom: 3px solid #408ed6;
    overflow: hidden;
    font-size: 15px;
    line-height: 32px;
    color: #999;
}
.middle_cont{
    display: flex;
    margin-top: 40px;
}
.middle_cont img{
    width: 290px;
    height: 200px;
}
.middle_cont .cont-list ul{
    width: 650px;
    margin-left: 10px;
    height: 200px;
    border-bottom: 1px solid #eee;
    line-height: 39px;
    margin: 0 0 0 10px;
    overflow: hidden;
    padding-left: 30px;
}
.middle_cont .cont-list ul li{
    list-style: none;
    height: 42px;
    line-height: 25px;
    overflow: hidden;
    vertical-align: bottom;
}
.middle_cont .cont-list ul li .fl{
    display: flex;
}
.middle_cont .cont-list a{
    font-size: 18px;
    cursor: pointer;
    color: #333;
    overflow: hidden;
    text-decoration: none;
    text-align: left;
    width: 290px;
    margin-right: 30px;
}
.middle_cont .cont-list a:hover{
    color: rgb(77, 243, 127);
}
.sortBar{
    display: flex;
    flex-direction: column;
    height: 380px;
    align-items: center;
}
.sortBar .title{
    /* border: 1px solid black; */
    font-size: 18px;
    margin: 0 auto;
    margin-bottom: 10px;
    color: #FFFFFF;
    font-weight: 600;
    background: #F23D63;
    width: 100%;
    text-align: center;
    height: 40px;
    line-height: 40px;
}
.body-box{
    height: 200px;
    color: black;
}
</style>
