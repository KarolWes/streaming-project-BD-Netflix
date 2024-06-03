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
## Ustawienia początkowe
### Producent Kafki
1. Na terminalu nadawczym utwórz potrzebne tematy kafki korzystając ze skryptu `kafka.sh`. Jeżeli nie można, dodaj uprawnienia poleceniem `chmod`.
2. Utwórz folder danych źródłowych `mkdir data` i skopiuj dane z zasobinka na klaster poleceniem `hadoop fs -copyToLocal gs://{bucket}/ścieżka/do/danych`. Upewnij się, że plik `movies.csv` jest w innym folderze niż reszta danych.
3. Utwórz folder `mkdir -p Producer/src/main/resources` i utwórz w nim plik `kafka.properties`, do którego skopiuj zawartość pliku `kafka.properties` dołączonego do rozwiązania. Ustaw parametr `input.dir` na ścieżkę do folderu z danymi (plikami csv).
4. Wgraj plik `producer.jar`

### Odbiorca - baza danych
1. Utwórz folder na dane wynikowe `mkdir -p /tmp/datadir`
2. Na terminalu odbiorczym uruchom kontener MySQL (hasło `admin`)
```shell
docker run --name mymysql -v /tmp/datadir:/var/lib/mysql -p 6033:3306 \
-e MYSQL_ROOT_PASSWORD=admin -d mysql:debian
```
3. Połącz się z terminalem `docker exec -it mymysql bash` i uruchom polecenie `mysql -uroot -padmin` podając hasło, gdy terminal o to zapyta.
4. Utwórz bazę danych, kopiując trzy pierwsze polecenia z pliku `sink.sql`
```mysql
CREATE USER 'streamuser'@'%' IDENTIFIED BY 'stream';
CREATE DATABASE IF NOT EXISTS etl CHARACTER SET utf8;
GRANT ALL ON etl.* TO 'streamuser'@'%';
```
5. Poleceniem `exit` wyloguj się z konsoli i zaloguj ponownie na konto użytkownika streamuser `mysql -u streamuser -p etl`
6. Utwórz tabelę wynikową za pomocą polecenia `CREATE TABLE` z pliku `sink.sql` i wyloguj się poleceniem `exit`. Zamknij klaster poleceniem `exit`.

### Odbiorca - kafka
Temat wynikowy kafki został utworzony skryptem `kafka.sh`. 

### Przetwarzanie
1. Pobierz konieczne pliki z repozytorium mavena
```shell
cd ~
wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-jdbc/1.15.4/flink-connector-jdbc-1.15.4.jar
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar
sudo cp ~/*-*.jar /usr/lib/flink/lib/
```
2. Utwórz plik właściwości w lokalizacji `Consumer/src/main/resources/flink.properties` i wgraj do niego zawartość załączonego pliku flink.properties. Upewnij się, że ścieżka do pliku `movie_titles.csv` jest ustawiona poprawnie. Ostatni parametr, czyli `delay` realizuje różne wyzwalacze. Ustaw go na 'C' lub 'A';
3. Wgraj plik `Consumer.jar` i uruchom go poleceniem `java -jar`. Jako parametry wywołania podaj trzy liczby, które preprezentują odpowiednio:
   * Szerokość okna analizy anomalii (w dniach)
   * Ilość wymaganych obserwacji
   * Minimalną średnią ocen
4. Z innego terminala uruchom plik `Producer.jar`. Uwaga: Błąd
```
Exception in thread "main" java.lang.NullPointerException
	at java.base/java.util.Arrays.stream(Arrays.java:5614)
	at TestProducer.main(TestProducer.java:21)
```
oznacza źle zdefiniowaną ścieżkę do plików danych w `kafka.properties`


## Wyniki
Anomalie są zbierane przez temat kafki. Aby je odebrać, na terminalu odbiorczym uruchom polecenie:
```shell
/usr/lib/kafka/bin/kafka-console-consumer.sh \
 --bootstrap-server {CLUSTER_NAME}-w-0:9092 \
 --topic OutputAnomalies
```
Nazwę klastra najlepiej wpisać ręczenie.
Wyniki przetwarzania czasu rzeczywistego zbierane są w bazie danych MySQL.
Aby je otworzyć, zaloguj się do konsoli MySQL `mysql -u streamuser -p streamdb` i uruchom polecenie select z pliku `sink.sql`
```mysql
select * from netflix_sink;
```
Gdyby pojawiło się ostrzeżenie o niewybranej bazie danych, użyj polecenia `use etl;`.

## Uwagi

