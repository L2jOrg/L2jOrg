package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Henna;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Zoey76
 */
public class HennaEquipList extends IClientOutgoingPacket {
    private final L2PcInstance _player;
    private final List<L2Henna> _hennaEquipList;

    public HennaEquipList(L2PcInstance player) {
        _player = player;
        _hennaEquipList = HennaData.getInstance().getHennaList(player.getClassId());
    }

    public HennaEquipList(L2PcInstance player, List<L2Henna> list) {
        _player = player;
        _hennaEquipList = list;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.HENNA_EQUIP_LIST.writeId(packet);
        packet.putLong(_player.getAdena()); // activeChar current amount of Adena
        packet.putInt(3); // available equip slot
        packet.putInt(_hennaEquipList.size());

        for (L2Henna henna : _hennaEquipList) {
            // Player must have at least one dye in inventory
            // to be able to see the Henna that can be applied with it.
            if ((_player.getInventory().getItemByItemId(henna.getDyeItemId())) != null) {
                packet.putInt(henna.getDyeId()); // dye Id
                packet.putInt(henna.getDyeItemId()); // item Id of the dye
                packet.putLong(henna.getWearCount()); // amount of dyes required
                packet.putLong(henna.getWearFee()); // amount of Adena required
                packet.putInt(henna.isAllowedClass(_player.getClassId()) ? 0x01 : 0x00); // meet the requirement or not
                // packet.putInt(0x00); // Does not exist in Classic.
            }
        }
    }
}
