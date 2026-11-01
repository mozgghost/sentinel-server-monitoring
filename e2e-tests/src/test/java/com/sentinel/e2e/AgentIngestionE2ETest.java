package com.sentinel.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AgentIngestionE2ETest {

    private static final String BASE_URL =
            System.getenv().getOrDefault("SENTINEL_BASE_URL", "http://localhost:8081");

    private static final String TOKEN =
            System.getenv().getOrDefault("SENTINEL_AGENT_TOKEN", "local-dev-token");

    private static final Path AGENT_JAR = Path.of(
            System.getenv().getOrDefault(
                    "AGENT_JAR_PATH",
                    "monitoring-agent/target/sentinel-monitoring-agent-0.1.0-jar-with-dependencies.jar"
            )
    );

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Test
    void agentShouldSendMetricsAndBackendShouldPersistThem() throws Exception {
        assertTrue(Files.isRegularFile(AGENT_JAR),
                "Agent JAR not found: " + AGENT_JAR.toAbsolutePath());

        String serverName = "e2e-" + UUID.randomUUID();
        String createBody = JSON.createObjectNode()
                .put("name", serverName)
                .put("host", "127.0.0.1")
                .put("environment", "e2e")
                .toString();

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/servers"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createBody))
                .build();

        HttpResponse<String> createResponse =
                HTTP.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, createResponse.statusCode(),
                "Could not create test server: " + createResponse.body());

        UUID serverId = UUID.fromString(
                JSON.readTree(createResponse.body()).path("id").asText()
        );

        Process agent = null;
        CompletableFuture<String> agentOutput = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java", "-jar", AGENT_JAR.toAbsolutePath().toString()
            );
            processBuilder.redirectErrorStream(true);
            processBuilder.environment().put("SENTINEL_SERVER_ID", serverId.toString());
            processBuilder.environment().put("SENTINEL_API_URL",
                    BASE_URL + "/api/metrics/ingest");
            processBuilder.environment().put("SENTINEL_AGENT_TOKEN", TOKEN);

            agent = processBuilder.start();

            Process runningAgent = agent;
            agentOutput = CompletableFuture.supplyAsync(() -> {
                try {
                    return new String(
                            runningAgent.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8
                    );
                } catch (Exception e) {
                    return "Could not read agent output: " + e.getMessage();
                }
            });

            Instant deadline = Instant.now().plusSeconds(45);
            JsonNode receivedMetric = null;

            while (Instant.now().isBefore(deadline)) {
                HttpRequest historyRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/servers/" + serverId + "/metrics"))
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();

                HttpResponse<String> historyResponse =
                        HTTP.send(historyRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, historyResponse.statusCode(),
                        "Metric history request failed: " + historyResponse.body());

                JsonNode history = JSON.readTree(historyResponse.body());
                if (history.isArray() && !history.isEmpty()) {
                    receivedMetric = history.get(history.size() - 1);
                    break;
                }

                Thread.sleep(1000);
            }

            assertNotNull(receivedMetric,
                    "No metrics received from the agent within 45 seconds");

            double cpu = receivedMetric.path("cpuPercent").asDouble(Double.NaN);
            double memory = receivedMetric.path("memoryPercent").asDouble(Double.NaN);
            String recordedAt = receivedMetric.path("recordedAt").asText("");

            assertTrue(Double.isFinite(cpu) && cpu >= 0 && cpu <= 100,
                    "CPU value is outside the expected range: " + cpu);
            assertTrue(Double.isFinite(memory) && memory >= 0 && memory <= 100,
                    "Memory value is outside the expected range: " + memory);
            assertDoesNotThrow(() -> Instant.parse(recordedAt),
                    "Metric timestamp is not a valid ISO-8601 instant");

        } finally {
            if (agent != null && agent.isAlive()) {
                agent.destroy();
                if (!agent.waitFor(3, TimeUnit.SECONDS)) {
                    agent.destroyForcibly();
                    agent.waitFor(3, TimeUnit.SECONDS);
                }
            }
        }

        assertNotNull(agentOutput);
        String output = agentOutput.get(5, TimeUnit.SECONDS);
        assertTrue(output.contains("HTTP 202"),
                "Agent did not report HTTP 202. Output: " + output);
    }

    @Test
    void ingestShouldRejectAnInvalidToken() throws Exception {
        String body = JSON.createObjectNode()
                .put("serverId", UUID.randomUUID().toString())
                .put("cpuPercent", 10.0)
                .put("memoryPercent", 20.0)
                .put("recordedAt", Instant.now().toString())
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/metrics/ingest"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("X-Agent-Token", "invalid-e2e-token")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                HTTP.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode(),
                "Backend should reject an invalid agent token");
    }
}
