package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.actor.Npc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SiegeClan {
    private final Set<Npc> _flags = ConcurrentHashMap.newKeySet();
    private int _clanId;
    private SiegeClanType _type;

    public SiegeClan(int clanId, SiegeClanType type) {
        _clanId = clanId;
        _type = type;
    }

    public int getNumFlags() {
        return _flags.size();
    }

    public void addFlag(Npc flag) {
        _flags.add(flag);
    }

    public boolean removeFlag(Npc flag) {
        if (flag == null) {
            return false;
        }

        flag.deleteMe();

        return _flags.remove(flag);
    }

    public void removeFlags() {
        for (Npc flag : _flags) {
            removeFlag(flag);
        }
    }

    public final int getClanId() {
        return _clanId;
    }

    public final Set<Npc> getFlag() {
        return _flags;
    }

    public SiegeClanType getType() {
        return _type;
    }

    public void setType(SiegeClanType setType) {
        _type = setType;
    }
}
