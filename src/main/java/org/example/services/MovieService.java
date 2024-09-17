package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.CastMemberDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class MovieService {

    private final ObjectMapper objectMapper;
    private final String apiKey = System.getenv("API_KEY");

    public MovieService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<CastMemberDTO> getCastMembersByMovieId(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.themoviedb.org/3/movie/" + id + "/credits"))
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                CastMemberDTO[] castmembers = objectMapper.treeToValue(json.get("cast"), CastMemberDTO[].class);

                if (castmembers.length > 0) {
                    return Arrays.stream(castmembers).filter(c -> c.getRole().equals("Directing") || c.getRole().equals("Acting")).toList();
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
}
