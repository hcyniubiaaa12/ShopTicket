import http from "..";
export const payOrder = (id) => {
    return http.post(`order/pay/${id}` );
}