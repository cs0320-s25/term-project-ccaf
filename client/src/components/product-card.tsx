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
  const [saved, setSaved] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [draftName, setDraftName] = useState("");
  const [pendingSaveDraftName, setPendingSaveDraftName] = useState<string | null>(null);
  const { createDraft, addToDraftWrapper, drafts } = useDrafts(uid);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);


  // validate and sanitize the image URL
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
      : "/placeholder.svg"; // use a local placeholder image

  const logClick = async () => {
    if (!uid || !piece?.id) return;
    try {
      await fetch("/log-click", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId: uid, pieceId: piece.id }),
      });
    } catch (err) {
      console.error("Failed to log click:", err);
    }
  };

  const handleProductClick = () => {
    sessionStorage.setItem("temp_piece", JSON.stringify(piece));
    logClick(); // log the click on the backend
  };

  const toggleModal = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setShowModal(true);
  };

  const handleSave = (draftId: string, piece: Piece | null) => {
    if (!uid?.trim() || !piece) return;
    addToDraftWrapper(uid, draftId, piece);
    setSaved(true);
    setShowModal(false);
    setShowConfirmation(true);
    setTimeout(() => {
    setShowConfirmation(false);
  }, 2500);
  };

  const handleNewDraft = () => {
    const trimmedName = draftName.trim();
    if (!trimmedName) return;
  
    createDraft(trimmedName); // triggers the state update async
    setPendingSaveDraftName(trimmedName); // track the draft we're waiting for
    setDraftName("");
  };

  useEffect(() => {
    if (!pendingSaveDraftName) return;
  
    const newDraft = drafts.find((d) => d.name === pendingSaveDraftName);
    if (newDraft) {
      handleSave(newDraft.id, piece); // piece must be in scope or passed in
      setPendingSaveDraftName(null); // clear tracking
    }
  }, [drafts, pendingSaveDraftName]);



  return (
    <div className="border rounded-lg overflow-hidden group relative">
      <Link
        href={{ pathname: `/product/${piece.id}` }}
        className="block"
        onClick={handleProductClick}
      >
        <div className="relative aspect-square bg-gray-100 overflow-hidden">
          <Image
            src={piece.imageUrl && piece.imageUrl.trim() !== "" ? piece.imageUrl : "/placeholder.svg"}
            alt={piece.title}
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

          {onDraftPage && (
            <button
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                setShowDeleteConfirm(true);
              }}
              className="absolute top-2 left-2 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
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

      {showConfirmation && (
      <div className="absolute bottom-2 left-2 bg-white text-sm text-green-600 border border-green-300 px-3 py-1 rounded shadow transition">
        saved to draft!
      </div>
    )}

      {/* save modal */}
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
                    onClick={() => handleSave(draft.id, piece)}
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

      {showDeleteConfirm && (
      <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
        <div className="bg-white p-6 rounded-lg shadow-md w-80">
          <h2 className="modal-header">are you sure?</h2>
          <p className="modal-body">
            once you delete a piece, you can't undo!
          </p>
          <div className="flex justify-center space-x-3">
            <button
              className="px-4 py-2 text-sm bg-gray-100 rounded hover:bg-gray-200"
              onClick={() => setShowDeleteConfirm(false)}
            >
              no
            </button>
            <button
              className="px-4 py-2 text-sm bg-red-500 text-white rounded hover:bg-red-600 flex justify-center"
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
