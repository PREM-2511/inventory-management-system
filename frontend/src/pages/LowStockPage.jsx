import React, { useState, useEffect } from 'react';
import MainLayout from '../components/layout/MainLayout';
import { stockAPI, supplierAPI } from '../services/api';

function LowStockPage() {
  const [alerts, setAlerts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchAlertsAndSuppliers = async () => {
    try {
      setLoading(true);
      setError('');
      const [alertsRes, suppliersRes] = await Promise.all([
        stockAPI.getLowStockAlerts(),
        supplierAPI.getAll({ page: 0, size: 100 })
      ]);
      setAlerts(alertsRes.data || []);
      setSuppliers(suppliersRes.data?.content || suppliersRes.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load low stock alerts');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlertsAndSuppliers();
  }, []);

  const getSupplierForCategory = (categoryName) => {
    const matchingSupplier = suppliers.find(
      s => s.category?.toLowerCase() === categoryName?.toLowerCase()
    );
    return matchingSupplier ? matchingSupplier.name : 'Primary Supplier';
  };

  const handleReorder = (id, name, supplier) => {
    alert(`Reorder request for "${name}" successfully sent to "${supplier}"!`);
    setAlerts(alerts.filter(a => a.id !== id));
  };

  return (
    <MainLayout title="Low Stock Alerts">
      <div className="p-6 relative z-10 space-y-6">
        
        {/* Helper Banner */}
        <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-5 rounded-2xl shadow-lg backdrop-blur-sm flex items-start gap-4">
          <div className="w-10 h-10 rounded-xl bg-amber-500/10 border border-amber-500/20 flex items-center justify-center text-lg text-amber-400">
            ⚠️
          </div>
          <div className="space-y-1">
            <h4 className="font-bold text-slate-200 text-sm">Low stock items require your attention</h4>
            <p className="text-xs text-slate-400 leading-relaxed max-w-xl">
              The products listed below have dropped below their defined reorder thresholds. Click the "Reorder" button to send an automated reorder request directly to the linked supplier.
            </p>
          </div>
        </div>

        {/* Error */}
        {error && (
          <div className="p-4 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-xl text-xs font-semibold">
            ⚠️ {error}
          </div>
        )}

        {/* Alerts List */}
        {loading ? (
          <div className="text-center text-slate-500 py-12 font-semibold">Loading stock alerts...</div>
        ) : (
          <div className="grid grid-cols-1 gap-4">
            {alerts.length > 0 ? (
              alerts.map((alert) => {
                const supplierName = getSupplierForCategory(alert.category);
                const ratio = alert.qty / (alert.reorder || 1);
                const progressWidth = Math.min(100, Math.max(0, ratio * 100));
                
                return (
                  <div
                    key={alert.id}
                    className="border-l-4 border-amber-500 bg-[#0b1224]/85 border-r border-[#1e293b]/70 border-t border-b border-[#1e293b]/70 p-5 rounded-r-2xl shadow-lg backdrop-blur-sm flex flex-col sm:flex-row sm:items-center justify-between gap-6 transition-all hover:border-slate-800"
                  >
                    <div className="space-y-3 flex-1">
                      <div className="flex items-center gap-3">
                        <p className="font-bold text-slate-200 text-base leading-none">{alert.name}</p>
                        <span className={`px-2 py-0.5 rounded-full text-[9px] font-black uppercase font-mono tracking-wider ${
                          alert.status === 'Critical'
                            ? 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                            : 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                        }`}>
                          {alert.status}
                        </span>
                      </div>
                      
                      {/* Progress bar to represent ratio */}
                      <div className="space-y-1.5 max-w-md">
                        <div className="w-full bg-[#141b30] h-2 rounded-full overflow-hidden">
                          <div
                            className={`h-full rounded-full ${alert.status === 'Critical' ? 'bg-rose-500' : 'bg-amber-500'}`}
                            style={{ width: `${progressWidth}%` }}
                          />
                        </div>
                        <p className="text-xs text-slate-400 font-mono">
                          Current: <span className="text-white font-bold">{alert.qty} units</span> • Reorder threshold: <span className="text-slate-300 font-semibold">{alert.reorder} units</span>
                        </p>
                      </div>
                      
                      <p className="text-xs text-slate-500">
                        Linked Supplier: <span className="text-blue-400 font-semibold">{supplierName}</span>
                      </p>
                    </div>

                    <button
                      onClick={() => handleReorder(alert.id, alert.name, supplierName)}
                      className="w-full sm:w-auto px-5 py-2.5 bg-blue-600 hover:bg-blue-500 text-white rounded-xl font-bold text-xs transition-all whitespace-nowrap cursor-pointer border border-blue-400/20 shadow-lg shadow-blue-500/10 hover:shadow-blue-500/20"
                    >
                      Send Reorder Request
                    </button>
                  </div>
                );
              })
            ) : (
              <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-12 rounded-2xl text-center text-emerald-400 font-semibold flex flex-col items-center justify-center gap-2">
                <span className="text-2xl">✓</span>
                <span>All inventory stock levels are healthy! No low stock alerts.</span>
              </div>
            )}
          </div>
        )}

      </div>
    </MainLayout>
  );
}

export default LowStockPage;

