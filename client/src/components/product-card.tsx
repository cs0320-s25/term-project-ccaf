// "use client"

// import Image from "next/image"
// import Link from "next/link"

// interface Product {
//   id: string
//   title: string
//   price: number
//   source: string
//   image: string
// }

// interface ProductCardProps {
//   product: Product
// }

// export function ProductCard({ product }: ProductCardProps) {
//   return (
//     <Link href={`/product/${product.id}`} className="block border rounded-lg overflow-hidden group">
//       <div className="relative aspect-square bg-gray-100 rounded-md overflow-hidden">
//         <Image
//           src={product.image || "/placeholder.svg"}
//           alt={product.title}
//           fill
//           className="object-cover transition-transform group-hover:scale-105"
//         />
//       </div>
//       <div className="p-4">
//         <p className="text-sm text-muted-foreground">{product.source}</p>
//         <h3 className="font-semibold">{product.title}</h3>
//         <p className="text-sm font-medium">${product.price.toFixed(2)}</p>
//       </div>
//     </Link>
//   )
// }
"use client";

import Image from "next/image";
import Link from "next/link";
import { useState } from "react";

interface Product {
  id: string;
  title: string;
  price: number;
  sourceWebsite: string;
  imageUrl: string;
  url: string;
}

interface ProductCardProps {
  product: Product;
}

// export function ProductCard({ product }: ProductCardProps) {
//   const [saved, setSaved] = useState(false);

//   const saveToDraft = () => {
//     const drafts = JSON.parse(localStorage.getItem("drafts") || "[]");
//     const updated = [...drafts, product];
//     localStorage.setItem("drafts", JSON.stringify(updated));
//     setSaved(true);
//   };

//   return (
//     <div className="border rounded-lg overflow-hidden group">
//       <Link href={product.url} target="_blank" rel="noopener noreferrer">
//         <div className="relative aspect-square bg-gray-100 overflow-hidden">
//           <Image
//             src={product.imageUrl || "/placeholder.svg"}
//             alt={product.title}
//             fill
//             className="object-cover"
//           />
//         </div>
//       </Link>
//       <div className="p-4">
//         <p className="text-sm text-muted-foreground">{product.sourceWebsite}</p>
//         <h3 className="font-semibold">{product.title}</h3>
//         <p className="text-sm font-medium">${product.price.toFixed(2)}</p>
//         <button
//           onClick={saveToDraft}
//           className="mt-2 text-xs underline text-blue-500"
//         >
//           {saved ? "Saved!" : "Save to Draft"}
//         </button>
//       </div>
//     </div>
//   );
// }

export function ProductCard({ product }: ProductCardProps) {
  if (!product.url) {
    return <div className="text-red-500">Invalid product link</div>;
  }

  return (
    <div className="border rounded-lg overflow-hidden group">
      <Link href={product.url} target="_blank" rel="noopener noreferrer">
        <div className="relative aspect-square bg-gray-100 overflow-hidden">
          <Image
            src={product.imageUrl || "/placeholder.svg"}
            alt={product.title}
            fill
            className="object-cover"
          />
        </div>
      </Link>
      <div className="p-4">
        <p className="text-sm text-muted-foreground">{product.sourceWebsite}</p>
        <h3 className="font-semibold">{product.title}</h3>
        <p className="text-sm font-medium">${product.price.toFixed(2)}</p>
      </div>
    </div>
  );
}

