CREATE SCHEMA `careymd` ;
CREATE TABLE `careymd`.`accounts` (
  `username` VARCHAR(20) NOT NULL COMMENT '',
  `password` VARCHAR(45) NULL COMMENT '',
  PRIMARY KEY (`username`)  COMMENT '');