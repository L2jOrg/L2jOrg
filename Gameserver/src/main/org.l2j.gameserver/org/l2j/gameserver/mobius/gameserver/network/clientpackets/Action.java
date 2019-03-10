package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.PcCondOverride;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.mobius.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public final class Action extends IClientIncomingPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);

    private int _objectId;
    @SuppressWarnings("unused")
    private int _originX;
    @SuppressWarnings("unused")
    private int _originY;
    @SuppressWarnings("unused")
    private int _originZ;
    private int _actionId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt(); // Target object Identifier
        _originX = packet.getInt();
        _originY = packet.getInt();
        _originZ = packet.getInt();
        _actionId = packet.get(); // Action identifier : 0-Simple click, 1-Shift click
    }

    @Override
    public void runImpl() {
        // Get the current L2PcInstance of the player
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.inObserverMode()) {
            activeChar.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final BuffInfo info = activeChar.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-4)) {
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        final L2Object obj;
        if (activeChar.getTargetId() == _objectId) {
            obj = activeChar.getTarget();
        } else if (activeChar.isInAirShip() && (activeChar.getAirShip().getHelmObjectId() == _objectId)) {
            obj = activeChar.getAirShip();
        } else {
            obj = L2World.getInstance().findObject(_objectId);
        }

        // If object requested does not exist, add warn msg into logs
        if (obj == null) {
            // pressing e.g. pickup many times quickly would get you here
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((!obj.isTargetable() || activeChar.isTargetingDisabled()) && !activeChar.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Players can't interact with objects in the other instances
        if (obj.getInstanceWorld() != activeChar.getInstanceWorld()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Only GMs can directly interact with invisible characters
        if (!obj.isVisibleFor(activeChar)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Check if the target is valid, if the player haven't a shop or isn't the requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...)
        if (activeChar.getActiveRequester() != null) {
            // Actions prohibited when in trade
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        switch (_actionId) {
            case 0: {
                obj.onAction(activeChar);
                break;
            }
            case 1: {
                if (!activeChar.isGM() && (!(obj.isNpc() && Config.ALT_GAME_VIEWNPC) || obj.isFakePlayer())) {
                    obj.onAction(activeChar, false);
                } else {
                    obj.onActionShift(activeChar);
                }
                break;
            }
            default: {
                // Invalid action detected (probably client cheating), log this
                LOGGER.warn("Character: {} requested invalid action: {}", activeChar.getName(), _actionId);
                client.sendPacket(ActionFailed.STATIC_PACKET);
                break;
            }
        }
    }
}
