package com.manaplastic.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class LeavebalanceEntityId implements Serializable {
    private static final long serialVersionUID = -2689799086420403665L;
    @NotNull
    @Column(name = "userID", nullable = false)
    private Integer userID;

    @NotNull
    @Column(name = "leavetypeid", nullable = false)
    private Integer leavetypeid;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LeavebalanceEntityId entity = (LeavebalanceEntityId) o;
        return Objects.equals(this.year, entity.year) &&
                Objects.equals(this.leavetypeid, entity.leavetypeid) &&
                Objects.equals(this.userID, entity.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, leavetypeid, userID);
    }

}