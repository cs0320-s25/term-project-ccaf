"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { ProductCard, Piece } from "@/components/product-card";
import { SearchBar } from "@/components/search-bar";
import { ChevronDown } from "lucide-react";

export default function SearchPage() {
  const searchParams = useSearchParams();
  const query = searchParams.get("q") || "";

  const [results, setResults] = useState<Piece[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [sizeFilter, setSizeFilter] = useState("all");
  const [colorFilter, setColorFilter] = useState("all");
  const [conditionFilter, setConditionFilter] = useState("all");

  useEffect(() => {
    if (!query) return;

    setLoading(true);
    setError(null);

    fetch(`http://localhost:3232/search?q=${encodeURIComponent(query)}`)
      .then((res) => res.json())
      .then((data) => {
        if (Array.isArray(data.matches)) {
          setResults(data.matches);
        } else {
          setResults([]);
          setError("Unexpected server response.");
        }
      })
      .catch(() => setError("Failed to load results."))
      .finally(() => setLoading(false));
  }, [query]);

  const filteredResults = results.filter((item) => {
    const sizeMatch = sizeFilter === "all" || item.size?.toLowerCase() === sizeFilter;
    const colorMatch = colorFilter === "all" || item.color?.toLowerCase() === colorFilter;
    const conditionMatch = conditionFilter === "all" || item.condition?.toLowerCase() === conditionFilter;
    return sizeMatch && colorMatch && conditionMatch;
  });

  return (
    <div className="container py-8 max-w-6xl mx-auto">
      <h2 className="text-lg font-semibold mb-4">search results for: "{query}"</h2>

      <div className="mb-6">
        <div className="w-[40rem] ml-0">
          <SearchBar />
        </div>
      </div>

      {/* filter menus */}
      <div className="flex gap-4 mb-6">
        <select
          value={sizeFilter}
          onChange={(e) => setSizeFilter(e.target.value)}
          className="btn-outline-rounded"
        >
          <option value="all">All Sizes</option>
          <option value="xs">XS</option>
          <option value="s">S</option>
          <option value="m">M</option>
          <option value="l">L</option>
          <option value="xl">XL</option>
        </select>

        <select
          value={colorFilter}
          onChange={(e) => setColorFilter(e.target.value)}
          className="btn-outline-rounded"
        >
          <option value="all">All Colors</option>
          <option value="black">Black</option>
          <option value="blue">Blue</option>
          <option value="white">White</option>
          <option value="red">Red</option>
          <option value="green">Green</option>
          <option value="pink">Pink</option>
        </select>

        <select
          value={conditionFilter}
          onChange={(e) => setConditionFilter(e.target.value)}
          className="btn-outline-rounded"
        >
          <option value="all">All Conditions</option>
          <option value="new with tags">New with Tags</option>
          <option value="like new">Like New</option>
          <option value="good">Good</option>
          <option value="used">Used</option>
        </select>
      </div>

      {loading && <p className="text-gray-500">Loading...</p>}
      {error && <p className="text-red-500 font-medium mb-4">{error}</p>}
      {!loading && filteredResults.length === 0 && !error && (
        <p className="text-muted-foreground">no results found</p>
      )}

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {filteredResults.map((product) => (
          <ProductCard key={product.id} piece={product} />
        ))}
      </div>
    </div>
  );
}
