package joshua.blog.Account;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import joshua.blog.JoshuaBlogApplication;
import joshua.blog.domain.Account;
import joshua.blog.domain.AccountDto;
import joshua.blog.service.AccountService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=JoshuaBlogApplication.class)
@WebAppConfiguration
@Transactional
public class AccountControllerTest {
	
	@Autowired
	private WebApplicationContext wac; 
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private AccountService accountService; 
	
	MockMvc mockMvc;
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain; 
	
	@Before
	public void setUp(){
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(springSecurityFilterChain).build();
	}
	
	@Test
	public void createAccount() throws JsonProcessingException, Exception{
	
		AccountDto.Create createDto = new AccountDto.Create();
		createDto.setUsername("joshua");
		createDto.setPassword("123456");
		
		ResultActions result = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)));
		
		result.andDo(print());
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.username",is("joshua"))); 
	}
	
	@Test
	public void createAccount_BadRequest() throws JsonProcessingException, Exception{
		AccountDto.Create createDto = new AccountDto.Create();
		createDto.setUsername("jo");
		createDto.setPassword("12");
		
		ResultActions result = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)));
		
		result.andDo(print());
		result.andExpect(status().isBadRequest());		
		result.andExpect(jsonPath("$.code",is("bad.request"))); 
	}
	
	@Test
	public void createAccount_Duplicated() throws JsonProcessingException, Exception{
		AccountDto.Create createDto = new AccountDto.Create();
		createDto.setUsername("joshua");
		createDto.setPassword("123456");
		
		ResultActions result = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)));
		
		result.andDo(print());
		result.andExpect(status().isCreated());
		
		result = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)));
		
		result.andDo(print());
		result.andExpect(status().isBadRequest());
		
		
	}
	
	@Test
	public void getAccounts() throws Exception{
		AccountDto.Create createDto = accountCreateFixture();
		
		accountService.createAccount(createDto);
		
		ResultActions result = mockMvc.perform(get("/accounts").with(httpBasic(createDto.getUsername(), createDto.getPassword())));
		
		result.andDo(print());
		result.andExpect(status().isOk());
	}
	
	
	@Test 
	public void getAccount() throws Exception {
		AccountDto.Create createDto = accountCreateFixture();
		
		Account account = accountService.createAccount(createDto);	
		
		ResultActions result = mockMvc.perform(get("/accounts/" + account.getId()));
		
		result.andDo(print());
		result.andExpect(status().isOk());		
		
	}
	
	@Test
	public void updateAccount() throws Exception{
		AccountDto.Create createDto = accountCreateFixture();
		
		Account account = accountService.createAccount(createDto);	
		
		AccountDto.Update updateDto = new AccountDto.Update();
		updateDto.setFullname("SimJisung");
		updateDto.setPassword("sim1856");
				
		
		ResultActions result = mockMvc.perform(patch("/accounts/"+account.getId())
										.with(httpBasic(createDto.getUsername(), createDto.getPassword()))
										.contentType(MediaType.APPLICATION_JSON)
										.content(objectMapper.writeValueAsString(updateDto)));
		
		result.andDo(print());
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.fullname", is("SimJisung")));
		//result.andExpect(jsonPath("$.password", is("sim1856")));
		
		ResultActions result1 = mockMvc.perform(get("/accounts"));
		
		result1.andDo(print());
		result1.andExpect(status().isOk());

	}
	
	@Test 
	public void deleteAccount() throws Exception {
		
		AccountDto.Create createDto = accountCreateFixture();
		
		Account account = accountService.createAccount(createDto);
		
		ResultActions result = mockMvc.perform(delete("/accounts/12345").with(httpBasic(createDto.getUsername(), createDto.getPassword())));
		
		result.andDo(print());
		result.andExpect(status().isBadRequest());
		
		
		
		result = mockMvc.perform(delete("/accounts/"+account.getId()).with(httpBasic(createDto.getUsername(), createDto.getPassword())));
		
		result.andDo(print());
		result.andExpect(status().isNoContent());
		
	}
	
	private AccountDto.Create accountCreateFixture() {
		AccountDto.Create createDto = new AccountDto.Create();
		
		createDto.setUsername("simjisung");
		createDto.setPassword("1234556");
		
		return createDto;
	}
	
}
