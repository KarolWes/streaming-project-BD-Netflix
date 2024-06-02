Aby uruchomić klaster na Google Cloud użyj:
```shell
gcloud dataproc clusters create ${CLUSTER_NAME} \
--enable-component-gateway --region ${REGION} --subnet default \
--master-machine-type n1-standard-4 --master-boot-disk-size 50 \
--num-workers 2 --worker-machine-type n1-standard-2 --worker-boot-disk-size 50 \
--image-version 2.1-debian11 --optional-components FLINK,DOCKER,ZOOKEEPER \
--project ${PROJECT_ID} --max-age=2h \
--metadata "run-on-master=true" \
--initialization-actions \
gs://goog-dataproc-initialization-actions-${REGION}/kafka/kafka.sh
```
## Uruchomienie
1. Na terminalu nadawczym utwórz potrzebne tematy kafki korzystając ze skryptu `kafka.sh`
2. Na terminalu odbiorczym uruchom kontener MySQL (hasło `admin`)
```shell
docker run --name mymysql -v /tmp/datadir:/var/lib/mysql -p 6033:3306 \
-e MYSQL_ROOT_PASSWORD=admin -d mysql:debian
```
3. Połącz się do terminala `docker exec -it mymysql bash` i uruchom polecenie `mysql -u root -p` podając hasło, gdy terminal o to zapyta.
4. Utwórz bazę danych, kopiując trzy pierwsze polecenia z pliku `sink.sql`
```mysql
CREATE USER 'streamuser'@'%' IDENTIFIED BY 'stream';
CREATE DATABASE IF NOT EXISTS etl CHARACTER SET utf8;
GRANT ALL ON streamdb.* TO 'streamuser'@'%';
```
5. Poleceniem `exit` wyloguj się z konsoli i zaloguj ponownie na konto użytkownika streamuser `mysql -u streamuser -p etl`
6. Utwórz tabelę wynikową za pomocą polecenia `CREATE TABLE` z pliku `sink.sql` i wyloguj się poleceniem `exit`


## Wyniki
Anomalie są zbierane przez temat kafki. Aby je odebrać, na terminalu odbiorczym uruchom polecenie:
```shell
/usr/lib/kafka/bin/kafka-console-consumer.sh \
 --bootstrap-server ${CLUSTER_NAME}-w-0:9092 \
 --topic OutputAnomalies
```
Wyniki przetwarzania czasu rzeczywistego zbierane są w bazie danych MySQL.
Aby je otworzyć, zaloguj się do konsoli MySQL `mysql -u streamuser -p streamdb` i uruchom polecenie select z pliku `sink.sql`
```mysql
select * from netflix_sink;
```
