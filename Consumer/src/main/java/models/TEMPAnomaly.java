package models;

public class TEMPAnomaly {
    private String windowStart;
    private String windowEnd;
    private String title;
    private Long rateCount;
    private Long rateSum;

    public TEMPAnomaly() {
        this("", "", "", 0L, 0L);
    }

    public TEMPAnomaly(String windowStart, String windowEnd, String title, Long rateCount, Long rateSum) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.title = title;
        this.rateCount = rateCount;
        this.rateSum = rateSum;
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

    public Long getRateSum() {
        return rateSum;
    }

    public void setRateSum(Long rateSum) {
        this.rateSum = rateSum;
    }
}
