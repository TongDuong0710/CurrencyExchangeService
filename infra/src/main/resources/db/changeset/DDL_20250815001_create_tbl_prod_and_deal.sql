-- 1) PRODUCT
DROP TABLE IF EXISTS product CASCADE;

CREATE TABLE product (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    category   VARCHAR(100) NOT NULL,
    price      NUMERIC(12,2) NOT NULL CHECK (price > 0),
    stock      INTEGER NOT NULL CHECK (stock >= 0),
    available  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for filter/pagination
CREATE INDEX idx_product_category        ON product (category);
CREATE INDEX idx_product_available       ON product (available);
CREATE INDEX idx_product_price           ON product (price);
CREATE INDEX idx_product_cat_avail_price ON product (category, available, price);
CREATE INDEX idx_product_created_at_id   ON product (created_at DESC, id);


-- 2) DEAL
DROP TABLE IF EXISTS deal CASCADE;

CREATE TABLE deal (
    id                  BIGSERIAL PRIMARY KEY,
    product_id          BIGINT NOT NULL REFERENCES product(id) ON DELETE RESTRICT,
    deal_type           VARCHAR(50) NOT NULL,
    description         VARCHAR(255),
    expiration_datetime TIMESTAMP NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_product_deal_type UNIQUE (product_id, deal_type)
);


-- Index for join và filter deal expired agreements
CREATE INDEX idx_deal_product_id          ON deal (product_id);
CREATE INDEX idx_deal_expiration_datetime ON deal (expiration_datetime);
CREATE INDEX idx_deal_product_expiration  ON deal (product_id, expiration_datetime);
CREATE INDEX idx_deal_created_at_id       ON deal (created_at DESC, id);


CREATE TABLE basket (
    id         BIGSERIAL PRIMARY KEY,
    user_id    VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status     VARCHAR(20) NOT NULL DEFAULT 'OPEN' -- OPEN / CHECKED_OUT
);
CREATE INDEX idx_basket_user_id_status ON basket(user_id, status);

CREATE TABLE basket_item (
    id          BIGSERIAL PRIMARY KEY,
    basket_id   BIGINT NOT NULL REFERENCES basket(id) ON DELETE CASCADE,
    product_id  BIGINT NOT NULL REFERENCES product(id) ON DELETE RESTRICT,
    quantity    INTEGER NOT NULL CHECK (quantity > 0),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_basket_item_basket_id ON basket_item(basket_id);
CREATE INDEX idx_basket_item_product_id ON basket_item(product_id);
