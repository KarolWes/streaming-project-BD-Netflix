package utils;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class MonthlyWindowAssigner extends WindowAssigner<Object, TimeWindow> {

    public long milisInDay = 1000L*60*60*24;

    private int numberOfDays(long timestamp){
        Date d = new Date(timestamp);
        int m = d.getMonth();
        int y = d.getYear();
        boolean special = y%4 == 0 && y%100 != 0 || y%400 == 0;
        switch(m){
            case 1:
                if (special) return 29;
                else return 28;
            case 3: case 5: case 8: case 10:
                return 30;
            default:
                return 31;
        }
    }


    @Override
    public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
        long windowSize = numberOfDays(timestamp) * milisInDay;
        Date d = new Date(timestamp);
        long monthStart = timestamp - (d.getDate()-1)*milisInDay;
        return Collections.singletonList(new TimeWindow(monthStart, monthStart + windowSize));
    }

    @Override
    public Trigger<Object, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
        return EventTimeTrigger.create();
    }

    @Override
    public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
        return new TimeWindow.Serializer();
    }

    @Override
    public String toString() {
        return "MonthlyTumblingEventTimeWindows";
    }

    @Override
    public boolean isEventTime() {
        return true;
    }
}
