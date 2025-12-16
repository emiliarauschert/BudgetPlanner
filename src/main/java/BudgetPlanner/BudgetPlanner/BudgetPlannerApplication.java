package BudgetPlanner.BudgetPlanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class BudgetPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetPlannerApplication.class, args);
    }


    @Bean
    CommandLineRunner dbHealth(DataSource ds) {
        return args -> {
            try (var c = ds.getConnection();
                 var s = c.createStatement();
                 var rs = s.executeQuery("SELECT 1")) {
                if (rs.next()) System.out.println("DB OK: " + rs.getInt(1));
            }
        };

    }
}
