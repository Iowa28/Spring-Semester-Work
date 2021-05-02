package ru.kpfu.aminovniaz.project.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game_info")
public class GameInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "release_date")
    private String releaseDate;
    private String publisher;
    private String developer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInfo gameInfo = (GameInfo) o;
        return Objects.equals(id, gameInfo.id) &&
                Objects.equals(releaseDate, gameInfo.releaseDate) &&
                Objects.equals(publisher, gameInfo.publisher) &&
                Objects.equals(developer, gameInfo.developer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, releaseDate, publisher, developer);
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "id=" + id +
                ", releaseDate='" + releaseDate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", developer='" + developer + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }
}
