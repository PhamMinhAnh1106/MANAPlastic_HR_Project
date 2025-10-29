package com.manaplastic.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class UserpermissionEntityId implements Serializable {
    private static final long serialVersionUID = 1037581269161480380L;
    @Column(name = "userID", nullable = false)
    private Integer userID;

    @Column(name = "permissionID", nullable = false)
    private Integer permissionID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserpermissionEntityId entity = (UserpermissionEntityId) o;
        return Objects.equals(this.permissionID, entity.permissionID) &&
                Objects.equals(this.userID, entity.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionID, userID);
    }

}