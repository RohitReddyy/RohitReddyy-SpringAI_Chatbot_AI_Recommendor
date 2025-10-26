import { lazy } from 'react';

// Only lazy load non-critical components
// Home should load immediately for better UX
import Home from '../components/Home';

// Lazy load secondary components
export const Cart = lazy(() => import('../components/Cart'));
export const AddProduct = lazy(() => import('../components/AddProduct'));
export const Product = lazy(() => import('../components/Product'));
export const UpdateProduct = lazy(() => import('../components/UpdateProduct'));
export const AskAi = lazy(() => import('../components/AskAI'));
export const SearchResults = lazy(() => import('../components/SearchResults'));
export const Order = lazy(() => import('../components/Order'));

// Export Home as default import
export { Home };
