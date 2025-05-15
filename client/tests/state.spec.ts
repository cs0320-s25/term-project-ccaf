import { test, expect, Page } from "@playwright/test";

test.describe("state updates and edge cases", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:3000/");
    await page.getByRole("button", { name: "sign in" }).click();
    await page.getByRole("textbox", { name: "Email address" }).fill("mock@brown.edu");
    await page.getByRole("textbox", { name: "Email address" }).press("Enter");
    await page.getByRole("button", { name: "Continue" }).click();
    await page.getByRole("textbox", { name: "Password" }).fill("mockexample");
    await page.getByRole("button", { name: "Continue" }).click();
    await page.getByRole("button", { name: "Next" }).click();
    await page.getByRole("button", { name: "Casual" }).click();
    await page.getByRole("button", { name: "Frequently" }).click();
    await page.getByRole("button", { name: "take me there!" }).click();
  });

  test("search filters reflect state change immediately", async ({ page }) => {
    await page.getByRole("searchbox").fill("y2k");
    await page.getByRole("searchbox").press("Enter");

    // wait for results
    await expect(page.getByText(/search results for/i)).toBeVisible();

    // change filter to 'small'
    await page.getByRole("combobox", { name: /size/i }).selectOption("s");
    const draftLinks = page.locator("a[href^='/draft/']");
    const count = await draftLinks.count();
    expect(count).toBeGreaterThan(0);  // ensure items still show

    // change filter to 'blue', should be fewer items
    await page.getByRole("combobox", { name: /color/i }).selectOption("pink");
    await expect(page.locator(".product-card")).toHaveCount(0); // check if filtered out
  });

  test("modal closes when cancel is clicked", async ({ page }) => {
    await page.getByRole("searchbox").fill("nike");
    await page.getByRole("searchbox").press("Enter");

    const saveBtn = page.locator('a:has-text("depopNike")').getByLabel("button to save piece to draft");
    await saveBtn.first().click();

    const cancelBtn = page.getByRole("button", { name: "close modal" });
    await expect(cancelBtn).toBeVisible();
    await cancelBtn.click();

    await expect(cancelBtn).not.toBeVisible(); // modal should be gone
  });

test("creating duplicate draft does not work", async ({ page }) => {
  // go to drafts page and create a draft
  await page.getByRole("link", { name: "draft page (gallery)" }).click();
  await page.getByRole("button", { name: "create draft button" }).click();
  await page.getByRole("textbox", { name: /input for/i }).fill("my duplicate draft");
  await page.getByText("create", { exact: true }).click();

  // try to create the same draft again
  await page.getByRole("button", { name: "create draft button" }).click();
  await page.getByRole("textbox", { name: /input for/i }).fill("my duplicate draft");
  await page.getByText("create", { exact: true }).click();

  // expect error message about duplication
  await expect(page.getByRole("alert", { name: "error message" })).toBeVisible();
  await expect(page.getByLabel("error message")).toContainText("Draft name is not available");
});

  test("deleting a piece updates state correctly", async ({ page }) => {
    await page.getByRole("link", { name: "draft page (gallery)" }).click();
    await page.getByRole("link", { name: /test draft #2/i }).click();

    const piece = page.getByRole("link", { name: /denim/i });
    await expect(piece).toBeVisible();

    const removeBtn = piece.getByRole("button", { name: /remove/i });
    await removeBtn.click();

    const confirmBtn = page.getByRole("button", { name: /yes, delete/i });
    await confirmBtn.click();

    await expect(piece).not.toBeVisible();
    await expect(page.getByLabel("piece count")).toContainText("0 pieces");
  });

  test("draft persists across page reload", async ({ page }) => {
    await page.getByRole("link", { name: "draft page (gallery)" }).click();
    await page.reload();

    const draft = page.getByRole("link", { name: /draft name:/i });
    await expect(draft).toBeVisible();
  });

  test("saving to draft shows confirmation modal and auto-dismisses", async ({ page }) => {
    await page.getByRole("searchbox").fill("nike");
    await page.getByRole("searchbox").press("Enter");

    const saveBtn = page.locator('a:has-text("depopNike")').getByLabel("button to save piece to draft");
    await saveBtn.first().click();

    await page.getByRole("textbox", { name: /new draft name/i }).fill("playwright-confirm");
    await page.getByRole("button", { name: /create & save/i }).click();

    const confirmationPopup = page.getByText(/you saved this piece to/i);
    await expect(confirmationPopup).toBeVisible();
    await page.waitForTimeout(2500);
    await expect(confirmationPopup).not.toBeVisible(); // auto-dismiss
  });
});

// utility for signing in a test user
async function signIn(page: Page) {
  await page.goto("http://localhost:3000/");
  await page.getByRole("button", { name: "sign in" }).click();
  await page.getByRole("textbox", { name: "Email address" }).fill("mock@brown.edu");
  await page.getByRole("textbox", { name: "Email address" }).press("Enter");
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await page.getByRole("textbox", { name: "Password" }).fill("mockexample");
  await page.getByRole("button", { name: "Continue" }).click();
}

test.describe("extended search functionality", () => {
  test.beforeEach(async ({ page }) => {
    await signIn(page);
  });

  test("searching twice updates the results", async ({ page }) => {
    await page.getByRole("searchbox").fill("nike");
    await page.getByRole("searchbox").press("Enter");
    await expect(page.getByText(/denim/i)).toBeVisible();

    await page.getByRole("searchbox").fill("jordan");
    await page.getByRole("searchbox").press("Enter");
    await expect(page.getByText(/y2k/i)).toBeVisible();
  });

  test("filtering with multiple criteria works", async ({ page }) => {
    await page.getByRole("searchbox").fill("jacket");
    await page.getByRole("searchbox").press("Enter");
    await page.getByRole("combobox", { name: /size/i }).selectOption("m");
    await page.getByRole("combobox", { name: /color/i }).selectOption("blue");
    await page.getByRole("combobox", { name: /condition/i }).selectOption("like new");

    const items = page.locator(".product-card");
    const count = await items.count();
    expect(count).toBeGreaterThan(0);
  });

  test("search handles long query strings gracefully", async ({ page }) => {
    const longQuery = "very long search term for a unique secondhand fashion piece that may not exist";
    await page.getByRole("searchbox").fill(longQuery);
    await page.getByRole("searchbox").press("Enter");

    const result = page.locator(".product-card");
    const count = await result.count();
    expect(count).toBeGreaterThanOrEqual(0); // tolerate no results
    await expect(page.getByRole("main")).toBeVisible(); // no crash or error page
  });

  test("resetting filters returns full results", async ({ page }) => {
    await page.getByRole("searchbox").fill("hoodie");
    await page.getByRole("searchbox").press("Enter");

    const beforeResetCount = await page.locator(".product-card").count();

    await page.getByRole("combobox", { name: /size/i }).selectOption("l");
    await page.getByRole("combobox", { name: /color/i }).selectOption("black");
    await page.getByRole("combobox", { name: /condition/i }).selectOption("used");

    await page.getByRole("button", { name: /reset/i }).click();

    const afterResetCount = await page.locator(".product-card").count();
    expect(afterResetCount).toBeGreaterThanOrEqual(beforeResetCount);
  });
});

test("same item can be saved to multiple drafts", async ({ page }) => {
  // search for product
  await page.getByRole("searchbox").fill("nike");
  await page.getByRole("searchbox").press("Enter");

  const firstProduct = page.locator("a").filter({ hasText: "nike" }).first();

  // save to draft one
  await firstProduct.getByLabel("button to save piece to draft").click();
  await page.getByRole("textbox", { name: /input for new draft name/i }).fill("draft one");
  await page.getByRole("button", { name: /create and save/i }).click();

  // save to draft two
  await firstProduct.getByLabel("button to save piece to draft").click();
  await page.getByRole("textbox", { name: /input for new draft name/i }).fill("draft two");
  await page.getByRole("button", { name: /create and save/i }).click();

  // check both drafts have the product
  await page.getByRole("link", { name: "draft page (gallery)" }).click();
  await page.getByRole("link", { name: /draft one/i }).click();
  await expect(page.locator(".product-card")).toHaveCount(1);

  await page.getByRole("link", { name: "draft page (gallery)" }).click();
  await page.getByRole("link", { name: /draft two/i }).click();
  await expect(page.locator(".product-card")).toHaveCount(1);
});

test("saving modal closes on cancel", async ({ page }) => {
  await page.getByRole("searchbox").fill("nike");
  await page.getByRole("searchbox").press("Enter");

  const product = page.locator("a").filter({ hasText: "nike" }).first();
  await product.getByLabel("button to save piece to draft").click();

  await page.getByRole("button", { name: "close modal" }).click();

  // modal should be gone
  await expect(page.getByRole("dialog")).not.toBeVisible();
});
