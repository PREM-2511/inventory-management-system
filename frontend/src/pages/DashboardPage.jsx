import React, { useEffect, useState } from 'react';
import MainLayout from '../components/layout/MainLayout';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { dashboardAPI, stockAPI } from '../services/api';

function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [summary, setSummary] = useState({ totalProducts: 0, totalSuppliers: 0, lowStock: 0, totalTransactions: 0 });
  const [categories, setCategories] = useState([]);
  const [recentTx, setRecentTx] = useState([]);
  const [allTx, setAllTx] = useState([]);
  const [lowStockAlerts, setLowStockAlerts] = useState([]);
  const [error, setError] = useState('');
  const [activeCategoryIdx, setActiveCategoryIdx] = useState(null);
  const [hoveredTxPoint, setHoveredTxPoint] = useState(null);
  const [hoveredMonthlyBar, setHoveredMonthlyBar] = useState(null);
  const [hoveredCategoryBar, setHoveredCategoryBar] = useState(null);
  const [hoveredLowStockBar, setHoveredLowStockBar] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        setError('');
        
        // Parallel requests
        const [summaryRes, categoriesRes, historyRes, alertsRes] = await Promise.all([
          dashboardAPI.getSummary(),
          dashboardAPI.getCategoryDistribution(),
          stockAPI.getHistory(),
          stockAPI.getLowStockAlerts()
        ]);

        setSummary(summaryRes.data);
        setCategories(categoriesRes.data || []);
        
        const historyData = historyRes.data || [];
        setAllTx(historyData);
        setRecentTx(historyData.slice(0, 5));
        setLowStockAlerts(alertsRes.data || []);
      } catch (err) {
        console.error('Dashboard loading error:', err);
        setError(err.message || 'Failed to fetch dashboard statistics.');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const stats = [
    {
      label: 'TOTAL PRODUCTS',
      value: summary.totalProducts.toString(),
      pillText: 'All items in database',
      pillClass: 'bg-blue-500/10 text-blue-500 dark:text-blue-400 border-blue-500/20',
      icon: (
        <div className="bg-blue-500/10 border border-blue-500/20 p-2.5 rounded-xl shadow-lg shadow-blue-500/5">
          <svg className="w-5 h-5 text-blue-500 dark:text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
      )
    },
    {
      label: 'TOTAL SUPPLIERS',
      value: summary.totalSuppliers.toString(),
      pillText: 'Registered vendors',
      pillClass: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/20',
      icon: (
        <div className="bg-emerald-500/10 border border-emerald-500/20 p-2.5 rounded-xl shadow-lg shadow-emerald-500/5">
          <svg className="w-5 h-5 text-emerald-600 dark:text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857" />
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>
      )
    },
    {
      label: 'LOW STOCK',
      value: summary.lowStock.toString(),
      pillText: summary.lowStock > 0 ? 'Needs reorder' : 'Levels healthy',
      pillClass: summary.lowStock > 0 
        ? 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20'
        : 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20',
      icon: (
        <div className={`p-2.5 rounded-xl border shadow-lg ${
          summary.lowStock > 0 
            ? 'bg-amber-500/10 border-amber-500/20 shadow-amber-500/5 text-amber-600 dark:text-amber-400' 
            : 'bg-emerald-500/10 border-emerald-500/20 shadow-emerald-500/5 text-emerald-600 dark:text-emerald-400'
        }`}>
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
      )
    },
    {
      label: 'TRANSACTIONS',
      value: summary.totalTransactions.toString(),
      pillText: 'Movement events logged',
      pillClass: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/20',
      icon: (
        <div className="bg-emerald-500/10 border border-emerald-500/20 p-2.5 rounded-xl shadow-lg shadow-emerald-500/5">
          <svg className="w-5 h-5 text-emerald-600 dark:text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
          </svg>
        </div>
      )
    }
  ];

  const categoryColors = [
    '#3b82f6', // blue
    '#10b981', // emerald
    '#f59e0b', // amber
    '#8b5cf6', // purple
    '#f43f5e', // rose
    '#06b6d4'  // cyan
  ];

  const categoryTailwindColors = [
    'bg-blue-500 shadow-blue-500/50',
    'bg-emerald-500 shadow-emerald-500/50',
    'bg-amber-500 shadow-amber-500/50',
    'bg-purple-500 shadow-purple-500/50',
    'bg-rose-500 shadow-rose-500/50',
    'bg-cyan-500 shadow-cyan-500/50'
  ];

  // DONUT CHART CALCULATIONS
  const radius = 35;
  const circumference = 2 * Math.PI * radius; // ~219.91
  let accumulatedPercent = 0;

  const donutSlices = categories.map((cat, idx) => {
    const percentage = cat.percentage || 0;
    const strokeDashoffset = circumference - (percentage / 100) * circumference;
    const rotation = (accumulatedPercent / 100) * 360;
    accumulatedPercent += percentage;
    return {
      ...cat,
      offset: strokeDashoffset,
      rotation: rotation,
      color: categoryColors[idx % categoryColors.length]
    };
  });

  // TREND CHART — separate IN and OUT per time slot
  const getTrendData = () => {
    if (allTx.length === 0) return { points: [], labels: [], maxVal: 10 };

    const recent = [...allTx].reverse().slice(-7);
    while (recent.length < 7) recent.unshift({ qty: 0, date: '', type: 'IN', product: 'N/A' });

    const labels = recent.map(tx => {
      if (!tx.date) return '';
      try {
        const d = new Date(tx.date);
        return isNaN(d) ? tx.date.slice(0, 5) : `${d.getDate()}/${d.getMonth() + 1}`;
      } catch { return tx.date.slice(0, 5) || ''; }
    });

    const inVals  = recent.map(tx => (tx.type?.toUpperCase() === 'IN'  ? (tx.qty || 0) : 0));
    const outVals = recent.map(tx => (tx.type?.toUpperCase() === 'OUT' ? (tx.qty || 0) : 0));
    const maxVal  = Math.max(...inVals, ...outVals, 10);

    const points = recent.map((tx, i) => ({
      x: (i / 6) * 92 + 4,
      inY:  10 + 60 - (inVals[i]  / maxVal) * 55,
      outY: 10 + 60 - (outVals[i] / maxVal) * 55,
      inVal: inVals[i],
      outVal: outVals[i],
      label: labels[i],
      original: tx
    }));

    return { points, labels, maxVal };
  };

  // Group transaction history into monthly buckets for the past 6 months
  const getMonthlyMovements = () => {
    const months = [];
    const now = new Date();
    // Generate the last 6 months (chronological order)
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      months.push({
        year: d.getFullYear(),
        month: d.getMonth(),
        label: d.toLocaleString('default', { month: 'short' }),
        inQty: 0,
        outQty: 0
      });
    }

    // Process all transactions
    allTx.forEach(tx => {
      if (!tx.date) return;
      const dateStr = tx.date.includes(' ') ? tx.date.replace(' ', 'T') : tx.date;
      const txDate = new Date(dateStr);
      if (isNaN(txDate.getTime())) return;

      const match = months.find(m => m.year === txDate.getFullYear() && m.month === txDate.getMonth());
      if (match) {
        const qty = tx.qty || 0;
        if (tx.type?.toUpperCase() === 'IN') {
          match.inQty += qty;
        } else if (tx.type?.toUpperCase() === 'OUT') {
          match.outQty += qty;
        }
      }
    });

    return months;
  };

  const { points: trendPoints, maxVal: trendMax } = getTrendData();

  // Smooth cubic bezier through points
  const smoothPath = (pts, yKey) => {
    if (pts.length < 2) return '';
    return pts.map((p, i) => {
      if (i === 0) return `M ${p.x},${p[yKey]}`;
      const prev = pts[i - 1];
      const cp1x = prev.x + (p.x - prev.x) / 2;
      const cp2x = p.x - (p.x - prev.x) / 2;
      return `C ${cp1x},${prev[yKey]} ${cp2x},${p[yKey]} ${p.x},${p[yKey]}`;
    }).join(' ');
  };

  const inLine   = smoothPath(trendPoints, 'inY');
  const outLine  = smoothPath(trendPoints, 'outY');
  const inArea   = trendPoints.length > 0 ? inLine  + ` L ${trendPoints[trendPoints.length-1].x},70 L ${trendPoints[0].x},70 Z` : '';
  const outArea  = trendPoints.length > 0 ? outLine + ` L ${trendPoints[trendPoints.length-1].x},70 L ${trendPoints[0].x},70 Z` : '';

  const yLabels = [0, Math.round(trendMax / 2), trendMax].map(v => ({
    v,
    y: 10 + 60 - (v / trendMax) * 55
  }));

  const handleReorder = (id, name) => {
    alert(`Reorder request for "${name}" successfully sent!`);
    setLowStockAlerts(prev => prev.filter(a => a.id !== id));
    setSummary(prev => ({ ...prev, lowStock: Math.max(0, prev.lowStock - 1) }));
  };

  return (
    <MainLayout title="Dashboard">
      <div className="p-6 relative z-10 space-y-6 sm:space-y-8">
        
        {/* Error Alert */}
        {error && (
          <div className="p-4 bg-rose-500/10 border border-rose-500/20 text-rose-500 rounded-xl text-xs font-semibold">
            ⚠️ {error}
          </div>
        )}

        {/* Loading overlay or stats list */}
        {loading ? (
          <div className="text-center text-slate-500 py-24 font-semibold">
            Loading dashboard data...
          </div>
        ) : (
          <>
            {/* Stats Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
              {stats.map((stat, idx) => (
                <div
                  key={idx}
                  className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-5 rounded-2xl flex flex-col justify-between shadow-lg shadow-slate-100 dark:shadow-black/30 backdrop-blur-sm transition-all hover:border-slate-300 dark:hover:border-slate-800 hover:-translate-y-0.5"
                >
                  <div className="flex items-start justify-between">
                    <div className="space-y-2">
                      <span className="text-[10px] text-slate-500 dark:text-slate-400 font-mono uppercase tracking-wider block">
                        {stat.label}
                      </span>
                      <h3 className="text-3xl font-black font-display text-slate-800 dark:text-white tracking-tight leading-none">
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

            {/* PREMIUM DUAL TREND CHART */}
            <div className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-slate-100 dark:shadow-black/30 backdrop-blur-sm">
              {/* Header */}
              <div className="flex flex-wrap items-start justify-between gap-3 mb-6">
                <div>
                  <h3 className="text-sm font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">Inventory Activity Trend</h3>
                  <p className="text-[10px] text-slate-500 dark:text-slate-400 mt-0.5">Stock IN vs OUT — last 7 movements</p>
                </div>
                <div className="flex items-center gap-5">
                  <div className="text-center">
                    <p className="text-[9px] font-bold uppercase tracking-wider text-emerald-500/80 mb-0.5">Total In</p>
                    <p className="text-lg font-black text-emerald-500 leading-none">
                      +{allTx.filter(t => t.type?.toUpperCase() === 'IN').reduce((s, t) => s + (t.qty || 0), 0)}
                    </p>
                  </div>
                  <div className="w-px h-8 bg-slate-200 dark:bg-slate-800" />
                  <div className="text-center">
                    <p className="text-[9px] font-bold uppercase tracking-wider text-rose-500/80 mb-0.5">Total Out</p>
                    <p className="text-lg font-black text-rose-500 leading-none">
                      -{allTx.filter(t => t.type?.toUpperCase() === 'OUT').reduce((s, t) => s + (t.qty || 0), 0)}
                    </p>
                  </div>
                  <div className="flex flex-col gap-1.5 ml-2">
                    <div className="flex items-center gap-1.5">
                      <span className="w-5 h-0.5 rounded-full bg-emerald-500 inline-block" />
                      <span className="text-[9px] font-bold text-slate-400 dark:text-slate-500 uppercase">Stock In</span>
                    </div>
                    <div className="flex items-center gap-1.5">
                      <span className="w-5 h-0.5 rounded-full bg-rose-500 inline-block" />
                      <span className="text-[9px] font-bold text-slate-400 dark:text-slate-500 uppercase">Stock Out</span>
                    </div>
                  </div>
                </div>
              </div>

              {trendPoints.length > 0 ? (
                <div className="relative">
                  <svg
                    viewBox="0 0 100 80"
                    className="w-full"
                    style={{ height: '180px' }}
                    preserveAspectRatio="none"
                  >
                    <defs>
                      <linearGradient id="inGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#10b981" stopOpacity="0.30" />
                        <stop offset="100%" stopColor="#10b981" stopOpacity="0.02" />
                      </linearGradient>
                      <linearGradient id="outGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#f43f5e" stopOpacity="0.30" />
                        <stop offset="100%" stopColor="#f43f5e" stopOpacity="0.02" />
                      </linearGradient>
                      <filter id="glow-in">
                        <feGaussianBlur stdDeviation="0.8" result="blur" />
                        <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
                      </filter>
                      <filter id="glow-out">
                        <feGaussianBlur stdDeviation="0.8" result="blur" />
                        <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
                      </filter>
                    </defs>

                    {/* Horizontal Grid Lines */}
                    {yLabels.map(({ y }, i) => (
                      <line key={i} x1="4" y1={y} x2="96" y2={y}
                        stroke="currentColor" strokeOpacity="0.07" strokeWidth="0.5" strokeDasharray="2 2"
                        className="text-slate-900 dark:text-white"
                      />
                    ))}

                    {/* Y-axis labels */}
                    {yLabels.map(({ v, y }, i) => (
                      <text key={i} x="2" y={y + 1}
                        fontSize="3" textAnchor="end"
                        className="fill-slate-400 dark:fill-slate-600" style={{ fontFamily: 'monospace' }}
                      >{v}</text>
                    ))}

                    {/* X-axis baseline */}
                    <line x1="4" y1="70" x2="96" y2="70"
                      stroke="currentColor" strokeOpacity="0.1" strokeWidth="0.5"
                      className="text-slate-900 dark:text-white"
                    />

                    {/* IN Area fill */}
                    {inArea  && <path d={inArea}  fill="url(#inGrad)"  />}
                    {/* OUT Area fill */}
                    {outArea && <path d={outArea} fill="url(#outGrad)" />}

                    {/* IN Line */}
                    {inLine  && <path d={inLine}  fill="none" stroke="#10b981" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" filter="url(#glow-in)" />}
                    {/* OUT Line */}
                    {outLine && <path d={outLine} fill="none" stroke="#f43f5e" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" filter="url(#glow-out)" />}

                    {/* IN Data Points */}
                    {trendPoints.map((p, i) => p.inVal > 0 && (
                      <g key={`in-${i}`}
                        onMouseEnter={() => setHoveredTxPoint(`in-${i}`)}
                        onMouseLeave={() => setHoveredTxPoint(null)}
                        className="cursor-pointer"
                      >
                        <circle cx={p.x} cy={p.inY} r="2.2"
                          fill="#0b1224" stroke="#10b981" strokeWidth="1"
                          style={{ filter: hoveredTxPoint === `in-${i}` ? 'drop-shadow(0 0 2px #10b981)' : 'none' }}
                        />
                        {hoveredTxPoint === `in-${i}` && (
                          <circle cx={p.x} cy={p.inY} r="4.5" fill="#10b981" fillOpacity="0.2" />
                        )}
                      </g>
                    ))}

                    {/* OUT Data Points */}
                    {trendPoints.map((p, i) => p.outVal > 0 && (
                      <g key={`out-${i}`}
                        onMouseEnter={() => setHoveredTxPoint(`out-${i}`)}
                        onMouseLeave={() => setHoveredTxPoint(null)}
                        className="cursor-pointer"
                      >
                        <circle cx={p.x} cy={p.outY} r="2.2"
                          fill="#0b1224" stroke="#f43f5e" strokeWidth="1"
                          style={{ filter: hoveredTxPoint === `out-${i}` ? 'drop-shadow(0 0 2px #f43f5e)' : 'none' }}
                        />
                        {hoveredTxPoint === `out-${i}` && (
                          <circle cx={p.x} cy={p.outY} r="4.5" fill="#f43f5e" fillOpacity="0.2" />
                        )}
                      </g>
                    ))}

                    {/* X-axis date labels */}
                    {trendPoints.map((p, i) => p.label && (
                      <text key={`lbl-${i}`} x={p.x} y="77"
                        fontSize="2.8" textAnchor="middle"
                        className="fill-slate-400 dark:fill-slate-600" style={{ fontFamily: 'monospace' }}
                      >{p.label}</text>
                    ))}
                  </svg>

                  {/* Hover Tooltip */}
                  {hoveredTxPoint !== null && (() => {
                    const [type, idxStr] = String(hoveredTxPoint).split('-');
                    const i = parseInt(idxStr);
                    const p = trendPoints[i];
                    if (!p) return null;
                    const isIn = type === 'in';
                    const val = isIn ? p.inVal : p.outVal;
                    return (
                      <div
                        className="absolute bottom-8 pointer-events-none z-20"
                        style={{ left: `${Math.min(p.x, 78)}%`, transform: 'translateX(-50%)' }}
                      >
                        <div className="bg-slate-950/95 border border-slate-800 rounded-xl px-3 py-2 shadow-2xl backdrop-blur-md min-w-max">
                          <p className="text-[10px] font-bold text-white mb-0.5">{p.original.product}</p>
                          <p className={`text-[10px] font-mono font-black ${ isIn ? 'text-emerald-400' : 'text-rose-400' }`}>
                            {isIn ? `+${val}` : `-${val}`} units
                          </p>
                          {p.label && <p className="text-[9px] text-slate-500 font-mono mt-0.5">{p.label}</p>}
                        </div>
                      </div>
                    );
                  })()}
                </div>
              ) : (
                <p className="text-center text-xs text-slate-400 dark:text-slate-500 py-12">No transaction data available yet.</p>
              )}
            </div>

            {/* ROW 2: DETAILED ANALYTICS CHARTS */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Monthly Stock Movements Card */}
              <div className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-slate-100 dark:shadow-black/30 backdrop-blur-sm relative flex flex-col justify-between">
                <div>
                  <div className="flex flex-col gap-1 mb-6">
                    <h3 className="text-sm font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">Monthly Stock Movements</h3>
                    <p className="text-[10px] text-slate-500 dark:text-slate-400">Total stock IN vs OUT aggregated per month (last 6 months)</p>
                  </div>
                  {allTx.length > 0 ? (
                    <div className="relative pt-2">
                      <svg viewBox="0 0 100 60" className="w-full" style={{ height: '180px' }} preserveAspectRatio="none">
                        <defs>
                          <linearGradient id="inBarGrad" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="0%" stopColor="#10b981" stopOpacity="0.85" />
                            <stop offset="100%" stopColor="#10b981" stopOpacity="0.20" />
                          </linearGradient>
                          <linearGradient id="outBarGrad" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="0%" stopColor="#f43f5e" stopOpacity="0.85" />
                            <stop offset="100%" stopColor="#f43f5e" stopOpacity="0.20" />
                          </linearGradient>
                        </defs>
                        
                        {/* Monthly chart grid lines */}
                        {(() => {
                          const monthlyData = getMonthlyMovements();
                          const maxMonthlyQty = Math.max(...monthlyData.map(m => Math.max(m.inQty, m.outQty)), 10);
                          return [0, 0.5, 1].map((ratio) => {
                            const y = 5 + 43 * (1 - ratio);
                            const val = Math.round(ratio * maxMonthlyQty);
                            return (
                              <g key={ratio}>
                                <line x1="8" y1={y} x2="96" y2={y} stroke="currentColor" strokeOpacity="0.07" strokeWidth="0.3" strokeDasharray="1.5 1.5" className="text-slate-900 dark:text-white" />
                                <text x="6" y={y + 1} fontSize="2.2" textAnchor="end" className="fill-slate-400 dark:fill-slate-600 font-mono">{val}</text>
                              </g>
                            );
                          });
                        })()}
                        
                        {/* Baseline */}
                        <line x1="8" y1="48" x2="96" y2="48" stroke="currentColor" strokeOpacity="0.1" strokeWidth="0.4" className="text-slate-900 dark:text-white" />
                        
                        {/* Bars rendering */}
                        {(() => {
                          const monthlyData = getMonthlyMovements();
                          const maxMonthlyQty = Math.max(...monthlyData.map(m => Math.max(m.inQty, m.outQty)), 10);
                          return monthlyData.map((data, idx) => {
                            const xCenter = 17 + idx * 15;
                            const barWidth = 3.2;
                            
                            const inHeight = (data.inQty / maxMonthlyQty) * 43;
                            const inY = 48 - inHeight;
                            
                            const outHeight = (data.outQty / maxMonthlyQty) * 43;
                            const outY = 48 - outHeight;
                            
                            return (
                              <g key={idx}>
                                {/* IN bar */}
                                <rect
                                  x={xCenter - barWidth - 0.6}
                                  y={inY}
                                  width={barWidth}
                                  height={inHeight || 0.5}
                                  rx="0.6"
                                  fill="url(#inBarGrad)"
                                  className="cursor-pointer transition-all duration-200 hover:brightness-110"
                                  onMouseEnter={() => setHoveredMonthlyBar({ monthIdx: idx, type: 'IN', val: data.inQty, label: data.label, x: xCenter - barWidth/2 - 0.6, y: inY })}
                                  onMouseLeave={() => setHoveredMonthlyBar(null)}
                                />
                                
                                {/* OUT bar */}
                                <rect
                                  x={xCenter + 0.6}
                                  y={outY}
                                  width={barWidth}
                                  height={outHeight || 0.5}
                                  rx="0.6"
                                  fill="url(#outBarGrad)"
                                  className="cursor-pointer transition-all duration-200 hover:brightness-110"
                                  onMouseEnter={() => setHoveredMonthlyBar({ monthIdx: idx, type: 'OUT', val: data.outQty, label: data.label, x: xCenter + barWidth/2 + 0.6, y: outY })}
                                  onMouseLeave={() => setHoveredMonthlyBar(null)}
                                />
                                
                                {/* Month Label */}
                                <text x={xCenter} y="54" fontSize="2.8" textAnchor="middle" className="fill-slate-400 dark:fill-slate-500 font-mono font-bold">{data.label}</text>
                              </g>
                            );
                          });
                        })()}
                      </svg>
                      
                      {/* Tooltip */}
                      {hoveredMonthlyBar && (
                        <div
                          className="absolute pointer-events-none z-20 bg-slate-950/95 border border-slate-800 rounded-xl px-3 py-2 shadow-2xl backdrop-blur-md min-w-max"
                          style={{
                            left: `${hoveredMonthlyBar.x}%`,
                            bottom: `${100 - (hoveredMonthlyBar.y / 60) * 100 + 4}%`,
                            transform: 'translateX(-50%)'
                          }}
                        >
                          <p className="text-[10px] font-bold text-white mb-0.5">{hoveredMonthlyBar.label} - Stock {hoveredMonthlyBar.type}</p>
                          <p className={`text-[10px] font-mono font-black ${hoveredMonthlyBar.type === 'IN' ? 'text-emerald-400' : 'text-rose-400'}`}>
                            {hoveredMonthlyBar.val} units
                          </p>
                        </div>
                      )}
                    </div>
                  ) : (
                    <p className="text-center text-xs text-slate-400 dark:text-slate-500 py-16">No monthly transaction data available yet.</p>
                  )}
                </div>
                {/* Monthly legend */}
                <div className="flex gap-4 items-center justify-end mt-4 pt-3 border-t border-slate-100 dark:border-slate-900/40 text-[9px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                  <div className="flex items-center gap-1.5">
                    <span className="w-2.5 h-2.5 rounded bg-emerald-500 inline-block" />
                    <span>Inbound (IN)</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <span className="w-2.5 h-2.5 rounded bg-rose-500 inline-block" />
                    <span>Outbound (OUT)</span>
                  </div>
                </div>
              </div>

              {/* Stock by Category Card */}
              <div className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-slate-100 dark:shadow-black/30 backdrop-blur-sm relative flex flex-col justify-between">
                <div>
                  <div className="flex flex-col gap-1 mb-6">
                    <h3 className="text-sm font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">Stock Volume by Category</h3>
                    <p className="text-[10px] text-slate-500 dark:text-slate-400">Total physical stock units currently grouped by product category</p>
                  </div>
                  {donutSlices.length > 0 ? (
                    <div className="space-y-4 pt-1">
                      {(() => {
                        const sortedCategories = [...donutSlices].sort((a, b) => (b.totalStock || 0) - (a.totalStock || 0));
                        const maxStock = Math.max(...sortedCategories.map(c => c.totalStock || 0), 1);
                        
                        return sortedCategories.map((category, idx) => {
                          const pct = maxStock > 0 ? ((category.totalStock || 0) / maxStock) * 100 : 0;
                          return (
                            <div
                              key={idx}
                              className="space-y-1.5 relative group"
                              onMouseEnter={() => setHoveredCategoryBar({ idx, name: category.name, stock: category.totalStock || 0, count: category.count, pct: category.percentage })}
                              onMouseLeave={() => setHoveredCategoryBar(null)}
                            >
                              <div className="flex justify-between items-center text-xs">
                                <span className="font-bold text-slate-700 dark:text-slate-300 flex items-center gap-1.5">
                                  <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: category.color }} />
                                  {category.name}
                                </span>
                                <span className="font-mono text-[10px] text-slate-400 dark:text-slate-500">
                                  <strong className="text-slate-800 dark:text-white font-black">{category.totalStock || 0}</strong> units 
                                  <span className="mx-1">•</span> 
                                  {category.count} items
                                </span>
                              </div>
                              <div className="w-full bg-slate-100 dark:bg-[#141b30] h-2.5 rounded-full overflow-hidden border border-slate-200/20 dark:border-slate-800/40 relative">
                                <div
                                  className="h-full rounded-full transition-all duration-500 group-hover:brightness-110"
                                  style={{
                                    width: `${pct}%`,
                                    backgroundColor: category.color,
                                    boxShadow: `0 0 8px ${category.color}30`
                                  }}
                                />
                              </div>
                            </div>
                          );
                        });
                      })()}
                    </div>
                  ) : (
                    <p className="text-center text-xs text-slate-400 dark:text-slate-500 py-16">No category stock data available yet.</p>
                  )}
                </div>
                
                {hoveredCategoryBar && (
                  <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 pointer-events-none z-20 bg-slate-950/95 border border-slate-800 rounded-xl px-4 py-2.5 shadow-2xl backdrop-blur-md min-w-max text-center">
                    <p className="text-xs font-black text-white">{hoveredCategoryBar.name}</p>
                    <p className="text-[10px] text-slate-400 mt-1">
                      Total Stock: <span className="text-white font-bold">{hoveredCategoryBar.stock} units</span>
                    </p>
                    <p className="text-[10px] text-slate-400">
                      Products Count: <span className="text-white font-bold">{hoveredCategoryBar.count} products</span>
                    </p>
                    <p className="text-[10px] text-slate-400">
                      Category Share: <span className="text-blue-400 font-bold">{hoveredCategoryBar.pct}%</span>
                    </p>
                  </div>
                )}
                
                {/* Empty element to align height with monthly bar card */}
                <div className="h-6" />
              </div>
            </div>

            {/* ROW 3: LOW STOCK TRENDS */}
            <div className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-slate-100 dark:shadow-black/30 backdrop-blur-sm">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-sm font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">Low Stock Trends & Risks</h3>
                  <p className="text-[10px] text-slate-500 dark:text-slate-400">Critical products with stock levels below reorder limits</p>
                </div>
                <button
                  onClick={() => navigate('/low-stock')}
                  className="text-xs text-blue-600 dark:text-blue-400 hover:underline transition-colors font-semibold"
                >
                  Manage Alerts
                </button>
              </div>

              {lowStockAlerts.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {(() => {
                    const criticalItems = [...lowStockAlerts]
                      .sort((a, b) => {
                        const ratioA = a.qty / (a.reorder || 1);
                        const ratioB = b.qty / (b.reorder || 1);
                        return ratioA - ratioB;
                      })
                      .slice(0, 4);
                    
                    return criticalItems.map((item, idx) => {
                      const ratio = item.qty / (item.reorder || 1);
                      const progressWidth = Math.min(100, Math.max(0, ratio * 100));
                      const isCritical = item.status === 'Critical' || ratio <= 0.3;
                      return (
                        <div
                          key={item.id || idx}
                          className={`p-4 rounded-xl border flex flex-col justify-between gap-3 transition-all ${
                            isCritical
                              ? 'bg-rose-500/5 border-rose-500/10 hover:border-rose-500/30'
                              : 'bg-amber-500/5 border-amber-500/10 hover:border-amber-500/30'
                          }`}
                        >
                          <div className="flex justify-between items-start">
                            <div>
                              <h4 className="font-bold text-slate-800 dark:text-slate-200 text-xs">{item.name}</h4>
                              <span className="text-[9px] uppercase tracking-wider text-slate-400 font-medium block mt-0.5">{item.category}</span>
                            </div>
                            <span className={`px-2 py-0.5 rounded-full text-[9px] font-black uppercase font-mono tracking-wider ${
                              isCritical
                                ? 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                                : 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                            }`}>
                              {isCritical ? 'Critical Deficit' : 'Low Stock'}
                            </span>
                          </div>

                          <div className="space-y-1.5">
                            <div className="flex justify-between text-[10px] font-mono text-slate-500">
                              <span>Current: <strong className="text-slate-800 dark:text-white">{item.qty}</strong></span>
                              <span>Threshold: {item.reorder}</span>
                            </div>
                            <div className="w-full bg-slate-100 dark:bg-[#141b30] h-2 rounded-full overflow-hidden border border-slate-200/20 dark:border-slate-800/40">
                              <div
                                className={`h-full rounded-full transition-all duration-500 ${
                                  isCritical ? 'bg-rose-500 shadow-[0_0_8px_#f43f5e50]' : 'bg-amber-500 shadow-[0_0_8px_#f59e0b50]'
                                }`}
                                style={{ width: `${progressWidth}%` }}
                              />
                            </div>
                          </div>

                          <div className="flex justify-between items-center pt-2 border-t border-slate-100 dark:border-slate-900/40">
                            <span className="text-[9px] text-slate-400">
                              Deficit: <span className="font-bold font-mono text-rose-500/90">-{Math.max(0, item.reorder - item.qty)} units</span>
                            </span>
                            <button
                              onClick={() => handleReorder(item.id, item.name)}
                              className={`px-3 py-1.5 rounded-lg text-[10px] font-black transition-all cursor-pointer ${
                                isCritical
                                  ? 'bg-rose-600 hover:bg-rose-500 text-white shadow-md shadow-rose-500/10 hover:shadow-rose-500/20 border border-rose-500/20'
                                  : 'bg-blue-600 hover:bg-blue-500 text-white shadow-md shadow-blue-500/10 hover:shadow-blue-500/20 border border-blue-500/20'
                              }`}
                            >
                              Quick Reorder
                            </button>
                          </div>
                        </div>
                      );
                    });
                  })()}
                </div>
              ) : (
                <div className="p-8 border border-emerald-500/20 bg-emerald-500/5 rounded-xl text-center flex flex-col items-center justify-center gap-2">
                  <span className="text-xl text-emerald-400">✓</span>
                  <p className="text-xs font-bold text-emerald-400">All product stock levels are fully healthy!</p>
                  <p className="text-[10px] text-slate-400 mt-0.5">No low stock thresholds have been breached.</p>
                </div>
              )}
            </div>

            {/* Two-Column Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              
              {/* Interactive SVG Donut Chart for Categories */}
              <div className="lg:col-span-2 bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-slate-100 dark:shadow-black/30 backdrop-blur-sm space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-sm font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">Stock distribution</h3>
                    <p className="text-[10px] text-slate-500 dark:text-slate-400 mt-0.5">Item percentage share per category</p>
                  </div>
                  <button
                    onClick={() => navigate('/categories')}
                    className="text-xs text-blue-600 dark:text-blue-400 hover:underline transition-colors font-semibold"
                  >
                    View all
                  </button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
                  
                  {/* Left Column: Donut Drawing */}
                  {donutSlices.length > 0 ? (
                    <div className="relative flex justify-center items-center">
                      <svg width="180" height="180" viewBox="0 0 100 100" className="transform -rotate-90">
                        {donutSlices.map((slice, idx) => {
                          const isActive = activeCategoryIdx === idx;
                          return (
                            <circle
                              key={idx}
                              cx="50"
                              cy="50"
                              r={radius}
                              fill="transparent"
                              stroke={slice.color}
                              strokeWidth={isActive ? "10" : "8"}
                              strokeDasharray={circumference}
                              strokeDashoffset={slice.offset}
                              transform={`rotate(${slice.rotation} 50 50)`}
                              className="cursor-pointer transition-all duration-200"
                              style={{
                                filter: isActive ? `drop-shadow(0px 0px 4px ${slice.color}80)` : 'none'
                              }}
                              onMouseEnter={() => setActiveCategoryIdx(idx)}
                              onMouseLeave={() => setActiveCategoryIdx(null)}
                            />
                          );
                        })}
                      </svg>
                      
                      {/* Center Label */}
                      <div className="absolute text-center select-none pointer-events-none">
                        {activeCategoryIdx !== null ? (
                          <>
                            <p className="text-[10px] uppercase font-bold text-slate-400 dark:text-slate-500">
                              {donutSlices[activeCategoryIdx].name}
                            </p>
                            <p className="text-xl font-black text-slate-800 dark:text-white">
                              {donutSlices[activeCategoryIdx].percentage}%
                            </p>
                          </>
                        ) : (
                          <>
                            <p className="text-[10px] uppercase font-bold text-slate-400 dark:text-slate-500">Total Items</p>
                            <p className="text-2xl font-black text-slate-800 dark:text-white">{summary.totalProducts}</p>
                          </>
                        )}
                      </div>
                    </div>
                  ) : (
                    <div className="text-center text-slate-400 dark:text-slate-500 text-xs py-12">No data to display.</div>
                  )}

                  {/* Right Column: Legend with Progress Indicators */}
                  <div className="space-y-3.5">
                    {donutSlices.length > 0 ? (
                      donutSlices.map((category, idx) => {
                        const colorClass = categoryTailwindColors[idx % categoryTailwindColors.length];
                        const isActive = activeCategoryIdx === idx;
                        return (
                          <div
                            key={idx}
                            className={`p-2.5 rounded-xl border transition-all duration-150 cursor-pointer ${
                              isActive 
                                ? 'bg-slate-100/60 dark:bg-slate-900/40 border-slate-200 dark:border-slate-800 translate-x-1' 
                                : 'bg-transparent border-transparent hover:bg-slate-50 dark:hover:bg-slate-900/20'
                            }`}
                            onMouseEnter={() => setActiveCategoryIdx(idx)}
                            onMouseLeave={() => setActiveCategoryIdx(null)}
                          >
                            <div className="flex items-center justify-between text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                              <div className="flex items-center gap-2">
                                <span
                                  className="w-2.5 h-2.5 rounded-full inline-block"
                                  style={{ backgroundColor: category.color }}
                                />
                                <span>{category.name}</span>
                              </div>
                              <div className="space-x-2 font-mono text-[10px]">
                                <span className="text-slate-400 dark:text-slate-500">{category.count} items</span>
                                <span className="text-slate-900 dark:text-white font-bold">{category.percentage}%</span>
                              </div>
                            </div>
                            <div className="w-full bg-slate-100 dark:bg-[#141b30] h-1.5 rounded-full overflow-hidden">
                              <div
                                className={`${colorClass} h-full rounded-full transition-all duration-700`}
                                style={{ width: `${category.percentage}%` }}
                              />
                            </div>
                          </div>
                        );
                      })
                    ) : (
                      <p className="text-center text-xs text-slate-400 dark:text-slate-500">No categories recorded.</p>
                    )}
                  </div>

                </div>
              </div>

              {/* Recent Transactions Panel */}
              <div className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-slate-100 dark:shadow-black/30 backdrop-blur-sm space-y-5">
                <div className="flex items-center justify-between">
                  <h3 className="text-sm font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">Recent transactions</h3>
                  <button
                    onClick={() => navigate('/stock')}
                    className="text-xs text-blue-600 dark:text-blue-400 hover:underline transition-colors font-semibold"
                  >
                    See all
                  </button>
                </div>

                <div className="space-y-3.5">
                  {recentTx.length > 0 ? (
                    recentTx.map((tx, idx) => {
                      const isIncoming = tx.type?.toUpperCase() === 'IN';
                      const bgClass = isIncoming
                        ? 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20'
                        : 'bg-rose-500/10 text-rose-500 dark:text-rose-400 border border-rose-500/20';
                      return (
                        <div
                          key={tx.id || idx}
                          className="flex items-center justify-between py-1 border-b border-slate-100 dark:border-slate-900/40 last:border-0"
                        >
                          <div className="flex items-center gap-3">
                            <span className={`w-8 h-8 rounded-lg flex items-center justify-center text-[10px] font-black font-mono leading-none ${bgClass}`}>
                              {tx.type}
                            </span>
                            <div className="space-y-0.5">
                              <p className="font-bold text-slate-800 dark:text-slate-200 text-xs">{tx.product}</p>
                              <p className="text-[10px] text-slate-400 dark:text-slate-500 font-medium font-mono">{tx.date || 'unknown date'}</p>
                            </div>
                          </div>
                          <span className={`font-mono font-black text-xs ${isIncoming ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-500 dark:text-rose-400'}`}>
                            {isIncoming ? `+${tx.qty}` : `-${tx.qty}`}
                          </span>
                        </div>
                      );
                    })
                  ) : (
                    <p className="text-center text-xs text-slate-400 dark:text-slate-500 py-6">No recent transactions recorded.</p>
                  )}
                </div>
              </div>

            </div>
          </>
        )}

      </div>
    </MainLayout>
  );
}

export default DashboardPage;
