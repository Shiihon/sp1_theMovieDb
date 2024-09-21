package app.services;

import app.dtos.CastMemberDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieService {

    private final String BASE_URL = "https://api.themoviedb.org/3";
    private final String API_TOKEN = System.getenv("api_token");

    private final ObjectMapper objectMapper;

    public MovieService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Set<CastMemberDTO> getCastMembersByMovieId(Integer id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/movie/" + id + "/credits"))
                    .header("Accept", "Application/json")
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                CastMemberDTO[] castMembers = objectMapper.treeToValue(json.get("cast"), CastMemberDTO[].class);
                CastMemberDTO[] crewMembers = objectMapper.treeToValue(json.get("crew"), CastMemberDTO[].class);

                Set<CastMemberDTO> castMembersSet = Stream.concat(Stream.of(castMembers), Stream.of(crewMembers)).collect(Collectors.toSet());

                if (!castMembersSet.isEmpty()) {
                    return castMembersSet.stream().filter(c -> c.getRole().equals("Acting") || c.getJob() != null && c.getJob().equals("Director")).collect(Collectors.toSet());
                } else {
                    return castMembersSet;
                }

            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Set<MovieDTO> getMoviesByCountry(String country) {
        try {
            Set<MovieDTO> movies = new HashSet<>();

            LocalDate endDate = LocalDate.of(2024, 1, 1);
            LocalDate startDate = endDate.minusYears(5);

            int currentPage = 1;
            int totalPages = 0;

            do {
                StringBuilder builder = new StringBuilder(BASE_URL)
                        .append("/discover/movie?include_adult=true&include_video=false&language=en-US&page=")
                        .append(currentPage)
                        .append("&release_date.gte=")
                        .append(startDate)
                        .append(("&primary_release_date.lte="))
                        .append(endDate)
                        .append("&sort_by=popularity.desc&with_origin_country=")
                        .append(country);

                HttpRequest request = HttpRequest.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + API_TOKEN)
                        .uri(URI.create(builder.toString()))
                        .GET()
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonNode json = objectMapper.readTree(response.body());

                    totalPages = json.get("total_pages").asInt();
                    currentPage++;

                    JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, MovieDTO.class);
                    movies.addAll(objectMapper.treeToValue(json.get("results"), type));
                } else {
                    System.out.println("GET request failed. Status code: " + response.statusCode());
                }
            } while (currentPage <= totalPages);

            return movies;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Set<MovieDTO> getMoviesByCountryWithCast(String country) {
        Set<MovieDTO> movies = getMoviesByCountry(country);

        movies.forEach(movie -> movie.setCast(getCastMembersByMovieId(movie.getId())));

        return movies;
    }

    public Set<MovieDTO> getMoviesByCountryParallelized(String country) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);

        try {
            LocalDate endDate = LocalDate.of(2024, 1, 1);
            LocalDate startDate = endDate.minusYears(5);

            JsonNode json = getMoviesByCountryJSON(country, startDate, endDate, 1);
            int totalPages = json.get("total_pages").asInt();
            int currentPage = json.get("page").asInt();

            List<Future<Set<MovieDTO>>> futures = new ArrayList<>();
            futures.add(executorService.submit(() -> getMoviesByCountry(json))); // Extract the first page results

            for (int page = currentPage + 1; page <= totalPages; page++) {
                int finalPage = page; // page must be effectively final to be passed into callable task bellow

                Callable<Set<MovieDTO>> task = () -> {
                    JsonNode innerJson = getMoviesByCountryJSON(country, startDate, endDate, finalPage);
                    return getMoviesByCountry(innerJson);
                };

                futures.add(executorService.submit(task));
            }

            Set<MovieDTO> movies = new HashSet<>();

            for (Future<Set<MovieDTO>> future : futures) {
                try {
                    movies.addAll(future.get()); // This will block until the task is complete
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            return movies;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return null;
    }

    public Set<MovieDTO> getMoviesByCountryParallelizedWithCast(String country) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);

        Set<MovieDTO> movies = getMoviesByCountryParallelized(country);
        List<Future<Void>> futures = new ArrayList<>();

        movies.forEach(movie -> {
            Callable<Void> task = () -> {
                movie.setCast(getCastMembersByMovieId(movie.getId()));
                return null;
            };

            futures.add(executorService.submit(task));
        });

        for (Future<Void> future : futures) {
            try {
                future.get(); // This will block until the task is complete
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        return movies;
    }

    private Set<MovieDTO> getMoviesByCountry(JsonNode json) throws IOException {
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(Set.class, MovieDTO.class);
        return objectMapper.treeToValue(json.get("results"), type);
    }

    private JsonNode getMoviesByCountryJSON(String country, LocalDate startDate, LocalDate endDate, int page) throws IOException, InterruptedException {
        StringBuilder builder = new StringBuilder(BASE_URL)
                .append("/discover/movie?include_adult=true&include_video=false&language=en-US&page=")
                .append(page)
                .append("&release_date.gte=")
                .append(startDate)
                .append(("&primary_release_date.lte="))
                .append(endDate)
                .append("&sort_by=popularity.desc&with_origin_country=")
                .append(country);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + API_TOKEN)
                .uri(URI.create(builder.toString()))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readTree(response.body());
        } else {
            throw new IOException("GET request failed. Status code: " + response.statusCode());
        }
    }

    public MovieDTO getMovieById(Integer id) {
        try {
            StringBuilder builder = new StringBuilder(BASE_URL)
                    .append("/movie/")
                    .append(id);

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .uri(URI.create(builder.toString()))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                MovieDTO movieDTO = objectMapper.treeToValue(json, MovieDTO.class);

                json.get("genres").forEach(genre -> movieDTO.addGenreId(genre.get("id").asInt()));

                return movieDTO;
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Set<GenreDTO> getAllGenres() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/genre/movie/list"))
                    .header("Accept", "Application/json")
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                GenreDTO[] genres = objectMapper.treeToValue(json.get("genres"), GenreDTO[].class);

                if (genres.length > 0) {
                    return Arrays.stream(genres).collect(Collectors.toSet());
                }
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
