package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.mobius.gameserver.datatables.ItemTable;
import org.l2j.gameserver.mobius.gameserver.enums.AttributeType;
import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.model.TradeItem;
import org.l2j.gameserver.mobius.gameserver.model.TradeList;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.mobius.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.l2j.gameserver.mobius.gameserver.model.itemcontainer.Inventory.MAX_ADENA;


public final class SetPrivateStoreListBuy extends IClientIncomingPacket
{
    private TradeItem[] _items = null;

    @Override
    public void readImpl(ByteBuffer packet) throws InvalidDataPacketException {
        final int count = packet.getInt();
        if ((count < 1) || (count > Config.MAX_ITEM_IN_PACKET))
        {
            throw new InvalidDataPacketException();
        }

        _items = new TradeItem[count];
        for (int i = 0; i < count; i++)
        {
            int itemId = packet.getInt();

            final L2Item template = ItemTable.getInstance().getTemplate(itemId);
            if (template == null)
            {
                _items = null;
                throw new InvalidDataPacketException();
            }

            final int enchantLevel = packet.getShort();
            packet.getShort(); // TODO analyse this

            long cnt = packet.getLong();
            long price = packet.getLong();

            if ((itemId < 1) || (cnt < 1) || (price < 0))
            {
                _items = null;
                throw  new InvalidDataPacketException();
            }

            final int option1 = packet.getInt();
            final int option2 = packet.getInt();
            final short attackAttributeId = packet.getShort();
            final int attackAttributeValue = packet.getShort();
            final int defenceFire = packet.getShort();
            final int defenceWater = packet.getShort();
            final int defenceWind = packet.getShort();
            final int defenceEarth = packet.getShort();
            final int defenceHoly = packet.getShort();
            final int defenceDark = packet.getShort();
            final int visualId = packet.getInt();

            final EnsoulOption[] soulCrystalOptions = new EnsoulOption[packet.get()];
            for (int k = 0; k < soulCrystalOptions.length; k++)
            {
                soulCrystalOptions[k] = EnsoulData.getInstance().getOption(packet.getInt());
            }
            final EnsoulOption[] soulCrystalSpecialOptions = new EnsoulOption[packet.get()];
            for (int k = 0; k < soulCrystalSpecialOptions.length; k++)
            {
                soulCrystalSpecialOptions[k] = EnsoulData.getInstance().getOption(packet.getInt());
            }

            final TradeItem item = new TradeItem(template, cnt, price);
            item.setEnchant(enchantLevel);
            item.setAugmentation(option1, option2);
            item.setAttackElementType(AttributeType.findByClientId(attackAttributeId));
            item.setAttackElementPower(attackAttributeValue);
            item.setElementDefAttr(AttributeType.FIRE, defenceFire);
            item.setElementDefAttr(AttributeType.WATER, defenceWater);
            item.setElementDefAttr(AttributeType.WIND, defenceWind);
            item.setElementDefAttr(AttributeType.EARTH, defenceEarth);
            item.setElementDefAttr(AttributeType.HOLY, defenceHoly);
            item.setElementDefAttr(AttributeType.DARK, defenceDark);
            item.setVisualId(visualId);
            item.setSoulCrystalOptions(Arrays.asList(soulCrystalOptions));
            item.setSoulCrystalSpecialOptions(Arrays.asList(soulCrystalSpecialOptions));
            _items[i] = item;
        }
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if (player == null)
        {
            return;
        }

        if (_items == null)
        {
            player.setPrivateStoreType(PrivateStoreType.NONE);
            player.broadcastUserInfo();
            return;
        }

        if (!player.getAccessLevel().allowTransaction())
        {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel())
        {
            player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.isInsideZone(ZoneId.NO_STORE))
        {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        TradeList tradeList = player.getBuyList();
        tradeList.clear();

        // Check maximum number of allowed slots for pvt shops
        if (_items.length > player.getPrivateBuyStoreLimit())
        {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        long totalCost = 0;
        for (TradeItem i : _items)
        {
            if ((MAX_ADENA / i.getCount()) < i.getPrice())
            {
                Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set price more than " + MAX_ADENA + " adena in Private Store - Buy.", Config.DEFAULT_PUNISH);
                return;
            }

            tradeList.addItemByItemId(i.getItem().getId(), i.getCount(), i.getPrice());

            totalCost += (i.getCount() * i.getPrice());
            if (totalCost > MAX_ADENA)
            {
                Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set total price more than " + MAX_ADENA + " adena in Private Store - Buy.", Config.DEFAULT_PUNISH);
                return;
            }
        }

        // Check for available funds
        if (totalCost > player.getAdena())
        {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
            return;
        }

        player.sitDown();
        player.setPrivateStoreType(PrivateStoreType.BUY);
        player.broadcastUserInfo();
        player.broadcastPacket(new PrivateStoreMsgBuy(player));
    }
}
