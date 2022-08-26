package com.project.watcha.domain.movie.dto.response;

import com.project.watcha.domain.movie.VideoPeople;
import com.project.watcha.domain.movie.enumType.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class VideoResponseDto {

    private String title;
    private String summary;
    private List<VideoPeople> videoPeople;
    private int spector;
    private List<Genre> genre;
    private String image_url;
}
