import "./globals.css"
import { Inter } from "next/font/google"
import { ReactNode } from "react"
import Header from "@/components/header"

const inter = Inter({ subsets: ["latin"] })

export const metadata = {
  title: "Draft - Curated Secondhand Fashion",
  description: "Collect secondhand fashion finds from across the web into curated style Drafts",
}

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body className={`${inter.className} bg-white text-black`}>
      <Header />
      <main className="min-h-screen">{children}</main>
      </body>
    </html>
  )
}
