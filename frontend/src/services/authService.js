import { loginRequest } from "../api/authApi";

export const login = async (username, password) => {

    const response = await loginRequest({
        username,
        password
    });

    return response.data;
};