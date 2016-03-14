package joshua.blog;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JoshuaBlogApplication {
	
	//최초 스프링 부트 시작 어플리케이션 영역
	public static void main(String[] args) {
		SpringApplication.run(JoshuaBlogApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
