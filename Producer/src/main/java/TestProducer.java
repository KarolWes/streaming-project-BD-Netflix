import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class TestProducer {
    public static void main(String[] args) throws IOException {
        ParameterTool properties = ParameterTool.fromPropertiesFile("Producer/src/main/resources/kafka.properties");


        KafkaProducer<String, String> producer = new KafkaProducer<>(properties.getProperties());
        final File folder = new File(properties.get("input.dir"));
        File[] listOfFiles = folder.listFiles();
        String[] listOfPaths = Arrays.stream(listOfFiles).
                map(file -> file.getAbsolutePath()).toArray(String[]::new);
        Arrays.sort(listOfPaths);
        for (final String fileName : listOfPaths) {
            try (Stream<String> stream = Files.lines(Paths.get(fileName)).
                    skip(0)) {
                stream.forEach(line ->
                        {
                            System.out.println(line);
                            producer.send(
                                    new ProducerRecord<>("NetflixInput", String.valueOf(line.hashCode()), line)
                            );
                        }
                        );
                TimeUnit.SECONDS.sleep(15);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        producer.close();
    }
}