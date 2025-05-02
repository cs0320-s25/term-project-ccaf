import { useEffect, useState } from "react";
import { addDraft, viewDrafts } from "../../utils/api";

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

  useEffect(() => {
    if (!uid) return;

    setLoading(true);
    viewDrafts(uid)
      .then((res) => {
        if (res.response_type === "success") {
          if (!Array.isArray(res.drafts) || res.drafts.length === 0) {
            setDrafts([]);
          } else {
            const parsed = res.drafts.map((draft: any) => {
              const pieces = draft.pieces || [];
              return {
                id: draft.id,
                name: draft.name,
                count: pieces.length,
                pieces: draft.pieces,
                thumbnails: draft.thumbnails || [],
              };
            });
            setDrafts(parsed);
          }
        } else {
          setError(res.error);
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [uid]);

  const createDraft = async (name: string) => {
    if (!uid || !name.trim()) return;

    try {
      const res = await addDraft(uid, name);
      if (res.response_type === "success") {
        const draft = res.draft;
        const count = (draft.pieces || []).length;
        const newDraft = {
          id: draft.id,
          name: draft.name,
          count,
          pieces: draft.pieces || [],
          thumbnails: draft.thumbnails || [],
        };
        setDrafts((prev) => [...prev, newDraft]);
      } else {
        throw new Error(res.error);
      }
    } catch (err: any) {
      setError(err.message);
    }
  };

  return { drafts, loading, error, createDraft };
}