insert into label (name)
values ('Home'),
       ('Work'),
       ('Shopping');

insert into memo (text, priority, created_date, due_date)
values ('Buy detergents', 'SHORT_TERM', current_timestamp, dateadd('DAY', +1, current_timestamp)),
       ('Do laundry', 'SHORT_TERM', current_timestamp, dateadd('DAY', +7, current_timestamp)),
       ('Think about new house', null, current_timestamp, null),
       ('Buy a new flash drive', 'MID_TERM', current_timestamp, dateadd('DAY', +14, current_timestamp)),
       ('Backup project files', 'MID_TERM', current_timestamp, dateadd('MONTH', +1, current_timestamp)),
       ('Learn a new language', 'LONG_TERM', current_timestamp, dateadd('YEAR', +1, current_timestamp));

insert into memo_label (memo_id, label_id)
values ((select id from memo where text = 'Buy detergents'), (select id from label where name = 'Shopping')),
       ((select id from memo where text = 'Buy detergents'), (select id from label where name = 'Home')),

       ((select id from memo where text = 'Do laundry'), (select id from label where name = 'Home')),

       ((select id from memo where text = 'Think about new house'), (select id from label where name = 'Home')),

       ((select id from memo where text = 'Buy a new flash drive'), (select id from label where name = 'Shopping')),
       ((select id from memo where text = 'Buy a new flash drive'), (select id from label where name = 'Work')),

       ((select id from memo where text = 'Backup project files'), (select id from label where name = 'Work')),

       ((select id from memo where text = 'Learn a new language'), (select id from label where name = 'Work'));
