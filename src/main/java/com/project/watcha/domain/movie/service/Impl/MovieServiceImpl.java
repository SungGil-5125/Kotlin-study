package com.project.watcha.domain.movie.service.Impl;

import com.project.watcha.domain.actor.Actor;
import com.project.watcha.domain.actor.repository.ActorRepository;
import com.project.watcha.domain.director.Director;
import com.project.watcha.domain.director.repository.DirectorRepository;
import com.project.watcha.domain.movie.Movie;
import com.project.watcha.domain.movie.dto.request.UploadMovieDto;
import com.project.watcha.domain.movie.repository.MovieRepository;
import com.project.watcha.domain.movie.service.MovieService;
import com.project.watcha.domain.movie.service.S3Service;
import com.project.watcha.global.exception.ErrorCode;
import com.project.watcha.global.exception.exceptions.ActorNotFoundException;
import com.project.watcha.global.exception.exceptions.DirectorNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.project.watcha.global.exception.ErrorCode.ACTOR_NOT_FOUND;
import static com.project.watcha.global.exception.ErrorCode.DIRECTOR_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final S3Service s3Service;
    private final DirectorRepository directorRepository;
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    @Value("${cloud.aws.s3.url}")
    private String url;

    @Override
    public Long uploadMovie(UploadMovieDto uploadMovieDto, MultipartFile imageFile, MultipartFile movieFile) {

        List<String> directorName = uploadMovieDto.getDirectorName();
        List<Director> directors = new ArrayList<>();
        List<String> actorName = uploadMovieDto.getActorName();
        List<Actor> actors = new ArrayList<>();

        directorName.forEach(Director ->{
            Director director = directorRepository.findByName(Director);
            if(director == null) {
                throw new DirectorNotFoundException("등록되지 않은 감독 입니다.", DIRECTOR_NOT_FOUND);
            }
            directors.add(director);
        });

        actorName.forEach(Actor -> {
            Actor actor = actorRepository.findByName(Actor);
            if(actor == null) {
                throw new ActorNotFoundException("등록되지 않은 배우 입니다.", ACTOR_NOT_FOUND);
            }
            actors.add(actor);
        });

        String uploadImage = s3Service.upload(imageFile, "movie_image/");
        String uploadMovie = s3Service.upload(movieFile, "movie/");

        Movie movie = uploadMovieDto.toEntity(directors, actors, url + "movie_image/" + uploadImage, url + "movie/" + uploadMovie);
        return movieRepository.save(movie).getMovie_id();
    }
}
