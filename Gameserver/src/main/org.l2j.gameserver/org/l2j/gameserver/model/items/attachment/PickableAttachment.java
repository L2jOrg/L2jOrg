package org.l2j.gameserver.model.items.attachment;

import org.l2j.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 0:50/04.06.2011
 */
public interface PickableAttachment extends ItemAttachment
{
	boolean canPickUp(Player player);

	void pickUp(Player player);
}