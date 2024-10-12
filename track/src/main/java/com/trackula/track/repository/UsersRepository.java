package com.trackula.track.repository;

import com.trackula.track.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, String> {
}
