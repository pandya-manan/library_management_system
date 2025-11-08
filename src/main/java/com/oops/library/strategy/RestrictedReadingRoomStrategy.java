package com.oops.library.strategy;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component("restricted")
public class RestrictedReadingRoomStrategy implements LendingStrategy {

	@Override
	public LocalDate calculateReturnDate(LocalDate borrowDate) {
		return borrowDate.plusDays(1);
	}

}
