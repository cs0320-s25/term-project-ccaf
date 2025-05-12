
"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import Link from "next/link";
import { Star, X } from "lucide-react";
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
}

export function ProductCard({ piece }: ProductCardProps) {
  const { user } = useUser();
  const uid = user?.id;
  const [saved, setSaved] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [draftName, setDraftName] = useState("");
  const [pendingSaveDraftName, setPendingSaveDraftName] = useState<string | null>(null);

  const { createDraft, addToDraftWrapper, drafts } = useDrafts(uid);

  // Validate and sanitize the image URL
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
      : "/placeholder.svg"; // Use a local placeholder image

  const handleProductClick = () => {
    sessionStorage.setItem("temp_piece", JSON.stringify(piece));
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
  };

  const handleNewDraft = () => {
    const trimmedName = draftName.trim();
    if (!trimmedName) return;
  
    createDraft(trimmedName); // This triggers the state update async
    setPendingSaveDraftName(trimmedName); // Track the draft we're waiting for
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

  // console.log("Image URL:", product.imageUrl);

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
        </div>

        <div className="p-4">
          <p className="text-sm text-muted-foreground">{piece.sourceWebsite}</p>
          <h3 className="font-semibold">{piece.title}</h3>
          <p className="text-sm font-medium">${piece.price.toFixed(2)}</p>
        </div>
      </Link>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-80">
            <div className="flex justify-between items-center mb-4">
              <h2>Save to Draft</h2>
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
                placeholder="New draft name"
                value={draftName}
                onChange={(e) => setDraftName(e.target.value)}
                className="w-full border px-2 py-1 rounded mb-2"
              />
              <button className="btn-outline-rounded" onClick={handleNewDraft}>
                create & save
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
// "use client";

// import { useState } from "react";
// import Image from "next/image";
// import Link from "next/link";
// import { Star, X } from "lucide-react";
// import { useDrafts } from "@/app/my-drafts/useDrafts";
// import { useUser } from "@clerk/clerk-react";

// export interface Piece {
//   id: string;
//   title: string;
//   price: number;
//   sourceWebsite: string;
//   url: string;
//   size: string;
//   color: string;
//   condition: string;
//   imageUrl: string;
//   tags: string[];
// }

// export type Draft = {
//   id: string;
//   name: string;
//   count: number;
//   pieces: any[];
//   thumbnails: string[];
// };

// interface ProductCardProps {
//   product: Piece;
//   drafts: Draft[];
// }

// export function ProductCard({ product, drafts }: ProductCardProps) {
//   const { user } = useUser();
//   const uid = user?.id;
//   const [saved, setSaved] = useState(false);
//   const [showModal, setShowModal] = useState(false);
//   const [draftName, setDraftName] = useState("");
//   const { createDraft, addToDraftWrapper } = useDrafts(uid);




//   const toggleModal = (e: React.MouseEvent) => {
//     e.preventDefault();
//     e.stopPropagation();
//     setShowModal(true);
//   };

//   const handleSave = (uid: string | undefined, draftId: string, piece: Piece) => {
//     if (!uid?.trim()) return;
//     addToDraftWrapper(uid, draftId, piece);
//     setSaved(true);
//     setShowModal(false);
//   };

//   const handleNewDraft = () => {
//     if (!draftName.trim()) return;
//     createDraft(draftName.trim());
//     setDraftName("");
//   };

//   const handleProductClick = () => {
//     // Store in sessionStorage first
//     sessionStorage.setItem("temp_piece", JSON.stringify(product));
//   };

//   console.log("Image URL:", product.imageUrl);

//   return (
//     <div className="border rounded-lg overflow-hidden group relative">
//       <Link
//         href={{ pathname: `/product/${product.id}` }}
//         className="block"
//         onClick={handleProductClick}  // Store product on click
//       >
//         <div className="relative aspect-square bg-gray-100 overflow-hidden">
//           <Image
//             src={product.imageUrl && product.imageUrl.trim() !== "" ? product.imageUrl : "/placeholder.svg"}
//             alt={product.title}
//             fill
//             className="object-cover transition-transform group-hover:scale-105"
//           />

//           {/* Star Button */}
//           <button
//             onClick={toggleModal}
//             className="absolute top-2 right-2 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
//           >
//             <Star
//               className={`h-5 w-5 ${
//                 saved ? "fill-yellow-400 stroke-yellow-400" : "text-gray-400"
//               }`}
//             />
//           </button>
//         </div>

//         <div className="p-4">
//           <p className="text-sm text-muted-foreground">{product.sourceWebsite}</p>
//           <h3 className="font-semibold">{product.title}</h3>
//           <p className="text-sm font-medium">${product.price.toFixed(2)}</p>
//         </div>
//       </Link>

//       {/* Modal */}
//       {showModal && (
//         <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
//           <div className="bg-white p-6 rounded-lg w-80">
//             <div className="flex justify-between items-center mb-4">
//               <h2>save to Draft</h2>
//               <button onClick={() => setShowModal(false)}>
//                 <X className="h-5 w-5" />
//               </button>
//             </div>

//             <div className="space-y-2">
//               {drafts.length > 0 ? (
//                 drafts.map((draft) => (
//                   <button
//                     key={draft.id}
//                     onClick={() => handleSave(uid, draft.id, product)}
//                     className="block w-full text-left p-2 border rounded hover:bg-gray-100"
//                   >
//                     {draft.name}
//                   </button>
//                 ))
//               ) : (
//                 <p className="text-sm text-muted-foreground">no drafts yet</p>
//               )}
//             </div>

//             <div className="mt-4">
//               <input
//                 type="text"
//                 placeholder="new draft name"
//                 value={draftName}
//                 onChange={(e) => setDraftName(e.target.value)}
//                 className="w-full border px-2 py-1 rounded mb-2"
//               />
//               <button className="btn-outline-rounded" onClick={handleNewDraft}>
//                 create & save
//               </button>
//             </div>
//           </div>
//         </div>
//       )}
//     </div>
//   );
// }
