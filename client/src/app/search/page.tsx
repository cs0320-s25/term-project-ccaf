"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { ProductCard } from "@/components/product-card";
import type { Product } from "@/components/product-card";
import { SearchBar } from "@/components/search-bar";

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

export default function SearchPage() {
  const searchParams = useSearchParams();
  const query = searchParams.get("q") || "";

  const [results, setResults] = useState<Product[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    if (!query) return;

    setLoading(true);
    setError(null);

    fetch(`http://localhost:3232/search?q=${encodeURIComponent(query)}`)
      .then((res) => res.json())
      .then((data) => {
        console.log("SERVER RESPONSE:", data);
        if (Array.isArray(data.matches)) {
          setResults(data.matches);
        } else {
          setResults([]);
          setError("Unexpected server response.");
        }
      })
      .catch((err) => {
        console.error("Fetch error:", err);
        setError("Failed to load results.");
        setResults([]);
      })
      .finally(() => setLoading(false));
  }, [query]);

  return (
    <div className="container py-8 max-w-5xl mx-auto">
      <h2 className="text-lg font-semibold mb-4">
        Search results for: "{query}"
      </h2>

      <div className="mb-6 flex">
        <div className="w-[40rem]">
          <SearchBar />
        </div>
      </div>

      {loading && <p className="text-gray-500">Loading...</p>}

      {error && <p className="text-red-500 font-medium mb-4">{error}</p>}

      {!loading && results.length === 0 && !error && (
        <p className="text-gray-500">No results found.</p>
      )}

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {results.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}
