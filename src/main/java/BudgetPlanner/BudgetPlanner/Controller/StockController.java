package BudgetPlanner.BudgetPlanner.Controller;

import BudgetPlanner.BudgetPlanner.Modell.Stock;
import BudgetPlanner.BudgetPlanner.Modell.User;
import BudgetPlanner.BudgetPlanner.Repository.UserRepository;
import BudgetPlanner.BudgetPlanner.Service.FinnhubService;
import BudgetPlanner.BudgetPlanner.Service.StockService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin
public class StockController {

    private final StockService stockService;
    private final FinnhubService finnhubService;
    private final UserRepository userRepository;

    public StockController(
            StockService stockService,
            FinnhubService finnhubService,
            UserRepository userRepository
    ) {
        this.stockService = stockService;
        this.finnhubService = finnhubService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Stock> getUserStocks(@AuthenticationPrincipal User user) {
        return stockService.getStocksForUser(user);
    }

    @PostMapping
    public Stock addStock(
            @RequestBody Stock stock,
            @AuthenticationPrincipal User user
    ) {
        stock.setUser(user);
        return stockService.saveStock(stock);
    }

    @GetMapping("/quote/{symbol}")
    public String getQuote(@PathVariable String symbol) {
        return finnhubService.getQuote(symbol);
    }

    @GetMapping("/chart/{symbol}")
    public String getChart(@PathVariable String symbol) {
        return finnhubService.getChart(symbol);
    }
}

