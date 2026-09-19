import axios from "axios";

// Points at auth-service (login / change-password only).
// Everything else still goes through api/api.js -> core-service.
const authApiClient = axios.create({
    baseURL: `${import.meta.env.VITE_AUTH_API_URL || "http://localhost:8081"}/api`,
    headers: {
        "Content-Type": "application/json",
    },
});

export default authApiClient;