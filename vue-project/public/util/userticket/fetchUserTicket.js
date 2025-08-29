import http from "..";
export const fetchUserTicket = () => {
  return http.get("userTicket/list");
};