<template>
  <div class="order-page">
    <!-- 订单状态筛选 -->
    <div class="order-tabs">
      <div v-for="(tab, index) in tabs" :key="index" class="tab-item" :class="{ active: activeTab === tab.name }"
        @click="setActiveTab(tab.name)">
        {{ tab.name }}
        <span v-if="orderCount(tab.name) > 0" class="tab-badge">
          {{ orderCount(tab.name) }}
        </span>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div v-for="(order, index) in filteredOrders" :key="index" class="order-item">
        <div class="order-header">
          <div class="order-info">
            <span class="order-id">订单号：{{ order.id }}</span>
            <span class="order-date">创建时间: {{ formatDate(order.createTime) }}</span>
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
            <h3 class="event-title">{{ order.title }} - {{ order.city }}站</h3>
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
            <van-button v-if="order.status === '待支付'" size="small" plain @click.stop="cancel(order.id)">
              取消
            </van-button>
            <van-button v-if="order.status === '待支付'" type="danger" size="small" @click.stop="pay(order.id)">
              去支付
            </van-button>
            <van-button v-if="order.status === '已支付'" type="primary" size="small" @click.stop="viewTicket(order.id)">
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

    <!-- 支付确认弹窗 -->
    <div v-if="showPaymentConfirm" class="payment-confirm-modal">
      <div class="payment-content">
        <!-- 图标 -->
        <div class="payment-icon">
          <van-icon name="bill-o" size="40" color="#FF69B4" />
        </div>

        <!-- 标题与描述 -->
        <h3 class="payment-title">确认支付</h3>
        <p class="payment-desc">
          您即将为以下订单完成支付，请确认信息无误。
        </p>

        <!-- 订单信息 -->
        <div class="order-summary">
          <div class="summary-row">
            <span>演出名称</span>
            <span class="value">{{ selectedOrder?.title }}</span>
          </div>
          <div class="summary-row">
            <span>场次时间</span>
            <span class="value">{{ formatDate(selectedOrder?.startTime) }}</span>
          </div>
          <div class="summary-row">
            <span>票价类型</span>
            <span class="value">{{ selectedOrder?.ticketType }}</span>
          </div>
          <div class="summary-row">
            <span>票数</span>
            <span class="value">{{ selectedOrder?.num }} 张</span>
          </div>
          <div class="summary-total">
            <strong>合计金额</strong>
            <strong class="amount">¥{{ (selectedOrder?.price * selectedOrder?.num).toFixed(2) }}</strong>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="payment-actions">
          <button class="btn-cancel" @click="cancelPayment">取消</button>
          <button class="btn-confirm" @click="confirmPayment">确认支付</button>
        </div>
      </div>
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
import { payOrder } from '../../public/util/order/payOrder'
import { cancelOrder } from '../../public/util/order/cancelOrder'


const loginStore = useLoginStore()
const router = useRouter()
const activeTab = ref('全部')
const activeFooterItem = ref('order')
const showPaymentConfirm = ref(false)
const selectedOrder = ref(null)

// 底部导航
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '想看', icon: '❤️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

// 订单状态标签
const tabs = ref([
  { name: '全部' },
  { name: '待支付' },
  { name: '待观看' },
  { name: '已支付' },
  { name: '已取消' }
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
      
      // 检查是否有从用户主页传入的状态参数
      const status = router.currentRoute.value.query.status
      if (status) {
        setActiveTab(status)
      }
    }
  } catch (error) {
    console.error('获取订单失败:', error)
  }
})

// 过滤订单
const filteredOrders = computed(() => {
  if (activeTab.value === '全部') return orders.value
  return orders.value.filter(order => order.status === activeTab.value)
})

// 切换标签
const setActiveTab = (tabName) => {
  activeTab.value = tabName
}

// 查看订单详情
const viewOrderDetail = (orderId) => {
  console.log('查看订单详情:', orderId)
}

// 取消订单
const cancel = async(orderId) => {
  let {data} = await cancelOrder(orderId);
  if(data.code ===200){
   let res = await fetchOrder();
   if(res.data.code ===200){
     orders.value = res.data.data
   }
    alert('取消成功')
  }
  console.log('取消订单:', orderId)
}

// 支付订单
const pay = async (orderId) => {
  // 查找要支付的订单
  const order = orders.value.find(o => o.id === orderId)
  if (order) {
    selectedOrder.value = order
    showPaymentConfirm.value = true
  }
}

// 确认支付
const confirmPayment = async () => {
  if (selectedOrder.value) {
    await payOrder(selectedOrder.value.id)
    console.log('支付订单:', selectedOrder.value.id)
    showPaymentConfirm.value = false
    selectedOrder.value = null
    
    // 重新加载订单数据以更新状态
    try {
      const { data } = await fetchOrder()
      if (data.code === 200) {
        orders.value = data.data
      }
    } catch (error) {
      console.error('获取订单失败:', error)
    }
  }
}

// 取消支付
const cancelPayment = () => {
  showPaymentConfirm.value = false
  selectedOrder.value = null
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


const orderCount = (tabName) => {
  if (tabName === '全部') return orders.value.length
  return orders.value.filter(o => o.status === tabName).length
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

/* ===== 支付确认弹窗样式 ===== */
.payment-confirm-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1100;
  display: flex;
  justify-content: center;
  align-items: center;
  animation: fadeIn 0.3s ease-out;
}

.payment-content {
  width: 90%;
  max-width: 380px;
  background: #fff;
  border-radius: 20px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  animation: scaleIn 0.3s ease-out;
}

.payment-icon {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 60px;
  background: #fff0f5;
  border-radius: 50%;
  margin-bottom: 16px;
}

.payment-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.payment-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

.order-summary {
  background-color: #f9f9fb;
  border-radius: 12px;
  padding: 16px;
  font-size: 14px;
  color: #555;
  text-align: left;
  margin-bottom: 24px;
  border: 1px solid #eee;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.summary-row .value {
  font-weight: 500;
  color: #333;
}

.summary-total {
  font-size: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #ddd;
  display: flex;
  justify-content: space-between;
  color: #e60000;
  font-weight: bold;
}

.payment-actions {
  display: flex;
  gap: 12px;
}

.btn-cancel {
  flex: 1;
  padding: 12px;
  background-color: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}

.btn-confirm {
  flex: 1;
  padding: 12px;
  background-color: #FF69B4;
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-confirm:hover {
  background-color: #e94e9c;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes scaleIn {
  from {
    transform: scale(0.9);
    opacity: 0;
  }

  to {
    transform: scale(1);
    opacity: 1;
  }
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