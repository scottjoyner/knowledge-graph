package com.mkv.custom;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Embeddings {

    public static class CustomEmbeddingsResult {

        public final List<Double> embeddings;

        public CustomEmbeddingsResult() {
            this.embeddings = null;
        }

        public CustomEmbeddingsResult(List<Double> embeddings) {
            this.embeddings = embeddings;
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Procedure(name = "com.mkv.custom.embeddings")
    @Description("com.mkv.custom.embeddings('s1') - return vector embeddings for the input text.")
    public Stream<CustomEmbeddingsResult> embeddings(
            @Name("text") String text) throws URISyntaxException, IOException, InterruptedException {
        if (text == null) {
            return null;
        }

        String POST_URL = "http://127.0.0.1:8081/generate_embeddings";
        URI postURI = new URI(POST_URL);
        HttpRequest httpRequestPost = HttpRequest.newBuilder()
                .uri(postURI)
                .POST(HttpRequest.BodyPublishers.ofString(text))
                .header("Content-Type", "text/plain")
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> postResponse = httpClient.send(httpRequestPost, HttpResponse.BodyHandlers.ofString());
        CustomEmbeddingsResult result = objectMapper.readValue(postResponse.body(), CustomEmbeddingsResult.class);
        return Stream.of(new CustomEmbeddingsResult(result.embeddings));
    }
}
