"use client";

import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { ProductCard, Piece } from "@/components/product-card";
import { viewPiecesInDraft } from "@/utils/api";
import { useDrafts } from "@/app/my-drafts/useDrafts";

import { useUser } from "@clerk/clerk-react";

interface Draft {
  id: string;
  name: string;
  items: Piece[];
}

export default function DraftPage() {
  const { user } = useUser();
  const uid = user?.id;
  const { id } = useParams();
  const [draft, setDraft] = useState<Draft | null>(null);
  const { drafts } = useDrafts(uid);

  useEffect(() => {
    if (!uid || !id) return;
    console.log(uid);
    console.log(id);
    viewPiecesInDraft(uid, id as string)
      .then((res) => {
        if (res.status === "success") {
          res.pieces.forEach((piece: Piece) => {
            console.log("Image URL for piece", piece.id, ":", piece.imageUrl);
          });
          
          setDraft({
            id: id as string,
            name: res.draftData.name || `Draft ${id}`,
            items: res.pieces,
          });
        } else {
          setDraft(null);
          console.error(res.error);
        }
      })
      .catch((error) => {
        console.error("Error fetching pieces in draft:", error);
        setDraft(null);
      });
  }, [uid, id]);


  return (
    <div className="container px-4 py-8 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">
        {draft?.name || "Draft not found"}
      </h1>

      {draft?.items?.length ? (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {draft.items.map((product: Piece) => (
            <ProductCard key={product.id} product={product} drafts={drafts} />
          ))}
        </div>
      ) : (
        <p className="text-muted-foreground mt-8">
          no items in this draft yet.
        </p>
      )}
    </div>
  );
}