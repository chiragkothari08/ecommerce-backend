-- ============================================================
-- Adds flash-sale countdown timer support.
-- V1 already ran on existing databases, so this is a separate
-- migration (V2) rather than editing V1 — Flyway would otherwise
-- fail with a checksum-mismatch error on any DB that already
-- applied V1.
-- ============================================================

ALTER TABLE products
    ADD COLUMN flash_sale_end_time TIMESTAMP;
