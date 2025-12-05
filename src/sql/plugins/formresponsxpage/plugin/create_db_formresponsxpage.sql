-- liquibase formatted sql
-- changeset core:create_db_formresponsxpage.sql
-- preconditions onFail:MARK_RAN onError:WARN

--
-- Structure for table formresponsxpage_formsreponseedito
--

DROP TABLE IF EXISTS formresponsxpage_formsreponseedito;
CREATE TABLE formresponsxpage_formsreponseedito (
id_formsreponseedito int AUTO_INCREMENT,
labelrichtext_un long varchar,
labelrichtext_deux long varchar,
PRIMARY KEY (id_formsreponseedito)
);
