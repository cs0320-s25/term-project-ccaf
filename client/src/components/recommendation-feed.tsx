import { useDrafts } from "@/app/my-drafts/useDrafts";
import { ProductCard, Piece } from "@/components/product-card"
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";

export function RecommendationFeed() {
  const { user } = useUser();
  const uid = user?.id;
  const { drafts } = useDrafts(uid);
  const [recommendations, setRecommendations] = useState<Piece[]>([]);

    useEffect(() => {
      if (uid) {
        fetch(`http://localhost:3232/recommend?uid=${uid}`)
          .then((response) => response.json())
          .then((data) => {
            if (data.recommendations) {
              setRecommendations(data.recommendations);
            }
          })
          .catch((error) => {
            console.error("Error fetching recommendations:", error);
          });
      }
    }, [uid]);

  return (
    <section>
      <h2 className="mb-6">recommended for you</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {recommendations.length > 0 ? (
          recommendations.map((piece) => (
            <ProductCard
              key={piece.id}
              piece={{
                id: piece.id,
                title: piece.title,
                price: piece.price,
                url: piece.url,
                sourceWebsite: piece.sourceWebsite,
                imageUrl: piece.imageUrl,
                size: piece.size,
                color: piece.color, 
                condition: piece.condition, 
                tags: piece.tags,
              }}
              onDraftPage={false}
            />
          ))
        ) : (
          <p>no recommendations available.</p>
        )}
      </div>
    </section>
  );
}