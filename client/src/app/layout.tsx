import { ClerkProvider } from "@clerk/nextjs";

import "./globals.css"
import { Inter } from "next/font/google"
import { ReactNode } from "react"
import Header from "@/components/header"

const inter = Inter({ subsets: ["latin"] })

export const metadata = {
  title: "draft.src.main.java.edu.brown.cs.termproject.draft.Draft - Curated Secondhand Fashion",
  description: "Collect secondhand fashion finds from across the web into curated style Drafts",
}

const PUBLISHABLE_KEY = process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY;


if (!PUBLISHABLE_KEY) {
  throw new Error("Missing Publishable Key");
}


export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <ClerkProvider publishableKey={PUBLISHABLE_KEY} afterSignOutUrl="/">

    <html lang="en">
      <body className={`${inter.className} bg-white text-black`}>
        <Header />
        <main className="min-h-screen">{children}</main>
      </body>
    </html>
    </ClerkProvider>
  );
}
