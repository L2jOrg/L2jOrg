package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Sdw, Mobius
 * @version Classic 2.0
 */
public class AcquireSkillList extends ServerPacket {
    final Player player;
    final List<SkillLearn> _learnable;

    public AcquireSkillList(Player player) {
        this.player = player;
        _learnable = SkillTreesData.getInstance().getAvailableSkills(player, player.getClassId(), false, false);
        _learnable.addAll(SkillTreesData.getInstance().getNextAvailableSkills(player, player.getClassId(), false, false));
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ACQUIRE_SKILL_LIST);

        writeShort((short) _learnable.size());
        for (SkillLearn skill : _learnable) {
            if (skill == null) {
                continue;
            }
            writeInt(skill.getSkillId());
            writeShort((short) skill.getSkillLevel());
            writeLong(skill.getLevelUpSp());
            writeByte((byte) skill.getGetLevel());
            writeShort((short) 0x00); // Salvation: Changed from byte to short.
            if (skill.getRequiredItems().size() > 0) {
                for (ItemHolder item : skill.getRequiredItems()) {
                    writeByte((byte) 0x01);
                    writeInt(item.getId());
                    writeLong(item.getCount());
                }
            } else {
                writeByte((byte) 0x00);
            }
            writeByte((byte) 0x00);
        }
    }

}
