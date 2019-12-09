package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public final class Action extends ClientPacket {

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
    public void readImpl() {
        _objectId = readInt(); // Target object Identifier
        _originX = readInt();
        _originY = readInt();
        _originZ = readInt();
        _actionId = readByte(); // Action identifier : 0-Simple click, 1-Shift click
    }

    @Override
    public void runImpl() {
        // Get the current Player of the player
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (player.inObserverMode()) {
            player.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-4)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        final WorldObject obj;
        if (player.getTargetId() == _objectId) {
            obj = player.getTarget();
        } else if (player.isInAirShip() && (player.getAirShip().getHelmObjectId() == _objectId)) {
            obj = player.getAirShip();
        } else {
            obj = World.getInstance().findObject(_objectId);
        }

        // If object requested does not exist, add warn msg into logs
        if (obj == null) {
            // pressing e.g. pickup many times quickly would get you here
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((!obj.isTargetable() || player.isTargetingDisabled()) && !player.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Players can't interact with objects in the other instances
        if (obj.getInstanceWorld() != player.getInstanceWorld()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Only GMs can directly interact with invisible characters
        if (!obj.isVisibleFor(player)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Check if the target is valid, if the player haven't a shop or isn't the requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...)
        if (player.getActiveRequester() != null) {
            // Actions prohibited when in trade
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.onActionRequest();

        switch (_actionId) {
            case 0 -> obj.onAction(player);
            case 1 -> {
                if (!player.isGM() && (!(isNpc(obj) && Config.ALT_GAME_VIEWNPC))) {
                    obj.onAction(player, false);
                } else {
                    obj.onActionShift(player);
                }
            }
            default -> {
                // Invalid action detected (probably client cheating), log this
                LOGGER.warn("Character: {} requested invalid action: {}", player.getName(), _actionId);
                client.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }
    }
}
