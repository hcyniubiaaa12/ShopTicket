import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TicketView from '../views/TicketView.vue'
import OrderView from '../views/OrderView.vue'
import ProfileView from '../views/ProfileView.vue'
import EventDetailView from '../views/EventDetailView.vue'
import LoginPage from '../views/LoginPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/ticket',
      name: 'ticket',
      component: TicketView,
    },
    {
      path: '/order',
      name: 'order',
      component: OrderView,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
    },
    {
      path: '/event/:id',
      name: 'eventDetail',
      component: EventDetailView,
      props: true
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage
    },
    {
    path:'/more',
    name:'more',
    component: () => import('../views/MoreView.vue')
    }
  ],
})

export default router