CREATE TABLE IF NOT EXISTS url
(
    original         varchar                NOT NULL,
    shorted          varchar PRIMARY KEY   NOT NULL,
    created_at       bigint           NOT NULL,
    times_called_day bigint           NOT NULL
)