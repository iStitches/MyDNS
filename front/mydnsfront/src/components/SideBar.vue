<template>
    <div id="baseBar">
        <!-- 侧边导航条 -->
        <div id="side">
            <input type="checkbox" id="check">
            <label for="check">
                <div id="btn" @click="btnClick()">
                    <img src="../assets/img/home_btn.jpg" alt="">
                </div>
                <div id="cancel" @click="cacelClick()">
                    <img src="../assets/img/home_cancel.jpg" alt="">
                </div>
            </label>
            <div class="sidebar">
                    <header>My App</header>
                    <ul>
                        <li><a @click="jumpToAnalysis()">域名解析管理</a></li>
                        <li><a @click="jumpToMonitor()">DNS服务器监控</a></li>
                        <!-- <li><a @click="jumpToControl()">账号管理</a></li> -->
                        <li><a @click="jumpToGuide()">使用向导</a></li>
                    </ul>
            </div>
        </div>

        <!-- 页面具体内容 -->
        <div id="content" :style="{width:contentWidth}">
            <top-bar></top-bar>
            <slot name="pageContent"></slot>
        </div>
    </div>
</template>

<script>
import TopBar from '../components/TopBar.vue'

export default {
  name:'sideBar',
  data(){
   return {
     contentWidth: document.documentElement.clientWidth+'px'
   }
  },
  methods: {
    btnClick(){
         this.contentWidth = '1286px';
    },
    cacelClick(){
         this.contentWidth = document.documentElement.clientWidth+'px';
    },
    jumpToAnalysis(){
         this.$router.push({path:'/analysis'})
    },
    jumpToMonitor(){
         this.$router.push({path:'/'})
    },
    jumpToControl(){
         this.$router.push({path:'/control'})
    },
    jumpToGuide(){
         this.$router.push({path:'/guide'})
    }
  },
  components: {
    TopBar
  }
}
</script>

<style scoped>
@import "../assets/css/home_1.css";

#baseBar{
  display: flex;
  justify-content: space-between;
}
</style>
