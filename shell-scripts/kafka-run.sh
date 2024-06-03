chmod +777 kafka.sh
./kafka.sh
mkdir data
cd data
hadoop fs -copyToLocal gs://{bucket}/ścieżka/do/danych
mkdir -p Producer/src/main/resources
nano Producer/src/main/resources/kafka.properties
# uzupełnij danymi z pliku