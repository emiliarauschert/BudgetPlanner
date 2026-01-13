package BudgetPlanner.BudgetPlanner.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class AlphaVantageService {

    @Value("${alpha.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Liefert Quote im Finnhub-ähnlichen Format:
     * Erfolg: { s:"ok", c, h, l, o, pc }
     * Fehler: { s:"error", message:"..." }
     */
    public JsonNode getQuote(String symbol) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.alphavantage.co/query")
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();

        try {
            String raw = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(raw);

            // Rate limit / invalid / other messages
            JsonNode err = extractAlphaVantageError(root);
            if (err != null) return err;

            JsonNode q = root.path("Global Quote");
            if (q.isMissingNode() || q.size() == 0) {
                return errorNode("No quote data (Symbol falsch oder keine Daten)");
            }

            double open = q.path("02. open").asDouble(0);
            double high = q.path("03. high").asDouble(0);
            double low  = q.path("04. low").asDouble(0);
            double price = q.path("05. price").asDouble(0);
            double prevClose = q.path("08. previous close").asDouble(0);

            ObjectNode out = mapper.createObjectNode();
            out.put("s", "ok");
            out.put("o", open);
            out.put("h", high);
            out.put("l", low);
            out.put("c", price);
            out.put("pc", prevClose);

            return out;

        } catch (Exception e) {
            return errorNode("Quote request failed: " + safeMsg(e));
        }
    }

    /**
     * Liefert Chart im Finnhub Candle-ähnlichen Format:
     * Erfolg: { s:"ok", t:[unixSeconds], c:[closePrices] }
     * Fehler: { s:"error", message:"..." }
     */
    public JsonNode getChart(String symbol) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.alphavantage.co/query")
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", symbol)
                .queryParam("outputsize", "compact")
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();

        try {
            String raw = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(raw);

            JsonNode err = extractAlphaVantageError(root);
            if (err != null) return err;

            JsonNode series = root.path("Time Series (Daily)");
            if (series.isMissingNode() || series.size() == 0) {
                return errorNode("No chart data (Symbol falsch oder keine Daten)");
            }

            List<Long> t = new ArrayList<>();
            List<Double> c = new ArrayList<>();

            List<String> dates = new ArrayList<>();
            series.fieldNames().forEachRemaining(dates::add);
            dates.sort(Comparator.naturalOrder());

            int start = Math.max(0, dates.size() - 30);
            for (int i = start; i < dates.size(); i++) {
                String dateStr = dates.get(i);
                JsonNode day = series.get(dateStr);

                double close = day.path("4. close").asDouble(0);
                long unix = LocalDate.parse(dateStr)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond();

                t.add(unix);
                c.add(close);
            }

            ObjectNode out = mapper.createObjectNode();
            out.put("s", "ok");
            out.set("t", mapper.valueToTree(t));
            out.set("c", mapper.valueToTree(c));
            return out;

        } catch (Exception e) {
            return errorNode("Chart request failed: " + safeMsg(e));
        }
    }

    /**
     * OPTIONAL aber sehr hilfreich:
     * Symbolsuche (damit BMW/SAP etc. korrekt gefunden werden)
     * Erfolg: { s:"ok", matches:[ {symbol,name,region,currency,matchScore} ... ] }
     * Fehler: { s:"error", message:"..." }
     */
    public JsonNode searchSymbols(String keywords) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.alphavantage.co/query")
                .queryParam("function", "SYMBOL_SEARCH")
                .queryParam("keywords", keywords)
                .queryParam("apikey", apiKey)
                .build()
                .toUriString();

        try {
            String raw = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(raw);

            JsonNode err = extractAlphaVantageError(root);
            if (err != null) return err;

            JsonNode best = root.path("bestMatches");
            if (best.isMissingNode() || !best.isArray() || best.size() == 0) {
                return errorNode("No matches");
            }

            List<Map<String, Object>> matches = new ArrayList<>();
            for (JsonNode m : best) {
                Map<String, Object> one = new LinkedHashMap<>();
                one.put("symbol", m.path("1. symbol").asText(""));
                one.put("name", m.path("2. name").asText(""));
                one.put("region", m.path("4. region").asText(""));
                one.put("currency", m.path("8. currency").asText(""));
                one.put("matchScore", m.path("9. matchScore").asText(""));
                matches.add(one);
            }

            ObjectNode out = mapper.createObjectNode();
            out.put("s", "ok");
            out.set("matches", mapper.valueToTree(matches));
            return out;

        } catch (Exception e) {
            return errorNode("Search request failed: " + safeMsg(e));
        }
    }

    // -------- helpers --------

    private ObjectNode errorNode(String msg) {
        ObjectNode out = mapper.createObjectNode();
        out.put("s", "error");
        out.put("message", msg);
        return out;
    }

    /**
     * AlphaVantage liefert Fehler häufig als:
     * - { "Note": "..." } (Rate Limit)
     * - { "Error Message": "..." }
     * - { "Information": "..." }
     */
    private JsonNode extractAlphaVantageError(JsonNode root) {
        if (root == null || root.isMissingNode()) return errorNode("Empty response");

        if (root.has("Note")) {
            return errorNode("API limit reached (Rate Limit). Warte kurz und versuche es erneut.");
        }
        if (root.has("Error Message")) {
            return errorNode("Invalid symbol / request: " + root.path("Error Message").asText(""));
        }
        if (root.has("Information")) {
            return errorNode(root.path("Information").asText("API returned Information"));
        }
        return null;
    }

    private String safeMsg(Exception e) {
        String m = e.getMessage();
        return (m == null || m.isBlank()) ? e.getClass().getSimpleName() : m;
    }
}
