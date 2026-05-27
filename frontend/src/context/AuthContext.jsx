import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

const DEFAULT_USERS = [
  {
    id: 'default-admin',
    fullName: 'Admin User',
    companyName: 'StockIQ Solutions',
    email: 'admin@company.com',
    password: 'password',
  }
];

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Initialize and load user session from localStorage
  useEffect(() => {
    // Ensure default users list exists in localStorage
    if (!localStorage.getItem('stockiq_users')) {
      localStorage.setItem('stockiq_users', JSON.stringify(DEFAULT_USERS));
    }

    const savedToken = localStorage.getItem('stockiq_token');
    const savedUser = localStorage.getItem('stockiq_current_user');

    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  // Helper to fetch all registered users
  const getUsersList = () => {
    const usersStr = localStorage.getItem('stockiq_users');
    return usersStr ? JSON.parse(usersStr) : DEFAULT_USERS;
  };

  // Login handler
  const login = async (email, password) => {
    setLoading(true);
    try {
      // Simulate network request delay
      await new Promise((resolve) => setTimeout(resolve, 600));

      const users = getUsersList();
      const foundUser = users.find(
        (u) => u.email.toLowerCase() === email.toLowerCase() && u.password === password
      );

      if (!foundUser) {
        throw new Error('Invalid email or password');
      }

      const mockToken = `mock-jwt-token-${Math.random().toString(36).substr(2)}`;
      
      // Sanitized user object to expose (omit password)
      const userProfile = {
        id: foundUser.id,
        fullName: foundUser.fullName,
        companyName: foundUser.companyName,
        email: foundUser.email,
      };

      localStorage.setItem('stockiq_token', mockToken);
      localStorage.setItem('stockiq_current_user', JSON.stringify(userProfile));

      setToken(mockToken);
      setUser(userProfile);

      return userProfile;
    } finally {
      setLoading(false);
    }
  };

  // Register handler
  const register = async (fullName, companyName, email, password) => {
    setLoading(true);
    try {
      await new Promise((resolve) => setTimeout(resolve, 800));

      const users = getUsersList();
      const emailExists = users.some(
        (u) => u.email.toLowerCase() === email.toLowerCase()
      );

      if (emailExists) {
        throw new Error('An account with this email already exists');
      }

      const newUser = {
        id: `user-${Date.now()}`,
        fullName,
        companyName,
        email,
        password,
      };

      // Save to virtual database
      users.push(newUser);
      localStorage.setItem('stockiq_users', JSON.stringify(users));

      // Auto sign-in
      const mockToken = `mock-jwt-token-${Math.random().toString(36).substr(2)}`;
      const userProfile = {
        id: newUser.id,
        fullName: newUser.fullName,
        companyName: newUser.companyName,
        email: newUser.email,
      };

      localStorage.setItem('stockiq_token', mockToken);
      localStorage.setItem('stockiq_current_user', JSON.stringify(userProfile));

      setToken(mockToken);
      setUser(userProfile);

      return userProfile;
    } finally {
      setLoading(false);
    }
  };

  // Logout handler
  const logout = () => {
    localStorage.removeItem('stockiq_token');
    localStorage.removeItem('stockiq_current_user');
    setToken(null);
    setUser(null);
  };

  // Password reset simulation
  const resetPassword = async (email) => {
    setLoading(true);
    try {
      await new Promise((resolve) => setTimeout(resolve, 800));
      const users = getUsersList();
      const emailExists = users.some(
        (u) => u.email.toLowerCase() === email.toLowerCase()
      );

      if (!emailExists) {
        throw new Error('No account found with this email address');
      }

      return true;
    } finally {
      setLoading(false);
    }
  };

  // OAuth simulated login
  const loginWithOAuth = async (provider) => {
    setLoading(true);
    try {
      await new Promise((resolve) => setTimeout(resolve, 600));
      const mockToken = `mock-oauth-token-${provider}-${Math.random().toString(36).substr(2)}`;
      
      const userProfile = {
        id: `oauth-${provider}-${Date.now()}`,
        fullName: `${provider.charAt(0).toUpperCase() + provider.slice(1)} Partner`,
        companyName: 'StockIQ Global Network',
        email: `${provider.toLowerCase()}@stockiq.com`,
      };

      localStorage.setItem('stockiq_token', mockToken);
      localStorage.setItem('stockiq_current_user', JSON.stringify(userProfile));

      setToken(mockToken);
      setUser(userProfile);
      return userProfile;
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        register,
        logout,
        resetPassword,
        loginWithOAuth,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
