 
import authApiClient from "./authApiClient";

export const loginRequest = (credentials) => {
    return authApiClient.post("/auth/login", credentials);
};