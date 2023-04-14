package com.pool.domine;

import lombok.*;

import java.io.Serializable;

@Entity
@Table(name="TBL_TITLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TitleEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long titleId;
    private String movieId;
    @Column(columnDefinition = "text")
    private String title;
    private String type;
    //@Column(columnDefinition = "text")
    //private String description;
    private String releaseYear;
    private String ageCertification;
    private String runTime;
    @Column(columnDefinition = "text")
    private String genres;
    private String productionCountries;
    private String seasons;
    private String imdbId;
    private String imdbScore;
    private String imdbVotes;
    private String tmdbPopularity;
    private String tmdbScore;

}
