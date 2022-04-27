insert into label (name)
values ('Home'),
       ('Work'),
       ('Shopping');

insert into memo (text, created_date, due_date)
values ('Buy detergents', CURRENT_TIMESTAMP, DATEADD('DAY', +1, CURRENT_TIMESTAMP)),
       ('Do laundry', CURRENT_TIMESTAMP, null),
       ('Buy a new flash drive', CURRENT_TIMESTAMP, null),
       ('Backup project files', CURRENT_TIMESTAMP, DATEADD('DAY', +7, CURRENT_TIMESTAMP));

insert into memo_label (memo_id, label_id)
values ((select id from memo where text = 'Buy detergents'), (select id from label where name = 'Home')),
       ((select id from memo where text = 'Buy detergents'), (select id from label where name = 'Shopping')),
       ((select id from memo where text = 'Do laundry'), (select id from label where name = 'Home')),
       ((select id from memo where text = 'Buy a new flash drive'), (select id from label where name = 'Work')),
       ((select id from memo where text = 'Buy a new flash drive'), (select id from label where name = 'Shopping')),
       ((select id from memo where text = 'Backup project files'), (select id from label where name = 'Work'));
