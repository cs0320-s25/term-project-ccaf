"use client";

import { useState, useEffect } from "react";
import Image from "next/image";
import Link from "next/link";
import { Star, X } from "lucide-react";

export interface Product {
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

export function ProductCard({ product }: ProductCardProps) {
  const [saved, setSaved] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [draftName, setDraftName] = useState("");

  useEffect(() => {
    const drafts = JSON.parse(localStorage.getItem("drafts") || "{}");
    for (const draft of Object.values(drafts)) {
      if ((draft as any).pieces?.some((p: Product) => p.id === product.id)) {
        setSaved(true);
      }
    }
  }, [product.id]);

  const toggleModal = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setShowModal(true);
  };

  const handleSave = (draft: string) => {
    const drafts = JSON.parse(localStorage.getItem("drafts") || "{}");
    if (!drafts[draft]) {
      drafts[draft] = { pieces: [] };
    }
    drafts[draft].pieces.push(product);
    localStorage.setItem("drafts", JSON.stringify(drafts));
    setSaved(true);
    setShowModal(false);
  };

  const handleNewDraft = () => {
    if (!draftName.trim()) return;
    handleSave(draftName.trim());
    setDraftName("");
  };

  const drafts = Object.keys(
    JSON.parse(localStorage.getItem("drafts") || "{}")
  );

  return (
    <div className="border rounded-lg overflow-hidden group relative">
      <Link href={`/product/${product.id}`} className="block">
        <div className="relative aspect-square bg-gray-100 overflow-hidden">
          <Image
            src={product.imageUrl || "/placeholder.svg"}
            alt={product.title}
            fill
            className="object-cover transition-transform group-hover:scale-105"
          />

          {/* Star Button */}
          <button
            onClick={toggleModal}
            className="absolute top-2 right-2 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
          >
            <Star
              className={`h-5 w-5 ${
                saved ? "fill-yellow-400 stroke-yellow-400" : "text-gray-400"
              }`}
            />
          </button>
        </div>

        <div className="p-4">
          <p className="text-sm text-muted-foreground">{product.sourceWebsite}</p>
          <h3 className="font-semibold">{product.title}</h3>
          <p className="text-sm font-medium">${product.price.toFixed(2)}</p>
        </div>
      </Link>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-80">
            <div className="flex justify-between items-center mb-4">
              <h2>save to Draft</h2>
              <button onClick={() => setShowModal(false)}>
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="space-y-2">
              {drafts.length > 0 ? (
                drafts.map((draft) => (
                  <button
                    key={draft}
                    onClick={() => handleSave(draft)}
                    className="block w-full text-left p-2 border rounded hover:bg-gray-100"
                  >
                    {draft}
                  </button>
                ))
              ) : (
                <p className="text-sm text-muted-foreground">no drafts yet</p>
              )}
            </div>

            <div className="mt-4">
              <input
                type="text"
                placeholder="new draft name"
                value={draftName}
                onChange={(e) => setDraftName(e.target.value)}
                className="w-full border px-2 py-1 rounded mb-2"
              />
              <button className="btn-outline-rounded"
                onClick={handleNewDraft}
              >
                create & save
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
