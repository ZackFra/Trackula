package com.trackula.track.repository;

import com.trackula.track.model.Authorities;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthoritiesRepository extends CrudRepository<Authorities, Long> {
    @Query("SELECT * FROM authorities WHERE username IN :usernames")
    Iterable<Authorities> findAllByUsernames(List<String> usernames);
    Iterable<Authorities> findAllByUsername(String username);
}
