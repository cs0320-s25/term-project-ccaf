"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"

export default function Header() {
  const pathname = usePathname()

  return (
    <header className="border-b px-6 py-4">
      <div className="max-w-5xl mx-auto flex items-center justify-between">
        <Link href="/" className="text-2xl font-bold tracking-tight -mb-1">
          draft
        </Link>
        <nav className="flex items-center gap-6 text-sm">
          <Link
            href="/explore"
            className={pathname === "/explore" ? "font-semibold" : "text-muted-foreground"}
          >
            explore
          </Link>
          <Link
            href="/my-drafts"
            className={pathname === "/my-drafts" ? "font-semibold" : "text-muted-foreground"}
          >
            my drafts
          </Link>
        </nav>
      </div>
    </header>
  )
}
