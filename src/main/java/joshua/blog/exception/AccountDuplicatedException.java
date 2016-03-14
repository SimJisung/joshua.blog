package joshua.blog.exception;

public class AccountDuplicatedException extends RuntimeException {
	
	String username; 
	
	public AccountDuplicatedException(String username) {
		// TODO Auto-generated constructor stub
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
	
	
	
}
