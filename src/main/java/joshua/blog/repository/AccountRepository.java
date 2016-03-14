package joshua.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import joshua.blog.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	Account findByUsername(String username);

}
