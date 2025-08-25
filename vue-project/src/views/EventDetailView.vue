<template>
  <div class="event-detail-page">
    <!-- 顶部导航栏 -->
    <header class="detail-header">
      <div class="header-left" @click="goBack">
        <van-icon name="arrow-left" size="20" />
      </div>
      <div class="header-title">演出详情</div>
      <div class="header-right">
        <van-icon name="share-o" size="20" />
      </div>
    </header>

    <!-- 演出海报 -->
    <div class="event-poster">
      <img :src="eventData.url" :alt="eventData.title">
    </div>

    <!-- 演出基本信息 -->
    <div class="event-info">
      <h1 class="event-title">{{ eventData.title }}</h1>
      <div class="event-meta">
        <p class="event-time">
          <van-icon name="clock-o" />
          {{
            eventData.totalPerformances > 1 ? eventData.firstShow + ' - ' + eventData.lastShow :
              eventData.firstShow
          }}
        </p>
        <p class="event-location">
          <van-icon name="location-o" />
          {{ eventData.venueName }}
        </p>
        <p class="event-price">
          <van-icon name="coupon-o" />
          ¥{{ eventData.minPrice }} 起
        </p>
      </div>
    </div>

    <!-- 演出详情 -->
    <div class="event-description">
      <van-collapse v-model="activeCollapse">
        <van-collapse-item title="演出详情" name="description">
          <p>
            座位：{{ eventData.availableSeats }} 个 <br />
            演出时长：约 {{ eventData.duration }} 分钟（以现场为准）<br>
            {{ eventData.description }}
          </p>
        </van-collapse-item>
        <van-collapse-item title="观演须知" name="notice">
          <p>{{ notice }}</p>
        </van-collapse-item>
      </van-collapse>
    </div>

    <!-- 底部操作栏 -->
    <div class="event-detail-footer">
      <div class="footer-actions">
        <div class="combined-button">
          <div class="favorite-section" @click="toggleFavorite">
            <span :class="{ favorited: isFavorite }">{{ isFavorite ? '已想看' : '想看' }}</span>
          </div>
          <div class="buy-section" @click="buyTicket">
            立即购票
          </div>
        </div>
      </div>
    </div>

    <!-- 购票弹窗 -->
    <div v-if="showTicketModal" class="ticket-modal">
      <div class="ticket-modal-content">
        <div class="modal-header">
          <h2>选择票品</h2>
          <van-icon name="close" @click="closeTicketModal" />
        </div>

        <div class="event-basic-info">
          <div class="event-poster-small">
            <img :src="eventData.url" :alt="eventData.title">
          </div>
          <div class="event-info-text">
            <h3>{{ eventData.title }}</h3>
            <p>{{ selectedDate ? formatDate(selectedDate) : '请选择场次' }}</p>
            <p>{{ eventData.venueName }}</p>
          </div>
        </div>

        <div class="ticket-section">
          <h3>场次时间</h3>
          <div class="date-selector">
            <div v-for="(date, index) in performanceDates" :key="index"
              :class="['date-item', { active: selectedDate === date.time }]" @click="selectDate(date)">
              {{ formatDate(date.time) }}
            </div>
          </div>
        </div>

        <div class="ticket-section">
          <h3>票价</h3>
          <div class="price-selector">
            <div v-for="(price, index) in ticketList" :key="index"
              :class="['price-item', { active: selectedPrice === price }]" @click="selectPrice(price)">
              <div class="price-info">
                <div class="price-name">{{ price.name }}</div>
                <div class="price-amount">¥{{ price.price }}</div>
              </div>
              <div class="price-stock" v-if="price.stock === 1">
                剩余 {{ price.stock }} 张
              </div>
            </div>
          </div>
        </div>

        <!-- 票数选择 -->
        <div class="ticket-section" v-if="visible">
          <h3>票数</h3>
          <div class="quantity-selector">
            <span class="quantity-limit">每场演唱会限购4张</span>
            <div class="quantity-control">
              <button class="quantity-btn" @click="decreaseQuantity" :disabled="selectedQuantity <= 1">-</button>
              <span class="quantity-display">{{ selectedQuantity }}</span>
              <button class="quantity-btn increase-btn" @click="increaseQuantity"
                :disabled="selectedQuantity >= 4">+</button>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button :disabled="!selectedDate || !selectedPrice"
            :class="['buy-button', { disabled: !selectedDate || !selectedPrice }]" @click="createOrderDirectly">
            确认购票
          </button>
        </div>
      </div>
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
            <span class="value">{{ eventData.title }}</span>
          </div>
          <div class="summary-row">
            <span>场次时间</span>
            <span class="value">{{ formatDate(selectedDate) }}</span>
          </div>
          <div class="summary-row">
            <span>票价类型</span>
            <span class="value">{{ selectedPrice?.name }}</span>
          </div>
          <div class="summary-row">
            <span>票数</span>
            <span class="value">{{ selectedQuantity }} 张</span>
          </div>
          <div class="summary-total">
            <strong>合计金额</strong>
            <strong class="amount">¥{{ (selectedPrice?.price * selectedQuantity).toFixed(2) }}</strong>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="payment-actions">
          <button class="btn-cancel" @click="cancelPayment">取消</button>
          <button class="btn-confirm" @click="confirmPayment">确认支付</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchDetail } from '../../public/util/performance/fetchDetail'
import { fetchTimeById } from '../../public/util/performance/fetchTime'
import { fetchhAllTicket } from '../../public/util/ticket/fetchAllTicket'
import { createOrder } from '../../public/util/order/createOrder'
import { payOrder } from '../../public/util/order/payOrder'
import { useLoginStore } from '@/stores/login'



const loginStore = useLoginStore()
const route = useRoute()
const router = useRouter()

const activeCollapse = ref(['description'])
const isFavorite = ref(false)
const notice = "1. 请在演出前30分钟入场\n2. 请勿携带易燃易爆物品\n3. 请勿拍照录像\n4. 1.2米以下儿童谢绝入场\n5. 请妥善保管好个人财物"

// 演出数据
const eventData = ref({})
const showTicketModal = ref(false)
const showPaymentConfirm = ref(false)
const selectedDate = ref('')
const selectedPrice = ref(null)
const selectedQuantity = ref(1)
const dateObject = ref([])
const timeList = ref([])
const ticketList = ref([])
const visible = ref(false)
const createdOrderId = ref(null) // 保存创建的订单ID

// 模拟场次数据
const performanceDates = computed(() => {
  return timeList.value || []
})

onMounted(async () => {
  const { data } = await fetchDetail(route.params.id)
  if (data.code === 200) {
    eventData.value = data.data[0]
  }
})

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 切换想看状态
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  console.log(isFavorite.value ? '添加演出到想看列表' : '从想看列表移除', eventData.value.id)
}

// 购票：打开选票弹窗
const buyTicket = async () => {
  if (!loginStore.isLogin) {
    router.push('/login')
    return
  }
  showTicketModal.value = true
  const { data } = await fetchTimeById(eventData.value.eventId)
  if (data.code === 200) {
    timeList.value = data.data
  }
}

// 关闭购票弹窗
const closeTicketModal = () => {
  showTicketModal.value = false
  showPaymentConfirm.value = false
}

// 选择场次
const selectDate = async (date) => {
  dateObject.value = date
  selectedDate.value = date.time
  const res = await fetchhAllTicket(date.id)
  if (res.data.code === 200) {
    ticketList.value = res.data.data
  }
}

// 选择票价
const selectPrice = (price) => {
  visible.value = true
  selectedPrice.value = price
}

// 修改票数
const increaseQuantity = () => {
  if (selectedQuantity.value < 4) selectedQuantity.value++
}
const decreaseQuantity = () => {
  if (selectedQuantity.value > 1) selectedQuantity.value--
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

// 直接创建订单（替代原来的显示支付确认弹窗）
const createOrderDirectly = async () => {
  if (!selectedDate.value || !selectedPrice.value) return

  try {
    const orderData = {
      userId: loginStore.userId,
      num: selectedQuantity.value,
      price: selectedPrice.value.price,
      ticketId: selectedPrice.value.id,
      performanceId: dateObject.value.id,

    }

    const { data } = await createOrder(orderData)
   
    if (data.code === 200) {
      // 保存订单ID
     
      createdOrderId.value = data.data
    
      // 显示支付确认弹窗
      showPaymentConfirm.value = true
    } else {
      alert(data.data)
    }
  } catch (error) {
    console.error('订单创建失败:', error)
    alert('网络错误，请稍后重试')
  }
}

// 确认支付（显示成功）
const confirmPayment = async () => {
  let { data } = await payOrder(createdOrderId.value);
  console.log(createdOrderId.value);
  
  if (data.code === 200) {
    // 删除创建的订单ID
    createdOrderId.value = null
    // 显示购票成功
    alert('购票成功！')
    // 关闭所有弹窗
    closeTicketModal()
    showPaymentConfirm.value = false
    // 跳转到订单页面
    router.push('/order')
  }

}

// 取消支付（跳转到订单未完成页）
const cancelPayment = () => {
  // 关闭支付确认弹窗
  showPaymentConfirm.value = false
  // 跳转到订单未完成页面
  router.push(`/order?status=pending&id=${createdOrderId.value}`)
}
</script>

<style scoped>
/* ===== 原有样式保持不变 ===== */
.event-detail-page {
  padding-bottom: 70px;
  background-color: #f5f5f5;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-left,
.header-right {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.header-title {
  flex: 1;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
}

.event-poster {
  width: 100%;
  height: 300px;
}

.event-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.event-info {
  background-color: #fff;
  padding: 20px 15px;
  margin-bottom: 10px;
}

.event-title {
  margin: 0 0 15px 0;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.event-meta p {
  margin: 10px 0;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
}

.event-meta .van-icon {
  margin-right: 8px;
  font-size: 16px;
}

.event-price {
  color: #ff6a00 !important;
  font-weight: bold;
}

.event-description {
  background-color: #fff;
  padding: 0 15px;
}

:deep(.van-collapse-item__content) {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  padding: 10px 0;
}

:deep(.van-collapse-item__content p) {
  margin: 0;
  white-space: pre-line;
}

.event-detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
  padding: 10px 15px;
}

.combined-button {
  display: flex;
  height: 44px;
  border-radius: 22px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.favorite-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FFD700;
  color: #333;
  font-weight: bold;
  cursor: pointer;
}

.buy-section {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FF69B4;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

/* 购票弹窗样式 */
.ticket-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: flex-end;
}

.ticket-modal-content {
  width: 100%;
  max-height: 80%;
  background-color: #fff;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  padding: 20px;
  padding-top: 50px;
  overflow-y: auto;
  position: relative;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 10px;
  background-color: #fff;
  z-index: 10;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
}

.modal-header .van-icon {
  font-size: 20px;
  cursor: pointer;
}

.event-basic-info {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.event-poster-small {
  width: 80px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 15px;
}

.event-poster-small img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.event-info-text h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: bold;
}

.event-info-text p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.ticket-section {
  margin-bottom: 20px;
}

.ticket-section h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
}

.date-selector {
  display: flex;
  overflow-x: auto;
  gap: 10px;
}

.date-item {
  flex: 0 0 auto;
  min-width: 60px;
  padding: 10px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.date-item.active {
  border-color: #FF69B4;
  color: #FF69B4;
  background-color: #fff0f5;
}

.price-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.price-item {
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  text-align: center;
  min-width: 100px;
  transition: all 0.3s ease;
}

.price-item.active {
  border-color: #FF69B4;
  background-color: #fff0f5;
}

.price-name {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.price-amount {
  font-size: 16px;
  font-weight: bold;
  color: #FF69B4;
}

.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 15px;
}

.quantity-limit {
  font-size: 12px;
  color: #999;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 1px solid #ddd;
  background-color: #fff;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.increase-btn {
  background-color: #FF69B4;
  border-color: #FF69B4;
  color: white;
}

.quantity-display {
  min-width: 24px;
  text-align: center;
  font-size: 14px;
  font-weight: bold;
}

.modal-footer {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.buy-button {
  width: 100%;
  padding: 15px;
  background-color: #FF69B4;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.buy-button.disabled {
  background-color: #ccc;
  cursor: not-allowed;
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
</style>