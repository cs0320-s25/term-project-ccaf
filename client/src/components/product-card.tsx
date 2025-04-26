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

export type { Product };

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

