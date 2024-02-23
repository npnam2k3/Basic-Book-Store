package vnua.fita.bookstore.bean;

import java.util.Date;

public class Book {
	private int bookId;
	private String title;
	private String author;
	private int price;
	private int quantityInStock;
	private String detail;
	private String imagePath;
	private Date createDate;

	public Book() {
		super();
	}

	public Book(int bookId, String title, String author, int price, int quantityInStock) {
		super();
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.price = price;
		this.quantityInStock = quantityInStock;
	}
	

	public Book(String title, String author, int price, int quantityInStock) {
		super();
		this.title = title;
		this.author = author;
		this.price = price;
		this.quantityInStock = quantityInStock;
	}
	

	public Book(int bookId, String title, String author, int price) {
		super();
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.price = price;
	}

	public Book(String title, String author, int price, int quantityInStock,
			String detail, String imagePath) {
		super();
		this.title = title;
		this.author = author;
		this.price = price;
		this.quantityInStock = quantityInStock;
		this.detail = detail;
		this.imagePath = imagePath;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(int quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return title+" - "+author+" - "+price;
	}
}
