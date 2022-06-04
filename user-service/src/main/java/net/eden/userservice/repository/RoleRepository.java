package net.eden.userservice.repository;

import org.springframework.data.repository.CrudRepository;


import net.eden.userservice.domain.Role;


public interface RoleRepository extends CrudRepository<Role,Long>{
    
}
