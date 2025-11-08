package com.oops.library.dto;

import com.oops.library.entity.*;

public class BookFormDto {

    private String type; // GENERAL, RARE, ANCIENT

    private String title;
    private String author;
    private String isbn;
    private String section;
    private BookStatus status;

    // GENERAL book field
    private boolean digitalAccess;

    // RARE book fields
    private String preservationMethod;
    private boolean inLibraryUseOnly;

    // ANCIENT script fields
    private String originalLanguage;
    
    // Note: cover image and manuscript files are handled in controller as MultipartFile

    // ====================
    // Getters and Setters
    // ====================



    public String getType() {
        return type;
    }
	public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }

    public BookStatus getStatus() {
        return status;
    }
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public boolean isDigitalAccess() {
        return digitalAccess;
    }
    public void setDigitalAccess(boolean digitalAccess) {
        this.digitalAccess = digitalAccess;
    }

    public String getPreservationMethod() {
        return preservationMethod;
    }
    public void setPreservationMethod(String preservationMethod) {
        this.preservationMethod = preservationMethod;
    }

    public boolean isInLibraryUseOnly() {
        return inLibraryUseOnly;
    }
    public void setInLibraryUseOnly(boolean inLibraryUseOnly) {
        this.inLibraryUseOnly = inLibraryUseOnly;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }
}
