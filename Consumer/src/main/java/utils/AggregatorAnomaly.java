package utils;

import models.*;
import org.apache.flink.api.common.functions.AggregateFunction;

public class AggregatorAnomaly implements AggregateFunction<CombinedData, TEMPAnomaly, AnomalyData> {
    @Override
    public TEMPAnomaly createAccumulator() {
        return new TEMPAnomaly();
    }

    @Override
    public TEMPAnomaly add(CombinedData combinedData, TEMPAnomaly tempAnomaly) {
        TEMPAnomaly anomaly = new TEMPAnomaly();
        anomaly.setTitle(combinedData.getTitle());
        anomaly.setRateCount(tempAnomaly.getRateCount() + 1);
        anomaly.setRateSum(combinedData.getRate() + tempAnomaly.getRateSum());
        return anomaly;
    }

    @Override
    public AnomalyData getResult(TEMPAnomaly tempAnomaly) {
        AnomalyData anomaly = new AnomalyData();
        anomaly.setTitle(tempAnomaly.getTitle());
        anomaly.setRateCount(tempAnomaly.getRateCount());
        anomaly.setRateAvg(tempAnomaly.getRateSum() * 1.0 / tempAnomaly.getRateCount());

        return anomaly;
    }

    @Override
    public TEMPAnomaly merge(TEMPAnomaly tempAnomaly, TEMPAnomaly acc1) {
        TEMPAnomaly anomaly = new TEMPAnomaly();
        anomaly.setTitle(tempAnomaly.getTitle());
        anomaly.setRateCount(tempAnomaly.getRateCount() + acc1.getRateCount());
        anomaly.setRateSum(tempAnomaly.getRateSum() + acc1.getRateSum());

        return anomaly;
    }
}
