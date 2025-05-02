"use client";

import Link from "next/link";
import Image from "next/image";

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
  if (drafts.length === 0) {
    return <p className="text-muted-foreground text-center mt-8">no drafts yet — start saving items!</p>;
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
      {drafts.map((draft) => (
        <Link key={draft.id} href={`/draft/${draft.id}`} className="block group">
          <div className="grid grid-cols-2 gap-1 rounded-lg overflow-hidden border">
            <div className="row-span-2 col-span-1">
              <div className="relative aspect-square">
                <Image src={draft.thumbnails[0]} alt="" fill className="object-cover" />
              </div>
            </div>
            <div className="col-span-1">
              <div className="relative aspect-square">
                <Image src={draft.thumbnails[1]} alt="" fill className="object-cover" />
              </div>
            </div>
            <div className="col-span-1">
              <div className="relative aspect-square">
                <Image src={draft.thumbnails[2]} alt="" fill className="object-cover" />
              </div>
            </div>
          </div>
          <div className="mt-3">
            <h3 className="font-medium tracking-tight">{draft.name}</h3>
            <p className="text-sm text-muted-foreground">{draft.count} pieces</p>
          </div>
        </Link>
      ))}
    </div>
  );
}
