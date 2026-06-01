import React from 'react';
import MainLayout from '../components/layout/MainLayout';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const stats = [
    {
      label: 'TOTAL PRODUCTS',
      value: '248',
      pillText: '12 new this month',
      pillClass: 'bg-blue-500/10 text-blue-400 border-blue-500/20',
      icon: (
        <div className="bg-blue-500/10 border border-blue-500/20 p-2.5 rounded-xl shadow-lg shadow-blue-500/5">
          <svg className="w-5 h-5 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
      )
    },
    {
      label: 'TOTAL SUPPLIERS',
      value: '34',
      pillText: 'Active',
      pillClass: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
      icon: (
        <div className="bg-emerald-500/10 border border-emerald-500/20 p-2.5 rounded-xl shadow-lg shadow-emerald-500/5">
          <svg className="w-5 h-5 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857" />
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>
      )
    },
    {
      label: 'LOW STOCK',
      value: '18',
      pillText: 'Needs reorder',
      pillClass: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
      icon: (
        <div className="bg-amber-500/10 border border-amber-500/20 p-2.5 rounded-xl shadow-lg shadow-amber-500/5">
          <svg className="w-5 h-5 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
      )
    },
    {
      label: 'TRANSACTIONS',
      value: '1,204',
      pillText: '+8% this week',
      pillClass: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
      icon: (
        <div className="bg-emerald-500/10 border border-emerald-500/20 p-2.5 rounded-xl shadow-lg shadow-emerald-500/5">
          <svg className="w-5 h-5 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
          </svg>
        </div>
      )
    }
  ];

  const categories = [
    { name: 'Electronics', count: 82, percentage: 42, color: 'bg-blue-500 shadow-blue-500/50' },
    { name: 'Office Supplies', count: 67, percentage: 28, color: 'bg-emerald-500 shadow-emerald-500/50' },
    { name: 'Furniture', count: 45, percentage: 18, color: 'bg-amber-500 shadow-amber-500/50' },
    { name: 'Raw Materials', count: 54, percentage: 12, color: 'bg-purple-500 shadow-purple-500/50' }
  ];

  const transactions = [
    { type: 'IN', name: 'Laptop Pro 15"', qty: '+20', time: '2h ago', bg: 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' },
    { type: 'OUT', name: 'USB-C Cable', qty: '-5', time: '4h ago', bg: 'bg-rose-500/10 text-rose-400 border border-rose-500/20' },
    { type: 'IN', name: 'Office Chair', qty: '+8', time: '6h ago', bg: 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' },
    { type: 'OUT', name: 'A4 Paper Box', qty: '-12', time: 'Yesterday', bg: 'bg-rose-500/10 text-rose-400 border border-rose-500/20' }
  ];

  return (
    <MainLayout title="Dashboard">
      <div className="p-6 relative z-10 space-y-6 sm:space-y-8">
        
        {/* Stats Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {stats.map((stat, idx) => (
            <div
              key={idx}
              className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-5 rounded-2xl flex flex-col justify-between shadow-lg shadow-black/30 backdrop-blur-sm transition-all hover:border-slate-800 hover:-translate-y-0.5"
            >
              <div className="flex items-start justify-between">
                <div className="space-y-2">
                  <span className="text-[10px] text-slate-500 font-mono uppercase tracking-wider block">
                    {stat.label}
                  </span>
                  <h3 className="text-3xl font-black font-display text-white tracking-tight leading-none">
                    {stat.value}
                  </h3>
                </div>
                {stat.icon}
              </div>
              <div className="pt-4">
                <span className={`text-[10px] px-2.5 py-0.5 rounded-full font-bold border ${stat.pillClass}`}>
                  {stat.pillText}
                </span>
              </div>
            </div>
          ))}
        </div>

        {/* Two-Column Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Stock by Category Panel */}
          <div className="lg:col-span-2 bg-[#0b1224]/85 border border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-black/30 backdrop-blur-sm flex flex-col justify-between">
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-bold uppercase tracking-wider text-slate-300">Stock by category</h3>
                <button
                  onClick={() => navigate('/categories')}
                  className="text-xs text-blue-400 hover:text-blue-300 transition-colors font-semibold"
                >
                  View all
                </button>
              </div>

              <div className="space-y-5">
                {categories.map((category, idx) => (
                  <div key={idx} className="space-y-1.5">
                    <div className="flex items-center justify-between text-xs font-semibold text-slate-300">
                      <span>{category.name}</span>
                      <div className="space-x-2 font-mono">
                        <span className="text-slate-500 text-[10px]">{category.count} items</span>
                        <span className="text-white font-bold">{category.percentage}%</span>
                      </div>
                    </div>
                    <div className="w-full bg-[#141b30] h-2 rounded-full overflow-hidden">
                      <div
                        className={`${category.color} h-full rounded-full transition-all duration-700`}
                        style={{ width: `${category.percentage}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Recent Transactions Panel */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-black/30 backdrop-blur-sm space-y-5">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold uppercase tracking-wider text-slate-300">Recent transactions</h3>
              <button
                onClick={() => navigate('/stock')}
                className="text-xs text-blue-400 hover:text-blue-300 transition-colors font-semibold"
              >
                See all
              </button>
            </div>

            <div className="space-y-3.5">
              {transactions.map((tx, idx) => (
                <div
                  key={idx}
                  className="flex items-center justify-between py-1 border-b border-slate-900/40 last:border-0"
                >
                  <div className="flex items-center gap-3">
                    <span className={`w-8 h-8 rounded-lg flex items-center justify-center text-[10px] font-black font-mono leading-none ${tx.bg}`}>
                      {tx.type}
                    </span>
                    <div className="space-y-0.5">
                      <p className="font-bold text-slate-200 text-xs">{tx.name}</p>
                      <p className="text-[10px] text-slate-500 font-medium">{tx.time}</p>
                    </div>
                  </div>
                  <span className={`font-mono font-black text-xs ${tx.type === 'IN' ? 'text-emerald-400' : 'text-rose-400'}`}>
                    {tx.qty}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

      </div>
    </MainLayout>
  );
}

export default DashboardPage;
