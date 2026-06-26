import React, { useState, useEffect } from 'react';
import Sidebar from './Sidebar';
import { useAuth } from '../../context/AuthContext';
import NotificationsPanel from '../notifications/NotificationsPanel';
import { notificationAPI } from '../../services/api';

const MainLayout = ({ children, title }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user } = useAuth();
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  const fetchUnreadCount = async () => {
    try {
      if (user) {
        const response = await notificationAPI.getUnreadCount();
        setUnreadCount(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch unread count:', err);
    }
  };

  useEffect(() => {
    fetchUnreadCount();
    const interval = setInterval(fetchUnreadCount, 30000);
    return () => clearInterval(interval);
  }, [user]);
  
  // Theme State (loads and saves to localStorage)
  const [theme, setTheme] = useState(() => {
    const saved = localStorage.getItem('theme');
    return saved ? saved : 'dark';
  });

  useEffect(() => {
    const root = window.document.documentElement;
    if (theme === 'light') {
      root.classList.remove('dark');
      root.classList.add('light');
    } else {
      root.classList.remove('light');
      root.classList.add('dark');
    }
    localStorage.setItem('theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  const getInitials = (name) => {
    if (!name) return 'US';
    const parts = name.trim().split(/\s+/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
  };

  // Theme switch button component
  const ThemeToggleBtn = () => (
    <button
      onClick={toggleTheme}
      className="p-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-900/60 dark:hover:bg-slate-800/80 rounded-xl text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-all cursor-pointer border border-slate-200/40 dark:border-slate-800/40"
      title={theme === 'light' ? 'Switch to Dark Mode' : 'Switch to Light Mode'}
    >
      {theme === 'light' ? (
        // Sun Icon
        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364-6.364l-.707.707M6.343 17.657l-.707.707m0-12.728l.707.707m12.728 12.728l.707.707M12 8a4 4 0 100 8 4 4 0 000-8z" />
        </svg>
      ) : (
        // Moon Icon
        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
        </svg>
      )}
    </button>
  );

  return (
    <div className="bg-slate-50 dark:bg-[#050814] text-slate-800 dark:text-white min-h-screen font-sans relative overflow-x-hidden bg-[linear-gradient(to_right,#e2e8f0_1px,transparent_1px),linear-gradient(to_bottom,#e2e8f0_1px,transparent_1px)] dark:bg-[linear-gradient(to_right,#0c1224_1px,transparent_1px),linear-gradient(to_bottom,#0c1224_1px,transparent_1px)] bg-[size:4rem_4rem] transition-colors duration-250">
      
      {/* Ambient Blur Lights */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[80%] h-[400px] bg-gradient-to-b from-blue-500/10 via-indigo-500/5 to-transparent dark:from-blue-500/5 dark:via-indigo-500/2 rounded-full blur-[100px] pointer-events-none z-0" />
      
      {/* Mobile Top Header */}
      <header className="sticky top-0 z-40 backdrop-blur-md bg-white/85 dark:bg-[#050814]/85 border-b border-slate-200/60 dark:border-slate-800/40 px-6 h-16 flex items-center justify-between md:hidden shadow-lg shadow-[#050814]/5 dark:shadow-[#050814]/20 transition-colors">
        <div className="flex items-center gap-3">
          {/* Hamburger button to open mobile menu */}
          <button
            onClick={() => setSidebarOpen(true)}
            className="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800/60 rounded-lg text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-colors cursor-pointer"
          >
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          
          <div className="flex items-center gap-2">
            <div className="bg-blue-600 p-1.5 rounded-lg shadow-md shadow-blue-500/25">
              <svg className="w-3.5 h-3.5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                <rect x="4" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                <rect x="14" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                <rect x="4" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                <rect x="14" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
              </svg>
            </div>
            <div>
              <span className="font-display font-black text-sm text-slate-800 dark:text-white tracking-tight block leading-none">StockIQ</span>
              <span className="text-[8px] text-slate-500 font-mono tracking-wider uppercase block mt-0.5 font-bold">Smart Inventory</span>
            </div>
          </div>
        </div>
        
        <div className="flex items-center gap-3">
          <ThemeToggleBtn />
          <button
            onClick={() => setNotificationsOpen(true)}
            className="p-1.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-900/60 dark:hover:bg-slate-800/80 rounded-xl text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-all relative cursor-pointer border border-slate-200/40 dark:border-slate-800/40"
            title="Notifications"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            {unreadCount > 0 && (
              <span className="absolute -top-1.5 -right-1.5 bg-red-500 text-white text-[9px] font-extrabold px-1.5 py-0.5 rounded-full min-w-5 text-center flex items-center justify-center border border-white dark:border-[#050814]">
                {unreadCount > 99 ? '99+' : unreadCount}
              </span>
            )}
          </button>
          <div className="w-7 h-7 rounded-full bg-gradient-to-tr from-blue-500 to-indigo-600 text-[10px] font-bold text-white flex items-center justify-center border border-blue-400/20 shadow-md shadow-blue-500/10">
            {getInitials(user?.fullName)}
          </div>
        </div>
      </header>

      <div className="flex">
        {/* Sidebar */}
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
        
        {/* Main Content */}
        <main className="flex-1 md:pl-56 min-h-screen flex flex-col">
          {/* Desktop/Tablet Top Header */}
          <header className="hidden md:flex items-center justify-between px-8 h-16 border-b border-slate-200/60 dark:border-slate-800/40 bg-white/85 dark:bg-[#050814]/85 backdrop-blur-md sticky top-0 z-30 transition-colors">
            <h1 className="text-base font-bold text-slate-800 dark:text-white tracking-tight">{title}</h1>
            <div className="flex items-center gap-4">
              {/* Theme Toggle Button */}
              <ThemeToggleBtn />

              {/* Notification Bell */}
              <button
                onClick={() => setNotificationsOpen(true)}
                className="p-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-900/60 dark:hover:bg-slate-800/80 rounded-xl text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-all relative cursor-pointer border border-slate-200/40 dark:border-slate-800/40"
                title="Notifications"
              >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
                {unreadCount > 0 && (
                  <span className="absolute -top-1.5 -right-1.5 bg-red-500 text-white text-[9px] font-extrabold px-1.5 py-0.5 rounded-full min-w-5 text-center flex items-center justify-center border border-white dark:border-[#050814]">
                    {unreadCount > 99 ? '99+' : unreadCount}
                  </span>
                )}
              </button>

              {/* User Avatar */}
              <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-blue-500 to-indigo-600 text-xs font-bold text-white flex items-center justify-center border border-blue-400/20 shadow-md shadow-blue-500/10">
                {getInitials(user?.fullName)}
              </div>
            </div>
          </header>
          
          <div className="relative z-10 flex-1">
            {children}
          </div>
        </main>
      </div>
      <NotificationsPanel
        isOpen={notificationsOpen}
        onClose={() => setNotificationsOpen(false)}
        onUnreadUpdate={fetchUnreadCount}
      />
    </div>
  );
};

export default MainLayout;
