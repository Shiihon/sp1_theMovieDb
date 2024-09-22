package app.services;

import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
                new GenreDTO(12, "Adventure"),
                new GenreDTO(28, "Action"),
                new GenreDTO(14, "Fantasy")
        );

        movieDTO = new MovieDTO(
                120,
                "The Lord of the Rings: The Fellowship of the Ring",
                "Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle Bilbo, must leave his home in order to keep it from falling into the hands of its evil creator. Along the way, a fellowship is formed to protect the ringbearer and make sure that the ring arrives at its final destination: Mt. Doom, the only place where it can be destroyed.",
                174.601,
                LocalDate.of(2001, 12, 18),
                8.4,
                Set.of(genreDTOs.get(0).getId(), genreDTOs.get(1).getId(), genreDTOs.get(2).getId()),
                null
        );
    }

    @Test
    void testGetCastMembersByMovieId() {
        int expected = 123;
        int actual = movieService.getCastMembersByMovieId(120).size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetMoviesByCountry() {
        int expected = 1307; // For danish movies.
        int actual = movieService.getMoviesByCountry("DK").size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetMoviesByCountryWithCast() {
        int expected = 1307; // For danish movies.
        int actual = movieService.getMoviesByCountryWithCast("DK").size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMoviesByCountryParallelized() {
        int expected = 1307; // For danish movies.
        int actual = movieService.getMoviesByCountryParallelized("DK").size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMoviesByCountryParallelizedWithCast() {
        int expected = 1307; // For danish movies.
        int actual = movieService.getMoviesByCountryParallelizedWithCast("DK").size();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetMovieById() {
        MovieDTO expected = movieDTO;
        MovieDTO actual = movieService.getMovieById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetAllGenres() {
        int expectedSize = 19;

        Set<GenreDTO> actualGenreDTOs = movieService.getAllGenres();

        Assertions.assertEquals(expectedSize, actualGenreDTOs.size());
        Assertions.assertTrue(actualGenreDTOs.containsAll(genreDTOs));
    }
}