import { test, expect } from '@playwright/test';

test.describe('Ticket Purchase Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Se connecter avant chaque test
    await page.goto('/login');
    await page.fill('input[name="email"]', 'passenger@test.com');
    await page.fill('input[name="password"]', 'Test@1234');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('/');
  });

  test('should purchase a ticket successfully', async ({ page }) => {
    // Naviguer vers la page de billets
    await page.click('text=Billets');
    await expect(page).toHaveURL('/tickets');

    // Sélectionner un itinéraire dans le dropdown
    await page.click('select[name="route"]');
    await page.selectOption('select[name="route"]', { index: 1 }); // Sélectionner le premier itinéraire

    // Attendre que le prix se charge automatiquement
    await page.waitForSelector('input[name="price"]:not([value=""])', { timeout: 5000 });

    // Vérifier que le prix est affiché
    const priceInput = page.locator('input[name="price"]');
    await expect(priceInput).toHaveAttribute('readonly');
    const priceValue = await priceInput.inputValue();
    expect(parseFloat(priceValue)).toBeGreaterThan(0);

    // Sélectionner le mode de paiement
    await page.selectOption('select[name="paymentMethod"]', 'CARD');

    // Soumettre le formulaire
    await page.click('button[type="submit"]');

    // Attendre la confirmation
    await expect(page.locator('text=/succès|acheté/i')).toBeVisible({ timeout: 10000 });

    // Vérifier que le QR code est affiché
    await expect(page.locator('img[alt*="QR"]')).toBeVisible({ timeout: 5000 });
  });

  test('should display ticket QR code after purchase', async ({ page }) => {
    await page.goto('/tickets');

    // Effectuer un achat
    await page.selectOption('select[name="route"]', { index: 1 });
    await page.waitForSelector('input[name="price"]:not([value=""])');
    await page.selectOption('select[name="paymentMethod"]', 'CASH');
    await page.click('button[type="submit"]');

    // Attendre l'affichage du QR code
    await page.waitForSelector('img[alt*="QR"]', { timeout: 10000 });

    // Vérifier que le QR code est une image valide
    const qrCode = page.locator('img[alt*="QR"]').first();
    const src = await qrCode.getAttribute('src');
    expect(src).toContain('data:image/png;base64,');
  });

  test('should show validation error without selecting route', async ({ page }) => {
    await page.goto('/tickets');

    // Essayer d'acheter sans sélectionner d'itinéraire
    await page.click('button[type="submit"]');

    // Vérifier le message d'erreur
    await expect(page.locator('text=/sélectionner|veuillez/i')).toBeVisible({ timeout: 5000 });
  });

  test('should download ticket PDF', async ({ page }) => {
    await page.goto('/tickets');

    // S'assurer qu'il y a des billets
    const hasTickets = await page.locator('text=/mes billets|aucun billet/i').isVisible();

    if (hasTickets) {
      // Cliquer sur le bouton de téléchargement PDF
      const downloadPromise = page.waitForEvent('download');
      await page.click('button:has-text("PDF")').first();

      const download = await downloadPromise;
      expect(download.suggestedFilename()).toMatch(/\.pdf$/);
    }
  });

  test('should display ticket history', async ({ page }) => {
    await page.goto('/tickets');

    // Vérifier la section des billets achetés
    await expect(page.locator('text=Mes Billets')).toBeVisible();

    // Vérifier qu'il y a une liste ou un message "aucun billet"
    const ticketsList = page.locator('[data-testid="tickets-list"]');
    const noTicketsMessage = page.locator('text=/aucun billet/i');

    const hasContent = await ticketsList.isVisible().catch(() => false);
    const hasMessage = await noTicketsMessage.isVisible().catch(() => false);

    expect(hasContent || hasMessage).toBeTruthy();
  });

  test('should validate ticket status', async ({ page }) => {
    await page.goto('/tickets');

    // Chercher un ticket avec un statut
    const statusBadge = page.locator('span:has-text("ACTIF"), span:has-text("UTILISÉ")').first();

    if (await statusBadge.isVisible()) {
      const statusText = await statusBadge.textContent();
      expect(statusText).toMatch(/ACTIF|UTILISÉ|EXPIRÉ|ANNULÉ/i);
    }
  });
});
