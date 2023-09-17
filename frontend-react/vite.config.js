import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  // setting for localhost
  // server: {
  //   proxy: {
  //     '/api/v1/': 'http://localhost:8000/' // setting for localhost
  //   }
  // }
});
