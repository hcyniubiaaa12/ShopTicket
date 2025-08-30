<template>
  <div class="my-performances-page">
    <div class="header">
      <h1>我的演出</h1>
      <p class="subtitle">查看您购买过的演出</p>
    </div>

    <div class="performances-list" v-if="performances.length > 0">
      <div class="performance-card" v-for="(performance, index) in performances" :key="index" 
       >
        <div class="performance-header">
          <div class="performance-image">
            <img :src="performance.url" :alt="performance.title">
          </div>
          <div class="performance-info">
            <h2 class="performance-title">{{ performance.title + '-' + performance.city + '站' }}</h2>
          
            <p class="performance-venue"> <van-icon name="location-o" /> {{ performance.venue }}</p>
            </div>
        </div>

       

      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">🎭</div>
      <h3>暂无演出记录</h3>
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
import { fetchMyPerformance } from '../../public/util/performance/fetchMyPerformance'
import { useLoginStore } from '@/stores/login'

const loginStore = useLoginStore()
const router = useRouter()

// 用户购买的演出版
const performances = ref([])

const activeFooterItem = ref('profile')

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '演出', icon: '❤️' },
  { id: 'userticket', name: '票夹', icon: '🎟️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

onMounted(async () => {
  // 获取用户购买的演出票
  let { data } = await fetchMyPerformance()
  if (data.code === 200) {
    performances.value = data.data
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
      if (!loginStore.isLogin) {
        router.push('/login')
      } else {
        router.push('/profile')
      }
      break
  }
}

// 跳转到首页
const goToHome = () => {
  router.push('/')
}


</script>

<style scoped>
.my-performances-page {
  padding-bottom: 60px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #6a11cb, #2575fc);
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

/* 演出卡片 */
.performance-card {
  background: white;
  border-radius: 15px;
  margin: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
}

.performance-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.performance-header {
  display: flex;
  padding: 15px;
}

.performance-image {
  width: 90px;
  height: 110px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  margin-right: 15px;
}

.performance-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.performance-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.performance-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: bold;
  color: #333;
  line-height: 1.3;
}

.performance-time,
.performance-venue {
  margin: 0 0 5px 0;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
}

.performance-time .van-icon,
.performance-venue .van-icon {
  margin-right: 5px;
  font-size: 14px;
  color: #ff6a00;
}

.performance-actions {
  padding: 0 15px 15px;
  text-align: right;
}

.comment-button {
  padding: 8px 20px;
  background: linear-gradient(135deg, #ff6b00, #ff9d00);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(255, 106, 0, 0.3);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  min-width: 80px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.comment-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0));
  transform: translateX(-100%);
  transition: transform 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.comment-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 106, 0, 0.4);
}

.comment-button:hover::before {
  transform: translateX(100%);
}

.comment-button:active {
  transform: scale(0.98);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 15px;
  margin: 15px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
}

.empty-state p {
  margin: 0 0 20px 0;
  font-size: 14px;
  color: #666;
}

.go-home-btn {
  background: linear-gradient(to right, #ff6a00, #ff9a4a);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.go-home-btn:hover {
  opacity: 0.9;
  transform: scale(1.05);
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
  font-size: 12px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.footer-item:hover {
  color: #ff6a00;
}

.footer-item.active {
  color: #ff6a00;
  font-weight: bold;
}

.footer-item .icon {
  font-size: 18px;
  margin-bottom: 2px;
}
</style>