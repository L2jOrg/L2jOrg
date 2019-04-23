package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw, UnAfraid
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType> {
    private final L2PcInstance _activeChar;

    private final int _relation;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _flRunSpd = 0;
    private final int _flWalkSpd = 0;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;
    private final int _enchantLevel;
    private final int _armorEnchant;
    private final byte[] _masks = new byte[]
            {
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00
            };
    private String _title;
    private int _initSize = 5;

    public UserInfo(L2PcInstance cha) {
        this(cha, true);
    }

    public UserInfo(L2PcInstance cha, boolean addAll) {
        _activeChar = cha;

        _relation = calculateRelation(cha);
        _moveMultiplier = cha.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = cha.isFlying() ? _runSpd : 0;
        _flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
        _enchantLevel = cha.getInventory().getWeaponEnchant();
        _armorEnchant = cha.getInventory().getArmorMinEnchant();

        _title = cha.getTitle();
        if (cha.isGM() && cha.isInvisible()) {
            _title = "[Invisible]";
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
            case BASIC_INFO: {
                _initSize += type.getBlockLength() + (_activeChar.getAppearance().getVisibleName().length() * 2);
                break;
            }
            case CLAN: {
                _initSize += type.getBlockLength() + (_title.length() * 2);
                break;
            }
            default: {
                _initSize += type.getBlockLength();
                break;
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.USER_INFO.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_initSize);
        packet.putShort((short) 24);
        packet.put(_masks);

        if (containsMask(UserInfoType.RELATION)) {
            packet.putInt(_relation);
        }

        if (containsMask(UserInfoType.BASIC_INFO)) {
            packet.putShort((short) (16 + (_activeChar.getAppearance().getVisibleName().length() * 2)));
            writeSizedString(_activeChar.getName(), packet);
            packet.put((byte) (_activeChar.isGM() ? 0x01 : 0x00));
            packet.put((byte) _activeChar.getRace().ordinal());
            packet.put((byte) (_activeChar.getAppearance().getSex() ? 0x01 : 0x00));
            packet.putInt(ClassId.getClassId(_activeChar.getBaseTemplate().getClassId().getId()).getRootClassId().getId());
            packet.putInt(_activeChar.getClassId().getId());
            packet.put((byte) _activeChar.getLevel());
        }

        if (containsMask(UserInfoType.BASE_STATS)) {
            packet.putShort((short) 18);
            packet.putShort((short) _activeChar.getSTR());
            packet.putShort((short) _activeChar.getDEX());
            packet.putShort((short) _activeChar.getCON());
            packet.putShort((short) _activeChar.getINT());
            packet.putShort((short) _activeChar.getWIT());
            packet.putShort((short) _activeChar.getMEN());
            packet.putShort((short) 0x00);
            packet.putShort((short) 0x00);
        }

        if (containsMask(UserInfoType.MAX_HPCPMP)) {
            packet.putShort((short) 14);
            packet.putInt(_activeChar.getMaxHp());
            packet.putInt(_activeChar.getMaxMp());
            packet.putInt(_activeChar.getMaxCp());
        }

        if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
            packet.putShort((short) 38);
            packet.putInt((int) Math.round(_activeChar.getCurrentHp()));
            packet.putInt((int) Math.round(_activeChar.getCurrentMp()));
            packet.putInt((int) Math.round(_activeChar.getCurrentCp()));
            packet.putLong(_activeChar.getSp());
            packet.putLong(_activeChar.getExp());
            packet.putDouble((float) (_activeChar.getExp() - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())));
        }

        if (containsMask(UserInfoType.ENCHANTLEVEL)) {
            packet.putShort((short) 4);
            packet.put((byte) _enchantLevel);
            packet.put((byte) _armorEnchant);
        }

        if (containsMask(UserInfoType.APPAREANCE)) {
            packet.putShort((short) 15);
            packet.putInt(_activeChar.getVisualHair());
            packet.putInt(_activeChar.getVisualHairColor());
            packet.putInt(_activeChar.getVisualFace());
            packet.put((byte) (_activeChar.isHairAccessoryEnabled() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.STATUS)) {
            packet.putShort((short) 6);
            packet.put((byte) _activeChar.getMountType().ordinal());
            packet.put((byte) _activeChar.getPrivateStoreType().getId());
            packet.put((byte) (_activeChar.hasDwarvenCraft() || (_activeChar.getSkillLevel(248) > 0) ? 1 : 0));
            packet.put((byte) 0x00);
        }

        if (containsMask(UserInfoType.STATS)) {
            packet.putShort((short) 56);
            packet.putShort((short) (_activeChar.getActiveWeaponItem() != null ? 40 : 20));
            packet.putInt(_activeChar.getPAtk());
            packet.putInt(_activeChar.getPAtkSpd());
            packet.putInt(_activeChar.getPDef());
            packet.putInt(_activeChar.getEvasionRate());
            packet.putInt(_activeChar.getAccuracy());
            packet.putInt(_activeChar.getCriticalHit());
            packet.putInt(_activeChar.getMAtk());
            packet.putInt(_activeChar.getMAtkSpd());
            packet.putInt(_activeChar.getPAtkSpd()); // Seems like atk speed - 1
            packet.putInt(_activeChar.getMagicEvasionRate());
            packet.putInt(_activeChar.getMDef());
            packet.putInt(_activeChar.getMagicAccuracy());
            packet.putInt(_activeChar.getMCriticalHit());
        }

        if (containsMask(UserInfoType.ELEMENTALS)) {
            packet.putShort((short) 14);
            packet.putShort((short) 0x00);
            packet.putShort((short) 0x00);
            packet.putShort((short) 0x00);
            packet.putShort((short) 0x00);
            packet.putShort((short) 0x00);
            packet.putShort((short) 0x00);
        }

        if (containsMask(UserInfoType.POSITION)) {
            packet.putShort((short) 18);
            packet.putInt(_activeChar.getX());
            packet.putInt(_activeChar.getY());
            packet.putInt(_activeChar.getZ());
            packet.putInt(_activeChar.isInVehicle() ? _activeChar.getVehicle().getObjectId() : 0);
        }

        if (containsMask(UserInfoType.SPEED)) {
            packet.putShort((short) 18);
            packet.putShort((short) _runSpd);
            packet.putShort((short) _walkSpd);
            packet.putShort((short) _swimRunSpd);
            packet.putShort((short) _swimWalkSpd);
            packet.putShort((short) _flRunSpd);
            packet.putShort((short) _flWalkSpd);
            packet.putShort((short) _flyRunSpd);
            packet.putShort((short) _flyWalkSpd);
        }

        if (containsMask(UserInfoType.MULTIPLIER)) {
            packet.putShort((short) 18);
            packet.putDouble(_moveMultiplier);
            packet.putDouble(_activeChar.getAttackSpeedMultiplier());
        }

        if (containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
            packet.putShort((short) 18);
            packet.putDouble(_activeChar.getCollisionRadius());
            packet.putDouble(_activeChar.getCollisionHeight());
        }

        if (containsMask(UserInfoType.ATK_ELEMENTAL)) {
            packet.putShort((short) 5);
            packet.put((byte) 0x00);
            packet.putShort((short) 0x00);
        }

        if (containsMask(UserInfoType.CLAN)) {
            packet.putShort((short) (32 + (_title.length() * 2)));
            writeSizedString(_title, packet);
            packet.putShort((short) _activeChar.getPledgeType());
            packet.putInt(_activeChar.getClanId());
            packet.putInt(_activeChar.getClanCrestLargeId());
            packet.putInt(_activeChar.getClanCrestId());
            packet.putInt(_activeChar.getClanPrivileges().getBitmask());
            packet.put((byte) (_activeChar.isClanLeader() ? 0x01 : 0x00));
            packet.putInt(_activeChar.getAllyId());
            packet.putInt(_activeChar.getAllyCrestId());
            packet.put((byte) (_activeChar.isInMatchingRoom() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.SOCIAL)) {
            packet.putShort((short) 22);
            packet.put((byte) _activeChar.getPvpFlag());
            packet.putInt(_activeChar.getReputation()); // Reputation
            packet.put((byte) (_activeChar.isNoble() ? 1 : 0));
            packet.put((byte) (_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) ? 2 : 0)); // 152 - Value for enabled changed to 2?
            packet.put((byte) _activeChar.getPledgeClass());
            packet.putInt(_activeChar.getPkKills());
            packet.putInt(_activeChar.getPvpKills());
            packet.putShort((short) _activeChar.getRecomLeft());
            packet.putShort((short) _activeChar.getRecomHave());
        }

        if (containsMask(UserInfoType.VITA_FAME)) {
            packet.putShort((short) 15);
            packet.putInt(_activeChar.getVitalityPoints());
            packet.put((byte) 0x00); // Vita Bonus
            packet.putInt(_activeChar.getFame());
            packet.putInt(_activeChar.getRaidbossPoints());
        }

        if (containsMask(UserInfoType.SLOTS)) {
            packet.putShort((short) 12); // 152
            packet.put((byte) _activeChar.getInventory().getTalismanSlots());
            packet.put((byte) _activeChar.getInventory().getBroochJewelSlots());
            packet.put((byte) _activeChar.getTeam().getId());
            packet.putInt(0x00);

            if (_activeChar.getInventory().getAgathionSlots() > 0) {
                packet.put((byte) 0x01); // Charm slots
                packet.put((byte) (_activeChar.getInventory().getAgathionSlots() - 1));
                packet.put((byte) _activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            } else {
                packet.put((byte) 0x00); // Charm slots
                packet.put((byte) 0x00);
                packet.put((byte) _activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            }
        }

        if (containsMask(UserInfoType.MOVEMENTS)) {
            packet.putShort((short) 4);
            packet.put((byte) (_activeChar.isInsideZone(ZoneId.WATER) ? 1 : _activeChar.isFlyingMounted() ? 2 : 0));
            packet.put((byte) (_activeChar.isRunning() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.COLOR)) {
            packet.putShort((short) 10);
            packet.putInt(_activeChar.getAppearance().getNameColor());
            packet.putInt(_activeChar.getAppearance().getTitleColor());
        }

        if (containsMask(UserInfoType.INVENTORY_LIMIT)) {
            packet.putShort((short) 9);
            packet.putShort((short) 0x00);
            packet.putShort((short) 0x00);
            packet.putShort((short) _activeChar.getInventoryLimit());
            packet.put((byte) (_activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_activeChar.getCursedWeaponEquippedId()) : 0));
        }

        if (containsMask(UserInfoType.TRUE_HERO)) {
            packet.putShort((short) 9);
            packet.putInt(0x00);
            packet.putShort((short) 0x00);
            packet.put((byte) (_activeChar.isTrueHero() ? 100 : 0x00));
        }

        if (containsMask(UserInfoType.ATT_SPIRITS)) // 152
        {
            packet.putShort((short) 26);
            packet.putInt(-1);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
        }

    }

    private int calculateRelation(L2PcInstance activeChar) {
        int relation = 0;
        final L2Party party = activeChar.getParty();
        final L2Clan clan = activeChar.getClan();

        if (party != null) {
            relation |= 0x08; // Party member
            if (party.getLeader() == _activeChar) {
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
