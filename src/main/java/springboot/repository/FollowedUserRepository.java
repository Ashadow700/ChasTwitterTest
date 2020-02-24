package springboot.repository;

import org.springframework.data.repository.CrudRepository;
import springboot.dataEntities.FollowedUser;

public interface FollowedUserRepository extends CrudRepository<FollowedUser, Integer> {
}