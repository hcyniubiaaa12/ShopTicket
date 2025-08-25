
import http from "..";
export const fetchDetail = (id, cityId) => {
    return http.get(`/performance/getPerformanceByEventId/${id}`, { params: { cityId: cityId } });
};