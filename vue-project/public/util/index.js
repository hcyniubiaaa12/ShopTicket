import axios from "axios";
import {useLoginStore} from '@/stores/login'

const http = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 5000
});
http.interceptors.request.use(
    (config) => {
        const loginStore = useLoginStore()
        const token = loginStore.token; // 从 Pinia store 中获取 token
    
        if (token) {
            config.headers['Authorization'] =  token
        }
        return config;
    }
)
export default http;