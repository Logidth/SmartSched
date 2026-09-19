import { createContext, useContext, useEffect, useState } from "react";
import { login as loginService } from "../services/authService";
import {
  saveToken,
  removeToken,
  getToken,
  getUser,
} from "../utils/token";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {

    const token = getToken();

    if (token) {

    setUser({
        token,
        userId: localStorage.getItem("userId"),
        username: localStorage.getItem("username"),
        role: localStorage.getItem("role"),
        firstLogin: localStorage.getItem("firstLogin") === "true",
        branchId: localStorage.getItem("branchId")
            ? Number(localStorage.getItem("branchId"))
            : null,
        branchName: localStorage.getItem("branchName")
    });

}
    

    setLoading(false);

}, []);

  const login = async (username, password) => {

    const response = await loginService(username, password);

    saveToken(response.token);

    localStorage.setItem("userId", response.userId);
    localStorage.setItem("username", response.username);
    localStorage.setItem("role", response.role);
    localStorage.setItem("firstLogin", response.firstLogin);

    // Department context — only PRINCIPAL has no branch.
    // ADMIN and HOD are always tied to a branch.
    if (response.branchId) {
        localStorage.setItem("branchId", response.branchId);
        localStorage.setItem("branchName", response.branchName || "");
    } else {
        localStorage.removeItem("branchId");
        localStorage.removeItem("branchName");
    }

    setUser({
        userId: response.userId,
        username: response.username,
        role: response.role,
        firstLogin: response.firstLogin,
        branchId: response.branchId || null,
        branchName: response.branchName || null
    });

    return response;
};
  const logout = () => {

    removeToken();

    localStorage.removeItem("userId");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    localStorage.removeItem("firstLogin");
    localStorage.removeItem("branchId");
    localStorage.removeItem("branchName");

    setUser(null);

};

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        logout,
        loading,
        isAuthenticated: !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);