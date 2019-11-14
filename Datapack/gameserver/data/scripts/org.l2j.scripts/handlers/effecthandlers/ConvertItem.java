package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Convert Item effect implementation.
 * @author Zoey76
 */
public final class ConvertItem extends AbstractEffect {

	public ConvertItem(StatsSet params) {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {

		if (effected.isAlikeDead() || !isPlayer(effected)) {
			return;
		}
		
		final Player player = effected.getActingPlayer();

		if (player.hasItemRequest()) {
			return;
		}
		
		final Weapon weaponItem = player.getActiveWeaponItem();
		if (isNull(weaponItem)) {
			return;
		}
		
		Item wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if (isNull(wpn)) {
			wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		}
		
		if (!GameUtils.isWeapon(wpn) || wpn.isAugmented() || weaponItem.getChangeWeaponId() == 0) {
			return;
		}
		
		final int newItemId = weaponItem.getChangeWeaponId();
		if (newItemId == -1) {
			return;
		}
		
		final int enchantLevel = wpn.getEnchantLevel();
		final AttributeHolder elementals = wpn.getAttributes() == null ? null : wpn.getAttackAttribute();
		final Item[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getTemplate().getBodyPart().getId());
		final InventoryUpdate iu = new InventoryUpdate();
		for (Item unequippedItem : unequiped)
		{
			iu.addModifiedItem(unequippedItem);
		}
		player.sendInventoryUpdate(iu);
		
		if (unequiped.length <= 0)
		{
			return;
		}
		byte count = 0;
		for (Item unequippedItem : unequiped)
		{
			if (!(unequippedItem.getTemplate() instanceof Weapon))
			{
				count++;
				continue;
			}
			
			final SystemMessage sm;
			if (unequippedItem.getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
				sm.addInt(unequippedItem.getEnchantLevel());
				sm.addItemName(unequippedItem);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(unequippedItem);
			}
			player.sendPacket(sm);
		}
		
		if (count == unequiped.length)
		{
			return;
		}
		
		final Item destroyItem = player.getInventory().destroyItem("ChangeWeapon", wpn, player, null);
		if (destroyItem == null)
		{
			return;
		}
		
		final Item newItem = player.getInventory().addItem("ChangeWeapon", newItemId, 1, player, destroyItem);
		if (newItem == null)
		{
			return;
		}
		
		if (elementals != null)
		{
			newItem.setAttribute(elementals, true);
		}
		newItem.setEnchantLevel(enchantLevel);
		player.getInventory().equipItem(newItem);
		
		final SystemMessage msg;
		if (newItem.getEnchantLevel() > 0)
		{
			msg = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2);
			msg.addInt(newItem.getEnchantLevel());
			msg.addItemName(newItem);
		}
		else
		{
			msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
			msg.addItemName(newItem);
		}
		player.sendPacket(msg);
		
		final InventoryUpdate u = new InventoryUpdate();
		u.addRemovedItem(destroyItem);
		u.addItem(newItem);
		player.sendInventoryUpdate(u);
		
		player.broadcastUserInfo();
	}
}
