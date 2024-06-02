package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CombinedData {
    private String date;
    private Integer movieId;
    private String title;
    private Integer year;
    private Integer userId;
    private Integer rate;
    public String key;

    public CombinedData() throws ParseException {
        this("1900-01-01", -1, "", 0, -1, 0);
    }

    public CombinedData(String date, Integer movieId, String title, Integer year, Integer userId, Integer rate) throws ParseException {
        this.date = date;
        this.movieId = movieId;
        this.title = title;
        this.year = year;
        this.userId = userId;
        this.rate = rate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = format.parse(date);
        this.key = d.getYear() + String.valueOf(d.getMonth());
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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
}
