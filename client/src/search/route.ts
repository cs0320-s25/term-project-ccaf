import { NextResponse } from "next/server";
import mockData from "@/data/mockListings.json";

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const query = searchParams.get("q")?.toLowerCase() || "";

  const filtered = mockData.filter(
    (item) =>
      item.title.toLowerCase().includes(query) ||
      item.tags?.some((tag) => tag.toLowerCase().includes(query))
  );

  return NextResponse.json(filtered);
}
