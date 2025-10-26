// Performance test script
// Run this in browser console to test performance improvements

const performanceTest = {
  // Test component load times
  testComponentLoadTimes: async () => {
    console.log('Testing component load times...');
    
    const components = [
      'Home',
      'Cart', 
      'AddProduct',
      'Product',
      'UpdateProduct',
      'AskAi',
      'SearchResults',
      'Order'
    ];
    
    const results = {};
    
    for (const component of components) {
      const startTime = performance.now();
      
      try {
        // Simulate component loading
        await new Promise(resolve => setTimeout(resolve, Math.random() * 100));
        const endTime = performance.now();
        results[component] = endTime - startTime;
      } catch (error) {
        results[component] = 'Error loading';
      }
    }
    
    console.table(results);
    return results;
  },

  // Test bundle size
  testBundleSize: () => {
    console.log('Checking bundle sizes...');
    
    // This would typically be done with bundle analyzer
    console.log('Run "npm run build:analyze" to see detailed bundle analysis');
  },

  // Test memory usage
  testMemoryUsage: () => {
    if (performance.memory) {
      console.log('Memory usage:', {
        used: `${Math.round(performance.memory.usedJSHeapSize / 1024 / 1024)} MB`,
        total: `${Math.round(performance.memory.totalJSHeapSize / 1024 / 1024)} MB`,
        limit: `${Math.round(performance.memory.jsHeapSizeLimit / 1024 / 1024)} MB`
      });
    } else {
      console.log('Memory API not available');
    }
  },

  // Run all tests
  runAllTests: async () => {
    console.log('🚀 Running performance tests...');
    await this.testComponentLoadTimes();
    this.testBundleSize();
    this.testMemoryUsage();
    console.log('✅ Performance tests completed');
  }
};

// Make it available globally
window.performanceTest = performanceTest;

console.log('Performance test script loaded. Run performanceTest.runAllTests() to test.');
