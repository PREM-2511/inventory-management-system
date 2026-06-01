import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isLandingPage = location.pathname === '/';
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { token } = useAuth();

  return (
    <header className="sticky top-0 z-50 backdrop-blur-md border-b border-slate-800/40 bg-[#050814]/85">
      <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
        {/* Logo */}
        <div
          className="flex items-center gap-3 cursor-pointer hover:opacity-90 transition-opacity"
          onClick={() => navigate('/')}
        >
          {/* 2x2 Grid Icon Logo */}
          <div className="bg-gradient-to-br from-blue-500 to-blue-600 p-2.5 rounded-2xl shadow-lg shadow-blue-500/25 border border-blue-400/20">
            <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <rect x="4" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
              <rect x="14" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
              <rect x="4" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
              <rect x="14" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
            </svg>
          </div>
          <div>
            <span className="text-xl font-display font-black tracking-tight block text-white">StockIQ</span>
            <span className="text-[9px] text-slate-500 font-mono tracking-[0.15em] uppercase block -mt-1 font-bold">Smart Inventory</span>
          </div>
        </div>

        {/* Navigation Links - Only on Landing Page */}
        {isLandingPage && (
          <nav className="hidden md:flex items-center gap-8 text-sm font-semibold text-slate-400">
            <a href="#features" className="hover:text-white transition-colors">Features</a>
            <a href="#process" className="hover:text-white transition-colors">How It Works</a>
            <a href="#preview" className="hover:text-white transition-colors">System</a>
            <a href="#pricing" className="hover:text-white transition-colors">Pricing</a>
            <a href="#contact" className="hover:text-white transition-colors">Contact</a>
          </nav>
        )}

        {/* Action Buttons - Desktop only */}
        <div className="hidden md:flex items-center gap-4">
          {token ? (
            <button
              onClick={() => navigate('/dashboard')}
              className="flex items-center gap-3 bg-gradient-to-r from-blue-600 to-indigo-600 px-6 py-2.5 rounded-xl text-xs font-black leading-tight hover:from-blue-500 hover:to-indigo-500 shadow-lg shadow-blue-500/20 border border-blue-400/20 hover:shadow-blue-500/30 transition-all cursor-pointer h-12 text-white"
            >
              <span>Dashboard</span>
              <span className="text-sm font-black">&rarr;</span>
            </button>
          ) : (
            <>
              {/* Sign In */}
              <button
                onClick={() => navigate('/login')}
                className="flex flex-col items-center justify-center border border-slate-800/80 bg-[#080d1a]/80 px-5 py-2.5 text-xs font-black leading-tight rounded-xl hover:bg-slate-900/60 hover:border-slate-700/80 transition-all cursor-pointer h-12 text-slate-200"
              >
                <span>Sign</span>
                <span>In</span>
              </button>
              {/* Get Started */}
              <button
                onClick={() => navigate('/register')}
                className="flex items-center gap-3 bg-gradient-to-r from-blue-600 to-indigo-600 px-5 py-2.5 rounded-xl text-xs font-black leading-tight hover:from-blue-500 hover:to-indigo-500 shadow-lg shadow-blue-500/20 border border-blue-400/20 hover:shadow-blue-500/30 transition-all cursor-pointer h-12 text-white"
              >
                <div className="flex flex-col text-left">
                  <span>Get</span>
                  <span>Started</span>
                </div>
                <span className="text-sm font-black">&rarr;</span>
              </button>
            </>
          )}
        </div>

        {/* Hamburger Menu Toggle Button - Mobile only */}
        <button
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          className="md:hidden p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800/40 transition-colors cursor-pointer"
        >
          {mobileMenuOpen ? (
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          ) : (
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          )}
        </button>
      </div>

      {/* Mobile sliding Menu Drawer */}
      <div
        className={`fixed inset-y-0 left-0 z-50 w-full max-w-sm bg-[#090e1a] border-r border-[#141b30] p-6 shadow-2xl transition-transform duration-300 md:hidden flex flex-col justify-between ${
          mobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="space-y-6">
          <div className="flex items-center justify-between border-b border-slate-800/40 pb-5">
            <div className="flex items-center gap-2">
              <div className="bg-blue-600 p-1.5 rounded-lg">
                <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                  <rect x="4" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="14" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="4" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="14" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                </svg>
              </div>
              <div>
                <span className="font-display font-black text-sm text-white tracking-tight block leading-none">StockIQ</span>
                <span className="text-[8px] text-slate-500 font-mono tracking-wider uppercase block mt-0.5 font-bold">Smart Inventory</span>
              </div>
            </div>
            <button
              onClick={() => setMobileMenuOpen(false)}
              className="p-1.5 hover:bg-slate-800/60 rounded-lg text-slate-400 hover:text-white cursor-pointer"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Navigation Links for Mobile */}
                    <nav className="flex flex-col gap-4 text-sm font-semibold text-slate-400">
            <a
              href="#features"
              onClick={() => setMobileMenuOpen(false)}
              className="hover:text-white transition-colors py-2 border-b border-slate-900"
            >
              Features
            </a>
            <a
              href="#process"
              onClick={() => setMobileMenuOpen(false)}
              className="hover:text-white transition-colors py-2 border-b border-slate-900"
            >
              How It Works
            </a>
            <a
              href="#preview"
              onClick={() => setMobileMenuOpen(false)}
              className="hover:text-white transition-colors py-2 border-b border-slate-900"
            >
              System
            </a>
            <a
              href="#pricing"
              onClick={() => setMobileMenuOpen(false)}
              className="hover:text-white transition-colors py-2 border-b border-slate-900"
            >
              Pricing
            </a>
            <a
              href="#contact"
              onClick={() => setMobileMenuOpen(false)}
              className="hover:text-white transition-colors py-2 border-b border-slate-900"
            >
              Contact
            </a>
          </nav>

        </div>

        {/* Action Buttons for Mobile */}
        <div className="flex flex-col gap-3 pt-6 border-t border-slate-800/40">
          {token ? (
            <button
              onClick={() => {
                navigate('/dashboard');
                setMobileMenuOpen(false);
              }}
              className="w-full py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-center text-xs font-bold rounded-xl hover:from-blue-500 hover:to-indigo-500 shadow-lg shadow-blue-500/20 border border-blue-400/20 transition-all cursor-pointer text-white flex items-center justify-center gap-1"
            >
              Dashboard &rarr;
            </button>
          ) : (
            <>
              <button
                onClick={() => {
                  navigate('/login');
                  setMobileMenuOpen(false);
                }}
                className="w-full py-3 border border-slate-800 bg-[#080d1a] text-center text-xs font-bold rounded-xl hover:bg-slate-900/60 hover:border-slate-700/80 transition-all cursor-pointer text-slate-200"
              >
                Sign In
              </button>
              <button
                onClick={() => {
                  navigate('/register');
                  setMobileMenuOpen(false);
                }}
                className="w-full py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-center text-xs font-bold rounded-xl hover:from-blue-500 hover:to-indigo-500 shadow-lg shadow-blue-500/20 border border-blue-400/20 transition-all cursor-pointer text-white flex items-center justify-center gap-1"
              >
                Get Started &rarr;
              </button>
            </>
          )}
        </div>
      </div>
      
      {/* Backdrop overlay for mobile drawer */}
      {mobileMenuOpen && (
        <div
          className="fixed inset-0 bg-[#02040a]/80 backdrop-blur-xs z-40 md:hidden"
          onClick={() => setMobileMenuOpen(false)}
        />
      )}
    </header>
  );
};

export default Navbar;
