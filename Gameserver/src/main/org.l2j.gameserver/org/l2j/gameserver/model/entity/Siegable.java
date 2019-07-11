package org.l2j.gameserver.model.entity;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author JIV
 */
public interface Siegable {
    void startSiege();

    void endSiege();

    L2SiegeClan getAttackerClan(int clanId);

    L2SiegeClan getAttackerClan(L2Clan clan);

    Collection<L2SiegeClan> getAttackerClans();

    List<Player> getAttackersInZone();

    boolean checkIsAttacker(L2Clan clan);

    L2SiegeClan getDefenderClan(int clanId);

    L2SiegeClan getDefenderClan(L2Clan clan);

    List<L2SiegeClan> getDefenderClans();

    boolean checkIsDefender(L2Clan clan);

    Set<L2Npc> getFlag(L2Clan clan);

    Calendar getSiegeDate();

    boolean giveFame();

    int getFameFrequency();

    int getFameAmount();

    void updateSiege();
}
