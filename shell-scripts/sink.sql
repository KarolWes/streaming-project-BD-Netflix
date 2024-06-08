mysql -uroot -padmin
CREATE USER 'streamuser'@'%' IDENTIFIED BY 'stream';
CREATE DATABASE IF NOT EXISTS etl CHARACTER SET utf8;
GRANT ALL ON etl.* TO 'streamuser'@'%';
exit

mysql -ustreamuser -pstream etl

create table netflix_sink
(
    movie_id       integer,
    title          varchar(100),
    date           varchar(20),
    rate_count     bigint,
    rate_sum       bigint,
    reviewer_count bigint
);
exit


mysql -u streamuser -p streamdb
use etl;
select * from netflix_sink;
exit