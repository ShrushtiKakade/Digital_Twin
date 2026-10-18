/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        twin: {
          bg: '#0f172a',
          card: '#1e293b',
          border: '#334155',
          primary: '#0ea5e9',
          success: '#10b981',
          warning: '#f59e0b',
          danger: '#ef4444',
          accent: '#8b5cf6'
        }
      }
    },
  },
  plugins: [],
}
