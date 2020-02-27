package handlers.playeractions;

import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.model.actor.instance.Player;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class ActionHandler implements IPlayerActionHandler {

    @Override
    public void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed) {
        var target = player.getTarget();
        if(nonNull(target) && target != player) {
            if(ctrlPressed) {
                target.onForcedAttack(player);
            } else if(shiftPressed) {
                target.onActionShift(player);
            } else {
                target.onAction(player);
            }
        }
    }
}
