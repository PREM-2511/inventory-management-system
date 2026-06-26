import axios from 'axios';

const API_BASE_URL = '/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Decode JWT payload (base64) without a library
const getEmailFromToken = (token) => {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return decoded.sub || decoded.email || '';
  } catch {
    return '';
  }
};

// Request Interceptor — attaches JWT token and X-Correlation-Id (user email)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('stockiq_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      const email = getEmailFromToken(token);
      if (email) {
        config.headers['X-Correlation-Id'] = email;
        config.headers['X-User-Email'] = email;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor — extract error messages from backend
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const data = error?.response?.data;
    const message =
      data?.message ||
      data?.title ||
      data ||
      error?.message ||
      'An unexpected error occurred';
    return Promise.reject(new Error(typeof message === 'string' ? message : JSON.stringify(message)));
  }
);

// Auth API
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (credentials) => api.post('/auth/login', credentials),
  getCurrentUser: () => api.get('/auth/me'),
  updateProfile: (data) => api.put('/auth/me', data),
  forgotPassword: (data) => api.post('/auth/forgot-password', data),
  loginWithGoogle: (idToken) => api.post('/auth/oauth/google', { idToken }),
  loginWithGithub: (code) => api.post('/auth/oauth/github', { code }),
};

// Dashboard API
export const dashboardAPI = {
  getSummary: () => api.get('/dashboard/summary'),
  getCategoryDistribution: () => api.get('/dashboard/categories'),
};

// Category API
export const categoryAPI = {
  getAll: (params) => api.get('/categories', { params }),
  getById: (id) => api.get(`/categories/${id}`),
  create: (data) => api.post('/categories', data),
  update: (id, data) => api.put(`/categories/${id}`, data),
  delete: (id) => api.delete(`/categories/${id}`),
};

// Product API
export const productAPI = {
  getAll: (params) => api.get('/products', { params }),
  getById: (id) => api.get(`/products/${id}`),
  create: (data) => api.post('/products', data),
  update: (id, data) => api.put(`/products/${id}`, data),
  delete: (id) => api.delete(`/products/${id}`),
};

// Supplier API
export const supplierAPI = {
  getAll: (params) => api.get('/suppliers', { params }),
  getById: (id) => api.get(`/suppliers/${id}`),
  create: (data) => api.post('/suppliers', data),
  update: (id, data) => api.put(`/suppliers/${id}`, data),
  delete: (id) => api.delete(`/suppliers/${id}`),
  getStatuses: () => api.get('/suppliers/statuses'),
};

// Stock API
export const stockAPI = {
  logMovement: (data) => api.post('/stock/move', data),
  getHistory: () => api.get('/stock/history'),
  getLowStockAlerts: () => api.get('/stock/alerts'),
};

// Notification API
export const notificationAPI = {
  getAll: (params) => api.get('/notifications', { params }),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  create: (data) => api.post('/notifications', data),
  markRead: (id) => api.put(`/notifications/${id}/read`),
  markAllRead: () => api.put('/notifications/read-all'),
  delete: (id) => api.delete(`/notifications/${id}`),
};

export default api;