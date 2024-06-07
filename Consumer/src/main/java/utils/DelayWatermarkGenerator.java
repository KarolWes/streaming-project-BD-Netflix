package utils;

import models.CombinedData;
import org.apache.flink.api.common.eventtime.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DelayWatermarkGenerator implements WatermarkStrategy<CombinedData> {
    private static final long MAX_DELAY = 1000L*60*60*24; // 1 minute = 60000L
    private long currentMaxTimestamp = Long.MIN_VALUE;

    @Override
    public TimestampAssigner<CombinedData> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
        return new MyTimestampAssigner();
    }

    @Override
    public WatermarkGenerator<CombinedData> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        return new MyWatermarkGenerator();
    }

    private long getTimestamp(String s) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(s);
            return date.getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private class MyTimestampAssigner implements TimestampAssigner<CombinedData> {
        @Override
        public long extractTimestamp(CombinedData cd, long previousElementTimestamp) {
            try
            {
                long timestamp = getTimestamp(cd.getDate());
                currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
                return timestamp;
            }
            catch(Exception ex)
            {
                return 0;
            }
        }
    }

    private class MyWatermarkGenerator implements WatermarkGenerator<CombinedData> {
        public MyWatermarkGenerator() {
        }

        @Override
        public void onEvent(CombinedData cd, long eventTimestamp, WatermarkOutput output) {
            currentMaxTimestamp = Math.max(getTimestamp(cd.getDate()), currentMaxTimestamp);
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            if(currentMaxTimestamp != Long.MIN_VALUE){
                output.emitWatermark(new Watermark(currentMaxTimestamp - MAX_DELAY));
            }
        }
    }
}
