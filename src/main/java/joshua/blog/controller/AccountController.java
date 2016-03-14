package joshua.blog.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import commons.ErrorResponse;
import joshua.blog.domain.Account;
import joshua.blog.domain.AccountDto;
import joshua.blog.exception.AccountDuplicatedException;
import joshua.blog.exception.AccountNotFoundException;
import joshua.blog.repository.AccountRepository;
import joshua.blog.service.AccountService;



@RestController
public class AccountController {
	
	@Autowired
	private AccountService accountService; 
	
	@Autowired
	private AccountRepository accountRepository; 
	
	@Autowired
	private ModelMapper modelMapper; 
	
	@RequestMapping(value = "/accounts" , method = RequestMethod.POST)
	public ResponseEntity createAccount(@RequestBody @Valid AccountDto.Create createAccount , BindingResult result){
		if(result.hasErrors()){
			//TODO: 에러 응답 본문 추가 하기
			ErrorResponse errorResponse = new ErrorResponse();
			
			errorResponse.setMessage("잘못된 요청 입니다.");
			errorResponse.setCode("bad.request");
			
			//TODO BindingResult 안에 들어있는 에러 정보 사용하기. 
			List<FieldError> errors = result.getFieldErrors();
			List<ErrorResponse.FieldError> saveErrorInfo = getErrorsInfo(errors);
			
			errorResponse.setErrors(saveErrorInfo);
			
			return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
		}
		
		Account newAccount = accountService.createAccount(createAccount);
		
		return new ResponseEntity<>(modelMapper.map(newAccount, AccountDto.Response.class) ,HttpStatus.CREATED);
	}
	
	
	// 별다른 로직이 없으면 굳이 .. 서비스에 넣어줄 필요는 없다.
	@RequestMapping(value = "/accounts" , method = RequestMethod.GET)
	public ResponseEntity getAccounts(Pageable pageable){
		
		Page<Account> page = accountRepository.findAll(pageable);
		
		List<AccountDto.Response> list = page.getContent().stream()
														.map(account->modelMapper.map(account, AccountDto.Response.class))
														.collect(Collectors.toList());
		
		
		//{"content":[{"id":1,"username":"simjisung","fullname":null,"email":null,"joined":1456634092901,"updated":1456634092901}],"totalElements":1,"totalPages":1,"last":true,"size":20,"number":0,"sort":null,"first":true,"numberOfElements":1}
		PageImpl<AccountDto.Response> resultList = new PageImpl<>(list,pageable,page.getTotalElements());
		
		return new ResponseEntity<>(resultList,HttpStatus.OK);
	}
	
	@RequestMapping(value ="/accounts/{id}",method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public AccountDto.Response getAccount(@PathVariable Long id){
		
		Account account = accountRepository.findOne(id);
		
		AccountDto.Response result = modelMapper.map(account, AccountDto.Response.class);
		return result; 
		
	}
	
	//전체 업데이트 (PUT) vs 부분 업데이트(PATCH) 
	@RequestMapping(value = "/accounts/{id}" , method=RequestMethod.PATCH)
	public ResponseEntity updateAccount(@PathVariable Long id , @RequestBody @Valid AccountDto.Update update, BindingResult result){
		if(result.hasErrors()){
			ErrorResponse errorResponse = new ErrorResponse();
			// BindingResult 안에 들어있는 에러 정보 사용하기. 
			List<FieldError> errors = result.getFieldErrors();
			List<ErrorResponse.FieldError> saveErrorInfo = getErrorsInfo(errors);
			
			errorResponse.setErrors(saveErrorInfo);
			
			return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
		}

		Account updateAccount = accountService.updateAccount(id,update);
		
		return new ResponseEntity<>(modelMapper.map(updateAccount, AccountDto.Update.class), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/accounts/{id}" , method=RequestMethod.DELETE)
	public ResponseEntity deleteAccount(@PathVariable Long id){
		accountService.deleteAccount(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
	private List<ErrorResponse.FieldError> getErrorsInfo(List<FieldError> errors) {
		/*
		List<ErrorResponse.FieldError> saveErrorInfo = new ArrayList<ErrorResponse.FieldError>();
		
		for (FieldError fieldError : errors) {
			ErrorResponse.FieldError errorInfo = new ErrorResponse.FieldError();
			
			//Size/size must be between 4 and 2147483647/username/create/jo
			//Size/size must be between 4 and 2147483647/password/create/12
			//System.out.println(fieldError.getCode() + "/" + fieldError.getDefaultMessage() + "/" + fieldError.getField() + "/" + fieldError.getObjectName() + "/" + fieldError.getRejectedValue());
			//modelMapper.map(fieldError, ErrorResponse.FieldError.class);
			
			errorInfo.setField(fieldError.getField());
			errorInfo.setValue(fieldError.getRejectedValue().toString());
			errorInfo.setReason(fieldError.getDefaultMessage());
			
			saveErrorInfo.add(errorInfo);	
		}
		return saveErrorInfo;
		*/
		
		/*
		 * java8의 스트림 기능 과 기존의 modelMapper 기능을 활용하여, 
		 * 기존의 FieldError 클래스의 정보를 ErrorResponse.FieldError 의 정보와 그대로 맵핑 시켜서 
		 * 리스트로 반환 시키는 로직을 구성하였다. 이렇게 사용하니, 코드가 굉장히 심플해졌다. 
		 * 
		 * */
		List<ErrorResponse.FieldError> saveErrorInfo = errors.stream()
															 .map(error->modelMapper.map(error, ErrorResponse.FieldError.class))
															 .collect(Collectors.toList());
		
		return saveErrorInfo; 
	}
	
	/*
	 * 익셉션 핸들러는 지정된 익셉션이 발동될시, 아래 익셉션 핸들러가 감지되어, 아래 메소드가 호출된다. 
	 * 또한 @ControllerAdvice를 사용하면, 전역으로 익셉션 처리를 할 수가 있다. 
	 * 그럼 전역으로 감지하여, 공통적인 익셉션 처리를 할 수가 있게 될 것 같다. 
	 */
	@ExceptionHandler(AccountDuplicatedException.class)
	public ResponseEntity handlerUserDuplicatedException(AccountDuplicatedException e){
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.setMessage("이미 사용중인 계정 정보 입니다. [ " + e.getUsername() + " ]");
		errorResponse.setCode("duplicated.username.exception");
		
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handlerAccountNotFoundException(AccountNotFoundException e){
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + e.getId() + "] 에 해당하는 계정을 찾을 수 없습니다. ");
		errorResponse.setCode("account.not.found.exception");
		return errorResponse;
		
	}
	
	
}
