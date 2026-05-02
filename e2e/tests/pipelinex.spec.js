const { test, expect } = require('@playwright/test');

async function login(page, email, password = 'password') {
  await page.goto('/login');
  await page.getByLabel('Email').fill(email);
  await page.getByLabel('Password').fill(password);
  await page.getByRole('button', { name: 'Sign In' }).click();
}

test('admin login lands on dashboard', async ({ page }) => {
  await login(page, 'admin@pipelinex.local');
  await expect(page).toHaveURL(/dashboard/);
  await expect(page.getByText('Operational dashboard')).toBeVisible();
});

test('rep login is forced to change password', async ({ page }) => {
  await login(page, 'rep.noah@pipelinex.local');
  await expect(page).toHaveURL(/profile\/password\/force/);
  await expect(page.getByText('Update your temporary password')).toBeVisible();
});

test('rep cannot see admin users page', async ({ page }) => {
  await login(page, 'rep.noah@pipelinex.local');
  await page.goto('/users');
  await expect(page.getByText('Access denied')).toBeVisible();
});

test('theme toggle works on login page shell after auth', async ({ page }) => {
  await login(page, 'admin@pipelinex.local');
  await page.getByRole('button', { name: 'Toggle theme' }).click();
  await expect(page.locator('html')).toHaveAttribute('data-theme', 'dark');
});
