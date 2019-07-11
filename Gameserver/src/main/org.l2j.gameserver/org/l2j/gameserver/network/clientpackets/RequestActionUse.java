package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.ActionData;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.model.ActionDataHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.RecipeShopManageList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * This class manages the action use request packet.
 *
 * @author Zoey76
 */
public final class RequestActionUse extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestActionUse.class);

    private int _actionId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    @Override
    public void readImpl() {
        _actionId = readInt();
        _ctrlPressed = (readInt() == 1);
        _shiftPressed = (readByte() == 1);
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        // Don't do anything if player is dead or confused
        if ((activeChar.isFakeDeath() && (_actionId != 0)) || activeChar.isDead() || activeChar.isControlBlocked()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final BuffInfo info = activeChar.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(_actionId)) {
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        final int[] allowedActions = activeChar.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
        if (!(Arrays.binarySearch(allowedActions, _actionId) >= 0)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            LOGGER.warn("Player " + activeChar + " used action which he does not have! Id = " + _actionId + " transform: " + activeChar.getTransformation().orElse(null));
            return;
        }

        final ActionDataHolder actionHolder = ActionData.getInstance().getActionData(_actionId);
        if (actionHolder != null) {
            final IPlayerActionHandler actionHandler = PlayerActionHandler.getInstance().getHandler(actionHolder.getHandler());
            if (actionHandler != null) {
                actionHandler.useAction(activeChar, actionHolder, _ctrlPressed, _shiftPressed);
                return;
            }
            LOGGER.warn("Couldnt find handler with name: " + actionHolder.getHandler());
            return;
        }

        switch (_actionId) {
            case 51: // General Manufacture
            {
                // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
                if (activeChar.isAlikeDead()) {
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }

                if (activeChar.isSellingBuffs()) {
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }

                if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
                    activeChar.setPrivateStoreType(PrivateStoreType.NONE);
                    activeChar.broadcastUserInfo();
                }
                if (activeChar.isSitting()) {
                    activeChar.standUp();
                }

                client.sendPacket(new RecipeShopManageList(activeChar, false));
                break;
            }
            default: {
                LOGGER.warn(activeChar.getName() + ": unhandled action type " + _actionId);
                break;
            }
        }
    }
}
