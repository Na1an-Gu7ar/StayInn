import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    // Initialize user from local storage to persist role during refreshes
    const [user, setUser] = useState(() => {
        const savedUser = localStorage.getItem('user');
        return savedUser ? JSON.parse(savedUser) : null;
    });

    const login = (role) => {
        // Simulating a real user object
        const mockUser = {
            id: '123',
            name: role === 'ADMIN' ? 'Admin User' : 'Normal User',
            email: role === 'ADMIN' ? 'admin@bookstay.com' : 'user@bookstay.com',
            role: role, // 'ADMIN' or 'USER'
            avatar: role === 'ADMIN' ? 'https://i.pravatar.cc/150?u=admin' : 'https://i.pravatar.cc/150?u=user'
        };
        setUser(mockUser);
        localStorage.setItem('user', JSON.stringify(mockUser));
    };

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
        logout,
        hasRole,
        isAuthenticated: !!user,
        isAdmin: user?.role === 'ADMIN'
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
