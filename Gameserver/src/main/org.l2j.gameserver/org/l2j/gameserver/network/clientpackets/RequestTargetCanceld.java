package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

public final class RequestTargetCanceld extends ClientPacket {
    private int _unselect;

    @Override
    public void readImpl() {
        _unselect = readShort();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if (player.isLockedTarget()) {
            player.sendPacket(SystemMessageId.FAILED_TO_REMOVE_ENMITY);
            return;
        }

        if (_unselect == 0) {
            if(player.isCastingNow()) {
                player.abortAllSkillCasters();
            }
        } else {
            player.setTarget(null);
        }
    }
}
