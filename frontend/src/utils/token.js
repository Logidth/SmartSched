import { jwtDecode } from "jwt-decode";

export const saveToken = (token) => {
    localStorage.setItem("token", token);
};

export const removeToken = () => {
    localStorage.removeItem("token");
};

export const getToken = () => {
    return localStorage.getItem("token");
};

export const getUser = () => {

    const token = getToken();

    if (!token) return null;

    try {
        return jwtDecode(token);
    } catch {
        return null;
    }
};

export const isLoggedIn = () => {

    const token = getToken();

    return token !== null;
};