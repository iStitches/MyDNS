<template>
   <div id="topbar">
        <img src="../assets/img/bg.jpg" alt="">
        <el-dropdown id="userInfo">
            <span class="el-dropdown-link">
                {{username}}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>黄金糕</el-dropdown-item>
                <el-dropdown-item>狮子头</el-dropdown-item>
                <el-dropdown-item>螺蛳粉</el-dropdown-item>
                <el-dropdown-item disabled>双皮奶</el-dropdown-item>
                <el-dropdown-item divided>蚵仔煎</el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
   </div>
</template>

<script>
export default {
  name:'',
  data(){
   return {
      username: "用户名"
   }
  }
}
</script>

<style scoped>
#topbar{
    display: flex;
    justify-content: flex-end;
    border: 1px solid black;
    align-items: center;
    width: 100%;
    height: 69.6px;
    background: #063146;
}

#topbar img{
    border-radius: 50%;
    height: 50px;
    width: 50px;
    margin-right: 20px;
}

#topbar #userInfo{
    margin-right:50px;
    color: white;
    font-size: 15px;
}
</style>
