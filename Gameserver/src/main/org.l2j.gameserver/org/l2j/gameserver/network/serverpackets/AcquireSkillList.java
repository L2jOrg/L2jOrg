package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Sdw, Mobius
 * @version Classic 2.0
 */
public class AcquireSkillList extends IClientOutgoingPacket {
    final L2PcInstance _activeChar;
    final List<L2SkillLearn> _learnable;

    public AcquireSkillList(L2PcInstance activeChar) {
        _activeChar = activeChar;
        _learnable = SkillTreesData.getInstance().getAvailableSkills(activeChar, activeChar.getClassId(), false, false);
        _learnable.addAll(SkillTreesData.getInstance().getNextAvailableSkills(activeChar, activeChar.getClassId(), false, false));
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ACQUIRE_SKILL_LIST.writeId(packet);

        packet.putShort((short) _learnable.size());
        for (L2SkillLearn skill : _learnable) {
            if (skill == null) {
                continue;
            }
            packet.putInt(skill.getSkillId());
            packet.putShort((short) skill.getSkillLevel());
            packet.putLong(skill.getLevelUpSp());
            packet.put((byte) skill.getGetLevel());
            packet.putShort((short) 0x00); // Salvation: Changed from byte to short.
            if (skill.getRequiredItems().size() > 0) {
                for (ItemHolder item : skill.getRequiredItems()) {
                    packet.put((byte) 0x01);
                    packet.putInt(item.getId());
                    packet.putLong(item.getCount());
                }
            } else {
                packet.put((byte) 0x00);
            }
            packet.put((byte) 0x00);
        }
    }
}
