import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // On app start — restore session from localStorage
  useEffect(() => {
    const savedToken = localStorage.getItem('stockiq_token');
    if (savedToken) {
      setToken(savedToken);
      // Fetch real user profile from backend using the saved token
      authAPI.getCurrentUser()
        .then((res) => {
          setUser(res.data);
        })
        .catch(() => {
          // Token invalid/expired — clear session
          localStorage.removeItem('stockiq_token');
          setToken(null);
          setUser(null);
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  // Login handler — calls real backend
  const login = async (email, password) => {
    try {
      const res = await authAPI.login({ email, password });
      const { token: jwt } = res.data;

      // Save token to localStorage first so the interceptor picks it up
      localStorage.setItem('stockiq_token', jwt);
      setToken(jwt);

      // Fetch full profile with the new token
      const profileRes = await authAPI.getCurrentUser();
      const userProfile = profileRes.data;
      setUser(userProfile);
      return userProfile;
    } catch (err) {
      // Clear any partial state on failure
      localStorage.removeItem('stockiq_token');
      setToken(null);
      setUser(null);
      throw err;
    }
  };

  // Register handler — calls real backend
  const register = async (fullName, companyName, email, password) => {
    try {
      // Register the user (returns 201)
      await authAPI.register({ fullName, companyName, email, password });

      // Auto-login after successful registration
      const loginRes = await authAPI.login({ email, password });
      const { token: jwt } = loginRes.data;

      // Save token to localStorage first so the interceptor picks it up
      localStorage.setItem('stockiq_token', jwt);
      setToken(jwt);

      // Fetch full profile
      const profileRes = await authAPI.getCurrentUser();
      const userProfile = profileRes.data;
      setUser(userProfile);
      return userProfile;
    } catch (err) {
      // Clear any partial state on failure
      localStorage.removeItem('stockiq_token');
      setToken(null);
      setUser(null);
      throw err;
    }
  };

  // Logout handler — clears all session data
  const logout = () => {
    localStorage.removeItem('stockiq_token');
    setToken(null);
    setUser(null);
  };

  // Password reset — calls real backend API
  const resetPassword = async (email, newPassword) => {
    await authAPI.forgotPassword({ email, newPassword });
    return true;
  };

  // Profile update — calls real backend API
  const updateProfile = async (fullName, companyName, email) => {
    try {
      const res = await authAPI.updateProfile({ fullName, companyName, email });
      const { token: jwt, user: userProfile } = res.data;

      localStorage.setItem('stockiq_token', jwt);
      setToken(jwt);
      setUser(userProfile);

      return userProfile;
    } catch (err) {
      throw err;
    }
  };

  const loginWithGoogle = async (idToken) => {
    try {
      const res = await authAPI.loginWithGoogle(idToken);
      const { token: jwt } = res.data;

      localStorage.setItem('stockiq_token', jwt);
      setToken(jwt);

      const profileRes = await authAPI.getCurrentUser();
      const userProfile = profileRes.data;
      setUser(userProfile);
      return userProfile;
    } catch (err) {
      localStorage.removeItem('stockiq_token');
      setToken(null);
      setUser(null);
      throw err;
    }
  };

  const loginWithGithub = async (code) => {
    try {
      const res = await authAPI.loginWithGithub(code);
      const { token: jwt } = res.data;

      localStorage.setItem('stockiq_token', jwt);
      setToken(jwt);

      const profileRes = await authAPI.getCurrentUser();
      const userProfile = profileRes.data;
      setUser(userProfile);
      return userProfile;
    } catch (err) {
      localStorage.removeItem('stockiq_token');
      setToken(null);
      setUser(null);
      throw err;
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
        updateProfile,
        loginWithGoogle,
        loginWithGithub,
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
