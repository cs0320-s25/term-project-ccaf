"use client";

import { useEffect, useState } from "react";
import {
  useUser,
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
} from "@clerk/nextjs";
import { OnboardingSurvey } from "@/components/onboarding-survey";
import { SearchBar } from "@/components/search-bar";
import { RecommendationFeed } from "@/components/recommendation-feed";
import "./globals.css";

interface Piece {
  id: string;
  title: string;
  price: number;
  sourceWebsite: string;
  url: string;
  size: string;
  color: string;
  condition: string;
  imageUrl: string;
}

export default function Home() {
  const { user, isLoaded } = useUser();
  const [showOnboarding, setShowOnboarding] = useState(false);

  useEffect(() => {
    if (isLoaded && user) {
      const onboardingCompleted = localStorage.getItem(`onboardingCompleted-${user.id}`);
      if (!onboardingCompleted) {
        setShowOnboarding(true);
      }
    }
  }, [user, isLoaded]);

  const handleCompleteOnboarding = () => {
    if (user) {
      localStorage.setItem(`onboardingCompleted-${user.id}`, "true");
      setShowOnboarding(false);
    }
  };

  return (
    <div className="container px-4 py-16 max-w-5xl mx-auto">
      {/* Heading */}
      <div className="text-center mb-12">
        <h1 className="text-4xl font-semibold tracking-tight">welcome to Draft</h1>
        <p className="text-[1.25rem] tracking-[-0.03em] text-muted-foreground mt-2">
          Draft lets you collect secondhand fashion finds from across the web
          <br />
          into curated style Drafts—your personal moodboards :-)
        </p>
      </div>

      <SignedOut>
        <div className="text-center">
          <h2>sign in to start drafting!</h2>
          <SignInButton>
            <button className="btn-outline-rounded mt-4">sign in</button>
          </SignInButton>
        </div>
      </SignedOut>

      <SignedIn>
        {showOnboarding ? (
          <OnboardingSurvey onComplete={handleCompleteOnboarding} />
        ) : (
          <>
            <div className="mb-12">
              <SearchBar />
            </div>

            <RecommendationFeed />

            <div className="mt-10 text-center">
              <SignOutButton>
                <button className="btn-outline-rounded">sign out</button>
              </SignOutButton>
            </div>
          </>
        )}
      </SignedIn>
    </div>
  );
}