import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/authApi';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    // Initialize user from local storage to persist role during refreshes
    const [user, setUser] = useState(() => {
        const savedUser = localStorage.getItem('user');
        return savedUser ? JSON.parse(savedUser) : null;
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Hydrate user profile on load to ensure we have the latest details (especially ID)
    // useEffect(() => {
    //     const fetchProfile = async () => {
    //         const savedUser = localStorage.getItem('user');
    //         if (savedUser) {
    //             const parsedUser = JSON.parse(savedUser);
    //             if (parsedUser.token) {
    //                 try {
    //                     const response = await authApi.getCurrentUser();
    //                     if (response.user) {
    //                         const updatedUser = { ...parsedUser, ...response.user };
    //                         // Only update state if data actually changed (to avoid loops if we depended on user state)
    //                         if (JSON.stringify(updatedUser) !== JSON.stringify(parsedUser)) {
    //                             setUser(updatedUser);
    //                             localStorage.setItem('user', JSON.stringify(updatedUser));
    //                             console.log("User profile refreshed:", updatedUser);
    //                         }
    //                     }
    //                 } catch (err) {
    //                     console.error("Background profile fetch failed:", err);
    //                     // If token is invalid (401), we might want to logout, but let's be passive for now
    //                     if (err.response && err.response.status === 401) {
    //                         logout();
    //                     }
    //                 }
    //             }
    //         }
    //     };

    //     fetchProfile();
    // }, []); // Run once on mount

    const login = async (credentials) => {
        setLoading(true);
        setError(null);
        try {
            // 1. Login to get Token
            const loginResponse = await authApi.login(credentials);
            // Expected: { message: "Login successful", token: "...", role: "..." }

            if (loginResponse.token) {
                // If backend returns full user object, use it
                let finalUser;
                if (loginResponse.user) {
                    finalUser = { ...loginResponse.user, token: loginResponse.token };
                } else {
                    // Fallback to minimal user if backend hasn't updated yet
                    finalUser = { token: loginResponse.token, role: loginResponse.role };
                }

                // Save to state and local storage
                setUser(finalUser);
                localStorage.setItem('user', JSON.stringify(finalUser));

                // Optionally still fetch profile to ensure up-to-date data, 
                // but we already have what we need to proceed.
                return finalUser;
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
