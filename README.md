# [DRAFT] term-project-ccaf: A Secondhand Fashion Discovery Tool

**Draft** is a secondhand fashion discovery and curation web application. It allows users to explore resale listings across platforms like eBay, Depop, and Poshmark, and save their favorite items into visual "drafts" (moodboards) based on personal style.

This tool is designed for fashion-forward users who want to find unique pieces and build curated looks from the secondhand ecosystem — making sustainable fashion stylish and personalized.


## Features

- **Search Resale Listings**: Users can search for clothing items across eBay, Depop, and Poshmark.
- **Moodboard Drafts**: Users create "drafts" — visual collections of pieces that reflect a theme, vibe, or style.
- **Personalized Recommendations**: Pieces are recommended based on user interactions, saved drafts, and style palettes.
- **Authentication**: Integrated with Clerk for secure user login and identity management.
- **Cloud Storage**: Firebase Firestore stores all user data, drafts, and curated pieces.


## Architecture

Draft uses a **Model-View-Controller (MVC)** architecture:

- Frontend (React + Clerk)
- Controller (Spark Java HTTP Endpoints)
- Model (Firestore, Recommender Engine, API Fetchers)


- **Frontend**: Built with React, using Clerk for user authentication and Firebase for real-time data.
- **Backend**: Spark Java framework handles routing and JSON endpoints.
- **Database**: Firebase Firestore stores user data, drafts, and individual piece metadata.
- **External APIs**: eBay data is fetched using the Browse API. Depop and Poshmark are scraped via internal scrapers.
- **Recommendation System**: Suggests new pieces based on user behavior (e.g., saves, clicks, styles).


## 📂 Key Components

| Path | Description |
|------|-------------|
| `draft/API/eBayFetcher.java` | Fetches and parses eBay search results into `Piece` objects. |
| `draft/FirebaseUtilities.java` | Interacts with Firestore for all data operations (e.g. saving drafts, users, and pieces). |
| `draft/model/Piece.java` | Represents a single clothing item from any platform. |
| `draft/model/Draft.java` | Represents a collection of saved pieces for moodboarding. |
| `draft/RecommendationEngine.java` | Core logic for generating personalized item recommendations. |
| `draft/handlers/` | Spark route handlers that expose endpoints for search, save, fetch, and recommendation actions. |
| `draft/Exceptions/` | Custom exception classes for robust and clear error handling. |


## 🔧 Setup Instructions

### 0. Clone the project
        git clone https://github.com/cs0320-s25/term-project-ccaf.git

### 1. Run backend 
- Make sure you have you have Java 17 and Maven installed.
        cd backend
        cd draft
        mvn clean install
        ./run
This starts the server at http://localhost:3232.

### 2. Run Frontend 
- Make sure you have Clerk, React, and Tailwind installed.
        cd ..
        cd ..
        cd client
        npm run dev
This starts the frontend at http://localhost:3000.
