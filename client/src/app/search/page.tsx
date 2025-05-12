"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { ProductCard } from "@/components/product-card";
import type { Piece } from "@/components/product-card";
import { SearchBar } from "@/components/search-bar";
import { useUser } from "@clerk/clerk-react";
import { useDrafts } from "../my-drafts/useDrafts";


export default function SearchPage() {
  const searchParams = useSearchParams();
  const query = searchParams.get("q") || "";

  const [results, setResults] = useState<Piece[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const { user } = useUser();
  const uid = user?.id;
  const { drafts } = useDrafts(uid);


  useEffect(() => {
    if (!query) return;

    setLoading(true);
    setError(null);

    fetch(`http://localhost:3232/search?q=${encodeURIComponent(query)}`)
      .then(async (res) => {
        // Check if response is JSON
        const contentType = res.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
          // If not JSON, get the text content for debugging
          const text = await res.text();
          console.error("Server returned non-JSON response:", text);
          throw new Error("Server returned non-JSON response");
        }

        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }

        return res.json();
      })
      .then((data) => {
        console.log("SERVER RESPONSE:", data);
        if (data.error) {
          setError(data.error);
          setResults([]);
        } else if (Array.isArray(data.matches)) {
          setResults(data.matches);
        } else {
          setError("Unexpected server response format");
          setResults([]);
        }
      })
      .catch((err) => {
        console.error("Fetch error:", err);
        setError(`Failed to load results: ${err.message}`);
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
        {results.map((product, index) => {
          // Create a unique key by combining id, platform, and index
          const uniqueKey = `${product.id}-${product.sourceWebsite}-${index}`;
          return (
            <ProductCard key={uniqueKey} product={product} drafts={drafts} />
          );
        })}
      </div>
    </div>
  );
}
