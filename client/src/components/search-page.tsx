// "use client";
// import { useSearchParams } from "next/navigation";
// import { useEffect, useState } from "react";
// import { ProductCard } from "@/components/product-card";

// export default function SearchPage() {
//   const searchParams = useSearchParams();
//   const query = searchParams.get("q") || "";
//   const [results, setPieces] = useState([]); // state to hold the pieces
//   const [loading, setLoading] = useState(true); // state for the loading indicator

//   useEffect(() => {
//     async function fetchResults() {
//       setLoading(true);
//       try {
//         const res = await fetch(`http://localhost:3232/search?query=${encodeURIComponent(query)}`);
//         const data = await res.json();
//         setPieces(data); // add the data to the state
//       } catch (err) {
//         console.error("Error fetching search results:", err);
//         setPieces([]);
//       } finally {
//         setLoading(false); // stop showing "Loading..." once the data is fetched
//       }
//     }

//     if (query) {
//       fetchResults(); // now call the fetch function
//     }
//   }, [query]);

//   if (loading) {
//     return <p>Loading...</p>;  // display a loading message while data is being fetched
//   }

//   return (
//     <div className="container">
//       <h1 className="text-2xl mb-4">Results for "{query}"</h1>
//       {loading ? (
//         <p>Loading...</p>
//       ) : results.length === 0 ? (
//         <p>No results found.</p>
//       ) : (
//         <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
//           {results.map((product, idx) => (
//             <ProductCard key={idx} product={product} />
//           ))}
//         </div>
//       )}
//     </div>
//   );
// }
