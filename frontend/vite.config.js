import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // Changes this if you are using a custom port
  },
  resolve: {
    extensions: ['.js', '.jsx', '.json'] // Tells Vite to look for these files automatically
  }
});