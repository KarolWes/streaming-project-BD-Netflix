package consumer;
import models.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import utils.*;

import static utils.Connector.getMySQLSink;


public class NetflixAnalyzer {
    public static void main(String[] args) throws Exception {

        int D = Integer.parseInt(args[0]);
        int L = Integer.parseInt(args[1]);
        double O = Double.parseDouble(args[2]);

        final StreamExecutionEnvironment senv = StreamExecutionEnvironment.getExecutionEnvironment();

        ParameterTool properties = ParameterTool.fromPropertiesFile("Consumer/src/main/resources/flink.properties");


        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "NetflixInput",
                new SimpleStringSchema(),
                properties.getProperties()
        );

        String path = properties.get("movies.path");
        DataStream<MovieData> movies = senv.readTextFile(path)
                .filter(a -> !a.startsWith("ID"))
                .filter(a -> !a.contains("NULL"))
                .map(a -> a.split(","))
                .filter(a -> a.length == 3)
                .map(a -> new MovieData(Integer.parseInt(a[0]), Integer.parseInt(a[1]), a[2]))
                ;

        DataStream<PrizeData> rates = senv.addSource(consumer)
                .filter(a -> !a.startsWith("date"))
                .filter(a -> !a.contains("NULL"))
                .map(line -> line.split(","))
                .filter(a -> a.length == 4)
                .map(a -> new PrizeData(
                        a[0],
                        Integer.parseInt(a[1]),
                        Integer.valueOf(a[2]),
                        Integer.valueOf(a[3])
                ))
                ;

        DataStream<CombinedData> scores = movies.connect(rates)
                .keyBy(MovieData::getId, PrizeData::getMovieId)
                .flatMap(new Combiner())
                .assignTimestampsAndWatermarks(new DelayWatermarkGenerator());

        DataStream<EtlAgg> aggregated = scores.keyBy(CombinedData::getMovieId)
                .window(new MonthlyWindowAssigner(properties.get("delay")))
                .aggregate(new AggregatorETL()); // nie działa

        aggregated.addSink(getMySQLSink(properties));
        // aggregated.print();


        DataStream<AnomalyData> anomalies = scores.keyBy(CombinedData::getTitle) // ta linia też nie działa
                .window(SlidingEventTimeWindows.of(Time.days(D), Time.days(1)))
                .aggregate(new AggregatorAnomaly(), new ProcessWindowFunction<AnomalyData, AnomalyData, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<AnomalyData> elements, Collector<AnomalyData> out) {
                        AnomalyData result = elements.iterator().next(); // assuming there is always one element
                        result.setWindowStart(String.valueOf(context.window().getStart()));
                        result.setWindowEnd(String.valueOf(context.window().getEnd()));
                        out.collect(result);
                    }
                })
                .filter(a -> a.getRateAvg() >= O).filter(a -> a.getRateCount() >= L);


        FlinkKafkaProducer<String> anomalyProducer = new FlinkKafkaProducer<>(properties.get("bootstrap.servers"), "OutputAnomalies", new SimpleStringSchema());
        anomalyProducer.setWriteTimestampToKafka(true);


        anomalies.map((MapFunction<AnomalyData, String>) Object::toString).addSink(anomalyProducer);


        senv.execute("Netflix prize data");
    }

}
