package utils;

import models.EtlAgg;
import org.apache.flink.api.common.functions.ReduceFunction;

public class ReductorEtl implements ReduceFunction<EtlAgg> {
    @Override
    public EtlAgg reduce(EtlAgg etlAgg, EtlAgg t1) throws Exception {
        return new EtlAgg(
                etlAgg.getFilmId(),
                etlAgg.getTitle(),
                etlAgg.getDate(),
                etlAgg.getRateCount() + t1.getRateCount(),
                etlAgg.getRateSum() + t1.getRateSum(),
                0L
        );
    }
}
