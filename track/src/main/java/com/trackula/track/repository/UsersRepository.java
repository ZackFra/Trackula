package com.trackula.track.repository;

import com.trackula.track.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, String> {
    @Query("SELECT username FROM (users INNER JOIN authorities ON authorities.username = username)")
    Iterable<User> findAllWithAuthorities();
}
