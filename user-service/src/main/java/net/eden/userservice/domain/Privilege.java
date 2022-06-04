package net.eden.userservice.domain;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Data
@Entity
public class Privilege {

    @Id
    // @GeneratedValue
    private Long id;
    private String description;
    private String url;
    private String type;

    @ManyToMany(mappedBy = "privs")
    private List<Role> roles = new LinkedList<>();

    public String getKey(){
        return this.url + "-" + this.type;
    }


    public static enum Type{
        GET,POST,PUT,DELETE,HEAD,OPTIONS,CONNECT,TRACE,PATCH
    }
}
