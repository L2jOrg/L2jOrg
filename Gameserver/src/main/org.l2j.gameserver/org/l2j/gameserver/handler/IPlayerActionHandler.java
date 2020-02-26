package org.l2j.gameserver.handler;

import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author UnAfraid
 */
public interface IPlayerActionHandler {
    void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed);
}