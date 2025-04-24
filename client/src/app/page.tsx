'use client';


import { useEffect, useState } from "react";
import { useUser, SignedIn, SignedOut, SignInButton, SignOutButton } from '@clerk/nextjs';
import { OnboardingSurvey } from "@/components/onboarding-survey";
import { SearchBar } from "@/components/search-bar";
import { RecommendationFeed } from "@/components/recommendation-feed";
import "./globals.css";

export default function Home() {

// these are leftover from when i tried to do some sort of first time user monitoring for the onboarding survey
// setup but it appears we'll have to use a cookie :O
  const { user, isLoaded } = useUser();
  const [showOnboarding, setShowOnboarding] = useState(false);


  return (
    <div className="container px-4 py-16 max-w-5xl mx-auto">
      <div className="text-center mb-12">
        <h1>welcome to Draft</h1>
        <p className="body-text">
          Draft lets you collect secondhand fashion finds from across the web
          <br />
          into curated style Drafts—your personal moodboards :-)
        </p>
      </div>

      <SignedOut>
        <div className="text-center mb-12">
          <h2>Please sign in to start drafting items!</h2>
          <SignInButton />
        </div>
      </SignedOut>

      <SignedIn>
        {showOnboarding && <OnboardingSurvey />}

        <div className="mb-12">
          <SearchBar />
        </div>

        <RecommendationFeed />

        <div className="mt-6 text-center">
          <SignOutButton />
        </div>
      </SignedIn>
    </div>
  );
}