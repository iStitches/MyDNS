<template>
    <div>
        <my-charts :options="options"></my-charts>
    </div>
</template>

<script>
import MyCharts from '../components/MyCharts.vue'

export default {
  name:'',
  data(){
   return {
       options:{
    xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [120, 200, 150, 80, 70, 110, 130],
        type: 'bar'
    }]
       }
   }
  },
  components: {
      MyCharts
  }
}
</script>

<style scoped>

</style>
