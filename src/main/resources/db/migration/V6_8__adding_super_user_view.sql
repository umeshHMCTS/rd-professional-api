create view super_user_view as select * from professional_user where id in (select professional_user_id from user_attribute where prd_enum_code = 4 AND prd_enum_type like 'ADMIN_ROLE');
