package com.oops.library.entity;
import jakarta.persistence.*;
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="book_type")
public abstract class Book {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String author;
	
	private String isbn;
	
	//Examples of sections can be General, Reserved, Restricted
	private String section;
	
	@Enumerated(EnumType.STRING)
	private BookStatus status;
	
	@Column(name = "cover_image_url", length = 512)
    private String coverImagePath;
	
	public abstract double getLateFeeRate();


	public abstract String getType();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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


	public String getCoverImagePath() {
		return coverImagePath;
	}


	public void setCoverImagePath(String coverImagePath) {
		this.coverImagePath = coverImagePath;
	}
	
	
	
}
