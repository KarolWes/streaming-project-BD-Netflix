package utils;

import models.CombinedData;
import models.MovieData;
import models.PrizeData;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;


public class Combiner extends RichCoFlatMapFunction<MovieData, PrizeData, CombinedData>
{
    private ValueState<MovieData> movieDataValueState;
    private ValueState<PrizeData> prizeDataValueState;

    public void open(Configuration config)
    {
        movieDataValueState = getRuntimeContext().getState(new ValueStateDescriptor<>("movies", MovieData.class));
        prizeDataValueState = getRuntimeContext().getState(new ValueStateDescriptor<>("prizes", PrizeData.class));
    }

    @Override
    public void flatMap1(MovieData movie, Collector<CombinedData> collector) throws Exception
    {
        PrizeData prize = prizeDataValueState.value();

        if(prize != null)
        {
            prizeDataValueState.clear();
            collector.collect(new CombinedData(
                    prize.getDate(),
                    prize.getMovieId(),
                    movie.getTitle(),
                    movie.getYear(),
                    prize.getUserId(),
                    prize.getRate()
            ));
        }
        else
        {
            movieDataValueState.update(movie);
        }
    }

    @Override
    public void flatMap2(PrizeData prize, Collector<CombinedData> collector) throws Exception
    {
        MovieData movie = movieDataValueState.value();

        if(movie != null)
        {
            movieDataValueState.clear();
            collector.collect(new CombinedData(
                    prize.getDate(),
                    prize.getMovieId(),
                    movie.getTitle(),
                    movie.getYear(),
                    prize.getUserId(),
                    prize.getRate()
            ));
        }
        else
        {
            prizeDataValueState.update(prize);
        }
    }
}
