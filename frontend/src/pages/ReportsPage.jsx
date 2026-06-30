import React, { useState, useEffect } from 'react';
import MainLayout from '../components/layout/MainLayout';
import { reportAPI } from '../services/api';

function ReportsPage() {
  const [activeTab, setActiveTab] = useState('inventory');
  const [inventoryData, setInventoryData] = useState([]);
  const [lowStockData, setLowStockData] = useState([]);
  const [supplierData, setSupplierData] = useState([]);
  
  const [loading, setLoading] = useState(false);
  const [exporting, setExporting] = useState(null); // 'pdf' or 'excel' or null
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState('');

  const fetchReportData = async (tab) => {
    setLoading(true);
    setError('');
    try {
      if (tab === 'inventory') {
        const res = await reportAPI.getInventoryReport();
        setInventoryData(res.data || []);
      } else if (tab === 'low-stock') {
        const res = await reportAPI.getLowStockReport();
        setLowStockData(res.data || []);
      } else if (tab === 'supplier') {
        const res = await reportAPI.getSupplierReport();
        setSupplierData(res.data || []);
      }
    } catch (err) {
      setError(err.message || 'Failed to load report data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReportData(activeTab);
  }, [activeTab]);

  const handleExport = async (format) => {
    setExporting(format);
    setError('');
    try {
      let res;
      let filename = '';
      const dateStr = new Date().toISOString().split('T')[0];

      if (activeTab === 'inventory') {
        res = await reportAPI.exportInventoryReport(format);
        filename = `inventory_report_${dateStr}`;
      } else if (activeTab === 'low-stock') {
        res = await reportAPI.exportLowStockReport(format);
        filename = `low_stock_report_${dateStr}`;
      } else if (activeTab === 'supplier') {
        res = await reportAPI.exportSupplierReport(format);
        filename = `supplier_performance_report_${dateStr}`;
      }

      const contentType = format === 'excel'
        ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        : 'application/pdf';
      const extension = format === 'excel' ? 'xlsx' : 'pdf';

      const blob = new Blob([res.data], { type: contentType });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${filename}.${extension}`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (err) {
      setError(err.message || 'Failed to export report');
    } finally {
      setExporting(null);
    }
  };

  const getFilteredData = () => {
    const term = searchTerm.toLowerCase();
    if (activeTab === 'inventory') {
      return inventoryData.filter(item => 
        (item.productName?.toLowerCase() || '').includes(term) ||
        (item.categoryName?.toLowerCase() || '').includes(term) ||
        (item.supplierNames?.toLowerCase() || '').includes(term)
      );
    } else if (activeTab === 'low-stock') {
      return lowStockData.filter(item => 
        (item.productName?.toLowerCase() || '').includes(term) ||
        (item.categoryName?.toLowerCase() || '').includes(term) ||
        (item.supplierNames?.toLowerCase() || '').includes(term)
      );
    } else if (activeTab === 'supplier') {
      return supplierData.filter(item => 
        (item.supplierName?.toLowerCase() || '').includes(term) ||
        (item.categorySupplied?.toLowerCase() || '').includes(term) ||
        (item.status?.toLowerCase() || '').includes(term)
      );
    }
    return [];
  };

  const filtered = getFilteredData();

  return (
    <MainLayout title="Reports Control Center">
      <div className="p-6 relative z-10 space-y-6">
        {/* Error notification banner */}
        {error && (
          <div className="p-4 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-xl text-xs font-semibold flex justify-between items-center">
            <span>⚠️ {error}</span>
            <button onClick={() => setError('')} className="font-bold opacity-80 hover:opacity-100">&times;</button>
          </div>
        )}

        {/* Tab Controls & Export Actions Row */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-4 rounded-2xl shadow-lg backdrop-blur-sm">
          {/* Tabs */}
          <div className="flex flex-wrap gap-1">
            <button
              onClick={() => { setActiveTab('inventory'); setSearchTerm(''); }}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                activeTab === 'inventory'
                  ? 'bg-blue-600 text-white shadow-md shadow-blue-500/10'
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              📋 Inventory Report
            </button>
            <button
              onClick={() => { setActiveTab('low-stock'); setSearchTerm(''); }}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                activeTab === 'low-stock'
                  ? 'bg-red-600 text-white shadow-md shadow-red-500/10'
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              ⚠️ Low Stock Report
            </button>
            <button
              onClick={() => { setActiveTab('supplier'); setSearchTerm(''); }}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                activeTab === 'supplier'
                  ? 'bg-emerald-600 text-white shadow-md shadow-emerald-500/10'
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              🤝 Supplier Performance
            </button>
          </div>

          {/* Export Buttons */}
          <div className="flex items-center gap-2">
            <button
              disabled={exporting !== null || loading || filtered.length === 0}
              onClick={() => handleExport('pdf')}
              className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-slate-800 border border-slate-700 text-white hover:bg-slate-700 disabled:opacity-40 disabled:cursor-not-allowed transition-all text-xs font-semibold cursor-pointer"
            >
              {exporting === 'pdf' ? (
                <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              )}
              Export PDF
            </button>
            <button
              disabled={exporting !== null || loading || filtered.length === 0}
              onClick={() => handleExport('excel')}
              className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-emerald-600 text-white hover:bg-emerald-500 disabled:opacity-40 disabled:cursor-not-allowed transition-all text-xs font-semibold cursor-pointer border border-emerald-400/20 shadow-lg shadow-emerald-500/10"
            >
              {exporting === 'excel' ? (
                <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              )}
              Export Excel
            </button>
          </div>
        </div>

        {/* Filter bar */}
        <div className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 p-4 rounded-2xl flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-lg backdrop-blur-sm">
          <div className="relative flex-1 max-w-sm">
            <input
              type="text"
              placeholder={`Filter preview by name or category...`}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 rounded-xl text-xs bg-slate-100 dark:bg-slate-900/60 border border-slate-200 dark:border-slate-800 text-slate-800 dark:text-white placeholder-slate-400 dark:placeholder-slate-500 focus:outline-none focus:border-blue-500/50 transition-colors"
            />
            <svg className="w-4 h-4 text-slate-400 dark:text-slate-500 absolute left-3.5 top-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <div className="text-[10px] text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider">
            Showing {filtered.length} entries
          </div>
        </div>

        {/* Preview Table Container */}
        <div className="bg-white dark:bg-[#0b1224]/85 border border-slate-200/80 dark:border-[#1e293b]/70 rounded-2xl overflow-hidden shadow-lg backdrop-blur-sm">
          {loading ? (
            <div className="flex flex-col items-center justify-center py-20 gap-3">
              <span className="w-8 h-8 border-3 border-blue-600/30 border-t-blue-600 rounded-full animate-spin" />
              <p className="text-xs text-slate-500 font-semibold">Generating preview...</p>
            </div>
          ) : filtered.length > 0 ? (
            <div className="overflow-x-auto scrollbar-thin">
              <table className="w-full min-w-[640px] border-collapse text-left text-xs">
                <thead>
                  <tr className="bg-slate-50 dark:bg-slate-900/40 border-b border-slate-200/80 dark:border-[#1e293b]/70 text-slate-500 dark:text-slate-400 font-bold uppercase tracking-wider font-mono">
                    {activeTab === 'inventory' && (
                      <>
                        <th className="p-4 w-[35%]">Product Name</th>
                        <th className="p-4 w-[20%]">Category</th>
                        <th className="p-4 w-[15%] text-center">Current Qty</th>
                        <th className="p-4 w-[30%]">Active Suppliers</th>
                      </>
                    )}
                    {activeTab === 'low-stock' && (
                      <>
                        <th className="p-4 w-[28%]">Product Name</th>
                        <th className="p-4 w-[18%]">Category</th>
                        <th className="p-4 w-[14%] text-center">Current Qty</th>
                        <th className="p-4 w-[14%] text-center">Threshold</th>
                        <th className="p-4 w-[26%]">Active Suppliers</th>
                      </>
                    )}
                    {activeTab === 'supplier' && (
                      <>
                        <th className="p-4 w-[28%]">Supplier Name</th>
                        <th className="p-4 w-[22%]">Category Supplied</th>
                        <th className="p-4 w-[12%] text-center">Status</th>
                        <th className="p-4 w-[20%] text-center">Products in Category</th>
                        <th className="p-4 w-[18%] text-center">Total Units Stock</th>
                      </>
                    )}
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800/40">
                  {filtered.map((item, idx) => (
                    <tr key={idx} className="hover:bg-slate-50/50 dark:hover:bg-slate-900/10 transition-colors">
                      {activeTab === 'inventory' && (
                        <>
                          <td className="p-4 font-bold text-slate-800 dark:text-slate-200 text-sm">{item.productName}</td>
                          <td className="p-4">
                            <span className="px-2.5 py-1 rounded-md bg-blue-500/10 border border-blue-500/25 text-blue-600 dark:text-blue-400 font-bold text-[10px]">
                              {item.categoryName || 'Uncategorized'}
                            </span>
                          </td>
                          <td className="p-4 text-center font-bold text-slate-700 dark:text-slate-300 font-mono text-sm">{item.currentQty}</td>
                          <td className="p-4 text-slate-500 dark:text-slate-400 font-medium max-w-[200px] truncate">{item.supplierNames || 'N/A'}</td>
                        </>
                      )}
                      {activeTab === 'low-stock' && (
                        <>
                          <td className="p-4 font-bold text-slate-800 dark:text-slate-200 text-sm">{item.productName}</td>
                          <td className="p-4">
                            <span className="px-2.5 py-1 rounded-md bg-red-500/10 border border-red-500/25 text-red-600 dark:text-red-400 font-bold text-[10px]">
                              {item.categoryName || 'Uncategorized'}
                            </span>
                          </td>
                          <td className="p-4 text-center text-red-500 font-black font-mono text-sm">{item.currentQty}</td>
                          <td className="p-4 text-center text-slate-500 dark:text-slate-400 font-mono font-medium">{item.reorderThreshold}</td>
                          <td className="p-4 text-slate-500 dark:text-slate-400 font-medium max-w-[200px] truncate">{item.supplierNames || 'N/A'}</td>
                        </>
                      )}
                      {activeTab === 'supplier' && (
                        <>
                          <td className="p-4 font-bold text-slate-800 dark:text-slate-200 text-sm">
                            <div>{item.supplierName}</div>
                            <div className="text-[10px] text-slate-400 dark:text-slate-500 font-normal mt-0.5">{item.email}</div>
                          </td>
                          <td className="p-4 text-slate-600 dark:text-slate-400 font-medium">{item.categorySupplied}</td>
                          <td className="p-4 text-center">
                            <span className={`px-2.5 py-0.5 rounded-full font-black text-[9px] uppercase tracking-wider ${
                              item.status === 'Active'
                                ? 'bg-emerald-500/10 text-emerald-500 dark:text-emerald-400 border border-emerald-500/20'
                                : 'bg-amber-500/10 text-amber-500 dark:text-amber-400 border border-amber-500/20'
                            }`}>
                              {item.status}
                            </span>
                          </td>
                          <td className="p-4 text-center text-slate-600 dark:text-slate-400 font-mono font-medium">{item.totalProducts}</td>
                          <td className="p-4 text-center font-bold text-slate-800 dark:text-slate-200 font-mono text-sm">{item.totalStockQuantity}</td>
                        </>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="text-center py-20 text-slate-500 font-semibold">
              No matching records found for this preview.
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
}

export default ReportsPage;
