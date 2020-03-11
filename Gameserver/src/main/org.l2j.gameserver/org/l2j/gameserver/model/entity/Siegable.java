package org.l2j.gameserver.model.entity;

import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author JIV
 */
public interface Siegable {
    void startSiege();

    void endSiege();

    SiegeClanData getAttackerClan(int clanId);

    SiegeClanData getAttackerClan(Clan clan);

    IntMap<SiegeClanData> getAttackerClans();

    List<Player> getAttackersInZone();

    boolean checkIsAttacker(Clan clan);

    SiegeClanData getDefenderClan(int clanId);

    SiegeClanData getDefenderClan(Clan clan);

    IntMap<SiegeClanData> getDefenderClans();

    boolean checkIsDefender(Clan clan);

    Set<Npc> getFlag(Clan clan);

    LocalDateTime getSiegeDate();

    boolean giveFame();

    int getFameFrequency();

    int getFameAmount();

    void updateSiege();
}
