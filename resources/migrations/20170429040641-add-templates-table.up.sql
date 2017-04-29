CREATE TABLE IF NOT EXISTS templates (
  template_name VARCHAR(80) PRIMARY KEY,
  build_system  VARCHAR(20),
  description   VARCHAR(80)
)