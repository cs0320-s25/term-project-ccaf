"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import { X, Star } from "lucide-react"; 
import { useUser } from "@clerk/clerk-react"; 
import { useDrafts } from "@/app/my-drafts/useDrafts";
import { Draft, Piece } from "@/components/product-card";


export default function ProductPage() {
  const { user } = useUser();
  const uid = user?.id;
  const { createDraft, addToDraftWrapper, drafts } = useDrafts(uid);
  const [piece, setPiece] = useState<Piece | null>(null); 
  const [showModal, setShowModal] = useState(false); 
  const [draftName, setDraftName] = useState(""); 
  const [saved, setSaved] = useState(false); 
  const params = useParams(); 
  const [pendingSaveDraftName, setPendingSaveDraftName] = useState<string | null>(null);
  const [errMessage, setErrMessage] = useState("");



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
  };

  const handleNewDraft = () => {
    const trimmedName = draftName.trim();
    if (!trimmedName) return;
  
    createDraft(draftName)
      .then(() => {
        setDraftName("");
        setShowModal(false);
        setPendingSaveDraftName(trimmedName); // Track the draft we're waiting for
        setDraftName("");
      })
      .catch((err) => {
        setErrMessage(err.message);
      });
  };

  useEffect(() => {
    if (!pendingSaveDraftName) return;
  
    const newDraft = drafts.find((d) => d.name === pendingSaveDraftName);
    if (newDraft) {
      handleSave(newDraft.id, piece); // piece must be in scope or passed in
      setPendingSaveDraftName(null); // clear tracking
    }
  }, [drafts, pendingSaveDraftName]);

  // Load piece from sessionStorage or fetch from API
  useEffect(() => {
    const storedPiece = sessionStorage.getItem("temp_piece");

    if (storedPiece) {
      const parsedPiece: Piece = JSON.parse(storedPiece);

      if (parsedPiece.id === params.id) {
        setPiece(parsedPiece);
        return;
      }
    }

  }, [params.id]);

  if (!piece) {
    return <div>Loading...</div>;
  }


  return (
    <div className="container px-4 py-8 max-w-5xl mx-auto">
      <div className="grid md:grid-cols-2 gap-8 mb-16">
        <div className="rounded-lg overflow-hidden border">
          <div className="relative aspect-square">
            <img
              src={piece.imageUrl || "/placeholder.svg"}
              alt={piece.title}
              className="object-cover w-full h-full"
              aria-label="product image"
            />
            {/* Star Button */}
            <button
              onClick={toggleModal}
              className="absolute top-2 right-2 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
              aria-label="save to draft"
            >
              <Star
                className={`h-5 w-5 ${
                  saved ? "fill-yellow-400 stroke-yellow-400" : "text-gray-400"
                }`}
                aria-label={saved ? "saved" : "not saved"}
              />
            </button>
          </div>
        </div>

        <div className="flex flex-col">
          <h1 className="text-2xl font-bold mb-2" aria-label="product title">{piece.title}</h1>
          <p className="text-xl font-bold mb-6" aria-label="product price">${piece.price}</p>

          <div className="mb-2" aria-label="source website">
            {piece.sourceWebsite.toLowerCase() === "ebay" ? (
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/4/48/EBay_logo.png"
                alt="ebay logo"
                className="w-[137.157px] h-[55px] object-contain"
                aria-label="ebay"
              />
            ) : (
              <p className="text-sm text-muted-foreground">{piece.sourceWebsite}</p>
            )}
          </div>

          <a
            href={piece.url}
            target="_blank"
            className="bg-gray-100 text-black text-center py-2 rounded-full font-medium"
            aria-label="visit product page"
          >
            visit product
          </a>

          <div className="mt-8" aria-label="product details">
            <h2 className="font-semibold mb-2">product details</h2>
            <p className="text-sm text-gray-600"><b>size:</b> {piece.size}</p>
            <p className="text-sm text-gray-600"><b>color:</b> {piece.color}</p>
            <p className="text-sm text-gray-600"><b>condition:</b> {piece.condition}</p>
          </div>
        </div>
      </div>

      {/* Modal for saving to drafts */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" aria-label="save to draft modal">
          <div className="bg-white p-6 rounded-lg w-80">
            <div className="flex justify-between items-center mb-4">
              <h2>save to draft</h2>
              <button onClick={() => setShowModal(false)} aria-label="close modal">
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="space-y-2" aria-label="existing drafts">
              {drafts.length > 0 ? (
                drafts.map((draft) => (
                  <button
                    key={draft.id}
                    onClick={() => handleSave(draft.id, piece)}
                    className="block w-full text-left p-2 border rounded hover:bg-gray-100"
                    aria-label={`save to draft ${draft.name}`}
                  >
                    {draft.name}
                  </button>
                ))
              ) : (
                <p className="text-sm text-muted-foreground">no drafts yet</p>
              )}
            </div>

            <div className="mt-4" aria-label="new draft input">
              {errMessage && (
                <p className="text-sm text-red-600 mb-2" role="alert">{errMessage}</p>
              )}
              <input
                type="text"
                placeholder="new draft name"
                value={draftName}
                onChange={(e) => setDraftName(e.target.value)}
                className="w-full border px-2 py-1 rounded mb-2"
                aria-label="new draft name"
              />
              <button
                className="btn-outline-rounded"
                onClick={handleNewDraft}
                aria-label="create and save new draft"
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
