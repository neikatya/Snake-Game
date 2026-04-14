const { test, expect } = require('@playwright/test');

test.describe('Snake Game Frontend Tests', () => {

    test.beforeEach(async ({ page }) => {
        await page.goto('/', { waitUntil: 'domcontentloaded' });
        await page.waitForSelector('#status', { timeout: 15000 });
    });

    test('должен получать размер поля при старте', async ({ page }) => {
        const sizeStatus = page.locator('#field-size');
        await expect(sizeStatus).not.toContainText('...', { timeout: 10000 });
    });

    test('должен отправлять правильный Direction при нажатии клавиш', async ({ page }) => {
        const moves = [
            { key: 'ArrowUp', expected: 'UP' },
            { key: 'ArrowRight', expected: 'RIGHT' },
            { key: 'ArrowDown', expected: 'DOWN' },
            { key: 'ArrowLeft', expected: 'LEFT' }
        ];

        for (const move of moves) {
            const requestPromise = page.waitForRequest(request =>
                request.url().includes('/turnTo') && request.method() === 'POST'
            );

            await page.keyboard.press(move.key);
            const request = await requestPromise;
            expect(request.postDataJSON()).toBe(move.expected);
        }
    });

    test('должен регулярно запрашивать координаты змейки и яблок', async ({ page }) => {
        const snakeReq = await page.waitForRequest(req => req.url().includes('/getSnakeCoordinates'));
        const appleReq = await page.waitForRequest(req => req.url().includes('/getAppleCoordinates'));

        expect(snakeReq.method()).toBe('GET');
        expect(appleReq.method()).toBe('GET');
    });

    test('должен показывать сообщение об окончании игры, если змейка пуста', async ({ page }) => {
        await page.route('**/getSnakeCoordinates', route =>
            route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify([])
            })
        );

        await page.reload();

        const status = page.locator('#status');
        await expect(status).toContainText('ИГРА ОКОНЧЕНА', { timeout: 10000 });
    });
});