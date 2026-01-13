package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.Stock;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import BudgetPlanner.BudgetPlanner.Service.AlphaVantageService;
import BudgetPlanner.BudgetPlanner.Service.StockService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class StockController {

    private final StockService stockService;
    private final AlphaVantageService alphaVantageService;
    private final UserRepository userRepository;

    public StockController(
            StockService stockService,
            AlphaVantageService alphaVantageService,
            UserRepository userRepository
    ) {
        this.stockService = stockService;
        this.alphaVantageService = alphaVantageService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Stock> getUserStocks(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return stockService.getStocksForUser(user);
    }

    @PostMapping
    public Stock addStock(@RequestBody Stock stock, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        stock.setUser(user);
        return stockService.saveStock(stock);
    }

    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        stockService.deleteStockForUser(id, user);
    }

    @DeleteMapping
    public void clearPortfolio(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        stockService.clearPortfolioForUser(user);
    }

    @GetMapping("/quote/{symbol}")
    public ResponseEntity<JsonNode> getQuote(@PathVariable String symbol) {
        JsonNode node = alphaVantageService.getQuote(symbol);

        if (isError(node)) {
            return ResponseEntity.status(statusFromMessage(node)).body(node);
        }
        return ResponseEntity.ok(node);
    }

    @GetMapping("/chart/{symbol}")
    public ResponseEntity<JsonNode> getChart(@PathVariable String symbol) {
        JsonNode node = alphaVantageService.getChart(symbol);

        if (isError(node)) {
            return ResponseEntity.status(statusFromMessage(node)).body(node);
        }
        return ResponseEntity.ok(node);
    }

    /**
     * OPTIONAL (aber sehr empfohlen):
     * /api/stocks/search/BMW -> liefert mögliche Symbole inkl. Region/Währung
     */
    @GetMapping("/search/{keywords}")
    public ResponseEntity<JsonNode> search(@PathVariable String keywords) {
        JsonNode node = alphaVantageService.searchSymbols(keywords);

        if (isError(node)) {
            return ResponseEntity.status(statusFromMessage(node)).body(node);
        }
        return ResponseEntity.ok(node);
    }

    private boolean isError(JsonNode node) {
        return node != null && node.has("s") && "error".equalsIgnoreCase(node.path("s").asText());
    }

    private HttpStatus statusFromMessage(JsonNode node) {
        String msg = node.path("message").asText("").toLowerCase();
        if (msg.contains("limit")) return HttpStatus.TOO_MANY_REQUESTS; // 429
        if (msg.contains("invalid")) return HttpStatus.BAD_REQUEST;     // 400
        if (msg.contains("no ")) return HttpStatus.NOT_FOUND;           // 404
        return HttpStatus.BAD_GATEWAY;                                  // 502
    }
}
