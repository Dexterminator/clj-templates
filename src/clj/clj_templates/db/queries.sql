-- :name insert-user :! :n
-- :doc Insert a user record
insert into users (name, age)
values (:name, :age);

-- :name all-users :? :*
-- :doc Get all users
select * from users
order by name;
