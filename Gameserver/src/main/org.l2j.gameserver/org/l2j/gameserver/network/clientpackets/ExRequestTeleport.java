package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.TeleportListData;
import org.l2j.gameserver.data.xml.model.TeleportData;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.network.SystemMessageId;

public class ExRequestTeleport extends ClientPacket {
    private int id;

    @Override
    protected void readImpl() throws Exception {
        id = readInt();
    }

    @Override
    protected void runImpl()  {
        var optionalInfo = TeleportListData.getInstance().getInfo(id);
        if (optionalInfo.isPresent()) {
            TeleportData info = optionalInfo.get();

            var player = getClient().getPlayer();
            if ((player.getLevel() >= 40) && !player.destroyItemByItemId("Teleport", CommonItem.ADENA, info.getPrice(), null, true)) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else if (!player.isAlikeDead()) {
                player.teleToLocation(info.getLocation());
            }
        }
    }
}