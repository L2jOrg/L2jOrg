package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfoDetail extends IClientIncomingPacket {
    private SkillEnchantType _type;
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;

    @Override
    public void readImpl() {
        _type = SkillEnchantType.values()[readInt()];
        _skillId = readInt();
        _skillLvl = readShort();
        _skillSubLvl = readShort();
    }

    @Override
    public void runImpl() {
        if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0)) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.sendPacket(new ExEnchantSkillInfoDetail(_type, _skillId, _skillLvl, _skillSubLvl, activeChar));
    }
}
