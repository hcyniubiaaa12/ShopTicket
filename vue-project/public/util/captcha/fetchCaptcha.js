import http from "..";
export const fetchCaptcha = () => {
  return http.get("/getCaptcha");
}