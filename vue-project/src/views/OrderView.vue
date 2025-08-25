<template>
  <div class="order-page">
    <!-- 订单状态筛选 -->
    <div class="order-tabs">
      <div v-for="(tab, index) in tabs" :key="index" class="tab-item" :class="{ active: activeTab === tab.id }"
        @click="setActiveTab(tab.id)">
        {{ tab.name }}
        <span v-if="orderCount(tab.id) > 0" class="tab-badge">
          {{ orderCount(tab.id) }}
        </span>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div v-for="(order, index) in filteredOrders" :key="index" class="order-item">
        <div class="order-header">
          <div class="order-info">
            <span class="order-id">订单号：{{ order.id }}</span>
            <span class="order-date">创建时间: {{  formatDate(order.createTime) }}</span>
          </div>
          <span class="order-status" :class="order.status">
            {{ order.status }}
          </span>
        </div>

        <div class="order-content" @click="viewOrderDetail(order.id)">
          <div class="event-image">
            <img :src="order.url" alt="活动封面" />
          </div>
          <div class="event-info">
            <h3 class="event-title">{{ order.Title }}</h3>
            <p class="event-time">
              <van-icon name="clock-o" /> {{ formatDate(order.startTime) }}
            </p>
            <p class="event-location">
              <van-icon name="location-o" /> {{ order.venue }}
            </p>
            <p class="order-price">¥{{ order.price }} × {{ order.num }}</p>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            合计：<strong>¥{{ (order.price * order.num).toFixed(2) }}</strong>
          </div>
          <div class="order-actions">
            <van-button v-if="order.status === 'pending'" size="small" plain @click.stop="cancelOrder(order.id)">
              取消
            </van-button>
            <van-button v-if="order.status === 'pending'" type="danger" size="small" @click.stop="payOrder(order.id)">
              去支付
            </van-button>
            <van-button v-if="order.status === 'completed'" type="primary" size="small"
              @click.stop="viewTicket(order.id)">
              查看电子票
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredOrders.length === 0" class="empty-state">
      <van-empty image="order" :description="emptyDescription" />
    </div>

    <!-- 底部导航 -->
    <footer class="app-footer">
      <div class="footer-container">
        <div v-for="(item, index) in footerItems" :key="index" class="footer-item"
          :class="{ active: activeFooterItem === item.id }" @click="setActiveFooterItem(item.id)">
          <div class="icon">{{ item.icon }}</div>
          <span>{{ item.name }}</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchOrder } from '../../public/util/order/fetchOrder'
import { useLoginStore } from '@/stores/login'


const loginStore = useLoginStore()
const router = useRouter()
const activeTab = ref('all')
const activeFooterItem = ref('order')

// 底部导航
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
const orders = ref([])

onMounted(async () => {
  if (!loginStore.isLogin) {
    router.push('/login')
    return
  }
  try {
    const { data } = await fetchOrder()
    if (data.code === 200) {
      orders.value = data.data
      console.log(orders.value);

    }
  } catch (error) {
    console.error('获取订单失败:', error)
  }
})

// 过滤订单
const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(order => order.status === activeTab.value)
})

// 切换标签
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

// 查看电子票
const viewTicket = (orderId) => {
  console.log('查看电子票:', orderId)
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
// 切换底部导航
const setActiveFooterItem = (itemId) => {
  activeFooterItem.value = itemId
  switch (itemId) {
    case 'home': router.push('/'); break
    case 'ticket': router.push('/ticket'); break
    case 'order': router.push('/order'); break
    case 'profile': router.push('/profile'); break
  }
}


const orderCount = (tabId) => {
  if (tabId === 'all') return orders.value.length
  return orders.value.filter(o => o.status === tabId).length
}

const emptyDescription = computed(() => {
  const map = {
    all: '暂无订单记录',
    pending: '暂无待支付订单',
    paid: '暂无待观看订单',
    completed: '暂无已完成订单',
    cancelled: '暂无已取消订单'
  }
  return map[activeTab.value] || '暂无订单'
})
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding-bottom: 60px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 标签页优化 */
.order-tabs {
  display: flex;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
  overflow-x: auto;
  white-space: nowrap;
  padding: 0 10px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 16px;
  font-size: 15px;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  min-width: 70px;
}

.tab-item.active {
  color: #ff6a00;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background-color: #ff6a00;
  border-radius: 2px;
}

.tab-badge {
  position: absolute;
  top: 6px;
  right: 8px;
  background-color: #ff6a00;
  color: white;
  font-size: 10px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 订单列表 */
.orders-list {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.order-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

/* 订单头部 */
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fcfcfc;
  font-size: 13px;
  color: #555;
}

.order-info {
  display: flex;
  flex-direction: column;
  flex: 1;
  margin-right: 10px;
}

.order-id {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.order-date {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  background-color: #f5f5f5;
  padding: 1px 6px;
  border-radius: 4px;
  width: fit-content;
  align-self: flex-start;
}

.order-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
  white-space: nowrap;
  min-width: 50px;
  text-align: center;
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
  background-color: #f5f5f5;
  color: #999;
}

/* 订单内容 */
.order-content {
  display: flex;
  padding: 16px;
  cursor: pointer;
  gap: 12px;
}

.event-image {
  width: 90px;
  height: 110px;
  flex-shrink: 0;
}

.event-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.event-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 110px;
}

.event-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.event-time,
.event-location,
.order-price {
  margin: 4px 0;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background-color: #fafafa;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
}

.order-total {
  color: #ff6a00;
  font-size: 14px;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.order-actions .van-button {
  height: 30px;
  padding: 0 12px;
  font-size: 13px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

/* 底部导航 */
.app-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #ffffff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  border-top: 1px solid #f0f0f0;
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
  transition: color 0.2s;
}

.footer-item.active {
  color: #ff6a00;
}

.footer-item .icon {
  font-size: 18px;
  margin-bottom: 4px;
}
</style>