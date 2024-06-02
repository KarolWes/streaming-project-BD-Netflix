package models;

public class MovieData {
    private Integer id;
    private Integer year;
    private String title;

    public MovieData() {
        this(-1, -1, "");
    }

    public MovieData(Integer id, Integer year, String title) {
        this.id = id;
        this.year = year;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MovieData{" +
                "id=" + id +
                ", year=" + year +
                ", title='" + title + '\'' +
                '}';
    }
}
