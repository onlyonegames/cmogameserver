package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BelongingInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    @ManyToOne
    @JoinColumn(name = "itemType_id")
    ItemType itemType;
    int itemId;
    int count;

    @Builder
    public BelongingInventory(Long useridUser, ItemType itemType, int itemId, int count) {
        this.useridUser = useridUser;
        this.itemType = itemType;
        this.itemId = itemId;
        this.count = count;
    }

    public boolean SpendItem(int spendCount) {
        if (count >= spendCount) {
            count -= spendCount;
            return true;
        }
        return false;
    }

    public void AddItem(int addCount, int stackLimit) {
        count += addCount;
        if(count > stackLimit)
            count = stackLimit;
    }
}