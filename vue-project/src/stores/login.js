
import { defineStore } from 'pinia'
export const useLoginStore = defineStore('login', {
    state: () => ({
        token: localStorage.getItem('token'),
        userId: localStorage.getItem('userId'),
        username: localStorage.getItem('username'),
        avatar: localStorage.getItem('avatar'),
        isLogin: localStorage.getItem('isLogin') === 'true' ? true : false
    }),
    actions: {

       
        setUser(user) {
            this.username = user.name
            this.userId = user.id
            this.avatar = user.url
            localStorage.setItem('userId', user.id)
            localStorage.setItem('username', user.name)
            localStorage.setItem('avatar', user.url)

        },
        setToken(token) {
            console.log('setToken 被调用，token:', token)// 会打印
            this.token = token
            this.isLogin = true

            localStorage.setItem('token', token)
            localStorage.setItem('isLogin', 'true')
        },
        logout(){
            this.isLogin = false
            this.token = null
            this.userId = null
            this.username = null
            this.avatar = null     
            // 清除localStorage中的登录信息
            localStorage.removeItem('token')
            localStorage.removeItem('userId')
            localStorage.removeItem('username')
            localStorage.removeItem('avatar')
            localStorage.removeItem('isLogin')
        }




    }

})
