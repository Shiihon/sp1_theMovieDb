package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.dtos.GenreDTO;
import org.example.dtos.MovieDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class MovieServiceTest {
    private static MovieService movieService;

    private List<GenreDTO> genreDTOs;
    private MovieDTO movieDTO;

    @BeforeAll
    static void beforeAll() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        movieService = new MovieService(objectMapper);
    }

    @BeforeEach
    void setUp() {
        genreDTOs = List.of(
                new GenreDTO(12L, "Adventure"),
                new GenreDTO(14L, "Fantasy"),
                new GenreDTO(28L, "Action")
        );

        movieDTO = new MovieDTO(
                120L,
                "The Lord of the Rings: The Fellowship of the Ring",
                "Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle Bilbo, must leave his home in order to keep it from falling into the hands of its evil creator. Along the way, a fellowship is formed to protect the ringbearer and make sure that the ring arrives at its final destination: Mt. Doom, the only place where it can be destroyed.",
                174.601,
                LocalDate.of(2001, 12, 18),
                8.4,
                List.of(genreDTOs.get(0), genreDTOs.get(1), genreDTOs.get(2)),
                null
        );
    }

    @Test
    void getCastMembersByMovieId() {
        int expected = 123;
        int actual = movieService.getCastMembersByMovieId(120).size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMoviesByCountry() {
        int expected = 1307; // For danish movies.
        int actual = movieService.getMoviesByCountry("DK").size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMovieById() {
        MovieDTO expected = movieDTO;
        MovieDTO actual = movieService.getMovieById(expected.getId().intValue());

        Assertions.assertEquals(expected, actual);
    }
}