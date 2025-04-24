import { ProductCard } from "@/components/product-card"

export function RecommendationFeed() {
  return (
    <section>
      <h2 className="text-2xl font-bold mb-4">Recommended for you</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <ProductCard
            key={i}
            product={{
              id: `${i}`,
              title: "Placeholder",
              price: 99,
              source: "ebay",
              image: "/placeholder.svg",
            }}
          />
        ))}
      </div>
    </section>
  )
}