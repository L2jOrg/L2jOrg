/*
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
package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SummonRequestHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * Call Pc effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class CallPc extends AbstractEffect {

    private final int itemId;
    private final int itemCount;

    private CallPc(StatsSet params) {
        itemId = params.getInt("item", 0);
        itemCount = params.getInt("item-count", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effector == effected) {
            return;
        }

        final Player target = effected.getActingPlayer();
        final Player player = effector.getActingPlayer();
        if (checkSummonTargetStatus(target, player)) {
            if (itemId != 0 && itemCount != 0) {
                if (target.getInventory().getInventoryItemCount(itemId, 0) < itemCount) {
                    target.sendPacket(getSystemMessage(SystemMessageId.S1_IS_REQUIRED_FOR_SUMMONING).addItemName(itemId));
                    return;
                }
                target.getInventory().destroyItemByItemId("Consume", itemId, itemCount, player, target);
                target.sendPacket( getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(itemId));
            }

            target.addScript(new SummonRequestHolder(player, skill));
            target.sendPacket(new ConfirmDlg(SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT).addString(player.getName()).addZoneName(player.getX(), player.getY(), player.getZ())
                    .addTime(30000).addRequesterId(player.getObjectId()));
        }
    }

    public static boolean checkSummonTargetStatus(Player target, Creature creature) {
        if (target == creature) {
            return false;
        }

        if (target.isAlikeDead()) {
            creature.sendPacket( getSystemMessage(SystemMessageId.C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED).addPcName(target) );
            return false;
        }

        if (target.isInStoreMode()) {
            creature.sendPacket( getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED).addPcName(target) );
            return false;
        }

        if (target.isRooted() || target.isInCombat()) {
            creature.sendPacket(getSystemMessage(SystemMessageId.C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED).addPcName(target));
            return false;
        }

        if (target.isInOlympiadMode()) {
            creature.sendPacket(SystemMessageId.A_USER_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_USE_SUMMONING_OR_TELEPORTING);
            return false;
        }

        if (target.isFlyingMounted() || target.isInTraingCamp() || target.isInTimedHuntingZone()) {
            creature.sendPacket(SystemMessageId.YOU_CANNOT_USE_SUMMONING_OR_TELEPORTING_IN_THIS_AREA);
            return false;
        }

        if (target.inObserverMode() || OlympiadManager.getInstance().isRegisteredInComp(target) || target.isInsideZone(ZoneType.NO_SUMMON_FRIEND) || target.isInsideZone(ZoneType.JAIL)) {
            creature.sendPacket(getSystemMessage(SystemMessageId.C1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING_OR_TELEPORTING).addString(target.getName()));
            return false;
        }

        final Instance instance = creature.getInstanceWorld();
        if (nonNull(instance) && !instance.isPlayerSummonAllowed()) {
            creature.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
            return false;
        }
        return true;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new CallPc(data);
        }

        @Override
        public String effectName() {
            return "call-pc";
        }
    }
}