package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.mobius.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author Sdw, Mobius
 * @version Classic 2.0
 */
public class AcquireSkillList implements IClientOutgoingPacket
{
    final L2PcInstance _activeChar;
    final List<L2SkillLearn> _learnable;

    public AcquireSkillList(L2PcInstance activeChar)
    {
        _activeChar = activeChar;
        _learnable = SkillTreesData.getInstance().getAvailableSkills(activeChar, activeChar.getClassId(), false, false);
        _learnable.addAll(SkillTreesData.getInstance().getNextAvailableSkills(activeChar, activeChar.getClassId(), false, false));
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.ACQUIRE_SKILL_LIST.writeId(packet);

        packet.writeH(_learnable.size());
        for (L2SkillLearn skill : _learnable)
        {
            if (skill == null)
            {
                continue;
            }
            packet.writeD(skill.getSkillId());
            packet.writeH(skill.getSkillLevel());
            packet.writeQ(skill.getLevelUpSp());
            packet.writeC(skill.getGetLevel());
            packet.writeH(0x00); // Salvation: Changed from byte to short.
            if (skill.getRequiredItems().size() > 0)
            {
                for (ItemHolder item : skill.getRequiredItems())
                {
                    packet.writeC(0x01);
                    packet.writeD(item.getId());
                    packet.writeQ(item.getCount());
                }
            }
            else
            {
                packet.writeC(0x00);
            }
            packet.writeC(0x00);
        }
        return true;
    }
}
