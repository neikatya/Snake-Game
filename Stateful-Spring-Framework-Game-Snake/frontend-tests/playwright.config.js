const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
    testDir: './tests',
    timeout: 30000,
    use: {
        baseURL: 'http://127.0.0.1:8080',
        browserName: 'chromium',
        headless: true,
        screenshot: 'only-on-failure',
        video: 'retain-on-failure',
    },
    fullyParallel: false,
    workers: 1,
});