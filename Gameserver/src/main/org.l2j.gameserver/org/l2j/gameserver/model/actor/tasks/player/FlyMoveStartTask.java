package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.SayuneRequest;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.network.serverpackets.sayune.ExNotifyFlyMoveStart;

import java.util.Objects;

/**
 * @author UnAfraid
 */
public class FlyMoveStartTask implements Runnable {
    private final L2PcInstance _player;
    private final L2ZoneType _zone;

    public FlyMoveStartTask(L2ZoneType zone, L2PcInstance player) {
        Objects.requireNonNull(zone);
        Objects.requireNonNull(player);
        _player = player;
        _zone = zone;
    }

    @Override
    public void run() {
        if (!_zone.isCharacterInZone(_player)) {
            return;
        }

        if (!_player.hasRequest(SayuneRequest.class)) {
            _player.sendPacket(ExNotifyFlyMoveStart.STATIC_PACKET);
            ThreadPoolManager.getInstance().schedule(this, 1000);
        }
    }
}