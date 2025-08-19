import http from "..";
export const fetchPerformanceById = (id) => {
    return http.get(`/performance/${id}`);
}
export const fetchAllPerformance = () => {
    return http.get(`/performance`);
}