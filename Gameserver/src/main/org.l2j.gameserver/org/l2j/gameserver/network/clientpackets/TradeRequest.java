package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.datatables.BotReportTable;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SendTradeRequest;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * This packet manages the trade request.
 */
public final class TradeRequest extends IClientIncomingPacket {
    private int _objectId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
    }

    private void scheduleDeny(L2PcInstance player, String name) {
        if (player != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE);
            sm.addString(name);
            player.sendPacket(sm);
            player.onTransactionResponse();
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
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
                if (!effect.checkCondition(BotReportTable.TRADE_ACTION_BLOCK_ID)) {
                    client.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        final L2Object target = L2World.getInstance().findObject(_objectId);
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

        if (FakePlayerData.getInstance().isTalkable(target.getName())) {
            final String name = FakePlayerData.getInstance().getProperName(target.getName());
            boolean npcInRange = false;
            for (L2Npc npc : L2World.getInstance().getVisibleObjectsInRange(player, L2Npc.class, 150)) {
                if (npc.getName().equals(name)) {
                    npcInRange = true;
                }
            }
            if (!npcInRange) {
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_TARGET_IS_OUT_OF_RANGE));
                return;
            }
            if (!player.isProcessingRequest()) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1);
                sm.addString(name);
                player.sendPacket(sm);
                ThreadPoolManager.getInstance().schedule(() -> scheduleDeny(player, name), 10000);
                player.blockRequest();
            } else {
                player.sendPacket(SystemMessageId.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
            }
            return;
        }

        if (!target.isPlayer()) {
            client.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        final L2PcInstance partner = target.getActingPlayer();
        if (partner.isInOlympiadMode() || player.isInOlympiadMode()) {
            player.sendMessage("A user currently participating in the Olympiad cannot accept or request a trade.");
            return;
        }

        info = partner.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(BotReportTable.TRADE_ACTION_BLOCK_ID)) {
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

        if (player.calculateDistance3D(partner) > 150) {
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
