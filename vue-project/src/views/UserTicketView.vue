<template>
  <div class="user-ticket-page">
    <div class="header">
      <h1>我的票夹</h1>
      <p class="subtitle">查看和管理您的演出票</p>
    </div>

    <div class="ticket-list" v-if="tickets.length > 0">
      <div class="ticket-card" v-for="(ticket, index) in tickets" :key="index">
        <div class="ticket-header">
          <div class="event-image">
            <img :src="ticket.url" :alt="ticket.title">
          </div>
          <div class="event-info">
            <h2 class="event-title">{{ ticket.title + '-' + ticket.city + '站' }}</h2>
            <p class="event-time"> <van-icon name="clock-o" /> {{ formatDate(ticket.startTime) }}</p>
            <p class="event-venue"> <van-icon name="location-o" /> {{ ticket.venue }}</p>
            <p class="event-ticketType">  {{ ticket.ticketType }}</p>
          </div>
        </div>

        <div class="ticket-details">
          <div class="ticket-row">
            <span class="label">演出时长:</span>
            <span class="value">{{ ticket.duration }} 分钟</span>
          </div>

          <div class="ticket-row">
            <span class="label">票价:</span>
            <span class="value price">¥{{ ticket.price }}</span>
          </div>
          <div class="ticket-row">
            <span class="label">观演人:</span>
            <span class="value">{{ ticket.username }}</span>
          </div>
           <div class="ticket-row">
            <span class="label">订单创建时间:</span>
            <span class="value">{{ formatDate(ticket.createTime) }}</span>
          </div>
        
        </div>

        <div class="ticket-footer">
          <div class="status" :class="ticket.statusClass">{{ ticket.status }}</div>
          <div class="actions">
            <button class="action-btn" @click="use(ticket)"  v-if="ticket.status === '待观看'">去使用</button>
            <button class="action-btn primary" v-if="ticket.status === '已观看'"
              @click="comment(ticket)">去评论</button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">🎫</div>
      <h3>暂无演出票</h3>
      <p>您还没有购买任何演出票</p>
      <button class="go-home-btn" @click="goToHome">去首页看看</button>
    </div>

    <!-- 底部导航 -->
    <footer class="app-footer">
      <div class="footer-container">
        <div class="footer-item" v-for="(item, index) in footerItems" :key="index"
          :class="{ active: activeFooterItem === item.id }" @click="setActiveFooterItem(item.id)">
          <div class="icon">{{ item.icon }}</div>
          <span>{{ item.name }}</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {useTicket} from '../../public/util/userticket/useTicket'
import { fetchUserTicket } from '../../public/util/userticket/fetchUserTicket'

import { showNotify } from 'vant'
;
const router = useRouter()

// 模拟票务数据
const tickets = ref([])

const activeFooterItem = ref('ticket')

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '演出', icon: '❤️' },
  { id: 'userticket', name: '票夹', icon: '🎟️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])
onMounted(async () => {
  let { data } = await fetchUserTicket()
  if (data.code === 200) {
    tickets.value = data.data

    

  }

})

// 设置底部导航
const setActiveFooterItem = (itemId) => {
  activeFooterItem.value = itemId
  switch (itemId) {
    case 'home':
      router.push('/')
      break
    case 'ticket':
      router.push('/ticket')
      break
    case 'userticket':
      router.push('/userticket')
      break
    case 'order':
      router.push('/order')
      break
    case 'profile':
      router.push('/profile')
      break
  }
}

// 查看票务详情
const use = async(ticket) => {
   let {data} = await useTicket(ticket);
   console.log(data);
   
  if(data.code === 200){
    showNotify({ type: 'success', message: data.data })
    // 更新票券列表以反映状态变化
    let res = await fetchUserTicket()
    if (res.data.code === 200) {
      tickets.value = res.data.data
    }
  }else{
    showNotify({ type: 'danger', message: data.data })
  }
  console.log('查看票务详情', ticket)
  // 实际项目中可以跳转到票务详情页
}

// 兑换票
const comment = async (ticket) => {
 
  console.log( ticket)
  // 实际项目中可以实现兑换逻辑
}

// 跳转到首页
const goToHome = () => {
  router.push('/')
}
const formatDate = (time) => {
  // 转换为 Date 对象
  const date = typeof time === 'string' ? new Date(time) : time

  // 验证是否为有效时间
  if (!time || isNaN(date.getTime())) {
    return '---'
  }

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0') // 月份从0开始
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')

  // 返回格式：2025-04-05 14:30
  return `${year}-${month}-${day} ${hours}:${minutes}`
}
</script>

<style scoped>
.user-ticket-page {
  padding-bottom: 60px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #ff6a00, #ff9a4a);
  color: white;
  padding: 20px 16px;
  border-radius: 0 0 20px 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header h1 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: bold;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.ticket-list {
  padding: 16px;
}

.ticket-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 16px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.ticket-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.ticket-header {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.event-image {
  width: 80px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.event-info {
  flex: 1;
  padding-left: 12px;
}

.event-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  line-height: 1.3;
}

.event-time,
.event-venue {
  margin: 6px 0;
  font-size: 13px;
  color: #666;
}

.ticket-details {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.ticket-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.ticket-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #999;
  font-size: 13px;
}

.value {
  color: #333;
  font-size: 13px;
  text-align: right;
}

.event-ticketType {
  font-size: 13px;
  color: #ff6a00;
  font-weight: 500;
  margin: 6px 0;
  padding: 2px 8px;
  background-color: #fff3e0;
  border-radius: 4px;
  display: inline-block;
}

.value.price {
  color: #ff6a00;
  font-weight: bold;
  font-size: 15px;
}

.ticket-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
}

.status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 16px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
  display: inline-block;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #e0e0e0;
}

.status.pending,
.status.completed,
.status.used {
  background: linear-gradient(135deg, #ff6a00, #ff9a4a);
  color: white;
  border: none;
  box-shadow: 0 2px 8px rgba(255, 106, 0, 0.2);
}

.actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.primary {
  background: linear-gradient(135deg, #ff6a00, #ff9a4a);
  color: white;
  border: none;
}

.action-btn.primary:hover {
  opacity: 0.9;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  margin: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.empty-state p {
  margin: 0 0 20px 0;
  color: #999;
  font-size: 14px;
}

.go-home-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #ff6a00, #ff9a4a);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.go-home-btn:hover {
  opacity: 0.9;
}

/* 底部导航栏 */
.app-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.footer-container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-item {
  flex: 1;
  text-align: center;
  padding: 8px 0;
  cursor: pointer;
  font-size: 12px;
  color: #666;
}

.footer-item.active {
  color: #ff6a00;
}

.footer-item .icon {
  font-size: 18px;
  margin-bottom: 4px;
}
</style>