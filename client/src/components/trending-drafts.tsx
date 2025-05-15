"use client";

import Image from "next/image";
import Link from "next/link";

const trendingDrafts = [
  {
    id: "mock_trend_1",
    name: "nike vintage looks",
    thumbnails: [
      "https://media-photos.depop.com/b1/43410517/2643968328_9295e1fb86ba4762b56fd40f896b2e32/P0.jpg",
      "https://media-photos.depop.com/b1/27314354/2619379562_2c88245092534ab8aa9fceecf2d8a757/P0.jpg",
    ],
  },
  {
    id: "mock_trend_2",
    name: "summer dresses",
    thumbnails: [
      "https://media-photos.depop.com/b1/23291182/2646014590_4bf1b7c7b7a045678d1aa4ee3ff86d4d/P0.jpg",
      "https://media-photos.depop.com/b1/29282984/2647600876_a3ae58d8c82d4d96a4a422190084d8ba/P0.jpg",
    ],
  },
  {
    id: "mock_trend_3",
    name: "doc martens",
    thumbnails: [
      "https://media-photos.depop.com/b1/43231777/2650571045_df987fd40347422fb073efe6d9ca7b35/P0.jpg",
    ],
  }
];

export function TrendingDrafts() {
  return (
    <div className="mt-12">
      <h2 className="text-xl font-semibold mb-4">trending drafts</h2>
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {trendingDrafts.map((draft) => (
          <div key={draft.id} className="border rounded-lg overflow-hidden">
            <Link href={`/draft/${draft.id}`}>
              <div className="aspect-square bg-gray-100 relative">
                {draft.thumbnails[0] && (
                  <Image
                    src={draft.thumbnails[0]}
                    alt={draft.name}
                    fill
                    className="object-cover"
                  />
                )}
              </div>
              <div className="p-3">
                <h3 className="font-medium text-sm">{draft.name}</h3>
              </div>
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}
