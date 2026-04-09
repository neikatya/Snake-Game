const { test, expect } = require('@playwright/test');

test.describe('Snake Game Frontend Tests', () => {

    // 1. Тест загрузки: проверяем, что фронтенд узнает размер поля
    test('должен получать размер поля при старте', async ({ page }) => {
        await page.goto('/');

        // Проверяем, что текст "Размер поля" обновился (был "...")
        const sizeStatus = page.locator('#field-size');
        await expect(sizeStatus).not.toContainText('...');
    });

    // 2. Тест управления: проверяем отправку команд поворота
    test('должен отправлять правильный Direction при нажатии клавиш', async ({ page }) => {
        await page.goto('/');

        // Список клавиш и ожидаемых Enum для вашего Direction.java
        const moves = [
            { key: 'ArrowUp', expected: 'UP' },
            { key: 'ArrowRight', expected: 'RIGHT' },
            { key: 'ArrowDown', expected: 'DOWN' },
            { key: 'ArrowLeft', expected: 'LEFT' }
        ];

        for (const move of moves) {
            // Ожидаем POST запрос на /turnTo
            const requestPromise = page.waitForRequest(request =>
                request.url().includes('/turnTo') && request.method() === 'POST'
            );

            await page.keyboard.press(move.key);
            const request = await requestPromise;

            // Проверяем, что в теле запроса передана верная строка для Enum
            expect(request.postDataJSON()).toBe(move.expected);
        }
    });

    // 3. Тест игрового цикла: проверка запросов обновления
    test('должен регулярно запрашивать координаты змейки и яблок', async ({ page }) => {
        await page.goto('/');

        // Проверяем, что за короткий промежуток времени ушли запросы на координаты
        const snakeReq = await page.waitForRequest(req => req.url().includes('/getSnakeCoordinates'));
        const appleReq = await page.waitForRequest(req => req.url().includes('/getAppleCoordinates'));

        expect(snakeReq.method()).toBe('GET');
        expect(appleReq.method()).toBe('GET');
    });

    // 4. Тест завершения игры
    test('должен показывать сообщение об окончании игры, если змейка пуста', async ({ page }) => {
        // Подменяем ответ сервера, имитируя пустой список координат (смерть змейки)
        await page.route('**/getSnakeCoordinates', route =>
            route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify([])
            })
        );

        await page.goto('/');

        // Проверяем, что статус сменился на "ИГРА ОКОНЧЕНА"
        const status = page.locator('#status');
        await expect(status).toContainText('ИГРА ОКОНЧЕНА');
    });
});