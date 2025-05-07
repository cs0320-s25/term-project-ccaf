import { useDrafts } from "@/app/my-drafts/useDrafts";
import { ProductCard } from "@/components/product-card"
import { useUser } from "@clerk/clerk-react";

export function RecommendationFeed() {
  const { user } = useUser();
  const uid = user?.id;
  const { drafts } = useDrafts(uid);

  return (
    <section>
      <h2>recommended for you</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <ProductCard
            key={i}
            product={{
              id: `${i}`,
              title: "Placeholder",
              price: 99,
              url: "https://example.com",
              sourceWebsite: "ebay",
              imageUrl: "/placeholder.svg",
            }}
            drafts={drafts}
          />
        ))}
      </div>
    </section>
  )
}