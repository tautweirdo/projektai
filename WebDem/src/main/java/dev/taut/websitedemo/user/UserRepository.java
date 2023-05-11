package dev.taut.websitedemo.user;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    @Query(value = "SELECT MAX(id) FROM User")
    Integer getMaxId();
    public Long countById(Integer id);
}
