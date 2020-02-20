package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.TeleportListData;
import org.l2j.gameserver.data.xml.model.TeleportData;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.request.TeleportRequest;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author JoeAlisson
 */
public class ExRequestTeleport extends ClientPacket {
    private int id;

    @Override
    protected void readImpl() throws Exception {
        id = readInt();
    }

    @Override
    protected void runImpl()  {
        TeleportListData.getInstance().getInfo(id).ifPresent(this::teleport);
    }

    private void teleport(TeleportData info) {
        var player = client.getPlayer();

        if(info.getCastleId() != -1 && CastleManager.getInstance().getCastleById(info.getCastleId()).isInSiege()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
            return;
        }

        if(GameUtils.canTeleport(player) && (player.getLevel() <= 40 || player.reduceAdena("Teleport", info.getPrice(), null, true))) {
            player.addRequest(new TeleportRequest(player, id));
            player.useMagic(CommonSkill.TELEPORT.getSkill(), null, false, true);
        }
    }
}