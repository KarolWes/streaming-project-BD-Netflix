package models;

public class AnomalyData {
    private String windowStart;
    private String windowEnd;
    private String title;
    private Long rateCount;
    private Double rateAvg;

    public AnomalyData() {
    }

    public AnomalyData(Double rateAvg, Long rateCount, String title, String windowEnd, String windowStart) {
        this.rateAvg = rateAvg;
        this.rateCount = rateCount;
        this.title = title;
        this.windowEnd = windowEnd;
        this.windowStart = windowStart;
    }

    public String getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(String windowStart) {
        this.windowStart = windowStart;
    }

    public String getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(String windowEnd) {
        this.windowEnd = windowEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getRateCount() {
        return rateCount;
    }

    public void setRateCount(Long rateCount) {
        this.rateCount = rateCount;
    }

    public Double getRateAvg() {
        return rateAvg;
    }

    public void setRateAvg(Double rateAvg) {
        this.rateAvg = rateAvg;
    }

    @Override
    public String toString() {
        return "AnomalyData{" +
                "windowStart='" + windowStart + '\'' +
                ", windowEnd='" + windowEnd + '\'' +
                ", title='" + title + '\'' +
                ", rateCount=" + rateCount +
                ", rateAvg=" + rateAvg +
                '}';
    }
}
