import http from "..";
export const fetchTimeById = (id,cityId) => {
    return http.get(`/performance/time/${id}`, {
        params: { cityId: cityId }
    });
}