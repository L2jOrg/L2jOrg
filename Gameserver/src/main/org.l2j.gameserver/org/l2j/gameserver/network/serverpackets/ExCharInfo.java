package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.computeIfNonNull;
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
        writeId(ServerPacketId.EX_CHAR_INFO);

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

        writeByte(player.getInventory().getArmorMinEnchant());

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

        writeInt(player.getVisualFace());
        writeInt(player.getVisualHair());
        writeInt(player.getVisualHairColor());

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
        writeByte((player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId()) : 0));
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

        var member = computeIfNonNull(player.getEvent(CeremonyOfChaosEvent.class), ev -> ev.getMember(player.getObjectId()));
        writeByte(nonNull(member) ? member.getPosition() : player.isTrueHero() ? 100 : 0);
        writeByte(player.isHairAccessoryEnabled());
        writeByte(player.getAbilityPointsUsed());
        writeInt(player.getCursedWeaponEquippedId());
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
