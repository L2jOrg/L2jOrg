package org.l2j.scripts.npc.model.residences.castle;

import org.l2j.scripts.npc.model.residences.ResidenceManager;
import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.data.dao.CastleDamageZoneDAO;
import org.l2j.gameserver.data.dao.CastleDoorUpgradeDAO;
import org.l2j.gameserver.data.xml.holder.MultiSellHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import org.l2j.gameserver.model.entity.events.objects.DoorObject;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.entity.residence.ResidenceSide;
import org.l2j.gameserver.model.instances.DoorInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.Privilege;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.CastleSiegeInfoPacket;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.Log;
import org.l2j.gameserver.utils.ReflectionUtils;

import java.util.List;
import java.util.StringTokenizer;


public class ChamberlainInstance extends ResidenceManager
{
	private static final long serialVersionUID = 1L;

	private static final int CloakLight = 34925;
	private static final int CloakDark = 34926;

	public ChamberlainInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void setDialogs()
	{
		_mainDialog = "residence2/castle/chamberlain_saius001.htm";
		_failDialog = "castle/chamberlain/chamberlain-notlord.htm";
		_siegeDialog = _mainDialog;
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		int condition = getCond(player);
		if(condition != COND_OWNER)
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		String val = "";
		if(st.countTokens() >= 1)
			val = st.nextToken();

		Castle castle = getCastle();
		if(actualCommand.equalsIgnoreCase("viewSiegeInfo"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			player.sendPacket(new CastleSiegeInfoPacket(castle,  player));
		}
		else if(actualCommand.equalsIgnoreCase("ManageTreasure"))
		{
			if(!player.isClanLeader())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("TakeTreasure"))
		{
			if(!player.isClanLeader())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(!val.equals(""))
			{
				long treasure = Long.parseLong(val);
				if(castle.getTreasury() < treasure)
				{
					HtmlMessage html = new HtmlMessage(this);
					html.setFile("castle/chamberlain/chamberlain-havenottreasure.htm");
					html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
					html.replace("%Requested%", String.valueOf(treasure));
					player.sendPacket(html);
					return;
				}
				if(treasure > 0)
				{
					castle.addToTreasuryNoTax(-treasure, false);
					Log.add(castle.getName() + "|" + -treasure + "|CastleChamberlain", "treasury");
					player.addAdena(treasure);
				}
			}

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("PutTreasure"))
		{
			if(!val.equals(""))
			{
				long treasure = Long.parseLong(val);
				if(treasure > player.getAdena())
				{
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}
				if(treasure > 0)
				{
					castle.addToTreasuryNoTax(treasure, false);
					Log.add(castle.getName() + "|" + treasure + "|CastleChamberlain", "treasury");
					player.reduceAdena(treasure, true);
				}
			}

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
			html.replace("%Treasure%", String.valueOf(castle.getTreasury()));
			html.replace("%CollectedShops%", String.valueOf(castle.getCollectedShops()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("operate_door")) // door control
		{
			if(!isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(castle.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, "residence2/castle/chamberlain_saius021.htm", false);
				return;
			}
			if(!val.equals(""))
			{
				boolean open = Integer.parseInt(val) == 1;
				while(st.hasMoreTokens())
				{
					DoorInstance door = ReflectionUtils.getDoor(Integer.parseInt(st.nextToken()));
					if(open)
						door.openMe(player, true);
					else
						door.closeMe(player, true);
				}
			}

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/" + getNpcId() + "-d.htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_castle"))
		{
			if(!checkSiegeFunctions(player))
				return;

			showChatWindow(player, "castle/chamberlain/chamberlain-upgrades.htm", false);
		}
		else if(actualCommand.equalsIgnoreCase("reinforce"))
		{
			if(!checkSiegeFunctions(player))
				return;

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/doorStrengthen-" + castle.getName() + ".htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("trap_select"))
		{
			if(!checkSiegeFunctions(player))
				return;

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/trap_select-" + castle.getName() + ".htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("buy_trap"))
		{
			if(!checkSiegeFunctions(player))
				return;

			if(castle.getSiegeEvent().getObjects(CastleSiegeEvent.BOUGHT_ZONES).contains(val))
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("castle/chamberlain/trapAlready.htm");
				player.sendPacket(html);
				return;
			}

			List<CastleDamageZoneObject> objects = castle.getSiegeEvent().getObjects(val);
			long price = 0;
			for(CastleDamageZoneObject o : objects)
				price += o.getPrice();

			if(player.getClan().getAdenaCount() < price)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			player.getClan().getWarehouse().destroyItemByItemId(Items.ADENA, price);
			castle.getSiegeEvent().addObject(CastleSiegeEvent.BOUGHT_ZONES, val);
			CastleDamageZoneDAO.getInstance().insert(castle, val);

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/trapSuccess.htm");
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("door_manage"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(castle.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, "residence2/castle/chamberlain_saius021.htm", false);
				return;
			}

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/doorManage.htm");
			html.replace("%id%", val);
			html.replace("%type%", st.nextToken());
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_door_confirm"))
		{
			if(!isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			int id = Integer.parseInt(val);
			int type = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());
			long price = getDoorCost(type, level);

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("castle/chamberlain/doorConfirm.htm");
			html.replace("%id%", String.valueOf(id));
			html.replace("%level%", String.valueOf(level));
			html.replace("%type%", String.valueOf(type));
			html.replace("%price%", String.valueOf(price));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("upgrade_door"))
		{
			if(checkSiegeFunctions(player))
				return;

			int id = Integer.parseInt(val);
			int type = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());
			long price = getDoorCost(type, level);

			List<DoorObject> doorObjects = castle.getSiegeEvent().getObjects(SiegeEvent.DOORS);
			DoorObject targetDoorObject = null;
			for(DoorObject o : doorObjects)
				if(o.getUId() == id)
				{
					targetDoorObject = o;
					break;
				}

			DoorInstance door = targetDoorObject.getDoor();
			int upgradeHp = (door.getMaxHp() - door.getUpgradeHp()) * level - door.getMaxHp();

			if(price == 0 || upgradeHp < 0)
			{
				player.sendMessage(new CustomMessage("common.Error"));
				return;
			}

			if(door.getUpgradeHp() >= upgradeHp)
			{
				int oldLevel = door.getUpgradeHp() / (door.getMaxHp() - door.getUpgradeHp()) + 1;
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("castle/chamberlain/doorAlready.htm");
				html.replace("%level%", String.valueOf(oldLevel));
				player.sendPacket(html);
				return;
			}

			if(player.getClan().getAdenaCount() < price)
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			player.getClan().getWarehouse().destroyItemByItemId(Items.ADENA, price);

			targetDoorObject.setUpgradeValue(castle.<SiegeEvent>getSiegeEvent(), upgradeHp);
			CastleDoorUpgradeDAO.getInstance().insert(door.getDoorId(), upgradeHp);
		}
		else if(actualCommand.equalsIgnoreCase("report")) // Report page
		{
			if(!isHaveRigths(player, Clan.CP_CS_USE_FUNCTIONS))
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}

			HtmlMessage html = new HtmlMessage(this);
			html.setFile("residence2/castle/chamberlain_saius002.htm");
			html.replace("<?my_pledge_name?>", player.getClan().getName());
			html.replace("<?my_owner_name?>", player.getName());
			html.replace("<?feud_name?>", castle.getNpcStringName());
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("Crown")) // Give Crown to Castle Owner
		{
			if(!player.isClanLeader())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			if(player.getInventory().getItemByItemId(6841) == null)
			{
				ItemFunctions.addItem(player, 6841, 1, true);

				HtmlMessage html = new HtmlMessage(this);
				html.setFile("castle/chamberlain/chamberlain-givecrown.htm");
				html.replace("%CharName%", player.getName());
				html.replace("%FeudName%", castle.getNpcStringName());
				player.sendPacket(html);
			}
			else
			{
				HtmlMessage html = new HtmlMessage(this);
				html.setFile("castle/chamberlain/alreadyhavecrown.htm");
				player.sendPacket(html);
			}
		}
		else if(actualCommand.equalsIgnoreCase("Cloak"))
		{
			if(castle.getId() == 5)
			{
				int itemId = getCastle().getResidenceSide() == ResidenceSide.LIGHT ? CloakLight : CloakDark;

				// Плащ может получить только маркиз и выше.
				if(player.getInventory().getItemByItemId(itemId) != null)
				{
					showChatWindow(player, "castle/chamberlain/alreadyhavecloak.htm", false);
					return;
				}
				else
				{
					ItemFunctions.addItem(player, itemId, 1, true);
					showChatWindow(player, "castle/chamberlain/chamberlain-givecloak.htm", false);
					return;
				}
			}
		}
		else if(actualCommand.equalsIgnoreCase("manageFunctions"))
		{
			if(!player.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS))
				showChatWindow(player, "residence2/castle/chamberlain_saius063.htm", false);
			else
				showChatWindow(player, "residence2/castle/chamberlain_saius065.htm", false);
		}
		else if(actualCommand.equalsIgnoreCase("manageSiegeFunctions"))
		{
			if(!player.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS))
				showChatWindow(player, "residence2/castle/chamberlain_saius063.htm", false);
			else
				showChatWindow(player, "residence2/castle/chamberlain_saius052.htm", false);
		}
		else if(actualCommand.equalsIgnoreCase("items"))
		{
			HtmlMessage html = new HtmlMessage(this);
			if(castle.getId() == 5)
				html.setFile("residence2/castle/chamberlain_saius064a.htm");
			else
				html.setFile("residence2/castle/chamberlain_saius064.htm");
			html.replace("%npcId%", String.valueOf(getNpcId()));
			player.sendPacket(html);
		}
		else if(actualCommand.equalsIgnoreCase("default"))
		{
			HtmlMessage html = new HtmlMessage(this);
			html.setFile("residence2/castle/chamberlain_saius001.htm");
			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		int condition = getCond(player);
		if(condition != COND_OWNER)
			return;

		Castle castle = getCastle();
		if(ask == -201)
		{
			if(reply == 1)
			{
				onBypassFeedback(player, "report");
			}
			else if(reply == 3)
			{
				onBypassFeedback(player, "ManageTreasure");
			}
			else if(reply == 4)
			{
				onBypassFeedback(player, "manageFunctions");
			}
			else if(reply == 5)
			{
				onBypassFeedback(player, "viewSiegeInfo");
			}
			else if(reply == 7)
			{
				onBypassFeedback(player, "items");
			}
			else if(reply == 8)
			{
				onBypassFeedback(player, "banish");
			}
			else if(reply == 9)
			{
				onBypassFeedback(player, "operate_door");
			}
			else if(reply == 10)
			{
				onBypassFeedback(player, "manageSiegeFunctions");
			}
			else if(reply == 12)
			{
				showShopWindow(player, 1, true);
			}
			else if(reply == 13)
			{
				onBypassFeedback(player, "Crown");
			}
			else if(reply == 14)
			{
				onBypassFeedback(player, "cloak");
			}
			else if(reply == 103)
			{
				onBypassFeedback(player, "functions");
			}
			else if(reply == 105)
			{
				onBypassFeedback(player, "manage");
			}
		}
		else if(ask == -204)
		{
			if(reply == 1)
			{
				onBypassFeedback(player, "reinforce");
			}
			else if(reply == 2)
			{
				onBypassFeedback(player, "trap_select");
			}
		}
		else if(ask == -1990)
		{
			MultiSellHolder.getInstance().SeparateAndSend((int) reply, player, 0);
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	protected boolean isCheckBuyFunction()
	{
		return false;
	}

	@Override
	protected int getCond(Player player)
	{
		if(player.isGM())
			return COND_OWNER;
		Residence castle = getCastle();
		if(castle != null && castle.getId() != 0)
			if(player.getClan() != null)
				if(castle.getSiegeEvent().isInProgress())
					return COND_SIEGE; // Busy because of siege
				else if(castle.getOwnerId() == player.getClanId())
				{
					if(player.isClanLeader()) // Leader of clan
						return COND_OWNER;
					if(isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT) || // doors
							isHaveRigths(player, Clan.CP_CS_MANOR_ADMIN) || // manor
							isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE) || // siege
							isHaveRigths(player, Clan.CP_CS_USE_FUNCTIONS) || // funcs
							isHaveRigths(player, Clan.CP_CS_DISMISS) || // banish
							isHaveRigths(player, Clan.CP_CS_TAXES) || // tax
							isHaveRigths(player, Clan.CP_CS_MERCENARIES) || // merc
							isHaveRigths(player, Clan.CP_CS_SET_FUNCTIONS) //funcs
					)
						return COND_OWNER; // Есть какие либо замковые привилегии
				}

		return COND_FAIL;
	}

	private long getDoorCost(int type, int level)
	{
		int price = 0;

		switch(type)
		{
			case 1: // Главные ворота
				switch(level)
				{
					case 2:
						price = 3000000;
						break;
					case 3:
						price = 4000000;
						break;
					case 5:
						price = 5000000;
						break;
				}
				break;
			case 2: // Внутренние ворота
				switch(level)
				{
					case 2:
						price = 750000;
						break;
					case 3:
						price = 900000;
						break;
					case 5:
						price = 1000000;
						break;
				}
				break;
			case 3: // Стены
				switch(level)
				{
					case 2:
						price = 1600000;
						break;
					case 3:
						price = 1800000;
						break;
					case 5:
						price = 2000000;
						break;
				}
				break;
		}

		return price;
	}

	@Override
	protected Residence getResidence()
	{
		return getCastle();
	}

	@Override
	public L2GameServerPacket decoPacket()
	{
		return null;
	}

	@Override
	protected int getPrivUseFunctions()
	{
		return Clan.CP_CS_USE_FUNCTIONS;
	}

	@Override
	protected int getPrivSetFunctions()
	{
		return Clan.CP_CS_SET_FUNCTIONS;
	}

	@Override
	protected int getPrivDismiss()
	{
		return Clan.CP_CS_DISMISS;
	}

	@Override
	protected int getPrivDoors()
	{
		return Clan.CP_CS_ENTRY_EXIT;
	}

	private boolean checkSiegeFunctions(Player player)
	{
		Castle castle = getCastle();
		if(!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR))
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}

		if(castle.getSiegeEvent().isInProgress())
		{
			showChatWindow(player, "residence2/castle/chamberlain_saius021.htm", false);
			return false;
		}
		return true;
	}

	@Override
	protected String getDialogsPrefix()
	{
		return "castle";
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "residence2/castle/";
	}
}