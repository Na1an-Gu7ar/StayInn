import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/authApi';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    // Initialize user from local storage to persist role during refreshes
    const [user, setUser] = useState(() => {
        const savedUser = localStorage.getItem('user');
        return savedUser ? JSON.parse(savedUser) : null;
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const login = async (credentials) => {
        setLoading(true);
        setError(null);
        try {
            // 1. Login to get Token
            const loginResponse = await authApi.login(credentials);
            // Expected: { message: "Login successful", token: "...", role: "..." }

            if (loginResponse.token) {
                // Temporary user object with token
                const tempUser = { token: loginResponse.token, role: loginResponse.role };

                // Save temporarily to allow the next request to have the token
                localStorage.setItem('user', JSON.stringify(tempUser));

                // 2. Fetch full user profile
                try {
                    const profileResponse = await authApi.getCurrentUser();
                    // Expected: { message: "User profile", user: { id, name, email, mobile, role, ... } }

                    if (profileResponse.user) {
                        const finalUser = { ...profileResponse.user, token: loginResponse.token };
                        setUser(finalUser);
                        localStorage.setItem('user', JSON.stringify(finalUser));
                        return finalUser;
                    }
                } catch (profileError) {
                    console.warn("Could not fetch full profile, using login data", profileError);
                    // Fallback if profile fetch fails (e.g. backend error), just use what we have
                    setUser(tempUser);
                    return tempUser;
                }
            } else {
                throw new Error("Invalid response from server");
            }

        } catch (err) {
            console.error("Login Failed:", err);
            setError(err.response?.data?.message || "Login failed");
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const register = async (userData) => {
        setLoading(true);
        setError(null);
        try {
            const data = await authApi.register(userData);
            return data;
        } catch (err) {
            console.error("Registration Failed:", err);
            setError(err.response?.data?.message || "Registration failed");
            throw err;
        } finally {
            setLoading(false);
        }
    }

    const logout = () => {
        setUser(null);
        localStorage.removeItem('user');
    };

    const hasRole = (allowedRoles) => {
        if (!user) return false;
        if (!Array.isArray(allowedRoles)) return user.role === allowedRoles;
        return allowedRoles.includes(user.role);
    };

    const value = {
        user,
        login,
        register,
        logout,
        hasRole,
        isAuthenticated: !!user,
        isAdmin: user?.role === 'ADMIN',
        loading,
        error
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
