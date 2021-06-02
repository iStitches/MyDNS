import Vue from 'vue'
import Router from 'vue-router'
import Test from '@/pages/Test'
import Monitor from '@/pages/Monitor'
import Analysis from '@/pages/Analysis'
import Control from '@/pages/Control'
import Guide from '@/pages/Guide'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/test',
      name: 'Test',
      component: Test
    },
    {
      path: '/',
      name: 'Monitor',
      component: Monitor
    },
    {
      path: '/analysis',
      name: 'Analysis',
      component: Analysis
    },
    {
      path: '/control',
      name: 'Control',
      component: Control
    },
    {
      path: '/guide',
      name: 'Guide',
      component: Guide
    }
    
  ]
})
