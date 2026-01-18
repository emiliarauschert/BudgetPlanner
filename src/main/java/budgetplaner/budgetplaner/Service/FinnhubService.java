package budgetplaner.budgetplaner.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FinnhubService {

    @Value("${finnhub.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Finnhub Quote -> in deinem Format:
     * Erfolg: { s:"ok", c,h,l,o,pc }
     * Fehler: { s:"error", message:"..." }
     */
    public JsonNode getQuote(String symbol) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://finnhub.io/api/v1/quote")
                .queryParam("symbol", symbol)
                .toUriString();

        try {
            JsonNode root = getJsonWithToken(url);

            // Finnhub liefert bei ungültig/leer oft 0-Werte. Wir prüfen "c".
            double c = root.path("c").asDouble(0);
            double h = root.path("h").asDouble(0);
            double l = root.path("l").asDouble(0);
            double o = root.path("o").asDouble(0);
            double pc = root.path("pc").asDouble(0);

            if (c == 0 && h == 0 && l == 0 && o == 0 && pc == 0) {
                return errorNode("No quote data (Symbol falsch oder keine Daten)");
            }

            ObjectNode out = mapper.createObjectNode();
            out.put("s", "ok");
            out.put("c", c);
            out.put("h", h);
            out.put("l", l);
            out.put("o", o);
            out.put("pc", pc);
            return out;

        } catch (Exception e) {
            return errorNode("Quote request failed: " + safeMsg(e));
        }
    }

    /**
     * Batch Quotes: /quotes?symbols=AAPL,MSFT
     * Erfolg: { s:"ok", data:{ "AAPL":{...}, "MSFT":{...} } }
     */
    public JsonNode getQuotes(String[] symbols) {
        try {
            ObjectNode data = mapper.createObjectNode();

            for (String raw : symbols) {
                String sym = (raw == null) ? "" : raw.trim().toUpperCase();
                if (sym.isBlank()) continue;

                JsonNode q = getQuote(sym);
                if (q != null && q.has("s") && "ok".equalsIgnoreCase(q.path("s").asText())) {
                    data.set(sym, q);
                }
            }

            ObjectNode out = mapper.createObjectNode();
            out.put("s", "ok");
            out.set("data", data);
            return out;

        } catch (Exception e) {
            return errorNode("Batch quote request failed: " + safeMsg(e));
        }
    }

    /**
     * Finnhub Symbol Search:
     * GET https://finnhub.io/api/v1/search?q=apple
     * Erfolg: { s:"ok", matches:[{symbol,description,type}...] }
     */
    public JsonNode searchSymbols(String keywords) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://finnhub.io/api/v1/search")
                .queryParam("q", keywords)
                .toUriString();

        try {
            JsonNode root = getJsonWithToken(url);
            JsonNode results = root.path("result");

            if (!results.isArray() || results.size() == 0) {
                return errorNode("No matches");
            }

            // Ausgabe in deinem Frontend-Format (matches)
            ObjectNode out = mapper.createObjectNode();
            out.put("s", "ok");

            var matches = mapper.createArrayNode();
            for (JsonNode r : results) {
                ObjectNode one = mapper.createObjectNode();
                one.put("symbol", r.path("symbol").asText(""));
                one.put("name", r.path("description").asText(""));
                one.put("type", r.path("type").asText(""));
                matches.add(one);
            }
            out.set("matches", matches);
            return out;

        } catch (Exception e) {
            return errorNode("Search request failed: " + safeMsg(e));
        }
    }

    // ---- helpers ----

    private JsonNode getJsonWithToken(String url) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Finnhub-Token", apiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            return mapper.readTree(resp.getBody());
        }
        throw new RuntimeException("HTTP " + resp.getStatusCode().value());
    }

    private ObjectNode errorNode(String msg) {
        ObjectNode out = mapper.createObjectNode();
        out.put("s", "error");
        out.put("message", msg);
        return out;
    }

    private String safeMsg(Exception e) {
        String m = e.getMessage();
        return (m == null || m.isBlank()) ? e.getClass().getSimpleName() : m;
    }
}
