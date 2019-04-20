package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.data.xml.impl.VipData;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.taskmanager.Task;
import org.l2j.gameserver.taskmanager.TaskManager;

public class TaskVipExpiration extends Task {

    @Override
    public String getName() {
        return "vipExpiration";
    }

    @Override
    public void onTimeElapsed(TaskManager.ExecutableTask task) {
        L2World.getInstance().getPlayers().forEach(player -> {
            if(player.getVipTier() < 1) {
                return;
            }

            VipData.getInstance().checkVipTierExpiration(player);
        });
    }
}
