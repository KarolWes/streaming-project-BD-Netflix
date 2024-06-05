/usr/lib/kafka/bin/kafka-server-stop.sh
/usr/lib/kafka/bin/kafka-server-start.sh

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
declare -a topics=("NetflixInput" "OutputAnomalies")

for i in "${topics[@]}"
do
  existing_topic_check=$( /usr/lib/kafka/bin/kafka-topics.sh --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --list| grep "$i")
  if [ -n "$existing_topic_check" ]; then
      # Delete the existing topic
      /usr/lib/kafka/bin/kafka-topics.sh --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --delete --topic "$i"
      echo "Deleted existing topic: $i"
  else
      echo "Topic '$i' does not exist."
  fi

  # Create a new topic
  /usr/lib/kafka/bin/kafka-topics.sh --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --create --topic "$i" --partitions 1 --replication-factor 1
  echo "Created new topic: $i"
done

echo "Currently existing topics: "
/usr/lib/kafka/bin/kafka-topics.sh --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --list
read -p "Press enter to continue"