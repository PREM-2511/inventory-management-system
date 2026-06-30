import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function OAuthCallbackPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { loginWithGithub } = useAuth();
  const [error, setError] = useState("");

  useEffect(() => {
    const code = searchParams.get("code");
    if (code) {
      loginWithGithub(code)
        .then(() => {
          navigate("/dashboard");
        })
        .catch((err) => {
          console.error("GitHub OAuth error:", err);
          setError(err?.message || "Failed to log in with GitHub.");
        });
    } else {
      setError("Authorization code not found from GitHub redirect.");
    }
  }, [searchParams, loginWithGithub, navigate]);

  return (
    <div className="bg-[#0a0f1d] text-white min-h-screen flex items-center justify-center font-sans relative overflow-x-hidden">
      {/* Tech Blueprint Grid Background Accent */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#141b30_1px,transparent_1px),linear-gradient(to_bottom,#141b30_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] pointer-events-none" />

      <div className="relative z-10 w-full max-w-md p-6 text-center">
        <div className="bg-[#0c1328]/60 border border-gray-800/80 rounded-2xl p-8 backdrop-blur-sm shadow-xl shadow-black/40">
          {!error ? (
            <div className="space-y-6">
              {/* Spinner */}
              <div className="flex justify-center">
                <div className="relative w-16 h-16">
                  <div className="absolute inset-0 border-4 border-blue-500/20 rounded-full"></div>
                  <div className="absolute inset-0 border-4 border-blue-500 border-t-transparent rounded-full animate-spin shadow-[0_0_15px_rgba(59,130,246,0.5)]"></div>
                </div>
              </div>
              <h2 className="text-xl font-bold tracking-tight">Authenticating with GitHub</h2>
              <p className="text-gray-400 text-sm">Please wait while we set up your session...</p>
            </div>
          ) : (
            <div className="space-y-6">
              <div className="inline-flex items-center justify-center w-12 h-12 bg-rose-500/10 border border-rose-500/20 text-rose-400 rounded-full mb-2">
                <span className="text-2xl font-bold">⚠️</span>
              </div>
              <h2 className="text-xl font-bold text-rose-400 tracking-tight">Authentication Failed</h2>
              <p className="text-gray-300 text-sm">{error}</p>
              <button
                onClick={() => navigate("/login")}
                className="w-full mt-4 py-2.5 bg-gray-900 hover:bg-gray-800 border border-gray-800 text-white rounded-xl font-semibold transition-colors cursor-pointer"
              >
                Return to Login
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default OAuthCallbackPage;
