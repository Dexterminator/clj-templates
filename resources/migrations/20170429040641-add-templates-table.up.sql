create table IF not exists templates (
  template_name varchar(80),
  build_system  varchar(20),
  description   text,
  github_url    text,
  primary key (template_name, build_system)
)
