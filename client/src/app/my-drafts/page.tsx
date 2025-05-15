"use client";
import { DraftGrid } from "@/components/draft-grid";

export default function MyDraftsPage() {
  return (
    <div className="container px-4 py-8 max-w-5xl mx-auto">
      <div className="text-center">
        <h3 className="text-4xl font-semibold tracking-tight mb-8">my gallery</h3>
        <p className="text-[1.25rem] tracking-[-0.03em] text-muted-foreground mt-2">
            welcome to your gallery! is it time to start a new draft?
          </p>
          <br />
      </div>
      <DraftGrid />
    </div>
  );
}
