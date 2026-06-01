import React, { useState } from 'react';
import MainLayout from '../components/layout/MainLayout';

const INITIAL_CATEGORIES = [
  { id: 1, name: 'Electronics', count: 82, icon: 'laptop' },
  { id: 2, name: 'Office Supplies', count: 67, icon: 'clip' },
  { id: 3, name: 'Furniture', count: 45, icon: 'chair' },
  { id: 4, name: 'Raw Materials', count: 54, icon: 'bolt' },
];

function CategoriesPage() {
  const [categories, setCategories] = useState(INITIAL_CATEGORIES);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({ name: '', icon: 'laptop' });

  const renderCategoryIcon = (iconName) => {
    const baseClass = "w-6 h-6 text-blue-400";
    switch (iconName) {
      case 'laptop':
        return (
          <svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
          </svg>
        );
      case 'clip':
        return (
          <svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
          </svg>
        );
      case 'chair':
        return (
          <svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 10h16M4 14h16M4 18v3M20 18v3M8 6V3a1 1 0 011-1h6a1 1 0 011 1v3" />
          </svg>
        );
      case 'bolt':
        return (
          <svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        );
      default:
        return (
          <svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M7 7h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        );
    }
  };

  const handleOpenAddModal = () => {
    setFormData({ name: '', icon: 'laptop' });
    setIsModalOpen(true);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const newCategory = {
      id: Date.now(),
      name: formData.name,
      count: 0,
      icon: formData.icon
    };
    setCategories([...categories, newCategory]);
    setIsModalOpen(false);
  };

  return (
    <MainLayout title="Category Management">
      <div className="p-6 relative z-10 space-y-6">
        
        {/* Actions Bar */}
        <div className="flex items-center justify-end bg-[#0b1224]/85 border border-[#1e293b]/70 p-4 rounded-2xl shadow-lg backdrop-blur-sm">
          <button
            onClick={handleOpenAddModal}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-500 px-5 py-2.5 rounded-xl text-xs font-bold leading-tight transition-all cursor-pointer border border-blue-400/20 text-white shadow-lg shadow-blue-500/10"
          >
            <span>+ Add category</span>
          </button>
        </div>

        {/* Categories Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {categories.map((category) => (
            <div
              key={category.id}
              className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/35 p-6 rounded-2xl shadow-lg shadow-black/30 backdrop-blur-sm flex items-center gap-4 transition-all hover:-translate-y-0.5"
            >
              {/* Icon Container */}
              <div className="w-12 h-12 rounded-xl bg-blue-500/10 border border-blue-500/20 flex items-center justify-center">
                {renderCategoryIcon(category.icon)}
              </div>
              <div className="space-y-1">
                <h4 className="font-bold text-slate-200 text-sm leading-tight">{category.name}</h4>
                <p className="text-[10px] text-slate-500 font-mono tracking-wider uppercase font-bold">{category.count} products</p>
              </div>
            </div>
          ))}

          {/* Add New Dashed Card */}
          <div
            onClick={handleOpenAddModal}
            className="border-2 border-dashed border-[#1e293b]/70 hover:border-blue-500/40 p-6 rounded-2xl flex items-center gap-4 cursor-pointer transition-all hover:bg-slate-900/10 justify-center min-h-[82px] text-slate-500 hover:text-blue-400"
          >
            <span className="text-xl font-black">+</span>
            <span className="text-xs font-bold font-sans">Add new</span>
          </div>
        </div>

        {/* Modal Dialog */}
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
            <div className="w-full max-w-sm bg-[#0c1328] border border-gray-800 rounded-2xl p-6 space-y-6 shadow-2xl">
              <h3 className="text-base font-bold text-white tracking-tight">Add New Category</h3>
              
              <form onSubmit={handleSubmit} className="space-y-5">
                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Category Name</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="e.g. Stationary"
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-blue-500/50 transition-colors text-xs"
                  />
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-400 mb-1.5">Select Icon</label>
                  <div className="grid grid-cols-4 gap-3">
                    {[
                      { name: 'laptop', label: '💻' },
                      { name: 'clip', label: '📎' },
                      { name: 'chair', label: '🪑' },
                      { name: 'bolt', label: '⚙️' }
                    ].map((ico) => (
                      <button
                        type="button"
                        key={ico.name}
                        onClick={() => setFormData({ ...formData, icon: ico.name })}
                        className={`p-3 rounded-xl border flex items-center justify-center text-lg transition-all ${
                          formData.icon === ico.name
                            ? 'bg-blue-600/15 border-blue-500 text-white'
                            : 'bg-gray-900/50 border-gray-800 text-gray-400 hover:border-gray-700'
                        }`}
                      >
                        {ico.label}
                      </button>
                    ))}
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
                    Create Category
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

export default CategoriesPage;
