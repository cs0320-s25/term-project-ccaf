
export async function initializeUser(userId: string) {
  try {
    const response = await fetch(
      `http://localhost:3232/check-user?uid=${userId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    if (!response.ok) {
      throw new Error("Failed to initialize user");
    }

    const data = await response.json();
    console.log("User initialization response:", data);
    return data;
  } catch (error) {
    console.error("Error initializing user:", error);
    throw error;
  }
}
