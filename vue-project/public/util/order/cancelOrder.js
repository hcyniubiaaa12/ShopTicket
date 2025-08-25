import http from "..";
export const cancelOrder = (id) => {
  return http.post(`/order/cancel/${id}`);
};