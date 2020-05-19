package org.l2j.gameserver.enums;

import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;

import java.util.function.ToIntFunction;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public enum ClanRewardType {
    MEMBERS_ONLINE(0, Clan::getPreviousMaxOnlinePlayers),
    HUNTING_MONSTERS(1, Clan::getPreviousHuntingPoints),
    ARENA(-1, Clan::getArenaProgress);

    final int _clientId;
    final int _mask;
    final ToIntFunction<Clan> _pointsFunction;

    ClanRewardType(int clientId, ToIntFunction<Clan> pointsFunction) {
        _clientId = clientId;
        _mask = 1 << clientId;
        _pointsFunction = pointsFunction;
    }

    public static int getDefaultMask() {
        int mask = 0;
        for (ClanRewardType type : values()) {
            mask |= type.getMask();
        }
        return mask;
    }

    public int getClientId() {
        return _clientId;
    }

    public int getMask() {
        return _mask;
    }

    public ClanRewardBonus getAvailableBonus(Clan clan) {
        ClanRewardBonus availableBonus = null;
        for (ClanRewardBonus bonus : ClanRewardManager.getInstance().getClanRewardBonuses(this)) {
            if (bonus.getRequiredAmount() <= _pointsFunction.applyAsInt(clan)) {
                if ((availableBonus == null) || (availableBonus.getLevel() < bonus.getLevel())) {
                    availableBonus = bonus;
                }
            }
        }
        return availableBonus;
    }
}
