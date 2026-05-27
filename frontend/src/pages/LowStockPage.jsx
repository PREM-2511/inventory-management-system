import React from 'react';
import MainLayout from '../components/layout/MainLayout';

function LowStockPage() {
  return (
    <MainLayout>
      <div className="p-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2">Low Stock Alerts</h1>
          <p className="text-gray-400">Monitor and manage items with low stock levels</p>
        </div>

        <div className="grid grid-cols-1 gap-4">
          {[
            {
              name: 'USB-C Hub 7-port',
              current: 7,
              reorder: 15,
              supplier: 'TechLink Pvt Ltd',
            },
            {
              name: 'A4 Paper (500 sheets)',
              current: 3,
              reorder: 20,
              supplier: 'OfficeFirst Co.',
            },
            {
              name: 'Whiteboard Markers',
              current: 4,
              reorder: 25,
              supplier: 'OfficeFirst Co.',
            },
          ].map((alert, idx) => (
            <div
              key={idx}
              className="border-l-4 border-amber-500 bg-[#0c1328]/60 border-r border-gray-800/80 border-t border-b border-gray-800/80 p-6 rounded-r-2xl bg-amber-950/10 flex items-center justify-between"
            >
              <div>
                <p className="font-semibold text-lg">{alert.name}</p>
                <p className="text-sm text-gray-400 mt-2">
                  Current: {alert.current} units • Reorder at: {alert.reorder} •{' '}
                  <span className="text-amber-400 font-medium">{alert.supplier}</span>
                </p>
              </div>
              <button className="px-6 py-2.5 bg-blue-600 hover:bg-blue-500 rounded-xl font-medium transition-colors whitespace-nowrap">
                Reorder Now
              </button>
            </div>
          ))}
        </div>
      </div>
    </MainLayout>
  );
}

export default LowStockPage;
