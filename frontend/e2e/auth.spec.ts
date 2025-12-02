import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/home');
  });

  test('should register a new passenger user', async ({ page }) => {
    // Naviguer vers la page d'inscription
    await page.click('text=S\'inscrire');
    await expect(page).toHaveURL('/register');

    // Générer un email unique
    const timestamp = Date.now();
    const email = `test.passenger.${timestamp}@test.com`;

    // Remplir le formulaire d'inscription
    await page.fill('input[name="firstName"]', 'Test');
    await page.fill('input[name="lastName"]', 'Passenger');
    await page.fill('input[name="email"]', email);
    await page.fill('input[name="password"]', 'Test@1234');
    await page.fill('input[name="phoneNumber"]', '+212600000001');

    // Soumettre le formulaire
    await page.click('button[type="submit"]');

    // Vérifier la redirection vers le dashboard
    await expect(page).toHaveURL('/', { timeout: 10000 });

    // Vérifier que l'utilisateur est connecté
    await expect(page.locator('text=Tableau de Bord')).toBeVisible();
  });

  test('should login with valid credentials', async ({ page }) => {
    // Naviguer vers la page de connexion
    await page.click('text=Se connecter');
    await expect(page).toHaveURL('/login');

    // Utiliser les credentials d'un utilisateur de test existant
    await page.fill('input[name="email"]', 'passenger@test.com');
    await page.fill('input[name="password"]', 'Test@1234');

    // Soumettre le formulaire
    await page.click('button[type="submit"]');

    // Vérifier la redirection vers le dashboard
    await expect(page).toHaveURL('/', { timeout: 10000 });

    // Vérifier que l'utilisateur est connecté
    await expect(page.locator('text=Tableau de Bord')).toBeVisible();
  });

  test('should show error with invalid credentials', async ({ page }) => {
    await page.click('text=Se connecter');
    await expect(page).toHaveURL('/login');

    // Credentials invalides
    await page.fill('input[name="email"]', 'invalid@test.com');
    await page.fill('input[name="password"]', 'WrongPassword');

    await page.click('button[type="submit"]');

    // Vérifier que l'erreur est affichée
    await expect(page.locator('text=/erreur|échec|invalide/i')).toBeVisible({ timeout: 5000 });
  });

  test('should logout successfully', async ({ page }) => {
    // Se connecter d'abord
    await page.goto('/login');
    await page.fill('input[name="email"]', 'passenger@test.com');
    await page.fill('input[name="password"]', 'Test@1234');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/');

    // Se déconnecter
    await page.click('text=Déconnexion');

    // Vérifier la redirection vers la page de connexion
    await expect(page).toHaveURL('/login', { timeout: 5000 });
  });

  test('should validate form fields', async ({ page }) => {
    await page.goto('/register');

    // Soumettre sans remplir les champs
    await page.click('button[type="submit"]');

    // Vérifier les messages de validation
    const validationMessages = await page.locator('input:invalid').count();
    expect(validationMessages).toBeGreaterThan(0);
  });
});
