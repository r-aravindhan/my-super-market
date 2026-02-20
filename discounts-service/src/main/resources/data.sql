INSERT INTO discounts (code, description, type, value, target_sku, target_category, min_quantity, min_purchase_amount, valid_from, valid_to, active) VALUES
('SAVE10', '10% off entire order', 'PERCENTAGE', 10.00, NULL, NULL, 0, 50.00, '2025-01-01', '2025-12-31', true),
('FLAT5', '5 dollars off', 'FIXED_AMOUNT', 5.00, NULL, NULL, 0, 25.00, '2025-01-01', '2025-12-31', true),
('MILK2FOR1', 'Buy 2 get 1 free on milk', 'BUY_X_GET_Y', 1.00, 'SKU001', NULL, 2, NULL, '2025-01-01', '2025-12-31', true);
