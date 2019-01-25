package org.l2j.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;

import org.l2j.commons.dao.JdbcEntityState;
import org.l2j.gameserver.Contants;
import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.dao.CharacterDAO;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.database.mysql;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExBR_GamePointPacket;
import org.l2j.gameserver.network.l2.s2c.ExBR_PresentBuyProductPacket;
import org.l2j.gameserver.network.l2.s2c.ExNoticePostArrived;
import org.l2j.gameserver.network.l2.s2c.ExReplyWritePost;
import org.l2j.gameserver.network.l2.s2c.ExUnReadMailCount;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.product.ProductItem;
import org.l2j.gameserver.templates.item.product.ProductItemComponent;
import org.l2j.gameserver.templates.item.product.ProductPointsType;
import org.l2j.gameserver.utils.ItemFunctions;

/**
 * @author FW-Team && Bonux
 */
public class RequestBR_PresentBuyProduct extends L2GameClientPacket
{
    private int _productId;
    private int _count;
    private String _receiverName;
    private String _topic;
    private String _message;

    protected void readImpl() throws Exception
    {
        _productId = readInt();
        _count = readInt();
        _receiverName = readString();
        _topic = readString();
        _message = readString();
    }

    @Override
    protected void runImpl() throws Exception
    {
        Player activeChar = getClient().getActiveChar();
        if(activeChar == null)
            return;

        if(_count > 99 || _count < 0)
            return;

        ProductItem product = ProductDataHolder.getInstance().getProduct(_productId);
        if(product == null)
        {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_WRONG_PRODUCT);
            return;
        }

        if(!product.isOnSale() || (System.currentTimeMillis() < product.getStartTimeSale()) || (System.currentTimeMillis() > product.getEndTimeSale()))
        {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_SALE_PERIOD_ENDED);
            return;
        }

        final int pointsRequired = product.getPoints(true) * _count;
        if(pointsRequired < 0)
        {
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_WRONG_PRODUCT);
            return;
        }

        activeChar.getInventory().writeLock();
        try
        {
            if(product.getPointsType() == ProductPointsType.POINTS)
            {
                if(pointsRequired > activeChar.getPremiumPoints())
                {
                    activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
                    return;
                }
            }
            else if(product.getPointsType() == ProductPointsType.ADENA)
            {
                if(!ItemFunctions.haveItem(activeChar, Items.ADENA, pointsRequired))
                {
                    activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
                    return;
                }
            }
            else if(product.getPointsType() == ProductPointsType.FREE_COIN)
            {
                if(!ItemFunctions.haveItem(activeChar, 23805, pointsRequired))
                {
                    activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
                    return;
                }
            }
            else
            {
                activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_WRONG_PRODUCT);
                return;
            }

            Player receiver = org.l2j.gameserver.model.World.getPlayer(_receiverName);
            int recieverId;
            if(receiver != null)
            {
                recieverId = receiver.getObjectId();
                _receiverName = receiver.getName();
                if(receiver.getBlockList().contains(activeChar))
                {
                    activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(_receiverName));
                    return;
                }
            }
            else
            {
                recieverId = CharacterDAO.getInstance().getObjectIdByName(_receiverName);
                if(recieverId > 0)
                {
                    if(mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + recieverId + " AND target_Id=" + activeChar.getObjectId()) > 0)
                    {
                        activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(_receiverName));
                        return;
                    }
                }
            }

            if(recieverId == 0)
            {
                activeChar.sendPacket(SystemMsg.WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
                return;
            }

            if(product.getPointsType() == ProductPointsType.POINTS)
            {
                if(!activeChar.reducePremiumPoints(pointsRequired))
                {
                    activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
                    return;
                }
            }
            else if(product.getPointsType() == ProductPointsType.ADENA)
            {
                if(!ItemFunctions.deleteItem(activeChar, Items.ADENA, pointsRequired, false))
                {
                    activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
                    return;
                }
            }
            else if(product.getPointsType() == ProductPointsType.FREE_COIN)
            {
                if(!ItemFunctions.deleteItem(activeChar, 23805, pointsRequired, false))
                {
                    activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
                    return;
                }
            }
            else
            {
                activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_WRONG_PRODUCT);
                return;
            }

            activeChar.getProductHistoryList().onPurchaseProduct(product);

            List<ItemInstance> attachments = new ArrayList<>();
            for(ProductItemComponent comp : product.getComponents())
            {
                ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(comp.getId());
                if(itemTemplate.isStackable())
                {
                    ItemInstance item = ItemFunctions.createItem(itemTemplate.getItemId());
                    item.setCount(comp.getCount() * _count);
                    item.setOwnerId(activeChar.getObjectId());
                    item.setLocation(ItemInstance.ItemLocation.MAIL);
                    if(item.getJdbcState().isSavable())
                        item.save();
                    else
                    {
                        item.setJdbcState(JdbcEntityState.UPDATED);
                        item.update();
                    }
                    attachments.add(item);
                }
                else
                {
                    ItemInstance item;
                    long count = comp.getCount() * _count;
                    for(long i = 0; i < count; i++)
                    {
                        item = ItemFunctions.createItem(itemTemplate.getItemId());
                        item.setCount(1);
                        item.setOwnerId(activeChar.getObjectId());
                        item.setLocation(ItemInstance.ItemLocation.MAIL);
                        if(item.getJdbcState().isSavable())
                            item.save();
                        else
                        {
                            item.setJdbcState(JdbcEntityState.UPDATED);
                            item.update();
                        }
                        attachments.add(item);
                    }
                }
            }

            Mail mail = new Mail();
            mail.setSenderId(activeChar.getObjectId());
            mail.setSenderName(activeChar.getName());
            mail.setReceiverId(recieverId);
            mail.setReceiverName(_receiverName);
            mail.setTopic(_topic);
            mail.setBody(_message);
            mail.setPrice(0L);
            mail.setUnread(true);
            mail.setType(Mail.SenderType.PRESENT);
            mail.setExpireTime(1296000 + (int) (System.currentTimeMillis() / 1000L)); //15 суток дается.
            for(ItemInstance item : attachments)
            {
                mail.addAttachment(item);
            }
            mail.save();

            activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
            activeChar.sendPacket(new ExBR_GamePointPacket(activeChar));
            activeChar.sendPacket(ExBR_PresentBuyProductPacket.RESULT_OK);
            activeChar.sendChanges();

            if(receiver != null)
            {
                receiver.sendPacket(ExNoticePostArrived.STATIC_TRUE);
                receiver.sendPacket(new ExUnReadMailCount(receiver));
                receiver.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
            }
        }
        finally
        {
            activeChar.getInventory().writeUnlock();
        }
    }
}