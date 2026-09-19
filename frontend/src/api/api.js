import axios from "axios";

const base = (import.meta.env.VITE_CORE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

const api = axios.create({
    baseURL: `${base}/api`,
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use((config) => {

    const token = localStorage.getItem("token");

    const publicUrls = [
        "/auth/login",
        "/auth/change-password"
    ];

    const isPublic = publicUrls.some(url =>
        config.url?.startsWith(url)
    );

    if (token && !isPublic) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export default api;