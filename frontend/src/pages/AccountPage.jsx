import React, { useState } from 'react';
import MainLayout from '../components/layout/MainLayout';
import { useAuth } from '../context/AuthContext';

function AccountPage() {
  const { user } = useAuth();
  
  const [fullName, setFullName] = useState(user?.fullName || 'Admin User');
  const [companyName, setCompanyName] = useState(user?.companyName || 'StockIQ Solutions');
  const [email, setEmail] = useState(user?.email || 'admin@company.com');
  const [role, setRole] = useState(user?.role || 'System Administrator');
  
  const [isEditing, setIsEditing] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);

  const getInitials = (name) => {
    if (!name) return 'SU';
    const parts = name.trim().split(/\s+/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
  };

  const handleSave = (e) => {
    e.preventDefault();
    setIsEditing(false);
    setSaveSuccess(true);
    setTimeout(() => setSaveSuccess(false), 3000);
  };

  return (
    <MainLayout title="Account Settings">
      <div className="p-6 relative z-10 max-w-4xl mx-auto space-y-6">
        
        {/* Success Alert toast */}
        {saveSuccess && (
          <div className="p-4 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 rounded-xl text-xs flex items-center gap-3 shadow-[0_0_15px_rgba(16,185,129,0.1)]">
            <span className="text-base font-bold">✓</span>
            <span className="font-semibold">Profile details successfully updated!</span>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          
          {/* Left Column: Avatar & Profile stats Summary */}
          <div className="bg-[#0b1224]/85 border border-[#1e293b]/70 p-6 rounded-2xl shadow-lg backdrop-blur-sm text-center flex flex-col items-center justify-center space-y-4">
            <div className="relative">
              <div className="absolute inset-0 rounded-full border border-blue-500/30 animate-pulse scale-105" />
              <div className="w-20 h-20 rounded-full bg-gradient-to-tr from-blue-500 to-indigo-600 text-2xl font-black text-white flex items-center justify-center border border-white/10 shadow-lg shadow-blue-500/20">
                {getInitials(fullName)}
              </div>
            </div>

            <div className="space-y-1">
              <h4 className="font-bold text-slate-200 text-lg">{fullName}</h4>
              <p className="text-xs text-slate-400 font-mono tracking-wider uppercase font-bold">{role}</p>
            </div>
            
            <hr className="w-full border-slate-900/40" />

            <div className="w-full text-left space-y-2 text-xs font-semibold text-slate-400">
              <div className="flex justify-between">
                <span>Account Status</span>
                <span className="text-emerald-400 font-bold uppercase tracking-wider text-[10px]">Verified</span>
              </div>
              <div className="flex justify-between">
                <span>Plan Level</span>
                <span className="text-blue-400 font-bold uppercase tracking-wider text-[10px]">Premium Business</span>
              </div>
            </div>
          </div>

          {/* Right Column: Details form */}
          <div className="md:col-span-2 bg-[#0b1224]/85 border border-[#1e293b]/70 p-6 rounded-2xl shadow-lg backdrop-blur-sm">
            <form onSubmit={handleSave} className="space-y-5">
              <div className="flex items-center justify-between pb-2 border-b border-slate-900/40">
                <h3 className="text-sm font-bold uppercase tracking-wider text-slate-300">Profile Details</h3>
                {!isEditing ? (
                  <button
                    type="button"
                    onClick={() => setIsEditing(true)}
                    className="px-3.5 py-1.5 bg-blue-600/10 hover:bg-blue-600/20 text-blue-400 rounded-lg border border-blue-500/25 transition-all text-[10px] font-bold cursor-pointer"
                  >
                    Edit Profile
                  </button>
                ) : (
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => setIsEditing(false)}
                      className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-300 rounded-lg text-[10px] font-bold cursor-pointer"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="px-3.5 py-1.5 bg-blue-600 hover:bg-blue-500 text-white rounded-lg border border-blue-400/20 text-[10px] font-bold shadow-md cursor-pointer"
                    >
                      Save
                    </button>
                  </div>
                )}
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-500 mb-1.5">Full Name</label>
                  <input
                    type="text"
                    required
                    disabled={!isEditing}
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs disabled:opacity-60"
                  />
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-500 mb-1.5">Company Name</label>
                  <input
                    type="text"
                    required
                    disabled={!isEditing}
                    value={companyName}
                    onChange={(e) => setCompanyName(e.target.value)}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs disabled:opacity-60"
                  />
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-500 mb-1.5">Email Address</label>
                  <input
                    type="email"
                    required
                    disabled={!isEditing}
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white focus:outline-none focus:border-blue-500/50 transition-colors text-xs disabled:opacity-60 font-mono"
                  />
                </div>

                <div>
                  <label className="block text-[10px] font-bold uppercase tracking-wider text-gray-500 mb-1.5">User Role</label>
                  <input
                    type="text"
                    disabled
                    value={role}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-slate-400 focus:outline-none transition-colors text-xs disabled:opacity-60 font-medium"
                  />
                </div>
              </div>
            </form>
          </div>

        </div>

      </div>
    </MainLayout>
  );
}

export default AccountPage;
