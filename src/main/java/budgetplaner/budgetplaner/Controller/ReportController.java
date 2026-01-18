package budgetplaner.budgetplaner.Controller;

import budgetplaner.budgetplaner.Modell.User;
import budgetplaner.budgetplaner.Repository.UserRepository;
import budgetplaner.budgetplaner.Service.ReportService;
import budgetplaner.budgetplaner.dto.ReportResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
@CrossOrigin
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    // Get
    @GetMapping
    public ReportResponse getReport(@RequestParam String month, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return reportService.buildReport(user, month);
    }
}

