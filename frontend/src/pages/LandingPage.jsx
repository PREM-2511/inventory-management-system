import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const LandingPage = () => {
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="bg-[#050814] text-white min-h-screen font-sans relative overflow-x-hidden selection:bg-blue-600 selection:text-white bg-[linear-gradient(to_right,#0c1224_1px,transparent_1px),linear-gradient(to_bottom,#0c1224_1px,transparent_1px)] bg-[size:4rem_4rem]">
      {/* Decorative Blur Ambient Lights */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[80%] h-[550px] bg-gradient-to-b from-blue-500/10 via-indigo-500/5 to-transparent rounded-full blur-[120px] pointer-events-none z-0" />
      <div className="absolute top-[800px] left-1/4 w-[400px] h-[400px] bg-indigo-500/5 rounded-full blur-[100px] pointer-events-none z-0" />
      <div className="absolute top-[1400px] right-1/4 w-[500px] h-[500px] bg-blue-500/5 rounded-full blur-[120px] pointer-events-none z-0" />

      {/* --- NAVBAR --- */}
      <header className="sticky top-0 z-50 backdrop-blur-md border-b border-slate-800/40 bg-[#050814]/85">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <div className="flex items-center gap-3">
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
              <span className="text-[9px] text-slate-500 font-mono tracking-[0.2em] uppercase block -mt-1 font-bold">Inventory</span>
            </div>
          </div>

          <nav className="hidden md:flex items-center gap-8 text-sm font-semibold text-slate-400">
            <a href="#features" className="hover:text-white transition-colors">Features</a>
            <a href="#process" className="hover:text-white transition-colors">How It Works</a>
            <a href="#preview" className="hover:text-white transition-colors">System</a>
            <a href="#pricing" className="hover:text-white transition-colors">Pricing</a>
            <a href="#contact" className="hover:text-white transition-colors">Contact</a>
          </nav>

          {/* Action Buttons - Desktop Only */}
          <div className="hidden md:flex items-center gap-4">
            {/* Sleek stacked dual-line Sign In Button */}
            <button onClick={() => navigate('/login')} className="flex flex-col items-center justify-center border border-slate-800/80 bg-[#080d1a]/80 px-5 py-2 text-xs font-black leading-tight rounded-xl hover:bg-slate-900/60 hover:border-slate-700/80 transition-all cursor-pointer h-12 text-slate-200">
              <span>Sign</span>
              <span>In</span>
            </button>
            {/* Sleek stacked dual-line Get Started Button */}
            <button onClick={() => navigate('/register')} className="flex items-center gap-3 bg-gradient-to-r from-blue-600 to-indigo-600 px-5 py-2 rounded-xl text-xs font-black leading-tight hover:from-blue-500 hover:to-indigo-500 shadow-lg shadow-blue-500/20 border border-blue-400/20 hover:shadow-blue-500/30 transition-all cursor-pointer h-12 text-white">
              <div className="flex flex-col text-left">
                <span>Get</span>
                <span>Started</span>
              </div>
              <span className="text-sm font-black">&rarr;</span>
            </button>
          </div>

          {/* Hamburger Menu Toggle Button - Mobile Only */}
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
          className={`fixed inset-y-0 right-0 z-50 w-full max-w-sm bg-[#090e1a] border-l border-[#141b30] p-6 shadow-2xl transition-transform duration-300 md:hidden flex flex-col justify-between ${
            mobileMenuOpen ? 'translate-x-0' : 'translate-x-full'
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
                <span className="font-display font-black text-sm text-white tracking-tight">StockIQ</span>
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

      {/* --- HERO SECTION --- */}
      <section className="relative pt-24 pb-20 px-6 text-center max-w-4xl mx-auto z-10">


        <h1 className="font-display font-black text-4xl sm:text-6xl md:text-[76px] tracking-tight leading-[1.08] mb-6 text-white text-center">
          Inventory That <br />
          <span className="bg-gradient-to-r from-[#5882f7] via-[#7b61ff] to-[#b388ff] bg-clip-text text-transparent">
            Thinks With You
          </span>
        </h1>

        <p className="text-slate-400 text-base sm:text-lg max-w-2xl mx-auto mb-10 leading-relaxed font-medium">
          StockIQ is an intelligent inventory system that tracks products, manages suppliers, and alerts you before you run out — all in one beautiful dashboard.
        </p>

        <div className="flex flex-col sm:flex-row justify-center items-center gap-4 mb-20">
          <button onClick={() => navigate('/register')} className="w-full sm:w-auto px-8 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white rounded-xl font-bold shadow-xl shadow-blue-500/25 border border-blue-400/20 hover:shadow-blue-500/35 transition-all flex items-center justify-center gap-2 text-sm cursor-pointer">
            Start Free Trial <span className="text-base font-black">&rarr;</span>
          </button>
          <a href="#preview" className="w-full sm:w-auto px-8 py-4 bg-[#090e1a]/80 hover:bg-[#111827]/80 text-slate-300 hover:text-white border border-slate-800/80 rounded-xl font-bold transition-all flex items-center justify-center gap-2 text-sm">
            <svg className="w-5 h-5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <circle cx="12" cy="12" r="10" />
              <path d="M10 8l6 4-6 4V8z" fill="currentColor" />
            </svg>
            See Preview
          </a>
        </div>

        {/* Hero Tech-Blueprint Quick Stats Grid */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-px bg-slate-800/40 rounded-2xl border border-slate-800/60 overflow-hidden backdrop-blur-sm shadow-2xl">
          <div className="p-6 md:p-8 bg-[#070c1a]/95 text-center flex flex-col justify-between">
            <h3 className="text-4xl md:text-5xl font-black font-display text-white tracking-tight">2K+</h3>
            <div className="text-[10px] md:text-[11px] text-slate-500 uppercase tracking-widest font-mono font-bold leading-tight mt-3">
              Active<br />Businesses
            </div>
          </div>
          <div className="p-6 md:p-8 bg-[#070c1a]/95 text-center flex flex-col justify-between">
            <h3 className="text-4xl md:text-5xl font-black font-display text-[#60a5fa] tracking-tight">18M</h3>
            <div className="text-[10px] md:text-[11px] text-slate-500 uppercase tracking-widest font-mono font-bold leading-tight mt-3">
              Items Tracked
            </div>
          </div>
          <div className="p-6 md:p-8 bg-[#070c1a]/95 text-center flex flex-col justify-between">
            <h3 className="text-4xl md:text-5xl font-black font-display text-[#818cf8] tracking-tight">99.9%</h3>
            <div className="text-[10px] md:text-[11px] text-slate-500 uppercase tracking-widest font-mono font-bold leading-tight mt-3">
              Uptime SLA
            </div>
          </div>
          <div className="p-6 md:p-8 bg-[#070c1a]/95 text-center flex flex-col justify-between">
            <h3 className="text-4xl md:text-5xl font-black font-display text-[#38bdf8] tracking-tight">~4hr</h3>
            <div className="text-[10px] md:text-[11px] text-slate-500 uppercase tracking-widest font-mono font-bold leading-tight mt-3">
              Avg. Setup Time
            </div>
          </div>
        </div>
      </section>

      {/* --- FEATURES GRID SECTION --- */}
      <section id="features" className="max-w-7xl mx-auto px-6 py-28 border-t border-slate-900/60 relative z-10">
        <div className="text-center mb-20">
          <span className="text-[10px] font-mono tracking-[0.3em] text-[#3b82f6] uppercase font-bold flex items-center justify-center gap-2 mb-3">
            <span className="w-6 h-px bg-blue-500/60 inline-block"></span>
            WHAT WE OFFER
          </span>
          <h2 className="text-3xl sm:text-4xl md:text-[44px] font-bold font-display tracking-tight text-white mt-4">Everything Inventory Needs</h2>
          <p className="text-slate-400 mt-4 text-sm sm:text-base max-w-xl mx-auto leading-relaxed">Built for growing businesses that need real-time control, smart alerts, and full visibility over their stock.</p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {/* Card 1 */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/5 group">
            <div className="w-12 h-12 rounded-2xl bg-orange-500/10 border border-orange-500/25 flex items-center justify-center text-xl mb-6 shadow-inner text-orange-400">📦</div>
            <h3 className="text-xl font-bold text-white mb-3">Product Management</h3>
            <p className="text-slate-400 text-sm leading-relaxed">Add, edit, and organise all your products with SKUs, categories, pricing, and stock levels in one unified view.</p>
          </div>
          {/* Card 2 */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/5 group">
            <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/25 flex items-center justify-center text-xl mb-6 shadow-inner text-emerald-400">📊</div>
            <h3 className="text-xl font-bold text-white mb-3">Live Dashboard</h3>
            <p className="text-slate-400 text-sm leading-relaxed">Real-time overview with key metrics, inventory movement charts, and category breakdowns updated instantly.</p>
          </div>
          {/* Card 3 */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/5 group">
            <div className="w-12 h-12 rounded-2xl bg-yellow-500/10 border border-yellow-500/25 flex items-center justify-center text-xl mb-6 shadow-inner text-yellow-400">🔔</div>
            <h3 className="text-xl font-bold text-white mb-3">Low Stock Alerts</h3>
            <p className="text-slate-400 text-sm leading-relaxed">Automated alerts when items fall below reorder levels. One-click reorder requests sent directly to suppliers.</p>
          </div>
          {/* Card 4 */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/5 group">
            <div className="w-12 h-12 rounded-2xl bg-indigo-500/10 border border-indigo-500/25 flex items-center justify-center text-xl mb-6 shadow-inner text-indigo-400">🏢</div>
            <h3 className="text-xl font-bold text-white mb-3">Supplier Management</h3>
            <p className="text-slate-400 text-sm leading-relaxed">Maintain a complete supplier directory with contacts, categories, and order history for every vendor.</p>
          </div>
          {/* Card 5 */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/5 group">
            <div className="w-12 h-12 rounded-2xl bg-cyan-500/10 border border-cyan-500/25 flex items-center justify-center text-xl mb-6 shadow-inner text-cyan-400">🔄</div>
            <h3 className="text-xl font-bold text-white mb-3">Stock In / Out Tracking</h3>
            <p className="text-slate-400 text-sm leading-relaxed">Log every movement — incoming stock, outgoing shipments, returns. Full audit trail for accountability.</p>
          </div>
          {/* Card 6 */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/5 group">
            <div className="w-12 h-12 rounded-2xl bg-rose-500/10 border border-rose-500/25 flex items-center justify-center text-xl mb-6 shadow-inner text-rose-400">🔐</div>
            <h3 className="text-xl font-bold text-white mb-3">Role-Based Access</h3>
            <p className="text-slate-400 text-sm leading-relaxed">Admin, Manager, Staff, and Viewer roles with fine-grained permissions to keep your data secure.</p>
          </div>
        </div>
      </section>

      {/* --- DASHBOARD PREVIEW SCREENSHOT MOCKUP --- */}
      <section id="preview" className="max-w-5xl mx-auto px-6 py-20 text-center relative z-10">
        <div className="mb-12">
          <span className="text-[10px] font-mono tracking-[0.3em] text-[#3b82f6] uppercase font-bold flex items-center justify-center gap-2 mb-3">
            <span className="w-6 h-px bg-blue-500/60 inline-block"></span>
            LIVE PREVIEW
          </span>
          <h2 className="text-3xl sm:text-4xl md:text-[40px] font-bold font-display tracking-tight mt-2 text-white">The Dashboard You'll Actually Use</h2>
          <p className="text-slate-400 mt-3 text-sm sm:text-base max-w-xl mx-auto">Clean, fast, and informative — designed for daily use by your whole team.</p>
        </div>

        {/* Dashboard Frame Window */}
        <div className="bg-[#070b14] border border-[#1e293b]/80 rounded-2xl shadow-[0_20px_50px_rgba(0,0,0,0.5)] overflow-hidden text-left max-w-4xl mx-auto">
          {/* Browser Top Bar Controls */}
          <div className="bg-[#0a0f1d] px-6 py-4 border-b border-[#141b30] flex items-center justify-between">
            <div className="flex gap-2">
              <span className="w-3 h-3 rounded-full bg-[#f87171] block"></span>
              <span className="w-3 h-3 rounded-full bg-[#fbbf24] block"></span>
              <span className="w-3 h-3 rounded-full bg-[#34d399] block"></span>
            </div>
            <div className="bg-[#0b1224] border border-slate-800/80 rounded-lg text-[10px] text-slate-500 font-mono px-12 sm:px-24 py-1.5 text-center">
              app.stockiq.io/dashboard
            </div>
            <div className="w-14"></div>
          </div>

          <div className="flex flex-col md:flex-row h-auto md:h-[480px]">
            {/* Embedded Mini Sidebar Demo */}
            <aside className="w-full md:w-52 bg-[#090e1a] border-r border-[#141b30] p-4 space-y-2 flex flex-col justify-between">
              <div className="space-y-2">
                <div className="flex items-center gap-2 px-3 mb-6 mt-1">
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

                <div className="bg-[#1e293b]/60 text-blue-400 text-xs font-semibold px-3 py-2.5 rounded-lg border border-slate-800 flex items-center gap-2.5 transition-all">
                  <span className="text-sm">📊</span> Dashboard
                </div>
                <div className="text-slate-400 hover:text-slate-200 text-xs px-3 py-2.5 flex items-center gap-2.5 transition-all cursor-pointer">
                  <span className="text-sm">📦</span> Products
                </div>
                <div className="text-slate-400 hover:text-slate-200 text-xs px-3 py-2.5 flex items-center gap-2.5 transition-all cursor-pointer">
                  <span className="text-sm">🏷️</span> Categories
                </div>
                <div className="text-slate-400 hover:text-slate-200 text-xs px-3 py-2.5 flex items-center gap-2.5 transition-all cursor-pointer">
                  <span className="text-sm">🏢</span> Suppliers
                </div>
                <div className="text-slate-400 hover:text-slate-200 text-xs px-3 py-2.5 flex items-center gap-2.5 transition-all cursor-pointer">
                  <span className="text-sm">🔄</span> Stock
                </div>
                <div className="text-slate-400 hover:text-slate-200 text-xs px-3 py-2.5 flex items-center justify-between transition-all cursor-pointer">
                  <div className="flex items-center gap-2.5">
                    <span className="text-sm">🔔</span> Alerts
                  </div>
                  <span className="bg-[#ef4444] text-[9px] text-white px-1.5 py-0.5 rounded-full font-bold">18</span>
                </div>
              </div>
            </aside>

            {/* Embedded Main Content Window View */}
            <main className="flex-1 p-6 space-y-6 overflow-y-auto bg-[#070b14]">
              <div className="flex items-center justify-between">
                <h3 className="text-base font-bold text-white font-sans">Dashboard</h3>
                <div className="flex items-center gap-3">
                  <span className="text-[10px] text-slate-500 font-medium">Today, Apr 14</span>
                  <div className="w-7 h-7 rounded-full bg-gradient-to-tr from-blue-500 to-indigo-600 text-[10px] font-bold text-white flex items-center justify-center border border-blue-400/20">AD</div>
                </div>
              </div>

              <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                <div className="bg-[#0b1224]/80 border border-[#1e293b]/70 p-4 rounded-xl space-y-2">
                  <span className="text-[9px] text-slate-500 font-mono uppercase tracking-wider block">Total Products</span>
                  <div className="text-2xl font-bold font-display text-white mt-1">248</div>
                  <span className="text-[10px] text-emerald-400 bg-emerald-950/40 border border-emerald-500/20 px-1.5 py-0.5 rounded font-medium inline-block mt-2">+12 this week</span>
                </div>
                <div className="bg-[#0b1224]/80 border border-[#1e293b]/70 p-4 rounded-xl space-y-2">
                  <span className="text-[9px] text-slate-500 font-mono uppercase tracking-wider block">Total Stock Value</span>
                  <div className="text-2xl font-bold font-display text-white mt-1">LKR 2.4M</div>
                  <span className="text-[10px] text-emerald-400 bg-emerald-950/40 border border-emerald-500/20 px-1.5 py-0.5 rounded font-medium inline-block mt-2">+4.2%</span>
                </div>
                <div className="bg-[#0b1224]/80 border border-[#1e293b]/70 p-4 rounded-xl space-y-2">
                  <span className="text-[9px] text-slate-500 font-mono uppercase tracking-wider block">Low Stock Items</span>
                  <div className="text-2xl font-bold font-display text-[#fb923c] mt-1">18</div>
                  <span className="text-[10px] text-amber-400 bg-amber-950/40 border border-amber-500/20 px-1.5 py-0.5 rounded font-medium inline-block mt-2">Action needed</span>
                </div>
                <div className="bg-[#0b1224]/80 border border-[#1e293b]/70 p-4 rounded-xl space-y-2">
                  <span className="text-[9px] text-slate-500 font-mono uppercase tracking-wider block">Suppliers</span>
                  <div className="text-2xl font-bold font-display text-white mt-1">12</div>
                  <span className="text-[10px] text-blue-400 bg-blue-950/40 border border-blue-500/20 px-1.5 py-0.5 rounded font-medium inline-block mt-2">All active</span>
                </div>
              </div>

              {/* Progress Distribution Mockup view */}
              <div className="bg-[#0b1224]/50 border border-[#1e293b]/60 p-5 rounded-xl space-y-4">
                <h4 className="text-xs font-bold uppercase text-slate-400 tracking-wider">Stock by Category</h4>
                <div className="space-y-4">
                  <div>
                    <div className="flex justify-between text-xs mb-1 text-slate-300 font-medium">
                      <span>Electronics</span>
                      <span className="font-bold text-white">42%</span>
                    </div>
                    <div className="w-full bg-[#141b30] h-2 rounded-full overflow-hidden">
                      <div className="bg-gradient-to-r from-blue-500 to-indigo-600 h-full w-[42%] rounded-full"></div>
                    </div>
                  </div>
                  <div>
                    <div className="flex justify-between text-xs mb-1 text-slate-300 font-medium">
                      <span>Office Supplies</span>
                      <span className="font-bold text-white">28%</span>
                    </div>
                    <div className="w-full bg-[#141b30] h-2 rounded-full overflow-hidden">
                      <div className="bg-gradient-to-r from-emerald-500 to-teal-500 h-full w-[28%] rounded-full"></div>
                    </div>
                  </div>
                  <div>
                    <div className="flex justify-between text-xs mb-1 text-slate-300 font-medium">
                      <span>Furniture</span>
                      <span className="font-bold text-white">18%</span>
                    </div>
                    <div className="w-full bg-[#141b30] h-2 rounded-full overflow-hidden">
                      <div className="bg-gradient-to-r from-amber-500 to-orange-500 h-full w-[18%] rounded-full"></div>
                    </div>
                  </div>
                </div>
              </div>
            </main>
          </div>
        </div>
      </section>

      {/* --- INVENTORY VISIBILITY & DATA TABLE SECTION --- */}
      <section className="max-w-7xl mx-auto px-6 py-24 grid md:grid-cols-2 gap-12 items-center relative z-10">
        <div>
          <span className="text-[10px] font-mono tracking-[0.3em] text-[#3b82f6] uppercase font-bold flex items-center gap-2 mb-3">
            <span className="w-6 h-px bg-blue-500/60 inline-block"></span>
            PRODUCT & STOCK
          </span>
          <h2 className="text-3xl sm:text-4xl md:text-5xl font-bold font-display tracking-tight text-white mt-4 mb-6 leading-tight">Full Inventory Visibility</h2>
          <p className="text-slate-400 mb-8 leading-relaxed text-sm sm:text-base">See every product, quantity, and value in real time. Filter, search, and sort across your entire catalog instantly.</p>
          <ul className="space-y-4">
            <li className="flex items-start gap-3">
              <span className="text-[#3b82f6] font-bold text-base mt-0.5">✓</span>
              <div>
                <strong className="block text-sm text-slate-200">Smart SKU Management</strong>
                <span className="text-xs text-slate-400">Auto-generate or manually assign SKUs. Barcode-ready for warehouse scanning.</span>
              </div>
            </li>
            <li className="flex items-start gap-3">
              <span className="text-[#3b82f6] font-bold text-base mt-0.5">✓</span>
              <div>
                <strong className="block text-sm text-slate-200">Category Grouping</strong>
                <span className="text-xs text-slate-400">Organise products into categories with icons and color codes for fast browsing.</span>
              </div>
            </li>
            <li className="flex items-start gap-3">
              <span className="text-[#3b82f6] font-bold text-base mt-0.5">✓</span>
              <div>
                <strong className="block text-sm text-slate-200">Quantity & Reorder Thresholds</strong>
                <span className="text-xs text-slate-400">Set per-product reorder levels. Receive alerts the moment stock dips below your limit.</span>
              </div>
            </li>
          </ul>
        </div>

        {/* Dynamic Mockup Live Inventory Table */}
        <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 rounded-2xl p-6 shadow-xl">
          <div className="text-xs font-mono uppercase text-slate-500 mb-4 tracking-wider">Product Management</div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="border-b border-slate-800 text-slate-500 font-mono">
                  <th className="pb-3 font-semibold uppercase tracking-wider">Product</th>
                  <th className="pb-3 font-semibold uppercase tracking-wider">SKU</th>
                  <th className="pb-3 font-semibold uppercase tracking-wider">Qty</th>
                  <th className="pb-3 font-semibold uppercase tracking-wider text-right">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/40">
                <tr>
                  <td className="py-4 font-semibold text-slate-200">Laptop Pro 15"</td>
                  <td className="py-4 text-slate-500 font-mono">SKU-001</td>
                  <td className="py-4 font-bold text-white">42</td>
                  <td className="py-4 text-right">
                    <span className="bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 px-2.5 py-0.5 rounded-full text-[10px] font-bold">In Stock</span>
                  </td>
                </tr>
                <tr>
                  <td className="py-4 font-semibold text-slate-200">USB-C Hub 7-port</td>
                  <td className="py-4 text-slate-500 font-mono">SKU-007</td>
                  <td className="py-4 font-bold text-white">7</td>
                  <td className="py-4 text-right">
                    <span className="bg-amber-500/10 text-amber-400 border border-amber-500/20 px-2.5 py-0.5 rounded-full text-[10px] font-bold">Low Stock</span>
                  </td>
                </tr>
                <tr>
                  <td className="py-4 font-semibold text-slate-200">A4 Paper (500 sheets)</td>
                  <td className="py-4 text-slate-500 font-mono">SKU-022</td>
                  <td className="py-4 font-bold text-white">3</td>
                  <td className="py-4 text-right">
                    <span className="bg-rose-500/10 text-rose-400 border border-rose-500/20 px-2.5 py-0.5 rounded-full text-[10px] font-bold">Critical</span>
                  </td>
                </tr>
                <tr>
                  <td className="py-4 font-semibold text-slate-200">Office Chair</td>
                  <td className="py-4 text-slate-500 font-mono">SKU-014</td>
                  <td className="py-4 font-bold text-white">19</td>
                  <td className="py-4 text-right">
                    <span className="bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 px-2.5 py-0.5 rounded-full text-[10px] font-bold">In Stock</span>
                  </td>
                </tr>
                <tr>
                  <td className="py-4 font-semibold text-slate-200">HDMI Cable 2m</td>
                  <td className="py-4 text-slate-500 font-mono">SKU-031</td>
                  <td className="py-4 font-bold text-white">6</td>
                  <td className="py-4 text-right">
                    <span className="bg-amber-500/10 text-amber-400 border border-amber-500/20 px-2.5 py-0.5 rounded-full text-[10px] font-bold">Low Stock</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      {/* --- REORDER ALERTS SECTION --- */}
      <section className="max-w-7xl mx-auto px-6 py-24 grid md:grid-cols-2 gap-12 items-center border-t border-slate-900/60 relative z-10">
        <div className="space-y-4 order-2 md:order-1">
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 rounded-2xl p-6 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-l-4 border-amber-500 bg-amber-500/5 p-4 rounded-r-xl border border-slate-800/40">
              <div>
                <span className="text-xs font-bold block text-white">Whiteboard Markers</span>
                <span className="text-[10px] text-slate-400 block mt-0.5 font-mono">Current: 4 units • Reorder at: 25</span>
              </div>
              <button className="bg-blue-600 hover:bg-blue-500 text-white text-[10px] font-bold px-3.5 py-2 rounded-lg transition-colors cursor-pointer border border-blue-400/20">Reorder</button>
            </div>
            <div className="flex items-center justify-between border-l-4 border-amber-500 bg-amber-500/5 p-4 rounded-r-xl border border-slate-800/40">
              <div>
                <span className="text-xs font-bold block text-white">USB-C Hub 7-port</span>
                <span className="text-[10px] text-slate-400 block mt-0.5 font-mono">Current: 7 units • Reorder at: 15</span>
              </div>
              <button className="bg-blue-600 hover:bg-blue-500 text-white text-[10px] font-bold px-3.5 py-2 rounded-lg transition-colors cursor-pointer border border-blue-400/20">Reorder</button>
            </div>
          </div>
        </div>

        <div className="order-1 md:order-2">
          <span className="text-[10px] font-mono tracking-[0.3em] text-[#3b82f6] uppercase font-bold flex items-center gap-2 mb-3">
            <span className="w-6 h-px bg-blue-500/60 inline-block"></span>
            SMART ALERTS
          </span>
          <h2 className="text-3xl sm:text-4xl md:text-5xl font-bold font-display tracking-tight text-white mt-4 mb-6 leading-tight">Never Run Out Again</h2>
          <p className="text-slate-400 mb-6 leading-relaxed text-sm sm:text-base">StockIQ watches your inventory 24/7 and alerts you — or your team — the moment action is needed.</p>
          <ul className="space-y-4">
            <li className="flex items-start gap-3">
              <span className="text-[#3b82f6] font-bold text-base mt-0.5">✓</span>
              <div>
                <strong className="block text-sm text-slate-200">Instant Low-Stock Notifications</strong>
                <span className="text-xs text-slate-400">Real-time in-app alerts with item details and recommended reorder quantities.</span>
              </div>
            </li>
            <li className="flex items-start gap-3">
              <span className="text-[#3b82f6] font-bold text-base mt-0.5">✓</span>
              <div>
                <strong className="block text-sm text-slate-200">One-Click Reorder Requests</strong>
                <span className="text-xs text-slate-400">Send a reorder directly to the linked supplier from the alert panel — no manual emails needed.</span>
              </div>
            </li>
          </ul>
        </div>
      </section>

      {/* --- STEP BY STEP PROCESS SECTION --- */}
      <section id="process" className="bg-[#050814]/80 py-28 border-t border-b border-slate-900/60 px-6 relative z-10">
        <div className="max-w-7xl mx-auto text-center">
          <span className="text-[10px] font-mono tracking-[0.3em] text-[#3b82f6] uppercase font-bold flex items-center justify-center gap-2 mb-3">
            <span className="w-6 h-px bg-blue-500/60 inline-block"></span>
            PROCESS
          </span>
          <h2 className="text-3xl sm:text-4xl md:text-5xl font-bold font-display tracking-tight text-white mt-4 mb-4">Up and Running in Minutes</h2>
          <p className="text-slate-400 max-w-xl mx-auto mb-20 text-sm sm:text-base">No complex setup. No IT team required. StockIQ is ready when you are.</p>

          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-10 relative">
            <div>
              {/* Massive Unbounded glowing number indicator */}
              <div className="text-5xl md:text-6xl font-black font-display text-blue-500/30 tracking-tighter mb-4 relative drop-shadow-[0_0_12px_rgba(59,130,246,0.3)] select-none">
                01
              </div>
              <h4 className="text-lg font-bold text-white mb-2 font-sans">Create Account</h4>
              <p className="text-slate-400 text-xs leading-relaxed max-w-xs mx-auto">Sign up with your business details and choose your team's roles and permissions.</p>
            </div>
            <div>
              <div className="text-5xl md:text-6xl font-black font-display text-blue-500/30 tracking-tighter mb-4 relative drop-shadow-[0_0_12px_rgba(59,130,246,0.3)] select-none">
                02
              </div>
              <h4 className="text-lg font-bold text-white mb-2 font-sans">Import Products</h4>
              <p className="text-slate-400 text-xs leading-relaxed max-w-xs mx-auto">Add products manually or import from CSV. Categories and SKUs auto-organised.</p>
            </div>
            <div>
              <div className="text-5xl md:text-6xl font-black font-display text-blue-500/30 tracking-tighter mb-4 relative drop-shadow-[0_0_12px_rgba(59,130,246,0.3)] select-none">
                03
              </div>
              <h4 className="text-lg font-bold text-white mb-2 font-sans">Connect Suppliers</h4>
              <p className="text-slate-400 text-xs leading-relaxed max-w-xs mx-auto">Add your supplier contacts, set reorder points, and link products to their sources.</p>
            </div>
            <div>
              <div className="text-5xl md:text-6xl font-black font-display text-blue-500/30 tracking-tighter mb-4 relative drop-shadow-[0_0_12px_rgba(59,130,246,0.3)] select-none">
                04
              </div>
              <h4 className="text-lg font-bold text-white mb-2 font-sans">Go Live</h4>
              <p className="text-slate-400 text-xs leading-relaxed max-w-xs mx-auto">Start tracking movements. StockIQ monitors stock levels and alerts you automatically.</p>
            </div>
          </div>
        </div>
      </section>

      {/* --- PRICING SECTION --- */}
      <section id="pricing" className="max-w-7xl mx-auto px-6 py-28 text-center relative z-10">
        <div className="mb-20">
          <span className="text-[10px] font-mono tracking-[0.3em] text-[#3b82f6] uppercase font-bold flex items-center justify-center gap-2 mb-3">
            <span className="w-6 h-px bg-blue-500/60 inline-block"></span>
            SIMPLE PRICING
          </span>
          <h2 className="text-3xl sm:text-4xl md:text-5xl font-bold font-display tracking-tight mt-4 text-white">Transparent Plans for Growth</h2>
        </div>

        {/* Business Pricing Tier Card */}
        <div className="max-w-md mx-auto bg-[#0b1225]/90 border-2 border-blue-600 rounded-3xl p-8 relative shadow-2xl shadow-blue-500/10 text-left backdrop-blur-md">
          <div className="absolute -top-3.5 right-6 bg-blue-600 text-[10px] font-mono tracking-widest uppercase font-black px-3.5 py-1 rounded-full text-white shadow-lg border border-blue-400/20">
            Most Popular
          </div>
          <h3 className="text-xl font-bold text-white mb-2 font-sans">Business</h3>
          <p className="text-xs text-slate-400 mb-6 leading-relaxed">For growing teams that need full inventory control and supplier tools.</p>
          
          <div className="flex items-baseline gap-2 mb-6">
            <span className="text-xl font-bold text-slate-300">LKR</span>
            <span className="text-5xl font-black text-white font-mono tracking-tight">4,900</span>
            <span className="text-xs text-slate-500">/mo</span>
          </div>
          <div className="text-[11px] text-slate-500 mb-6 -mt-4 font-mono font-medium">Billed monthly • cancel anytime</div>

          <hr className="border-slate-800/80 mb-6" />

          <ul className="space-y-4 text-xs text-slate-300 mb-8 font-medium">
            <li className="flex items-center gap-3"><span className="text-blue-500 font-black text-base">✓</span> Unlimited products</li>
            <li className="flex items-center gap-3"><span className="text-blue-500 font-black text-base">✓</span> Up to 10 users</li>
            <li className="flex items-center gap-3"><span className="text-blue-500 font-black text-base">✓</span> Full dashboard + analytics</li>
            <li className="flex items-center gap-3"><span className="text-blue-500 font-black text-base">✓</span> Smart alerts + reorder</li>
            <li className="flex items-center gap-3"><span className="text-blue-500 font-black text-base">✓</span> Supplier management</li>
            <li className="flex items-center gap-3"><span className="text-blue-500 font-black text-base">✓</span> CSV import & export</li>
            <li className="flex items-center gap-3 text-slate-600"><span className="text-slate-700 font-bold">×</span> API access</li>
          </ul>

          <button onClick={() => navigate('/register')} className="w-full py-4 bg-blue-600 hover:bg-blue-500 text-white rounded-xl font-bold shadow-lg shadow-blue-600/20 hover:shadow-blue-600/35 transition-all text-center text-sm cursor-pointer border border-blue-400/20">
            Start 14-Day Free Trial
          </button>
        </div>
      </section>

      {/* --- CONTACT SECTION --- */}
      <section id="contact" className="max-w-7xl mx-auto px-6 py-28 border-t border-slate-900/60 relative z-10">
        <div className="text-center mb-16">
          <span className="text-[10px] font-mono tracking-[0.3em] text-[#3b82f6] uppercase font-bold flex items-center justify-center gap-2 mb-3">
            <span className="w-6 h-px bg-blue-500/60 inline-block"></span>
            GET IN TOUCH
          </span>
          <h2 className="text-3xl sm:text-4xl md:text-[44px] font-bold font-display tracking-tight text-white mt-4">Contact Us</h2>
          <p className="text-slate-400 mt-4 text-sm sm:text-base max-w-xl mx-auto leading-relaxed">Have questions? We'd love to hear from you. Reach out and our team will get back to you shortly.</p>
        </div>

        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-8 max-w-4xl mx-auto">
          {/* Phone Card */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/5 group text-center">
            <div className="w-14 h-14 rounded-2xl bg-blue-500/10 border border-blue-500/25 flex items-center justify-center mx-auto mb-5 shadow-inner">
              <svg className="w-6 h-6 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
            </div>
            <h3 className="text-lg font-bold text-white mb-2">Phone</h3>
            <a href="tel:077200077" className="text-blue-400 hover:text-blue-300 transition-colors font-mono text-sm font-semibold">077 200 077</a>
            <p className="text-[11px] text-slate-500 mt-2">Mon – Fri, 9AM – 6PM</p>
          </div>

          {/* Email Card */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-indigo-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-indigo-500/5 group text-center">
            <div className="w-14 h-14 rounded-2xl bg-indigo-500/10 border border-indigo-500/25 flex items-center justify-center mx-auto mb-5 shadow-inner">
              <svg className="w-6 h-6 text-indigo-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
            </div>
            <h3 className="text-lg font-bold text-white mb-2">Email</h3>
            <a href="mailto:stockIo@gmail.com" className="text-indigo-400 hover:text-indigo-300 transition-colors font-mono text-sm font-semibold">stockIo@gmail.com</a>
            <p className="text-[11px] text-slate-500 mt-2">We reply within 24 hours</p>
          </div>

          {/* Location Card */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-emerald-500/40 rounded-3xl p-8 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-emerald-500/5 group text-center sm:col-span-2 lg:col-span-1">
            <div className="w-14 h-14 rounded-2xl bg-emerald-500/10 border border-emerald-500/25 flex items-center justify-center mx-auto mb-5 shadow-inner">
              <svg className="w-6 h-6 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path strokeLinecap="round" strokeLinejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </div>
            <h3 className="text-lg font-bold text-white mb-2">Office</h3>
            <p className="text-emerald-400 font-mono text-sm font-semibold">Sri Lanka</p>
            <p className="text-[11px] text-slate-500 mt-2">Available worldwide</p>
          </div>
        </div>
      </section>

      {/* --- FOOTER --- */}
      <footer className="border-t border-slate-900/60 bg-[#03050c] py-12 px-6 relative z-10">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row items-center justify-between gap-8">
            {/* Logo + Tagline */}
            <div className="flex items-center gap-3">
              <div className="bg-blue-600/10 p-2 rounded-xl border border-blue-500/20">
                <svg className="w-4 h-4 text-blue-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2.5}>
                  <rect x="4" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="14" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="4" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="14" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                </svg>
              </div>
              <div>
                <span className="font-display font-black text-slate-200 block text-sm">StockIQ</span>
                <span className="text-[9px] text-slate-600 font-mono tracking-widest uppercase">Smart Inventory</span>
              </div>
            </div>

            {/* Footer Links */}
            <nav className="flex flex-wrap items-center justify-center gap-6 text-xs text-slate-500 font-semibold">
              <a href="#features" className="hover:text-slate-300 transition-colors">Features</a>
              <a href="#process" className="hover:text-slate-300 transition-colors">How It Works</a>
              <a href="#pricing" className="hover:text-slate-300 transition-colors">Pricing</a>
              <a href="#contact" className="hover:text-slate-300 transition-colors">Contact</a>
            </nav>

            {/* Contact Info */}
            <div className="flex items-center gap-6 text-xs text-slate-500">
              <a href="tel:077200077" className="flex items-center gap-1.5 hover:text-slate-300 transition-colors">
                <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                077 200 077
              </a>
              <a href="mailto:stockIo@gmail.com" className="flex items-center gap-1.5 hover:text-slate-300 transition-colors">
                <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                stockIo@gmail.com
              </a>
            </div>
          </div>

          {/* Bottom Divider + Copyright */}
          <div className="border-t border-slate-900/60 mt-8 pt-6 text-center text-[11px] text-slate-600 font-mono">
            &copy; {new Date().getFullYear()} StockIQ Smart Inventory. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;