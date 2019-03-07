/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.primeshop;

import com.l2jmobius.Config;
import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.data.sql.impl.CharNameTable;
import com.l2jmobius.gameserver.data.xml.impl.PrimeShopData;
import com.l2jmobius.gameserver.enums.MailType;
import com.l2jmobius.gameserver.instancemanager.MailManager;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.actor.request.PrimeShopRequest;
import com.l2jmobius.gameserver.model.entity.Message;
import com.l2jmobius.gameserver.model.itemcontainer.Inventory;
import com.l2jmobius.gameserver.model.itemcontainer.Mail;
import com.l2jmobius.gameserver.model.primeshop.PrimeShopGroup;
import com.l2jmobius.gameserver.model.primeshop.PrimeShopItem;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import com.l2jmobius.gameserver.network.serverpackets.primeshop.ExBRBuyProduct.ExBrProductReplyType;
import com.l2jmobius.gameserver.network.serverpackets.primeshop.ExBRGamePoint;
import com.l2jmobius.gameserver.util.Util;

import java.util.Calendar;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRPresentBuyProduct implements IClientIncomingPacket
{
	private static final int HERO_COINS = 23805;
	
	private int _brId;
	private int _count;
	private String _charName;
	private String _mailTitle;
	private String _mailBody;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_brId = packet.readD();
		_count = packet.readD();
		_charName = packet.readS();
		_mailTitle = packet.readS();
		_mailBody = packet.readS();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final int receiverId = CharNameTable.getInstance().getIdByName(_charName);
		if (receiverId <= 0)
		{
			activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER));
			return;
		}
		
		if (activeChar.hasItemRequest() || activeChar.hasRequest(PrimeShopRequest.class))
		{
			activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return;
		}
		
		activeChar.addRequest(new PrimeShopRequest(activeChar));
		
		final PrimeShopGroup item = PrimeShopData.getInstance().getItem(_brId);
		if (validatePlayer(item, _count, activeChar))
		{
			final int price = (item.getPrice() * _count);
			final int paymentId = validatePaymentId(activeChar, item, price);
			
			if (paymentId < 0)
			{
				activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
				activeChar.removeRequest(PrimeShopRequest.class);
				return;
			}
			else if (paymentId > 0)
			{
				if (!activeChar.destroyItemByItemId("PrimeShop-" + item.getBrId(), paymentId, price, activeChar, true))
				{
					activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
					activeChar.removeRequest(PrimeShopRequest.class);
					return;
				}
			}
			else if (paymentId == 0)
			{
				if (activeChar.getPrimePoints() < price)
				{
					activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
					activeChar.removeRequest(PrimeShopRequest.class);
					return;
				}
				activeChar.setPrimePoints(activeChar.getPrimePoints() - price);
			}
			
			activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.SUCCESS));
			activeChar.sendPacket(new ExBRGamePoint(activeChar));
			
			final Message mail = new Message(receiverId, _mailTitle, _mailBody, MailType.PRIME_SHOP_GIFT);
			
			final Mail attachement = mail.createAttachments();
			for (PrimeShopItem subItem : item.getItems())
			{
				attachement.addItem("Prime Shop Gift", subItem.getId(), subItem.getCount(), activeChar, this);
			}
			MailManager.getInstance().sendMessage(mail);
		}
		
		activeChar.removeRequest(PrimeShopRequest.class);
	}
	
	/**
	 * @param item
	 * @param count
	 * @param player
	 * @return
	 */
	private static boolean validatePlayer(PrimeShopGroup item, int count, L2PcInstance player)
	{
		final long currentTime = System.currentTimeMillis() / 1000;
		if (item == null)
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_PRODUCT));
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid brId from Prime", Config.DEFAULT_PUNISH);
			return false;
		}
		else if ((count < 1) || (count > 99))
		{
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid itemcount [" + count + "] from Prime", Config.DEFAULT_PUNISH);
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return false;
		}
		else if ((item.getMinLevel() > 0) && (item.getMinLevel() > player.getLevel()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER));
			return false;
		}
		else if ((item.getMaxLevel() > 0) && (item.getMaxLevel() < player.getLevel()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER));
			return false;
		}
		else if ((item.getMinBirthday() > 0) && (item.getMinBirthday() > player.getBirthdays()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return false;
		}
		else if ((item.getMaxBirthday() > 0) && (item.getMaxBirthday() < player.getBirthdays()))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_USER_STATE));
			return false;
		}
		else if ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) & item.getDaysOfWeek()) == 0)
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.NOT_DAY_OF_WEEK));
			return false;
		}
		else if ((item.getStartSale() > 1) && (item.getStartSale() > currentTime))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.BEFORE_SALE_DATE));
			return false;
		}
		else if ((item.getEndSale() > 1) && (item.getEndSale() < currentTime))
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.AFTER_SALE_DATE));
			return false;
		}
		
		final int weight = item.getWeight() * count;
		final long slots = item.getCount() * count;
		
		if (player.getInventory().validateWeight(weight))
		{
			if (!player.getInventory().validateCapacity(slots))
			{
				player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTROY_OVERFLOW));
				return false;
			}
		}
		else
		{
			player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTROY_OVERFLOW));
			return false;
		}
		
		return true;
	}
	
	private static int validatePaymentId(L2PcInstance player, PrimeShopGroup item, long amount)
	{
		switch (item.getPaymentType())
		{
			case 0: // Prime points
			{
				return 0;
			}
			case 1: // Adenas
			{
				return Inventory.ADENA_ID;
			}
			case 2: // Hero coins
			{
				return HERO_COINS;
			}
		}
		
		return -1;
	}
}
