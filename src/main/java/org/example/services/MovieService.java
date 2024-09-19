package org.example.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.CastMemberDTO;
import org.example.dtos.GenreDTO;
import org.example.dtos.MovieDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieService {

    private final String BASE_URL = "https://api.themoviedb.org/3";
    private final String API_TOKEN = System.getenv("api_token");

    private final ObjectMapper objectMapper;

    public MovieService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Set<CastMemberDTO> getCastMembersByMovieId(int id) {
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
                    System.out.println("No information for cast members found.");
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
            HttpClient httpClient = HttpClient.newHttpClient();
            Set<MovieDTO> movies = new HashSet<>();

            LocalDate endYear = LocalDate.of(2024, 1, 1);
            LocalDate startYear = endYear.minusYears(5);

            int currentPage = 1;
            int totalPages = 0;

            do {
                StringBuilder builder = new StringBuilder(BASE_URL)
                        .append("/discover/movie?include_adult=true&include_video=false&language=en-US&page=")
                        .append(currentPage)
                        .append("&release_date.gte=")
                        .append(startYear)
                        .append(("&primary_release_date.lte="))
                        .append(endYear)
                        .append("&sort_by=popularity.desc&with_origin_country=")
                        .append(country);

                HttpRequest request = HttpRequest.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + API_TOKEN)
                        .uri(URI.create(builder.toString()))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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
        } catch (IOException | InterruptedException | RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MovieDTO getMovieById(int id) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

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

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), MovieDTO.class);
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
