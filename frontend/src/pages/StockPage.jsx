import React from 'react';
import MainLayout from '../components/layout/MainLayout';

function StockPage() {
  return (
    <MainLayout>
      <div className="p-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2">Stock Movements</h1>
          <p className="text-gray-400">Track and manage stock in/out transactions</p>
        </div>

        <div className="bg-[#0c1328]/60 border border-gray-800/80 rounded-2xl p-8 backdrop-blur-sm text-center">
          <p className="text-gray-400 mb-4">Stock movement tracking interface coming soon</p>
          <button className="px-6 py-2.5 bg-blue-600 hover:bg-blue-500 rounded-xl font-medium transition-colors">
            Record Movement
          </button>
        </div>
      </div>
    </MainLayout>
  );
}

export default StockPage;
