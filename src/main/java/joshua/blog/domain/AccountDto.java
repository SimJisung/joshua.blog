package joshua.blog.domain;

import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

public class AccountDto {


	@Data
	public static class Create{
		
		@NotBlank
		@Size(min=4)
		private String username; 
		
		@NotBlank
		@Size(min=4)
		private String password; 
	}
	
	@Data
	public static class Response{
		private Long id; 
		private String username; 
		private String fullname; 
		private String email; 
		private Date joined; 
		private Date updated; 
	}
	
	@Data
	public static class Update {
		private String password; 
		private String fullname; 

		
	}
}
