package consumer;
import models.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import utils.*;

import static utils.Connector.getMySQLSink;


public class NetflixAnalyzer {
    public static void main(String[] args) throws Exception {



        final StreamExecutionEnvironment senv = StreamExecutionEnvironment.getExecutionEnvironment();

        ParameterTool propertiesFile = ParameterTool.fromPropertiesFile("Consumer/src/main/resources/flink.properties");
        ParameterTool propertiesArgs = ParameterTool.fromArgs(args);
        ParameterTool properties = propertiesFile.mergeWith(propertiesArgs);

        int D = Integer.parseInt(properties.get("D"));
        int L = Integer.parseInt(properties.get("L"));
        double O = Double.parseDouble(properties.get("O"));

        KafkaSource<String> consumer = KafkaSource.<String>builder()
                .setBootstrapServers(properties.get("bootstrap.servers"))
                .setTopics("NetflixInput")
                .setDeserializer(new KafkaRecordDeserializationSchema<>() {
                    @Override
                    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<String> collector){
                        collector.collect(new String(consumerRecord.value()));
                    }

                    @Override
                    public TypeInformation<String> getProducedType() {
                        return TypeInformation.of(String.class);
                    }
                })
                .build();

        String path = properties.get("movies.path");
        DataStream<MovieData> movies = senv.readTextFile(path)
                .filter(a -> !a.startsWith("ID"))
                .filter(a -> !a.contains("NULL"))
                .map(a -> a.split(","))
                .filter(a -> a.length == 3)
                .map(a -> new MovieData(Integer.parseInt(a[0]), Integer.parseInt(a[1]), a[2]));

        DataStream<PrizeData> rates = senv.fromSource(consumer, WatermarkStrategy.noWatermarks(), "Rates     Source")
                .filter(a -> !a.startsWith("date"))
                .filter(a -> !a.contains("NULL"))
                .map(line -> line.split(","))
                .filter(a -> a.length == 4)
                .map(a -> new PrizeData(
                        a[0],
                        Integer.parseInt(a[1]),
                        Integer.valueOf(a[2]),
                        Integer.valueOf(a[3])
                ));

        DataStream<CombinedData> scores = movies.connect(rates)
                .keyBy(MovieData::getId, PrizeData::getMovieId)
                .flatMap(new Combiner())
                .assignTimestampsAndWatermarks(new DelayWatermarkGenerator());

        DataStream<EtlAgg> aggregated = scores.keyBy(CombinedData::getMovieId)
                .window(new MonthlyWindowAssigner(properties.get("delay")))
                .aggregate(new AggregatorETL());

        // aggregated.addSink(getMySQLSink(properties));
        aggregated.print();


        DataStream<AnomalyData> anomalies = scores.keyBy(CombinedData::getTitle)
                .window(SlidingEventTimeWindows.of(Time.days(D), Time.days(1)))
                .aggregate(new AggregatorAnomaly(), new ProcessWindowFunction<AnomalyData, AnomalyData, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<AnomalyData> elements, Collector<AnomalyData> out) {
                        AnomalyData result = elements.iterator().next();
                        result.setWindowStart(String.valueOf(context.window().getStart()));
                        result.setWindowEnd(String.valueOf(context.window().getEnd()));
                        out.collect(result);
                    }
                })
                .filter(a -> a.getRateAvg() >= O).filter(a -> a.getRateCount() >= L);


        anomalies.map((MapFunction<AnomalyData, String>) Object::toString).sinkTo(KafkaSink.<String>builder()
                .setBootstrapServers(properties.get("bootstrap.servers"))
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("OutputAnomalies")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                ).setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build());


        senv.execute("Netflix prize data");
    }

}
