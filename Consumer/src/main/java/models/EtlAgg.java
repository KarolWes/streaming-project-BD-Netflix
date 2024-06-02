package models;


public class EtlAgg {
    private Integer filmId;
    private String title;
    private String date;
    private Long rateCount;
    private Long rateSum;
    private Long reviewerCount;

    public EtlAgg() {
        this(-1, "", "", 0L,  0L, 0L);
    }

    public EtlAgg(Integer filmId, String title, String date, Long rateCount, Long rateSum, Long reviewerCount) {
        this.filmId = filmId;
        this.title = title;
        this.date = date;
        this.rateCount = rateCount;
        this.rateSum = rateSum;
        this.reviewerCount = reviewerCount;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Long getReviewerCount() {
        return reviewerCount;
    }

    public void setReviewerCount(Long reviewerCount) {
        this.reviewerCount = reviewerCount;
    }

    @Override
    public String toString() {
        return "etlAgg{" +
                "film_id=" + filmId +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", rate_count=" + rateCount +
                ", rate_sum=" + rateSum +
                ", reviewer_count=" + reviewerCount +
                '}';
    }
}
