import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.getByRole('button', { name: 'sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('mock@brown.edu');
  await page.getByRole('textbox', { name: 'Email address' }).press('Enter');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('mockexample');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('button', { name: 'Next', exact: true }).click();
  await page.getByRole('button', { name: 'Casual' }).click();
  await page.getByRole('button', { name: 'Frequently' }).click();
  await page.getByRole('button', { name: 'take me there!' }).click();
});


/**
 * Tests that a new user sees a prompting message and only the new draft button on their gallety/
 * "my drafts" page.
 */
test('when i log in on a new account, i see prompting to make drafts', async ({ page }) => {
    await page.getByRole('link', { name: 'draft page (gallery)' }).click();
    await expect(page.getByRole('button', { name: 'create draft button' })).toBeVisible();  

    await expect(page.getByLabel('no draft prompt')).toBeVisible();
    await expect(page.getByLabel('no draft prompt')).toContainText('no drafts yet — start saving items!');
  });


test('when i try to add a draft, i can do so successfully', async ({ page }) => {
    await page.getByRole('link', { name: 'draft page (gallery)' }).click();

    await page.getByRole('button', { name: 'create draft button' }).click();
    await page.getByRole('textbox', { name: 'input for' }).click();
    await page.getByRole('textbox', { name: 'input for' }).fill('new draft #1');
    await page.getByText('create', { exact: true }).click();
    await expect(page.getByRole('link', { name: 'draft name: new draft #1, and' })).toBeVisible();
    await expect(page.getByLabel('draft name: new draft #1, and')).toContainText('new draft #1');
    await expect(page.getByLabel('draft name', { exact: true })).toContainText('new draft #1');
    await expect(page.getByLabel('piece count')).toContainText('0 pieces');
  });

test('upon re-login, the draft i added is there', async ({ page }) => {
    await page.getByRole('link', { name: 'draft page (gallery)' }).click();

    await expect(page.getByRole('link', { name: 'draft name: new draft #1, and' })).toBeVisible();
    await expect(page.getByLabel('draft name: new draft #1, and').getByLabel('draft name')).toContainText('new draft #1');
    await expect(page.getByLabel('draft name', { exact: true })).toContainText('new draft #1');
    await expect(page.getByLabel('piece count')).toContainText('0 pieces');
  });

test('if i refresh my draft stays', async ({ page }) => {
    await page.getByRole('link', { name: 'draft page (gallery)' }).click();

    await page.goto('http://localhost:3000/my-drafts');
    await expect(page.getByRole('link', { name: 'draft name: new draft #1, and' })).toBeVisible();
    await expect(page.getByLabel('draft name: new draft #1, and')).toContainText('new draft #1');
    await expect(page.getByLabel('draft name', { exact: true })).toContainText('new draft #1');
    await expect(page.getByLabel('piece count')).toContainText('0 pieces');
  });


test('when i click on a draft all the info i expect is there', async ({ page }) => {
  await page.getByRole('link', { name: 'draft page (gallery)' }).click();

  await page.getByRole('link', { name: 'draft name: new draft #1, and' }).click();
  await expect(page.locator('div').filter({ hasText: /^new draft #1$/ })).toBeVisible();
  await expect(page.getByLabel('piece count')).toContainText('0 pieces');
  await expect(page.getByLabel('no pieces saved prompt')).toContainText('no pieces in this draft yet. time to start saving!');
  await expect(page.getByLabel('no pieces saved prompt')).toBeVisible();
  await expect(page.getByRole('button', { name: 'delete draft button' })).toBeVisible();
});


test('i can successfully delete a draft', async ({ page }) => {
  await page.getByRole('link', { name: 'draft page (gallery)' }).click();

  await page.getByRole('link', { name: 'draft name: new draft #1, and' }).click();
  await expect(page.getByRole('button', { name: 'delete draft button' })).toBeVisible();
  await page.getByRole('button', { name: 'delete draft button' }).click();
  await page.getByRole('button', { name: 'button to confirm draft' }).click();
  await expect(page.getByLabel('no draft prompt')).toBeVisible();
  await expect(page.getByRole('heading', { name: 'my gallery' })).toBeVisible();
});

test('i can search for and add a piece', async ({ page }) => {
  await page.getByRole('searchbox', { name: 'Search for clothing, brands,' }).click();
  await page.getByRole('searchbox', { name: 'Search for clothing, brands,' }).fill('nike vintage');
  await page.getByRole('searchbox', { name: 'Search for clothing, brands,' }).press('Enter');
  await expect(page.locator('a').filter({ hasText: 'poshmarkNike Vintage' })).toBeVisible();

  await page.pause();

  await expect(page.getByRole('heading', { name: 'search results for: "nike' })).toBeVisible();
  await expect(page.getByRole('main')).toContainText('Nike Vintage');
  await expect(page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' }).getByLabel('button to save piece to draft')).toBeVisible();

  await page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' }).getByLabel('button to save piece to draft').click();
  await page.getByRole('textbox', { name: 'input for new draft name' }).click();
  await page.getByRole('textbox', { name: 'input for new draft name' }).fill('test draft #2');
  await page.getByRole('button', { name: 'button to create and save new' }).click();

  await expect(page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' }).getByLabel('button to save piece to draft')).toBeVisible();
  await expect(page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' }).getByLabel('button to save piece to draft')).toBeVisible();
  await expect(page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' }).getByLabel('button to save piece to draft')).toBeVisible();
});


test('i can view a piece in the draft', async ({ page }) => {
  await page.getByRole('link', { name: 'draft page (gallery)' }).click();
  // await expect(page.getByText('Loading complete')).toBeVisible({ timeout: 5000 });
  await expect(page.getByLabel('view drafts is loading')).not.toBeVisible({ timeout: 5000 });  
  await page.pause();
  await expect(page.getByLabel('draft name', { exact: true })).toContainText('test draft #2');
  await page.getByRole('link', { name: 'draft name: test draft #2,' }).click();

  await expect(page.getByLabel('no pieces saved prompt')).not.toBeVisible({ timeout: 5000 });
  await expect(page.getByRole('link', { name: 'piece titled: Nike Vintage' })).toBeVisible();
  await expect(page.getByLabel('piece title', { exact: true })).toContainText('Nike Vintage Windbreaker Jacket');
})



test('i can successfully remove a piece from a draft', async ({ page }) => {
  // getting to individual page, checking for what we saved
  await page.getByRole('link', { name: 'draft page (gallery)' }).click();
  await page.getByRole('link', { name: 'draft name: test draft #2,' }).click();

  await expect(page.getByLabel('no pieces saved prompt')).not.toBeVisible({ timeout: 5000 });
  await expect(page.getByRole('link', { name: 'piece titled: Nike Vintage' })).toBeVisible();


  // deletion
  await expect(page.getByRole('button', { name: 'button to remove piece from' })).toBeVisible();
  await page.getByRole('button', { name: 'button to remove piece from' }).click();
  
  // checking post deletion
  await expect(page.getByRole('link', { name: 'piece titled: Nike Vintage' })).not.toBeVisible();

  // check if the piece is still in the dom
  await expect(page.getByRole('link', { name: 'piece titled: Nike Vintage' })).toHaveCount(0);
  await expect(page.getByLabel('no pieces saved prompt')).toBeVisible();
  await expect(page.getByLabel('no pieces saved prompt')).toContainText('no pieces in this draft yet. time to start saving!');
  await expect(page.getByLabel('piece count')).toContainText('0 pieces');
});

test('i can successfully delete a draft, part 2, to cleanup the last part', async ({ page }) => {
  await page.getByRole('link', { name: 'draft page (gallery)' }).click();

  await page.getByRole('link', { name: 'draft name: test draft #2,' }).click();
  await expect(page.getByRole('button', { name: 'delete draft button' })).toBeVisible();
  await page.getByRole('button', { name: 'delete draft button' }).click();
  await page.getByRole('button', { name: 'button to confirm draft' }).click();
  await expect(page.getByLabel('no draft prompt')).toBeVisible();
  await expect(page.getByRole('heading', { name: 'my gallery' })).toBeVisible();
});


test('testing view on a piece dropdown', async ({ page }) => {
  await page.getByRole('link', { name: 'draft page (gallery)' }).click();
  // add three test drafts
  await page.getByText('+new draft').click();
  await page.getByRole('textbox', { name: 'input for' }).click();
  await page.getByRole('textbox', { name: 'input for' }).fill('t1');
  await page.getByText('create', { exact: true }).click();

  await page.getByText('+new draft').click();
  await page.getByRole('textbox', { name: 'input for' }).click();
  await page.getByRole('textbox', { name: 'input for' }).fill('t2');
  await page.getByText('create', { exact: true }).click();

  await page.getByText('+new draft').click();
  await page.getByRole('textbox', { name: 'input for' }).click();
  await page.getByRole('textbox', { name: 'input for' }).fill('t3');
  await page.getByText('create', { exact: true }).click();

  await page.getByRole('link', { name: 'home/search page' }).click();
  await page.getByRole('searchbox', { name: 'Search for clothing, brands,' }).click();
  await page.getByRole('searchbox', { name: 'Search for clothing, brands,' }).fill('nike vintage');
  await page.getByRole('searchbox', { name: 'Search for clothing, brands,' }).press('Enter');

  // check that the drafts appear when you go to save a new draft
  await expect(page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' })).toBeVisible();
  await expect(page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' }).getByLabel('button to save piece to draft')).toBeVisible();
  await page.locator('a').filter({ hasText: 'depopNike Vintage Windbreaker' }).getByLabel('button to save piece to draft').click();

  await expect(page.getByRole('button', { name: 'button to save to draft t1' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'button to save to draft t3' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'button to save to draft t2' })).toBeVisible();
  await expect(page.getByLabel('button to save to draft t1')).toContainText('t1');
  await expect(page.getByLabel('button to save to draft t3')).toContainText('t3');
  await expect(page.getByLabel('button to save to draft t2')).toContainText('t2');

  await page.getByRole('button', { name: 'close modal' }).click();

  await page.getByRole('link', { name: 'draft page (gallery)' }).click();

  // delete everything
  await page.getByRole('link', { name: 'draft name: t1, and it has 0' }).click();
  await page.getByRole('button', { name: 'delete draft button' }).click();
  await page.getByRole('button', { name: 'button to confirm draft' }).click();
  await page.getByRole('link', { name: 'draft name: t3, and it has 0' }).click();
  await page.getByRole('button', { name: 'delete draft button' }).click();
  await page.getByRole('button', { name: 'button to confirm draft' }).click();
  await page.getByRole('link', { name: 'draft name: t2, and it has 0' }).click();
  await page.getByRole('button', { name: 'delete draft button' }).click();
  await page.getByRole('button', { name: 'button to confirm draft' }).click();
});

test('i cannot use a draft name that already exists', async ({ page }) => {
  await page.getByRole('link', { name: 'draft page (gallery)' }).click();

  await page.getByRole('button', { name: 'create draft button' }).click();
  await page.getByRole('textbox', { name: 'input for' }).click();
  await page.getByRole('textbox', { name: 'input for' }).fill('duplicate');
  await page.getByText('create', { exact: true }).click();

  await expect(page.getByRole('link', { name: 'draft name: duplicate, and it' })).toBeVisible();
  await expect(page.getByLabel('draft name', { exact: true })).toContainText('duplicate');

  // try to make the same draft
  await page.getByRole('button', { name: 'create draft button' }).click();
  await page.getByRole('textbox', { name: 'input for' }).click();
  await page.getByRole('textbox', { name: 'input for' }).fill('duplicate');
  await page.getByText('create', { exact: true }).click();

  // get the error
  await expect(page.getByRole('alert', { name: 'error message' })).toBeVisible();
  await expect(page.getByLabel('error message')).toContainText('Draft name is not available');

  // change it to something unique
  await page.getByRole('textbox', { name: 'input for' }).click();
  await page.getByRole('textbox', { name: 'input for' }).click();
  await page.getByRole('textbox', { name: 'input for' }).fill('duplicate no more!');
  await page.getByText('create', { exact: true }).click();

  // it should show up now, no error!
  await expect(page.getByRole('link', { name: 'draft name: duplicate no more' })).toBeVisible();
  await expect(page.getByLabel('draft name: duplicate no more').getByLabel('draft name')).toContainText('duplicate no more!');


  // delete + reset
  await page.getByRole('link', { name: 'draft name: duplicate, and it' }).click();
  await page.getByRole('button', { name: 'delete draft button' }).click();
  await page.getByRole('button', { name: 'button to confirm draft' }).click();
  await page.getByRole('link', { name: 'draft name: duplicate no more' }).click();
  await page.getByRole('button', { name: 'delete draft button' }).click();
  await page.getByRole('button', { name: 'button to confirm draft' }).click();
});