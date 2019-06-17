package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Henna;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Zoey76
 */
public class HennaEquipList extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.HENNA_EQUIP_LIST);
        writeLong(_player.getAdena()); // activeChar current amount of Adena
        writeInt(3); // available equip slot
        writeInt(_hennaEquipList.size());

        for (L2Henna henna : _hennaEquipList) {
            // Player must have at least one dye in inventory
            // to be able to see the Henna that can be applied with it.
            if ((_player.getInventory().getItemByItemId(henna.getDyeItemId())) != null) {
                writeInt(henna.getDyeId()); // dye Id
                writeInt(henna.getDyeItemId()); // item Id of the dye
                writeLong(henna.getWearCount()); // amount of dyes required
                writeLong(henna.getWearFee()); // amount of Adena required
                writeInt(henna.isAllowedClass(_player.getClassId()) ? 0x01 : 0x00); // meet the requirement or not
                // writeInt(0x00); // Does not exist in Classic.
            }
        }
    }

}
