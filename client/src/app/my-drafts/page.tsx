import React from "react";
import "app/globals.css";


const MyDraftsPage = () => {
  return (
    <div className="container px-4 py-16 max-w-5xl mx-auto">
      <div className="text-center mb-12">
        <h1>your saved drafts</h1>
        <p className="body-text">
          here's your collections of drafts!
          <br />
          let's get inspired!
        </p>
      </div>

    </div>
  );
};

export default MyDraftsPage;
// "use client";

// import { useEffect, useState } from "react";
// import { ProductCard } from "@/components/product-card";

// export default function MyDrafts() {
//   const [drafts, setDrafts] = useState([]);
//   const [query, setQuery] = useState("");

//   useEffect(() => {
//     const stored = JSON.parse(localStorage.getItem("drafts") || "[]");
//     setDrafts(stored);
//   }, []);

//   const filtered = drafts.filter((piece: any) =>
//     piece.title.toLowerCase().includes(query.toLowerCase())
//   );

//   return (
//     <div className="container max-w-5xl mx-auto py-8">
//       <h2 className="text-xl font-semibold mb-4">My Draft</h2>
//       <input
//         type="text"
//         placeholder="Search within your draft..."
//         value={query}
//         onChange={(e) => setQuery(e.target.value)}
//         className="mb-6 w-full border rounded px-4 py-2"
//       />
//       <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
//         {filtered.map((product: any) => (
//           <ProductCard key={product.id} product={product} />
//         ))}
//       </div>
//     </div>
//   );
// }
