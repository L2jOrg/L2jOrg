package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class GMViewCharacterInfo extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;

    public GMViewCharacterInfo(L2PcInstance cha) {
        _activeChar = cha;
        _moveMultiplier = cha.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = cha.isFlying() ? _runSpd : 0;
        _flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_VIEW_CHARACTER_INFO.writeId(packet);

        packet.putInt(_activeChar.getX());
        packet.putInt(_activeChar.getY());
        packet.putInt(_activeChar.getZ());
        packet.putInt(_activeChar.getHeading());
        packet.putInt(_activeChar.getObjectId());
        writeString(_activeChar.getName(), packet);
        packet.putInt(_activeChar.getRace().ordinal());
        packet.putInt(_activeChar.getAppearance().getSex() ? 1 : 0);
        packet.putInt(_activeChar.getClassId().getId());
        packet.putInt(_activeChar.getLevel());
        packet.putLong(_activeChar.getExp());
        packet.putDouble((float) (_activeChar.getExp() - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel()))); // High Five exp %
        packet.putInt(_activeChar.getSTR());
        packet.putInt(_activeChar.getDEX());
        packet.putInt(_activeChar.getCON());
        packet.putInt(_activeChar.getINT());
        packet.putInt(_activeChar.getWIT());
        packet.putInt(_activeChar.getMEN());
        packet.putInt(0x00); // LUC
        packet.putInt(0x00); // CHA
        packet.putInt(_activeChar.getMaxHp());
        packet.putInt((int) _activeChar.getCurrentHp());
        packet.putInt(_activeChar.getMaxMp());
        packet.putInt((int) _activeChar.getCurrentMp());
        packet.putLong(_activeChar.getSp());
        packet.putInt(_activeChar.getCurrentLoad());
        packet.putInt(_activeChar.getMaxLoad());
        packet.putInt(_activeChar.getPkKills());

        for (int slot : getPaperdollOrder()) {
            packet.putInt(_activeChar.getInventory().getPaperdollObjectId(slot));
        }

        for (int slot : getPaperdollOrder()) {
            packet.putInt(_activeChar.getInventory().getPaperdollItemDisplayId(slot));
        }

        for (int slot : getPaperdollOrder()) {
            final VariationInstance augment = _activeChar.getInventory().getPaperdollAugmentation(slot);
            packet.putInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
            packet.putInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
        }

        packet.put((byte) _activeChar.getInventory().getTalismanSlots()); // CT2.3
        packet.put((byte)(_activeChar.getInventory().canEquipCloak() ? 1 : 0)); // CT2.3
        packet.putInt(0x00);
        packet.putShort((short) 0x00);
        packet.putInt(_activeChar.getPAtk());
        packet.putInt(_activeChar.getPAtkSpd());
        packet.putInt(_activeChar.getPDef());
        packet.putInt(_activeChar.getEvasionRate());
        packet.putInt(_activeChar.getAccuracy());
        packet.putInt(_activeChar.getCriticalHit());
        packet.putInt(_activeChar.getMAtk());

        packet.putInt(_activeChar.getMAtkSpd());
        packet.putInt(_activeChar.getPAtkSpd());

        packet.putInt(_activeChar.getMDef());
        packet.putInt(_activeChar.getMagicEvasionRate());
        packet.putInt(_activeChar.getMagicAccuracy());
        packet.putInt(_activeChar.getMCriticalHit());

        packet.putInt(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
        packet.putInt(_activeChar.getReputation());

        packet.putInt(_runSpd);
        packet.putInt(_walkSpd);
        packet.putInt(_swimRunSpd);
        packet.putInt(_swimWalkSpd);
        packet.putInt(_flyRunSpd);
        packet.putInt(_flyWalkSpd);
        packet.putInt(_flyRunSpd);
        packet.putInt(_flyWalkSpd);
        packet.putDouble(_moveMultiplier);
        packet.putDouble(_activeChar.getAttackSpeedMultiplier()); // 2.9);//
        packet.putDouble(_activeChar.getCollisionRadius()); // scale
        packet.putDouble(_activeChar.getCollisionHeight()); // y offset ??!? fem dwarf 4033
        packet.putInt(_activeChar.getAppearance().getHairStyle());
        packet.putInt(_activeChar.getAppearance().getHairColor());
        packet.putInt(_activeChar.getAppearance().getFace());
        packet.putInt(_activeChar.isGM() ? 0x01 : 0x00); // builder level

        writeString(_activeChar.getTitle(), packet);
        packet.putInt(_activeChar.getClanId()); // pledge id
        packet.putInt(_activeChar.getClanCrestId()); // pledge crest id
        packet.putInt(_activeChar.getAllyId()); // ally id
        packet.put((byte) _activeChar.getMountType().ordinal()); // mount type
        packet.put((byte) _activeChar.getPrivateStoreType().getId());
        packet.put((byte)(_activeChar.hasDwarvenCraft() ? 1 : 0));
        packet.putInt(_activeChar.getPkKills());
        packet.putInt(_activeChar.getPvpKills());

        packet.putShort((short) _activeChar.getRecomLeft());
        packet.putShort((short) _activeChar.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
        packet.putInt(_activeChar.getClassId().getId());
        packet.putInt(0x00); // special effects? circles around player...
        packet.putInt(_activeChar.getMaxCp());
        packet.putInt((int) _activeChar.getCurrentCp());

        packet.put((byte)(_activeChar.isRunning() ? 0x01 : 0x00)); // changes the Speed display on Status Window

        packet.put((byte) 321);

        packet.putInt(_activeChar.getPledgeClass()); // changes the text above CP on Status Window

        packet.put((byte) (_activeChar.isNoble() ? 0x01 : 0x00));
        packet.put((byte) (_activeChar.isHero() ? 0x01 : 0x00));

        packet.putInt(_activeChar.getAppearance().getNameColor());
        packet.putInt(_activeChar.getAppearance().getTitleColor());

        final AttributeType attackAttribute = _activeChar.getAttackElement();
        packet.putShort((short) attackAttribute.getClientId());
        packet.putShort((short) _activeChar.getAttackElementValue(attackAttribute));
        for (AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            packet.putShort((short) _activeChar.getDefenseElementValue(type));
        }
        packet.putInt(_activeChar.getFame());
        packet.putInt(_activeChar.getVitalityPoints());
        packet.putInt(0x00);
        packet.putInt(0x00);
    }

    @Override
    protected int size(L2GameClient client) {
        return 360 + (AttributeType.ATTRIBUTE_TYPES.length + _activeChar.getName().length() + _activeChar.getTitle().length()) * 2 + PAPERDOLL_ORDER.length * 16 ;
    }
}
