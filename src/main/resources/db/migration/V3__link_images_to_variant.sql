-- ============================================================
-- Adds the ability to tag a product image to a specific
-- color/size variant, so the frontend can swap photos when the
-- user picks a different color. NULL = general product image.
-- ============================================================

ALTER TABLE product_images
    ADD COLUMN variant_id UUID REFERENCES product_variants(id) ON DELETE SET NULL;

CREATE INDEX idx_product_images_variant ON product_images(variant_id);
