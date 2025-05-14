"use client"

import { SignedIn, SignedOut, SignInButton, UserButton } from "@clerk/nextjs";
import Link from "next/link";

export default function Header() {
  return (
    <header className="fixed top-0 left-0 w-full z-50 bg-white border-b shadow">
      <div className="container flex h-16 items-center justify-between px-4">
        <Link href="/" className="text-2xl font-bold">
          draft
        </Link>
        <nav className="flex items-center gap-6">
          <Link href="/" className="text-sm" aria-label="home/search page">
            explore
          </Link>
          <Link href="/my-drafts" className="text-sm" aria-label="draft page (gallery)">
            my drafts
          </Link>

          {/* Show sign-in button if signed out */}
          <SignedOut>
            <SignInButton mode="modal">
              <button className="px-4 py-2 text-sm font-medium bg-black text-white rounded-full">
                log in
              </button>
            </SignInButton>
          </SignedOut>

          {/* Show user button if signed in */}
          <SignedIn>
            <UserButton afterSignOutUrl="/" />
          </SignedIn>
        </nav>
      </div>
    </header>
  );
}

