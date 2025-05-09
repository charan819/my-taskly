package com.charan.mytaskly.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "organizations")
public class Organizations {

    @Id
    @Column(name = "organizations_id")
    private String organizationsId;

    @Column(name = "organization_name", unique = true, nullable = false)
    private String organizationName;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private Users owner;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "organizations-projects")
    private List<Projects> projects;

    public Organizations() {
        super();
    }

    public Organizations(String organizationsId, String organizationName, Users owner, List<Projects> projects) {
        this.organizationsId = organizationsId;
        this.organizationName = organizationName;
        this.owner = owner;
        this.projects = projects;
    }

    public String getOrganizationsId() {
        return organizationsId;
    }

    public void setOrganizationsId(String organizationsId) {
        this.organizationsId = organizationsId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public List<Projects> getProjects() {
        return projects;
    }

    public void setProjects(List<Projects> projects) {
        this.projects = projects;
    }
}
