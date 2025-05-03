import { expect, test } from "@playwright/test";

/**
  The general shapes of tests in Playwright Test are:
    1. Navigate to a URL
    2. Interact with the page
    3. Assert something about the page against your expectations
  Look for this pattern in the tests below!
 */


/**
 * Tests that a new user sees a prompting message and only the new draft button on their gallety/
 * "my drafts" page.
 */
test('when i log in on a new account, i see prompting to make drafts', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'sign in' }).click();
    await page.getByRole('textbox', { name: 'Email address' }).click();
    await page.getByRole('textbox', { name: 'Email address' }).fill('mock@brown.edu');
    await page.getByRole('textbox', { name: 'Email address' }).press('Enter');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('mockexample');
    await page.getByRole('button', { name: 'Continue' }).click();
    await page.getByRole('link', { name: 'my drafts' }).click();
    await expect(page.getByRole('button', { name: '+ New Draft' })).toBeVisible();
    await expect(page.getByText('no drafts yet — start saving')).toBeVisible();
  });

test('when i try to add a draft, i can do so successfully', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'sign in' }).click();
    await page.locator('div').filter({ hasText: /^Email address$/ }).nth(2).click();
    await page.getByRole('textbox', { name: 'Email address' }).fill('mock@brown.edu');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('mockexample');
    await page.getByRole('button', { name: 'Continue' }).click();
    await page.getByRole('link', { name: 'my drafts' }).click();
    page.once('dialog', async dialog => {
      console.log(`Dialog message: ${dialog.message()}`);
      await dialog.accept('new draft #1');    
    });
    await page.getByRole('button', { name: '+ New Draft' }).click();
    await expect(page.getByRole('link', { name: 'new draft #1 0 pieces' })).toBeVisible();
    await expect(page.getByRole('main')).toContainText('new draft #1');
    await expect(page.getByRole('main')).toContainText('0 pieces');
  });

test('upon re-login, the draft i added is there', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'sign in' }).click();
    await page.getByRole('textbox', { name: 'Email address' }).click();
    await page.getByRole('textbox', { name: 'Email address' }).fill('mock@brown.edu');
    await page.getByRole('textbox', { name: 'Email address' }).press('Enter');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('mockexample');
    await page.getByRole('button', { name: 'Continue' }).click();
    await page.getByRole('link', { name: 'my drafts' }).click();
    await expect(page.getByRole('link', { name: 'new draft #1 0 pieces' })).toBeVisible();
    await expect(page.getByRole('main')).toContainText('new draft #1');
  });

test('if i refresh my draft stays', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await page.getByRole('button', { name: 'sign in' }).click();
    await page.getByRole('textbox', { name: 'Email address' }).click();
    await page.getByRole('textbox', { name: 'Email address' }).fill('mock@brown.edu');
    await page.getByRole('textbox', { name: 'Email address' }).press('Enter');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('textbox', { name: 'Password' }).fill('mockexample');
    await page.getByRole('button', { name: 'Continue' }).click();
    await page.getByRole('link', { name: 'my drafts' }).click();
    await expect(page.getByRole('link', { name: 'new draft #1 0 pieces' })).toBeVisible();
    await expect(page.getByRole('main')).toContainText('new draft #1');
    await page.goto('http://localhost:3000/my-drafts');
    await expect(page.getByRole('link', { name: 'new draft #1 0 pieces' })).toBeVisible();
    await expect(page.getByRole('main')).toContainText('new draft #1');
  });
