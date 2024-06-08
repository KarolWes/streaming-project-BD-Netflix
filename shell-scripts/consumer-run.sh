cd ~
wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-jdbc/1.15.0/flink-connector-jdbc-1.15.0.jar
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-kafka/1.15.4/flink-connector-kafka-1.15.4.jar
sudo cp ~/*-*.jar /usr/lib/flink/lib/
mkdir -p Consumer/src/main/resources
nano Consumer/src/main/resources/flink.properties
# uzupełnij danymi z pliku
export HADOOP_CONF_DIR=/etc/hadoop/conf
export HADOOP_CLASSPATH=`hadoop classpath`
flink run -m yarn-cluster -p 4 \
 -yjm 1024m -ytm 1024m -c \
 consumer.NetflixAnalyzer ~/Consumer.jar \
 --D 30 --L 1 --O 4 \
 --delay C