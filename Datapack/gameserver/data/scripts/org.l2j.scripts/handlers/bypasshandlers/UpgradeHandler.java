package handlers.bypasshandlers;

import org.l2j.gameserver.api.item.UpgradeAPI;
import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.MathUtil;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.SPACE;

/**
 * @author JoeAlisson
 */
public class UpgradeHandler implements IBypassHandler {

    @Override
    public boolean useBypass(String command, Player player, Creature npc) {
        if(isNull(npc) || !MathUtil.isInsideRadius3D(player, npc, Npc.INTERACTION_DISTANCE) || !command.contains(SPACE)) {
            return false;
        }

        final var typeName = command.split(SPACE)[1];
        return UpgradeAPI.showUpgradeUI(player, UpgradeType.valueOf(typeName));
    }

    @Override
    public String[] getBypassList() {
        return new String[] { "upgrade_item" };
    }
}
