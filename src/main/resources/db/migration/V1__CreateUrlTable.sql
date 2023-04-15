CREATE TABLE IF NOT EXISTS url
(
    id                  uuid    PRIMARY KEY NOT NULL,
    original            varchar             NOT NULL,
    shorted             varchar             NOT NULL,
    created_at          bigint              NOT NULL,
    times_called_day    bigint              NOT NULL
)