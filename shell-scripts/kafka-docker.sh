
declare -a topics=("NetflixInput" "OutputAnomalies")

for i in "${topics[@]}"
do
  existing_topic_check=$(docker exec kafka kafka-topics.sh --list --zookeeper zookeeper:2181 | grep "$i")
  if [ -n "$existing_topic_check" ]; then
      # Delete the existing topic
      docker exec kafka kafka-topics.sh --delete --topic "$i" --zookeeper zookeeper:2181
      echo "Deleted existing topic: $i"
  else
      echo "Topic '$i' does not exist."
  fi

  # Create a new topic
  docker exec kafka kafka-topics.sh --create --topic "$i" --partitions 1 --replication-factor 1 --zookeeper zookeeper:2181
  echo "Created new topic: $i"
done

echo "Currently existing topics: "
docker exec kafka kafka-topics.sh --list --zookeeper zookeeper:2181
read -p "Press enter to continue"