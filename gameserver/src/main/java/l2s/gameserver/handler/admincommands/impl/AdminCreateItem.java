package l2s.gameserver.handler.admincommands.impl;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.dao.HidenItemsDAO;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;

public class AdminCreateItem implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_itemcreate,
		admin_create_item,
		admin_hidden_item,
		admin_ci,
		admin_spreaditem,
		admin_create_item_element
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().UseGMShop)
			return false;

		switch(command)
		{
			case admin_itemcreate:
				activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
				break;
			case admin_ci:
			case admin_create_item:
				try
				{
					if(wordList.length < 2)
					{
						activeChar.sendMessage("USAGE: create_item id [count]");
						return false;
					}

					int item_id = Integer.parseInt(wordList[1]);
					long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
					ItemInstance item = null;
					if(activeChar.getTarget() == null || activeChar.getTarget() == activeChar)
						item = createItem(activeChar, item_id, item_count);
					else if(activeChar.getTarget() instanceof Player)
						item = createItem(activeChar.getTarget().getPlayer(), item_id, item_count);
					else
						item = createItem(activeChar, item_id, item_count);

					if(item == null)
						activeChar.sendMessage("Undefined item id!");
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("USAGE: create_item id [count]");
				}
				activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
				break;
			case admin_hidden_item:
				try
				{
					if(wordList.length < 2)
					{
						activeChar.sendMessage(new CustomMessage("common.Admin.Createitem.CreateItemUssage"));
						return false;
					}
					int item_id = Integer.parseInt(wordList[1]);
					long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
					
					if(activeChar.getTarget() == null || activeChar.getTarget() == activeChar)
						createItemH(activeChar, item_id, item_count);
						
					else if(activeChar.getTarget().isPlayer())
						createItemH(activeChar.getTarget().getPlayer(), item_id, item_count);
						
					else
						createItemH(activeChar, item_id, item_count);
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage(new CustomMessage("common.Admin.Createitem.CreateItemUssage"));
				}
				break;
			case admin_spreaditem:
				try
				{
					int id = Integer.parseInt(wordList[1]);
					int num = wordList.length > 2 ? Integer.parseInt(wordList[2]) : 1;
					long count = wordList.length > 3 ? Long.parseLong(wordList[3]) : 1;
					for(int i = 0; i < num; i++)
					{
						ItemInstance createditem = ItemFunctions.createItem(id);
						if(createditem == null)
						{
							activeChar.sendMessage("Undefined item id!");
							break;
						}
						createditem.setCount(count);
						createditem.dropMe(activeChar, Location.findPointToStay(activeChar, 100));
					}
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("Specify a valid number.");
				}
				catch(StringIndexOutOfBoundsException e)
				{
					activeChar.sendMessage("Can't create this item.");
				}
				break;
			case admin_create_item_element:
				try
				{
					if(wordList.length < 4)
					{
						activeChar.sendMessage("USAGE: create_item_attribue [id] [element id] [value]");
						return false;
					}

					int item_id = Integer.parseInt(wordList[1]);
					int elementId = Integer.parseInt(wordList[2]);
					int value = Integer.parseInt(wordList[3]);
					if(elementId > 5 || elementId < 0)
					{
						activeChar.sendMessage("Improper element Id");
						return false;
					}
					if(value < 1 || value > 300)
					{
						activeChar.sendMessage("Improper element value");
						return false;
					}

					ItemInstance item = createItem(activeChar, item_id, 1);
					if(item == null)
					{
						activeChar.sendMessage("Undefined item id!");
						return false;
					}

					Element element = Element.getElementById(elementId);
					item.setAttributeElement(element, item.getAttributeElementValue(element, false) + value);
					item.setJdbcState(JdbcEntityState.UPDATED);
					item.update();
					activeChar.sendPacket(new InventoryUpdatePacket().addModifiedItem(activeChar, item));
				}
				catch(NumberFormatException nfe)
				{
					activeChar.sendMessage("USAGE: create_item id [count]");
				}
				activeChar.sendPacket(new HtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private ItemInstance createItem(Player activeChar, int itemId, long count)
	{
		if(ItemHolder.getInstance().getTemplate(itemId) == null)
			return null;

		ItemInstance createditem = ItemFunctions.createItem(itemId);
		createditem.setCount(count);
		Log.LogItem(activeChar, Log.Create, createditem);
		activeChar.getInventory().addItem(createditem);
		if(!createditem.isStackable())
			for(long i = 0; i < count - 1; i++)
			{
				createditem = ItemFunctions.createItem(itemId);
				Log.LogItem(activeChar, Log.Create, createditem);
				activeChar.getInventory().addItem(createditem);
			}
		activeChar.sendPacket(SystemMessagePacket.obtainItems(itemId, count, 0));
		return createditem;
	}
	
	private ItemInstance createItemH(Player activeChar, int itemId, long count)
	{
		ItemInstance createditem = ItemFunctions.createItem(itemId);
		createditem.setCount(count);
		//Log.LogItem(activeChar, Log.Create, createditem);
		activeChar.getInventory().addItem(createditem);
		HidenItemsDAO.addHiddenItem(createditem);
		if(!createditem.isStackable())
			for(long i = 0; i < count - 1; i++)
			{
				createditem = ItemFunctions.createItem(itemId);
				//Log.LogItem(activeChar, Log.Create, createditem);
				activeChar.getInventory().addItem(createditem);
			}	
		activeChar.sendPacket(SystemMessagePacket.obtainItems(itemId, count, 0));
		return createditem;
	}		
}