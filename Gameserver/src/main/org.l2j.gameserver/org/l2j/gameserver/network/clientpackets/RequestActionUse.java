package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.handler.PlayerActionHandler;
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

import static java.util.Objects.isNull;

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
        var player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        // Don't do anything if player is dead or confused
        if ((player.isFakeDeath() && (_actionId != 0)) || player.isDead() || player.isControlBlocked()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(_actionId)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        final int[] allowedActions = player.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
        if (!(Arrays.binarySearch(allowedActions, _actionId) >= 0)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            LOGGER.warn("Player " + player + " used action which he does not have! Id = " + _actionId + " transform: " + player.getTransformation().orElse(null));
            return;
        }

        final ActionData actionHolder = ActionManager.getInstance().getActionData(_actionId);
        if (actionHolder != null) {
            final IPlayerActionHandler actionHandler = PlayerActionHandler.getInstance().getHandler(actionHolder.getHandler());
            if (actionHandler != null) {
                actionHandler.useAction(player, actionHolder, _ctrlPressed, _shiftPressed);
                return;
            }
            LOGGER.warn("Couldnt find handler with name: " + actionHolder.getHandler());
            return;
        }

        switch (_actionId) {
            case 51: // General Manufacture
            {
                // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
                if (player.isAlikeDead()) {
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }

                if (player.isSellingBuffs()) {
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }

                if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
                    player.setPrivateStoreType(PrivateStoreType.NONE);
                    player.broadcastUserInfo();
                }
                if (player.isSitting()) {
                    player.standUp();
                }

                client.sendPacket(new RecipeShopManageList(player, false));
                break;
            }
            default: {
                LOGGER.warn(player.getName() + ": unhandled action type " + _actionId);
                break;
            }
        }
    }
}
