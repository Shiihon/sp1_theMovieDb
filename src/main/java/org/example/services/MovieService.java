package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.MovieDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MovieService {

    private final ObjectMapper objectMapper;

    public MovieService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MovieDTO getMovieById(int id) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            StringBuilder builder = new StringBuilder("https://api.themoviedb.org/3/movie/")
                    .append(id)
                    .append("?api_key=")
                    .append(System.getenv("api_key"));

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .header("Accept", "application/json")
                    .uri(URI.create(builder.toString()))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                MovieDTO movieDTO = objectMapper.readValue(response.body(), MovieDTO.class);
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
