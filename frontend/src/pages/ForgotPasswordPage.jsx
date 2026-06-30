import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../components/layout/Navbar";
import { useAuth } from "../context/AuthContext";

const ForgotPasswordPage = () => {
  const navigate = useNavigate();
  const { resetPassword } = useAuth();
  const [email, setEmail] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await resetPassword(email, newPassword);
      setSubmitted(true);
    } catch (err) {
      setError(err.message || "Failed to reset password. Please check your credentials.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-[#0a0f1d] text-white min-h-screen font-sans relative overflow-x-hidden selection:bg-blue-600 selection:text-white">
      {/* Tech Blueprint Grid Background Accent */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#141b30_1px,transparent_1px),linear-gradient(to_bottom,#141b30_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] pointer-events-none" />

      <Navbar />

      {/* Forgot Password Container */}
      <div className="relative z-10 flex items-center justify-center min-h-[calc(100vh-80px)] px-6">
        <div className="w-full max-w-md">
          
          <div className="text-center mb-8">
            <div className="inline-flex items-center gap-3 mb-6">
              {/* 2x2 Grid Icon Logo */}
              <div className="bg-gradient-to-br from-blue-500 to-blue-600 p-2.5 rounded-2xl shadow-lg shadow-blue-500/25 border border-blue-400/20">
                <svg className="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                  <rect x="4" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="14" y="4" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="4" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                  <rect x="14" y="14" width="6" height="6" rx="1.5" strokeWidth={2.5} />
                </svg>
              </div>
              <div>
                <h2 className="text-2xl font-bold tracking-tight block">StockIQ</h2>
                <span className="text-[10px] text-gray-500 tracking-widest uppercase block">Smart Inventory</span>
              </div>
            </div>

            <h1 className="text-3xl font-bold mb-2">Reset Password</h1>
            <p className="text-gray-400 text-sm">
              Enter your email and chosen password to update your login credentials
            </p>
          </div>

          {/* Container Card */}
          <div className="bg-[#0c1328]/60 border border-gray-800/80 rounded-2xl p-8 backdrop-blur-sm shadow-2xl">
            {error && (
              <div className="mb-4 p-4 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-xl text-xs flex items-center gap-3 shadow-[0_0_15px_rgba(239,68,68,0.1)]">
                <span className="text-base font-bold">⚠️</span>
                <span className="font-semibold">{error}</span>
              </div>
            )}
            {!submitted ? (
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-gray-400 mb-2">
                    Email address
                  </label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="name@company.com"
                    required
                    disabled={loading}
                    className="w-full px-4 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-blue-500/50 transition-colors text-sm disabled:opacity-50"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-gray-400 mb-2">
                    New Password
                  </label>
                  <div className="relative">
                    <input
                      type={showPassword ? "text" : "password"}
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      placeholder="••••••••"
                      required
                      disabled={loading}
                      className="w-full pl-4 pr-10 py-2.5 bg-gray-900/50 border border-gray-800 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-blue-500/50 transition-colors text-sm disabled:opacity-50 font-mono"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3.5 top-3.5 text-slate-500 hover:text-slate-300 transition-colors cursor-pointer"
                    >
                      {showPassword ? (
                        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                        </svg>
                      ) : (
                        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      )}
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full py-2.5 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 disabled:opacity-50 text-white rounded-xl font-bold shadow-lg shadow-blue-500/20 hover:shadow-blue-500/35 transition-all text-sm flex items-center justify-center cursor-pointer"
                >
                  {loading ? "Resetting..." : "Reset Password"}
                </button>

                <div className="text-center pt-2">
                  <Link
                    to="/login"
                    className="inline-flex items-center gap-2 text-xs font-semibold text-slate-400 hover:text-white transition-colors"
                  >
                    <span>&larr;</span> Back to Sign In
                  </Link>
                </div>
              </form>
            ) : (
              <div className="text-center space-y-6 py-2">
                {/* Glowing Success Envelope Icon */}
                <div className="w-16 h-16 rounded-2xl bg-emerald-500/10 border border-emerald-500/25 flex items-center justify-center text-3xl mx-auto shadow-[0_0_20px_rgba(16,185,129,0.1)] text-emerald-400">
                  ✓
                </div>

                <div className="space-y-2">
                  <h3 className="text-xl font-bold text-white">Password Updated</h3>
                  <p className="text-gray-400 text-sm leading-relaxed">
                    Your password has been successfully reset for: <br />
                    <span className="text-blue-400 font-medium break-all">{email}</span>
                  </p>
                </div>

                <div className="pt-2 space-y-4">
                  <Link
                    to="/login"
                    className="w-full py-2.5 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white rounded-xl font-bold shadow-lg shadow-blue-500/20 hover:shadow-blue-500/35 transition-all text-sm flex items-center justify-center cursor-pointer"
                  >
                    Sign In
                  </Link>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
