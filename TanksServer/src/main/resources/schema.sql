DROP TABLE ID EXISTS t_user CASCADE;
DROP TABLE IF EXISTS t_statistic CASCADE;

CREATE TABLE IF NOT EXISTS t_user (
        id SERIAL PRIMARY KEY NOT null,
        name VARCHAR NOT null);

CREATE TABLE IF NOT EXISTS t_statistic (
        user_id INTEGER NOT null,
        shots INTEGER DEFAULT 0,
        hits INTEGER DEFAULT 0,
        misses INTEGER DEFAULT 0,
        CONSTRAINT fk_t_statistic_user_id FOREIGN KEY (user_id) REFERENCES t_user(id));


CREATE OR REPLACE FUNCTION fnc_insert_user_create_statistic()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO t_statistic(user_id)
    VALUES(NEW.id);
RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';



CREATE OR REPLACE TRIGGER trg_insert_user_create_statictic
AFTER INSERT ON t_user
FOR EACH ROW
EXECUTE FUNCTION fnc_insert_user_create_statistic();
