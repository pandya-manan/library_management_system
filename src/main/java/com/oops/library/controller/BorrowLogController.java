package com.oops.library.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.oops.library.entity.BorrowLog;
import com.oops.library.service.BorrowLogService;
import com.oops.library.strategy.LateFeeService;

@Controller
public class BorrowLogController {
    // Assuming borrowLogService is injected
    private final BorrowLogService borrowLogService;

    private final LateFeeService lateFeeService;

    public BorrowLogController(BorrowLogService borrowLogService, LateFeeService lateFeeService) {
        this.borrowLogService = borrowLogService;
        this.lateFeeService = lateFeeService;
    }

    @GetMapping("/borrowlogs")
    public String viewBorrowLogs(Model model) {
        List<BorrowLog> borrowLogs = borrowLogService.findAll();
        Map<BorrowLog, Double> lateFees = new HashMap<>();
        for (BorrowLog log : borrowLogs) {
            if (log != null && log.getBook() != null) {
                lateFees.put(log, lateFeeService.calculateLateFee(log));
            } else {
                lateFees.put(log, 0.0);
            }
        }
        model.addAttribute("borrowLogs", borrowLogs);
        model.addAttribute("lateFees", lateFees);
        return "borrowlogs";
    }
}