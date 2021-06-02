<template>
   <div id="analysis">
      <side-bar>
          <template v-slot:pageContent>
               <div id="analysis" style="background:#EFF0F0">
                 <!-- 表格 -->
                 <el-row :gutter="20" style="padding: 10px 20px">
                    <el-col :span="24">
                        <el-card shadow="hover" class="analysis-tbl">
                        <!-- 自定义表头 -->
                            <div class="top-tab">
                               <span>域名解析设置</span>
                               <el-button type="success" icon="el-icon-plus" @click="handleAdd">添加新条目</el-button>
                               <el-dialog title="DNS域名解析配置" :visible.sync="isAdd" width="80%">
                                  <el-row :gutter="30">
                                    <el-col :span="16">
                                        <!-- 表单 -->
                                        <el-form ref="editForm" :model="addForm" label-width="150px">
                                          <el-form-item label="来源IP段：">
                                            <el-row>
                                                <el-col :span="11">
                                                    <el-input v-model="addForm.ipFrom" placeholder="开始地址,包含"></el-input>
                                                </el-col>
                                                <el-col :span="1">
                                                    <div> 至</div>
                                                </el-col>
                                                <el-col :span="11">
                                                    <el-input v-model="addForm.ipTo" placeholder="结束地址,包含"></el-input>
                                                </el-col>
                                            </el-row>
                                          </el-form-item>
                                          <el-form-item label="请求时间段：">
                                            <el-time-picker
                                                v-model="addForm.startTime"
                                                :picker-options="{
                                                  selectableRange: '00:00:00 - 23:59:59'
                                                }"
                                                value-format= "HH:mm:ss"
                                                placeholder="任意时间点">
                                              </el-time-picker>
                                            <el-time-picker
                                                v-model="addForm.endTime"
                                                :picker-options="{
                                                  selectableRange: '00:00:00 - 23:59:59'
                                                }"
                                                value-format= "HH:mm:ss"
                                                placeholder="任意时间点">
                                              </el-time-picker>
                                          </el-form-item>
                                          <el-form-item label="匹配模式：">
                                              <el-radio v-model="addForm.matchMode" label="prefix">前缀匹配,如www.匹配www.sina.cn、www.163.com等</el-radio>
                                              <el-radio v-model="addForm.matchMode" label="suffix">后缀匹配,如.baidu.com匹配www.baidu.com、tieba.baidu.com等</el-radio>
                                              <el-radio v-model="addForm.matchMode" label="contains">包含,只要解析的域名中包含指定域名即可</el-radio>
                                          </el-form-item>
                                          <el-form-item label="匹配域名：">
                                            <el-input v-model="addForm.ruleName" placeholder="例如 .baidu.com"></el-input>
                                          </el-form-item>
                                          <el-form-item label="应答IP分发模式：">
                                              <el-radio v-model="addForm.dispatchMode" label="round-robin">轮循,依次应答每个IP</el-radio>
                                              <el-radio v-model="addForm.dispatchMode" label="hash">IP Hash,与请求来源IP绑定</el-radio>
                                              <el-radio v-model="addForm.dispatchMode" label="random">随机选择一个IP分发</el-radio>
                                          </el-form-item>
                                        </el-form>
                                    </el-col>
                                    <el-col :span="8">
                                        <el-row>
                                          <el-col :span="24">
                                              <el-input type="textarea" v-model="addForm.respIPList" :autosize="{minRows:8}" placeholder="请输入应答IP,一行一个IP"></el-input>
                                          </el-col>
                                        </el-row>
                                        <el-row>
                                          <el-col>
                                              <div class="form-notice">在设置完域名所指向的IP后,注意记得到cmd窗口下使用nslookup进行测试哦！</div>
                                          </el-col>
                                        </el-row>
                                    </el-col>
                                  </el-row>
                                    <!-- 通知脚 -->
                                        <div slot="footer" class="dialog-footer">
                                          <el-button @click="isAdd = false">取 消</el-button>
                                          <el-button type="primary" @click="submitAdd">确 定</el-button>
                                        </div>
                              </el-dialog>
                            </div>
                            <!-- 具体表格信息 -->
                            <el-table
                                :data="tableData"
                                stripe
                                style="width: 100%">
                                <el-table-column
                                  label="序号"
                                  width="90">
                                  <template slot-scope="scope">
                                    <span style="margin-left: 10px">{{ scope.row.index }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column
                                  label="IP地址段"
                                  width="280">
                                  <template slot-scope="scope">
                                    <span style="margin-left: 10px">{{ scope.row.address }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column
                                  label="时间段"
                                  width="260">
                                  <template slot-scope="scope">
                                    <i class="el-icon-time"></i>
                                    <span style="margin-left: 10px;font-size:16px;font-weight:600">{{ scope.row.date }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column
                                  label="域名"
                                  width="180">
                                  <template slot-scope="scope">
                                    <el-popover trigger="hover" placement="top">
                                      <p>匹配规则: {{ scope.row.name }}</p>
                                      <p>IP地址段: {{ scope.row.address }}</p>
                                      <div slot="reference" class="name-wrapper">
                                        <el-tag size="medium">{{ scope.row.name }}</el-tag>
                                      </div>
                                    </el-popover>
                                  </template>
                                </el-table-column>
                                <el-table-column
                                  label="域名匹配模式"
                                  width="180">
                                  <template slot-scope="scope">
                                    <span style="margin-left: 10px">{{ scope.row.matchMode }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column
                                  label="IP分发模式"
                                  width="180">
                                  <template slot-scope="scope">
                                    <span style="margin-left: 10px">{{ scope.row.dispatchMode }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column
                                  label="启用"
                                  width="180">
                                  <template slot-scope="scope">
                                    <el-tooltip :content="scope.row.isOpen==true?'开启':'关闭'" placement="top">
                                      <el-switch
                                        v-model="scope.row.isOpen"
                                        active-color="#13ce66"
                                        inactive-color="#ff4949"
                                        @change="enabledChange(scope.row)">
                                      </el-switch>
                                    </el-tooltip>
                                  </template>
                                </el-table-column>
                                <!-- 编辑、删除按钮 -->
                                <el-table-column label="操作" width="180px">
                                  <template slot-scope="scope">
                                    <el-button
                                      size="mini"
                                      @click="handleEdit(scope.row)">编辑</el-button>
                                        <el-dialog title="DNS域名解析配置" :visible.sync="isEdit" width="80%">
                                            <el-row :gutter="30">
                                              <el-col :span="16">
                                                  <!-- 表单 -->
                                                  <el-form ref="editForm" :model="editForm" label-width="150px">
                                                    <el-form-item label="来源IP段：">
                                                      <el-row>
                                                         <el-col :span="11">
                                                             <el-input v-model="editForm.ipFrom" placeholder="开始地址,包含"></el-input>
                                                         </el-col>
                                                         <el-col :span="1">
                                                             <div> 至</div>
                                                         </el-col>
                                                         <el-col :span="11">
                                                             <el-input v-model="editForm.ipTo" placeholder="结束地址,包含"></el-input>
                                                         </el-col>
                                                      </el-row>
                                                    </el-form-item>
                                                    <el-form-item label="请求时间段：">
                                                      <el-time-picker
                                                          v-model="editForm.startTime"
                                                          :picker-options="{
                                                            selectableRange: '00:00:00 - 23:59:59'
                                                          }"
                                                          value-format= "HH:mm:ss"
                                                          placeholder="任意时间点">
                                                        </el-time-picker>
                                                      <el-time-picker
                                                          v-model="editForm.endTime"
                                                          :picker-options="{
                                                            selectableRange: '00:00:00 - 23:59:59'
                                                          }"
                                                          value-format= "HH:mm:ss"
                                                          placeholder="任意时间点">
                                                        </el-time-picker>
                                                    </el-form-item>
                                                    <el-form-item label="匹配模式：">
                                                        <el-radio v-model="editForm.matchMode" label="prefix">前缀匹配,如www.匹配www.sina.cn、www.163.com等</el-radio>
                                                        <el-radio v-model="editForm.matchMode" label="suffix">后缀匹配,如.baidu.com匹配www.baidu.com、tieba.baidu.com等</el-radio>
                                                        <el-radio v-model="editForm.matchMode" label="contains">包含,只要解析的域名中包含指定域名即可</el-radio>
                                                    </el-form-item>
                                                    <el-form-item label="匹配域名：">
                                                      <el-input v-model="editForm.ruleName" placeholder="例如 .baidu.com"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="应答IP分发模式：">
                                                        <el-radio v-model="editForm.dispatchMode" label="round-robin">轮循,依次应答每个IP</el-radio>
                                                        <el-radio v-model="editForm.dispatchMode" label="hash">IP Hash,与请求来源IP绑定</el-radio>
                                                        <el-radio v-model="editForm.dispatchMode" label="random">随机选择一个IP分发</el-radio>
                                                    </el-form-item>
                                                  </el-form>
                                              </el-col>
                                              <el-col :span="8">
                                                  <el-row>
                                                    <el-col :span="24">
                                                        <el-input type="textarea" v-model="editForm.respIPList" :autosize="{minRows:8}" placeholder="请输入应答IP,一行一个IP" style="font-size:17px;color:#f43e3e"></el-input>
                                                    </el-col>
                                                  </el-row>
                                                  <el-row>
                                                    <el-col>
                                                        <div class="form-notice">在设置完域名所指向的IP后,注意记得到cmd窗口下使用nslookup进行测试哦！</div>
                                                    </el-col>
                                                  </el-row>
                                              </el-col>
                                            </el-row>
                                              <!-- 通知脚 -->
                                                  <div slot="footer" class="dialog-footer">
                                                    <el-button @click="isEdit = false">取 消</el-button>
                                                    <el-button type="primary" @click="editSubscribe">确 定</el-button>
                                                  </div>
                                        </el-dialog>
                                    <el-button
                                      size="mini"
                                      type="danger"
                                      @click="handleDelete(scope.row)">删除</el-button>
                                  </template>
                                </el-table-column>
                            </el-table>
                            <!-- 分页条 -->
                            <div class="tbl-pageinate">
                                <el-pagination
                                  :pager-count="pageCounts"
                                  layout="sizes,prev, pager, next"
                                  :total="totalCounts"
                                  @current-change="pageClick"
                                  @size-change="handleSizeChange"
                                  :current-page.sync="currentPage"
                                  :page-size="pageSize"
                                  :page-sizes="[3, 5, 8, 10]"
                                  background
                                  style="text-align:center;padding-top:20px">
                                </el-pagination>
                            </div>
                        </el-card>
                    </el-col>
                 </el-row>

                 <!-- 域名对应IP数统计排行 -->
                 <el-row :gutter="20" style="padding: 10px 20px" class="top-graph">
                    <el-col :span="24">
                         <el-card shadow="hover">
                             <div class="graph-header">
                               <h4>自定义查询</h4>
                               <el-input v-model="curTopNum" placeholder="请输入查询的TopN数量"  class="graph-header-input"></el-input>
                               <el-button type="success" icon="el-icon-search" @click="changeTopN">搜索</el-button>
                             </div>
                              <el-tabs v-model="chooseGroup" @tab-click="handleClick" class="changeTabs" type="card">
                                <el-tab-pane label="横向" name="0">
                                   <span slot="label"><i class="el-icon-top"></i> 纵向展示</span>
                                   <my-charts  :options="ruleMatchOption" :time="time1" :height="'350px'" :width="'1200px'" ref="chart1"></my-charts>
                                </el-tab-pane>
                                <el-tab-pane label="反转" name="1">
                                   <span slot="label"><i class="el-icon-right"></i> 横向展示</span>
                                   <my-charts  :options="ruleMatchOption2" :time="time2" :height="'350px'" :width="'1200px'" ref="chart2"></my-charts>
                                </el-tab-pane>
                                <el-tab-pane label="环状" name="2">
                                   <span slot="label"><i class="el-icon-more"></i> 其它方式</span>
                                   <my-charts  :options="ruleMatchOption3" :time="time3" :height="'350px'" :width="'1200px'" ref="chart3"></my-charts>
                                </el-tab-pane>
                              </el-tabs>
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
import { v4 as uuidv4 } from 'uuid'
import { domainNameList } from '../api/index.js'
import { domainNameControl } from '../api/index.js'
import { findConcreteItem } from '../api/index.js'
import { trim } from '../api/common.js'
import { subscribeEdit } from '../api/index.js'
import { deleteRule } from '../api/index.js'
import { addRule } from '../api/index.js'
import { changeDateFormat } from '../api/common.js'
import { ipNumPerRules } from '../api/index.js'

export default {
  name:'',
  data(){
   return {
        tableData: [{
          index: 1,
          itemId: 0,
          date: '2016-05-02',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄',
          isOpen: true
        }, {
          index: 2,
          itemId: 0,
          date: '2016-05-04',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1517 弄',
          isOpen: true
        }, {
          index: 3,
          itemId: 0,
          date: '2016-05-01',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1519 弄',
          isOpen: false
        }, {
          index: 4,
          itemId: 0,
          date: '2016-05-03',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1516 弄',
          isOpen: false
        }],
        pageSize: 5,
        pageCounts: 5,
        totalCounts: 25,
        currentPage: 1,
        isEdit: false,
        isAdd: false,
        editForm: {
            ruleId: 0,
            ipFrom: '',
            ipTo: '',
            startTime: '',
            endTime: '',
            matchMode: '',
            ruleName: '',
            dispatchMode: '',
            respIPList: ''
        },
        addForm: {
            ipFrom: '',
            ipTo: '',
            startTime: '',
            endTime: '',
            matchMode: '',
            ruleName: '',
            dispatchMode: '',
            respIPList: ''
        },
        N: 4,
        curTopNum: '',
        // 域名对应的IP地址数目统计排行
        ruleMatchOption: {
            title: {
                text: '域名规则映射IP地址数统计',
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['IP地址数目']
            },
            toolbox: {
                show: true,
                feature: {
                    dataView: {show: true, readOnly: false},
                    magicType: {show: true, type: ['line', 'bar']},
                    restore: {show: true},
                    saveAsImage: {show: true}
                }
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series: [
                {
                    name: 'IP地址数目',
                    type: 'bar',
                    data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3],
                    markPoint: {
                        data: [
                            {name: '年最高', value: 182.2, xAxis: 7, yAxis: 183},
                            {name: '年最低', value: 2.3, xAxis: 11, yAxis: 3}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    },
                    itemStyle: {
                       normal: {
                          color: '#DE33A5',
                          shadowBlur: 300,
                          shadowColor: 'rgba(0, 0, 0, 0.5)'
                       }
                    }
                }
            ]
        },
        ruleMatchOption2: {
            title: {
              text: '域名规则映射IP地址数统计'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
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
                data: ['巴西', '印尼', '美国', '印度', '中国', '世界人口(万)']
            },
            series: [
                {
                    name: '2012年',
                    type: 'bar',
                    data: [19325, 23438, 31000, 121594, 134141, 681807],
                    itemStyle:{
                        normal:{
                            color:'#F7AC46',
                            shadowBlur: 300,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    markPoint: {
                        data: [
                            {name: '年最高', value: 182.2, yAxis: 1, xAxis: 183},
                            {name: '年最低', value: 2.3, yAxis: 2, xAxis: 3}
                        ]
                    },
                }
            ]
        },
        ruleMatchOption3: {
            tooltip: {
                trigger: 'item'
            },
            legend: {
                top: '5%',
                left: 'center'
            },
            series: [
                {
                    name: 'IP地址数目',
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        show: false,
                        position: 'center'
                    },
                    emphasis: {
                        label: {
                            show: true,
                            fontSize: '40',
                            fontWeight: 'bold'
                        }
                    },
                    labelLine: {
                        show: false
                    },
                    data: [
                        {value: 1048, name: '搜索引擎'},
                        {value: 735, name: '直接访问'},
                        {value: 580, name: '邮件营销'},
                        {value: 484, name: '联盟广告'},
                        {value: 300, name: '视频广告'}
                    ],
                    itemStyle:{
                        normal:{
                            shadowBlur: 300,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                }
            ]
        },
        time1: '0',
        time2: '0',
        time3: '0',
        chooseGroup: "0"
   }
  },
  methods: {
      // 点击编辑按钮
      handleEdit(row) {
        this.isEdit = true;
        findConcreteItem({ruleId:row.itemId}).then(res=>{
           var rrr = this.parseIPAddress(res.data.data);
           var tmpIpFrom = rrr.split("-");
           this.editForm.ruleId = res.data.data.id
           this.editForm.ipFrom = trim(tmpIpFrom[0])
           this.editForm.ipTo = trim(tmpIpFrom[1])
           this.editForm.startTime = this.parseTimeRange(res.data.data.timeFrom+"")
           this.editForm.endTime = this.parseTimeRange(res.data.data.timeTo+"")
           this.editForm.matchMode = res.data.data.matchMode
           this.editForm.ruleName = res.data.data.ruleName
           this.editForm.dispatchMode = res.data.data.dispatchMode
           var ipArray = res.data.data.addressList
           var respIPStr = ""
           for(var i=0;i<ipArray.length-1;i++){
               respIPStr += (ipArray[i].address+"\n")
           }
           respIPStr += ipArray[ipArray.length-1].address
           this.editForm.respIPList = respIPStr
        })
      },
      // 编辑完成提交
      editSubscribe(){
          this.isEdit = false
          subscribeEdit({
            ruleId: this.editForm.ruleId,
            fromIP: this.editForm.ipFrom,
            toIP: this.editForm.ipTo,
            fromTime: this.editForm.startTime,
            toTime: this.editForm.endTime,
            matchMode: this.editForm.matchMode,
            ruleName: this.editForm.ruleName,
            dispatchMode: this.editForm.dispatchMode,
            addressList: this.editForm.respIPList
          }).then(res=>{
              if(res.data.code == "200")
              {
                this.$message({
                  type: 'success',
                  message: `成功完成编辑操作`
                })
              }
              else{
                  this.$message({
                    type: 'error',
                    message: `编辑修改失败,请重试`
                  })
              }
          })
      },
      // 提交删除数据项
      handleDelete(row) {
        this.$alert('确认删除?', '通知', {
          confirmButtonText: '确定',
          callback: action => {
            console.log(row)
            deleteRule({ruleId:row.itemId}).then(res=>{
              if(res.data.code == "200")
              {
                this.$message({
                  type: 'success',
                  message: `成功删除`
                })
              }
              else{
                  this.$message({
                    type: 'error',
                    message: `删除失败,请重试`
                  })
              }
            })
          }
        });
        this.paintTable(this.currentPage,this.pageSize)
      },
      // 点击新增按钮
      handleAdd() {
        this.isAdd = true;
      },
      // 提交新增数据
      submitAdd(){
          addRule({
            fromIP: this.addForm.ipFrom,
            toIP: this.addForm.ipTo,
            fromTime: this.addForm.startTime,
            toTime: this.addForm.endTime,
            matchMode: this.addForm.matchMode,
            ruleName: this.addForm.ruleName,
            dispatchMode: this.addForm.dispatchMode,
            addressList: this.addForm.respIPList
          }).then(res=>{
              if(res.data.code == "200")
              {
                this.$message({
                  type: 'success',
                  message: `新增规则成功`
                })
              }
              else{
                  this.$message({
                    type: 'error',
                    message: '新增失败,错误为：'+res.data.msg
                  })
              }
              this.isAdd = false
          })
      },
      // 解析时间段
      parseTimeRange(str){
         if(str == "0")
            return "00:00:00"
         else{
            var ans = ""
            ans += str.substring(0,2);
            ans += ":"+str.substring(2,4);
            ans += ":"+str.substring(4,6);
            return ans;
         }
      },
      // 解析IP地址
      parseIPAddress(r){
          var f = r.ipFrom;
          var t = r.ipTo;
          if (f == null || t == null) return '--';
          f = ((f >> 24) & 0xff) + '.' + ((f >> 16) & 0xff) + '.' + ((f >> 8) & 0xff) + '.' + ((f >> 0) & 0xff);
          t = ((t >> 24) & 0xff) + '.' + ((t >> 16) & 0xff) + '.' + ((t >> 8) & 0xff) + '.' + ((t >> 0) & 0xff);
          return f + ' - ' + t;
      },
      // 渲染表格数据
      paintTable(pageNum,pageSize) {
         domainNameList({"pageNum":pageNum,"pageSize":pageSize}).then(res=>{
              this.totalCounts = res.data.data.total;
              this.tableData = []
              for(var i=0;i<res.data.data.list.length;i++){
                var tmp = res.data.data.list[i]
                this.tableData.push({
                    index: i+1,
                    itemId: tmp.id,
                    date: this.parseTimeRange(tmp.timeFrom+"")+"~"+this.parseTimeRange(tmp.timeTo+""),
                    name: tmp.ruleName,
                    address: this.parseIPAddress(tmp),
                    isOpen: tmp.enabled,
                    matchMode: tmp.matchMode,
                    dispatchMode: tmp.dispatchMode
                  })
              } 
         })
      },
      // 渲染图形数据
      paintGraphChart(){
         ipNumPerRules({N:this.N}).then(res=>{
             // 如果当前是纵向柱状图
             if(this.chooseGroup == 0){
                  this.ruleMatchOption.xAxis[0].data = []
                  this.ruleMatchOption.series[0].data = []
                  for(var i=0;i<res.data.data.length;i++){
                      this.ruleMatchOption.xAxis[0].data.push(res.data.data[i].ruleName)
                      this.ruleMatchOption.series[0].data.push(res.data.data[i].addressList.length)
                  }
                  this.ruleMatchOption.series[0].markPoint.data = [
                    {name: '最高', value: res.data.data[0].addressList.length, xAxis: 0, yAxis: res.data.data[0].addressList.length},
                    {name: '最低', value: res.data.data[res.data.data.length-1].addressList.length, xAxis: res.data.data.length-1, yAxis: res.data.data[res.data.data.length-1].addressList.length}
                  ]
                  this.time1 = uuidv4()
             }
             // 如果当前是横向柱状图
             else if(this.chooseGroup == 1){
                  this.ruleMatchOption2.yAxis.data = []
                  this.ruleMatchOption2.series[0].data = []
                  for(var i=0;i<res.data.data.length;i++){
                      this.ruleMatchOption2.yAxis.data.push(res.data.data[i].ruleName)
                      this.ruleMatchOption2.series[0].data.push(res.data.data[i].addressList.length)
                  }
                  this.ruleMatchOption2.series[0].markPoint.data = [
                      {name: '年最高', value: res.data.data[0].addressList.length, yAxis: 0, xAxis: res.data.data[0].addressList.length},
                      {name: '年最低', value: res.data.data[res.data.data.length-1].addressList.length, yAxis: res.data.data.length-1, xAxis: res.data.data[res.data.data.length-1].addressList.length}
                  ]
                  this.time2 = uuidv4()
             }
             // 如果当前是其余图
             else{
                  this.ruleMatchOption3.series[0].data = []
                  for(var i=0;i<res.data.data.length;i++){
                      this.ruleMatchOption3.series[0].data.push({
                        value: res.data.data[i].addressList.length, name: res.data.data[i].ruleName
                      })
                  }
                  this.time3 = uuidv4()
             }
         })
      },
      // 导航栏切换图形
      handleClick(tab, event){
          this.chooseGroup = tab.name
          this.paintGraphChart()
      },
      // 重新切换查询TOP N
      changeTopN(){
          this.N = this.curTopNum
          this.paintGraphChart()
      },
      pageClick(item){
        this.currentPage = item
        this.paintTable(this.currentPage,this.pageSize)
      },
      handleSizeChange(item){
        this.pageSize = item
        this.paintTable(this.currentPage,this.pageSize)
      },
      // 禁用/启动 配置
      enabledChange(item){
        domainNameControl({ruleId:item.itemId,enabled:item.isOpen}).then(res=>{
           if(res.data.code == "200"){
                const h = this.$createElement;
                this.$message({
                  message: h('p', null, [
                    h('span', null, '操作结果：'),
                    h('i', { style: 'color: green' }, res.data.msg)
                  ])
                });
           }
        })
      },

  },
  components: {
     SideBar,
     MyCharts
  },
  mounted() {
      this.paintTable(this.currentPage,this.pageSize)
      this.paintGraphChart()
  }
}
</script>

<style scoped>
.analysis-tbl .top-tab{
   display: flex;
   justify-content: space-between;
   background: rgb(245, 240, 240);
}

.analysis-tbl .top-tab span{
   line-height: 40px;
   font-size: 20px;
   margin-left: 20px;
}

.form-notice{
    margin-top: 70px;
    background: #DAF0FB;
    font-size: 16px;
    padding: 10px;
    line-height: 30px;
    font-weight: 600;
}

.tbl-pageinate >>> .el-pagination.is-background .btn-next{
    width: 40px;
    height: 40px;
}
.tbl-pageinate >>> .el-pagination.is-background .btn-prev{
    width: 40px;
    height: 40px;
}
.tbl-pageinate >>> .el-pagination.is-background .el-pager li{
    font-size: 15px;
    color: #5d5656;
    width: 40px;
    height: 40px;
    line-height: 40px;
    padding: 0px 12px;
}
.tbl-pageinate >>> .el-pagination.is-background .el-pager li:not(.disabled).active{
    background-color: #f3bcbc;
}
.tbl-pageinate >>> .el-input--mini .el-input__inner{
    height: 40px;
}
.changeTabs >>> .el-tabs__nav-scroll{
    display: flex;
    justify-content: center;
}
.changeTabs >>> .el-tabs__item{
    font-size: 17px;
}
.top-graph .graph-header{
    display: flex;
    align-items: center;
}
.top-graph .graph-header h4{
    font-size: 22px;
    color: #000;
    font-weight: 400;
    border-left: 5px solid #408ed6;
    line-height: 22px;
    padding-left: 10px;
}
.top-graph .graph-header .graph-header-input{
    margin: 10px 20px;
    width: 200px;
}
</style>
