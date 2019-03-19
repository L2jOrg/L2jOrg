package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.L2AccessLevel;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.itemcontainer.Mail;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExNoticePostSent;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author Migi, DS
 */
public final class RequestSendPost extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestSendPost.class);
    private static final int BATCH_LENGTH = 12; // length of the one item

    private static final int MAX_RECV_LENGTH = 16;
    private static final int MAX_SUBJ_LENGTH = 128;
    private static final int MAX_TEXT_LENGTH = 512;
    private static final int MAX_ATTACHMENTS = 8;
    private static final int INBOX_SIZE = 240;
    private static final int OUTBOX_SIZE = 240;

    private static final int MESSAGE_FEE = 100;
    private static final int MESSAGE_FEE_PER_SLOT = 1000; // 100 adena message fee + 1000 per each item slot

    private String _receiver;
    private boolean _isCod;
    private String _subject;
    private String _text;
    private AttachmentItem _items[] = null;
    private long _reqAdena;

    public RequestSendPost() {
    }

    @Override
    public void readImpl(ByteBuffer packet) throws InvalidDataPacketException {
        _receiver = readString(packet);
        _isCod = packet.getInt() != 0;
        _subject = readString(packet);
        _text = readString(packet);

        final int attachCount = packet.getInt();
        if ((attachCount < 0) || (attachCount > Config.MAX_ITEM_IN_PACKET) || (((attachCount * BATCH_LENGTH) + 8) != packet.remaining())) {
            throw new InvalidDataPacketException();
        }

        if (attachCount > 0) {
            _items = new AttachmentItem[attachCount];
            for (int i = 0; i < attachCount; i++) {
                final int objectId = packet.getInt();
                final long count = packet.getLong();
                if ((objectId < 1) || (count < 0)) {
                    _items = null;
                    throw new InvalidDataPacketException();
                }
                _items[i] = new AttachmentItem(objectId, count);
            }
        }

        _reqAdena = packet.getLong();
    }

    @Override
    public void runImpl() {
        if (!Config.ALLOW_MAIL) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!Config.ALLOW_ATTACHMENTS) {
            _items = null;
            _isCod = false;
            _reqAdena = 0;
        }

        if (!activeChar.getAccessLevel().allowTransaction()) {
            activeChar.sendMessage("Transactions are disabled for your Access Level.");
            return;
        }

        if (!activeChar.isInsideZone(ZoneId.PEACE) && (_items != null)) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (activeChar.getActiveTradeList() != null) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
            return;
        }

        if (activeChar.hasItemRequest()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_SEND_MAIL_WHILE_ENCHANTING_AN_ITEM_BESTOWING_AN_ATTRIBUTE_OR_COMBINING_JEWELS);
            return;
        }

        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        if (_receiver.length() > MAX_RECV_LENGTH) {
            activeChar.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED);
            return;
        }

        if (_subject.length() > MAX_SUBJ_LENGTH) {
            activeChar.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return;
        }

        if (_text.length() > MAX_TEXT_LENGTH) {
            // not found message for this
            activeChar.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return;
        }

        if ((_items != null) && (_items.length > MAX_ATTACHMENTS)) {
            activeChar.sendPacket(SystemMessageId.ITEM_SELECTION_IS_POSSIBLE_UP_TO_8);
            return;
        }

        if ((_reqAdena < 0) || (_reqAdena > Inventory.MAX_ADENA)) {
            return;
        }

        if (_isCod) {
            if (_reqAdena == 0) {
                activeChar.sendPacket(SystemMessageId.WHEN_NOT_ENTERING_THE_AMOUNT_FOR_THE_PAYMENT_REQUEST_YOU_CANNOT_SEND_ANY_MAIL);
                return;
            }
            if ((_items == null) || (_items.length == 0)) {
                activeChar.sendPacket(SystemMessageId.IT_S_A_PAYMENT_REQUEST_TRANSACTION_PLEASE_ATTACH_THE_ITEM);
                return;
            }
        }

        if (FakePlayerData.getInstance().isTalkable(_receiver)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1);
            sm.addString(FakePlayerData.getInstance().getProperName(_receiver));
            activeChar.sendPacket(sm);
            return;
        }

        final int receiverId = CharNameTable.getInstance().getIdByName(_receiver);
        if (receiverId <= 0) {
            activeChar.sendPacket(SystemMessageId.WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
            return;
        }

        if (receiverId == activeChar.getObjectId()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
            return;
        }

        final int level = CharNameTable.getInstance().getAccessLevelById(receiverId);
        final L2AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(level);

        if ((accessLevel != null) && accessLevel.isGm() && !activeChar.getAccessLevel().isGm()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_MESSAGE_TO_C1_DID_NOT_REACH_ITS_RECIPIENT_YOU_CANNOT_SEND_MAIL_TO_THE_GM_STAFF);
            sm.addString(_receiver);
            activeChar.sendPacket(sm);
            return;
        }

        if (activeChar.isJailed() && ((Config.JAIL_DISABLE_TRANSACTION && (_items != null)) || Config.JAIL_DISABLE_CHAT)) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (BlockList.isInBlockList(receiverId, activeChar.getObjectId())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1);
            sm.addString(_receiver);
            activeChar.sendPacket(sm);
            return;
        }

        if (MailManager.getInstance().getOutboxSize(activeChar.getObjectId()) >= OUTBOX_SIZE) {
            activeChar.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }

        if (MailManager.getInstance().getInboxSize(receiverId) >= INBOX_SIZE) {
            activeChar.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }

        if (!client.getFloodProtectors().getSendMail().tryPerformAction("sendmail")) {
            activeChar.sendPacket(SystemMessageId.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED);
            return;
        }

        final Message msg = new Message(activeChar.getObjectId(), receiverId, _isCod, _subject, _text, _reqAdena);
        if (removeItems(activeChar, msg)) {
            MailManager.getInstance().sendMessage(msg);
            activeChar.sendPacket(ExNoticePostSent.valueOf(true));
            activeChar.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_SENT);
        }
    }

    private boolean removeItems(L2PcInstance player, Message msg) {
        long currentAdena = player.getAdena();
        long fee = MESSAGE_FEE;

        if (_items != null) {
            for (AttachmentItem i : _items) {
                // Check validity of requested item
                final L2ItemInstance item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "attach");
                if ((item == null) || !item.isTradeable() || item.isEquipped()) {
                    player.sendPacket(SystemMessageId.THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER);
                    return false;
                }

                fee += MESSAGE_FEE_PER_SLOT;

                if (item.getId() == Inventory.ADENA_ID) {
                    currentAdena -= i.getCount();
                }
            }
        }

        // Check if enough adena and charge the fee
        if ((currentAdena < fee) || !player.reduceAdena("MailFee", fee, null, false)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
            return false;
        }

        if (_items == null) {
            return true;
        }

        final Mail attachments = msg.createAttachments();

        // message already has attachments ? oO
        if (attachments == null) {
            return false;
        }

        // Proceed to the transfer
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (AttachmentItem i : _items) {
            // Check validity of requested item
            final L2ItemInstance oldItem = player.checkItemManipulation(i.getObjectId(), i.getCount(), "attach");
            if ((oldItem == null) || !oldItem.isTradeable() || oldItem.isEquipped()) {
                LOGGER.warn("Error adding attachment for char " + player.getName() + " (olditem == null)");
                return false;
            }

            final L2ItemInstance newItem = player.getInventory().transferItem("SendMail", i.getObjectId(), i.getCount(), attachments, player, msg.getReceiverName() + "[" + msg.getReceiverId() + "]");
            if (newItem == null) {
                LOGGER.warn("Error adding attachment for char " + player.getName() + " (newitem == null)");
                continue;
            }
            newItem.setItemLocation(newItem.getItemLocation(), msg.getId());

            if (playerIU != null) {
                if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                    playerIU.addModifiedItem(oldItem);
                } else {
                    playerIU.addRemovedItem(oldItem);
                }
            }
        }

        // Send updated item list to the player
        if (playerIU != null) {
            player.sendInventoryUpdate(playerIU);
        } else {
            player.sendItemList();
        }

        return true;
    }

    private static class AttachmentItem {
        private final int _objectId;
        private final long _count;

        public AttachmentItem(int id, long num) {
            _objectId = id;
            _count = num;
        }

        public int getObjectId() {
            return _objectId;
        }

        public long getCount() {
            return _count;
        }
    }
}
