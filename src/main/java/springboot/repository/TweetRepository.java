package springboot.repository;

import org.springframework.data.repository.CrudRepository;
import springboot.dataEntities.Tweet;


public interface TweetRepository extends CrudRepository<Tweet, Integer> {

}
