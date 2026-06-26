import React, { useState, useEffect } from 'react';
import MainLayout from '../components/layout/MainLayout';
import { categoryAPI } from '../services/api';

const ICON_OPTIONS = [
  { name: 'laptop', label: '💻', defaultText: 'Electronics' },
  { name: 'clip', label: '📎', defaultText: 'Office Supplies' },
  { name: 'chair', label: '🪑', defaultText: 'Furniture' },
  { name: 'bolt', label: '⚙️', defaultText: 'Hardware' },
  { name: 'box', label: '📦', defaultText: 'Packaging' },
  { name: 'tag', label: '🏷️', defaultText: 'Labels' },
  { name: 'bag', label: '🛍️', defaultText: 'Retail' },
  { name: 'beaker', label: '🧪', defaultText: 'Chemicals' },
  { name: 'book', label: '📚', defaultText: 'Books' },
  { name: 'truck', label: '🚚', defaultText: 'Logistics' },
  { name: 'heart', label: '❤️', defaultText: 'Health' },
  { name: 'star', label: '⭐', defaultText: 'Premium' },
];

function CategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [formData, setFormData] = useState({ name: '', icon: 'laptop' });
  const [submitting, setSubmitting] = useState(false);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await categoryAPI.getAll({ page: 0, size: 100 });
      setCategories(res.data.content || res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load categories');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCategories(); }, []);

  const renderCategoryIcon = (iconName) => {
    const baseClass = "w-6 h-6 text-blue-400";
    switch (iconName) {
      case 'laptop':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>);
      case 'clip':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" /></svg>);
      case 'chair':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 10h16M4 14h16M4 18v3M20 18v3M8 6V3a1 1 0 011-1h6a1 1 0 011 1v3" /></svg>);
      case 'bolt':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>);
      case 'box':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M21 7.5l-9-5.25L3 7.5m18 0l-9 5.25m9-5.25v9l-9 5.25M3 7.5l9 5.25M3 7.5v9l9 5.25m0-9v9" /></svg>);
      case 'tag':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M9.568 3H5.25A2.25 2.25 0 003 5.25v4.318c0 .597.237 1.17.659 1.591l9.581 9.581c.699.699 1.78.872 2.607.33a18.095 18.095 0 005.223-5.223c.542-.827.369-1.908-.33-2.607L11.16 3.66A2.25 2.25 0 009.568 3zM6 6h.008v.008H6V6z" /></svg>);
      case 'bag':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M15.75 10.5V6a3.75 3.75 0 10-7.5 0v4.5m11.356-1.993l1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 01-1.12-1.243l1.264-12A1.125 1.125 0 015.513 7.5h12.974c.576 0 1.059.435 1.119 1.007zM8.625 10.5a.375.375 0 11-.75 0 .375.375 0 01.75 0zm7.5 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" /></svg>);
      case 'beaker':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M9.75 3.104v5.714a2.25 2.25 0 01-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 014.5 0m0 0v5.714c0 .597.237 1.17.659 1.591L19.894 15c.427.43.682 1.026.682 1.666 0 1.341-1.087 2.434-2.434 2.434H5.858C4.511 19.1 3.424 18.007 3.424 16.666c0-.64.255-1.236.682-1.666l4.985-5.091A2.25 2.25 0 019.75 8.818v-5.714m4.5 0a25.26 25.26 0 011.5-.082M12 16.5h.008v.008H12v-.008z" /></svg>);
      case 'book':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25" /></svg>);
      case 'truck':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 00-3.213-9.193 2.056 2.056 0 00-1.58-.86H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 00-10.026 0 1.106 1.106 0 00-.987 1.106v7.635m12-6.677v6.677m0 4.5v-4.5m0 0h-12" /></svg>);
      case 'heart':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z" /></svg>);
      case 'star':
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" /></svg>);
      default:
        return (<svg className={baseClass} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M7 7h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>);
    }
  };

  const handleOpenAddModal = () => {
    setEditingCategory(null);
    setFormData({ name: '', icon: 'laptop' });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (cat) => {
    setEditingCategory(cat);
    setFormData({ name: cat.name, icon: cat.icon || 'laptop' });
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this category?')) return;
    try {
      await categoryAPI.delete(id);
      setCategories(categories.filter(c => c.id !== id));
    } catch (err) {
      alert(err.message || 'Failed to delete category');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      if (editingCategory) {
        await categoryAPI.update(editingCategory.id, { name: formData.name, icon: formData.icon });
      } else {
        await categoryAPI.create({ name: formData.name, icon: formData.icon });
      }
      setIsModalOpen(false);
      await fetchCategories();
    } catch (err) {
      alert(err.message || 'Failed to save category');
    } finally {
      setSubmitting(false);
    }
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

        {/* Error */}
        {error && (
          <div className="p-4 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-xl text-xs font-semibold">
            ⚠️ {error}
          </div>
        )}

        {/* Loading */}
        {loading ? (
          <div className="text-center text-slate-500 py-12 font-semibold">Loading categories...</div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {categories.map((category) => (
              <div
                key={category.id}
                className="bg-[#0b1224]/85 border border-[#1e293b]/70 hover:border-blue-500/35 p-6 rounded-2xl shadow-lg shadow-black/30 backdrop-blur-sm flex items-center justify-between gap-4 transition-all hover:-translate-y-0.5"
              >
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 rounded-xl bg-blue-500/10 border border-blue-500/20 flex items-center justify-center">
                    {renderCategoryIcon(category.icon)}
                  </div>
                  <div className="space-y-1">
                    <h4 className="font-bold text-slate-200 text-sm leading-tight">{category.name}</h4>
                    <p className="text-[10px] text-slate-500 font-mono tracking-wider uppercase font-bold">
                      {category.status || 'Active'}
                    </p>
                  </div>
                </div>
                <div className="flex flex-col gap-1.5">
                  <button onClick={() => handleOpenEditModal(category)} className="px-3 py-1.5 bg-slate-800/50 hover:bg-slate-800 text-slate-300 hover:text-white rounded-lg border border-slate-800 transition-colors cursor-pointer text-[10px] font-bold">Edit</button>
                  <button onClick={() => handleDelete(category.id)} className="px-3 py-1.5 bg-rose-950/20 hover:bg-rose-900/40 text-rose-400 hover:text-rose-300 rounded-lg border border-rose-950/40 transition-colors cursor-pointer text-[10px] font-bold">Del</button>
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
        )}

        {/* Modal Dialog */}
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
            <div className="w-full max-w-sm bg-[#0c1328] border border-gray-800 rounded-2xl p-6 space-y-6 shadow-2xl">
              <h3 className="text-base font-bold text-white tracking-tight">
                {editingCategory ? 'Edit Category' : 'Add New Category'}
              </h3>

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
                    {ICON_OPTIONS.map((ico) => (
                      <button
                        type="button"
                        key={ico.name}
                        onClick={() => {
                          setFormData(prev => {
                            const isDefaultName = prev.name.trim() === '' || ICON_OPTIONS.some(i => i.defaultText === prev.name);
                            return {
                              ...prev,
                              icon: ico.name,
                              name: isDefaultName ? ico.defaultText : prev.name
                            };
                          });
                        }}
                        className={`p-3 rounded-xl border flex items-center justify-center text-lg transition-all ${
                          formData.icon === ico.name
                            ? 'bg-blue-600/15 border-blue-500 text-white'
                            : 'bg-gray-900/50 border-gray-800 text-gray-400 hover:border-gray-700'
                        }`}
                        title={ico.defaultText}
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
                    disabled={submitting}
                    className="px-5 py-2.5 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 rounded-xl text-xs font-bold text-white border border-blue-400/20 shadow-lg shadow-blue-500/10 cursor-pointer"
                  >
                    {submitting ? 'Saving...' : editingCategory ? 'Save Changes' : 'Create Category'}
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
