import React, { useState } from 'react';
import Sidebar from './Sidebar';
import { useAuth } from '../../context/AuthContext';

const MainLayout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user } = useAuth();

  const getInitials = (name) => {
    if (!name) return 'US';
    const parts = name.trim().split(/\s+/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
  };

  return (
    <div className="bg-[#050814] text-white min-h-screen font-sans relative overflow-x-hidden bg-[linear-gradient(to_right,#0c1224_1px,transparent_1px),linear-gradient(to_bottom,#0c1224_1px,transparent_1px)] bg-[size:4rem_4rem]">
      {/* Ambient Blur Lights */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[80%] h-[400px] bg-gradient-to-b from-blue-500/5 via-indigo-500/2 to-transparent rounded-full blur-[100px] pointer-events-none z-0" />
      
      {/* Mobile Top Header */}
      <header className="sticky top-0 z-40 backdrop-blur-md bg-[#050814]/85 border-b border-slate-800/40 px-6 h-16 flex items-center justify-between md:hidden shadow-lg shadow-[#050814]/20">
        <div className="flex items-center gap-3">
          {/* Hamburger button to open mobile menu */}
          <button
            onClick={() => setSidebarOpen(true)}
            className="p-1.5 hover:bg-slate-800/60 rounded-lg text-slate-400 hover:text-white transition-colors cursor-pointer"
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
            <span className="font-display font-black text-sm text-white tracking-tight">StockIQ</span>
          </div>
        </div>
        
        <div className="w-7 h-7 rounded-full bg-gradient-to-tr from-blue-500 to-indigo-600 text-[10px] font-bold text-white flex items-center justify-center border border-blue-400/20 shadow-md shadow-blue-500/10">
          {getInitials(user?.fullName)}
        </div>
      </header>

      <div className="flex">
        {/* Sidebar */}
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
        
        {/* Main Content */}
        <main className="flex-1 md:pl-56 min-h-screen">
          <div className="relative z-10">{children}</div>
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
