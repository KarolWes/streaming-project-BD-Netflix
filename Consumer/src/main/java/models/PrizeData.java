package models;

public class PrizeData {
    private String date;
    private Integer movieId;
    private Integer userId;
    private Integer rate;

    public PrizeData() {
        this("", -1, -1, 0);
    }

    public PrizeData(String date, Integer movieId, Integer userId, Integer rate) {
        this.date = date;
        this.movieId = movieId;
        this.userId = userId;
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "PrizeData{" +
                "date='" + date + '\'' +
                ", film_id=" + movieId +
                ", user_id=" + userId +
                ", rate=" + rate +
                '}';
    }
}
