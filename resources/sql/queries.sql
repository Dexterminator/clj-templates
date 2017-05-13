-- :name upsert-template :! :n
-- :doc "Upsert" a leiningen or boot template record
insert into templates (template_name, build_system, description, github_url, github_id, github_stars, github_readme, homepage, downloads)
values (:template-name, :build-system, :description, :github-url, :github-id, :github-stars, :github-readme, :homepage, :downloads)
on conflict (template_name, build_system)
do update set build_system = :build-system, description = :description, github_url = :github-url, github_id = :github-id, github_stars = :github-stars, github_readme = :github-readme,
homepage = :homepage, downloads = :downloads;

-- :name all-templates :? :*
-- :doc Get all templates
select * from templates order by downloads desc;

-- :name templates :? :*
-- :doc Get all templates for build system
select * from templates where build_system = :build-system;

-- :name delete-all-templates :! :n
delete from templates;
