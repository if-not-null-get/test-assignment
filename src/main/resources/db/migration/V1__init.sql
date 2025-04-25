CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          code CHAR(10) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL,
                          price_eur NUMERIC(10, 2) NOT NULL CHECK (price_eur >= 0),
                          price_usd NUMERIC(10, 2) NOT NULL CHECK (price_usd >= 0),
                          is_available BOOLEAN NOT NULL
);