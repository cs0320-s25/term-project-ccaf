"use client";

import { useUser } from "@clerk/nextjs";
import { useEffect } from "react";
import { initializeUser } from "@/utils/user";

export function UserInitializer() {
  const { user, isLoaded } = useUser();

  useEffect(() => {
    if (isLoaded && user?.id) {
      initializeUser(user.id)
        .then(() => console.log("User initialized in Firebase:", user.id))
        .catch((error) => console.error("Failed to initialize user:", error));
    }
  }, [user?.id, isLoaded]);

  return null;
}
