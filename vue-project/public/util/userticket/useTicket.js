import http from "..";
export const useTicket = (ticket) => {
    return http.post("/userTicket/useTicket", ticket);
}