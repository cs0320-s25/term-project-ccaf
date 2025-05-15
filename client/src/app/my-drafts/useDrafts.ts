import { useCallback, useEffect, useState } from "react";
import { addDraft, viewDrafts, addToDraft, removeDraft } from "../../utils/api";
import { Piece } from "@/components/product-card";

type Draft = {
  id: string;
  name: string;
  count: number;
  pieces: any[];
  thumbnails: string[];
};

export function useDrafts(uid: string | undefined) {
  const [drafts, setDrafts] = useState<Draft[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchDrafts = useCallback(() => {
    if (!uid) return;

    setLoading(true);
    viewDrafts(uid)
      .then((res) => {
        if (res.response_type === "success") {
          const parsed = (res.drafts || []).map((draft: any) => ({
            id: draft.id,
            name: draft.name,
            count: (draft.pieces || []).length,
            pieces: draft.pieces || [],
            thumbnails: draft.thumbnails || [],
          }));
          setDrafts(parsed);
        } else {
          setDrafts([]);
          setError(res.error);
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [uid]);

  useEffect(() => {
    fetchDrafts();
  }, [fetchDrafts]);

  const createDraft = (name: string): Promise<void> => {
    if (!uid || !name.trim()) {
      return Promise.reject(new Error("Invalid UID or name"));
    }

    return addDraft(uid, name).then((res) => {
      if (res.response_type === "success") {
        const draft = res.draft;
        const newDraft = {
          id: draft.id,
          name: draft.name,
          count: (draft.pieces || []).length,
          pieces: draft.pieces || [],
          thumbnails: draft.thumbnails || [],
        };
        setDrafts((prev) => [...prev, newDraft]);
      } else {
        throw new Error(res.error);
      }
    });
  };


  const addToDraftWrapper = (uid: string, draftId: string,  piece: Piece) => {
    if (!uid || !draftId.trim() || !piece.id.trim() || !piece.title.trim() || !piece.price || !piece.sourceWebsite.trim() || !piece.url.trim() || !piece.size.trim() || !piece.color.trim() || !piece.condition.trim() || !piece.imageUrl.trim() || !piece.tags) return;

    addToDraft(uid, draftId, piece.id, piece.title, piece.price, piece.sourceWebsite, piece.url, piece.size, piece.color, piece.condition, piece.imageUrl, piece.tags)
      .then((res) => {
        if (res.response_type === "success") {
          const draft = res.draft;
          const newDraft = {
            id: draft.id,
            name: draft.name,
            count: (draft.pieces || []).length,
            pieces: draft.pieces || [],
            thumbnails: draft.thumbnails || [],
          };
          setDrafts((prev) => [...prev, newDraft]);
        } else {
          throw new Error(res.error);
        }
      })
      .catch((err: any) => {
        setError(err.message);
      });
  };

  // const deleteDraft = (uid: string | undefined, draftId: string) => {
  //   if (!uid || !draftId.trim()) return;

  //   removeDraft(uid, draftId)
  //     .then((res) => {
  //       if (res.response_type === "success") {
  //         setDrafts((prev) => prev.filter((d) => d.id !== draftId));
  //       } else {
  //         setError(res.error);
  //       }
  //     })
  //     .catch((err) => {
  //       setError(err.message);
  //     });
  // };
    

  return { drafts, loading, error, setError, createDraft, fetchDrafts, addToDraftWrapper };
}