package handlers.playeractions;

import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.model.actor.instance.Player;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class ActionHandler implements IPlayerActionHandler {

    @Override
    public void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed) {
        doIfNonNull(player.getTarget(),  target -> target.onAction(player));
    }
}
