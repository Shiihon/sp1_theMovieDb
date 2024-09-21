package app;

import app.config.HibernateConfig;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

public class Main {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("movie_database");
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static MovieService movieService = new MovieService(objectMapper);
    private static MovieDAO movieDAO = new MovieDAO(emf);

    public static void main(String[] args) {
        objectMapper.registerModule(new JavaTimeModule());

        //Opgave 1
        //loadGenres();
        //saveAllMoviesInDB("DK");

        //Opgave 2
        //displayAllMoviesInDB();

        //Opgave 3
        //displayAllMoviesInDBWithDetails();

        //Opgave 4

        //Display all genres
        //displayAllGenres();

        //Display all movies within a genre
        //displayMoviesByGenre(35L);

        //Opgave 6
        //displayMovieByTitle("fra");

        //Opgave 7
        //displayTopTenHighest();
        //displayTopTenLowest();
        //displayTopTenMostPopular();
    }

    public static void loadGenres() {
        GenreDAO genreDAO = new GenreDAO(emf);
        Set<GenreDTO> genreDTOs = movieService.getAllGenres();

        genreDTOs.forEach(genre -> genreDAO.create(genre));
    }

    public static void saveAllMoviesInDB(String country) {
        MovieDAO movieDAO = new MovieDAO(emf);
        Set<MovieDTO> movies = movieService.getMoviesByCountryParallelizedWithCast(country);

        movies.forEach(movie -> movieDAO.create(movie));
    }

    public static void displayAllMoviesInDB() {
        Set<MovieDTO> movies = movieDAO.getAll();
        System.out.println(movies.size());
        movies.forEach(movie -> System.out.printf("%s%n%n", movie));
    }

    public static void displayAllMoviesInDBWithDetails() {
        Set<MovieDTO> movies = movieDAO.getAllWithDetails();
        System.out.println(movies.size());
        movies.forEach(movie -> System.out.printf("%s%n%n", movie));
    }

    public static void displayAllGenres() {
        GenreDAO genreDAO = new GenreDAO(emf);
        Set<GenreDTO> genresInDB = genreDAO.getAll();
        System.out.println(genresInDB.size());
        genresInDB.forEach(genre -> System.out.printf("%s%n%n", genre));
    }

    public static void displayMoviesByGenre(Long genreId) {
        Set<MovieDTO> movies = movieDAO.getMoviesWithinGenre(genreId);
        System.out.println(movies.size());
        movies.forEach(movie -> System.out.printf("%s%n%n", movie));
    }

    public static void displayMovieByTitle(String originalTitle) {
        Set<MovieDTO> movies = movieDAO.getMovieByTitle(originalTitle);
        System.out.println(movies.size());
        movies.forEach(movie -> System.out.printf("%s%n%n", movie));
    }

    public static void displayTopTenHighest() {
        Set<MovieDTO> movies = movieDAO.getTopTenAverage(false);
        System.out.println(movies.size());
        movies.forEach(movie -> System.out.printf("%s%n%n", movie));
    }

    public static void displayTopTenLowest() {
        Set<MovieDTO> movies = movieDAO.getTopTenAverage(true);
        System.out.println(movies.size());
        movies.forEach(movie -> System.out.printf("%s%n%n", movie));
    }

    public static void displayTopTenMostPopular() {
        Set<MovieDTO> movies = movieDAO.getTopTenMostPopular();
        System.out.println(movies.size());
        movies.forEach(movie -> System.out.printf("%s%n%n", movie));
    }
}