package com.github.kuros.random.jpa.testUtil.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "relation")
public class RelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "relation_one_to_one_id")
    private RelationOneToOne relationOneToOne;

    @ManyToOne
    @JoinColumn(name = "relation_many_to_one_id")
    private RelationManyToOne relationManyToOne;

    @OneToMany
    @JoinColumn(name = "relation_one_to_many_id")
    private List<RelationOneToMany> relationOneToMany;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public RelationOneToOne getRelationOneToOne() {
        return relationOneToOne;
    }

    public void setRelationOneToOne(final RelationOneToOne relationOneToOne) {
        this.relationOneToOne = relationOneToOne;
    }

    public RelationManyToOne getRelationManyToOne() {
        return relationManyToOne;
    }

    public void setRelationManyToOne(final RelationManyToOne relationManyToOne) {
        this.relationManyToOne = relationManyToOne;
    }

    public List<RelationOneToMany> getRelationOneToMany() {
        return relationOneToMany;
    }

    public void setRelationOneToMany(final List<RelationOneToMany> relationOneToMany) {
        this.relationOneToMany = relationOneToMany;
    }
}
