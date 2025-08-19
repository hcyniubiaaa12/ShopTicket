<template>
  <div class="profile-page">
    <!-- 用户信息 -->
    <div class="user-header">
      <div class="user-avatar">
        <van-icon name="user-o" size="40" />
      </div>
      <div class="user-info">
        <h3 class="user-name">用户名</h3>
        <p class="user-id">ID: 123456789</p>
      </div>
      <div class="user-actions">
        <van-button size="small" @click="editProfile">编辑资料</van-button>
      </div>
    </div>

    <!-- 用户统计数据 -->
    <div class="user-stats">
      <div class="stat-item" v-for="(stat, index) in stats" :key="index">
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
      </van-cell-group>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

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
  { value: 8, label: '待支付' },
  { value: 3, label: '待观看' },
  { value: 12, label: '已完成' },
  { value: 2, label: '优惠券' }
])

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
      router.push('/profile')
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