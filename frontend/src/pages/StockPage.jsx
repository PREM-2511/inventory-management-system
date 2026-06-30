import React, { useState, useEffect } from 'react';
import MainLayout from '../components/layout/MainLayout';
import { stockAPI, productAPI } from '../services/api';

function StockPage() {
  const [dbMovements, setDbMovements] = useState([]);
  const [dbProducts, setDbProducts] = useState([]);
  const [filterType, setFilterType] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  
  // Modal states
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({ type: 'IN', product: '', qty: 1 });

  const fetchMovements = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await stockAPI.getHistory();
      setDbMovements(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load stock movement history');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMovements();
  }, []);

  useEffect(() => {
    const fetchDropdownProducts = async () => {
      try {
        const res = await productAPI.getAll({ page: 0, size: 100 });
        setDbProducts(res.data.content || res.data || []);
      } catch (err) {
        console.error('Failed to load products for dropdown selector', err);
      }
    };
    fetchDropdownProducts();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload = {
        type: formData.type,
        product: formData.product,
        qty: Number(formData.qty)
      };
      await stockAPI.logMovement(payload);
      setIsModalOpen(false);
      fetchMovements();
    } catch (err) {
      alert(err.message || 'Failed to log stock movement');
    } finally {
      setSubmitting(false);
    }
  };

  const filteredMovements = dbMovements.filter(m => {
    if (filterType === 'ALL') return true;
    return m.type === filterType;
  });

  return (
    <MainLayout title="Stock Movements">
      <div className="p-6 relative z-10 space-y-6">
        
        {/* Controls bar */}
        <div className="flex flex-col sm:flex-row gap-4 items-stretch sm:items-center justify-between bg-[#0b1224]/85 border border-[#1e293b]/70 p-4 rounded-2xl shadow-lg backdrop-blur-sm">
          {/* Tabs */}
          <div className="flex gap-2 p-1 bg-slate-900/60 border border-slate-800 rounded-xl max-w-xs">
            {['ALL', 'IN', 'OUT'].map((t) => (
              <button
                key={t}
                onClick={() => setFilterType(t)}
                className={`px-4 py-1.5 rounded-lg text-[10px] font-black uppercase tracking-wider transition-all cursor-pointer ${
                  filterType === t
                    ? 'bg-blue-600 text-white shadow-md'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                {t}
              </button>
            ))}
          </div>

          <button
            onClick={() => {
              setFormData({ type: 'IN', product: dbProducts[0]?.name || '', qty: 5 });
              setIsModalOpen(true);
            }}
            disabled={dbProducts.length === 0}
            className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 px-5 py-2.5 rounded-xl text-xs font-bold leading-tight transition-all cursor-pointer border border-blue-400/20 text-white shadow-lg shadow-blue-500/10"
          >
            <span>+ Record movement</span>
          </button>
        </div>

        {/* Error */}
        {error && (
          <div className="p-4 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-xl text-xs font-semibold">
            ⚠️ {error}
          </div>
        )}

        {/* Movements Table */}
        <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 rounded-2xl shadow-lg overflow-hidden backdrop-blur-sm">
          {loading ? (
            <div className="text-center text-slate-500 py-12 font-semibold">Loading history...</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs border-collapse">
                <thead>
                  <tr className="border-b border-[#1e293b]/70 bg-slate-900/40 text-slate-400 font-mono">
                    <th className="p-4 uppercase font-semibold tracking-wider">Type</th>
                    <th className="p-4 uppercase font-semibold tracking-wider">Product</th>
                    <th className="p-4 uppercase font-semibold tracking-wider text-center">Quantity</th>
                    <th className="p-4 uppercase font-semibold tracking-wider">Date & Time</th>
                    <th className="p-4 uppercase font-semibold tracking-wider">Logged By</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/40 font-medium">
                  {filteredMovements.length > 0 ? (
                    filteredMovements.map((movement) => (
                      <tr key={movement.id} className="hover:bg-slate-900/10 transition-colors">
                        <td className="p-4">
                          <span className={`px-2.5 py-0.5 rounded-lg text-[9px] font-black uppercase font-mono tracking-wider inline-block ${
                            movement.type === 'IN'
                              ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                              : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                          }`}>
                            {movement.type}
                          </span>
                        </td>
                        <td className="p-4 font-bold text-slate-200 text-sm">{movement.product}</td>
                        <td className="p-4 text-center font-bold text-white font-mono text-sm">{movement.qty}</td>
                        <td className="p-4 text-slate-400 font-mono">{movement.date}</td>
                        <td className="p-4 text-slate-400">{movement.user}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="5" className="p-8 text-center text-slate-500 font-semibold">
                        No stock movements found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Modal Form Overlay */}
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
            <div className="w-full max-w-sm bg-[#0c1328] border border-gray-800 rounded-2xl p-6 space-y-6 shadow-2xl">
              <h3 className="text-base font-bold text-white tracking-tight">Record Stock Movement</h3>
              
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Movement Type</label>
                  <select
                    value={formData.type}
                    onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs cursor-pointer"
                  >
                    <option value="IN">IN (Receive Stock)</option>
                    <option value="OUT">OUT (Dispatch Stock)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Select Product</label>
                  <select
                    value={formData.product}
                    onChange={(e) => setFormData({ ...formData, product: e.target.value })}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs cursor-pointer"
                  >
                    {dbProducts.map((p) => (
                      <option key={p.id} value={p.name}>{p.name}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Quantity</label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={formData.qty}
                    onChange={(e) => setFormData({ ...formData, qty: e.target.value })}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs font-mono"
                  />
                </div>

                <div className="flex items-center justify-end gap-3 pt-2">
                  <button
                    type="button"
                    onClick={() => setIsModalOpen(false)}
                    className="px-4 py-2.5 border border-gray-800 bg-[#080d1a] hover:bg-slate-900/60 rounded-xl text-xs font-bold transition-all cursor-pointer text-slate-300"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={submitting}
                    className="px-5 py-2.5 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 rounded-xl text-xs font-bold text-white border border-blue-400/20 shadow-lg shadow-blue-500/10 cursor-pointer"
                  >
                    {submitting ? 'Logging...' : 'Log Movement'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

      </div>
    </MainLayout>
  );
}

export default StockPage;

