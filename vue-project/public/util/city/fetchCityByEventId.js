import http from "..";
export const fetchCityByEventId = (eventId) => {
  return http.get(`/city/${eventId}`);
}