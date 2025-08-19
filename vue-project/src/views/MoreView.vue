<template>
  <div class="more-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left" @click="goBack">
        <van-icon name="arrow-left" size="20" />
      </div>
      <h2>全部演出</h2>
    </div>

    <!-- 演出列表 -->
    <div class="events-list">
      <div class="event-card" v-for="(event, index) in events" :key="index" @click="goToEventDetail(event.eventId)">
        <div class="event-image">
          <img :src="event.url" />
        </div>
        <div class="event-info">
          <h3 class="event-title">{{ event.title + '-' + event.cityName + '站' }}</h3>
          <p class="event-time">
            {{
              event.totalPerformances > 1 ? event.firstShow + ' - ' + event.lastShow : event.firstShow
            }}
          </p>
          <p class="event-location">{{ event.venueName }}</p>
          <p class="event-price">¥{{ event.minPrice || '待定' }} 起</p>
        </div>
      </div>
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
import { fetchAllPerformance } from '../../public/util/performance/fetchPerformance'

const router = useRouter()

const activeFooterItem = ref('home')
const events = ref([])

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '想看', icon: '❤️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

// 获取所有演出数据
const fetchEvents = async () => {
  try {
    const res = await fetchAllPerformance()
    if (res.data.code === 200) {
      events.value = res.data.data
    } else {
      console.warn('获取演出数据失败:', res.data.message)
    }
  } catch (error) {
    console.error('获取演出数据异常:', error)
  }
}

// 跳转到演出详情
const goToEventDetail = (eventId) => {
  router.push(`/event/${eventId}`)
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

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
    case 'order':
      router.push('/order')
      break
    case 'profile':
      router.push('/profile')
      break
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchEvents()
})
</script>

<style scoped>
.more-page {
  padding-bottom: 60px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  background-color: #fff;
  padding: 15px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
}

.header-left {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin-right: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 18px;
  flex: 1;
}

/* 演出列表 */
.events-list {
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.event-card {
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.event-image {
  width: 120px;
  height: 150px;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  display: block;
}

.event-info {
  flex: 1;
  padding: 8px;
  display: flex;
  flex-direction: column;
}

.event-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.event-time,
.event-location {
  margin: 4px 0;
  font-size: 12px;
  color: #666;
}

.event-price {
  color: #ff6a00;
  font-weight: bold;
  font-size: 15px;
  margin-top: auto;
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