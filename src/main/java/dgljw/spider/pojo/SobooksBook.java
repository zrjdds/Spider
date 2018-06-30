package dgljw.spider.pojo;


public class SobooksBook {
	private String bookImgURL;
	private String bookTitle;
	private String bookAuthor;
	private String bookFormat;
	private String bookBaiduLink;
	private String bookBaiduPassword;
	private String bookContentDescription;
	private String bookAuthorDescription;
	private String bookFileName;
	private String bookFilePath;
	private String bookSource;
	private boolean bookDownloaded;
	
	public void setBookImgURL(String bookImgURL) {
	    this.bookImgURL = bookImgURL == null ? null : bookImgURL.trim();
	}
	
	public String getBookImgURL() {
		return this.bookImgURL;
	}
	
	public void setBookTitle(String bookTitle) {
	    this.bookTitle = bookTitle == null ? null : bookTitle.trim();
	}
	
	public String getBookTitle() {
		return this.bookTitle;
	}
	
	public void setBookAuthor(String bookAuthor) {
	    this.bookAuthor = bookAuthor == null ? null : bookAuthor.trim();
	}
	
	public String getBookAuthor() {
		return this.bookAuthor;
	}
	
	public void setBookFormat(String bookFormat) {
	    this.bookFormat = bookFormat == null ? null : bookFormat.trim();
	}
	
	public String getBookFormat() {
		return this.bookFormat;
	}
	
	
	public void setBookBaiduLink(String bookBaiduLink) {
	    this.bookBaiduLink = bookBaiduLink == null ? null : bookBaiduLink.trim();
	}
	
	public String getBookBaiduLink() {
		return this.bookBaiduLink;
	}
	
	public void setBookBaiduPassword(String bookBaiduPassword) {
	    this.bookBaiduPassword = bookBaiduPassword == null ? null : bookBaiduPassword.trim();
	}
	
	public String getBookBaiduPassword() {
		return this.bookBaiduPassword;
	}
	
	public void setBookContentDescription(String bookContentDescription) {
	    this.bookContentDescription = bookContentDescription == null ? null : bookContentDescription.trim();
	}
	
	public String getBookContentDescription() {
		return this.bookContentDescription;
	}
	
	public void setBookAuthorDescription(String bookAuthorDescription) {
	    this.bookAuthorDescription = bookAuthorDescription == null ? null : bookAuthorDescription.trim();
	}
	
	public String getBookAuthorDescription() {
		return this.bookAuthorDescription;
	}
	
	public void setBookFileName(String bookFileName) {
	    this.bookFileName = bookFileName == null ? null : bookFileName.trim();
	}
	
	public String getBookFileName() {
		return this.bookFileName;
	}
	
	public void setBookFilePath(String bookFilePath) {
	    this.bookFilePath = bookFilePath == null ? null : bookFilePath.trim();
	}
	
	public String getBookFilePath() {
		return this.bookFilePath;
	}
	
	public void setBookSource(String bookSource) {
	    this.bookSource = bookSource == null ? null : bookSource.trim();
	}
	
	public String getBookSource() {
		return this.bookSource;
	}
	
	public void setBookDownloaded(boolean bookDownloaded) {
	    this.bookDownloaded = bookDownloaded;
	}
	
	public boolean getBookDownloaded() {
		return this.bookDownloaded;
	}
}
