import { defineConfig, devices } from '@playwright/test';

/**
 * Configuration Playwright pour les tests E2E
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './e2e',

  /* Timeout maximum par test */
  timeout: 30 * 1000,

  /* Configuration des expect */
  expect: {
    timeout: 5000
  },

  /* Exécuter les tests en parallèle */
  fullyParallel: true,

  /* Échouer le build si des tests sont marqués .only */
  forbidOnly: !!process.env.CI,

  /* Nombre de tentatives en cas d'échec */
  retries: process.env.CI ? 2 : 0,

  /* Workers en parallèle */
  workers: process.env.CI ? 1 : undefined,

  /* Reporter à utiliser */
  reporter: [
    ['html'],
    ['list'],
    ['json', { outputFile: 'test-results/results.json' }]
  ],

  /* Configuration partagée pour tous les projets */
  use: {
    /* URL de base de l'application */
    baseURL: 'http://localhost:5173',

    /* Collecter les traces en cas d'échec uniquement */
    trace: 'on-first-retry',

    /* Captures d'écran en cas d'échec */
    screenshot: 'only-on-failure',

    /* Vidéo en cas d'échec */
    video: 'retain-on-failure',
  },

  /* Configuration pour chaque navigateur */
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },

    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },

    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },

    /* Tests mobile */
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
    {
      name: 'Mobile Safari',
      use: { ...devices['iPhone 12'] },
    },
  ],

  /* Démarrer le serveur de dev avant les tests */
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
    timeout: 120 * 1000,
  },
});
