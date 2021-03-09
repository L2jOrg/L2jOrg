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
            addComponentType(infoTypes);
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
        initSize += switch (type) {
            case BASIC_INFO -> type.getBlockLength() + (player.getAppearance().getVisibleName().length() * 2);
            case CLAN ->  type.getBlockLength() + (title.length() * 2);
            default ->  type.getBlockLength();
        };
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.USER_INFO, buffer );

        buffer.writeInt(player.getObjectId());
        buffer.writeInt(initSize);
        buffer.writeShort(0x1C);
        buffer.writeBytes(mask);

        if (containsMask(UserInfoType.RELATION)) {
            buffer.writeInt(relation);
        }

        if (containsMask(UserInfoType.BASIC_INFO)) {
            buffer.writeShort(UserInfoType.BASIC_INFO.getBlockLength() + player.getAppearance().getVisibleName().length() * 2);
            buffer.writeSizedString(player.getName());
            buffer.writeByte(player.isGM());
            buffer.writeByte(player.getRace().ordinal());
            buffer.writeByte(player.getAppearance().isFemale());
            buffer.writeInt(player.getBaseTemplate().getClassId().getRootClassId().getId());
            buffer.writeInt(player.getClassId().getId());
            buffer.writeInt(player.getLevel());
            buffer.writeInt(-1); // unk
        }

        if (containsMask(UserInfoType.BASE_STATS)) {
            buffer.writeShort(UserInfoType.BASE_STATS.getBlockLength());
            buffer.writeShort(player.getSTR());
            buffer.writeShort(player.getDEX());
            buffer.writeShort(player.getCON());
            buffer.writeShort(player.getINT());
            buffer.writeShort(player.getWIT());
            buffer.writeShort(player.getMEN());
            buffer.writeShort(0x01); // LUC
            buffer.writeShort(0x01); // CHA
        }

        if (containsMask(UserInfoType.MAX_HPCPMP)) {
            buffer.writeShort(UserInfoType.MAX_HPCPMP.getBlockLength());
            buffer.writeInt(player.getMaxHp());
            buffer.writeInt(player.getMaxMp());
            buffer.writeInt(player.getMaxCp());
        }

        if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
            buffer.writeShort(UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
            buffer.writeInt((int) Math.round(player.getCurrentHp()));
            buffer.writeInt((int) Math.round(player.getCurrentMp()));
            buffer.writeInt((int) Math.round(player.getCurrentCp()));
            buffer.writeLong(player.getSp());
            buffer.writeLong(player.getExp());
            buffer.writeDouble((float) (player.getExp() - LevelData.getInstance().getExpForLevel(player.getLevel())) / (LevelData.getInstance().getExpForLevel(player.getLevel() + 1) - LevelData.getInstance().getExpForLevel(player.getLevel())));
        }

        if (containsMask(UserInfoType.ENCHANTLEVEL)) {
            buffer.writeShort(UserInfoType.ENCHANTLEVEL.getBlockLength());
            buffer.writeByte(enchantLevel);
            buffer.writeByte(armorEnchant);
        }

        if (containsMask(UserInfoType.APPAREANCE)) {
            buffer.writeShort(UserInfoType.APPAREANCE.getBlockLength());
            buffer.writeInt(player.getVisualHair());
            buffer.writeInt(player.getVisualHairColor());
            buffer.writeInt(player.getVisualFace());
            buffer.writeByte(player.isHairAccessoryEnabled());
        }

        if (containsMask(UserInfoType.STATUS)) {
            buffer.writeShort(UserInfoType.STATUS.getBlockLength());
            buffer.writeByte(player.getMountType().ordinal());
            buffer.writeByte(player.getPrivateStoreType().getId());
            buffer.writeByte(player.hasDwarvenCraft() || player.getSkillLevel(248) > 0);
            buffer.writeByte(0x00); // Ability Points
        }

        if (containsMask(UserInfoType.STATS)) {
            buffer.writeShort(UserInfoType.STATS.getBlockLength());
            buffer.writeShort(player.getActiveWeaponItem() != null ? 40 : 20);
            buffer.writeInt(player.getPAtk());
            buffer.writeInt(player.getPAtkSpd());
            buffer.writeInt(player.getPDef());
            buffer.writeInt(player.getEvasionRate());
            buffer.writeInt(player.getAccuracy());
            buffer.writeInt(player.getCriticalHit());
            buffer.writeInt(player.getMAtk());
            buffer.writeInt(player.getMAtkSpd());
            buffer.writeInt(player.getPAtkSpd()); // Seems like atk speed - 1
            buffer.writeInt(player.getMagicEvasionRate());
            buffer.writeInt(player.getMDef());
            buffer.writeInt(player.getMagicAccuracy());
            buffer.writeInt(player.getMCriticalHit());
            buffer.writeInt(0x00); // pAtkAdd
            buffer.writeInt(0x00); // mAtkAdd
        }

        if (containsMask(UserInfoType.ELEMENTALS)) {
            buffer.writeShort(UserInfoType.ELEMENTALS.getBlockLength());
            buffer.writeShort(0x00); // Fire defense
            buffer.writeShort(0x00); // Water defense
            buffer.writeShort(0x00); // Wind defense
            buffer.writeShort(0x00); // Earth defense
            buffer.writeShort(0x00); // Holy defense
            buffer.writeShort(0x00); // dark defense
        }

        if (containsMask(UserInfoType.POSITION)) {
            buffer.writeShort(UserInfoType.POSITION.getBlockLength());
            buffer.writeInt(player.getX());
            buffer.writeInt(player.getY());
            buffer.writeInt(player.getZ());
            buffer.writeInt(player.isInVehicle() ? player.getVehicle().getObjectId() : 0);
        }

        if (containsMask(UserInfoType.SPEED)) {
            buffer.writeShort(UserInfoType.SPEED.getBlockLength());
            buffer.writeShort(runSpd);
            buffer.writeShort(walkSpd);
            buffer.writeShort(swimRunSpd);
            buffer.writeShort(swimWalkSpd);
            buffer.writeShort(mountRunSpd);
            buffer.writeShort(mountWalkSpd);
            buffer.writeShort(flyRunSpd);
            buffer.writeShort(flyWalkSpd);
        }

        if (containsMask(UserInfoType.MULTIPLIER)) {
            buffer.writeShort(UserInfoType.MULTIPLIER.getBlockLength());
            buffer.writeDouble(_moveMultiplier);
            buffer.writeDouble(player.getAttackSpeedMultiplier());
        }

        if (containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
            buffer.writeShort(UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
            buffer.writeDouble(player.getCollisionRadius());
            buffer.writeDouble(player.getCollisionHeight());
        }

        if (containsMask(UserInfoType.ATK_ELEMENTAL)) {
            buffer.writeShort(UserInfoType.ATK_ELEMENTAL.getBlockLength());
            buffer.writeByte(-2); // Attack Element
            buffer.writeShort( 0x00); // Attack element power
        }

        if (containsMask(UserInfoType.CLAN)) {
            buffer.writeShort(UserInfoType.CLAN.getBlockLength() + (title.length() * 2));
            buffer.writeSizedString(title);
            buffer.writeShort(player.getPledgeType());
            buffer.writeInt(player.getClanId());
            buffer.writeInt(player.getClanCrestLargeId());
            buffer.writeInt(player.getClanCrestId());
            buffer.writeInt(player.getClanPrivileges().getBitmask());
            buffer.writeByte(player.isClanLeader());
            buffer.writeInt(player.getAllyId());
            buffer.writeInt(player.getAllyCrestId());
            buffer.writeByte(player.isInMatchingRoom());
        }

        if (containsMask(UserInfoType.SOCIAL)) {
            buffer.writeShort(UserInfoType.SOCIAL.getBlockLength());
            buffer.writeByte(player.getPvpFlag());
            buffer.writeInt(player.getReputation()); // Reputation
            buffer.writeByte(player.isNoble());
            buffer.writeByte(player.isHero() || (player.isGM() && Config.GM_HERO_AURA) ? 2 : 0); // 152 - Value for enabled changed to 2?
            buffer.writeByte(player.getPledgeClass());
            buffer.writeInt(player.getPkKills());
            buffer.writeInt(player.getPvpKills());
            buffer.writeShort(player.getRecomLeft());
            buffer.writeShort(player.getRecomHave());
            buffer.writeInt(0x00); // unk 196
            buffer.writeInt(0x00); // dislike
        }

        if (containsMask(UserInfoType.VITA_FAME)) {
            buffer.writeShort(UserInfoType.VITA_FAME.getBlockLength()); // 196
            buffer.writeInt(player.getVitalityPoints());
            buffer.writeByte(0x00); // Vita Bonus
            buffer.writeInt(player.getFame());
            buffer.writeInt(player.getRaidbossPoints());
            buffer.writeByte(0x00); // unk
            buffer.writeByte(0x00); // unk
            buffer.writeShort(0x00); // unk 196
        }

        if (containsMask(UserInfoType.SLOTS)) {
            buffer.writeShort(UserInfoType.SLOTS.getBlockLength()); // 152
            buffer.writeByte(player.getInventory().getTalismanSlots());
            buffer.writeByte(player.getInventory().getBroochJewelSlots());
            buffer.writeByte(player.getTeam().getId());
            buffer.writeInt(0x00);

            if (player.getInventory().getAgathionSlots() > 0) {
                buffer.writeByte(0x01);
                buffer.writeByte(player.getInventory().getAgathionSlots() - 1);
            } else {
                buffer.writeByte(0x00);
                buffer.writeByte(0x00);
            }
            buffer.writeByte(player.getInventory().getArtifactSlots()); // Artifact set slots // 152
        }

        if (containsMask(UserInfoType.MOVEMENTS)) {
            buffer.writeShort(UserInfoType.MOVEMENTS.getBlockLength());
            buffer.writeByte(player.isInsideZone(ZoneType.WATER) ? 1 : player.isFlyingMounted() ? 2 : 0);
            buffer.writeByte(player.isRunning());
        }

        if (containsMask(UserInfoType.COLOR)) {
            buffer.writeShort(UserInfoType.COLOR.getBlockLength());
            buffer.writeInt(player.getAppearance().getNameColor());
            buffer.writeInt(player.getAppearance().getTitleColor());
        }

        if (containsMask(UserInfoType.INVENTORY_LIMIT)) {
            buffer.writeShort(UserInfoType.INVENTORY_LIMIT.getBlockLength());
            buffer.writeInt(0x00); // mount ??
            buffer.writeShort(player.getInventoryLimit());
            buffer.writeByte(0x00); // cursed weapon level
            buffer.writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.TRUE_HERO)) {
            buffer.writeShort(UserInfoType.TRUE_HERO.getBlockLength());
            buffer.writeByte(0x01);
            buffer.writeInt(0x00);
            buffer.writeByte(0x00);
            buffer.writeByte(0x00); // ceremony of chaos true hero
        }

        if (containsMask(UserInfoType.SPIRITS)) {
            buffer.writeShort(UserInfoType.SPIRITS.getBlockLength());
            buffer.writeInt((int) player.getActiveElementalSpiritAttack());
            buffer.writeInt((int) player.getFireSpiritDefense());
            buffer.writeInt((int) player.getWaterSpiritDefense());
            buffer.writeInt((int) player.getWindSpiritDefense());
            buffer.writeInt((int) player.getEarthSpiritDefense());
            buffer.writeInt(player.getActiveElementalSpiritType());
        }

        if (containsMask(UserInfoType.RANKER)) {
            buffer.writeShort(UserInfoType.RANKER.getBlockLength());
            buffer.writeInt(player.getRank() == 1 ? 1 : player.getRankRace() == 1 ? 2 : 0);
        }

        if(containsMask(UserInfoType.STATS_POINTS)) {
            buffer.writeShort(UserInfoType.STATS_POINTS.getBlockLength());
            var statsData = player.getStatsData();
            buffer.writeShort(statsData.getPoints());
            buffer.writeShort(statsData.getValue(BaseStats.STR));
            buffer.writeShort(statsData.getValue(BaseStats.DEX));
            buffer.writeShort(statsData.getValue(BaseStats.CON));
            buffer.writeShort(statsData.getValue(BaseStats.INT));
            buffer.writeShort(statsData.getValue(BaseStats.WIT));
            buffer.writeShort(statsData.getValue(BaseStats.MEN));
        }

        if(containsMask(UserInfoType.STATS_ABILITIES)) {
            buffer.writeShort(UserInfoType.STATS_ABILITIES.getBlockLength());
            buffer.writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_STR) + player.getHennaValue(BaseStats.STR));
            buffer.writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_DEX) + player.getHennaValue(BaseStats.DEX));
            buffer.writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_CON) + player.getHennaValue(BaseStats.CON));
            buffer.writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_INT) + player.getHennaValue(BaseStats.INT));
            buffer.writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_WIT) + player.getHennaValue(BaseStats.WIT));
            buffer.writeShort((short) Stat.defaultValue(player, Optional.empty(), Stat.STAT_MEN) + player.getHennaValue(BaseStats.MEN));
            buffer.writeShort(0x01);
            buffer.writeShort(0x01);
        }
        
        if(containsMask(UserInfoType.ELIXIR_USED) && player.getStatsData().getElixirsPoints() > 0) {
            buffer.writeShort(player.getStatsData().getElixirsPoints());
            buffer.writeShort(0x00);
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
