insert into label (name)
values ('Home'),
       ('Work'),
       ('Shopping');

insert into memo (id, text, created_date, due_date)
values (1, 'Buy detergents', CURRENT_TIMESTAMP, DATEADD('DAY', +1, CURRENT_TIMESTAMP)),
       (2, 'Do laundry', CURRENT_TIMESTAMP, null),
       (3, 'Buy a new flash drive', CURRENT_TIMESTAMP, null),
       (4, 'Backup project files', CURRENT_TIMESTAMP, DATEADD('DAY', +7, CURRENT_TIMESTAMP));

insert into memo_label (memo_id, label_id)
values (1, (select id from label where name = 'Home')),
       (1, (select id from label where name = 'Shopping')),
       (2, (select id from label where name = 'Home')),
       (3, (select id from label where name = 'Work')),
       (3, (select id from label where name = 'Shopping')),
       (4, (select id from label where name = 'Work'));
