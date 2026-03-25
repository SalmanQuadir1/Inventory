-- Drop legacy tables that might block schema updates
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS medicines;
SET FOREIGN_KEY_CHECKS = 1;
