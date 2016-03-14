package joshua.blog.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import joshua.blog.domain.Account;
import joshua.blog.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService{

	
	@Autowired
	private AccountRepository accountRepository; 
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Account account = accountRepository.findByUsername(username);
		
		log.info("UserDatails username : [ " + username + "]");
		
		if(account == null){
			throw new UsernameNotFoundException(username);
		}
		return new UserDetailsImpl(account);
	}

}
