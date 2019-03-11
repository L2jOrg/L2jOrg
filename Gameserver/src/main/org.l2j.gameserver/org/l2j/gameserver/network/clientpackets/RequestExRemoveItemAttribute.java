package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExBaseAttributeCancelResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.model.items.type.CrystalType;

import java.nio.ByteBuffer;

public class RequestExRemoveItemAttribute extends IClientIncomingPacket {
    private int _objectId;
    private long _price;
    private byte _element;

    public RequestExRemoveItemAttribute() {
    }

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
        _element = (byte) packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_objectId);
        if (targetItem == null) {
            return;
        }

        final AttributeType type = AttributeType.findByClientId(_element);
        if (type == null) {
            return;
        }

        if ((targetItem.getAttributes() == null) || (targetItem.getAttribute(type) == null)) {
            return;
        }

        if (activeChar.reduceAdena("RemoveElement", getPrice(targetItem), activeChar, true)) {
            targetItem.clearAttribute(type);
            client.sendPacket(new UserInfo(activeChar));

            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(targetItem);
            activeChar.sendInventoryUpdate(iu);
            SystemMessage sm;
            final AttributeType realElement = targetItem.isArmor() ? type.getOpposite() : type;
            if (targetItem.getEnchantLevel() > 0) {
                if (targetItem.isArmor()) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_S_S3_ATTRIBUTE_WAS_REMOVED_SO_RESISTANCE_TO_S4_WAS_DECREASED);
                } else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_S_S3_ATTRIBUTE_HAS_BEEN_REMOVED);
                }
                sm.addInt(targetItem.getEnchantLevel());
                sm.addItemName(targetItem);
                if (targetItem.isArmor()) {
                    sm.addAttribute(realElement.getClientId());
                    sm.addAttribute(realElement.getOpposite().getClientId());
                }
            } else {
                if (targetItem.isArmor()) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_WAS_REMOVED_AND_RESISTANCE_TO_S3_WAS_DECREASED);
                } else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_BEEN_REMOVED);
                }
                sm.addItemName(targetItem);
                if (targetItem.isArmor()) {
                    sm.addAttribute(realElement.getClientId());
                    sm.addAttribute(realElement.getOpposite().getClientId());
                }
            }
            client.sendPacket(sm);
            client.sendPacket(new ExBaseAttributeCancelResult(targetItem.getObjectId(), _element));
        } else {
            client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_FUNDS_TO_CANCEL_THIS_ATTRIBUTE);
        }
    }

    private long getPrice(L2ItemInstance item) {
        switch (item.getItem().getCrystalType()) {
            case CrystalType.S: {
                if (item.getItem() instanceof L2Weapon) {
                    _price = 50000;
                } else {
                    _price = 40000;
                }
                break;
            }
            case CrystalType.S80: {
                if (item.getItem() instanceof L2Weapon) {
                    _price = 100000;
                } else {
                    _price = 80000;
                }
                break;
            }
            case CrystalType.S84: {
                if (item.getItem() instanceof L2Weapon) {
                    _price = 200000;
                } else {
                    _price = 160000;
                }
                break;
            }
            case CrystalType.R: {
                if (item.getItem() instanceof L2Weapon) {
                    _price = 400000;
                } else {
                    _price = 320000;
                }
                break;
            }
            case CrystalType.R95: {
                if (item.getItem() instanceof L2Weapon) {
                    _price = 800000;
                } else {
                    _price = 640000;
                }
                break;
            }
            case CrystalType.R99: {
                if (item.getItem() instanceof L2Weapon) {
                    _price = 3200000;
                } else {
                    _price = 2560000;
                }
                break;
            }
        }

        return _price;
    }
}
