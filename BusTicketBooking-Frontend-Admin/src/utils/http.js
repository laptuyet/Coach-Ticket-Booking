import axios from 'axios'

const http = axios.create({
    baseURL: 'https://coach-ticket-booking.onrender.com/api/v1/',
    timeout: 10000, // 10s
    headers: {
        "Content-Type": "application/json",
    }
})

http.interceptors.request.use((config) => {
    const accessToken = localStorage.getItem('acToken');
    if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
})

export { http }