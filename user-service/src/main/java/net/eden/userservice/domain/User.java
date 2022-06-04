package net.eden.userservice.domain;


import java.util.LinkedList;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;


@Data
@Entity
public class User {

    @Id
    // @GeneratedValue
    private Long id;

    private String username;
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Role> roles = new LinkedList<>();

    public void addRole(Role role){
        this.roles.add(role);
    }

}