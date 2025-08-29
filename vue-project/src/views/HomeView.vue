<template>
  <div class="home-page">
    <!-- 顶部搜索栏 -->
    <header class="mobile-header">
      <div class="location">
        <span>北京</span>
      </div>
      <div class="search-box">
        <van-search v-model="searchValue" placeholder="搜索演出、艺人或场馆" background="transparent" shape="round"
          @search="onSearch" />
      </div>
      <div class="qr-code">
        <van-icon name="scan" size="20" />
      </div>
    </header>

    <!-- 轮播图 -->
    <van-swipe class="banner-swipe" :autoplay="3000" indicator-color="#ff6a00">
      <van-swipe-item>
        <div class="banner-item" style="background-color: #ff9966;">
          <img src="https://dummyimage.com/750x300/ff9966/ffffff&text=Banner1" alt="Banner">
        </div>
      </van-swipe-item>
      <van-swipe-item>
        <div class="banner-item" style="background-color: #66ccff;">
          <img src="https://dummyimage.com/750x300/66ccff/ffffff&text=Banner2" alt="Banner">
        </div>
      </van-swipe-item>
      <van-swipe-item>
        <div class="banner-item" style="background-color: #66ff99;">
          <img src="https://dummyimage.com/750x300/66ff99/ffffff&text=Banner3" alt="Banner">
        </div>
      </van-swipe-item>
    </van-swipe>

    <!-- 分类导航 -->
    <div class="category-section">
      <div class="section-title">
        <h3>演出类型</h3>
      </div>
      <div class="category-grid">
        <div class="category-item" v-for="(category, index) in categories" :key="index"
          @click="goToCategory(category.id)">
          <div class="category-icon" :style="{ backgroundColor: category.bgColor }">
            <van-icon :name="category.icon" :color="category.color" size="24" />
          </div>
          <span>{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- 热门推荐 -->
    <div class="hot-events">
      <div class="section-title">
        <h3>热门推荐</h3>
        <router-link to='/more' class="more-link">查看更多 ></router-link>
      </div>
      <div class="events-list">
        <div class="event-card" v-for="(event, index) in hotEvents" :key="index"
          @click="goToEventDetail(event.eventId,event.cityId)">
          <div class="event-image">
            <img :src="event.url">
            <div class="event-tag" v-if="event.tag">{{ event.tag }}</div>
          </div>
          <div class="event-info">
            <h3 class="event-title">{{ event.title + '-' + event.city + '站' }}</h3>
            <p class="event-arist">{{ event.artist }}</p>
            <p class="event-time" v-if="event.firstShow != event.lastShow">
              {{
                event.firstShow + ' - ' + event.lastShow
              }}

            </p>
            <p class="event-time" v-else>{{ event.firstShow }}</p>
            <p class="event-location">{{ event.venue }}</p>

            <p class="event-price">¥{{ event.minPrice }} 起</p>
          </div>
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
import { fetchAllType } from '../../public/util/type/fetchAllType'
import { fetchAllPerformance } from '../../public/util/performance/fetchPerformance'
import { fetchCity } from '../../public/util/city/fetchCity'


const router = useRouter()
const activeFooterItem = ref('home')


let city = ref([])

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '演出', icon: '❤️' },
  { id: 'userticket', name: '票夹', icon: '🎟️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

// 分类导航

const predefinedStyles = [
  { name: '演唱会', icon: 'music', color: '#ff6a00', bgColor: '#fff0e8' },
  { name: '音乐剧', icon: 'like', color: '#ff9966', bgColor: '#fff5f0' },
  { name: '话剧', icon: 'tv-o', color: '#66ccff', bgColor: '#e8f5ff' },
  { name: '儿童剧', icon: 'flower-o', color: '#66ff99', bgColor: '#e8fff5' },
  { name: '脱口秀', icon: 'underway', color: '#cc66ff', bgColor: '#f5e8ff' }
];

// 创建 name -> 样式 的映射表
const styleMap = Object.fromEntries(
  predefinedStyles.map(item => [item.name, item])
);

// 响应式分类数据（将来会被后端数据填充）
const categories = ref([]);

// 热门演出 (5条数据)
const hotEvents = ref([

])
onMounted(async () => {
  try {
    const { data } = await fetchAllType();
    const res = await fetchAllPerformance()
    const result = await fetchCity();
    if (result.data.code === 200) {


      city.value = result.data.data;

    }
    if (res.data.code === 200) {
      hotEvents.value = res.data.data.slice(0, 5); // 取前5条热门演出
   



    } else {
      console.warn('获取演出数据失败:', res.data.message);
    }

    // 映射后端数据 + 前端样式
    const mapped = data.data
      .filter(item => item.isDeleted === 0) // 过滤已删除
      .map(item => {
        const matchedStyle = styleMap[item.name];
        if (matchedStyle) {
          return {
            id: item.id,      // 使用后端数字 id
            name: item.name,
            icon: matchedStyle.icon,
            color: matchedStyle.color,
            bgColor: matchedStyle.bgColor
          };
        } else {
          // 如果没有匹配到样式，使用默认值
          return {
            id: item.id,
            name: item.name,
            icon: 'question-o',
            color: '#999',
            bgColor: '#f9f9f9'
          };
        }
      });

    // 更新 categories
    categories.value = mapped;

  } catch (error) {
    console.error('获取分类失败:', error);
  }
});

const onSearch = () => {
  console.log('搜索:', searchValue.value)
}

const goToCategory = (categoryId) => {
  console.log('查看分类演出:', categoryId)
  router.push(`/ticket?category=${categoryId}`)
}

const goToEventDetail = (eventId,cityId) => {
  console.log('查看演出详情:', eventId)

  router.push({
    path: `/event/${eventId}`,
    query: { cityId: cityId }
  })
}

const searchValue = ref('')
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
      router.push('/profile')
      break
  }
}
</script>

<style scoped>
.home-page {
  padding-bottom: 60px;
  background-color: #f5f5f5;
}

/* 顶部搜索栏 */
.mobile-header {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.location {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-right: 10px;
}

.search-box {
  flex: 1;
}

.qr-code {
  margin-left: 10px;
}

/* 轮播图 */
.banner-swipe {
  height: 150px;
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 分类导航 */
.category-section {
  background-color: #fff;
  margin: 10px 0;
  padding: 15px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.more-link {
  color: #999;
  font-size: 12px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 5px;
}

.category-item span {
  font-size: 12px;
  color: #333;
}

/* 热门推荐 */
.hot-events {
  background-color: #fff;
  padding: 15px;
  margin: 10px 0;
}

.events-list {
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
  position: relative;
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

.event-tag {
  position: absolute;
  top: 5px;
  left: 5px;
  background-color: #ff6a00;
  color: #fff;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 3px;
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

.event-arist {
  margin: 4px 0;
  font-size: 12px;
  color: orange
}

.event-price {
  color: #ff6a00;
  font-weight: bold;
  font-size: 15px;
  margin-bottom: auto;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}
</style>