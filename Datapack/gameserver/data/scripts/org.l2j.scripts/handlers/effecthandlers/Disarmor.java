package handlers.effecthandlers;

import io.github.joealisson.primitive.CHashIntIntMap;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Disarm by inventory slot effect implementation. At end of effect, it re-equips that item.
 * @author Nik
 * @author JoeAlisson
 */
public final class Disarmor extends AbstractEffect {
	private final IntIntMap unequippedItems = new CHashIntIntMap(); // PlayerObjId, ItemObjId
	private final BodyPart bodyPart;
	
	public Disarmor(StatsSet params) {
		bodyPart = params.getEnum("slot", BodyPart.class, BodyPart.CHEST);
		if (bodyPart == BodyPart.NONE) {
			LOGGER.error("Unknown bodypart slot for effect: {}", bodyPart);
		}
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill) {
		return (bodyPart != BodyPart.NONE) && isPlayer(effected);
	}
	
	@Override
	public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isPlayer(effected)) {
			return;
		}
		
		final Player player = effected.getActingPlayer();
		final Item[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(bodyPart.getId());

		if (unequiped.length > 0) {
			final InventoryUpdate iu = new InventoryUpdate();
			for (Item itm : unequiped) {
				iu.addModifiedItem(itm);
			}
			player.sendInventoryUpdate(iu);
			player.broadcastUserInfo();
			
			SystemMessage sm;
			if (unequiped[0].getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
				sm.addInt(unequiped[0].getEnchantLevel());
				sm.addItemName(unequiped[0]);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(unequiped[0]);
			}
			player.sendPacket(sm);
			effected.getInventory().blockItemSlot(bodyPart.getId());
			unequippedItems.put(effected.getObjectId(), unequiped[0].getObjectId());
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (!isPlayer(effected))
		{
			return;
		}
		
		int disarmedObjId = unequippedItems.remove(effected.getObjectId());
		if (disarmedObjId > 0)
		{
			final Player player = effected.getActingPlayer();
			player.getInventory().unblockItemSlot(bodyPart.getId());
			
			final Item item = player.getInventory().getItemByObjectId(disarmedObjId);
			if (item != null)
			{
				player.getInventory().equipItem(item);
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(item);
				player.sendInventoryUpdate(iu);
				
				SystemMessage sm;
				if (item.isEquipped())
				{
					if (item.getEnchantLevel() > 0)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2);
						sm.addInt(item.getEnchantLevel());
						sm.addItemName(item);
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
						sm.addItemName(item);
					}
					player.sendPacket(sm);
				}
			}
		}
	}
}
