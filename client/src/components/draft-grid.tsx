"use client";

import Link from "next/link";
import Image from "next/image";
import { addDraft, viewDrafts } from "../utils/api";
import { useUser } from "@clerk/clerk-react";
import { useEffect, useState } from "react";



type Draft = {
  id: string;
  name: string;
  count: number;
  pieces: any[];
  thumbnails: string[];
};

// mock data 
const drafts = [
  {
    id: "1",
    name: "wish list <3",
    count: 40,
    thumbnails: [
      "/placeholder.svg?height=200&width=200",
      "/placeholder.svg?height=100&width=100",
      "/placeholder.svg?height=100&width=100",
      "/placeholder.svg?height=100&width=100",
    ],
  },
  {
    id: "2",
    name: "style inspo",
    count: 62,
    thumbnails: [
      "/placeholder.svg?height=200&width=200",
      "/placeholder.svg?height=100&width=100",
      "/placeholder.svg?height=100&width=100",
      "/placeholder.svg?height=100&width=100",
    ],
  },
  {
    id: "3",
    name: "hairstyles",
    count: 10,
    thumbnails: [
      "/placeholder.svg?height=200&width=200",
      "/placeholder.svg?height=100&width=100",
      "/placeholder.svg?height=100&width=100",
      "/placeholder.svg?height=100&width=100",
    ],
  },
];

export function DraftGrid() {
  const { user } = useUser();
  const uid = user?.id;
  const [drafts, setDrafts] = useState<Draft[]>([]);
  const [name, setName] = useState("");

  useEffect(() => {
    if (!uid) return;
  
    viewDrafts(uid)
      .then((res) => {
        console.log("fetching drafts");
  
        if (res.response_type === "success") {
          if (!Array.isArray(res.drafts) || res.drafts.length === 0) {
            // No drafts found, reset state and exit early
            setDrafts([]);
            return;
          }
  
          console.log(res.drafts);
  
          const drafts = res.drafts.map((draft: any) => {
            const pieces = draft.pieces || [];
            return {
              id: draft.id,
              name: draft.name,
              count: pieces.length,
              pieces: draft.pieces,
              thumbnails: draft.thumbnails || [],
            };
          });
  
          setDrafts(drafts);
        } else {
          console.error("Failed to view drafts:", res.error);
        }
      })
      .catch((err) => {
        console.error("Error viewing drafts:", err);
      });
  
  }, [uid]);

  const handleAddDraft = () => {

    if (!name.trim()) return;
    if (!uid) return;

    console.log(uid);
    addDraft(uid, name).then((res) => {
      if (res.response_type === "success") {
        const createdDraft = res.draft;
        console.log("Draft added:", createdDraft);
        setDrafts((prev) => [...prev, createdDraft]);
      } else {
        console.error("Failed to add draft:", res.error);
      }
    })
    .catch((err) => {
      console.error("Error adding draft:", err);
    });
  };

  return (
    <div>
      {/* Input + Button */}
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
  
      {/* Conditional rendering for drafts */}
      {drafts.length === 0 ? (
        <p className="text-muted-foreground text-center mt-8">
          no drafts yet — start saving items!
        </p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-6">
          {drafts.map((draft) => {
            const thumbnails =
              draft.thumbnails?.length > 0
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
    </div>
  );
}  