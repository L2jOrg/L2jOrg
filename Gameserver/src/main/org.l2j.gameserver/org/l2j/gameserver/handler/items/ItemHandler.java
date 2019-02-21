package org.l2j.gameserver.handler.items;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.handler.items.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bonux
 */
public class ItemHandler extends AbstractHolder {

	private static final Logger _log = LoggerFactory.getLogger(ItemHandler.class);

	public static final IItemHandler DEFAULT_HANDLER = new DefaultItemHandler();
	public static final IItemHandler EQUIPABLE_HANDLER = new EquipableItemHandler();
	public static final IItemHandler ENCHANT_SCROLL_HANDLER = new EnchantScrollItemHandler();
	public static final IItemHandler FISHSHOT_HANDLER = new FishShotItemHandler();
	public static final IItemHandler SOULSHOT_HANDLER = new SoulShotItemHandler();
	public static final IItemHandler SPIRITSHOT_HANDLER = new SpiritShotItemHandler();
	public static final IItemHandler BLESSED_SPIRITSHOT_HANDLER = new BlessedSpiritShotItemHandler();
	public static final IItemHandler BEAST_SOULSHOT_HANDLER = new BeastSoulShotItemHandler();
	public static final IItemHandler BEAST_SPIRITSHOT_HANDLER = new BeastSpiritShotItemHandler();
	public static final IItemHandler BEAST_BLESSED_SPIRITSHOT_HANDLER = new BeastBlessedSpiritShotItemHandler();
	public static final IItemHandler SKILL_ITEM_HANDLER = new SkillsItemHandler();
	public static final IItemHandler SKILL_REDUCE_ITEM_HANDLER = new SkillsReduceItemHandler();
	public static final IItemHandler CAPSULED_ITEM_HANDLER = new CapsuledItemHandler();
	public static final IItemHandler PET_SUMMON_HANDLER = new PetSummonItemHandler();
	public static final IItemHandler RECIPE_HANDLER = new RecipeItemHandler();
	public static final IItemHandler MERCENARY_TICKET_HANDLER = new MercenaryTicketItemHandler();

	private static final ItemHandler _instance = new ItemHandler();

	private final Map<String, IItemHandler> _handlers = new HashMap<>();

	public static ItemHandler getInstance() {
		return _instance;
	}

	private ItemHandler() {
		registerItemHandler(DEFAULT_HANDLER);
		registerItemHandler(ENCHANT_SCROLL_HANDLER);
		registerItemHandler(EQUIPABLE_HANDLER);
		registerItemHandler(FISHSHOT_HANDLER);
		registerItemHandler(SOULSHOT_HANDLER);
		registerItemHandler(SPIRITSHOT_HANDLER);
		registerItemHandler(BLESSED_SPIRITSHOT_HANDLER);
		registerItemHandler(BEAST_SOULSHOT_HANDLER);
		registerItemHandler(BEAST_SPIRITSHOT_HANDLER);
		registerItemHandler(BEAST_BLESSED_SPIRITSHOT_HANDLER);
		registerItemHandler(SKILL_ITEM_HANDLER);
		registerItemHandler(SKILL_REDUCE_ITEM_HANDLER);
		registerItemHandler(CAPSULED_ITEM_HANDLER);
		registerItemHandler(PET_SUMMON_HANDLER);
		registerItemHandler(RECIPE_HANDLER);
		registerItemHandler(MERCENARY_TICKET_HANDLER);

		registerItemHandler(new CalculatorItemHandler());
		registerItemHandler(new HarvesterItemHandler());
		registerItemHandler(new KeyItemHandler());
		registerItemHandler(new NameColorItemHandler());
		registerItemHandler(new RollingDiceItemHandler());
		registerItemHandler(new WorldMapItemHandler());
	}

	public void registerItemHandler(IItemHandler handler) {
		_handlers.put(handler.getClass().getSimpleName().replace("ItemHandler", ""), handler);
	}

	public IItemHandler getItemHandler(String handler) {
		if(handler.contains("ItemHandler"))
			handler = handler.replace("ItemHandler", "");

		if(_handlers.isEmpty() || !_handlers.containsKey(handler)) {
			_log.warn("ItemHandler: Cannot find handler [" + handler + "]!");
			return DEFAULT_HANDLER;
		}

		return _handlers.get(handler);
	}

	@Override
	public int size()
	{
		return _handlers.size();
	}

	@Override
	public void clear()
	{
		_handlers.clear();
	}
}
