import http from "..";
export const fetchDetail = (id) => {
    return http.get(`/performance/getPerformanceByEventId/${id}`);
};