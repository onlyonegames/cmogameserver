package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;

import org.hibernate.annotations.NaturalId;

import lombok.Data;

@Data
@Entity
public class ItemType {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private ItemTypeName itemTypeName;
}