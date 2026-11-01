
package com.sentinel.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MonitoringAgent {

    public static void main(String[] args) throws Exception {
        String serverId = env("SENTINEL_SERVER_ID", "");
        String api = env(
                "SENTINEL_API_URL",
                "http://localhost:8081/api/metrics/ingest");
        String token = env("SENTINEL_AGENT_TOKEN", "local-dev-token");

        if (serverId.isBlank()) {
            throw new IllegalArgumentException(
                    "Set SENTINEL_SERVER_ID to an existing server UUID");
        }

        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor cpu = systemInfo.getHardware().getProcessor();
        GlobalMemory memory = systemInfo.getHardware().getMemory();

        long[] ticks = cpu.getSystemCpuLoadTicks();

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        while (true) {
            double cpuPercent = cpu.getSystemCpuLoadBetweenTicks(ticks) * 100.0;

            ticks = cpu.getSystemCpuLoadTicks();

            double memoryPercent = (1.0 - (double) memory.getAvailable()
                    / (double) memory.getTotal()) * 100.0;

            Map<String, Object> body = Map.of(
                    "serverId", UUID.fromString(serverId),
                    "cpuPercent", round(cpuPercent),
                    "memoryPercent", round(memoryPercent),
                    "recordedAt", Instant.now().toString());

            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder(URI.create(api))
                    .header("Content-Type", "application/json")
                    .header("X-Agent-Token", token)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println(
                    Instant.now()
                            + " CPU=" + round(cpuPercent) + "%"
                            + " RAM=" + round(memoryPercent) + "%"
                            + " -> HTTP " + response.statusCode());

            Thread.sleep(15000);
        }
    }

    private static String env(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }

    private static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
