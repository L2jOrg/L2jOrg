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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.enums.InventorySlot.*;

/**
 * @author JoeAlisson
 */
public class ExCharInfo extends ServerPacket {

    private static final InventorySlot[] PAPERDOLL_ORDER = new InventorySlot[]{PENDANT, HEAD, RIGHT_HAND, LEFT_HAND, GLOVES, CHEST, LEGS, FEET, CLOAK, TWO_HAND, HAIR, HAIR2};

    private final Player player;
    private int dynamicSize;

    public ExCharInfo(Player player) {
        this.player = player;

        dynamicSize += player.getAppearance().getVisibleTitle().length() * 2;
        dynamicSize += player.getCubics().size() * 2;
        dynamicSize += player.getEffectList().getCurrentAbnormalVisualEffects().size() * 2;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHAR_INFO);

        writeShort(0x151 + dynamicSize);

        var appearence = player.getAppearance();
        writeInt(player.getObjectId());
        writeShort(player.getRace().ordinal());
        writeByte(appearence.isFemale());
        writeInt(player.getBaseClass());

        writeShort(0x32);
        for (InventorySlot slot : PAPERDOLL_ORDER) {
            writeInt(player.getInventory().getPaperdollItemDisplayId(slot));
        }

        writeShort(0x1A);
        for (InventorySlot slot : getPaperdollOrderAugument()) { // normal
            var augment = player.getInventory().getPaperdollAugmentation(slot);
            writeInt(Util.zeroIfNullOrElse(augment, VariationInstance::getOption1Id));
        }
        for (InventorySlot slot : getPaperdollOrderAugument()) { // random
            var augment = player.getInventory().getPaperdollAugmentation(slot);
            writeInt(Util.zeroIfNullOrElse(augment, VariationInstance::getOption2Id));
        }

        writeByte(player.getInventory().getArmorMaxEnchant());

        writeShort(0x26); // slot item shape shift mask size
        writeInt(0x00); // right hand
        writeInt(0x00); // left hand
        writeInt(0x00); // two hand
        writeInt(0x00); // gloves
        writeInt(0x00); // chest
        writeInt(0x00); // legs
        writeInt(0x00); // feet
        writeInt(0x00); // hair
        writeInt(0x00); // hair 2

        writeByte(player.getPvpFlag());
        writeInt(player.getReputation());

        writeInt(player.getMAtkSpd());
        writeInt(player.getPAtkSpd());

        writeInt((int) player.getRunSpeed());
        writeInt((int) player.getWalkSpeed());
        writeInt((int) player.getSwimRunSpeed());
        writeInt((int) player.getSwimWalkSpeed());

        writeInt(0x00); // mount speed?
        writeInt(0x00); // mount speed?
        int flySpeed = player.isFlying() ? (int) player.getRunSpeed() : 0;
        writeInt(flySpeed);
        writeInt(flySpeed);

        writeFloat((float) player.getMovementSpeedMultiplier());
        writeFloat((float) player.getAttackSpeedMultiplier());

        writeFloat((float) player.getCollisionRadius());
        writeFloat((float) player.getCollisionHeight());

        writeInt(player.getVisualHair());
        writeInt(player.getVisualHairColor());
        writeInt(player.getVisualFace());

        writeSizedString(appearence.getVisibleTitle());

        writeInt(appearence.getVisibleClanId());
        writeInt(appearence.getVisibleClanCrestId());
        writeInt(appearence.getVisibleAllyId());
        writeInt(appearence.getVisibleAllyCrestId());

        writeByte(!player.isSitting());
        writeByte(player.isRunning());
        writeByte(player.isInCombat());
        writeByte(!player.isInOlympiadMode() && player.isAlikeDead());
        writeByte(player.getMountType().ordinal());
        writeByte(player.getPrivateStoreType().getId());

        writeInt(player.getCubics().size());
        player.getCubics().keySet().forEach(this::writeShort);

        writeByte(player.isInMatchingRoom());
        writeByte(player.isInsideZone(ZoneType.WATER) ? 1 : player.isFlyingMounted() ? 2 : 0);
        writeShort(player.getRecomHave());
        writeInt(player.getMountNpcId() == 0 ? 0 : player.getMountNpcId() + 1000000);
        writeInt(player.getActiveClass());
        writeInt(0x00); // foot effect

        writeByte(player.isMounted() ? 0 : player.getInventory().getWeaponEnchant());
        writeByte(player.getTeam().getId());

        writeInt(appearence.getVisibleClanLargeCrestId());
        writeByte(player.isNoble());
        writeByte((player.isHero() || (player.isGM() && Config.GM_HERO_AURA) ? 2 : 0));

        writeByte(player.isFishing());
        var baitLocation = player.getFishing().getBaitLocation();
        writeInt(baitLocation.getX());
        writeInt(baitLocation.getY());
        writeInt(baitLocation.getZ());

        writeInt(appearence.getNameColor());
        writeInt(player.getHeading());
        writeByte(player.getPledgeClass());
        writeShort(player.getPledgeType());

        writeInt(appearence.getTitleColor());
        writeByte(0x00); // cursed weapon level
        writeInt(zeroIfNullOrElse(player.getClan(), Clan::getReputationScore));
        writeInt(player.getTransformationDisplayId());
        writeInt(player.getAgathionId());

        writeByte(0x01); // pvp restrain status

        writeInt((int) Math.round(player.getCurrentCp()));
        writeInt(player.getMaxHp());
        writeInt((int) Math.round(player.getCurrentHp()));
        writeInt(player.getMaxMp());
        writeInt((int) Math.round(player.getCurrentMp()));
        writeByte(0x00); // lecture mark

        var abnormalVisualEffects = player.getEffectList().getCurrentAbnormalVisualEffects();
        writeInt(abnormalVisualEffects.size());
        abnormalVisualEffects.forEach(effect -> writeShort(effect.getClientId()));

        writeByte(0); // ceremony of chaos position
        writeByte(player.isHairAccessoryEnabled());
        writeByte(player.getAbilityPointsUsed());
        writeInt(0x00); // cursed weapon equipped id
        writeInt(0x00); // wait action id
        writeInt(player.getRank() == 1 ? 1 : player.getRankRace() == 1 ? 2 : 0);
        writeShort(0x00); // notoriety

        writeShort(0x16 + appearence.getVisibleName().length() * 2);
        writeByte(0x00); // create or update
        writeByte(0x00); // show spawn event

        ILocational loc = nonNull(player.getVehicle()) ? player.getInVehiclePosition() : player;
        writeInt(loc.getX());
        writeInt(loc.getY());
        writeInt(loc.getZ());
        writeInt(zeroIfNullOrElse(player.getVehicle(), WorldObject::getObjectId));

        writeSizedString(appearence.getVisibleName());
    }
}
