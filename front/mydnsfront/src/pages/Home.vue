<template>
   <div id="home">
     <side-bar>
        <template v-slot:pageContent>
            
        </template>
     </side-bar>
   </div>
</template>

<script>
import SideBar from '../components/SideBar.vue'

export default {
  name:'Home',
  data(){
   return {
   }
  },
  methods: {
  },
  components: {
    SideBar
  }
}
</script>

<style scoped>
</style>
