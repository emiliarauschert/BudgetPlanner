package BudgetPlanner.BudgetPlanner.Modell;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double quantity;

    private double buyPrice;

    private LocalDate buyDate;

    // Zuordnung zum User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getter & Setter
}

