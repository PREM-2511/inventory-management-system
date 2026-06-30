import React, { useState, useEffect } from 'react';
import MainLayout from '../components/layout/MainLayout';
import { auditTrailAPI } from '../services/api';

function AuditTrailPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [size] = useState(10);

  // Filters
  const [entityType, setEntityType] = useState('ALL');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const fetchLogs = async () => {
    try {
      setLoading(true);
      setError('');
      
      const params = {
        page,
        size,
        entityType: entityType === 'ALL' ? undefined : entityType,
        startDate: startDate || undefined,
        endDate: endDate || undefined,
      };

      const res = await auditTrailAPI.getAll(params);
      
      if (res.data) {
        setLogs(res.data.content || []);
        setTotalItems(res.data.totalElements || 0);
        setTotalPages(res.data.totalPages || 1);
      }
    } catch (err) {
      setError(err.message || 'Failed to load audit trail logs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, [page, entityType, startDate, endDate]);

  const handleResetFilters = () => {
    setEntityType('ALL');
    setStartDate('');
    setEndDate('');
    setPage(1);
  };

  const getActionBadgeClass = (action) => {
    switch (action) {
      case 'CREATE':
        return 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20';
      case 'UPDATE':
        return 'bg-blue-500/10 text-blue-400 border border-blue-500/20';
      case 'DELETE':
        return 'bg-rose-500/10 text-rose-400 border border-rose-500/20';
      case 'STOCK_IN':
        return 'bg-teal-500/10 text-teal-400 border border-teal-500/20';
      case 'STOCK_OUT':
        return 'bg-amber-500/10 text-amber-400 border border-amber-500/20';
      default:
        return 'bg-slate-500/10 text-slate-400 border border-slate-500/20';
    }
  };

  const renderChanges = (oldVal, newVal, action) => {
    if (!oldVal && !newVal) {
      return <span className="text-slate-500 font-mono text-[11px]">No value details</span>;
    }

    if (action === 'CREATE') {
      return (
        <div className="flex flex-wrap gap-1">
          {newVal.split(', ').map((field, idx) => (
            <span
              key={idx}
              className="text-[11px] font-mono text-emerald-400 bg-emerald-500/10 px-1.5 py-0.5 rounded border border-emerald-500/20"
            >
              {field}
            </span>
          ))}
        </div>
      );
    }

    if (action === 'DELETE') {
      return (
        <div className="flex flex-wrap gap-1">
          {oldVal.split(', ').map((field, idx) => (
            <span
              key={idx}
              className="text-[11px] font-mono text-rose-400 bg-rose-500/10 px-1.5 py-0.5 rounded border border-rose-500/20"
            >
              {field}
            </span>
          ))}
        </div>
      );
    }

    // UPDATE
    const oldFields = oldVal ? oldVal.split('; ') : [];
    const newFields = newVal ? newVal.split('; ') : [];
    
    return (
      <div className="space-y-1.5 flex flex-col max-w-full">
        {oldFields.map((oldField, idx) => {
          const newField = newFields[idx] || '';
          return (
            <div key={idx} className="flex flex-wrap items-center gap-1.5 text-[11px] font-mono leading-none">
              <span className="text-rose-400 bg-rose-500/10 px-1.5 py-0.5 rounded border border-rose-500/20 truncate max-w-[200px]">
                {oldField}
              </span>
              <span className="text-slate-500">→</span>
              <span className="text-emerald-400 bg-emerald-500/10 px-1.5 py-0.5 rounded border border-emerald-500/20 truncate max-w-[200px]">
                {newField}
              </span>
            </div>
          );
        })}
      </div>
    );
  };

  return (
    <MainLayout title="Audit Trail">
      <div className="p-6 relative z-10 space-y-6">
        
        {/* Header Summary Banner */}
        <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-5 rounded-2xl shadow-lg backdrop-blur-sm flex items-start gap-4">
          <div className="w-10 h-10 rounded-xl bg-blue-500/10 border border-blue-500/20 flex items-center justify-center text-lg text-blue-400 font-bold">
            📜
          </div>
          <div className="space-y-1 flex-1">
            <div className="flex items-center justify-between">
              <h4 className="font-bold text-slate-200 text-sm">System Audit & Change History</h4>
              <span className="text-xs text-slate-400 font-mono">
                Total records: <span className="text-blue-400 font-bold">{totalItems}</span>
              </span>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed max-w-2xl">
              This log stores the change history for key system resources. Every create, update, delete, and stock movement is tracked dynamically with precise user information, action type, and field-level diffs.
            </p>
          </div>
        </div>

        {/* Filters Panel */}
        <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-4 rounded-2xl shadow-md backdrop-blur-sm">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
            
            {/* Entity Type Filter */}
            <div className="space-y-1.5">
              <label className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Entity Type</label>
              <select
                value={entityType}
                onChange={(e) => { setEntityType(e.target.value); setPage(1); }}
                className="w-full bg-[#141b30] border border-[#1e293b] rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-blue-500 font-medium"
              >
                <option value="ALL">All Entities</option>
                <option value="1">Products</option>
                <option value="2">Categories</option>
                <option value="3">Suppliers</option>
                <option value="4">Stock Movements</option>
              </select>
            </div>

            {/* Start Date */}
            <div className="space-y-1.5">
              <label className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Start Date</label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => { setStartDate(e.target.value); setPage(1); }}
                className="w-full bg-[#141b30] border border-[#1e293b] rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-blue-500 font-medium"
              />
            </div>

            {/* End Date */}
            <div className="space-y-1.5">
              <label className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">End Date</label>
              <input
                type="date"
                value={endDate}
                onChange={(e) => { setEndDate(e.target.value); setPage(1); }}
                className="w-full bg-[#141b30] border border-[#1e293b] rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-blue-500 font-medium"
              />
            </div>

            {/* Reset Button */}
            <div>
              <button
                onClick={handleResetFilters}
                className="w-full px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-bold text-xs rounded-xl border border-slate-700/60 transition-all cursor-pointer"
              >
                Reset Filters
              </button>
            </div>

          </div>
        </div>

        {/* Error */}
        {error && (
          <div className="p-4 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-xl text-xs font-semibold">
            ⚠️ {error}
          </div>
        )}

        {/* Log Table */}
        <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 rounded-2xl shadow-xl overflow-hidden backdrop-blur-sm">
          {loading ? (
            <div className="text-center text-slate-500 py-16 font-semibold">Loading audit logs...</div>
          ) : logs.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-[#1e293b]/70 bg-[#090f1d]/50 text-slate-400 text-[10px] font-extrabold uppercase tracking-wider">
                    <th className="px-5 py-4">User</th>
                    <th className="px-5 py-4">Action</th>
                    <th className="px-5 py-4">Entity</th>
                    <th className="px-5 py-4">Change Details (Old → New)</th>
                    <th className="px-5 py-4 text-right">Time</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#1e293b]/40">
                  {logs.map((log) => (
                    <tr key={log.id} className="hover:bg-[#141c2f]/20 transition-colors text-xs">
                      {/* User Email */}
                      <td className="px-5 py-4 text-slate-300 font-medium font-mono truncate max-w-[200px]">
                        {log.userEmail}
                      </td>

                      {/* Action Badge */}
                      <td className="px-5 py-4">
                        <span className={`px-2.5 py-0.5 rounded-full text-[9px] font-black uppercase font-mono tracking-wider ${getActionBadgeClass(log.action)}`}>
                          {log.action}
                        </span>
                      </td>

                      {/* Entity Description */}
                      <td className="px-5 py-4 space-y-1">
                        <div className="flex items-center gap-1.5">
                          <span className="px-1.5 py-0.5 rounded bg-slate-800 text-slate-400 text-[9px] font-extrabold uppercase tracking-wider font-mono">
                            {log.entityType}
                          </span>
                          <span className="font-semibold text-slate-200">
                            {log.entityName}
                          </span>
                        </div>
                        {log.entityId && (
                          <div className="text-[10px] text-slate-500 font-mono">
                            ID: #{log.entityId}
                          </div>
                        )}
                      </td>

                      {/* Values changed */}
                      <td className="px-5 py-4">
                        {renderChanges(log.oldValue, log.newValue, log.action)}
                      </td>

                      {/* Time */}
                      <td className="px-5 py-4 text-right text-slate-400 font-mono text-[11px]">
                        {log.createdAt}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="text-center text-slate-500 py-16 font-semibold flex flex-col items-center justify-center gap-2">
              <span className="text-2xl">📋</span>
              <span>No audit logs found matching the selected filters.</span>
            </div>
          )}

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="px-5 py-4 border-t border-[#1e293b]/70 bg-[#090f1d]/20 flex items-center justify-between">
              <button
                disabled={page === 1 || loading}
                onClick={() => setPage(page - 1)}
                className="px-3.5 py-1.5 bg-slate-800 hover:bg-slate-700 disabled:opacity-30 text-slate-300 font-bold text-xs rounded-lg transition-all cursor-pointer border border-slate-700/60"
              >
                ← Previous
              </button>
              <span className="text-xs font-semibold text-slate-400">
                Page <span className="text-slate-200">{page}</span> of <span className="text-slate-200">{totalPages}</span>
              </span>
              <button
                disabled={page === totalPages || loading}
                onClick={() => setPage(page + 1)}
                className="px-3.5 py-1.5 bg-slate-800 hover:bg-slate-700 disabled:opacity-30 text-slate-300 font-bold text-xs rounded-lg transition-all cursor-pointer border border-slate-700/60"
              >
                Next →
              </button>
            </div>
          )}
        </div>

      </div>
    </MainLayout>
  );
}

export default AuditTrailPage;
