import React, { useState } from 'react';
import MainLayout from '../components/layout/MainLayout';

const INITIAL_PRODUCTS = [
  { id: 1, name: 'Laptop Pro 15"', category: 'Electronics', qty: 42, reorder: 10, status: 'In stock' },
  { id: 2, name: 'USB-C Hub 7-port', category: 'Electronics', qty: 7, reorder: 15, status: 'Low stock' },
  { id: 3, name: 'Ergonomic Chair', category: 'Furniture', qty: 19, reorder: 5, status: 'In stock' },
  { id: 4, name: 'A4 Paper (500 sheets)', category: 'Office Supplies', qty: 3, reorder: 20, status: 'Critical' },
];

function ProductsPage() {
  const [products, setProducts] = useState(INITIAL_PRODUCTS);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All categories');
  
  // Modal/Form States
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [formData, setFormData] = useState({ name: '', category: 'Electronics', qty: 0, reorder: 0 });

  const getStatus = (qty, reorder) => {
    if (qty <= 0) return 'Out of stock';
    if (qty <= reorder / 2) return 'Critical';
    if (qty <= reorder) return 'Low stock';
    return 'In stock';
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'In stock':
        return 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20';
      case 'Low stock':
        return 'bg-amber-500/10 text-amber-400 border border-amber-500/20';
      case 'Critical':
        return 'bg-rose-500/10 text-rose-400 border border-rose-500/20';
      default:
        return 'bg-slate-500/10 text-slate-400 border border-slate-500/20';
    }
  };

  const handleOpenAddModal = () => {
    setEditingProduct(null);
    setFormData({ name: '', category: 'Electronics', qty: 10, reorder: 5 });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (product) => {
    setEditingProduct(product);
    setFormData({ name: product.name, category: product.category, qty: product.qty, reorder: product.reorder });
    setIsModalOpen(true);
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      setProducts(products.filter(p => p.id !== id));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const computedStatus = getStatus(Number(formData.qty), Number(formData.reorder));
    
    if (editingProduct) {
      setProducts(products.map(p => p.id === editingProduct.id ? {
        ...p,
        name: formData.name,
        category: formData.category,
        qty: Number(formData.qty),
        reorder: Number(formData.reorder),
        status: computedStatus
      } : p));
    } else {
      const newProduct = {
        id: Date.now(),
        name: formData.name,
        category: formData.category,
        qty: Number(formData.qty),
        reorder: Number(formData.reorder),
        status: computedStatus
      };
      setProducts([...products, newProduct]);
    }
    setIsModalOpen(false);
  };

  // Filtering Logic
  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === 'All categories' || product.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <MainLayout title="Product Management">
      <div className="p-6 relative z-10 space-y-6">
        
        {/* Controls Bar */}
        <div className="flex flex-col sm:flex-row gap-4 items-stretch sm:items-center justify-between bg-[#0b1224]/85 border border-[#1e293b]/70 p-4 rounded-2xl shadow-lg backdrop-blur-sm">
          <div className="flex flex-col sm:flex-row gap-3 flex-1 max-w-2xl">
            {/* Search Input */}
            <div className="relative flex-1">
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search products..."
                className="w-full pl-10 pr-4 py-2.5 bg-slate-900/60 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-blue-500/50 transition-colors"
              />
              <svg className="w-4 h-4 text-slate-500 absolute left-3.5 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            
            {/* Category Select */}
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="px-4 py-2.5 bg-slate-900/60 border border-slate-800 rounded-xl text-xs text-slate-300 focus:outline-none focus:border-blue-500/50 cursor-pointer"
            >
              <option>All categories</option>
              <option>Electronics</option>
              <option>Office Supplies</option>
              <option>Furniture</option>
              <option>Raw Materials</option>
            </select>
          </div>

          <button
            onClick={handleOpenAddModal}
            className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-500 px-5 py-2.5 rounded-xl text-xs font-bold leading-tight transition-all cursor-pointer border border-blue-400/20 text-white shadow-lg shadow-blue-500/10"
          >
            <span>+ Add product</span>
          </button>
        </div>

        {/* Table Card */}
        <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 rounded-2xl shadow-lg overflow-hidden backdrop-blur-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="border-b border-[#1e293b]/70 bg-slate-900/40 text-slate-400 font-mono">
                  <th className="p-4 uppercase font-semibold tracking-wider">Product</th>
                  <th className="p-4 uppercase font-semibold tracking-wider">Category</th>
                  <th className="p-4 uppercase font-semibold tracking-wider text-center">Qty</th>
                  <th className="p-4 uppercase font-semibold tracking-wider text-center">Reorder At</th>
                  <th className="p-4 uppercase font-semibold tracking-wider text-center">Status</th>
                  <th className="p-4 uppercase font-semibold tracking-wider text-center">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/40 font-medium">
                {filteredProducts.length > 0 ? (
                  filteredProducts.map((product) => (
                    <tr key={product.id} className="hover:bg-slate-900/10 transition-colors">
                      <td className="p-4 font-bold text-slate-200 text-sm">{product.name}</td>
                      <td className="p-4 text-slate-400">{product.category}</td>
                      <td className="p-4 text-center font-bold text-white font-mono text-sm">{product.qty}</td>
                      <td className="p-4 text-center font-semibold text-slate-400 font-mono">{product.reorder}</td>
                      <td className="p-4 text-center">
                        <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-black uppercase tracking-wider inline-block ${getStatusClass(product.status)}`}>
                          {product.status}
                        </span>
                      </td>
                      <td className="p-4">
                        <div className="flex items-center justify-center gap-2">
                          <button
                            onClick={() => handleOpenEditModal(product)}
                            className="px-3 py-1.5 bg-slate-800/50 hover:bg-slate-800 text-slate-300 hover:text-white rounded-lg border border-slate-800 transition-colors cursor-pointer text-[10px] font-bold"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDelete(product.id)}
                            className="px-3 py-1.5 bg-rose-950/20 hover:bg-rose-900/40 text-rose-400 hover:text-rose-300 rounded-lg border border-rose-950/40 transition-colors cursor-pointer text-[10px] font-bold"
                          >
                            Del
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="6" className="p-8 text-center text-slate-500 font-semibold">
                      No products found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Modal Form Overlay */}
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
            <div className="w-full max-w-md bg-[#0c1328] border border-gray-800 rounded-2xl p-6 space-y-6 shadow-2xl relative">
              <h3 className="text-base font-bold text-white tracking-tight">
                {editingProduct ? 'Edit Product' : 'Add New Product'}
              </h3>
              
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Product Name</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder='e.g. Laptop Pro 15"'
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-blue-500/50 transition-colors text-xs"
                  />
                </div>

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

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Quantity</label>
                    <input
                      type="number"
                      min="0"
                      required
                      value={formData.qty}
                      onChange={(e) => setFormData({ ...formData, qty: e.target.value })}
                      className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs font-mono"
                    />
                  </div>
                  <div>
                    <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Reorder Point</label>
                    <input
                      type="number"
                      min="0"
                      required
                      value={formData.reorder}
                      onChange={(e) => setFormData({ ...formData, reorder: e.target.value })}
                      className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs font-mono"
                    />
                  </div>
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

export default ProductsPage;
