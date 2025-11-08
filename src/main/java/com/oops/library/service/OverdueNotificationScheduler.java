package com.oops.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oops.library.entity.BorrowLog;
import com.oops.library.entity.Role;
import com.oops.library.entity.User;
import com.oops.library.repository.BorrowLogRepository;
import com.oops.library.repository.UserRepository;

@Component
public class OverdueNotificationScheduler {

    private final BorrowLogRepository borrowLogRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OverdueNotificationScheduler(BorrowLogRepository borrowLogRepository,
                                        UserRepository userRepository,
                                        EmailService emailService) {
        this.borrowLogRepository = borrowLogRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void sendOverdueSummary() {
        List<BorrowLog> overdueLogs = borrowLogRepository.findByReturnedFalseAndReturnDateBefore(LocalDate.now());
        if (overdueLogs.isEmpty()) {
            return;
        }

        String summary = overdueLogs.stream()
                .map(log -> "• " + log.getBook().getTitle()
                        + " | Borrower: " + log.getBorrower().getName()
                        + " (" + log.getBorrower().getEmail() + ")"
                        + " | Due: " + log.getReturnDate())
                .collect(Collectors.joining("\n"));

        summary = "The following books are overdue:\n\n" + summary
                + "\n\nPlease follow up with the borrowers.";

        List<String> librarianEmails = userRepository.findByRole(Role.LIBRARIAN).stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        emailService.sendOverdueSummary(librarianEmails, summary);
    }
}


