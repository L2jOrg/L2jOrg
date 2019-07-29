package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketBuffer;
import org.l2j.gameserver.network.clientpackets.*;
import org.l2j.gameserver.network.clientpackets.friend.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static java.lang.Short.toUnsignedInt;
import static java.util.Objects.requireNonNullElse;

/**
 * @author UnAfraid
 */
public enum IncomingPackets implements PacketFactory {
    LOGOUT(0x00, Logout::new, ConnectionState.AUTHENTICATED, ConnectionState.IN_GAME),
    ATTACK(0x01, Attack::new, ConnectionState.IN_GAME),
    REQUEST_START_PLEDGE_WAR(0x03, RequestStartPledgeWar::new, ConnectionState.IN_GAME),
    REQUEST_REPLY_START_PLEDGE(0x04, RequestReplyStartPledgeWar::new, ConnectionState.IN_GAME),
    REQUEST_STOP_PLEDGE_WAR(0x05, RequestStopPledgeWar::new, ConnectionState.IN_GAME),
    REQUEST_REPLY_STOP_PLEDGE_WAR(0x06, RequestReplyStopPledgeWar::new, ConnectionState.IN_GAME),
    REQUEST_SURRENDER_PLEDGE_WAR(0x07, RequestSurrenderPledgeWar::new, ConnectionState.IN_GAME),
    REQUEST_REPLY_SURRENDER_PLEDGE_WAR(0x08, RequestReplySurrenderPledgeWar::new, ConnectionState.IN_GAME),
    REQUEST_SET_PLEDGE_CREST(0x09, RequestSetPledgeCrest::new, ConnectionState.IN_GAME),
    REQUEST_GIVE_NICK_NAME(0x0B, RequestGiveNickName::new, ConnectionState.IN_GAME),
    CHARACTER_CREATE(0x0C, CharacterCreate::new, ConnectionState.AUTHENTICATED),
    CHARACTER_DELETE(0x0D, CharacterDelete::new, ConnectionState.AUTHENTICATED),
    PROTOCOL_VERSION(0x0E, ProtocolVersion::new, ConnectionState.CONNECTED),
    MOVE_BACKWARD_TO_LOCATION(0x0F, MoveBackwardToLocation::new, ConnectionState.IN_GAME),
    ENTER_WORLD(0x11, EnterWorld::new, ConnectionState.JOINING_GAME),
    CHARACTER_SELECT(0x12, CharacterSelect::new, ConnectionState.AUTHENTICATED),
    NEW_CHARACTER(0x13, NewCharacter::new, ConnectionState.AUTHENTICATED),
    REQUEST_ITEM_LIST(0x14, RequestItemList::new, ConnectionState.IN_GAME),
    REQUEST_UN_EQUIP_ITEM(0x16, RequestUnEquipItem::new, ConnectionState.IN_GAME),
    REQUEST_DROP_ITEM(0x17, RequestDropItem::new, ConnectionState.IN_GAME),
    USE_ITEM(0x19, UseItem::new, ConnectionState.IN_GAME),
    TRADE_REQUEST(0x1A, TradeRequest::new, ConnectionState.IN_GAME),
    ADD_TRADE_ITEM(0x1B, AddTradeItem::new, ConnectionState.IN_GAME),
    TRADE_DONE(0x1C, TradeDone::new, ConnectionState.IN_GAME),
    ACTION(0x1F, Action::new, ConnectionState.IN_GAME),
    REQUEST_LINK_HTML(0x22, RequestLinkHtml::new, ConnectionState.IN_GAME),
    REQUEST_BYPASS_TO_SERVER(0x23, RequestBypassToServer::new, ConnectionState.IN_GAME),
    REQUEST_BBS_WRITE(0x24, RequestBBSwrite::new, ConnectionState.IN_GAME),
    REQUEST_JOIN_PLEDGE(0x26, RequestJoinPledge::new, ConnectionState.IN_GAME),
    REQUEST_ANSWER_JOIN_PLEDGE(0x27, RequestAnswerJoinPledge::new, ConnectionState.IN_GAME),
    REQUEST_WITHDRAWAL_PLEDGE(0x28, RequestWithdrawalPledge::new, ConnectionState.IN_GAME),
    REQUEST_OUST_PLEDGE_MEMBER(0x29, RequestOustPledgeMember::new, ConnectionState.IN_GAME),
    AUTH_LOGIN(0x2B, AuthLogin::new, ConnectionState.CONNECTED),
    REQUEST_GET_ITEM_FROM_PET(0x2C, RequestGetItemFromPet::new, ConnectionState.IN_GAME),
    REQUEST_ALLY_INFO(0x2E, RequestAllyInfo::new, ConnectionState.IN_GAME),
    REQUEST_CRYSTALLIZE_ITEM(0x2F, RequestCrystallizeItem::new, ConnectionState.IN_GAME),
    REQUEST_PRIVATE_STORE_MANAGE_SELL(0x30, RequestPrivateStoreManageSell::new, ConnectionState.IN_GAME),
    SET_PRIVATE_STORE_LIST_SELL(0x31, SetPrivateStoreListSell::new, ConnectionState.IN_GAME),
    ATTACK_REQUEST(0x32, AttackRequest::new, ConnectionState.IN_GAME),
    REQUEST_TELEPORT(0x33, null, ConnectionState.IN_GAME),
    SOCIAL_ACTION(0x34, null, ConnectionState.IN_GAME),
    CHANGE_MOVE_TYPE(0x35, null, ConnectionState.IN_GAME),
    CHANGE_WAIT_TYPE(0x36, null, ConnectionState.IN_GAME),
    REQUEST_SELL_ITEM(0x37, RequestSellItem::new, ConnectionState.IN_GAME),
    REQUEST_MAGIC_SKILL_LIST(0x38, RequestMagicSkillList::new, ConnectionState.IN_GAME),
    REQUEST_MAGIC_SKILL_USE(0x39, RequestMagicSkillUse::new, ConnectionState.IN_GAME),
    APPEARING(0x3A, Appearing::new, ConnectionState.IN_GAME),
    SEND_WARE_HOUSE_DEPOSIT_LIST(0x3B, SendWareHouseDepositList::new, ConnectionState.IN_GAME),
    SEND_WARE_HOUSE_WITH_DRAW_LIST(0x3C, SendWareHouseWithDrawList::new, ConnectionState.IN_GAME),
    REQUEST_SHORT_CUT_REG(0x3D, RequestShortCutReg::new, ConnectionState.IN_GAME),
    REQUEST_SHORT_CUT_DEL(0x3F, RequestShortCutDel::new, ConnectionState.IN_GAME),
    REQUEST_BUY_ITEM(0x40, RequestBuyItem::new, ConnectionState.IN_GAME),
    REQUEST_JOIN_PARTY(0x42, RequestJoinParty::new, ConnectionState.IN_GAME),
    REQUEST_ANSWER_JOIN_PARTY(0x43, RequestAnswerJoinParty::new, ConnectionState.IN_GAME),
    REQUEST_WITH_DRAWAL_PARTY(0x44, RequestWithDrawalParty::new, ConnectionState.IN_GAME),
    REQUEST_OUST_PARTY_MEMBER(0x45, RequestOustPartyMember::new, ConnectionState.IN_GAME),
    CANNOT_MOVE_ANYMORE(0x47, CannotMoveAnymore::new, ConnectionState.IN_GAME),
    REQUEST_TARGET_CANCELD(0x48, RequestTargetCanceld::new, ConnectionState.IN_GAME),
    SAY2(0x49, Say2::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_MEMBER_LIST(0x4D, RequestPledgeMemberList::new, ConnectionState.IN_GAME),
    REQUEST_MAGIC_LIST(0x4F, null, ConnectionState.IN_GAME),
    REQUEST_SKILL_LIST(0x50, RequestSkillList::new, ConnectionState.IN_GAME),
    MOVE_WITH_DELTA(0x52, MoveWithDelta::new, ConnectionState.IN_GAME),
    REQUEST_GET_ON_VEHICLE(0x53, RequestGetOnVehicle::new, ConnectionState.IN_GAME),
    REQUEST_GET_OFF_VEHICLE(0x54, RequestGetOffVehicle::new, ConnectionState.IN_GAME),
    ANSWER_TRADE_REQUEST(0x55, AnswerTradeRequest::new, ConnectionState.IN_GAME),
    REQUEST_ACTION_USE(0x56, RequestActionUse::new, ConnectionState.IN_GAME),
    REQUEST_RESTART(0x57, RequestRestart::new, ConnectionState.IN_GAME),
    VALIDATE_POSITION(0x59, ValidatePosition::new, ConnectionState.IN_GAME),
    START_ROTATING(0x5B, StartRotating::new, ConnectionState.IN_GAME),
    FINISH_ROTATING(0x5C, FinishRotating::new, ConnectionState.IN_GAME),
    REQUEST_SHOW_BOARD(0x5E, RequestShowBoard::new, ConnectionState.IN_GAME),
    REQUEST_ENCHANT_ITEM(0x5F, RequestEnchantItem::new, ConnectionState.IN_GAME),
    REQUEST_DESTROY_ITEM(0x60, RequestDestroyItem::new, ConnectionState.IN_GAME),
    REQUEST_QUEST_LIST(0x62, RequestQuestList::new, ConnectionState.IN_GAME),
    REQUEST_QUEST_ABORT(0x63, RequestQuestAbort::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_INFO(0x65, RequestPledgeInfo::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_EXTENDED_INFO(0x66, RequestPledgeExtendedInfo::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_CREST(0x67, RequestPledgeCrest::new, ConnectionState.IN_GAME),
  //  REQUEST_FRIEND_LIST(0x6B, null, ConnectionState.IN_GAME), // TODO
    REQUEST_SEND_FRIEND_MSG(0x6B, RequestSendFriendMsg::new, ConnectionState.IN_GAME),
    REQUEST_SHOW_MINI_MAP(0x6C, RequestShowMiniMap::new, ConnectionState.IN_GAME),
    REQUEST_RECORD_INFO(0x6E, RequestRecordInfo::new, ConnectionState.IN_GAME),
    REQUEST_HENNA_EQUIP(0x6F, RequestHennaEquip::new, ConnectionState.IN_GAME),
    REQUEST_HENNA_REMOVE_LIST(0x70, RequestHennaRemoveList::new, ConnectionState.IN_GAME),
    REQUEST_HENNA_ITEM_REMOVE_INFO(0x71, RequestHennaItemRemoveInfo::new, ConnectionState.IN_GAME),
    REQUEST_HENNA_REMOVE(0x72, RequestHennaRemove::new, ConnectionState.IN_GAME),
    REQUEST_ACQUIRE_SKILL_INFO(0x73, RequestAcquireSkillInfo::new, ConnectionState.IN_GAME),
    SEND_BYPASS_BUILD_CMD(0x74, SendBypassBuildCmd::new, ConnectionState.IN_GAME),
    REQUEST_MOVE_TO_LOCATION_IN_VEHICLE(0x75, RequestMoveToLocationInVehicle::new, ConnectionState.IN_GAME),
    CANNOT_MOVE_ANYMORE_IN_VEHICLE(0x76, CannotMoveAnymoreInVehicle::new, ConnectionState.IN_GAME),
    REQUEST_FRIEND_INVITE(0x77, RequestFriendInvite::new, ConnectionState.IN_GAME),
    REQUEST_ANSWER_FRIEND_INVITE(0x78, RequestAnswerFriendInvite::new, ConnectionState.IN_GAME),
    REQUEST_FRIEND_LIST(0x79, RequestFriendList::new, ConnectionState.IN_GAME),
    REQUEST_FRIEND_DEL(0x7A, RequestFriendDel::new, ConnectionState.IN_GAME),
    CHARACTER_RESTORE(0x7B, CharacterRestore::new, ConnectionState.AUTHENTICATED),
    REQUEST_ACQUIRE_SKILL(0x7C, RequestAcquireSkill::new, ConnectionState.IN_GAME),
    REQUEST_RESTART_POINT(0x7D, RequestRestartPoint::new, ConnectionState.IN_GAME),
    REQUEST_GM_COMMAND(0x7E, RequestGMCommand::new, ConnectionState.IN_GAME),
    REQUEST_PARTY_MATCH_CONFIG(0x7F, RequestPartyMatchConfig::new, ConnectionState.IN_GAME),
    REQUEST_PARTY_MATCH_LIST(0x80, RequestPartyMatchList::new, ConnectionState.IN_GAME),
    REQUEST_PARTY_MATCH_DETAIL(0x81, RequestPartyMatchDetail::new, ConnectionState.IN_GAME),
    REQUEST_PRIVATE_STORE_BUY(0x83, RequestPrivateStoreBuy::new, ConnectionState.IN_GAME),
    REQUEST_TUTORIAL_LINK_HTML(0x85, RequestTutorialLinkHtml::new, ConnectionState.IN_GAME),
    REQUEST_TUTORIAL_PASS_CMD_TO_SERVER(0x86, RequestTutorialPassCmdToServer::new, ConnectionState.IN_GAME),
    REQUEST_TUTORIAL_QUESTION_MARK(0x87, RequestTutorialQuestionMark::new, ConnectionState.IN_GAME),
    REQUEST_TUTORIAL_CLIENT_EVENT(0x88, RequestTutorialClientEvent::new, ConnectionState.IN_GAME),
    REQUEST_PETITION(0x89, RequestPetition::new, ConnectionState.IN_GAME),
    REQUEST_PETITION_CANCEL(0x8A, RequestPetitionCancel::new, ConnectionState.IN_GAME),
    REQUEST_GM_LIST(0x8B, RequestGmList::new, ConnectionState.IN_GAME),
    REQUEST_JOIN_ALLY(0x8C, RequestJoinAlly::new, ConnectionState.IN_GAME),
    REQUEST_ANSWER_JOIN_ALLY(0x8D, RequestAnswerJoinAlly::new, ConnectionState.IN_GAME),
    ALLY_LEAVE(0x8E, AllyLeave::new, ConnectionState.IN_GAME),
    ALLY_DISMISS(0x8F, AllyDismiss::new, ConnectionState.IN_GAME),
    REQUEST_DISMISS_ALLY(0x90, RequestDismissAlly::new, ConnectionState.IN_GAME),
    REQUEST_SET_ALLY_CREST(0x91, RequestSetAllyCrest::new, ConnectionState.IN_GAME),
    REQUEST_ALLY_CREST(0x92, RequestAllyCrest::new, ConnectionState.IN_GAME),
    REQUEST_CHANGE_PET_NAME(0x93, RequestChangePetName::new, ConnectionState.IN_GAME),
    REQUEST_PET_USE_ITEM(0x94, RequestPetUseItem::new, ConnectionState.IN_GAME),
    REQUEST_GIVE_ITEM_TO_PET(0x95, RequestGiveItemToPet::new, ConnectionState.IN_GAME),
    REQUEST_PRIVATE_STORE_QUIT_SELL(0x96, RequestPrivateStoreQuitSell::new, ConnectionState.IN_GAME),
    SET_PRIVATE_STORE_MSG_SELL(0x97, SetPrivateStoreMsgSell::new, ConnectionState.IN_GAME),
    REQUEST_PET_GET_ITEM(0x98, RequestPetGetItem::new, ConnectionState.IN_GAME),
    REQUEST_PRIVATE_STORE_MANAGE_BUY(0x99, RequestPrivateStoreManageBuy::new, ConnectionState.IN_GAME),
    SET_PRIVATE_STORE_LIST_BUY(0x9A, SetPrivateStoreListBuy::new, ConnectionState.IN_GAME),
    REQUEST_PRIVATE_STORE_QUIT_BUY(0x9C, RequestPrivateStoreQuitBuy::new, ConnectionState.IN_GAME),
    SET_PRIVATE_STORE_MSG_BUY(0x9D, SetPrivateStoreMsgBuy::new, ConnectionState.IN_GAME),
    REQUEST_PRIVATE_STORE_SELL(0x9F, RequestPrivateStoreSell::new, ConnectionState.IN_GAME),
    SEND_TIME_CHECK_PACKET(0xA0, null, ConnectionState.IN_GAME),
    REQUEST_SKILL_COOL_TIME(0xA6, RequestSkillCoolTime::new, ConnectionState.JOINING_GAME, ConnectionState.IN_GAME),
    REQUEST_PACKAGE_SENDABLE_ITEM_LIST(0xA7, RequestPackageSendableItemList::new, ConnectionState.IN_GAME),
    REQUEST_PACKAGE_SEND(0xA8, RequestPackageSend::new, ConnectionState.IN_GAME),
    REQUEST_BLOCK(0xA9, RequestBlock::new, ConnectionState.IN_GAME),
    REQUEST_SIEGE_INFO(0xAA, RequestSiegeInfo::new, ConnectionState.IN_GAME),
    REQUEST_SIEGE_ATTACKER_LIST(0xAB, RequestSiegeAttackerList::new, ConnectionState.IN_GAME),
    REQUEST_SIEGE_DEFENDER_LIST(0xAC, RequestSiegeDefenderList::new, ConnectionState.IN_GAME),
    REQUEST_JOIN_SIEGE(0xAD, RequestJoinSiege::new, ConnectionState.IN_GAME),
    REQUEST_CONFIRM_SIEGE_WAITING_LIST(0xAE, RequestConfirmSiegeWaitingList::new, ConnectionState.IN_GAME),
    REQUEST_SET_CASTLE_SIEGE_TIME(0xAF, RequestSetCastleSiegeTime::new, ConnectionState.IN_GAME),
    MULTI_SELL_CHOOSE(0xB0, MultiSellChoose::new, ConnectionState.IN_GAME),
    NET_PING(0xB1, null, ConnectionState.IN_GAME),
    REQUEST_REMAIN_TIME(0xB2, null, ConnectionState.IN_GAME),
    BYPASS_USER_CMD(0xB3, BypassUserCmd::new, ConnectionState.IN_GAME),
    SNOOP_QUIT(0xB4, SnoopQuit::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_BOOK_OPEN(0xB5, RequestRecipeBookOpen::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_BOOK_DESTROY(0xB6, RequestRecipeBookDestroy::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_ITEM_MAKE_INFO(0xB7, RequestRecipeItemMakeInfo::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_ITEM_MAKE_SELF(0xB8, RequestRecipeItemMakeSelf::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_MANAGE_LIST(0xB9, null, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_MESSAGE_SET(0xBA, RequestRecipeShopMessageSet::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_LIST_SET(0xBB, RequestRecipeShopListSet::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_MANAGE_QUIT(0xBC, RequestRecipeShopManageQuit::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_MANAGE_CANCEL(0xBD, null, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_MAKE_INFO(0xBE, RequestRecipeShopMakeInfo::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_MAKE_ITEM(0xBF, RequestRecipeShopMakeItem::new, ConnectionState.IN_GAME),
    REQUEST_RECIPE_SHOP_MANAGE_PREV(0xC0, RequestRecipeShopManagePrev::new, ConnectionState.IN_GAME),
    OBSERVER_RETURN(0xC1, ObserverReturn::new, ConnectionState.IN_GAME),
    REQUEST_EVALUATE(0xC2, null, ConnectionState.IN_GAME),
    REQUEST_HENNA_ITEM_LIST(0xC3, RequestHennaItemList::new, ConnectionState.IN_GAME),
    REQUEST_HENNA_ITEM_INFO(0xC4, RequestHennaItemInfo::new, ConnectionState.IN_GAME),
    REQUEST_BUY_SEED(0xC5, RequestBuySeed::new, ConnectionState.IN_GAME),
    DLG_ANSWER(0xC6, DlgAnswer::new, ConnectionState.IN_GAME),
    REQUEST_PREVIEW_ITEM(0xC7, RequestPreviewItem::new, ConnectionState.IN_GAME),
    REQUEST_SSQ_STATUS(0xC8, null, ConnectionState.IN_GAME),
    REQUEST_PETITION_FEEDBACK(0xC9, RequestPetitionFeedback::new, ConnectionState.IN_GAME),
    GAME_GUARD_REPLY(0xCB, GameGuardReply::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_POWER(0xCC, RequestPledgePower::new, ConnectionState.IN_GAME),
    REQUEST_MAKE_MACRO(0xCD, RequestMakeMacro::new, ConnectionState.IN_GAME),
    REQUEST_DELETE_MACRO(0xCE, RequestDeleteMacro::new, ConnectionState.IN_GAME),
    REQUEST_BUY_PROCURE(0xCF, null, ConnectionState.IN_GAME),
    EX_PACKET(0xD0, null, true, ConnectionState.values()); // This packet has its own connection state checking so we allow all of them

    public static final IncomingPackets[] PACKET_ARRAY;

    static {
        final short maxPacketId = (short) Arrays.stream(values()).mapToInt(IncomingPackets::getPacketId).max().orElse(0);
        PACKET_ARRAY = new IncomingPackets[maxPacketId + 1];
        for (IncomingPackets incomingPacket : values()) {
            PACKET_ARRAY[incomingPacket.getPacketId()] = incomingPacket;
        }
    }

    private short _packetId;
    private Supplier<ClientPacket> _incomingPacketFactory;
    private Set<ConnectionState> _connectionStates;
    private boolean hasExtension;

    IncomingPackets(int packetId, Supplier<ClientPacket> incomingPacketFactory, boolean hasExtension, ConnectionState... connectionStates) {
        // packetId is an unsigned byte
        if (packetId > 0xFF) {
            throw new IllegalArgumentException("packetId must not be bigger than 0xFF");
        }

        _packetId = (short) packetId;
        _incomingPacketFactory = incomingPacketFactory != null ? incomingPacketFactory : NULL_PACKET_SUPLIER;
        _connectionStates = new HashSet<>(Arrays.asList(connectionStates));
        this.hasExtension = hasExtension;
    }

    IncomingPackets(int packetId, Supplier<ClientPacket> incomingPacketFactory, ConnectionState... connectionStates) {
        this(packetId, incomingPacketFactory, false, connectionStates);
    }

    @Override
    public int getPacketId() {
        return _packetId;
    }

    @Override
    public ClientPacket newIncomingPacket() {
        return _incomingPacketFactory.get();
    }

    @Override
    public Set<ConnectionState> getConnectionStates() {
        return _connectionStates;
    }

    @Override
    public boolean canHandleState(ConnectionState connectionState) {
        return _connectionStates.contains(connectionState);
    }

    @Override
    public boolean hasExtension() {
        return hasExtension;
    }

    @Override
    public PacketFactory handleExtension(PacketBuffer buffer) {
        var exPacketId = toUnsignedInt(buffer.readShort());
        if (exPacketId >= ExIncomingPackets.PACKET_ARRAY.length) {
            return NULLABLE_PACKET_FACTORY;
        }

        return requireNonNullElse(ExIncomingPackets.PACKET_ARRAY[exPacketId], NULLABLE_PACKET_FACTORY);
    }

}
