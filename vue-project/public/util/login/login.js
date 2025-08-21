import http from "..";
export const login = (user) => {
  return http.post("/login", user);
}