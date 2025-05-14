"use client";

import Link from "next/link";
import Image from "next/image";
import { useUser } from "@clerk/clerk-react";
import { useDrafts } from "../app/my-drafts/useDrafts";
import { useState } from "react";
import { X } from "lucide-react"; 

export function DraftGrid() {
  const { user } = useUser();
  const uid = user?.id;
  const { drafts, loading, createDraft } = useDrafts(uid);

  const [showModal, setShowModal] = useState(false);
  const [draftName, setDraftName] = useState("");
  const [errMessage, setErrMessage] = useState("")

  const handleCreate = () => {
    if (draftName.trim()) {
      createDraft(draftName)
        .then(() => {
          setDraftName("");
          setShowModal(false);
        })
        .catch((err) => {
          setErrMessage(err.message);
        });
    }
  };

  const renderCreateButton = () => (
    <button
      onClick={() => setShowModal(true)}
      className="border rounded-lg aspect-square flex flex-col items-center justify-center hover:bg-gray-100 transition"
      aria-label="create draft button"
    >
      <span className="text-5xl font-bold text-gray-600">+</span>
      <span className="text-sm mt-2 text-muted-foreground">new draft</span>
    </button>
  );

  return (
    <div>
      {loading ? (
        <p className="mt-8 text-center text-muted-foreground">loading drafts...</p>
      ) : drafts.length === 0 ? (
        <div>
          <p className="text-muted-foreground text-center mt-8" aria-label="no draft prompt">
            no drafts yet — start saving items!
          </p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-6" aria-label="create draft button">
            {renderCreateButton()}
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-6" aria-label="collection of user's drafts">
          {drafts.map((draft) => {
            const placeholders = ["/placeholder.svg", "/placeholder.svg", "/placeholder.svg", "/placeholder.svg"];
            const thumbnails = draft.thumbnails && draft.thumbnails.length > 0
              ? [...draft.thumbnails, ...placeholders].slice(0, 4)
              : placeholders;

            return (
              <Link aria-label={"draft name: " + draft.name + ", and it has " + draft.pieces.length + " piece(s)."} key={draft.id} href={`/draft/${draft.id}`} className="block group">
                <div className="grid grid-cols-2 gap-1 rounded-lg overflow-hidden border aspect-square">
                  {thumbnails.map((src, i) => (
                    <div key={i} className="relative aspect-square">
                      <Image src={src} aria-label={`Thumbnail ${i}`} alt="" fill className="object-cover" />
                    </div>
                  ))}
                </div>
                <div className="mt-3">
                  <h3 className="font-medium tracking-tight" aria-label="draft name">{draft.name}</h3>
                  <p className="text-sm text-muted-foreground" aria-label="piece count">{draft.count ?? 0} pieces</p>
                </div>
              </Link>
            );
          })}
          {renderCreateButton()}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-80" aria-label="pop up to enter the name of your new draft">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-medium">create a new draft</h2>
              <button onClick={() => setShowModal(false)} aria-label="close modal">
                <X className="h-5 w-5" />
              </button>
            </div>

              {/* Show error message if draft name is a duplicate */}
              {errMessage && (
                <p className="text-sm text-red-600 mb-2" role="alert"aria-label="error message">{errMessage}</p>
              )}

            <input
              aria-label="input for"
              type="text"
              placeholder="new draft name"
              value={draftName}
              onChange={(e) => setDraftName(e.target.value)}
              className="w-full border px-2 py-1 rounded mb-4"
              onKeyDown={(e) => {
                if (e.key === "Enter") handleCreate();
              }}
            />

            <button
              onClick={handleCreate}
              className="btn-outline-rounded w-full"
              aria-label="create draft button"
            >
              create
            </button>
          </div>
        </div>
      )}
    </div>
  );
}