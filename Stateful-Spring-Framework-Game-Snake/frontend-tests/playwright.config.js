const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
    testDir: './tests',
    timeout: 30000,
    use: {
        baseURL: 'http://localhost:8080',
        browserName: 'chromium',
        headless: true,
        screenshot: 'only-on-failure',
        video: 'retain-on-failure',
    },
    fullyParallel: false,
    workers: 1,
});