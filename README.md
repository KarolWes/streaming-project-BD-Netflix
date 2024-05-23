Aby uruchomić klaster na Google Cloud użyj:
```shell
gcloud dataproc clusters create ${CLUSTER_NAME} \
--enable-component-gateway --region ${REGION} --subnet default \
--master-machine-type n1-standard-4 --master-boot-disk-size 50 \
--num-workers 2 --worker-machine-type n1-standard-2 --worker-boot-disk-size 50 \
--image-version 2.1-debian11 --optional-components DOCKER,ZOOKEEPER \
--project ${PROJECT_ID} --max-age=2h \
--metadata "run-on-master=true" \
--initialization-actions \
gs://goog-dataproc-initialization-actions-${REGION}/kafka/kafka.sh
```
Do testów lokalnych uruchom `docker compose -d up`
Utwórz potrzebne tematy kafki korzystając ze skryptu `kafka.sh`

Aby odbierać wiadomości, w konsoli gcloud uruchom polecenie
```shell
/usr/lib/kafka/bin/kafka-console-consumer.sh \
 --bootstrap-server ${CLUSTER_NAME}-w-0:9092 \
 --topic NetflixInput
```
Korzystając z dockera w poleceniach pomija się ścieżkę do plików shellowych (zostaje sama nazwa), a zamiast nazwy klastra adres hosta (127.0.0.1)