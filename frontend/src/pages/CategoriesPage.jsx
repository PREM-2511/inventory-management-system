import React from 'react';
import MainLayout from '../components/layout/MainLayout';

function CategoriesPage() {
  return (
    <MainLayout>
      <div className="p-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2">Categories</h1>
          <p className="text-gray-400">Organize and manage your product categories</p>
        </div>

        <div className="bg-[#0c1328]/60 border border-gray-800/80 rounded-2xl p-8 backdrop-blur-sm text-center">
          <p className="text-gray-400 mb-4">Categories management interface coming soon</p>
          <button className="px-6 py-2.5 bg-blue-600 hover:bg-blue-500 rounded-xl font-medium transition-colors">
            Add Category
          </button>
        </div>
      </div>
    </MainLayout>
  );
}

export default CategoriesPage;
