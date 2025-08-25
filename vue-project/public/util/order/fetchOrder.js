import http from "..";
export const fetchOrder = ()=>{
    return http.get("/order/list");
}