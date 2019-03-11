package org.l2j.gameserver.model.pledge;

import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;

/**
 * @author UnAfraid
 */
public class ClanRewardBonus {
    private final ClanRewardType _type;
    private final int _level;
    private final int _requiredAmount;
    private SkillHolder _skillReward;
    private ItemHolder _itemReward;

    public ClanRewardBonus(ClanRewardType type, int level, int requiredAmount) {
        _type = type;
        _level = level;
        _requiredAmount = requiredAmount;
    }

    public ClanRewardType getType() {
        return _type;
    }

    public int getLevel() {
        return _level;
    }

    public int getRequiredAmount() {
        return _requiredAmount;
    }

    public SkillHolder getSkillReward() {
        return _skillReward;
    }

    public void setSkillReward(SkillHolder skillReward) {
        _skillReward = skillReward;
    }

    public ItemHolder getItemReward() {
        return _itemReward;
    }

    public void setItemReward(ItemHolder itemReward) {
        _itemReward = itemReward;
    }
}
