package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeLock;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.SystemMessageId.CANNOT_EDIT_THE_LOCK_TRANSFORMATION_SETTING_DURING_A_BATTLE;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeLock extends ClientPacket {

    private int id;
    private boolean lock;

    @Override
    protected void readImpl() throws Exception {
        id = readInt();
        lock = readBoolean();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        if(AttackStanceTaskManager.getInstance().hasAttackStanceTask(player)) {
            client.sendPacket(CANNOT_EDIT_THE_LOCK_TRANSFORMATION_SETTING_DURING_A_BATTLE);
            return;
        }

        doIfNonNull(player.getCostume(id), costume -> {
            costume.setLocked(lock);
            client.sendPacket(new ExCostumeLock(id, lock, true));
        });
    }
}
