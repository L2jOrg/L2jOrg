package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
public class TeleportRequest extends AbstractRequest{

    private final int teleportId;

    public TeleportRequest(Player player, int teleportId) {
        super(player);
        this.teleportId = teleportId;
    }

    @Override
    public boolean isUsing(int objectId) {
        return false;
    }

    public int getTeleportId() {
        return teleportId;
    }
}
