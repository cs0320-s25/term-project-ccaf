"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import Link from "next/link";
import { Star, Trash, X } from "lucide-react";
import { useDrafts } from "@/app/my-drafts/useDrafts";
import { useUser } from "@clerk/clerk-react";

export interface Piece {
  id: string;
  title: string;
  price: number;
  sourceWebsite: string;
  url: string;
  size: string;
  color: string;
  condition: string;
  imageUrl: string;
  tags: string[];
}

export type Draft = {
  id: string;
  name: string;
  count: number;
  pieces: any[];
  thumbnails: string[];
};

interface ProductCardProps {
  piece: Piece;
  onDraftPage: boolean;
  onRemove?: (pieceId: string) => void;
}

export function ProductCard({ piece, onDraftPage, onRemove }: ProductCardProps) {
  const { user } = useUser();
  const uid = user?.id;

  // local ui state
  const [saved, setSaved] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [draftName, setDraftName] = useState("");
  const [pendingSaveDraftName, setPendingSaveDraftName] = useState<string | null>(null);
  const [errMessage, setErrMessage] = useState("");
  const [showSavedPopup, setShowSavedPopup] = useState(false);
  const [savedDraftName, setSavedDraftName] = useState("");
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const { createDraft, addToDraftWrapper, drafts } = useDrafts(uid);

  // helper to validate image urls
  const isValidUrl = (url: string) => {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  };

  const imageUrl =
    piece.imageUrl && isValidUrl(piece.imageUrl)
      ? piece.imageUrl
      : "/placeholder.svg";

  // log when a user clicks on a product
  const logClick = async () => {
    if (!uid || !piece?.id) return;
    try {
      await fetch("/log-click", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId: uid, pieceId: piece.id }),
      });
    } catch (err) {
      console.error("failed to log click:", err);
    }
  };

  const handleProductClick = () => {
    sessionStorage.setItem("temp_piece", JSON.stringify(piece));
    logClick();
  };

  // open save-to-draft modal
  const toggleModal = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setShowModal(true);
  };

  // handle saving to a draft
  const handleSave = (draftId: string, piece: Piece | null, draftName?: string) => {
    if (!uid?.trim() || !piece) return;
    addToDraftWrapper(uid, draftId, piece);
    setSaved(true);
    setShowModal(false);

    // show saved popup
    setSavedDraftName(draftName || "your draft");
    setShowSavedPopup(true);

    // auto-dismiss popup after 3.5 seconds
    setTimeout(() => {
      setShowSavedPopup(false);
    }, 3500);
  };

  // handle creating a new draft, then saving to it
  const handleNewDraft = () => {
    const trimmedName = draftName.trim();
    if (!trimmedName) return;

    createDraft(trimmedName)
      .then(() => {
        setDraftName("");
        setShowModal(false);
        setPendingSaveDraftName(trimmedName);
      })
      .catch((err) => {
        setErrMessage(err.message);
      });
  };

  // once the new draft is added, save the piece to it
  useEffect(() => {
    if (!pendingSaveDraftName) return;
    const newDraft = drafts.find((d) => d.name === pendingSaveDraftName);
    if (newDraft) {
      handleSave(newDraft.id, piece, newDraft.name);
      setPendingSaveDraftName(null);
    }
  }, [drafts, pendingSaveDraftName]);

  return (
    <div className="border rounded-lg overflow-hidden group relative">
      <Link
        href={{ pathname: `/product/${piece.id}` }}
        className="block"
        onClick={handleProductClick}
        aria-label={"piece titled: " + piece.title}
      >
        <div className="relative aspect-square bg-gray-100 overflow-hidden">
          <Image
            src={imageUrl}
            alt={piece.title}
            fill
            className="object-cover transition-transform group-hover:scale-105"
          />

          {/* save button in top-right */}
          <button
            onClick={toggleModal}
            className="absolute top-2 right-2 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
            aria-label="button to save piece to draft"
          >
            <Star
              className={`h-5 w-5 ${
                saved ? "fill-yellow-400 stroke-yellow-400" : "text-gray-400"
              }`}
            />
          </button>

          {/* delete button if we're on a draft page */}
          {onDraftPage && (
            <button
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                setShowDeleteConfirm(true);
              }}
              className="absolute top-2 left-2 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
              aria-label="button to remove piece from draft"
            >
              <Trash className="h-5 w-5 text-red-500" />
            </button>
          )}
        </div>

        <div className="p-4">
          <p className="text-sm text-muted-foreground">{piece.sourceWebsite}</p>
          <h3 className="font-semibold">{piece.title}</h3>
          <p className="text-sm font-medium">${piece.price.toFixed(2)}</p>
        </div>
      </Link>

      {/* popup modal after saving to draft */}
      {showSavedPopup && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="modal-popup">
            <h2 className="modal-header">🎉 piece saved!</h2>
            <p className="modal-body">
              you saved this piece to <strong>{savedDraftName}</strong>
            </p>
            <div className="flex justify-center">
              <button
                className="btn-outline-rounded"
                onClick={() => setShowSavedPopup(false)}
              >
                got it!
              </button>
            </div>
          </div>
        </div>
      )}

      {/* modal for selecting a draft or creating one */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-80">
            <div className="flex justify-between items-center mb-4">
              <h2 className="modal-header">save to a draft</h2>
              <button onClick={() => setShowModal(false)}>
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="space-y-2">
              {drafts.length > 0 ? (
                drafts.map((draft) => (
                  <button
                    key={draft.id}
                    onClick={() => handleSave(draft.id, piece, draft.name)}
                    className="block w-full text-left p-2 border rounded hover:bg-gray-100"
                  >
                    {draft.name}
                  </button>
                ))
              ) : (
                <p className="text-sm text-muted-foreground">no drafts yet :(</p>
              )}
            </div>

            <div className="mt-4">
              {errMessage && (
                <p className="text-sm text-red-600 mb-2" role="alert">
                  {errMessage}
                </p>
              )}
              <input
                type="text"
                placeholder="new draft name"
                value={draftName}
                onChange={(e) => setDraftName(e.target.value)}
                className="w-full border px-2 py-1 rounded mb-2"
              />
              <button className="btn-outline-rounded mt-2" onClick={handleNewDraft}>
                create & save
              </button>
            </div>
          </div>
        </div>
      )}

      {/* confirmation modal before deleting a piece */}
      {showDeleteConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-md w-80">
            <h2 className="modal-header">are you sure?</h2>
            <p className="modal-body">once you delete a piece, you can't undo!</p>
            <div className="flex justify-center space-x-3">
              <button
                className="px-4 py-2 text-sm bg-gray-100 rounded hover:bg-gray-200"
                onClick={() => setShowDeleteConfirm(false)}
              >
                no
              </button>
              <button
                className="px-4 py-2 text-sm bg-red-500 text-white rounded hover:bg-red-600"
                onClick={() => {
                  onRemove?.(piece.id);
                  setShowDeleteConfirm(false);
                }}
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