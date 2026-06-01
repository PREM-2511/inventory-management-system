import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../components/layout/Navbar";
import { useAuth } from "../context/AuthContext";

const ForgotPasswordPage = () => {
  const navigate = useNavigate();
  const { resetPassword } = useAuth();
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await resetPassword(email);
      setSubmitted(true);
    } catch (err) {
      setError(err.message || "Failed to send reset link. Please check your email.");
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    setError("");
    setLoading(true);
    try {
      await resetPassword(email);
      alert("Reset link resent to " + email);
    } catch (err) {
      setError(err.message || "Failed to resend reset link.");
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

            <h1 className="text-3xl font-bold mb-2">Reset password</h1>
            <p className="text-gray-400 text-sm">
              We'll send you an email with instructions to set a new password
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

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full py-2.5 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 disabled:opacity-50 text-white rounded-xl font-bold shadow-lg shadow-blue-500/20 hover:shadow-blue-500/35 transition-all text-sm flex items-center justify-center cursor-pointer"
                >
                  {loading ? "Sending..." : "Send Reset Link"}
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
                  ✉️
                </div>

                <div className="space-y-2">
                  <h3 className="text-xl font-bold text-white">Check your email</h3>
                  <p className="text-gray-400 text-sm leading-relaxed">
                    We have sent a secure password reset link to: <br />
                    <span className="text-blue-400 font-medium break-all">{email}</span>
                  </p>
                </div>

                <div className="pt-2 space-y-4">
                  <button
                    type="button"
                    onClick={handleResend}
                    disabled={loading}
                    className="w-full py-2 bg-slate-900 hover:bg-slate-800 border border-slate-800 hover:border-slate-700 text-slate-300 hover:text-white transition-colors rounded-xl text-xs font-bold cursor-pointer"
                  >
                    {loading ? "Resending..." : "Resend Email"}
                  </button>

                  <div>
                    <Link
                      to="/login"
                      className="inline-flex items-center gap-2 text-xs font-semibold text-slate-400 hover:text-white transition-colors"
                    >
                      <span>&larr;</span> Back to Sign In
                    </Link>
                  </div>
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
