import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Sidebar = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const location = useLocation();

  const menuItems = [
    { name: 'Dashboard', path: '/dashboard', icon: '📊' },
    { name: 'Products', path: '/products', icon: '📦' },
    { name: 'Categories', path: '/categories', icon: '🏷️' },
    { name: 'Suppliers', path: '/suppliers', icon: '🏢' },
    { name: 'Stock', path: '/stock', icon: '🔄' },
    { name: 'Alerts', path: '/alerts', icon: '🔔' },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Backdrop overlay for mobile */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-[#02040a]/80 backdrop-blur-xs z-40 md:hidden transition-opacity"
          onClick={onClose}
        />
      )}
      
      <aside
        className={`w-56 bg-[#090e1a] border-r border-[#141b30] p-4 flex flex-col justify-between h-screen fixed left-0 top-0 overflow-y-auto z-50 transition-transform duration-300 md:translate-x-0 ${
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
              <span className="font-display font-black text-sm text-white tracking-tight">StockIQ</span>
            </div>

            {/* Mobile Sidebar Close Button */}
            <button
              onClick={onClose}
              className="md:hidden p-1.5 hover:bg-slate-800/60 rounded-lg text-slate-400 hover:text-white transition-colors cursor-pointer"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Navigation Menu */}
          <nav className="space-y-1">
            {menuItems.map((item) => (
              <button
                key={item.path}
                onClick={() => {
                  navigate(item.path);
                  if (onClose) onClose();
                }}
                className={`w-full text-left px-3 py-2.5 rounded-lg text-xs font-semibold transition-all flex items-center justify-between border cursor-pointer ${
                  isActive(item.path)
                    ? 'bg-[#1e293b]/60 text-blue-400 border-slate-800'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/10 border-transparent'
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <span className="text-sm">{item.icon}</span>
                  <span>{item.name}</span>
                </div>
                {item.name === 'Alerts' && (
                  <span className="bg-[#ef4444] text-[9px] text-white px-1.5 py-0.5 rounded-full font-bold">18</span>
                )}
              </button>
            ))}
          </nav>
        </div>

        {/* Logout Button */}
        <button
          onClick={() => {
            logout();
            navigate('/login');
            if (onClose) onClose();
          }}
          className="w-full px-3 py-2.5 rounded-lg text-xs font-semibold text-slate-400 hover:text-slate-200 hover:bg-red-950/20 transition-all border border-slate-800/40 hover:border-red-500/30 flex items-center gap-2.5 justify-center cursor-pointer"
        >
          <span>🚪</span>
          Logout
        </button>
        </aside>
        </>
    );
  };

export default Sidebar;
