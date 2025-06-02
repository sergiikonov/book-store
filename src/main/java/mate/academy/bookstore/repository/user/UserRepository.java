package mate.academy.bookstore.repository.user;

import jakarta.validation.constraints.Email;
import java.util.Optional;
import mate.academy.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Email String email);
}
