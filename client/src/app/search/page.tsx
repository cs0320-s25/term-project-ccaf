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

  // ...existing code...

  useEffect(() => {
    // Ignore empty queries
    if (!query) {
      setResults([]);
      return;
    }

    let isSubscribed = true;
    const controller = new AbortController();


    const fetchResults = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await fetch(
          `http://localhost:3232/search?q=${encodeURIComponent(query)}`,
          {
            signal: controller.signal,
            headers: {
              Accept: "application/json",
            },
          }
        );

        // Check if component is still mounted
        if (!isSubscribed) return;

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(
            errorData.error || `HTTP error! status: ${response.status}`
          );
//     fetch(`http://localhost:3232/search?q=${encodeURIComponent(query)}`)
//       .then(async (res) => {
//         // check if response is JSON
//         const contentType = res.headers.get("content-type");
//         if (!contentType || !contentType.includes("application/json")) {
//           const text = await res.text();
//           console.error("Server returned non-JSON response:", text);
//           throw new Error("Server returned non-JSON response");
// >>>>>>> 12d9e40cfb0983eb4a53825e8ed5a3486c95aa48
        }

        const data = await response.json();

        // Check if component is still mounted
        if (!isSubscribed) return;

        if (data.error) {
          setError(data.error);
          setResults([]);
        } else if (Array.isArray(data.matches)) {
          setResults(data.matches);
        } else {
          console.error("Unexpected data structure:", data);
          setError("Unexpected server response format");
          setResults([]);
        }
      } catch (err) {
        // Only set error if the component is still mounted and it's not an abort error
        if (isSubscribed && err instanceof Error && err.name !== "AbortError") {
          console.error("Search error details:", err);
          setError(`Search failed: ${err.message}`);
          setResults([]);
        }
      } finally {
        if (isSubscribed) {
          setLoading(false);
        }
      }
    };

    fetchResults();

    // Cleanup function
    return () => {
      isSubscribed = false;
      controller.abort();
    };
  }, [query]);

  // ...existing code...

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
          return <ProductCard key={product.id} piece={product} />;
        })}
      </div>
    </div>
  );
}
