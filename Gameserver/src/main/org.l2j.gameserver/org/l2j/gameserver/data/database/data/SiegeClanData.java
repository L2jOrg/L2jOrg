package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JoeAlisson
 */
@Table("siege_clans")
public class SiegeClanData {

    @Transient
    private final Set<Npc> flags = ConcurrentHashMap.newKeySet();

    @Column("castle_id")
    private int castleId;

    @Column("clan_id")
    private int clanId;

    private SiegeClanType type;

    @Column("castle_owner")
    private int ownerId;

    public SiegeClanData() {
    }

    public SiegeClanData(int clanId, SiegeClanType type) {
        this.clanId = clanId;
        this.type = type;

    }

    public int getCastleId() {
        return castleId;
    }

    public int getClanId() {
        return clanId;
    }

    public SiegeClanType getType() {
        return type;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setType(SiegeClanType type) {
        this.type = type;
    }

    public Set<Npc> getFlags() {
        return flags;
    }

    public boolean removeFlag(Npc flag) {
        if (flag == null) {
            return false;
        }

        flag.deleteMe();

        return flags.remove(flag);
    }

    public void removeFlags() {
        for (Npc flag : flags) {
            removeFlag(flag);
        }
    }

    public void addFlag(SiegeFlag siegeFlag) {
        flags.add(siegeFlag);
    }

    public int getNumFlags() {
        return flags.size();
    }
}
