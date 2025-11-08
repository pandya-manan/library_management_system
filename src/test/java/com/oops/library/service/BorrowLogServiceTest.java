package com.oops.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oops.library.entity.BorrowLog;
import com.oops.library.repository.BorrowLogRepository;

@ExtendWith(MockitoExtension.class)
class BorrowLogServiceTest {

    @Mock
    private BorrowLogRepository borrowLogRepository;

    @InjectMocks
    private BorrowLogService borrowLogService;

    @Test
    void saveBorrowLog_delegatesToRepository() {
        BorrowLog borrowLog = new BorrowLog();

        borrowLogService.saveBorrowLog(borrowLog);

        verify(borrowLogRepository).save(borrowLog);
    }

    @Test
    void findAll_returnsRepositoryResult() {
        List<BorrowLog> logs = List.of(new BorrowLog());
        when(borrowLogRepository.findAll()).thenReturn(logs);

        List<BorrowLog> result = borrowLogService.findAll();

        assertEquals(logs, result);
    }
}


