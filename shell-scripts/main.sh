cd ~
wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-jdbc/1.15.4/flink-connector-jdbc-1.15.4.jar
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar
sudo cp ~/*-*.jar /usr/lib/flink/lib/
mkdir -p Consumer/src/main/resources
nano Consumer/src/main/resources/flink.properties
# uzupełnij danymi z pliku
java -jar Consumer.jar a b c