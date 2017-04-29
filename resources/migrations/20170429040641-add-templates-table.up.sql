create table IF not exists templates (
  template_name varchar(80) primary key,
  build_system  varchar(20),
  description   text
)
