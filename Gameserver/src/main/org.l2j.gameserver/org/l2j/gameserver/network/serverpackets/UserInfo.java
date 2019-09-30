package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.nonNull;

/**
 * @author Sdw, UnAfraid
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType> {
    private final Player activeChar;

    private final int _relation;
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
    private final byte[] _masks = new byte[] { 0x00, 0x00, 0x00, 0x00 };
    private String title;
    private int _initSize = 5;

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
        activeChar = cha;

        _relation = calculateRelation(cha);
        _moveMultiplier = cha.getMovementSpeedMultiplier();
        runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        flyRunSpd = cha.isFlying() ? runSpd : 0;
        flyWalkSpd = cha.isFlying() ? walkSpd : 0;
        enchantLevel = cha.getInventory().getWeaponEnchant();
        armorEnchant = cha.getInventory().getArmorMinEnchant();

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
        return _masks;
    }

    @Override
    protected void onNewMaskAdded(UserInfoType component) {
        calcBlockSize(component);
    }

    private void calcBlockSize(UserInfoType type) {
        switch (type) {
            case BASIC_INFO:
                _initSize += type.getBlockLength() + (activeChar.getAppearance().getVisibleName().length() * 2);
            case CLAN:
                _initSize += type.getBlockLength() + (title.length() * 2);
            default:
                _initSize += type.getBlockLength();
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.USER_INFO);

        writeInt(activeChar.getObjectId());
        writeInt(_initSize);
        writeShort((short) 25);
        writeBytes(_masks);

        if (containsMask(UserInfoType.RELATION)) {
            writeInt(_relation);
        }

        if (containsMask(UserInfoType.BASIC_INFO)) {
            writeShort(UserInfoType.BASIC_INFO.getBlockLength() + activeChar.getAppearance().getVisibleName().length() * 2);
            writeSizedString(activeChar.getName());
            writeByte(activeChar.isGM());
            writeByte(activeChar.getRace().ordinal());
            writeByte(activeChar.getAppearance().isFemale());
            writeInt(activeChar.getBaseTemplate().getClassId().getRootClassId().getId());
            writeInt(activeChar.getClassId().getId());
            writeByte(activeChar.getLevel());
        }

        if (containsMask(UserInfoType.BASE_STATS)) {
            writeShort(UserInfoType.BASE_STATS.getBlockLength());
            writeShort(activeChar.getSTR());
            writeShort(activeChar.getDEX());
            writeShort(activeChar.getCON());
            writeShort(activeChar.getINT());
            writeShort(activeChar.getWIT());
            writeShort(activeChar.getMEN());
            writeShort(0x01); // LUC
            writeShort(0x01); // CHA
        }

        if (containsMask(UserInfoType.MAX_HPCPMP)) {
            writeShort(UserInfoType.MAX_HPCPMP.getBlockLength());
            writeInt(activeChar.getMaxHp());
            writeInt(activeChar.getMaxMp());
            writeInt(activeChar.getMaxCp());
        }

        if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
            writeShort(UserInfoType.CURRENT_HPMPCP_EXP_SP.getBlockLength());
            writeInt((int) Math.round(activeChar.getCurrentHp()));
            writeInt((int) Math.round(activeChar.getCurrentMp()));
            writeInt((int) Math.round(activeChar.getCurrentCp()));
            writeLong(activeChar.getSp());
            writeLong(activeChar.getExp());
            writeDouble((float) (activeChar.getExp() - ExperienceData.getInstance().getExpForLevel(activeChar.getLevel())) / (ExperienceData.getInstance().getExpForLevel(activeChar.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(activeChar.getLevel())));
        }

        if (containsMask(UserInfoType.ENCHANTLEVEL)) {
            writeShort(UserInfoType.ENCHANTLEVEL.getBlockLength());
            writeByte(enchantLevel);
            writeByte(armorEnchant);
        }

        if (containsMask(UserInfoType.APPAREANCE)) {
            writeShort(UserInfoType.APPAREANCE.getBlockLength());
            writeInt(activeChar.getVisualHair());
            writeInt(activeChar.getVisualHairColor());
            writeInt(activeChar.getVisualFace());
            writeByte(activeChar.isHairAccessoryEnabled());
        }

        if (containsMask(UserInfoType.STATUS)) {
            writeShort(UserInfoType.STATUS.getBlockLength());
            writeByte(activeChar.getMountType().ordinal());
            writeByte(activeChar.getPrivateStoreType().getId());
            writeByte(activeChar.hasDwarvenCraft() || activeChar.getSkillLevel(248) > 0);
            writeByte(0x00); // Ability Points
        }

        if (containsMask(UserInfoType.STATS)) {
            writeShort(UserInfoType.STATS.getBlockLength());
            writeShort(activeChar.getActiveWeaponItem() != null ? 40 : 20);
            writeInt(activeChar.getPAtk());
            writeInt(activeChar.getPAtkSpd());
            writeInt(activeChar.getPDef());
            writeInt(activeChar.getEvasionRate());
            writeInt(activeChar.getAccuracy());
            writeInt(activeChar.getCriticalHit());
            writeInt(activeChar.getMAtk());
            writeInt(activeChar.getMAtkSpd());
            writeInt(activeChar.getPAtkSpd()); // Seems like atk speed - 1
            writeInt(activeChar.getMagicEvasionRate());
            writeInt(activeChar.getMDef());
            writeInt(activeChar.getMagicAccuracy());
            writeInt(activeChar.getMCriticalHit());
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
            writeInt(activeChar.getX());
            writeInt(activeChar.getY());
            writeInt(activeChar.getZ());
            writeInt(activeChar.isInVehicle() ? activeChar.getVehicle().getObjectId() : 0);
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
            writeDouble(activeChar.getAttackSpeedMultiplier());
        }

        if (containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
            writeShort(UserInfoType.COL_RADIUS_HEIGHT.getBlockLength());
            writeDouble(activeChar.getCollisionRadius());
            writeDouble(activeChar.getCollisionHeight());
        }

        if (containsMask(UserInfoType.ATK_ELEMENTAL)) {
            writeShort(UserInfoType.ATK_ELEMENTAL.getBlockLength());
            writeByte(-2); // Attack Element
            writeShort( 0x00); // Attack element power
        }

        if (containsMask(UserInfoType.CLAN)) {
            writeShort(UserInfoType.CLAN.getBlockLength() + (title.length() * 2));
            writeSizedString(title);
            writeShort(activeChar.getPledgeType());
            writeInt(activeChar.getClanId());
            writeInt(activeChar.getClanCrestLargeId());
            writeInt(activeChar.getClanCrestId());
            writeInt(activeChar.getClanPrivileges().getBitmask());
            writeByte(activeChar.isClanLeader());
            writeInt(activeChar.getAllyId());
            writeInt(activeChar.getAllyCrestId());
            writeByte(activeChar.isInMatchingRoom());
        }

        if (containsMask(UserInfoType.SOCIAL)) {
            writeShort(UserInfoType.SOCIAL.getBlockLength());
            writeByte(activeChar.getPvpFlag());
            writeInt(activeChar.getReputation()); // Reputation
            writeByte(activeChar.isNoble());
            writeByte(activeChar.isHero() || (activeChar.isGM() && Config.GM_HERO_AURA) ? 2 : 0); // 152 - Value for enabled changed to 2?
            writeByte(activeChar.getPledgeClass());
            writeInt(activeChar.getPkKills());
            writeInt(activeChar.getPvpKills());
            writeShort(activeChar.getRecomLeft());
            writeShort(activeChar.getRecomHave());
            writeInt(0x00); // unk 196
            writeInt(0x00);
        }

        if (containsMask(UserInfoType.VITA_FAME)) {
            writeShort(UserInfoType.VITA_FAME.getBlockLength()); // 196
            writeInt(activeChar.getVitalityPoints());
            writeByte(0x00); // Vita Bonus
            writeInt(activeChar.getFame());
            writeInt(activeChar.getRaidbossPoints());
            writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.SLOTS)) {
            writeShort(UserInfoType.SLOTS.getBlockLength()); // 152
            writeByte(activeChar.getInventory().getTalismanSlots());
            writeByte(activeChar.getInventory().getBroochJewelSlots());
            writeByte(activeChar.getTeam().getId());
            writeInt(0x00);

            if (activeChar.getInventory().getAgathionSlots() > 0) {
                writeByte(0x01);
                writeByte(activeChar.getInventory().getAgathionSlots() - 1);
            } else {
                writeByte(0x00);
                writeByte(0x00);
            }
            writeByte(activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
        }

        if (containsMask(UserInfoType.MOVEMENTS)) {
            writeShort(UserInfoType.MOVEMENTS.getBlockLength());
            writeByte(activeChar.isInsideZone(ZoneType.WATER) ? 1 : activeChar.isFlyingMounted() ? 2 : 0);
            writeByte(activeChar.isRunning());
        }

        if (containsMask(UserInfoType.COLOR)) {
            writeShort(UserInfoType.COLOR.getBlockLength());
            writeInt(activeChar.getAppearance().getNameColor());
            writeInt(activeChar.getAppearance().getTitleColor());
        }

        if (containsMask(UserInfoType.INVENTORY_LIMIT)) {
            writeShort(UserInfoType.INVENTORY_LIMIT.getBlockLength());
            writeInt(0x00); // mount ??
            writeShort(activeChar.getInventoryLimit());
            writeByte((activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(activeChar.getCursedWeaponEquippedId()) : 0));
            writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.TRUE_HERO)) {
            writeShort(UserInfoType.TRUE_HERO.getBlockLength());
            writeByte(0x01);
            writeInt(0x00);
            writeByte(0x00);
            writeByte(activeChar.isTrueHero() ? 100 : 0x00);
        }

        if (containsMask(UserInfoType.SPIRITS)) {
            writeShort(UserInfoType.SPIRITS.getBlockLength());
            writeInt((int) activeChar.getActiveElementalSpiritAttack());
            writeInt((int) activeChar.getFireSpiritDefense());
            writeInt((int) activeChar.getWaterSpiritDefense());
            writeInt((int) activeChar.getWindSpiritDefense());
            writeInt((int) activeChar.getEarthSpiritDefense());
            writeInt(activeChar.getActiveElementalSpiritType());
        }

        if (containsMask(UserInfoType.UNK)) {
            writeShort(6); // 196
            writeInt(0x00); // 196
        }
    }

    private int calculateRelation(Player activeChar) {
        int relation = 0;
        final Party party = activeChar.getParty();
        final Clan clan = activeChar.getClan();

        if (party != null) {
            relation |= 0x08; // Party member
            if (party.getLeader() == this.activeChar) {
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
