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

    <!-- 主体：左图右信息 -->
    <div class="event-main-section">
      <div class="event-poster">
        <img :src="eventData.url" :alt="eventData.title" />
      </div>
      <div class="event-info">
        <h1 class="event-title">{{ eventData.title + '-' + eventData.city+ '站'}}</h1>
        <div class="event-meta">
          <p class="event-location">
            <van-icon name="location-o" />
            {{ eventData.venue }}
          </p>
          <p class="event-price">
            <van-icon name="coupon-o" />
            ¥{{ eventData.minPrice }} 起
          </p>
        </div>
      </div>
    </div>

    <!-- 城市巡演站列表 -->
    <!-- 城市巡演站 - 横向滚动 -->
    <div class="tour-stations-section-horizontal">
      <h3 class="section-title">巡演城市</h3>
      <div class="stations-scroll">
        <div v-for="(station, index) in tourStations" :key="index"
          :class="['station-pill', { current: station.name === eventData.city }]" @click="selectStation(station)">
          <span class="city-name">{{ station.name }}</span>
          <span class="current-marker" v-if="station.name === eventData.city"></span>
        </div>
      </div>
    </div>

    <!-- 演出详情（折叠面板） -->
    <div class="event-description">
      <van-collapse v-model="activeCollapse">
        <van-collapse-item title="演出详情" name="description">
          <p>
            座位：{{ eventData.availableSeats }} 个 <br />
            演出时长：约 {{ eventData.duration }} 分钟（以现场为准）<br />
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
            <img :src="eventData.url" :alt="eventData.title" />
          </div>
          <div class="event-info-text">
            <h3>{{ eventData.title +'-'+eventData.city+'站'}}</h3>
            <p>{{ selectedDate ? formatDate(selectedDate) : '请选择场次' }}</p>
            <p>{{ eventData.venue }}</p>
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
        <!-- 票数选择 -->
        <div class="ticket-section" v-if="visible">
          <h3>票数</h3>
          <div class="quantity-selector">
            <span class="quantity-limit">每场演唱会限购4张</span>
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
import { fetchCityByEventId } from '../../public/util/city/fetchCityByEventId'



// 使用登录状态
const loginStore = useLoginStore()
const route = useRoute()
const router = useRouter()

// 折叠面板控制
const activeCollapse = ref(['description'])

// 收藏状态
const isFavorite = ref(false)

// 观演须知
const notice = "1. 请在演出前30分钟入场\n2. 请勿携带易燃易爆物品\n3. 请勿拍照录像\n4. 1.2米以下儿童谢绝入场\n5. 请妥善保管好个人财物"

// 演出数据
const eventData = ref({})
const currentCityId = ref(route.query.cityId)

// 购票弹窗控制
const showTicketModal = ref(false)
const showPaymentConfirm = ref(false)

// 选中信息
const selectedDate = ref('')
const selectedPrice = ref(null)
const selectedQuantity = ref(1)

const dateObject = ref([])

// 场次和票价数据
const timeList = ref([])
const ticketList = ref([])
const visible = ref(false)
const createdOrderId = ref(null) // 保存创建的订单ID



// 城市巡演站数据（实际应从接口获取，这里模拟）
const tourStations = ref([])

// 模拟场次数据（用于购票弹窗）
const performanceDates = computed(() => {
 
  
  return timeList.value || []
})

// 页面初始化
onMounted(async () => {
  try {
    const { data } = await fetchDetail(route.params.id, currentCityId.value)
    const cityData = await fetchCityByEventId(route.params.id)
    if (cityData.data.code === 200) {
      tourStations.value = cityData.data.data
    
    }

    if (data.code === 200 && data.data.length > 0) {
      eventData.value = { ...data.data[0] }
     
     
     
    } else {
      alert('未找到演出信息')
      router.back()
    }
  } catch (err) {
    console.error('加载失败:', err)
    alert('网络错误')
    router.back()
  }
})

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 切换想看状态
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  console.log(isFavorite.value ? '已添加到想看' : '已取消想看', eventData.value.id)
}

// 打开购票弹窗
const buyTicket = async () => {
  if (!loginStore.isLogin) {
    router.push('/login')
    return
  }
  showTicketModal.value = true
  try {
    const { data } = await fetchTimeById(route.params.id,currentCityId.value)
    if (data.code === 200) {
      timeList.value = data.data
     
    }
  } catch (err) {
    console.error('加载场次失败:', err)
  }
}


// 关闭购票弹窗
const closeTicketModal = () => {
  showTicketModal.value = false
  showPaymentConfirm.value = false
  showPaymentConfirm.value = false
}

// 选择场次
// 选择场次
const selectDate = async (date) => {
  dateObject.value = date
  selectedDate.value = date.time
  try {
    const res = await fetchhAllTicket(date.id)
    if (res.data.code === 200) {
      ticketList.value = res.data.data
    
    }
  } catch (err) {
    console.error('加载票价失败:', err)
  }
}

// 选择票价
const selectPrice = (price) => {

  
  visible.value = true
  selectedPrice.value = price
}

// 修改票数
// 修改票数
const increaseQuantity = () => {
  if (selectedQuantity.value < 4) selectedQuantity.value++
}
const decreaseQuantity = () => {
  if (selectedQuantity.value > 1) selectedQuantity.value--
  if (selectedQuantity.value > 1) selectedQuantity.value--
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

// 切换城市站（可扩展：请求该城市对应场次）
const selectStation = async (station) => {
  // 更新当前显示信息
  eventData.value.city = station.name
  eventData.value.venue = station.venue
  currentCityId.value = station.id
  const { data } = await fetchDetail(route.params.id, currentCityId.value);
  if(data.code === 200 && data.data.length > 0) {
    eventData.value = { ...data.data[0] };
  } else {
    alert('未找到该城市的演出信息');
    return;
  }
  router.replace({
    query: {
      ...route.query,        // 保留其他 query 参数
      cityId: station.id      // 更新 cityId
    }

  })

  // 可在此处重新加载该城市的场次数据
}

// 创建订单
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
      createdOrderId.value = data.data
      showTicketModal.value = false // 关闭购票弹窗
      showPaymentConfirm.value = true // 显示支付确认弹窗
    } else {
      alert(data.message || '订单创建失败')
    }
  } catch (error) {
    console.error('订单创建失败:', error)
    alert('网络错误，请稍后重试')
  }
}

// 确认支付
const confirmPayment = async () => {
  try {
    const { data } = await payOrder(createdOrderId.value)
    if (data.code === 200) {
      createdOrderId.value = null
      alert('购票成功！')
      closeTicketModal()
      router.push('/order')
    } else {
      alert('支付失败')
    }
  } catch (err) {
    console.error('支付失败:', err)
    alert('支付请求失败')
  }
}

// 取消支付
const cancelPayment = () => {
  showPaymentConfirm.value = false
  router.push(`/order?status=pending&id=${createdOrderId.value}`)
}
</script>

<style scoped>
/* ===== 页面整体 ===== */
.event-detail-page {
  padding-bottom: 70px;
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* ===== 顶部导航栏 ===== */
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
  color: #333;
}

/* ===== 左图右信息 ===== */
.event-main-section {
  display: flex;
  gap: 15px;
  background-color: #fff;
  padding: 15px;
  margin: 10px 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.event-poster {
  width: 120px;
  height: 160px;
  flex-shrink: 0;
}

.event-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.event-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.event-title {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
}

.event-meta p {
  margin: 6px 0;
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

/* ===== 横向城市巡演站 ===== */
.tour-stations-section-horizontal {
  background-color: #fff;
  margin: 0 15px 10px;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tour-stations-section-horizontal .section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0 0 12px 0;
}

.stations-scroll {
  display: flex;
  overflow-x: auto;
  gap: 12px;
  padding: 4px 0;
  scrollbar-width: none;
  /* Firefox */
}

.stations-scroll::-webkit-scrollbar {
  display: none;
  /* Chrome/Safari */
}

.station-pill {
  flex: 0 0 auto;
  min-width: 60px;
  text-align: center;
  cursor: pointer;
  position: relative;
}

.city-name {
  display: block;
  font-size: 14px;
  color: #555;
  padding: 6px 8px;
  border-radius: 6px;
  transition: color 0.2s;
}

/* 当前城市：文字变色 + 小方框 */
.station-pill.current .city-name {
  color: #FF69B4;
  font-weight: 500;
}

.current-marker {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 6px;
  background-color: #FF69B4;
  border-radius: 50%;
  /* 小圆点，若要方块改为 border-radius: 1px */
  /* border-radius: 1px;  // 如果想要小方块，取消这行注释 */
}

/* ===== 演出详情折叠面板 ===== */
.event-description {
  background-color: #fff;
  padding: 0 15px;
  margin: 0 15px 10px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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

/* ===== 底部操作栏 ===== */
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

/* ===== 购票弹窗 ===== */
.ticket-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 2000;
  /* 必须高于页面其他元素 */
  display: flex;
  justify-content: center;
  align-items: flex-end;
  /* 从底部弹出 */
  pointer-events: none;
  /* 防止遮罩层拦截点击 */
}

.ticket-modal-content {
  pointer-events: auto;
  /* 确保内容可点击 */
  width: 100%;
  max-height: 80vh;
  background-color: #fff;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  padding: 20px;
  padding-top: 50px;
  overflow-y: auto;
  position: relative;
  transform: translateY(100%);
  /* 初始隐藏 */
  animation: slideUp 0.3s forwards;
  /* 动画弹出 */
}

/* 弹出动画 */
@keyframes slideUp {
  to {
    transform: translateY(0);
  }
}

/* 弹窗头部 */
.modal-header {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 16px 10px;
  background-color: #fff;
  z-index: 10;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

/* 内容区域 */
.event-basic-info {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.event-poster-small {
  width: 70px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 12px;
}

.event-poster-small img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.event-info-text h3 {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.event-info-text p {
  margin: 4px 0;
  font-size: 13px;
  color: #666;
}

/* 场次时间选择 */
.date-selector {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 10px 0;
  scrollbar-width: none;
}

.date-selector::-webkit-scrollbar {
  display: none;
}

.date-item {
  flex: 0 0 auto;
  min-width: 60px;
  padding: 10px 12px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.date-item.active {
  border-color: #FF69B4;
  background-color: #fff0f5;
  color: #FF69B4;
}

/* 票价选择 */
.price-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.price-item {
  width: calc(50% - 5px);
  padding: 12px 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  text-align: center;
  transition: all 0.2s;
  box-sizing: border-box;
}

.price-item.active {
  border-color: #FF69B4;
  background-color: #fff0f5;
}

.price-name {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.price-amount {
  font-size: 15px;
  font-weight: bold;
  color: #FF69B4;
}

/* 票数选择 */
.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
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
  font-size: 16px;
  cursor: pointer;
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
  font-weight: bold;
}

/* 确认按钮 */
.modal-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.buy-button {
  width: 100%;
  padding: 14px;
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