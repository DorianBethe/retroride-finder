CREATE TABLE IF NOT EXISTS search_queries (
    id SERIAL PRIMARY KEY,
    query TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS api_cars (
    id SERIAL PRIMARY KEY,
    make TEXT NOT NULL,
    model TEXT NOT NULL,
    year INT NOT NULL
);
