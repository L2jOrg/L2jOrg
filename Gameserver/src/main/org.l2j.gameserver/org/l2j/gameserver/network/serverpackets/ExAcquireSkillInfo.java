package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class ExAcquireSkillInfo extends IClientOutgoingPacket {
    private final int _id;
    private final int _level;
    private final int _dualClassLevel;
    private final int _spCost;
    private final int _minLevel;
    private final List<ItemHolder> _itemReq;
    private final List<Skill> _skillRem;

    /**
     * Special constructor for Alternate Skill Learning system.<br>
     * Sets a custom amount of SP.
     *
     * @param player
     * @param skillLearn the skill learn.
     */
    public ExAcquireSkillInfo(L2PcInstance player, L2SkillLearn skillLearn) {
        _id = skillLearn.getSkillId();
        _level = skillLearn.getSkillLevel();
        _dualClassLevel = skillLearn.getDualClassLevel();
        _spCost = skillLearn.getLevelUpSp();
        _minLevel = skillLearn.getGetLevel();
        _itemReq = skillLearn.getRequiredItems();
        _skillRem = skillLearn.getRemoveSkills().stream().map(player::getKnownSkill).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ACQUIRE_SKILL_INFO.writeId(packet);

        packet.putInt(_id);
        packet.putInt(_level);
        packet.putLong(_spCost);
        packet.putShort((short) _minLevel);
        packet.putShort((short) _dualClassLevel);
        packet.putInt(_itemReq.size());
        for (ItemHolder holder : _itemReq) {
            packet.putInt(holder.getId());
            packet.putLong(holder.getCount());
        }

        packet.putInt(_skillRem.size());
        for (Skill skill : _skillRem) {
            packet.putInt(skill.getId());
            packet.putInt(skill.getLevel());
        }
    }
}
