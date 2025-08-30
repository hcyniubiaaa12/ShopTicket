<template>
  <div class="ticket-page">
    <!-- 分类筛选 -->
    <div class="category-filter">
      <div class="category-item" v-for="(category, index) in categories" :key="index"
        :class="{ active: activeCategory === category.id }" @click="setActiveCategory(category.id)">
        {{ category.name }}
      </div>
    </div>

    <!-- 演出列表 -->
    <div class="events-list">
      <div class="event-card" v-for="(event, index) in filteredEvents" :key="index"

        @click="goToEventDetail(event.eventId,event.cityId)">
        <div class="event-image">
          <img :src="event.url" />
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchAllType } from '../../public/util/type/fetchAllType'
import { fetchPerformanceById } from '../../public/util/performance/fetchPerformance'
import { useLoginStore } from '@/stores/login'
const route = useRoute()
const router = useRouter()
const loginStore = useLoginStore()
const activeFooterItem = ref('ticket')
const categories = ref([])

// 活跃分类
const activeCategory = ref(route.query.category || null)

// 演出数据
const events = ref([])

// 底部导航项
const footerItems = ref([
  { id: 'home', name: '首页', icon: '🏠' },
  { id: 'ticket', name: '演出', icon: '❤️' },
  { id: 'userticket', name: '票夹', icon: '🎟️' },
  { id: 'order', name: '订单', icon: '📋' },
  { id: 'profile', name: '我的', icon: '👤' }
])

// ========== 修复核心：将 async 逻辑移出 computed ==========

// 根据分类筛选演出（同步 computed）
const filteredEvents = computed(() => {

  // 找到当前分类的 name（如 "演唱会"）
  const selectedCategory = categories.value.find(cat => cat.id == activeCategory.value)

  if (!selectedCategory) return []

  // 使用 typeName 匹配

   
  return events.value.filter(event => event.typeName === selectedCategory.name)
})

// 设置分类并加载对应数据
const setActiveCategory = async (categoryId) => {
  activeCategory.value = categoryId
  try {
    let { data } = await fetchPerformanceById(categoryId)
    if (data && data.code === 200) {
      events.value = data.data
      console.log('获取演出数据成功:', data.data);

    } else {
      events.value = []
      console.warn('数据获取失败或为空', data?.message)
    }
  } catch (err) {
    console.error('请求演出数据失败:', err)
    events.value = []
  }

  // 更新 URL
  router.replace({
    query: { ...route.query, category: categoryId }
  })
}

// 跳转到演出详情
const goToEventDetail = (eventId,cityId) => {
  console.log('查看演出详情:', eventId);

  router.push({
    path: `/event/${eventId}`,
    query: { cityId: cityId }
  })
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

// 页面初始化：获取分类并加载默认数据
onMounted(async () => {
  try {
    const { data } = await fetchAllType()

    if (data.code === 200) {
      categories.value = data.data.map(item => ({
        id: item.id,
        name: item.name
      }))

      // 设置默认分类（URL 优先，否则选第一个）
      const initialCategory = route.query.category
        ? route.query.category
        : (categories.value.length > 0 ? categories.value[0].id : null)

      activeCategory.value = initialCategory
      await setActiveCategory(initialCategory) // 加载数据
    } else {
      // 失败时加载全部演出
      await setActiveCategory(null)
    }
  } catch (err) {
    console.error('初始化分类失败:', err)
    await setActiveCategory(null)
  }
})

// 监听 URL 中 category 参数变化
watch(
  () => route.query.category,
  async (newId) => {
    if (newId !== activeCategory.value) {
      activeCategory.value = newId
      await setActiveCategory(newId)
    }
  }
)
</script>

<style scoped>
.ticket-page {
  padding-bottom: 60px;
  background-color: #f5f5f5;
}

/* 分类筛选 */
.category-filter {
  display: flex;
  overflow-x: auto;
  background-color: #fff;
  padding: 10px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.category-filter::-webkit-scrollbar {
  display: none;
}

.category-item {
  flex-shrink: 0;
  padding: 10px 20px;
  margin: 0 8px;
  border-radius: 30px;
  font-size: 15px;
  font-weight: 500;
  white-space: nowrap;
  cursor: pointer;
  background-color: #f8f8f8;
  color: #555;
  transition: all 0.2s ease;
  border: 2px solid transparent;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.category-item.active {
  background-color: #f8f8f8;
  color: #555;
  transform: scale(1.05);
  border: 2px solid #dddddd;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
  font-weight: 600;
}

.category-item:hover {
  background-color: #e9e9e9;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.category-item:active {
  transform: scale(0.98);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.category-item.active:hover {
  background-color: #f0f0f0;
  transform: scale(1.08);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
}

.category-item.active:active {
  transform: scale(1.03);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.18);
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
.event-arist {
  margin: 4px 0;
  font-size: 12px;
  color: orange
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