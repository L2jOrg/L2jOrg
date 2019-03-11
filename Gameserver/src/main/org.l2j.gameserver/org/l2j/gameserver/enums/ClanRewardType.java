package org.l2j.gameserver.enums;

import org.l2j.gameserver.data.xml.impl.ClanRewardData;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;

import java.util.function.Function;

/**
 * @author UnAfraid
 */
public enum ClanRewardType {
    MEMBERS_ONLINE(0, L2Clan::getPreviousMaxOnlinePlayers),
    HUNTING_MONSTERS(1, L2Clan::getPreviousHuntingPoints);

    final int _clientId;
    final int _mask;
    final Function<L2Clan, Integer> _pointsFunction;

    ClanRewardType(int clientId, Function<L2Clan, Integer> pointsFunction) {
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

    public ClanRewardBonus getAvailableBonus(L2Clan clan) {
        ClanRewardBonus availableBonus = null;
        for (ClanRewardBonus bonus : ClanRewardData.getInstance().getClanRewardBonuses(this)) {
            if (bonus.getRequiredAmount() <= _pointsFunction.apply(clan)) {
                if ((availableBonus == null) || (availableBonus.getLevel() < bonus.getLevel())) {
                    availableBonus = bonus;
                }
            }
        }
        return availableBonus;
    }
}
