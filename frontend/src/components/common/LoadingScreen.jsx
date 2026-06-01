import React, { useState, useEffect } from 'react';

const LOADING_STATUSES = [
  'Establishing secure gateway connection...',
  'Verifying local authentication tokens...',
  'Decrypting user workspace settings...',
  'Syncing product and category databases...',
  'Validating inventory stock alert counts...',
  'Optimizing dashboard layouts...',
  'Finalizing startup configurations...'
];

const LoadingScreen = ({ message }) => {
  const [elapsed, setElapsed] = useState(0.0);
  const [percent, setPercent] = useState(0);
  const [statusIdx, setStatusIdx] = useState(0);

  useEffect(() => {
    // 1. Live elapsed timer
    const elapsedInterval = setInterval(() => {
      setElapsed((prev) => +(prev + 0.1).toFixed(1));
    }, 100);

    // 2. Premium progress percentage count (slowing down asymptotically as it approaches 98%)
    const percentInterval = setInterval(() => {
      setPercent((prev) => {
        if (prev >= 98) return 98;
        const diff = 100 - prev;
        const increment = Math.max(1, Math.floor(Math.random() * (diff / 6) + 1));
        return Math.min(98, prev + increment);
      });
    }, 200);

    // 3. Status text cycle
    const statusInterval = setInterval(() => {
      setStatusIdx((prev) => (prev + 1) % LOADING_STATUSES.length);
    }, 850);

    return () => {
      clearInterval(elapsedInterval);
      clearInterval(percentInterval);
      clearInterval(statusInterval);
    };
  }, []);

  return (
    <div className="bg-[#050814] text-white min-h-screen flex flex-col items-center justify-center relative font-sans select-none overflow-hidden">
      {/* Tech Blueprint Grid Background Accent */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#141b30_1px,transparent_1px),linear-gradient(to_bottom,#141b30_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] pointer-events-none" />
      
      {/* Glowing Ambient light behind logo */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[280px] h-[280px] bg-blue-500/10 rounded-full blur-[100px] pointer-events-none animate-pulse" />

      <div className="relative z-10 flex flex-col items-center max-w-sm px-6 text-center space-y-8">
        
        {/* Brand Logo with Pulsing Outer Spinner */}
        <div className="relative">
          <div className="absolute inset-0 rounded-3xl border border-blue-500/40 animate-ping opacity-25" />
          <div className="relative bg-gradient-to-br from-blue-500 to-indigo-600 p-4 rounded-2xl shadow-xl shadow-blue-500/20 border border-blue-400/20 flex items-center justify-center animate-bounce duration-1000">
            <svg className="w-10 h-10 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <rect x="4" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
              <rect x="14" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
              <rect x="4" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
              <rect x="14" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
            </svg>
          </div>
        </div>

        {/* Brand System Name */}
        <div className="space-y-1.5">
          <h2 className="text-3xl font-black tracking-tight text-white font-display">StockIQ</h2>
          <span className="text-[10px] text-blue-500 font-mono font-bold tracking-[0.3em] uppercase block">
            Smart Inventory Suite
          </span>
        </div>

        {/* Custom Progress Bar & Percentage */}
        <div className="w-64 space-y-2">
          <div className="flex items-center justify-between text-[11px] font-mono font-bold text-slate-400">
            <span className="uppercase tracking-wider">Loading System</span>
            <span className="text-blue-400">{percent}%</span>
          </div>
          <div className="w-full bg-slate-950/60 border border-slate-900 h-2.5 rounded-full overflow-hidden p-0.5 backdrop-blur-sm">
            <div 
              className="bg-gradient-to-r from-blue-500 to-indigo-600 h-full rounded-full transition-all duration-300 ease-out shadow-[0_0_10px_rgba(59,130,246,0.5)]" 
              style={{ width: `${percent}%` }}
            />
          </div>
        </div>

        {/* Live Timer and Current Action details */}
        <div className="space-y-1 pt-2">
          <p className="text-[13px] font-medium text-slate-200 min-h-[20px] transition-all duration-300">
            {message || LOADING_STATUSES[statusIdx]}
          </p>
          <p className="text-[10px] font-mono text-slate-500 font-bold tracking-widest uppercase">
            Elapsed Time: <span className="text-slate-400 font-semibold">{elapsed.toFixed(1)}s</span>
          </p>
        </div>

      </div>
    </div>
  );
};

export default LoadingScreen;
