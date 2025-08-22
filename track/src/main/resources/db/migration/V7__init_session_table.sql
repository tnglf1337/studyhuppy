create table session(
    id serial primary key,
    fach_id uuid not null,
    username varchar(200) not null,
    titel varchar(100),
    beschreibung text
);

create table block(
    fach_id uuid,
    modul_id uuid not null,
    lernzeit_seconds int not null,
    pausezeit_seconds int not null,
    session int references session(id) on delete cascade,
    session_key int
);