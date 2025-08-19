<template>
  <div class="order-page">
    <!-- 订单状态筛选 -->
    <div class="order-tabs">
      <div 
        class="tab-item" 
        v-for="(tab, index) in tabs" 
        :key="index"
        :class="{ active: activeTab === tab.id }"
        @click="setActiveTab(tab.id)"
      >
        {{ tab.name }}
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div 
        class="order-item" 
        v-for="(order, index) in filteredOrders" 
        :key="index"
      >
        <div class="order-header">
          <span class="order-id">订单号: {{ order.id }}</span>
          <span class="order-status" :class="order.status">{{ order.statusText }}</span>
        </div>
        <div class="order-content" @click="viewOrderDetail(order.id)">
          <div class="event-image">
            <img :src="order.eventImage" :alt="order.eventTitle">
          </div>
          <div class="event-info">
            <h3 class="event-title">{{ order.eventTitle }}</h3>
            <p class="event-time">{{ order.eventTime }}</p>
            <p class="event-location">{{ order.eventLocation }}</p>
            <p class="order-price">¥{{ order.price }} x {{ order.quantity }}</p>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">合计: ¥{{ order.total }}</span>
          <div class="order-actions">
            <van-button 
              v-if="order.status === 'pending'" 
              size="small" 
              @click="cancelOrder(order.id)"
            >
              取消订单
            </van-button>
            <van-button 
              v-if="order.status === 'pending'" 
              type="danger" 
              size="small" 
              @click="payOrder(order.id)"
            >
              立即支付
            </van-button>
            <van-button 
              v-if="order.status === 'completed'" 
              size="small" 
              @click="viewTicket(order.id)"
            >
              查看票夹
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="filteredOrders.length === 0">
      <van-icon name="orders-o" size="60" />
      <p>暂无相关订单</p>
    </div>

    <!-- 底部导航 -->
    <footer class="app-footer">
      <div class="footer-container">
        <div 
          class="footer-item" 
          v-for="(item, index) in footerItems" 
          :key="index"
          :class="{ active: activeFooterItem === item.id }"
          @click="setActiveFooterItem(item.id)"
        >
          <div class="icon">{{ item.icon }}</div>
          <span>{{ item.name }}</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('all')
const activeFooterItem = ref('order')

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '想看', icon: '❤️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

// 订单状态标签
const tabs = ref([
  { id: 'all', name: '全部' },
  { id: 'pending', name: '待支付' },
  { id: 'paid', name: '待观看' },
  { id: 'completed', name: '已完成' },
  { id: 'cancelled', name: '已取消' }
])

// 订单数据
const orders = ref([
  {
    id: 'D20230915001',
    status: 'pending',
    statusText: '待支付',
    eventTitle: '周杰伦2023嘉年华世界巡回演唱会',
    eventTime: '2023.10.15 19:30',
    eventLocation: '北京鸟巢体育馆',
    price: '580',
    quantity: 2,
    total: '1160',
    eventImage: 'https://dummyimage.com/80x100/ff6a00/ffffff&text=周杰伦'
  },
  {
    id: 'D20230910002',
    status: 'paid',
    statusText: '待观看',
    eventTitle: '五月天2023好好好想见到你演唱会',
    eventTime: '2023.11.20 19:00',
    eventLocation: '上海梅赛德斯奔驰文化中心',
    price: '480',
    quantity: 1,
    total: '480',
    eventImage: 'https://dummyimage.com/80x100/ff9966/ffffff&text=五月天'
  },
  {
    id: 'D20230820003',
    status: 'completed',
    statusText: '已完成',
    eventTitle: '音乐剧《猫》',
    eventTime: '2023.09.30 19:30',
    eventLocation: '广州大剧院',
    price: '380',
    quantity: 2,
    total: '760',
    eventImage: 'https://dummyimage.com/80x100/66ccff/ffffff&text=音乐剧'
  },
  {
    id: 'D20230901004',
    status: 'cancelled',
    statusText: '已取消',
    eventTitle: '德云社相声专场',
    eventTime: '2023.10.05 19:30',
    eventLocation: '天津大礼堂',
    price: '180',
    quantity: 3,
    total: '540',
    eventImage: 'https://dummyimage.com/80x100/66ff99/ffffff&text=相声'
  }
])

// 根据状态筛选订单
const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeTab.value)
})

// 设置活跃标签
const setActiveTab = (tabId) => {
  activeTab.value = tabId
}

// 查看订单详情
const viewOrderDetail = (orderId) => {
  console.log('查看订单详情:', orderId)
}

// 取消订单
const cancelOrder = (orderId) => {
  console.log('取消订单:', orderId)
}

// 支付订单
const payOrder = (orderId) => {
  console.log('支付订单:', orderId)
}

// 查看票夹
const viewTicket = (orderId) => {
  console.log('查看票夹:', orderId)
}

const setActiveFooterItem = (itemId) => {
  activeFooterItem.value = itemId
  switch (itemId) {
    case 'home':
      router.push('/')
      break
    case 'ticket':
      router.push('/ticket')
      break
    case 'order':
      router.push('/order')
      break
    case 'profile':
      router.push('/profile')
      break
  }
}
</script>

<style scoped>
.order-page {
  padding-bottom: 60px;
  background-color: #f5f5f5;
}

/* 订单状态筛选 */
.order-tabs {
  display: flex;
  background-color: #fff;
  padding: 10px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 8px 0;
  font-size: 14px;
  cursor: pointer;
  color: #666;
}

.tab-item.active {
  color: #ff6a00;
  font-weight: bold;
  border-bottom: 2px solid #ff6a00;
}

/* 订单列表 */
.orders-list {
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-item {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-size: 12px;
  color: #999;
}

.order-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.order-status.pending {
  background-color: #fff0e8;
  color: #ff6a00;
}

.order-status.paid {
  background-color: #e8f5ff;
  color: #3399ff;
}

.order-status.completed {
  background-color: #e8fff0;
  color: #00cc66;
}

.order-status.cancelled {
  background-color: #f0f0f0;
  color: #999;
}

.order-content {
  display: flex;
  padding: 15px;
  cursor: pointer;
}

.event-image {
  width: 80px;
  height: 100px;
  margin-right: 10px;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.event-info {
  flex: 1;
}

.event-title {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.event-time,
.event-location,
.order-price {
  margin: 5px 0;
  font-size: 12px;
  color: #666;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-top: 1px solid #f0f0f0;
}

.order-total {
  font-weight: bold;
  color: #ff6a00;
}

.order-actions {
  display: flex;
  gap: 10px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 50px 0;
  color: #999;
}

.empty-state p {
  margin: 10px 0 0 0;
  font-size: 14px;
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