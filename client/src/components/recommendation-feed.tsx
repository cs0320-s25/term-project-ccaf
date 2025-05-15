import { useDrafts } from "@/app/my-drafts/useDrafts";
import { ProductCard, Piece } from "@/components/product-card";
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";

export function RecommendationFeed() {
  const { user } = useUser();
  const uid = user?.id;
  const { drafts } = useDrafts(uid);

  const [recommendations, setRecommendations] = useState<Piece[]>([]);
  const [loading, setLoading] = useState(true); // <-- track loading state

  useEffect(() => {
    if (uid) {
      setLoading(true); // start loading
      fetch(`http://localhost:3232/recommend?uid=${uid}`)
        .then((response) => response.json())
        .then((data) => {
          if (data.recommendations) {
            setRecommendations(data.recommendations);
          }
        })
        .catch((error) => {
          console.error("Error fetching recommendations:", error);
        })
        .finally(() => {
          setLoading(false); // end loading
        });
    }
  }, [uid]);

  return (
    <section>
      <h2 className="mb-6">recommended for you</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {loading ? (
          <p>Loading...</p>
        ) : recommendations.length > 0 ? (
          recommendations.map((piece) => (
            <ProductCard
              aria-label={"Piece: " + piece.title}
              key={piece.id}
              piece={piece}
              onDraftPage={false}
            />
          ))
        ) : (
          <p>No recommendations available. Try searching and saving some times!</p>
        )}
      </div>
    </section>
  );
}
