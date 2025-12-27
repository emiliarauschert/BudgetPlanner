package BudgetPlanner.BudgetPlanner.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FinnhubService {

    @Value("${finnhub.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getQuote(String symbol) {
        String url =
                "https://finnhub.io/api/v1/quote?symbol=" +
                        symbol + "&token=" + apiKey;
        return restTemplate.getForObject(url, String.class);
    }

    public String getChart(String symbol) {
        long now = System.currentTimeMillis() / 1000;
        long from = now - 60 * 60 * 24 * 30;

        String url =
                "https://finnhub.io/api/v1/stock/candle" +
                        "?symbol=" + symbol +
                        "&resolution=D" +
                        "&from=" + from +
                        "&to=" + now +
                        "&token=" + apiKey;

        return restTemplate.getForObject(url, String.class);
    }
}

