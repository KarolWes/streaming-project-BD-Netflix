mkdir -p /tmp/datadir
docker run --name mymysql -v /tmp/datadir:/var/lib/mysql -p 6033:3306 -e MYSQL_ROOT_PASSWORD=admin -d mysql:debian
docker exec -it mymysql bash

exit
