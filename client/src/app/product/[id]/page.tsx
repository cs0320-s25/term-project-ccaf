import Image from "next/image";
import Link from "next/link";

// interface ProductPageProps {
//   params: { id: string };
// }

// export default function ProductPage({ params }: ProductPageProps) {
export default function ProductPage({ params }: { params: { id: string } }) {
  const product = {
    id: params.id,
    title: "denim skirt high quality",
    price: 110,
    source: "ebay",
    sourceUrl: "https://www.ebay.com",
    image: "/placeholder.svg?height=600&width=600",
    details:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
  };

  return (
    <div className="container px-4 py-8 max-w-5xl mx-auto">
      <div className="grid md:grid-cols-2 gap-8 mb-16">
        <div className="rounded-lg overflow-hidden border">
          <div className="relative aspect-square">
            <Image
              src={product.image}
              alt={product.title}
              fill
              className="object-cover"
            />
          </div>
        </div>

        <div className="flex flex-col">
          <Image
            src="/ebay-logo.png" // Replace with actual logo if needed
            alt={product.source}
            width={120}
            height={40}
            className="mb-4"
          />
          <h1 className="text-2xl font-bold mb-2">{product.title}</h1>
          <p className="text-xl font-bold mb-6">${product.price}</p>

          <Link
            href={product.sourceUrl}
            target="_blank"
            className="bg-gray-100 text-black text-center py-2 rounded-full font-medium"
          >
            visit site
          </Link>

          <div className="mt-8">
            <h2 className="font-semibold mb-2">product details</h2>
            <p className="text-sm text-gray-600">{product.details}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
