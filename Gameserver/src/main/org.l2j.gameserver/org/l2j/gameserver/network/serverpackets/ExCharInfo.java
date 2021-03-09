/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
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
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_CHAR_INFO, buffer );

        //0x151 272 protocol
        //0x155 286 protocol or +4 it's from unk dynamic size
        buffer.writeShort(0x155 + dynamicSize);

        var appearence = player.getAppearance();
        buffer.writeInt(player.getObjectId());
        buffer.writeShort(player.getRace().ordinal());
        buffer.writeByte(appearence.isFemale());
        buffer.writeInt(player.getBaseClass());

        buffer.writeShort(0x32);
        for (InventorySlot slot : PAPERDOLL_ORDER) {
            buffer.writeInt(player.getInventory().getPaperdollItemDisplayId(slot));
        }

        buffer.writeShort(0x1A);
        for (InventorySlot slot : getPaperdollOrderAugument()) { // normal
            var augment = player.getInventory().getPaperdollAugmentation(slot);
            buffer.writeInt(zeroIfNullOrElse(augment, VariationInstance::getOption1Id));
        }
        for (InventorySlot slot : getPaperdollOrderAugument()) { // random
            var augment = player.getInventory().getPaperdollAugmentation(slot);
            buffer.writeInt(zeroIfNullOrElse(augment, VariationInstance::getOption2Id));
        }

        buffer.writeByte(player.getInventory().getArmorMaxEnchant());

        buffer.writeShort(0x26); // slot item shape shift mask size
        buffer.writeInt(0x00); // right hand
        buffer.writeInt(0x00); // left hand
        buffer.writeInt(0x00); // two hand
        buffer.writeInt(0x00); // gloves
        buffer.writeInt(0x00); // chest
        buffer.writeInt(0x00); // legs
        buffer.writeInt(0x00); // feet
        buffer.writeInt(0x00); // hair
        buffer.writeInt(0x00); // hair 2

        buffer.writeByte(player.getPvpFlag());
        buffer.writeInt(player.getReputation());

        buffer.writeInt(player.getMAtkSpd());
        buffer.writeInt(player.getPAtkSpd());

        buffer.writeInt((int) player.getRunSpeed());
        buffer.writeInt((int) player.getWalkSpeed());
        buffer.writeInt((int) player.getSwimRunSpeed());
        buffer.writeInt((int) player.getSwimWalkSpeed());

        buffer.writeInt(0x00); // mount speed?
        buffer.writeInt(0x00); // mount speed?
        int flySpeed = player.isFlying() ? (int) player.getRunSpeed() : 0;
        buffer.writeInt(flySpeed);
        buffer.writeInt(flySpeed);

        buffer.writeFloat((float) player.getMovementSpeedMultiplier());
        buffer.writeFloat((float) player.getAttackSpeedMultiplier());

        buffer.writeFloat((float) player.getCollisionRadius());
        buffer.writeFloat((float) player.getCollisionHeight());

        buffer.writeInt(player.getVisualHair());
        buffer.writeInt(player.getVisualHairColor());
        buffer.writeInt(player.getVisualFace());

        buffer.writeSizedString(appearence.getVisibleTitle());

        buffer.writeInt(appearence.getVisibleClanId());
        buffer.writeInt(appearence.getVisibleClanCrestId());
        buffer.writeInt(appearence.getVisibleAllyId());
        buffer.writeInt(appearence.getVisibleAllyCrestId());

        buffer.writeByte(!player.isSitting());
        buffer.writeByte(player.isRunning());
        buffer.writeByte(player.isInCombat());
        buffer.writeByte(!player.isInOlympiadMode() && player.isAlikeDead());
        buffer.writeByte(player.getMountType().ordinal());
        buffer.writeByte(player.getPrivateStoreType().getId());

        buffer.writeInt(player.getCubics().size());
        player.getCubics().keySet().forEach(buffer::writeShort);

        buffer.writeByte(player.isInMatchingRoom());
        buffer.writeByte(player.isInsideZone(ZoneType.WATER) ? 1 : player.isFlyingMounted() ? 2 : 0);
        buffer.writeShort(player.getRecomHave());
        buffer.writeInt(player.getMountNpcId() == 0 ? 0 : player.getMountNpcId() + 1000000);
        buffer.writeInt(player.getActiveClass());
        buffer.writeInt(0x00); // foot effect

        buffer.writeByte(player.isMounted() ? 0 : player.getInventory().getWeaponEnchant());
        buffer.writeByte(player.getTeam().getId());

        buffer.writeInt(appearence.getVisibleClanLargeCrestId());
        buffer.writeByte(player.isNoble());
        buffer.writeByte((player.isHero() || (player.isGM() && Config.GM_HERO_AURA) ? 2 : 0));

        buffer.writeByte(player.isFishing());
        var baitLocation = player.getFishing().getBaitLocation();
        buffer.writeInt(baitLocation.getX());
        buffer.writeInt(baitLocation.getY());
        buffer.writeInt(baitLocation.getZ());

        buffer.writeInt(appearence.getNameColor());
        buffer.writeInt(player.getHeading());
        buffer.writeByte(player.getPledgeClass());
        buffer.writeShort(player.getPledgeType());

        buffer.writeInt(appearence.getTitleColor());
        buffer.writeByte(0x00); // cursed weapon level
        buffer.writeInt(zeroIfNullOrElse(player.getClan(), Clan::getReputationScore));
        buffer.writeInt(player.getTransformationDisplayId());
        buffer.writeInt(player.getAgathionId());

        buffer.writeByte(0x01); // pvp restrain status

        buffer.writeInt((int) Math.round(player.getCurrentCp()));
        buffer.writeInt(player.getMaxHp());
        buffer.writeInt((int) Math.round(player.getCurrentHp()));
        buffer.writeInt(player.getMaxMp());
        buffer.writeInt((int) Math.round(player.getCurrentMp()));
        buffer.writeByte(0x00); // lecture mark

        var abnormalVisualEffects = player.getEffectList().getCurrentAbnormalVisualEffects();
        buffer.writeInt(abnormalVisualEffects.size());
        for (AbnormalVisualEffect effect : abnormalVisualEffects) {
            buffer.writeShort(effect.getClientId());
        }

        buffer.writeByte(0); // ceremony of chaos position
        buffer.writeByte(player.isHairAccessoryEnabled());
        buffer.writeByte(player.getAbilityPointsUsed());
        buffer.writeInt(0x00); // cursed weapon equipped id
        buffer.writeInt(0x00); // wait action id
        buffer.writeInt(player.getRank() == 1 ? 1 : player.getRankRace() == 1 ? 2 : 0);
        buffer.writeShort(0x00); // notoriety

        buffer.writeInt(-1); // fix unk new (286 protocol)

        buffer.writeShort(0x16 + appearence.getVisibleName().length() * 2);
        buffer.writeByte(0x00); // create or update
        buffer.writeByte(0x00); // show spawn event

        ILocational loc = nonNull(player.getVehicle()) ? player.getInVehiclePosition() : player;
        buffer.writeInt(loc.getX());
        buffer.writeInt(loc.getY());
        buffer.writeInt(loc.getZ());
        buffer.writeInt(zeroIfNullOrElse(player.getVehicle(), WorldObject::getObjectId));

        buffer.writeSizedString(appearence.getVisibleName());
    }
}
