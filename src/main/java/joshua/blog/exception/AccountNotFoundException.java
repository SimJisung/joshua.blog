package joshua.blog.exception;

public class AccountNotFoundException extends RuntimeException {
	
	private Long id;
	
	public AccountNotFoundException() {
		// TODO Auto-generated constructor stub
	}
	
	public AccountNotFoundException(Long id) {
		super();
		this.id = id;
	}



	



	public Long getId() {
		return id;
	}


	
	
	
	
	
	
}
