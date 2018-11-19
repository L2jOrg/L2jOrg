package l2s.gameserver.handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Location;

/**
 * Mother class of all itemHandlers.<BR><BR>
 * an IItemHandler implementation has to be stateless
 */
public interface IItemHandler
{
	/**
	 * Launch task associated to the item.
	 * @param playable
	 * @param item : L2ItemInstance designating the item to use
	 * @param ctrl
	 */
	public boolean forceUseItem(Playable playable, ItemInstance item, boolean ctrl);

	/**
	 * Launch task associated to the item.
	 * @param playable
	 * @param item : L2ItemInstance designating the item to use
	 * @param ctrl
	 */
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl);

	/**
	 * Check can drop or not
	 *
	 *
	 * @param player
	 * @param item
	 * @param count
	 *@param loc  @return can drop
	 */
	public void dropItem(Player player, ItemInstance item, long count, Location loc);

	/**
	 * Check if can pick up item
	 * @param playable
	 * @param item
	 * @return
	 */
	public boolean pickupItem(Playable playable, ItemInstance item);

	/**
	 * Item actions after restore item
	 * @param playable
	 * @param item
	 * @return
	 */
	public void onRestoreItem(Playable playable, ItemInstance item);

	/**
	 * Item actions after add item
	 * @param playable
	 * @param item
	 * @return
	 */
	public void onAddItem(Playable playable, ItemInstance item);

	/**
	 * Item actions after remove item
	 * @param playable
	 * @param item
	 * @return
	 */
	public void onRemoveItem(Playable playable, ItemInstance item);

	public boolean isAutoUse();

	public SystemMsg checkCondition(Playable playable, ItemInstance item);
}