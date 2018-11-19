package l2s.gameserver.model.items;

import java.util.Collection;
import java.util.Collections;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance.ItemLocation;
import l2s.gameserver.model.items.listeners.*;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExAdenaInvenCount;
import l2s.gameserver.network.l2.s2c.ExUserInfoEquipSlot;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.taskmanager.DelayedItemsManager;
import l2s.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.ItemFunctions;

import org.apache.commons.lang3.ArrayUtils;

public class PcInventory extends Inventory
{
	private final Player _owner;

	// locks
	private LockType _lockType = LockType.NONE;
	private int[] _lockItems = ArrayUtils.EMPTY_INT_ARRAY;

	public PcInventory(Player owner)
	{
		super(owner.getObjectId());
		_owner = owner;

		addListener(ItemSkillsListener.getInstance());
		addListener(ItemAugmentationListener.getInstance());
		addListener(ItemEnchantOptionsListener.getInstance());
		addListener(ArmorSetListener.getInstance());
		addListener(BowListener.getInstance());
		addListener(AccessoryListener.getInstance());
		addListener(RodListener.getInstance());
	}

	@Override
	public Player getActor()
	{
		return _owner;
	}

	@Override
	protected ItemLocation getBaseLocation()
	{
		return ItemLocation.INVENTORY;
	}

	@Override
	protected ItemLocation getEquipLocation()
	{
		return ItemLocation.PAPERDOLL;
	}

	/**
	 * Добавляет адену игроку.<BR><BR>
	 * @param amount - сколько адены дать
	 * @return L2ItemInstance - новое количество адены
	 */
	public ItemInstance addAdena(long amount)
	{
		return addItem(ItemTemplate.ITEM_ID_ADENA, amount);
	}

	public boolean reduceAdena(long adena)
	{
		return destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, adena);
	}

	public int getPaperdollVariation1Id(int slot)
	{
		ItemInstance item = _paperdoll[slot];
		if(item != null && item.isAugmented())
			return item.getVariation1Id();
		return 0;
	}

	public int getPaperdollVariation2Id(int slot)
	{
		ItemInstance item = _paperdoll[slot];
		if(item != null && item.isAugmented())
			return item.getVariation2Id();
		return 0;
	}

	@Override
	public int getPaperdollVisualId(int slot)
	{
		Player player = getActor();

		int itemId = super.getPaperdollVisualId(slot);

		if(player.isInTrainingCamp())
		{
			if(slot == PAPERDOLL_RHAND || slot == PAPERDOLL_LRHAND)
				itemId = 135;
		}
		return itemId;
	}

	@Override
	protected void onRefreshWeight()
	{
		// notify char for overload checking
		getActor().refreshOverloaded();
	}

	/**
	 * Функция для валидации вещей в инвентаре.
	 * Снимает все вещи, которые нельзя носить.
	 * Применяется при входе в игру, смене саба, захвате замка, выходе из клана.
	 */
	public void validateItems()
	{
		for(ItemInstance item : _paperdoll)
			if(item != null && (ItemFunctions.checkIfCanEquip(getActor(), item) != null || !item.getTemplate().testCondition(getActor(), item, false)))
			{
				unEquipItem(item);
				getActor().sendDisarmMessage(item);
			}
	}

	/**
	 * FIXME[VISTALL] для скилов критично их всегда удалять и добавлять, для тригеров нет
	 */
	public void validateItemsSkills()
	{
		for(ItemInstance item : _paperdoll)
		{
			if(item == null || item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON)
				continue;

			boolean needUnequipSkills = getActor().getWeaponsExpertisePenalty() > 0;

			if(item.getTemplate().getAttachedSkills().length > 0)
			{
				boolean has = getActor().getSkillLevel(item.getTemplate().getAttachedSkills()[0].getId()) > 0;
				if(needUnequipSkills && has)
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				else if(!needUnequipSkills && !has)
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
			}
			else if(item.getTemplate().getEnchant4Skill() != null)
			{
				boolean has = getActor().getSkillLevel(item.getTemplate().getEnchant4Skill().getId()) > 0;
				if(needUnequipSkills && has)
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				else if(!needUnequipSkills && !has)
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
			}
			else if(!item.getTemplate().getTriggerList().isEmpty())
			{
				if(needUnequipSkills)
					ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
				else
					ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
			}
		}
	}

	/**
	 * FIXME Хак для обновления скиллов от эквипа при смене сабкласса
	 */
	public boolean isRefresh = false;

	public void refreshEquip()
	{
		isRefresh = true;
		for(ItemInstance item : getItems())
		{
			if(item.isEquipped())
			{
				int slot = item.getEquipSlot();
				_listeners.onUnequip(slot, item);
				_listeners.onEquip(slot, item);
			}
			else if(item.getTemplate().isRune())
			{
				_listeners.onUnequip(-1, item);
				_listeners.onEquip(-1, item);
			}
		}
		isRefresh = false;
	}

	/**
	 * Вызывается из RequestSaveInventoryOrder
	 */
	public void sort(int[][] order)
	{
		boolean needSort = false;
		for(int[] element : order)
		{
			ItemInstance item = getItemByObjectId(element[0]);
			if(item == null)
				continue;
			if(item.getLocation() != ItemLocation.INVENTORY)
				continue;
			if(item.getLocData() == element[1])
				continue;
			item.setLocData(element[1]);
			item.setJdbcState(JdbcEntityState.UPDATED); // lazy update
			needSort = true;
		}
		if(needSort)
			Collections.sort(_items, ItemOrderComparator.getInstance());
	}

	public ItemInstance findArrowForBow(ItemTemplate bow)
	{
		ItemInstance res = null;
		for(ItemInstance temp : getItems())
		{
			if(temp.getItemType() == EtcItemType.ARROW || temp.getItemType() == EtcItemType.ARROW_QUIVER)
			{
				if(bow.getGrade().extOrdinal() == temp.getGrade().extOrdinal())
				{
					if(temp.getLocation() == ItemLocation.PAPERDOLL && temp.getEquipSlot() == PAPERDOLL_LHAND)
						return temp;
					else if(res == null || temp.getItemId() < res.getItemId())
						res = temp;
				}
			}
		}
		return res;
	}

	public ItemInstance findArrowForCrossbow(ItemTemplate crossbow)
	{
		ItemInstance res = null;
		for(ItemInstance temp : getItems())
		{
			if(temp.getItemType() == EtcItemType.BOLT || temp.getItemType() == EtcItemType.BOLT_QUIVER)
			{
				if(crossbow.getGrade().extOrdinal() == temp.getGrade().extOrdinal())
				{
					if(temp.getLocation() == ItemLocation.PAPERDOLL && temp.getEquipSlot() == PAPERDOLL_LHAND)
						return temp;
					else if(res == null || temp.getItemId() < res.getItemId())
						res = temp;
				}
			}
		}
		return res;
	}

	public void lockItems(LockType lock, int[] items)
	{
		if(_lockType != LockType.NONE)
			return;

		_lockType = lock;
		_lockItems = items;

		getActor().sendItemList(false);
	}

	public void unlock()
	{
		if(_lockType == LockType.NONE)
			return;

		_lockType = LockType.NONE;
		_lockItems = ArrayUtils.EMPTY_INT_ARRAY;

		getActor().sendItemList(false);
	}

	public boolean isLockedItem(ItemInstance item)
	{
		switch(_lockType)
		{
			case INCLUDE:
				return ArrayUtils.contains(_lockItems, item.getItemId());
			case EXCLUDE:
				return !ArrayUtils.contains(_lockItems, item.getItemId());
			default:
				return false;
		}
	}

	public LockType getLockType()
	{
		return _lockType;
	}

	public int[] getLockItems()
	{
		return _lockItems;
	}

	@Override
	protected void onRestoreItem(ItemInstance item)
	{
		super.onRestoreItem(item);

		if(item.getTemplate().isRune())
			_listeners.onEquip(-1, item);

		if(item.isTemporalItem() || item.isFlagLifeTime())
			item.startTimer(new LifeTimeTask(item));

		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onAddItem(ItemInstance item)
	{
		super.onAddItem(item);

		if(item.getTemplate().isRune())
			_listeners.onEquip(-1, item);

		if(item.getTemplate().isArrow() || item.getTemplate().isBolt() || item.getTemplate().isQuiver())
			getActor().checkAndEquipArrows();

		if(item.isTemporalItem() || item.isFlagLifeTime())
			item.startTimer(new LifeTimeTask(item));

		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onModifyItem(ItemInstance item)
	{
		super.onModifyItem(item);

		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onRemoveItem(ItemInstance item)
	{
		super.onRemoveItem(item);

		Player owner = getActor();
		owner.removeItemFromShortCut(item.getObjectId());

		if(item.getTemplate().isRune())
			_listeners.onUnequip(-1, item);

		if(item.isTemporalItem() || item.isFlagLifeTime())
			item.stopTimer();

		if(owner.getMountControlItemObjId() == item.getObjectId())
			owner.setMount(null);

		if(owner.getPetControlItem() == item)
		{
			PetInstance pet = owner.getPet();
			if(pet != null)
				pet.unSummon(false);
		}

		for(QuestState state : _owner.getAllQuestsStates())
			state.getQuest().notifyUpdateItem(item, state);
	}

	@Override
	protected void onEquip(int slot, ItemInstance item)
	{
		super.onEquip(slot, item);

		if(item.isShadowItem())
			item.startTimer(new ShadowLifeTimeTask(item));
	}

	@Override
	protected void onReequip(int slot, ItemInstance newItem, ItemInstance oldItem)
	{
		super.onReequip(slot, newItem, oldItem);

		if(oldItem.isShadowItem())
			oldItem.stopTimer();

		if(newItem.isShadowItem())
			newItem.startTimer(new ShadowLifeTimeTask(newItem));
	}

	@Override
	protected void onUnequip(int slot, ItemInstance item)
	{
		super.onUnequip(slot, item);

		if(item.isShadowItem())
			item.stopTimer();
	}

	@Override
	public void restore()
	{
		final int ownerId = getOwnerId();

		writeLock();
		try
		{
			Collection<ItemInstance> items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getBaseLocation());

			for(ItemInstance item : items)
			{
				_items.add(item);
				onRestoreItem(item);
			}
			Collections.sort(_items, ItemOrderComparator.getInstance());

			items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getEquipLocation());

			for(ItemInstance item : items)
			{
				_items.add(item);
				onRestoreItem(item);
				if(item.getEquipSlot() >= PAPERDOLL_MAX)
				{
					// Неверный слот - возвращаем предмет в инвентарь.
					item.setLocation(getBaseLocation());
					item.setLocData(0); // Немного некрасиво, но инвентарь еще весь не загружен и свободный слот не найти
					item.setEquipped(false);
					continue;
				}
				setPaperdollItem(item.getEquipSlot(), item);
			}
		}
		finally
		{
			writeUnlock();
		}

		DelayedItemsManager.getInstance().loadDelayed(getActor(), false);

		refreshWeight();
	}

	@Override
	public void store()
	{
		writeLock();
		try
		{
			_itemsDAO.update(_items);
		}
		finally
		{
			writeUnlock();
		}
	}

	@Override
	public void sendAddItem(ItemInstance item)
	{
		Player actor = getActor();

		actor.sendPacket(new InventoryUpdatePacket().addNewItem(actor, item));
		if(item.getItemId() == ItemTemplate.ITEM_ID_ADENA)
			actor.sendPacket(new ExAdenaInvenCount(actor));
	}

	@Override
	public void sendModifyItem(ItemInstance... items)
	{
		Player actor = getActor();

		InventoryUpdatePacket iu = new InventoryUpdatePacket();
		for(ItemInstance item : items)
			iu.addModifiedItem(actor, item);

		actor.sendPacket(iu);

		for(ItemInstance item : items)
		{
			if(item.getItemId() == ItemTemplate.ITEM_ID_ADENA)
				actor.sendPacket(new ExAdenaInvenCount(actor));
		}
	}

	@Override
	public void sendRemoveItem(ItemInstance item)
	{
		Player actor = getActor();
		actor.sendPacket(new InventoryUpdatePacket().addRemovedItem(actor, item));
		if(item.getItemId() == ItemTemplate.ITEM_ID_ADENA)
			actor.sendPacket(new ExAdenaInvenCount(actor));
	}

	@Override
	public void sendEquipInfo(int slot)
	{
		getActor().broadcastUserInfo(true);
		getActor().sendPacket(new ExUserInfoEquipSlot(getActor(), slot));
	}

	public void startTimers()
	{

	}

	public void stopAllTimers()
	{
		for(ItemInstance item : getItems())
		{
			if(item.isShadowItem() || item.isTemporalItem() || item.isFlagLifeTime())
				item.stopTimer();
		}
	}

	protected class ShadowLifeTimeTask extends RunnableImpl
	{
		private ItemInstance item;

		ShadowLifeTimeTask(ItemInstance item)
		{
			this.item = item;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player player = getActor();

			if(!item.isEquipped())
				return;

			int mana;
			synchronized (item)
			{
				item.setLifeTime(item.getLifeTime() - 1);
				mana = item.getShadowLifeTime();
				if(mana <= 0)
					destroyItem(item);
			}

			SystemMessage sm = null;
			if(mana == 10)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_10);
			else if(mana == 5)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_5);
			else if(mana == 1)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON);
			else if(mana <= 0)
				sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED);
			else
				player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, item));

			if(sm != null)
			{
				sm.addItemName(item.getItemId());
				player.sendPacket(sm);
			}
		}
	}

	protected class LifeTimeTask extends RunnableImpl
	{
		private ItemInstance item;

		LifeTimeTask(ItemInstance item)
		{
			this.item = item;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player player = getActor();

			int left;
			synchronized (item)
			{
				left = item.getTemporalLifeTime();
				if(left <= 0)
					destroyItem(item);
			}

			if(left <= 0)
				player.sendPacket(new SystemMessage(SystemMessage.THE_LIMITED_TIME_ITEM_HAS_BEEN_DELETED).addItemName(item.getItemId()));
		}
	}
}