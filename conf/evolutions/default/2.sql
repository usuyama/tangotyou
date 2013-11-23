# --- !Ups

create table query (
  id          bigint auto_increment not null,
  primary key (id),
  user_id     bigint not null,
  word        text not null,
  url         text not null,
  site        varchar(255),
  created_at  TIMESTAMP DEFAULT NOW(),
  CONSTRAINT fk_wr_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

# --- !Downs
drop table query;
