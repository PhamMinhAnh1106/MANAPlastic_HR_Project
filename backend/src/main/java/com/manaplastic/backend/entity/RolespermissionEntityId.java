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
public class RolespermissionEntityId implements Serializable {
    private static final long serialVersionUID = 2886046708955238402L;
    @Column(name = "roleID", nullable = false)
    private Integer roleID;

    @Column(name = "permissionID", nullable = false)
    private Integer permissionID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RolespermissionEntityId entity = (RolespermissionEntityId) o;
        return Objects.equals(this.permissionID, entity.permissionID) &&
                Objects.equals(this.roleID, entity.roleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionID, roleID);
    }

}