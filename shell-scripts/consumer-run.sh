export HADOOP_CONF_DIR=/etc/hadoop/conf
export HADOOP_CLASSPATH=`hadoop classpath`
flink run -m yarn-cluster -p 4 \
 -yjm 1024m -ytm 1024m -c \
 consumer/NetflixAnalyzer ~/Consumer.jar \
 --D 30 --L 1 --O 4 \
 --delay C
