chmod +777 kafka.sh
./kafka.sh
mkdir data
cd data
hadoop fs -copyToLocal gs://pbd-23-kw/project-netflix
cd ..
mkdir -p Producer/src/main/resources
nano Producer/src/main/resources/kafka.properties
# uzupełnij danymi z pliku
export HADOOP_CONF_DIR=/etc/hadoop/conf
export HADOOP_CLASSPATH=`hadoop classpath`
flink run -m yarn-cluster -p 4 \
 -yjm 1024m -ytm 1024m -c \
 TestProducer ~/Producer.jar