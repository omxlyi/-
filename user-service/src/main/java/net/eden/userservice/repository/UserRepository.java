package net.eden.userservice.repository;

import org.springframework.data.repository.CrudRepository;


import net.eden.userservice.domain.User;


public interface UserRepository extends CrudRepository<User,Long>{
    
}
