package dev.yashasvi.ragplatform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RagApiIntegrationTest {
    @Value("${local.server.port}")
    private int port;

    @Test
    void statusAndValidationAreAvailableOverHttp() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> status = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/status")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> invalid = client.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/query"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"question\":\"\"}"))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(status.statusCode()).isEqualTo(200);
        assertThat(status.body()).contains("\"status\":\"ready\"").contains("\"vectorStore\":\"memory\"");
        assertThat(invalid.statusCode()).isEqualTo(400);
        assertThat(invalid.body()).contains("Validation failed").contains("question");
    }
}
