import Image from "next/image";
import Link from "next/link";

export interface Product {
  id: string;
  title: string;
  price: number;
  sourceWebsite: string;
  imageUrl: string;
}

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  return (
    <Link href={`/product/${product.id}`} className="block border rounded-lg overflow-hidden group">
      <div className="relative aspect-square bg-gray-100 overflow-hidden">
        <Image
          src={product.imageUrl || "/placeholder.svg"}
          alt={product.title}
          fill
          className="object-cover transition-transform group-hover:scale-105"
        />
      </div>
      <div className="p-4">
        <p className="text-sm text-muted-foreground">{product.sourceWebsite}</p>
        <h3 className="font-semibold">{product.title}</h3>
        <p className="text-sm font-medium">${product.price.toFixed(2)}</p>
      </div>
    </Link>
  );
}
