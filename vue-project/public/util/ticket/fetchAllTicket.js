import http from "..";
export const fetchhAllTicket = (id) => {
  return http.get(`/ticket/getTicketById/${id}`);
};