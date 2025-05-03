"use client";

import Link from "next/link";
import Image from "next/image";
import { useUser } from "@clerk/clerk-react";
import { useState } from "react";
import { useDrafts } from "../app/my-drafts/useDrafts";

export function DraftGrid() {
  const { user } = useUser();
  const uid = user?.id;
  const [name, setName] = useState("");
  const { drafts, loading, error, createDraft } = useDrafts(uid);

  const handleAddDraft = () => {
    createDraft(name);
    setName("");
  };

  return (
    <div>
      <div className="flex items-center gap-2 mt-4">
        <input
          className="border p-2 rounded"
          type="text"
          value={name}
          placeholder="Enter draft name"
          onChange={(e) => setName(e.target.value)}
        />
        <button onClick={handleAddDraft} className="bg-black text-white px-4 py-2 rounded">
          + Add Draft
        </button>
      </div>

      {loading ? (
        <p className="mt-8 text-center text-muted-foreground">Loading drafts...</p>
      ) : drafts.length === 0 ? (
        <p className="text-muted-foreground text-center mt-8">
          no drafts yet — start saving items!
        </p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-6">
          {drafts.map((draft) => {
            const thumbnails = draft.thumbnails?.length > 0
              ? draft.thumbnails
              : ["/placeholder.svg", "/placeholder.svg", "/placeholder.svg"];

            return (
              <Link key={draft.id} href={`/draft/${draft.id}`} className="block group">
                <div className="grid grid-cols-2 gap-1 rounded-lg overflow-hidden border">
                  <div className="row-span-2 col-span-1">
                    <div className="relative aspect-square">
                      <Image src={thumbnails[0]} alt="" fill className="object-cover" />
                    </div>
                  </div>
                  <div className="col-span-1">
                    <div className="relative aspect-square">
                      <Image src={thumbnails[1]} alt="" fill className="object-cover" />
                    </div>
                  </div>
                  <div className="col-span-1">
                    <div className="relative aspect-square">
                      <Image src={thumbnails[2]} alt="" fill className="object-cover" />
                    </div>
                  </div>
                </div>
                <div className="mt-3">
                  <h3 className="font-medium tracking-tight">{draft.name}</h3>
                  <p className="text-sm text-muted-foreground">{draft.count ?? 0} pieces</p>
                </div>
              </Link>
            );
          })}
        </div>
      )}
      {error && <p className="text-red-500 mt-4 text-center">{error}</p>}
    </div>
  );
}