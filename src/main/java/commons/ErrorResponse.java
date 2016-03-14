package commons;

import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
	
	private String message; 
	
	private String code; 
	
	
	//에러에 대한 디테일한 정보 저장하기 
	private List<ErrorResponse.FieldError> errors; 
	
	@Data
	public static class FieldError{
		private String field;
		private String value;
		private String defaultMessage; 
	}
	
	
	
}
