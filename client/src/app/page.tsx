import { SearchBar } from "@/components/search-bar"
import { RecommendationFeed } from "@/components/recommendation-feed"
import "./globals.css";


export default function Home() {
  return (
    <div className="container px-4 py-16 max-w-5xl mx-auto">
      <div className="text-center mb-12">
        <h1>welcome to Draft</h1>
        <p className="body-text">
          Draft lets you collect secondhand fashion finds from across the web
          <br />
          into curated style Drafts—your personal moodboards :-)
        </p>
      </div>

      <div className="mb-12">
        <SearchBar />
      </div>

      <RecommendationFeed />
    </div>
  )
}