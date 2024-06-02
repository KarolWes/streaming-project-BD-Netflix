package models;

import java.util.ArrayList;
import java.util.List;

public class TEMPEtl {
    private Integer movieId;
    private String title;
    private String date;
    private Long rateCount;
    private Long rateSum;
    private List<Integer> reviewers;

    public TEMPEtl() { this(-1,"","",0L, 0L, new ArrayList<>());    }

    public TEMPEtl(Integer movieId, String title, String date, Long rateCount, Long rateSum, List<Integer> reviewers) {
        this.movieId = movieId;
        this.title = title;
        this.date = date;
        this.rateCount = rateCount;
        this.rateSum = rateSum;
        this.reviewers = reviewers;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
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

    public List<Integer> getReviewers() {
        return reviewers;
    }

    public void setReviewers(List<Integer> reviewers) {
        this.reviewers = reviewers;
    }

    public void addReviewer(Integer reviewer) {
        this.reviewers.add(reviewer);
    }
    public void addAllReviewers(List<Integer> reviewers) {
        this.reviewers.addAll(reviewers);
    }
}
