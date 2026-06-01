import React, { useState } from 'react';
import MainLayout from '../components/layout/MainLayout';

const INITIAL_SUPPLIERS = [
  { id: 1, name: 'TechLink Pvt Ltd', initials: 'TL', category: 'Electronics', status: 'Active', email: 'info@techlink.lk', phone: '+94 11 234 5678', color: 'from-blue-500 to-indigo-600' },
  { id: 2, name: 'OfficeFirst Co.', initials: 'OF', category: 'Office Supplies', status: 'Active', email: 'contact@officefirst.lk', phone: '+94 11 876 5432', color: 'from-emerald-500 to-teal-500' },
  { id: 3, name: 'FurniMart', initials: 'FM', category: 'Furniture', status: 'Pending', email: 'sales@furnimart.lk', phone: '+94 77 111 2222', color: 'from-amber-500 to-orange-500' },
];

function SuppliersPage() {
  const [suppliers, setSuppliers] = useState(INITIAL_SUPPLIERS);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Modal/Form States
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState(null);
  const [formData, setFormData] = useState({ name: '', category: 'Electronics', status: 'Active', email: '', phone: '' });

  const getInitials = (name) => {
    if (!name) return 'SU';
    const parts = name.trim().split(/\s+/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
  };

  const getGradientColor = (initials) => {
    const gradients = [
      'from-blue-500 to-indigo-600',
      'from-emerald-500 to-teal-500',
      'from-amber-500 to-orange-500',
      'from-purple-500 to-pink-500',
      'from-cyan-500 to-blue-600'
    ];
    const code = initials.charCodeAt(0) + (initials.charCodeAt(1) || 0);
    return gradients[code % gradients.length];
  };

  const handleOpenAddModal = () => {
    setEditingSupplier(null);
    setFormData({ name: '', category: 'Electronics', status: 'Active', email: '', phone: '' });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (supplier) => {
    setEditingSupplier(supplier);
    setFormData({ name: supplier.name, category: supplier.category, status: supplier.status, email: supplier.email, phone: supplier.phone });
    setIsModalOpen(true);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const initials = getInitials(formData.name);
    
    if (editingSupplier) {
      setSuppliers(suppliers.map(s => s.id === editingSupplier.id ? {
        ...s,
        name: formData.name,
        initials,
        category: formData.category,
        status: formData.status,
        email: formData.email,
        phone: formData.phone
      } : s));
    } else {
      const newSupplier = {
        id: Date.now(),
        name: formData.name,
        initials,
        category: formData.category,
        status: formData.status,
        email: formData.email,
        phone: formData.phone,
        color: getGradientColor(initials)
      };
      setSuppliers([...suppliers, newSupplier]);
    }
    setIsModalOpen(false);
  };

  const filteredSuppliers = suppliers.filter(s =>
    s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    s.category.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <MainLayout title="Supplier Management">
      <div className="p-6 relative z-10 space-y-6">
        
        {/* Controls Bar */}
        <div className="flex flex-col sm:flex-row gap-4 items-stretch sm:items-center justify-between bg-[#0b1224]/85 border border-[#1e293b]/70 p-4 rounded-2xl shadow-lg backdrop-blur-sm">
          {/* Search Input */}
          <div className="relative flex-1 max-w-md">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search suppliers..."
              className="w-full pl-10 pr-4 py-2.5 bg-slate-900/60 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-blue-500/50 transition-colors"
            />
            <svg className="w-4 h-4 text-slate-500 absolute left-3.5 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>

          <button
            onClick={handleOpenAddModal}
            className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-500 px-5 py-2.5 rounded-xl text-xs font-bold leading-tight transition-all cursor-pointer border border-blue-400/20 text-white shadow-lg shadow-blue-500/10"
          >
            <span>+ Add supplier</span>
          </button>
        </div>

        {/* Suppliers Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredSuppliers.length > 0 ? (
            filteredSuppliers.map((supplier) => (
              <div
                key={supplier.id}
                className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-6 rounded-2xl shadow-lg shadow-black/30 backdrop-blur-sm space-y-5 transition-all hover:border-slate-800 hover:-translate-y-0.5"
              >
                {/* Header Row: Initials Badge, Name, Status */}
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className={`w-10 h-10 rounded-full bg-gradient-to-tr ${supplier.color || 'from-blue-500 to-indigo-600'} text-xs font-black text-white flex items-center justify-center border border-white/10 shadow-inner`}>
                      {supplier.initials}
                    </div>
                    <div className="space-y-0.5">
                      <h4 className="font-bold text-slate-200 text-sm leading-tight">{supplier.name}</h4>
                      <p className="text-[10px] text-slate-500 font-medium">{supplier.category}</p>
                    </div>
                  </div>
                  
                  <span className={`px-2.5 py-0.5 rounded-full text-[9px] font-black uppercase tracking-wider ${
                    supplier.status === 'Active'
                      ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                      : 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                  }`}>
                    {supplier.status}
                  </span>
                </div>

                <hr className="border-slate-900/40" />

                {/* Details list */}
                <div className="space-y-2 text-xs font-semibold text-slate-400">
                  <div className="flex items-center gap-2">
                    <svg className="w-3.5 h-3.5 text-blue-500/80" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                    <span className="font-mono text-[10px] font-medium">{supplier.email}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <svg className="w-3.5 h-3.5 text-pink-500/80" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                    </svg>
                    <span className="font-mono text-[10px] font-medium">{supplier.phone}</span>
                  </div>
                </div>

                <div className="pt-2">
                  <button
                    onClick={() => handleOpenEditModal(supplier)}
                    className="w-full py-2 bg-slate-800/50 hover:bg-slate-800 text-slate-300 hover:text-white rounded-xl border border-slate-800 transition-colors cursor-pointer text-xs font-bold text-center"
                  >
                    Edit
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="col-span-full bg-[#0b1224]/85 border border-[#1e293b]/70 p-12 rounded-2xl text-center text-slate-500 font-semibold">
              No suppliers found.
            </div>
          )}
        </div>

        {/* Modal Form Overlay */}
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
            <div className="w-full max-w-md bg-[#0c1328] border border-gray-800 rounded-2xl p-6 space-y-6 shadow-2xl">
              <h3 className="text-base font-bold text-white tracking-tight">
                {editingSupplier ? 'Edit Supplier' : 'Add New Supplier'}
              </h3>
              
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Supplier Name</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="e.g. Global Tech Distributors"
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-blue-500/50 transition-colors text-xs"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Category</label>
                    <select
                      value={formData.category}
                      onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                      className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs cursor-pointer"
                    >
                      <option>Electronics</option>
                      <option>Office Supplies</option>
                      <option>Furniture</option>
                      <option>Raw Materials</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Status</label>
                    <select
                      value={formData.status}
                      onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                      className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs cursor-pointer"
                    >
                      <option>Active</option>
                      <option>Pending</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Email Address</label>
                  <input
                    type="email"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    placeholder="e.g. sales@company.com"
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-blue-500/50 transition-colors text-xs font-mono"
                  />
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Phone Number</label>
                  <input
                    type="text"
                    required
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    placeholder="e.g. +94 11 200 3000"
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-blue-500/50 transition-colors text-xs font-mono"
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
                    className="px-5 py-2.5 bg-blue-600 hover:bg-blue-500 rounded-xl text-xs font-bold text-white border border-blue-400/20 shadow-lg shadow-blue-500/10 cursor-pointer"
                  >
                    Save Changes
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

export default SuppliersPage;
