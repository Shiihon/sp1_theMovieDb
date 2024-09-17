package org.example.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.CastMemberDTO;
import org.example.dtos.MovieDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieService {

    private final String BASE_URL = "https://api.themoviedb.org/3";
    private final String API_KEY = System.getenv("api_key");

    private final ObjectMapper objectMapper;

    public MovieService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<CastMemberDTO> getCastMembersByMovieId(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/movie/" + id + "/credits"))
                    .header("accept", "Application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                CastMemberDTO[] castMembers = objectMapper.treeToValue(json.get("cast"), CastMemberDTO[].class);

                if (castMembers.length > 0) {
                    return Arrays.stream(castMembers).filter(c -> c.getRole().equals("Directing") || c.getRole().equals("Acting")).toList();
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

    public List<MovieDTO> getMoviesByCountry(String country) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            List<MovieDTO> movies = new ArrayList<>();

            int currentPage = 1;
            int totalPages;

            do {
                StringBuilder builder = new StringBuilder(BASE_URL)
                        .append("/discover/movie?include_adult=true&include_video=false&language=en-US&page=1&release_date.gte=")
                        .append(LocalDate.now().minusYears(5))
                        .append("&sort_by=popularity.desc&with_origin_country=")
                        .append(country);

                HttpRequest request = HttpRequest.newBuilder()
                        .header("Authorization", "Bearer " + API_KEY)
                        .header("Accept", "application/json")
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
                    throw new RuntimeException(String.format("Could not get movies from country '%s'.", country));
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
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
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
}
