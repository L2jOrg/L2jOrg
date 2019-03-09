package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.enums.ShotType;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.type.ActionType;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExAutoSoulShot;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * @author Unknown, UnAfraid
 */
public final class RequestAutoSoulShot extends IClientIncomingPacket
{
    private int _itemId;
    private boolean _enable;
    private int _type;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _itemId = packet.getInt();
        _enable = packet.getInt() == 1;
        _type = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        if ((activeChar.getPrivateStoreType() == PrivateStoreType.NONE) && (activeChar.getActiveRequester() == null) && !activeChar.isDead())
        {
            final L2ItemInstance item = activeChar.getInventory().getItemByItemId(_itemId);
            if (item == null)
            {
                return;
            }

            if (_enable)
            {
                if (!activeChar.getInventory().canManipulateWithItemId(item.getId()))
                {
                    activeChar.sendMessage("Cannot use this item.");
                    return;
                }

                if (isSummonShot(item.getItem()))
                {
                    if (activeChar.hasSummon())
                    {
                        final boolean isSoulshot = item.getEtcItem().getDefaultAction() == ActionType.SUMMON_SOULSHOT;
                        final boolean isSpiritshot = item.getEtcItem().getDefaultAction() == ActionType.SUMMON_SPIRITSHOT;
                        if (isSoulshot)
                        {
                            int soulshotCount = 0;
                            final L2Summon pet = activeChar.getPet();
                            if (pet != null)
                            {
                                soulshotCount += pet.getSoulShotsPerHit();
                            }
                            for (L2Summon servitor : activeChar.getServitors().values())
                            {
                                soulshotCount += servitor.getSoulShotsPerHit();
                            }
                            if (soulshotCount > item.getCount())
                            {
                                client.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR);
                                return;
                            }
                        }
                        else if (isSpiritshot)
                        {
                            int spiritshotCount = 0;
                            final L2Summon pet = activeChar.getPet();
                            if (pet != null)
                            {
                                spiritshotCount += pet.getSpiritShotsPerHit();
                            }
                            for (L2Summon servitor : activeChar.getServitors().values())
                            {
                                spiritshotCount += servitor.getSpiritShotsPerHit();
                            }
                            if (spiritshotCount > item.getCount())
                            {
                                client.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR);
                                return;
                            }
                        }

                        // Activate shots
                        activeChar.addAutoSoulShot(_itemId);
                        client.sendPacket(new ExAutoSoulShot(_itemId, _enable, _type));

                        // Recharge summon's shots
                        final L2Summon pet = activeChar.getPet();
                        if (pet != null)
                        {
                            // Send message
                            if (!pet.isChargedShot(item.getItem().getDefaultAction() == ActionType.SUMMON_SOULSHOT ? ShotType.SOULSHOTS : ((item.getId() == 6647) || (item.getId() == 20334)) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS))
                            {
                                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED);
                                sm.addItemName(item);
                                client.sendPacket(sm);
                            }
                            // Charge
                            pet.rechargeShots(isSoulshot, isSpiritshot, false);
                        }
                        for (L2Summon summon : activeChar.getServitors().values())
                        {
                            // Send message
                            if (!summon.isChargedShot(item.getItem().getDefaultAction() == ActionType.SUMMON_SOULSHOT ? ShotType.SOULSHOTS : ((item.getId() == 6647) || (item.getId() == 20334)) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS))
                            {
                                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED);
                                sm.addItemName(item);
                                client.sendPacket(sm);
                            }
                            // Charge
                            summon.rechargeShots(isSoulshot, isSpiritshot, false);
                        }
                    }
                    else
                    {
                        client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR_AND_THEREFORE_CANNOT_USE_THE_AUTOMATIC_USE_FUNCTION);
                    }
                }
                else if (isPlayerShot(item.getItem()))
                {
                    final boolean isSoulshot = item.getEtcItem().getDefaultAction() == ActionType.SOULSHOT;
                    final boolean isSpiritshot = item.getEtcItem().getDefaultAction() == ActionType.SPIRITSHOT;
                    final boolean isFishingshot = item.getEtcItem().getDefaultAction() == ActionType.FISHINGSHOT;
                    if ((activeChar.getActiveWeaponItem() == activeChar.getFistsWeaponItem()) || (item.getItem().getCrystalType() != activeChar.getActiveWeaponItem().getCrystalTypePlus()))
                    {
                        client.sendPacket(isSoulshot ? SystemMessageId.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON : SystemMessageId.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPON_S_GRADE);
                        return;
                    }

                    // Activate shots
                    activeChar.addAutoSoulShot(_itemId);
                    client.sendPacket(new ExAutoSoulShot(_itemId, _enable, _type));

                    // Send message
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED);
                    sm.addItemName(item);
                    client.sendPacket(sm);

                    // Recharge player's shots
                    activeChar.rechargeShots(isSoulshot, isSpiritshot, isFishingshot);
                }
            }
            else
            {
                // Cancel auto shots
                activeChar.removeAutoSoulShot(_itemId);
                client.sendPacket(new ExAutoSoulShot(_itemId, _enable, _type));

                // Send message
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED);
                sm.addItemName(item);
                client.sendPacket(sm);
            }
        }
    }

    public static boolean isPlayerShot(L2Item item)
    {
        switch (item.getDefaultAction())
        {
            case SPIRITSHOT:
            case SOULSHOT:
            case FISHINGSHOT:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    public static boolean isSummonShot(L2Item item)
    {
        switch (item.getDefaultAction())
        {
            case SUMMON_SPIRITSHOT:
            case SUMMON_SOULSHOT:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }
    }
}
