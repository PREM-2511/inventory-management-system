import React from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import Navbar from '../components/layout/Navbar';

function ErrorPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const type = queryParams.get('type') || '404';
  const customMsg = queryParams.get('msg');

  let errorDetails = {
    title: '404 - Page Not Found',
    subtitle: 'The resource you requested could not be located on this server.',
    icon: (
      <svg className="w-8 h-8 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>
    ),
    iconColor: 'bg-amber-500/10 border-amber-500/25 shadow-amber-500/10',
    bullets: [
      'Verify the URL address in your browser search bar',
      'The requested page might have been relocated or deleted',
      'If you pasted this URL, check that it is complete and correct'
    ],
    actionText: 'Go to Dashboard',
    actionPath: '/dashboard'
  };

  if (type === 'offline') {
    errorDetails = {
      title: 'Server Connection Failed',
      subtitle: customMsg || 'StockIQ was unable to establish a secure connection with the backend api server.',
      icon: (
        <svg className="w-8 h-8 text-rose-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
        </svg>
      ),
      iconColor: 'bg-rose-500/10 border-rose-500/25 shadow-rose-500/10',
      bullets: [
        'Confirm the backend server API is running locally on port 5000',
        'Verify your workstation has active network/internet access',
        'Check database environment configs (.env) and server firewall settings'
      ],
      actionText: 'Retry Connection',
      actionPath: '/dashboard'
    };
  } else if (type === '500') {
    errorDetails = {
      title: '500 - Internal Server Error',
      subtitle: 'The server encountered an unexpected crash while compiling your request.',
      icon: (
        <svg className="w-8 h-8 text-rose-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      ),
      iconColor: 'bg-rose-500/10 border-rose-500/25 shadow-rose-500/10',
      bullets: [
        'Check the backend console command logs for critical node stack errors',
        'Verify database schema columns exist and match frontend forms',
        'Reload the page or sign back in to start a clean user session'
      ],
      actionText: 'Return Home',
      actionPath: '/'
    };
  }

  const handleAction = () => {
    if (type === 'offline') {
      // Reload current route or push to dashboard to trigger reconnect check
      window.location.href = errorDetails.actionPath;
    } else {
      navigate(errorDetails.actionPath);
    }
  };

  return (
    <div className="bg-[#0a0f1d] text-white min-h-screen font-sans relative overflow-x-hidden selection:bg-blue-600 selection:text-white">
      {/* Tech Blueprint Grid Background Accent */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#141b30_1px,transparent_1px),linear-gradient(to_bottom,#141b30_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] pointer-events-none" />

      <Navbar />

      <div className="relative z-10 flex items-center justify-center min-h-[calc(100vh-80px)] px-6 py-12">
        <div className="w-full max-w-lg">
          
          {/* Card Wrapper */}
          <div className="bg-[#0c1328]/60 border border-gray-800/80 rounded-2xl p-8 backdrop-blur-sm shadow-2xl">
            
            {/* Header Block */}
            <div className="flex flex-col items-center text-center space-y-4 mb-6">
              <div className={`p-4 rounded-2xl border flex items-center justify-center shadow-lg ${errorDetails.iconColor}`}>
                {errorDetails.icon}
              </div>
              <div className="space-y-1">
                <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-white">
                  {errorDetails.title}
                </h1>
                <p className="text-sm text-slate-400 font-medium leading-relaxed max-w-sm mx-auto">
                  {errorDetails.subtitle}
                </p>
              </div>
            </div>

            {/* Suggestions Checklist */}
            <div className="bg-slate-900/30 border border-slate-800/40 rounded-xl p-5 space-y-3 mb-8">
              <h4 className="text-xs font-mono font-bold text-slate-500 tracking-wider uppercase">
                Troubleshooting Checklist:
              </h4>
              <ul className="space-y-2.5 text-xs text-slate-300 font-medium">
                {errorDetails.bullets.map((bullet, idx) => (
                  <li key={idx} className="flex items-start gap-2.5 leading-relaxed">
                    <span className="text-blue-500 select-none mt-0.5">•</span>
                    <span>{bullet}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Action buttons */}
            <div className="flex flex-col sm:flex-row items-center gap-3">
              <button
                onClick={handleAction}
                className="w-full py-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white rounded-xl font-bold text-sm shadow-lg shadow-blue-500/20 hover:shadow-blue-500/30 transition-all flex items-center justify-center cursor-pointer"
              >
                {errorDetails.actionText}
              </button>
              
              <Link
                to="/"
                className="w-full py-3 bg-[#080d1a]/80 hover:bg-slate-900 border border-gray-800 hover:border-slate-700 text-slate-300 hover:text-white transition-all rounded-xl text-center font-bold text-sm cursor-pointer"
              >
                Back to Welcome Screen
              </Link>
            </div>

          </div>

        </div>
      </div>
    </div>
  );
}

export default ErrorPage;
