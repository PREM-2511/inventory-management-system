import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingScreen from './LoadingScreen';

const GuestRoute = ({ children }) => {
  const { token, loading } = useAuth();

  if (loading) {
    return <LoadingScreen message="Redirecting to your dashboard..." />;
  }

  if (token) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default GuestRoute;
