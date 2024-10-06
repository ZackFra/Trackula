package com.trackula.track.repository;

import com.trackula.track.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
