"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { ProductCard, Piece } from "@/components/product-card";
import { viewPiecesInDraft, removeDraft, removeFromDraft } from "@/utils/api";
import { Trash2 } from "lucide-react";

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

  const [showModal, setShowModal] = useState(false);
  const [deleted, setDeleted] = useState(false);


  const router = useRouter();

  useEffect(() => {
    if (!uid || !id) return;
    console.log(uid);
    console.log(id);
    viewPiecesInDraft(uid, id as string)
      .then((res) => {
        if (res.status === "success") {
          if (res.message.includes("no pieces")) { 
            setDraft({
              id: id as string,
              name: res.draftData.name || `draft ${id}`,
              items: [],
            });
          } else {
            // res.pieces.forEach((piece: Piece) => {
            //   console.log("Image URL for piece", piece.id, ":", piece.imageUrl);
            // });
            
            setDraft({
              id: id as string,
              name: res.draftData.name || `draft ${id}`,
              items: res.pieces,
            });
          }
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


  const handleDelete = () => {
    if (!uid || !draft?.id) return;

    removeDraft(uid, draft.id)
      .then(() => {
        setShowModal(false);
        setDeleted(true);
      })
      .catch((err) => {
        console.error("Failed to delete draft:", err);
        setShowModal(false);
      });
  };

  const handlePieceRemove = (pieceId: string) => {
    if (!draft || !uid) return;
    removeFromDraft(uid, draft.id, pieceId)
      .then((res) => {
        if (res.status === "success") {
          setDraft((prevDraft) =>
            prevDraft
              ? {
                  ...prevDraft,
                  items: prevDraft.items.filter((item) => item.id !== pieceId),
                }
              : null
          );
        } else {
          console.error("Failed to remove piece:", res.error);
        }
      })
      .catch((err) => {
        console.error("Error removing piece:", err);
      });
  };

  useEffect(() => {
    if (deleted) {
      router.push("/my-drafts");
    }
  }, [deleted, router]);


  return (
    <div className="container px-4 py-8 max-w-6xl mx-auto">
      <div className="flex items-center justify-between mb-2">
        <h1 className="text-2xl font-bold" aria-label="draft title">
          {draft?.name || "Draft not found"}
        </h1>
        {draft && (
          <button
            onClick={() => setShowModal(true)}
            className="text-red-500 hover:text-red-700"
            aria-label="delete draft button"
          >
            <Trash2 className="w-5 h-5 text-red-500 hover:text-red-700" />
          </button>
        )}
      </div>

      <p className="text-sm text-muted-foreground mb-6" aria-label="piece count">
        {draft?.items?.length ?? 0} pieces
      </p>

      {draft?.items?.length ? (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {draft.items.map((product) => (
            <ProductCard aria-label={"Piece: " + product.title} key={product.id} piece={product} onDraftPage={true} onRemove={handlePieceRemove}/>
          ))}
        </div>
      ) : (
        <p className="text-muted-foreground mt-8" aria-label="no pieces saved prompt">
          no pieces in this draft yet. time to start saving!
        </p>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded shadow-lg" aria-label="pop up to delete a draft">
            <h2 className="text-lg font-semibold mb-4">confirm deletion</h2>
            <p className="mb-4">are you sure you want to delete this draft?</p>
            <div className="flex justify-end gap-4">
              <button
                className="px-4 py-2 bg-gray-200 rounded"
                onClick={() => setShowModal(false)}
                aria-label="button to cancel draft deletion"
              >
                cancel
              </button>
              <button
                className="px-4 py-2 bg-red-500 text-white rounded"
                onClick={handleDelete}
                aria-label="button to confirm draft deletion"
              >
                yes, delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}