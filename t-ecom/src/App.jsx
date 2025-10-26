import React, { useState, Suspense, lazy } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AppProvider } from "./Context/Context";
import Navbar from "./components/Navbar"; // Load Navbar immediately - it's critical UI
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { ToastContainer } from "react-toastify";

// Import lazy-loaded components from routes
import { 
  Home, 
  Cart, 
  AddProduct, 
  Product, 
  UpdateProduct, 
  AskAi, 
  SearchResults, 
  Order
} from "./routes";

// Simple, lightweight loading component
const LoadingSpinner = () => (
  <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '200px' }}>
    <div className="spinner-border text-primary" role="status">
      <span className="visually-hidden">Loading...</span>
    </div>
  </div>
);

function App() {
  const [selectedCategory, setSelectedCategory] = useState("");

  const handleCategorySelect = (category) => {
    setSelectedCategory(category);
    console.log("Selected category:", category);
  };

  return (
    <AppProvider>
      <BrowserRouter>
        <ToastContainer autoClose={2000} hideProgressBar={true} />
        <Navbar onSelectCategory={handleCategorySelect} />
        <div className="min-vh-100 bg-light">
          <Suspense fallback={<LoadingSpinner />}>
            <Routes>
              <Route
                path="/"
                element={<Home selectedCategory={selectedCategory} />}
              />
              <Route path="/add_product" element={<AddProduct />} />
              <Route path="/product" element={<Product />} />
              <Route path="product/:id" element={<Product />} />
              <Route path="/cart" element={<Cart />} />
              <Route path="/product/update/:id" element={<UpdateProduct />} />
              <Route path="/askai" element={<AskAi />} />
              <Route path="/search-results" element={<SearchResults />} />
              <Route path="/orders" element={<Order />} />
            </Routes>
          </Suspense>
        </div>
      </BrowserRouter>
    </AppProvider>
  );
}

export default App;