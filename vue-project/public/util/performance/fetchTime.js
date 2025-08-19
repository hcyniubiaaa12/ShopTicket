import http from "..";
export const fetchTimeById = (id) => {
    return http.get(`/performance/time/${id}`);
}