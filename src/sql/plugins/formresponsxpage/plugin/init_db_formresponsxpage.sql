-- liquibase formatted sql
-- changeset core:init_db_formresponsxpage.sql
-- preconditions onFail:MARK_RAN onError:WARN
insert into formresponsxpage_formsreponseedito (labelrichtext_un, labelrichtext_deux) values ('<p> test1 </p>', '<p> test2 </p>');