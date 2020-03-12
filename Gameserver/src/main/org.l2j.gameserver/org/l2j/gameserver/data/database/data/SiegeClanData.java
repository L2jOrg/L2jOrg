package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

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

    public SiegeClanData() {
    }

    public SiegeClanData(int id, SiegeClanType type, int castleId) {
        this.clanId = id;
        this.type = type;
        this.castleId = castleId;
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

    public void setType(SiegeClanType type) {
        this.type = type;
    }

    public Set<Npc> getFlags() {
        return flags;
    }

    public boolean removeFlag(Npc flag) {
        if (isNull(flag)) {
            return false;
        }

        flag.deleteMe();

        return flags.remove(flag);
    }

    public void removeFlags() {
        flags.forEach(this::removeFlag);
    }

    public void addFlag(SiegeFlag siegeFlag) {
        flags.add(siegeFlag);
    }

    public int getNumFlags() {
        return flags.size();
    }
}
