import http from "..";
export const createOrder = (order) => {
    return http.post('/order/saveOrder', order);
}