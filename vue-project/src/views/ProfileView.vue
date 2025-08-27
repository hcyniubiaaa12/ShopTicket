<template>
  <div class="profile-page">
    <!-- 用户信息 -->
    <div class="user-header">
      <div class="user-avatar">
        <img :src=loginStore.avatar >
      
      </div>
      <div class="user-info" >
        <h3 class="user-name">用户:{{ loginStore.username }}</h3>
       
      </div>
      <div class="user-actions">
        <van-button size="small" @click="editProfile">编辑资料</van-button>
      </div>
    </div>

    <!-- 用户统计数据 -->
    <div class="user-stats">
      <div class="stat-item" v-for="(stat, index) in stats" :key="index" @click="goToOrder(stat.status)">
        <p class="stat-value">{{ stat.value }}</p>
        <p class="stat-label">{{ stat.label }}</p>
      </div>
    </div>


    <!-- 功能菜单 -->
    <div class="menu-section">
      <van-cell-group>
        <van-cell title="我的想看" is-link to="/ticket">
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
        <van-cell title="我的订单" is-link to="/order">
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
        <van-cell title="观演人信息" is-link>
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
        <van-cell title="地址管理" is-link>
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <div class="menu-section">
      <van-cell-group>
        <van-cell title="优惠券" is-link>
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
        <van-cell title="积分商城" is-link>
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <div class="menu-section">
      <van-cell-group>
        <van-cell title="帮助与客服" is-link>
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
        <van-cell title="设置" is-link>
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
        <van-cell title="退出" class="logout-button" is-link @click="logout">
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-cell>
      </van-cell-group>
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
import { useLoginStore } from '@/stores/login'
import { fetchOrder } from '../../public/util/order/fetchOrder'
import { fetchComment } from '../../public/util/performance/fetchComment'

const loginStore = useLoginStore()
const router = useRouter()
const activeFooterItem = ref('profile')

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '想看', icon: '❤️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

// 用户统计数据
const stats = ref([
  { value: 0, label: '待支付', status: '待支付' },
  { value: 0, label: '待观看', status: '待观看' },
  { value: 0, label: '已完成', status: '已支付' },
  { value: 0, label: '已取消', status: '已取消' },
  { value: 0, label: '我的评价', status: 'comment' }
])

// 用户评价数据
const comments = ref([])

// 检查登录状态
onMounted(async () => {
  // 模拟检查登录状态的逻辑
  // 实际项目中可以从store、localStorage或API获取用户登录状态
  if (!loginStore.isLogin) {
    // 如果未登录，跳转到登录页面
    router.push('/login')
  } else {
    // 获取真实的订单统计数据
    await loadOrderStats()
    // 获取用户评价数据
    await loadComments()
  }
})

// 加载订单统计数据
const loadOrderStats = async () => {
  try {
    const { data } = await fetchOrder()
    if (data.code === 200) {
      const orders = data.data
      
      // 统计各类订单数量
      const pendingCount = orders.filter(order => order.status === '待支付').length
      const watchingCount = orders.filter(order => order.status === '待观看').length
      const completedCount = orders.filter(order => order.status === '已支付').length
      const cancelledCount = orders.filter(order => order.status === '已取消').length
      
      stats.value[0].value = pendingCount
      stats.value[1].value = watchingCount
      stats.value[2].value = completedCount
      stats.value[3].value = cancelledCount
      
      // 更新我的评价数量
      stats.value[4].value = comments.value.length
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

// 加载用户评价数据
const loadComments = async () => {
  try {
    const { data } = await fetchComment()
    if (data.code === 200) {
      comments.value = data.data.slice(0, 3) // 只显示前3条评论
      
      // 更新我的评价数量
      stats.value[4].value = comments.value.length
    }
  } catch (error) {
    console.error('获取评价数据失败:', error)
  }
}

// 编辑资料
const editProfile = () => {
  console.log('编辑资料')
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
      if (!loginStore.isLogin) {
        // 如果未登录，跳转到登录页面
        router.push('/login')
      } else {
        router.push('/profile')
      }

      break
  }
}
</script>

<style scoped>
.profile-page {
  padding-bottom: 60px;
  background-color: #f5f5f5;
}

/* 用户信息 */
.user-header {
  display: flex;
  align-items: center;
  padding: 20px 15px;
  background-color: #fff;
  margin-bottom: 10px;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.user-info {
  flex: 1;
}

.user-name {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.user-id {
  margin: 0;
  font-size: 12px;
  color: #999;
}

/* 用户统计数据 */
.user-stats {
  display: flex;
  background-color: #fff;
  padding: 15px 0;
  margin-bottom: 10px;
}

.stat-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
}

.stat-value {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: bold;
  color: #ff6a00;
}

.stat-label {
  margin: 0;
  font-size: 12px;
  color: #666;
}

/* 我的评价 */
.section-header {
  padding: 10px 15px;
  background-color: #fff;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.comments-list {
  background-color: #fff;
  padding: 0 15px 10px;
}

.comment-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.comment-performance {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.comment-rating {
  display: flex;
  align-items: center;
}

.comment-content {
  font-size: 13px;
  color: #666;
  margin-bottom: 5px;
  line-height: 1.4;
}

.comment-date {
  font-size: 12px;
  color: #999;
}

.no-comments {
  background-color: #fff;
  padding: 20px 0;
}

/* 菜单区域 */
.menu-section {
  margin-bottom: 10px;
  background-color: #fff;
}

:deep(.van-cell) {
  align-items: center;
}

:deep(.van-cell__title) {
  font-size: 14px;
}

:deep(.van-cell__right-icon) {
  color: #999;
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