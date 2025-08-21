<template>
  <div class="login-page">
    <!-- 登录表单页面 -->
    <div class="login-container" v-if="!isLogin">
      <!-- 页面顶部导航 -->
      <div class="page-header">
        <van-icon name="arrow-left" class="back-icon" @click="goBack" />
        <span class="header-title">请登录您的账户</span>
      </div>

      <div class="login-form" v-if="!isLogin">
        <van-form>
          <!-- 账号输入框 -->
          <div class="form-item">
            <van-field v-model="user.account" name="account" placeholder="请输入用户名/手机号" :rules="[{ required: true, message: '请输入用户名或手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
            ]" left-icon="user-o" class="custom-field" />
          </div>

          <!-- 验证码输入框和图片 -->
          <div class="form-item">
            <van-field v-model="user.code" name="code" placeholder="请输入验证码" :rules="[{ required: true, message: '请输入验证码' },
            { pattern: /^\d{4}$/, message: '请输入4位数字验证码' }
            ]" left-icon="shield-o" class="custom-field">
              <template #button>
                <div class="captcha-container" @click="refreshCaptcha">
                  <img :src="captchaImage.captcha" alt="验证码" class="captcha-image" />
                </div>
              </template>
            </van-field>
          </div>

          <!-- 登录按钮 -->
          <div class="login-button">
            <van-button round block native-type="submit" class="login-btn" @click="onSubmit(user)">
              登录
            </van-button>
          </div>
        </van-form>
      </div>
    </div>

    <!-- 未登录状态下的"点击我"页面 -->
    <div class="unauthorized-page" v-if="showUnauthorized">
      <div class="unauthorized-content">
        <div class="unauthorized-icon">
          <van-icon name="warning-o" size="60" color="#ee0a24" />
        </div>
        <h2 class="unauthorized-title">请登录</h2>
        <p class="unauthorized-desc">您需要登录后才能访问此页面</p>
        <van-button round type="primary" @click="goToLogin" class="auth-button">
          前往登录
        </van-button>

        <div class="back-link" @click="hideUnauthorizedPage">
          &lt; 返回
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showNotify } from 'vant'
import { fetchCaptcha } from '../../public/util/captcha/fetchCaptcha'
import { login } from '../../public/util/login/login'
import { useLoginStore } from '@/stores/login'
import { fetchUser } from '../../public/util/user/fetchUser'
const loginStore = useLoginStore()
let isLogin = ref(loginStore.isLogin)
// 验证码图片 (示例图片)
const captchaImage = ref({})
// 表单数据

const user = ref({
  account: '19859351310',
  key: '',
  code: '',
})


// 路由
const router = useRouter()

// 是否显示未授权页面
const showUnauthorized = ref(false)

// 刷新验证码
const refreshCaptcha = async () => {
  let { data } = await fetchCaptcha()
  if (data.code === 200) {
    captchaImage.value = data.data
    user.value.key = captchaImage.value.key
  }
}

// 显示未授权页面
const showUnauthorizedPage = () => {
  showUnauthorized.value = true
}

// 隐藏未授权页面
const hideUnauthorizedPage = () => {
  showUnauthorized.value = false
}

// 回退上一页
const goBack = () => {
  router.go(-1)
}

// 前往登录页
const goToLogin = () => {
  showUnauthorized.value = false
}

// 提交表单
const onSubmit = async (user) => {
  console.log('提交表单:', user)
  let { data } = await login(user);

  if (data.code === 200) {
    showNotify({ type: 'success', message: '登录成功' })
    loginStore.setToken(data.data)
    let res = await fetchUser()
    if (res.data.code === 200) {
      loginStore.setUser(res.data.data)


    }

    router.push({ path: '/' })

  }



}

// 页面加载时检查是否需要显示未授权页面
onMounted(() => {
  // 可以在这里添加是否显示未授权页面的逻辑
  // 例如从路由参数中获取
  refreshCaptcha()
  const needAuth = router.currentRoute.value.query.needAuth === 'true'
  showUnauthorized.value = needAuth
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  width: 100%;
  max-width: 400px;
  background: white;
  margin: 0 auto;
  min-height: 100vh;
}

/* 页面顶部导航 */
.page-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  background: white;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-icon {
  font-size: 20px;
  color: #333;
  margin-right: 10px;
  cursor: pointer;
}

.header-title {
  font-size: 16px;
  color: #333;
  flex: 1;
  text-align: center;
}

/* 输入框容器：控制宽度并居中 */
.form-item {
  width: 90%;
  max-width: 300px;
  margin: 0 auto;
}

/* 简洁输入框整体样式 */
:deep(.custom-field .van-field) {
  display: flex;
  align-items: center;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  /* 整体圆角 */
  background-color: #ffffff;
  height: 52px;
  padding: 0 16px;
  font-size: 14px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

/* 聚焦状态：粉色边框高亮 */
:deep(.custom-field .van-field:focus-within) {
  border-color: #ff69b4;
  box-shadow: 0 0 0 2px rgba(255, 105, 180, 0.1);
}

/* 左侧图标 */
:deep(.custom-field .van-field__left-icon) {
  margin-right: 12px;
}

:deep(.custom-field .van-field__left-icon i) {
  color: #999;
  font-size: 18px;
}

/* 输入文字区域 */
:deep(.custom-field .van-field__control) {
  flex: 1;
  padding: 6px 0;
}

/* 验证码按钮区域 */
:deep(.custom-field .van-field__button) {
  margin-left: 10px;
  display: flex;
  align-items: center;
}

/* 验证码图片容器：与输入框等高，圆角衔接 */
.captcha-container {
  width: 100px;
  height: 30px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #e0e0e0;
  background-color: #fafafa;
}

.captcha-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 点击刷新反馈 */
.captcha-container:hover {
  border-color: #ff69b4;
}

/* 登录按钮：与输入框同宽 */
.login-button {
  width: 90%;
  max-width: 300px;
  margin: 10px auto 20px;
}

.login-btn {
  width: 100%;
  height: 48px;
  background: #ff69b4;
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  box-shadow: 0 2px 6px rgba(255, 105, 180, 0.2);
  transition: background 0.3s;
}

.login-btn:active {
  background: #e958a6;
}

/* 未授权页面 */
.unauthorized-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.unauthorized-content {
  width: 85%;
  max-width: 300px;
  text-align: center;
  background: white;
  border-radius: 12px;
  padding: 32px 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.unauthorized-icon {
  margin-bottom: 16px;
}

.unauthorized-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 8px 0;
}

.unauthorized-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 24px 0;
}

.auth-button {
  width: 100%;
  height: 44px;
  background: #ff69b4;
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 6px rgba(255, 105, 180, 0.2);
}

.back-link {
  color: #1989fa;
  font-size: 14px;
  cursor: pointer;
}
</style>