package springboot.repository;

import org.springframework.data.repository.CrudRepository;
import springboot.dataEntities.User;

public interface UserRepository extends CrudRepository<User, String>{
}