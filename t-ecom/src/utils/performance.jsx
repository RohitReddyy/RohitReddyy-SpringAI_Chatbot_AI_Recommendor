import React from 'react';

// Performance monitoring utilities
export const performanceMonitor = {
  // Track component load times
  trackComponentLoad: (componentName) => {
    const startTime = performance.now();
    return () => {
      const endTime = performance.now();
      const loadTime = endTime - startTime;
      console.log(`${componentName} loaded in ${loadTime.toFixed(2)}ms`);
      
      // Store in localStorage for analytics
      const metrics = JSON.parse(localStorage.getItem('performanceMetrics') || '{}');
      metrics[componentName] = {
        loadTime,
        timestamp: Date.now()
      };
      localStorage.setItem('performanceMetrics', JSON.stringify(metrics));
    };
  },

  // Track route changes
  trackRouteChange: (fromRoute, toRoute) => {
    const routeChangeTime = performance.now();
    console.log(`Route changed from ${fromRoute} to ${toRoute} at ${routeChangeTime}`);
  },

  // Get performance metrics
  getMetrics: () => {
    return JSON.parse(localStorage.getItem('performanceMetrics') || '{}');
  },

  // Clear metrics
  clearMetrics: () => {
    localStorage.removeItem('performanceMetrics');
  }
};

// Higher-order component for performance tracking
export const withPerformanceTracking = (WrappedComponent, componentName) => {
  return function PerformanceTrackedComponent(props) {
    const trackLoad = performanceMonitor.trackComponentLoad(componentName);
    
    React.useEffect(() => {
      trackLoad();
    }, []);

    return <WrappedComponent {...props} />;
  };
};
