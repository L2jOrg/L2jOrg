package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfoDetail extends IClientIncomingPacket
{
    private SkillEnchantType _type;
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _type = SkillEnchantType.values()[packet.getInt()];
        _skillId = packet.getInt();
        _skillLvl = packet.getShort();
        _skillSubLvl = packet.getShort();
    }

    @Override
    public void runImpl()
    {
        if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0))
        {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }
        activeChar.sendPacket(new ExEnchantSkillInfoDetail(_type, _skillId, _skillLvl, _skillSubLvl, activeChar));
    }
}
