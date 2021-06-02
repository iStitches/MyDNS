<template>
   <div id="guide">
     <side-bar>
        <template v-slot:pageContent>
          <!-- 使用指导nslookup -->
            <el-row class="guide-step">
                  <el-card shadow="hover" class="cardItem"> 
                      <h2>本地测试工具 nslookup</h2>
                      <div class="block">
                        <el-timeline>
                            <el-timeline-item timestamp="步骤一" placement="top" :color="'#0bbd87'">
                              <el-card>
                                <h4>查看本机默认DNS服务器IP地址</h4>
                                <img src="../assets/img/guide1.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤二" placement="top" :color="'#F9B440'">
                              <el-card>
                                <h4>修改本机默认DNS服务器地址为当前主机的IP地址</h4>
                                <img src="../assets/img/guide2.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤三" placement="top" :color="'#D933A5'">
                              <el-card>
                                <h4>输入自定义域名配置进行测试</h4>
                                <img src="../assets/img/guide3.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤四" placement="top" :color="'#999999'">
                              <el-card>
                                <h4>输入网络随机域名进行测试(掘金)</h4>
                                <img src="../assets/img/guide4.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤五" placement="top" :color="'#57DEDC'">
                              <el-card>
                                <h4>输入网络随机域名进行测试(豆瓣)</h4>
                                <img src="../assets/img/guide5.png" alt="">
                              </el-card>
                            </el-timeline-item>
                          </el-timeline>
                      </div>
                  </el-card>
            </el-row>
            <!-- 实体机测试 -->
            <el-row class="guide-step">
                  <el-card shadow="hover" class="cardItem"> 
                      <h2>实体机测试</h2>
                      <div class="block">
                        <el-timeline>
                            <el-timeline-item timestamp="步骤一" placement="top" :color="'#0bbd87'">
                              <el-card>
                                <h4>修改本机真正的DNS服务器地址</h4>
                                <img src="../assets/img/guide_true1.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤二" placement="top" :color="'#F9B440'">
                              <el-card>
                                <h4>浏览器随机输入网址(测试1)</h4>
                                <img src="../assets/img/guide_true2.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤三" placement="top" :color="'#D933A5'">
                              <el-card>
                                <h4>浏览器随机输入网址(测试2)</h4>
                                <img src="../assets/img/guide_true3.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤四" placement="top" :color="'#999999'">
                              <el-card>
                                <h4>后端控制台打印输出</h4>
                                <img src="../assets/img/guide_true4.png" alt="">
                              </el-card>
                            </el-timeline-item>
                            <el-timeline-item timestamp="步骤五" placement="top" :color="'#57DEDC'">
                              <el-card>
                                <h4>DNS流量监控状态</h4>
                                <img src="../assets/img/guide_true5.png" alt="">
                              </el-card>
                            </el-timeline-item>
                          </el-timeline>
                      </div>
                  </el-card>
            </el-row>
        </template>
     </side-bar>
   </div>
</template>

<script>
import SideBar from '../components/SideBar.vue'

export default {
  name:'',
  data(){
   return {
      active: 1
   }
  },
  components: {
    SideBar
  }
}
</script>

<style scoped>
.cardItem{
    margin: 10px 100px;
    padding: 10px;
}
.guide-step >>> .el-timeline-item__timestamp{
    font-size: 18px;
}
.guide-step img{
    width: 800px;
    height: 400px;
}
.guide-step h4{
    color: #13ac9e;
    font-size: 16px;
}
</style>
