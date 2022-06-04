package net.eden.userservice.repository;

import org.springframework.data.repository.CrudRepository;

import net.eden.userservice.domain.Privilege;


public interface PrivilegeRepository extends CrudRepository<Privilege,Long>{

    // List<Privilege> findall();
}
