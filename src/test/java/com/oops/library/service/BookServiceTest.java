package com.oops.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.Book;
import com.oops.library.entity.GeneralBook;
import com.oops.library.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks_returnsBooksWhenAvailable() throws EnchantedLibraryException {
        Book book = new GeneralBook();
        List<Book> expectedBooks = List.of(book);
        when(bookRepository.findAllBooks()).thenReturn(expectedBooks);

        List<Book> result = bookService.getAllBooks();

        assertEquals(expectedBooks, result);
    }

    @Test
    void getAllBooks_throwsWhenNoBooksFound() {
        when(bookRepository.findAllBooks()).thenReturn(Collections.emptyList());

        assertThrows(EnchantedLibraryException.class, () -> bookService.getAllBooks());
    }

    @Test
    void getBookById_delegatesToRepository() {
        Book expectedBook = new GeneralBook();
        when(bookRepository.findBookById(1L)).thenReturn(expectedBook);

        Book result = bookService.getBookById(1L);

        assertSame(expectedBook, result);
    }

    @Test
    void saveBook_persistsViaRepository() {
        Book book = new GeneralBook();

        bookService.saveBook(book);

        verify(bookRepository).save(book);
    }
}


