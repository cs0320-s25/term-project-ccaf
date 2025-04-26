"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { ProductCard } from "@/components/product-card";
import type { Product } from "@/components/product-card";


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
  const [results, setResults] = useState<Product[]>([])


  useEffect(() => {
<<<<<<< HEAD

  fetch(`http://localhost:3232/search?q=${encodeURIComponent(query)}`)
    .then(res => res.json())
    .then(setResults)
        .catch((error) => {
          console.error("Error fetching search results:", error);
          setResults([]); // in case of an error, clear the results
        });
=======
    const placeholderResult = {
      id: "1",
      title: "Denim Skirt High Quality",
      price: 110,
      sourceWebsite: "ebay",
      url: "https://www.ebay.com",
      imageUrl: "/placeholder.svg?height=300&width=300",
    };
  
    setResults([placeholderResult]);
  
    // TODO: uncomment when backend is ready
    // fetch(`/api/search?q=${encodeURIComponent(query)}`)
    //   .then((res) => res.json())
    //   .then(setResults);
>>>>>>> 66100af14b2b393322c085a9843051b7019d9417
  }, [query]);
  

  return (
    <div className="container py-8 max-w-5xl mx-auto">
      <h2>search results for: "{query}"</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {results.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}