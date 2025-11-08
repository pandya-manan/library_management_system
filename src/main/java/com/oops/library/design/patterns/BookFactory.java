package com.oops.library.design.patterns;

import com.oops.library.entity.AncientScript;
import com.oops.library.entity.Book;
import com.oops.library.entity.BookStatus;
import com.oops.library.entity.GeneralBook;
import com.oops.library.entity.RareBook;

public class BookFactory {
	
	public static Book createBook(String type, String title, String author, boolean digitalAccess,
	            String preservationMethod, Boolean inLibraryUseOnly, String originalLanguage,
	            String manuscriptPath, BookStatus status, String section,String isbn,String coverImagePath) {
return switch (type.toUpperCase()) {
case "GENERAL" -> {
GeneralBook b = new GeneralBook();
b.setTitle(title);
b.setAuthor(author);
b.setDigitalAccess(digitalAccess);
b.setStatus(status);
b.setSection(section);
b.setIsbn(isbn);
	b.setCoverImagePath(coverImagePath);
yield b;
}
case "RARE" -> {
RareBook b = new RareBook();
b.setTitle(title);
b.setAuthor(author);
b.setPreservationMethod(preservationMethod);
	b.setInLibraryUseOnly(Boolean.TRUE.equals(inLibraryUseOnly));
b.setStatus(status);
b.setSection(section);
b.setIsbn(isbn);
	b.setCoverImagePath(coverImagePath);
yield b;
}
case "ANCIENT" -> {
	AncientScript b = new AncientScript();
b.setTitle(title);
b.setAuthor(author);
b.setOriginalLanguage(originalLanguage);
b.setTranslationNotes("Translation Pending");
b.setManuscriptPath(manuscriptPath);
b.setStatus(status);
b.setSection(section);
	b.setIsbn(isbn);
	b.setCoverImagePath(coverImagePath);
yield b;
}
default -> throw new IllegalArgumentException("Unknown book type");
};
}


}
