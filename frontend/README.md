# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.


inventory-frontend/
├── public/
├── src/
│   ├── assets/
│   │   ├── images/
│   │   └── icons/
│   │
│   ├── components/
│   │   ├── layout/
│   │   │   ├── Navbar.jsx
│   │   │   ├── Sidebar.jsx
│   │   │   └── MainLayout.jsx
│   │   │
│   │   ├── common/
│   │   │   ├── PageHeader.jsx
│   │   │   ├── SearchBar.jsx
│   │   │   ├── ConfirmModal.jsx
│   │   │   └── LoadingSpinner.jsx
│   │   │
│   │   ├── dashboard/
│   │   │   ├── SummaryCard.jsx
│   │   │   └── RecentTransactionsTable.jsx
│   │   │
│   │   ├── products/
│   │   │   ├── ProductForm.jsx
│   │   │   ├── ProductTable.jsx
│   │   │   └── ProductFilter.jsx
│   │   │
│   │   ├── categories/
│   │   │   ├── CategoryForm.jsx
│   │   │   └── CategoryTable.jsx
│   │   │
│   │   ├── suppliers/
│   │   │   ├── SupplierForm.jsx
│   │   │   └── SupplierTable.jsx
│   │   │
│   │   ├── stock/
│   │   │   ├── StockInForm.jsx
│   │   │   ├── StockOutForm.jsx
│   │   │   └── StockHistoryTable.jsx
│   │   │
│   │   └── alerts/
│   │       └── LowStockTable.jsx
│   │
│   ├── pages/
│   │   ├── LoginPage.jsx
│   │   ├── DashboardPage.jsx
│   │   ├── ProductsPage.jsx
│   │   ├── CategoriesPage.jsx
│   │   ├── SuppliersPage.jsx
│   │   ├── StockPage.jsx
│   │   └── LowStockPage.jsx
│   │
│   ├── services/
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── productService.js
│   │   ├── categoryService.js
│   │   ├── supplierService.js
│   │   ├── stockService.js
│   │   └── dashboardService.js
│   │
│   ├── routes/
│   │   ├── AppRoutes.jsx
│   │   └── ProtectedRoute.jsx
│   │
│   ├── styles/
│   │   └── app.css
│   │
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
│
├── package.json
└── vite.config.js