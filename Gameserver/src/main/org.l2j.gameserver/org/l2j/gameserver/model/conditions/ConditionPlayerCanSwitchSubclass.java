/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * @author Sdw
 */
public class ConditionPlayerCanSwitchSubclass extends Condition {
    private final int _subIndex;

    public ConditionPlayerCanSwitchSubclass(int subIndex) {
        _subIndex = subIndex;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        boolean canSwitchSub = true;

        final Player player = effector.getActingPlayer();
        if ((player == null) || player.isAlikeDead()) {
            canSwitchSub = false;
        } else if (((_subIndex != 0) && (player.getSubClasses().get(_subIndex) == null)) || (player.getClassIndex() == _subIndex)) {
            canSwitchSub = false;
        } else if (!player.isInventoryUnder90(true)) {
            player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
            canSwitchSub = false;
        } else if (player.getWeightPenalty() >= 2) {
            player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
            canSwitchSub = false;
        } else if (player.isOnEvent()) {
            player.sendMessage("You cannot change your subclass while registered in an event.");
            canSwitchSub = false;
        } else if (player.isAllSkillsDisabled()) {
            canSwitchSub = false;
        } else if (player.isAffected(EffectFlag.MUTED)) {
            canSwitchSub = false;
            player.sendPacket(SystemMessageId.YOU_CANNOT_CHANGE_THE_CLASS_BECAUSE_OF_IDENTITY_CRISIS);
        } else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || (player.getPvpFlag() > 0) || player.isInInstance() || player.isTransformed() || player.isMounted()) {
            canSwitchSub = false;
        }

        return canSwitchSub;
    }
}
