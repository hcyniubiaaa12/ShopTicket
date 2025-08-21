import http from "..";
export const fetchUser = () => {
    return http.get('/user/getUser');
}