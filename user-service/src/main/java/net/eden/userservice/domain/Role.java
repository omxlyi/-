package net.eden.userservice.domain;

import java.util.LinkedList;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Data
@Entity
public class Role {

    @Id
    // @GeneratedValue
    private Long id;
    private String name;
    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Privilege> privs = new LinkedList<>();

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new LinkedList<>();

}
