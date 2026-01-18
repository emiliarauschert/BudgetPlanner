package budgetplaner.budgetplaner.Modell;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String tvSymbol;

    private String name;
    private double quantity;
    private double buyPrice;
    private LocalDate buyDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- Getter/Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getTvSymbol() { return tvSymbol; }
    public void setTvSymbol(String tvSymbol) { this.tvSymbol = tvSymbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }

    public LocalDate getBuyDate() { return buyDate; }
    public void setBuyDate(LocalDate buyDate) { this.buyDate = buyDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
