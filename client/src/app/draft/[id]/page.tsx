"use client";

import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { ProductCard } from "@/components/product-card";
import { Piece } from "@/components/product-card";

interface Draft {
  id: string;
  name: string;
  items: Piece[];
}

export default function DraftPage() {
  const { id } = useParams();
  const [draft, setDraft] = useState<Draft | null>(null);

  useEffect(() => {
    const allDrafts = JSON.parse(localStorage.getItem("drafts") || "{}");
    const selected = allDrafts[id as string];
    if (selected) setDraft(selected);
  }, [id]);

  return (
    <div className="container px-4 py-8 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">
        {draft?.name || "Draft not found"}
      </h1>

      {draft?.items?.length ? (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {draft.items.map((product: Piece) => (
            <ProductCard key={product.id} product={product} drafts={[]} />
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
