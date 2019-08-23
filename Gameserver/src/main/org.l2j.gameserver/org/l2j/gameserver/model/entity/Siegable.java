package org.l2j.gameserver.model.entity;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.SiegeClan;
import org.l2j.gameserver.model.actor.Npc;
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

    SiegeClan getAttackerClan(int clanId);

    SiegeClan getAttackerClan(Clan clan);

    Collection<SiegeClan> getAttackerClans();

    List<Player> getAttackersInZone();

    boolean checkIsAttacker(Clan clan);

    SiegeClan getDefenderClan(int clanId);

    SiegeClan getDefenderClan(Clan clan);

    Collection<SiegeClan> getDefenderClans();

    boolean checkIsDefender(Clan clan);

    Set<Npc> getFlag(Clan clan);

    Calendar getSiegeDate();

    boolean giveFame();

    int getFameFrequency();

    int getFameAmount();

    void updateSiege();
}
