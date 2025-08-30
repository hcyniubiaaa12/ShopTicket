import http from "..";
export const fetchMyPerformance = () => {
    return http.get("/performance/getMyPerformance");
}