existing_topic="NetflixInput"

# Check if the topic exists
existing_topic_check=$(docker exec kafka kafka-topics.sh --list --zookeeper zookeeper:2181 | grep "$existing_topic")

if [ -n "$existing_topic_check" ]; then
    # Delete the existing topic
    docker exec kafka kafka-topics.sh --delete --topic "$existing_topic" --zookeeper zookeeper:2181
    echo "Deleted existing topic: $existing_topic"
else
    echo "Topic '$existing_topic' does not exist."
fi

# Create a new topic
new_topic="NetflixInput"
docker exec kafka kafka-topics.sh --create --topic "$new_topic" --partitions 1 --replication-factor 1 --zookeeper zookeeper:2181
echo "Created new topic: $new_topic"
read -p "Press enter to continue"