package com.trackula.track.repository;

import com.trackula.track.model.Authorities;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface AuthoritiesRepository extends CrudRepository<String, Authorities> {
    @Query("SELECT * FROM authorities WHERE username IN :usernames")
    Iterable<Authorities> findAllByUsernames(Iterable<String> usernames);
    Iterable<Authorities> findAllByUsername(String username);
}
