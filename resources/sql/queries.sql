-- :name upsert-template :! :n
-- :doc "Upsert" a leiningen or boot template record
insert into templates (template_name, build_system, description)
values (:template-name, :build-system, :description)
on conflict (template_name, build_system)
do update set build_system = :build-system, description = :description;

-- :name all-templates :? :*
-- :doc Get all templates
select * from templates order by template_name;

-- :name templates :? :*
-- :doc Get all templates for build system
select * from templates where build_system = :build-system;

-- :name delete-all-templates :! :n
delete from templates;
