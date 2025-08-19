import http from "..";
export const fetchAllType = () => {
  return http.get("/type");
};