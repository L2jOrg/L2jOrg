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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * @author Sdw, UnAfraid
 * @author JoeAlisson
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType> {
    private final Player player;

    private final int relation;
    private final int runSpd;
    private final int walkSpd;
    private final int swimRunSpd;
    private final int swimWalkSpd;
    private final int mountRunSpd = 0;
    private final int mountWalkSpd = 0;
    private final int flyRunSpd;
    private final int flyWalkSpd;
    private final double _moveMultiplier;
    private final int enchantLevel;
    private final int armorEnchant;
    private final byte[] mask = new byte[] { 0x00, 0x00, 0x00, 0x00 };
    private String title;
    private int initSize = 5;

    public UserInfo(Player cha) {
        this(cha, true);
    }

    public UserInfo(Player cha, UserInfoType... infoTypes) {
        this(cha, false);
        if(nonNull(infoTypes)) {
            for (UserInfoType infoType : infoTypes) {
              addComponentType(infoType);
            }
        }
    }

    public UserInfo(Player cha, boolean addAll) {
        player = cha;

        relation = calculateRelation(cha);
        _moveMultiplier = cha.getMovementSpeedMultiplier();
        runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        flyRunSpd = cha.isFlying() ? runSpd : 0;
        flyWalkSpd = cha.isFlying() ? walkSpd : 0;
        enchantLevel = cha.getInventory().getWeaponEnchant();
        armorEnchant = cha.getInventory().getArmorMaxEnchant();

        title = cha.getTitle();
        if (cha.isGM() && cha.isInvisible()) {
            title = "[Invisible]";
        }

        if (addAll) {
            addComponentType(UserInfoType.values());
        }
    }

    @Override
    protected byte[] getMasks() {
        return mask;
    }

    @Override
    protected void onNewMaskAdded(UserInfoType component) {
        calcBlockSize(component);
    }

    private void calcBlockSize(UserInfoType type) {
        switch (type) {
            case BASIC_INFO:
                initSize += type.getBlockLength() + (player.getAppearance().getVisibleName().length() * 2);
            case CLAN:
                initSize += type.getBlockLength() + (title.length() * 2);
            default:
                initSize += type.getBlockLength();
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.USER_INFO);

        writeInt(player.getObjectId());
        writeInt(initSize);
        writeShort(0x1B);
        writeBytes(mask);

        if (containsMask(UserInfoType.RELATION)) {
            writeInt(relation);
        }

        if (containsMask(UserInfoType.BASIC_INFO)) {
            writeShort(UserInfoType.BASIC_INFO.getBlockLength() + player.getAppearance().getVisibleName().length() * 2);
            writeSizedString(player.getName());
            writeByte(player.isGM());
            writeByte(player.getRace().ordinal());
            writeByte(player.getAppearance().isFemale());
            writeInt(player.getBaseTemplate().getClassId().getRootClassId().getId());
            writeInt(player.getClassId().getId());
            writeInt(player.getLevel());
        }

        if (containsMask(UserInfoType.BASE_STATS)) {
            writeShort(UserInfoType.BASE_STATS.getBlockLength());
            writeShort(player.getSTR());
            writeShort(player.getDEX());
            writeShort(player.getCON());
            writeShort(player.getINT());
            writeShort(player.getWIT());
            writeShort(player.getMEN());
            writeShort(0x01); // LUC
            writeShort(0x01); // CHA
        }

        if (containsMask(UserInfoType.MAX_HPCPMP)) {
            writeShort(UserInfoType.MAX_HPCPMP.getBlockLength());
            writeInt(player.getMaxHp());
            writeInt(player.getMaxMp());
            writeInt(player.getMaxCp());
        }

        if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
            writeShort(UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
            writeInt((int) Math.round(player.getCurrentHp()));
            writeInt((int) Math.round(player.getCurrentMp()));
            writeInt((int) Math.round(player.getCurrentCp()));
            writeLong(player.getSp());
            writeLong(player.getExp());
            writeDouble((float) (player.getExp() - LevelData.getInstance().getExpForLevel(player.getLevel())) / (LevelData.getInstance().getExpForLevel(player.getLevel() + 1) - LevelData.getInstance().getExpForLevel(player.getLevel())));
        }

        if (containsMask(UserInfoType.ENCHANTLEVEL)) {
            writeShort(UserInfoType.ENCHANTLEVEL.getBlockLength());
            writeByte(enchantLevel);
            writeByte(armorEnchant);
        }

        if (containsMask(UserInfoType.APPAREANCE)) {
            writeShort(UserInfoType.APPAREANCE.getBlockLength());
            writeInt(player.getVisualHair());
            writeInt(player.getVisualHairColor());
            writeInt(player.getVisualFace());
            writeByte(player.isHairAccessoryEnabled());
        }

        if (containsMask(UserInfoType.STATUS)) {
            writeShort(UserInfoType.STATUS.getBlockLength());
            writeByte(player.getMountType().ordinal());
            writeByte(player.getPrivateStoreType().getId());
            writeByte(player.hasDwarvenCraft() || player.getSkillLevel(248) > 0);
            writeByte(0x00); // Ability Points
        }

        if (containsMask(UserInfoType.STATS)) {
            writeShort(UserInfoType.STATS.getBlockLength());
            writeShort(player.getActiveWeaponItem() != null ? 40 : 20);
            writeInt(player.getPAtk());
            writeInt(player.getPAtkSpd());
            writeInt(player.getPDef());
            writeInt(player.getEvasionRate());
            writeInt(player.getAccuracy());
            writeInt(player.getCriticalHit());
            writeInt(player.getMAtk());
            writeInt(player.getMAtkSpd());
            writeInt(player.getPAtkSpd()); // Seems like atk speed - 1
            writeInt(player.getMagicEvasionRate());
            writeInt(player.getMDef());
            writeInt(player.getMagicAccuracy());
            writeInt(player.getMCriticalHit());
            writeInt(0x00); // pAtkAdd
            writeInt(0x00); // mAtkAdd
        }

        if (containsMask(UserInfoType.ELEMENTALS)) {
            writeShort(UserInfoType.ELEMENTALS.getBlockLength());
            writeShort(0x00); // Fire defense
            writeShort(0x00); // Water defense
            writeShort(0x00); // Wind defense
            writeShort(0x00); // Earth defense
            writeShort(0x00); // Holy defense
            writeShort(0x00); // dark defense
        }

        if (containsMask(UserInfoType.POSITION)) {
            writeShort(UserInfoType.POSITION.getBlockLength());
            writeInt(player.getX());
            writeInt(player.getY());
            writeInt(player.getZ());
            writeInt(player.isInVehicle() ? player.getVehicle().getObjectId() : 0);
        }

        if (containsMask(UserInfoType.SPEED)) {
            writeShort(UserInfoType.SPEED.getBlockLength());
            writeShort(runSpd);
            writeShort(walkSpd);
            writeShort(swimRunSpd);
            writeShort(swimWalkSpd);
            writeShort(mountRunSpd);
            writeShort(mountWalkSpd);
            writeShort(flyRunSpd);
            writeShort(flyWalkSpd);
        }

        if (containsMask(UserInfoType.MULTIPLIER)) {
            writeShort(UserInfoType.MULTIPLIER.getBlockLength());
            writeDouble(_moveMultiplier);
            writeDouble(player.getAttackSpeedMultiplier());
        }

        if (containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
            writeShort(UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
            writeDouble(player.getCollisionRadius());
            writeDouble(player.getCollisionHeight());
        }

        if (containsMask(UserInfoType.ATK_ELEMENTAL)) {
            writeShort(UserInfoType.ATK_ELEMENTAL.getBlockLength());
            writeByte(-2); // Attack Element
            writeShort( 0x00); // Attack element power
        }

        if (containsMask(UserInfoType.CLAN)) {
            writeShort(UserInfoType.CLAN.getBlockLength() + (title.length() * 2));
            writeSizedString(title);
            writeShort(player.getPledgeType());
            writeInt(player.getClanId());
            writeInt(player.getClanCrestLargeId());
            writeInt(player.getClanCrestId());
            writeInt(player.getClanPrivileges().getBitmask());
            writeByte(player.isClanLeader());
            writeInt(player.getAllyId());
            writeInt(player.getAllyCrestId());
            writeByte(player.isInMatchingRoom());
        }

        if (containsMask(UserInfoType.SOCIAL)) {
            writeShort(UserInfoType.SOCIAL.getBlockLength());
            writeByte(player.getPvpFlag());
            writeInt(player.getReputation()); // Reputation
            writeByte(player.isNoble());
            writeByte(player.isHero() || (player.isGM() && Config.GM_HERO_AURA) ? 2 : 0); // 152 - Value for enabled changed to 2?
            writeByte(player.getPledgeClass());
            writeInt(player.getPkKills());
            writeInt(player.getPvpKills());
            writeShort(player.getRecomLeft());
            writeShort(player.getRecomHave());
            writeInt(0x00); // unk 196
            writeInt(0x00); // dislike
        }

        if (containsMask(UserInfoType.VITA_FAME)) {
            writeShort(UserInfoType.VITA_FAME.getBlockLength()); // 196
            writeInt(player.getVitalityPoints());
            writeByte(0x00); // Vita Bonus
            writeInt(player.getFame());
            writeInt(player.getRaidbossPoints());
            writeByte(0x00); // unk
            writeByte(0x00); // unk
            writeShort(0x00); // unk 196
        }

        if (containsMask(UserInfoType.SLOTS)) {
            writeShort(UserInfoType.SLOTS.getBlockLength()); // 152
            writeByte(player.getInventory().getTalismanSlots());
            writeByte(player.getInventory().getBroochJewelSlots());
            writeByte(player.getTeam().getId());
            writeInt(0x00);

            if (player.getInventory().getAgathionSlots() > 0) {
                writeByte(0x01);
                writeByte(player.getInventory().getAgathionSlots() - 1);
            } else {
                writeByte(0x00);
                writeByte(0x00);
            }
            writeByte(player.getInventory().getArtifactSlots()); // Artifact set slots // 152
        }

        if (containsMask(UserInfoType.MOVEMENTS)) {
            writeShort(UserInfoType.MOVEMENTS.getBlockLength());
            writeByte(player.isInsideZone(ZoneType.WATER) ? 1 : player.isFlyingMounted() ? 2 : 0);
            writeByte(player.isRunning());
        }

        if (containsMask(UserInfoType.COLOR)) {
            writeShort(UserInfoType.COLOR.getBlockLength());
            writeInt(player.getAppearance().getNameColor());
            writeInt(player.getAppearance().getTitleColor());
        }

        if (containsMask(UserInfoType.INVENTORY_LIMIT)) {
            writeShort(UserInfoType.INVENTORY_LIMIT.getBlockLength());
            writeInt(0x00); // mount ??
            writeShort(player.getInventoryLimit());
            writeByte(0x00); // cursed weapon level
            writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.TRUE_HERO)) {
            writeShort(UserInfoType.TRUE_HERO.getBlockLength());
            writeByte(0x01);
            writeInt(0x00);
            writeByte(0x00);
            writeByte(0x00); // ceremony of chaos true hero
        }

        if (containsMask(UserInfoType.SPIRITS)) {
            writeShort(UserInfoType.SPIRITS.getBlockLength());
            writeInt((int) player.getActiveElementalSpiritAttack());
            writeInt((int) player.getFireSpiritDefense());
            writeInt((int) player.getWaterSpiritDefense());
            writeInt((int) player.getWindSpiritDefense());
            writeInt((int) player.getEarthSpiritDefense());
            writeInt(player.getActiveElementalSpiritType());
        }

        if (containsMask(UserInfoType.RANKER)) {
            writeShort(UserInfoType.RANKER.getBlockLength());
            writeInt(player.getRank() == 1 ? 1 : player.getRankRace() == 1 ? 2 : 0);
        }

        if(containsMask(UserInfoType.STATS_POINTS)) {
            writeShort(UserInfoType.STATS_POINTS.getBlockLength());
            var statsData = player.getStatsData();
            writeShort(statsData.getPoints());
            writeShort(statsData.getValue(BaseStats.STR));
            writeShort(statsData.getValue(BaseStats.DEX));
            writeShort(statsData.getValue(BaseStats.CON));
            writeShort(statsData.getValue(BaseStats.INT));
            writeShort(statsData.getValue(BaseStats.WIT));
            writeShort(statsData.getValue(BaseStats.MEN));
        }

        if(containsMask(UserInfoType.STATS_ABILITIES)) {
            writeShort(UserInfoType.STATS_ABILITIES.getBlockLength());
            writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_STR) + player.getHennaValue(BaseStats.STR));
            writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_DEX) + player.getHennaValue(BaseStats.DEX));
            writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_CON) + player.getHennaValue(BaseStats.CON));
            writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_INT) + player.getHennaValue(BaseStats.INT));
            writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_WIT) + player.getHennaValue(BaseStats.WIT));
            writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_MEN) + player.getHennaValue(BaseStats.MEN));
            writeShort(0x01);
            writeShort(0x01);
        }
    }

    private int calculateRelation(Player activeChar) {
        int relation = 0;
        final Party party = activeChar.getParty();
        final Clan clan = activeChar.getClan();
        if (party != null) {
            relation |= 0x08; // Party member
            if (party.getLeader() == this.player) {
                relation |= 0x10; // Party leader
            }
        }

        if (clan != null) {
            relation |= 0x20; // Clan member
            if (clan.getLeaderId() == activeChar.getObjectId()) {
                relation |= 0x40; // Clan leader
            }
        }

        if (activeChar.isInSiege()) {
            relation |= 0x80; // In siege
        }

        return relation;
    }
}
