import axios from 'axios';

// URLs for Microservices
export const SPRING_API_URL = "http://localhost:8080/api";
export const AUTH_API_URL = "http://localhost:5000/api";

// Create Axios Instance for Spring Boot (Core/Business Logic)
const springInstance = axios.create({
    baseURL: SPRING_API_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

// Create Axios Instance for Node.js (Auth)
const authInstance = axios.create({
    baseURL: AUTH_API_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

// Interceptor to attach JWT token to Spring Requests
springInstance.interceptors.request.use(
    (config) => {
        const user = localStorage.getItem('user');
        if (user) {
            const parsedUser = JSON.parse(user);
            if (parsedUser.token) {
                config.headers.Authorization = `Bearer ${parsedUser.token}`;
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor to attach JWT token to Auth Requests (for /profile)
// authInstance.interceptors.request.use(
//     (config) => {
//         const user = localStorage.getItem('user');
//         if (user) {
//             const parsedUser = JSON.parse(user);
//             if (parsedUser.token) {
//                 config.headers.Authorization = `Bearer ${parsedUser.token}`;
//             }
//         }
//         return config;
//     },
//     (error) => Promise.reject(error)
// );

authInstance.interceptors.request.use(config => {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user?.token) {
        config.headers.Authorization = `Bearer ${user.token}`;
    }
    return config;
});


export { springInstance, authInstance };
