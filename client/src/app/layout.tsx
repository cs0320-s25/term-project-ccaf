import "./globals.css";
import { Inter } from "next/font/google";
import { Providers } from "./providers";
import Header from "@/components/header";

const inter = Inter({ subsets: ["latin"] });

export const metadata = {
  title: "Draft - Curated Secondhand Fashion",
  description:
    "Collect secondhand fashion finds from across the web into curated style Drafts",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={`${inter.className} bg-white text-black`}>
        <Providers>
          <Header />
          <main className="min-h-screen pt-16">{children}</main>
        </Providers>
      </body>
    </html>
  );
}
