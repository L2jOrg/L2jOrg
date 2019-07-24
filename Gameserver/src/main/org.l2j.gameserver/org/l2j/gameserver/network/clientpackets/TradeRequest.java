package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SendTradeRequest;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

/**
 * This packet manages the trade request.
 */
public final class TradeRequest extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    private void scheduleDeny(Player player, String name) {
        if (player != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE);
            sm.addString(name);
            player.sendPacket(sm);
            player.onTransactionResponse();
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your current Access Level.");
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(ReportTable.TRADE_ACTION_BLOCK_ID)) {
                    client.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        final WorldObject target = World.getInstance().findObject(_objectId);
        // If there is no target, target is far away or
        // they are in different instances
        // trade request is ignored and there is no system message.
        if ((target == null) || !player.isInSurroundingRegion(target) || (target.getInstanceWorld() != player.getInstanceWorld())) {
            return;
        }

        // If target and acting player are the same, trade request is ignored
        // and the following system message is sent to acting player.
        if (target.getObjectId() == player.getObjectId()) {
            client.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (!isPlayer(target)) {
            client.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        final Player partner = target.getActingPlayer();
        if (partner.isInOlympiadMode() || player.isInOlympiadMode()) {
            player.sendMessage("A user currently participating in the Olympiad cannot accept or request a trade.");
            return;
        }

        info = partner.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(ReportTable.TRADE_ACTION_BLOCK_ID)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_IS_CURRENTLY_BEING_INVESTIGATED);
                    sm.addString(partner.getName());
                    client.sendPacket(sm);
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        // L2J Customs: Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getReputation() < 0)) {
            player.sendMessage("You cannot trade while you are in a chaotic state.");
            return;
        }

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (partner.getReputation() < 0)) {
            player.sendMessage("You cannot request a trade while your target is in a chaotic state.");
            return;
        }

        if (Config.JAIL_DISABLE_TRANSACTION && (player.isJailed() || partner.isJailed())) {
            player.sendMessage("You cannot trade while you are in in Jail.");
            return;
        }

        if ((player.getPrivateStoreType() != PrivateStoreType.NONE) || (partner.getPrivateStoreType() != PrivateStoreType.NONE)) {
            client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (player.isProcessingTransaction()) {
            client.sendPacket(SystemMessageId.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
            return;
        }

        SystemMessage sm;
        if (partner.isProcessingRequest() || partner.isProcessingTransaction()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            sm.addString(partner.getName());
            client.sendPacket(sm);
            return;
        }

        if (partner.getTradeRefusal()) {
            player.sendMessage("That person is in trade refusal mode.");
            return;
        }

        if (BlockList.isBlocked(partner, player)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
            sm.addString(partner.getName());
            client.sendPacket(sm);
            return;
        }

        if (!isInsideRadius2D(player, partner, 150)) {
            client.sendPacket(SystemMessageId.YOUR_TARGET_IS_OUT_OF_RANGE);
            return;
        }

        player.onTransactionRequest(partner);
        partner.sendPacket(new SendTradeRequest(player.getObjectId()));
        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1);
        sm.addString(partner.getName());
        client.sendPacket(sm);
    }
}
