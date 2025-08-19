import http from "..";
export const fetchCity = () =>{
    return http.get('/city');
}