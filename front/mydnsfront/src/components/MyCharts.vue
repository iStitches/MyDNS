<template>
    <div :id="elId" class="container" :style="{'width':width,'height':height,'margin':'0 auto'}"></div>
</template>

<script>
import * as echarts from 'echarts'
import { GridComponent } from 'echarts/components'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/legend'
import {v4 as uuidv4} from 'uuid'

export default {
  name:'',
  data(){
   return {
       elId: ''
   }
  },
  props:{
      options:{
          type: Object,
          default(){
            return {}
          }
      },
      width: {
          type: String,
          default: '584px'
      },
      height: {
          type: String,
          default: '440px'
      },
      time: {
          type: String,
          default: ''
      }
  },
  methods: {
      repaint(){
            var chart = echarts.init(document.getElementById(this.elId))
            chart.clear()
            chart.setOption(this.options)
            // chart.resize()
      }
  },
  created() {
      this.elId = uuidv4()
  },
  mounted() {
      echarts.use([GridComponent]);
      console.log(this.options)
      this.repaint()
  },
  watch: {
      time(){
          this.repaint()
      }
  }
}
</script>

<style scoped>

</style>
