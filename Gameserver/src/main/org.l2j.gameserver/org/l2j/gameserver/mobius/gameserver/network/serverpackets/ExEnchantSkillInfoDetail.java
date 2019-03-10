package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnchantSkillGroupsData;
import org.l2j.gameserver.mobius.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.EnchantSkillHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author KenM
 */
public class ExEnchantSkillInfoDetail extends IClientOutgoingPacket {
    private final SkillEnchantType _type;
    private final int _skillId;
    private final int _skillLvl;
    private final int _skillSubLvl;
    private final EnchantSkillHolder _enchantSkillHolder;

    public ExEnchantSkillInfoDetail(SkillEnchantType type, int skillId, int skillLvl, int skillSubLvl, L2PcInstance player) {
        _type = type;
        _skillId = skillId;
        _skillLvl = skillLvl;
        _skillSubLvl = skillSubLvl;

        _enchantSkillHolder = EnchantSkillGroupsData.getInstance().getEnchantSkillHolder(skillSubLvl % 1000);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_SKILL_INFO_DETAIL.writeId(packet);

        packet.putInt(_type.ordinal());
        packet.putInt(_skillId);
        packet.putShort((short) _skillLvl);
        packet.putShort((short) _skillSubLvl);
        if (_enchantSkillHolder != null) {
            packet.putLong(_enchantSkillHolder.getSp(_type));
            packet.putInt(_enchantSkillHolder.getChance(_type));
            final Set<ItemHolder> holders = _enchantSkillHolder.getRequiredItems(_type);
            packet.putInt(holders.size());
            holders.forEach(holder ->
            {
                packet.putInt(holder.getId());
                packet.putInt((int) holder.getCount());
            });
        }
    }
}
