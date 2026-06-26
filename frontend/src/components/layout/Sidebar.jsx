import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Sidebar = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const location = useLocation();

  const menuItems = [
    { name: 'Dashboard', path: '/dashboard' },
    { name: 'Products', path: '/products' },
    { name: 'Categories', path: '/categories' },
    { name: 'Suppliers', path: '/suppliers' },
    { name: 'Stock', path: '/stock' },
    { name: 'Low Stock', path: '/alerts' },
    { name: 'Account', path: '/account' },
  ];

  const renderIcon = (name, active) => {
    const colorClass = active ? 'text-blue-400' : 'text-slate-400 group-hover:text-slate-200';
    switch (name) {
      case 'Dashboard':
        return (
          <svg className={`w-4 h-4 ${colorClass}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <rect x="3" y="3" width="7" height="7" rx="1.5" />
            <rect x="14" y="3" width="7" height="7" rx="1.5" />
            <rect x="14" y="14" width="7" height="7" rx="1.5" />
            <rect x="3" y="14" width="7" height="7" rx="1.5" />
          </svg>
        );
      case 'Products':
        return (
          <svg className={`w-4 h-4 ${colorClass}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        );
      case 'Categories':
        return (
          <svg className={`w-4 h-4 ${colorClass}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
          </svg>
        );
      case 'Suppliers':
        return (
          <svg className={`w-4 h-4 ${colorClass}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        );
      case 'Stock':
        return (
          <svg className={`w-4 h-4 ${colorClass}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 002 2h2a2 2 0 002-2z" />
          </svg>
        );
      case 'Low Stock':
        return (
          <svg className={`w-4 h-4 ${colorClass}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
        );
      case 'Account':
        return (
          <svg className={`w-4 h-4 ${colorClass}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
        );
      default:
        return null;
    }
  };

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Backdrop overlay for mobile */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-[#02040a]/80 dark:bg-[#02040a]/80 bg-slate-900/40 backdrop-blur-xs z-40 md:hidden transition-opacity"
          onClick={onClose}
        />
      )}
      
      <aside
        className={`w-56 bg-white dark:bg-[#090e1a] border-r border-slate-200/60 dark:border-[#141b30] p-4 flex flex-col justify-between h-screen fixed left-0 top-0 overflow-y-auto z-50 transition-all duration-300 md:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="space-y-2">
          {/* Logo with Close button on mobile */}
          <div className="flex items-center justify-between px-1 mb-6 mt-1">
            <div
              className="flex items-center gap-2 cursor-pointer hover:opacity-90 transition-opacity"
              onClick={() => {
                navigate('/dashboard');
                if (onClose) onClose();
              }}
            >
              {/* 2x2 Grid Icon Logo */}
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

            {/* Mobile Sidebar Close Button */}
            <button
              onClick={onClose}
              className="md:hidden p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800/60 rounded-lg text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-colors cursor-pointer"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Navigation Menu */}
          <nav className="space-y-1">
            {menuItems.map((item) => {
              const active = isActive(item.path);
              return (
                <button
                  key={item.path}
                  onClick={() => {
                    navigate(item.path);
                    if (onClose) onClose();
                  }}
                  className={`w-full text-left px-3 py-2.5 rounded-lg text-xs font-semibold transition-all flex items-center justify-between border cursor-pointer group ${
                    active
                      ? 'bg-blue-50/70 dark:bg-[#1e293b]/60 text-blue-600 dark:text-blue-400 border-blue-100/50 dark:border-slate-800/50 shadow-sm dark:shadow-inner font-bold'
                      : 'text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800/10 border-transparent'
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    {renderIcon(item.name, active)}
                    <span>{item.name}</span>
                  </div>
                  {item.name === 'Low Stock' && (
                    <span className="w-1.5 h-1.5 bg-red-500 rounded-full animate-pulse shadow-sm shadow-red-500/50" />
                  )}
                </button>
              );
            })}
          </nav>
        </div>

        {/* Logout Button */}
        <button
          onClick={() => {
            logout();
            navigate('/login');
            if (onClose) onClose();
          }}
          className="w-full px-3 py-2.5 rounded-lg text-xs font-semibold text-slate-500 dark:text-slate-400 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50/50 dark:hover:bg-red-950/20 transition-all border border-slate-200 dark:border-slate-800/40 hover:border-red-200 dark:hover:border-red-500/30 flex items-center gap-2.5 justify-center cursor-pointer"
        >
          <span>🚪</span>
          Logout
        </button>
      </aside>
    </>
  );
};

export default Sidebar;
