package joshua.blog.service;


import java.util.Date;



import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import joshua.blog.domain.Account;
import joshua.blog.domain.AccountDto;
import joshua.blog.domain.AccountDto.Update;
import joshua.blog.exception.AccountNotFoundException;
import joshua.blog.exception.AccountDuplicatedException;
import joshua.blog.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository; 
	
	@Autowired
	private ModelMapper modelMapper; 
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
	public Account createAccount(AccountDto.Create createDto){
		/*
		Account account = new Account();
		
		account.setLoginId(createDto.getLoginId());
		account.setPassword(createDto.getPassword());
		*/
		//modelMapper 를 만들어서 위의 과정을 생략 할 수 있다. 
		Account account = modelMapper.map(createDto , Account.class);
		//Account account = new Account();
		//BeanUtils.copyProperties(createDto, account);
		
		
		
		
		//유효한 계정인지 확인 
		String username = createDto.getUsername();
		if(accountRepository.findByUsername(username)!=null){
			log.error("user duplicated exception {}" , username);
			throw new AccountDuplicatedException(username);
		}
		
		//password해싱(암호화) 
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		
		// 여기서 현재 날짜로 할 수도 있지만, 도메인 부분에서 @PreUpdate 부분을 사용해서 최신날짜 시켜주는것도 나쁘진 않다. 
		Date nowDate = new Date();
		account.setJoined(nowDate);
		account.setUpdated(nowDate);
		
		log.info("createAccount method : [ "+account.getUsername() + " ]");
		
		return accountRepository.save(account);
		
	}



	public Account updateAccount(Long id, Update update) {
		// TODO Auto-generated method stub
		Account account = getAccount(id);
		
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.setFullname(update.getFullname());
		
		return accountRepository.save(account);
		
	}



	public Account getAccount(Long id) {
		// TODO Auto-generated method stub
		Account account = accountRepository.findOne(id);
		
		
		if(account == null){
			throw new AccountNotFoundException(id);
		}
		return account;
	}



	public void deleteAccount(Long id) {
		// TODO Auto-generated method stub
		accountRepository.delete(getAccount(id));
	}



	
}
