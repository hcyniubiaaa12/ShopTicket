<template>
  <div class="comment-page">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <van-nav-bar
        title="我的评价"
       
        left-arrow
        @click-left="onClickLeft"
      />
    </div>

    <!-- 评价状态筛选 -->
    <div class="comment-tabs">
      <div 
        v-for="(tab, index) in tabs" 
        :key="index" 
        class="tab-item" 
        :class="{ active: activeTab === tab.name }"
        @click="setActiveTab(tab.name)"
      >
        {{ tab.name }}
      </div>
    </div>

    <!-- 已评价列表 -->
    <div v-if="activeTab === '已评价'" class="comments-list">
      <div v-if="commentedOrders.length > 0">
        <div 
          v-for="(order, index) in commentedOrders" 
          :key="index" 
          class="comment-item"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-id">订单号：{{ order.id }}</span>
            </div>
          </div>

          <div class="order-content">
            <div class="event-image">
              <img :src="order.url" alt="活动封面" />
            </div>
            <div class="event-info">
              <h3 class="event-title">{{ order.title }} - {{ order.city }}站</h3>
              <p class="event-time">
                <van-icon name="clock-o" /> {{ formatDate(order.startTime) }}
              </p>
              <p class="event-location">
                <van-icon name="location-o" /> {{ order.venue }}
              </p>
            </div>
          </div>

          <div class="comment-content">
            <div class="comment-rating">
              <van-rate v-model="order.rating" readonly />
            </div>
            <div class="comment-text">{{ order.comment }}</div>
            <div class="comment-date">{{ formatDate(order.commentTime) }}</div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <van-empty description="暂无已评价订单" />
      </div>
    </div>

    <!-- 未评价列表 -->
    <div v-if="activeTab === '未评价'" class="comments-list">
      <div v-if="uncommentedOrders.length > 0">
        <div 
          v-for="(order, index) in uncommentedOrders" 
          :key="index" 
          class="comment-item"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-id">订单号：{{ order.id }}</span>
            </div>
          </div>

          <div class="order-content">
            <div class="event-image">
              <img :src="order.url" alt="活动封面" />
            </div>
            <div class="event-info">
              <h3 class="event-title">{{ order.title }} - {{ order.city }}站</h3>
              <p class="event-time">
                <van-icon name="clock-o" /> {{ formatDate(order.startTime) }}
              </p>
              <p class="event-location">
                <van-icon name="location-o" /> {{ order.venue }}
              </p>
            </div>
          </div>

          <div class="comment-actions">
            <van-button 
              type="primary" 
              size="small" 
              @click="goToComment(order)"
            >
              去评价
            </van-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <van-empty description="暂无未评价订单" />
      </div>
    </div>

    <!-- 底部导航 -->
    <footer class="app-footer">
      <div class="footer-container">
        <div 
          v-for="(item, index) in footerItems" 
          :key="index"
          class="footer-item"
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginStore } from '@/stores/login'
import { fetchOrder } from '../../public/util/order/fetchOrder'

const loginStore = useLoginStore()
const router = useRouter()
const activeTab = ref('未评价')
const activeFooterItem = ref('profile')

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '想看', icon: '❤️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

// 评价状态标签
const tabs = ref([
  { name: '未评价' },
  { name: '已评价' }
])

// 订单数据
const orders = ref([])

// 已评价订单
const commentedOrders = ref([
  {
    id: 'P20240515001',
    url: 'https://picsum.photos/100/100?random=1',
    title: '周杰伦2024嘉年华世界巡回演唱会',
    city: '北京',
    venue: '国家体育场',
    startTime: '2024-05-15 19:30',
    rating: 5,
    comment: '非常棒的演唱会！音响效果和舞台设计都超出了我的预期。',
    commentTime: '2024-05-16 10:30'
  }
])

// 未评价订单
const uncommentedOrders = ref([
  {
    id: 'P20240422002',
    url: 'https://picsum.photos/100/100?random=2',
    title: '五月天2024好好好想见到你演唱会',
    city: '上海',
    venue: '梅赛德斯奔驰文化中心',
    startTime: '2024-04-22 19:00'
  },
  {
    id: 'P20240310003',
    url: 'https://picsum.photos/100/100?random=3',
    title: '林俊杰2024JJ20世界巡回演唱会',
    city: '广州',
    venue: '广州体育馆',
    startTime: '2024-03-10 20:00'
  }
])

onMounted(async () => {
  if (!loginStore.isLogin) {
    router.push('/login')
    return
  }
  
  try {
    const { data } = await fetchOrder()
    if (data.code === 200) {
      orders.value = data.data
      console.log('订单数据:', orders.value)
    }
  } catch (error) {
    console.error('获取订单失败:', error)
  }
})

// 切换标签
const setActiveTab = (tabName) => {
  activeTab.value = tabName
}

// 返回上一页
const onClickLeft = () => {
  router.go(-1)
}

// 去评价
const goToComment = (order) => {
  console.log('去评价订单:', order)
  // 这里可以跳转到具体的评价页面
  // 暂时用alert代替
  alert(`去评价订单: ${order.title}`)
}

// 格式化日期
const formatDate = (time) => {
  if (!time) return '---'
  
  // 如果是日期字符串，直接返回
  if (typeof time === 'string') {
    // 简单处理日期格式
    if (time.includes('-') && time.includes(':')) {
      return time
    }
    // 只有日期的情况
    if (time.includes('-')) {
      return time
    }
  }
  
  // 如果是Date对象
  const date = new Date(time)
  if (isNaN(date.getTime())) return '---'
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 切换底部导航
const setActiveFooterItem = (itemId) => {
  activeFooterItem.value = itemId
  switch (itemId) {
    case 'home': router.push('/'); break
    case 'ticket': router.push('/ticket'); break
    case 'order': router.push('/order'); break
    case 'profile': 
      if (!loginStore.isLogin) {
        router.push('/login')
      } else {
        router.push('/profile')
      }
      break
  }
}
</script>

<style scoped>
.comment-page {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding-bottom: 60px;
}

.top-nav {
  position: sticky;
  top: 0;
  z-index: 100;
}

.comment-tabs {
  display: flex;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 15px 0;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.tab-item.active {
  color: #ff6a00;
  border-bottom: 2px solid #ff6a00;
}

.comments-list {
  padding: 10px;
}

.comment-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-size: 12px;
  color: #999;
}

.order-content {
  display: flex;
  margin-bottom: 15px;
}

.event-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 10px;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.event-info {
  flex: 1;
}

.event-title {
  margin: 0 0 5px 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.event-time,
.event-location {
  margin: 5px 0;
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
}

.event-time .van-icon,
.event-location .van-icon {
  margin-right: 3px;
}

.comment-content {
  background-color: #f8f9fa;
  border-radius: 4px;
  padding: 10px;
  margin-top: 10px;
}

.comment-rating {
  margin-bottom: 5px;
}

.comment-text {
  font-size: 13px;
  color: #333;
  line-height: 1.4;
  margin-bottom: 5px;
}

.comment-date {
  font-size: 12px;
  color: #999;
  text-align: right;
}

.comment-actions {
  text-align: right;
}

.empty-state {
  padding: 50px 0;
  background-color: #fff;
  border-radius: 8px;
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