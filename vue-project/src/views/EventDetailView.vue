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
            演出时长：约 {{ eventData.duration }} 分钟（以现场为准） <br>
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

        <!-- 修改票数选择区域为加减按钮形式 -->
        <div class="ticket-section" v-if="visible">
          <h3>票数</h3>
          <div class="quantity-selector">
            <span class="quantity-limit">每笔订单限购4张</span>
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
            :class="['buy-button', { disabled: !selectedDate || !selectedPrice }]" @click="confirmPurchase">
            确认购票
          </button>
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
import {useLoginStore} from '@/stores/login'
const loginStore = useLoginStore()

const route = useRoute()
const router = useRouter()
const activeCollapse = ref(['description'])
const isFavorite = ref(false)
const notice = "1. 请在演出前30分钟入场\n2. 请勿携带易燃易爆物品\n3. 请勿拍照录像\n4. 1.2米以下儿童谢绝入场\n5. 请妥善保管好个人财物"

// 购票弹窗相关状态
const showTicketModal = ref(false)
const selectedDate = ref('')
const selectedPrice = ref(null)
const selectedQuantity = ref(1) // 添加票数选择，默认为1
const dateObject = ref([])
const timeList = ref([])
const ticketList = ref([])
const visible = ref(false)

// 演出数据 
const eventData = ref([])

// 模拟演出场次数据
const performanceDates = computed(() => {
  if (!eventData.value.firstShow) return []

  return timeList.value

})


onMounted(async () => {
  const { data } = await fetchDetail(route.params.id);
  if (data.code === 200) {
    eventData.value = data.data[0];
  
    

  }

})

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 切换想看状态
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  if (isFavorite.value) {
    console.log('添加演出到想看列表:', eventData.value.id)
  } else {
    console.log('从想看列表移除演出:', eventData.value.id)
  }
}

// 分享演出
const shareEvent = () => {
  console.log('分享演出:', eventData.value.id)
}

// 购票
const buyTicket = async () => {
    if (!loginStore.isLogin) {
    // 如果未登录，跳转到登录页面
    router.push('/login')
  }
  showTicketModal.value = true
  let { data } = await fetchTimeById(eventData.value.eventId)

  if (data.code === 200) {
    timeList.value = data.data



  }
}

// 关闭购票弹窗
const closeTicketModal = () => {
  showTicketModal.value = false
}

// 选择场次日期
const selectDate = async (date) => {
  dateObject.value = date
  selectedDate.value = date.time
  const res = await fetchhAllTicket(date.id);
  if (res.data.code === 200) {
    ticketList.value = res.data.data

  }
}

// 选择票价
const selectPrice = (price) => {
  visible.value = true
  selectedPrice.value = price
}

// 添加增加票数的方法
const increaseQuantity = () => {
  if (selectedQuantity.value < 4) {
    selectedQuantity.value++
  }
}

// 添加减少票数的方法
const decreaseQuantity = () => {
  if (selectedQuantity.value > 1) {
    selectedQuantity.value--
  }
}

// 格式化日期显示
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

// 确认购票
const confirmPurchase = () => {
  if (selectedDate.value && selectedPrice.value) {
    console.log('确认购票:', {
      id: dateObject.value.id,
      eventId: eventData.value.eventId,
      date: dateObject.value.time,
      price: selectedPrice.value.price,
      ticketId: selectedPrice.value.id,
      quantity: selectedQuantity.value // 添加票数信息
    })

    closeTicketModal()
  }
}
</script>

<style scoped>
.event-detail-page {
  padding-bottom: 70px;
  background-color: #f5f5f5;
}

/* 顶部导航栏 */
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

.header-left {
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

.header-right {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

/* 演出海报 */
.event-poster {
  width: 100%;
  height: 300px;
}

.event-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 演出信息 */
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

.event-meta {
  margin-bottom: 20px;
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

/* 演出详情 */
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

/* 底部操作栏 */
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
  /* 黄色 */
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
  /* 粉色 */
  color: white;
  font-weight: bold;
  cursor: pointer;
}

.favorite-section.favorited {
  background-color: #FFD700;
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

.date-item .day {
  font-size: 12px;
  color: #999;
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
</style>