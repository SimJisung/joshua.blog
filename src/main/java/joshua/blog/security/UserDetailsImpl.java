package joshua.blog.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import joshua.blog.domain.Account;

public class UserDetailsImpl extends User{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserDetailsImpl(Account account) {
		super(account.getUsername(), account.getPassword(), athorities(account));
		// TODO Auto-generated constructor stub
	}

	private static Collection<? extends GrantedAuthority> athorities(Account account) {
		// TODO Auto-generated method stub
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		
		if(account.isAdmin()){
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}else{
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		
		
		/*authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		if(account.isAdmin()){
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}*/
		return authorities;
	}
	
}
