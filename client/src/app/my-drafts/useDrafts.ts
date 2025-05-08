import { useCallback, useEffect, useState } from "react";
import { addDraft, viewDrafts, addToDraft } from "../../utils/api";

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

  const createDraft = (name: string) => {
    if (!uid || !name.trim()) return;

    addDraft(uid, name)
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

  const addToDraftWrapper = (uid: string, draftId: string, pieceId: string, title: string, price: number, sourceWebsite: string, url: string, size: string, color: string, condition: string, imageUrl: string, tags: string[]) => {
    if (!uid || !draftId.trim() || !pieceId.trim() || !title.trim() || !price || !sourceWebsite.trim() || !url.trim() || !size.trim() || !color.trim() || !condition.trim() || !imageUrl.trim() || !tags) return;

    addToDraft(uid, draftId, pieceId, title, price, sourceWebsite, url, size, color, condition, imageUrl, tags)
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
    

  return { drafts, loading, error, createDraft, fetchDrafts };
}