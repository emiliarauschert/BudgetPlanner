package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.Stock;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import BudgetPlanner.BudgetPlanner.Service.FinnhubService;
import BudgetPlanner.BudgetPlanner.Service.StockService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    private final FinnhubService finnhubService;
    private final UserRepository userRepository;

    public StockController (StockService stockService, FinnhubService finnhubService, UserRepository userRepository) {
        this.stockService = stockService;
        this.finnhubService = finnhubService;
        this.userRepository = userRepository;
    }

    //Get
    @GetMapping
    public List<Stock> getUserStocks(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return stockService.getStocksForUser(user);
    }

    // Post
    @PostMapping
    public Stock addStock(@RequestBody Stock stock, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        stock.setUser(user);

        if (stock.getBuyDate() == null) stock.setBuyDate(LocalDate.now());

        if (stock.getSymbol() == null || stock.getSymbol().isBlank()) {
            throw new IllegalArgumentException("symbol fehlt");
        }
        if (stock.getTvSymbol() == null || stock.getTvSymbol().isBlank()) {
            throw new IllegalArgumentException("tvSymbol fehlt");
        }

        return stockService.saveStock(stock);
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        stockService.deleteStockForUser(id, user);
    }

    // Delete
    @DeleteMapping
    public void clearPortfolio(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        stockService.clearPortfolioForUser(user);
    }

    // Get (Finnhub)
    @GetMapping("/quote/{symbol}")
    public ResponseEntity<JsonNode> getQuote(@PathVariable String symbol) {
        JsonNode node = finnhubService.getQuote(symbol);

        if (isError(node)) return ResponseEntity.status(statusFromMessage(node)).body(node);
        return ResponseEntity.ok(node);
    }

    // Get (Finnub)
    @GetMapping("/quotes")
    public ResponseEntity<JsonNode> getQuotes(@RequestParam(name = "symbols") String symbolsCsv) {
        String[] symbols = (symbolsCsv == null) ? new String[0] : symbolsCsv.split(",");
        JsonNode node = finnhubService.getQuotes(symbols);

        if (isError(node)) return ResponseEntity.status(statusFromMessage(node)).body(node);
        return ResponseEntity.ok(node);
    }

    // Get (Symbole über Finnhub)
    @GetMapping("/search/{keywords}")
    public ResponseEntity<JsonNode> search(@PathVariable String keywords) {
        JsonNode node = finnhubService.searchSymbols(keywords);

        if (isError(node)) return ResponseEntity.status(statusFromMessage(node)).body(node);
        return ResponseEntity.ok(node);
    }

    private boolean isError(JsonNode node) {
        return node != null && node.has("s") && "error".equalsIgnoreCase(node.path("s").asText());
    }

    // Fehlerbehandlung wegen Free API Einschränkungen
    private HttpStatus statusFromMessage(JsonNode node) {
        String msg = node.path("message").asText("").toLowerCase();
        if (msg.contains("limit") || msg.contains("rate")) return HttpStatus.TOO_MANY_REQUESTS;
        if (msg.contains("invalid")) return HttpStatus.BAD_REQUEST;
        if (msg.contains("no ")) return HttpStatus.NOT_FOUND;
        return HttpStatus.BAD_GATEWAY;
    }
}
