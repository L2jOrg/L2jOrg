/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketBuffer;
import org.l2j.gameserver.network.clientpackets.*;
import org.l2j.gameserver.network.clientpackets.friend.*;

import java.util.EnumSet;
import java.util.function.Supplier;

import static java.lang.Short.toUnsignedInt;
import static java.util.Objects.requireNonNullElse;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public enum IncomingPackets implements PacketFactory {
    LOGOUT(Logout::new, ConnectionState.AUTHENTICATED_AND_IN_GAME),
    ATTACK(Attack::new, ConnectionState.IN_GAME_STATES),
    MOVE_BACKWARD_TO_LOCATION(null, ConnectionState.IN_GAME_STATES),
    START_PLEDGE_WAR(RequestStartPledgeWar::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_10(RequestReplyStartPledgeWar::new, ConnectionState.IN_GAME_STATES),
    STOP_PLEDGE_WAR(RequestStopPledgeWar::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_11(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_12(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_13(DISCARD, ConnectionState.IN_GAME_STATES),
    SET_PLEDGE_CREST(RequestSetPledgeCrest::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_14(DISCARD, ConnectionState.IN_GAME_STATES),
    GIVE_NICKNAME(RequestGiveNickName::new, ConnectionState.IN_GAME_STATES),
    CHARACTER_CREATE(CharacterCreate::new, ConnectionState.AUTHENTICATED_STATES),
    CHARACTER_DELETE(CharacterDelete::new, ConnectionState.AUTHENTICATED_STATES),
    VERSION(ProtocolVersion::new, ConnectionState.CONNECTED_STATES),
    MOVE_TO_LOCATION(MoveBackwardToLocation::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_34(DISCARD, ConnectionState.JOINING_GAME_STATES),
    ENTER_WORLD(EnterWorld::new, ConnectionState.JOINING_GAME_STATES),
    CHARACTER_SELECT(CharacterSelect::new, ConnectionState.AUTHENTICATED_STATES),
    NEW_CHARACTER(NewCharacter::new, ConnectionState.AUTHENTICATED_STATES),
    ITEMLIST(RequestItemList::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_1(DISCARD, ConnectionState.IN_GAME_STATES),
    UNEQUIP_ITEM(RequestUnEquipItem::new, ConnectionState.IN_GAME_STATES),
    DROP_ITEM(RequestDropItem::new, ConnectionState.IN_GAME_STATES),
    GET_ITEM(null, ConnectionState.IN_GAME_STATES),
    USE_ITEM(UseItem::new, ConnectionState.IN_GAME_STATES),
    TRADE_REQUEST(TradeRequest::new, ConnectionState.IN_GAME_STATES),
    TRADE_ADD(AddTradeItem::new, ConnectionState.IN_GAME_STATES),
    TRADE_DONE(TradeDone::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_35(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_36(DISCARD, ConnectionState.IN_GAME_STATES),
    ACTION(Action::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_37(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_38(DISCARD, ConnectionState.IN_GAME_STATES),
    LINK_HTML(RequestLinkHtml::new, ConnectionState.IN_GAME_STATES),
    PASS_CMD_TO_SERVER(RequestBypassToServer::new, ConnectionState.IN_GAME_STATES),
    WRITE_BBS(RequestBBSwrite::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_39(DISCARD, ConnectionState.IN_GAME_STATES),
    JOIN_PLEDGE(RequestJoinPledge::new, ConnectionState.IN_GAME_STATES),
    ANSWER_JOIN_PLEDGE(RequestAnswerJoinPledge::new, ConnectionState.IN_GAME_STATES),
    WITHDRAWAL_PLEDGE(RequestWithdrawalPledge::new, ConnectionState.IN_GAME_STATES),
    OUST_PLEDGE_MEMBER(RequestOustPledgeMember::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_40(DISCARD, ConnectionState.IN_GAME_STATES),
    LOGIN(AuthLogin::new, ConnectionState.CONNECTED_STATES),
    GET_ITEM_FROM_PET(RequestGetItemFromPet::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_22(DISCARD, ConnectionState.IN_GAME_STATES),
    ALLIANCE_INFO(RequestAllyInfo::new, ConnectionState.IN_GAME_STATES),
    CRYSTALLIZE_ITEM(RequestCrystallizeItem::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_19(RequestPrivateStoreManageSell::new, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_LIST_SET(SetPrivateStoreListSell::new, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_MANAGE_CANCEL(null, ConnectionState.IN_GAME_STATES),
    NOT_USE_41(DISCARD, ConnectionState.IN_GAME_STATES),
    SOCIAL_ACTION(null, ConnectionState.IN_GAME_STATES),
    CHANGE_MOVE_TYPE(null, ConnectionState.IN_GAME_STATES),
    CHANGE_WAIT_TYPE(null, ConnectionState.IN_GAME_STATES),
    SELL_LIST(RequestSellItem::new, ConnectionState.IN_GAME_STATES),
    MAGIC_SKILL_LIST(RequestMagicSkillList::new, ConnectionState.IN_GAME_STATES),
    MAGIC_SKILL_USE(RequestMagicSkillUse::new, ConnectionState.IN_GAME_STATES),
    APPEARING(Appearing::new, ConnectionState.IN_GAME_STATES),
    WAREHOUSE_DEPOSIT_LIST(SendWareHouseDepositList::new, ConnectionState.IN_GAME_STATES),
    WAREHOUSE_WITHDRAW_LIST(SendWareHouseWithDrawList::new, ConnectionState.IN_GAME_STATES),
    SHORTCUT_REG(RequestShortCutReg::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_3(DISCARD, ConnectionState.IN_GAME_STATES),
    DEL_SHORTCUT(RequestShortCutDel::new, ConnectionState.IN_GAME_STATES),
    BUY_LIST(RequestBuyItem::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_2(DISCARD, ConnectionState.IN_GAME_STATES),
    JOIN_PARTY(RequestJoinParty::new, ConnectionState.IN_GAME_STATES),
    ANSWER_JOIN_PARTY(RequestAnswerJoinParty::new, ConnectionState.IN_GAME_STATES),
    WITHDRAWAL_PARTY(RequestWithDrawalParty::new, ConnectionState.IN_GAME_STATES),
    OUST_PARTY_MEMBER(RequestOustPartyMember::new, ConnectionState.IN_GAME_STATES),
    DISMISS_PARTY(null, ConnectionState.IN_GAME_STATES),
    CAN_NOT_MOVE_ANYMORE(CannotMoveAnymore::new, ConnectionState.IN_GAME_STATES),
    TARGET_UNSELECTED(RequestTargetCanceld::new, ConnectionState.IN_GAME_STATES),
    SAY2(Say2::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_42(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_4(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_5(DISCARD, ConnectionState.IN_GAME_STATES),
    PLEDGE_REQ_SHOW_MEMBER_LIST_OPEN(RequestPledgeMemberList::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_6(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_7(DISCARD, ConnectionState.IN_GAME_STATES),
    SKILL_LIST(RequestSkillList::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_8(DISCARD, ConnectionState.IN_GAME_STATES),
    MOVE_WITH_DELTA(MoveWithDelta::new, ConnectionState.IN_GAME_STATES),
    GETON_VEHICLE(RequestGetOnVehicle::new, ConnectionState.IN_GAME_STATES),
    GETOFF_VEHICLE(RequestGetOffVehicle::new, ConnectionState.IN_GAME_STATES),
    TRADE_START(AnswerTradeRequest::new, ConnectionState.IN_GAME_STATES),
    ICON_ACTION(RequestActionUse::new, ConnectionState.IN_GAME_STATES),
    RESTART(RequestRestart::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_9(DISCARD, ConnectionState.IN_GAME_STATES),
    VALIDATE_POSITION(ValidatePosition::new, ConnectionState.IN_GAME_STATES),
    SEK_COSTUME(null, ConnectionState.IN_GAME_STATES),
    START_ROTATING(StartRotating::new, ConnectionState.IN_GAME_STATES),
    FINISH_ROTATING(FinishRotating::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_15(DISCARD, ConnectionState.IN_GAME_STATES),
    SHOW_BOARD(RequestShowBoard::new, ConnectionState.IN_GAME_STATES),
    REQUEST_ENCHANT_ITEM(RequestEnchantItem::new, ConnectionState.IN_GAME_STATES),
    DESTROY_ITEM(RequestDestroyItem::new, ConnectionState.IN_GAME_STATES),
    TARGET_USER_FROM_MENU(null, ConnectionState.IN_GAME_STATES),
    QUESTLIST(RequestQuestList::new, ConnectionState.IN_GAME_STATES),
    DESTROY_QUEST(RequestQuestAbort::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_16(DISCARD, ConnectionState.IN_GAME_STATES),
    PLEDGE_INFO(RequestPledgeInfo::new, ConnectionState.IN_GAME_STATES),
    PLEDGE_EXTENDED_INFO(RequestPledgeExtendedInfo::new, ConnectionState.IN_GAME_STATES),
    PLEDGE_CREST(RequestPledgeCrest::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_17(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_18(DISCARD, ConnectionState.IN_GAME_STATES),
    L2_FRIEND_LIST(PacketFactory.DISCARD, ConnectionState.IN_GAME_STATES), // discard this packet, the friend information is sent upon login no need to send all time since the information is keep on client
    L2_FRIEND_SAY(RequestSendFriendMsg::new, ConnectionState.IN_GAME_STATES),
    OPEN_MINIMAP(RequestShowMiniMap::new, ConnectionState.IN_GAME_STATES),
    MSN_CHAT_LOG(null, ConnectionState.IN_GAME_STATES),
    RELOAD(RequestRecordInfo::new, ConnectionState.IN_GAME_STATES),
    HENNA_EQUIP(RequestHennaEquip::new, ConnectionState.IN_GAME_STATES),
    HENNA_UNEQUIP_LIST(RequestHennaRemoveList::new, ConnectionState.IN_GAME_STATES),
    HENNA_UNEQUIP_INFO(RequestHennaItemRemoveInfo::new, ConnectionState.IN_GAME_STATES),
    HENNA_UNEQUIP(RequestHennaRemove::new, ConnectionState.IN_GAME_STATES),
    ACQUIRE_SKILL_INFO(RequestAcquireSkillInfo::new, ConnectionState.IN_GAME_STATES),
    SYS_CMD_2(SendBypassBuildCmd::new, ConnectionState.IN_GAME_STATES),
    MOVE_TO_LOCATION_IN_VEHICLE(RequestMoveToLocationInVehicle::new, ConnectionState.IN_GAME_STATES),
    CAN_NOT_MOVE_ANYMORE_IN_VEHICLE(CannotMoveAnymoreInVehicle::new, ConnectionState.IN_GAME_STATES),
    FRIEND_ADD_REQUEST(RequestFriendInvite::new, ConnectionState.IN_GAME_STATES),
    FRIEND_ADD_REPLY(RequestAnswerFriendInvite::new, ConnectionState.IN_GAME_STATES),
    FRIEND_LIST(RequestFriendList::new, ConnectionState.IN_GAME_STATES),
    FRIEND_REMOVE(RequestFriendDel::new, ConnectionState.IN_GAME_STATES),
    RESTORE_CHARACTER(CharacterRestore::new, ConnectionState.AUTHENTICATED_STATES),
    REQ_ACQUIRE_SKILL(RequestAcquireSkill::new, ConnectionState.IN_GAME_STATES),
    RESTART_POINT(RequestRestartPoint::new, ConnectionState.IN_GAME_STATES),
    GM_COMMAND_TYPE(RequestGMCommand::new, ConnectionState.IN_GAME_STATES),
    LIST_PARTY_WAITING(RequestPartyMatchConfig::new, ConnectionState.IN_GAME_STATES),
    MANAGE_PARTY_ROOM(RequestPartyMatchList::new, ConnectionState.IN_GAME_STATES),
    JOIN_PARTY_ROOM(RequestPartyMatchDetail::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_20(DISCARD, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_BUY_LIST_SEND(RequestPrivateStoreBuy::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_21(DISCARD, ConnectionState.IN_GAME_STATES),
    TUTORIAL_LINK_HTML(RequestTutorialLinkHtml::new, ConnectionState.IN_GAME_STATES),
    TUTORIAL_PASS_CMD_TO_SERVER(RequestTutorialPassCmdToServer::new, ConnectionState.IN_GAME_STATES),
    TUTORIAL_MARK_PRESSED(RequestTutorialQuestionMark::new, ConnectionState.IN_GAME_STATES),
    TUTORIAL_CLIENT_EVENT(RequestTutorialClientEvent::new, ConnectionState.IN_GAME_STATES),
    PETITION(RequestPetition::new, ConnectionState.IN_GAME_STATES),
    PETITION_CANCEL(RequestPetitionCancel::new, ConnectionState.IN_GAME_STATES),
    GMLIST(RequestGmList::new, ConnectionState.IN_GAME_STATES),
    JOIN_ALLIANCE(RequestJoinAlly::new, ConnectionState.IN_GAME_STATES),
    ANSWER_JOIN_ALLIANCE(RequestAnswerJoinAlly::new, ConnectionState.IN_GAME_STATES),
    WITHDRAW_ALLIANCE(AllyLeave::new, ConnectionState.IN_GAME_STATES),
    OUST_ALLIANCE_MEMBER_PLEDGE(AllyDismiss::new, ConnectionState.IN_GAME_STATES),
    DISMISS_ALLIANCE(RequestDismissAlly::new, ConnectionState.IN_GAME_STATES),
    SET_ALLIANCE_CREST(RequestSetAllyCrest::new, ConnectionState.IN_GAME_STATES),
    ALLIANCE_CREST(RequestAllyCrest::new, ConnectionState.IN_GAME_STATES),
    CHANGE_PET_NAME(RequestChangePetName::new, ConnectionState.IN_GAME_STATES),
    PET_USE_ITEM(RequestPetUseItem::new, ConnectionState.IN_GAME_STATES),
    GIVE_ITEM_TO_PET(RequestGiveItemToPet::new, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_QUIT(RequestPrivateStoreQuitSell::new, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_SET_MSG(SetPrivateStoreMsgSell::new, ConnectionState.IN_GAME_STATES),
    PET_GET_ITEM(RequestPetGetItem::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_23(DISCARD, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_BUY_LIST_SET(SetPrivateStoreListBuy::new, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_BUY_MANAGE_CANCEL(null, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_BUY_QUIT(RequestPrivateStoreQuitBuy::new, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_BUY_SET_MSG(SetPrivateStoreMsgBuy::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_24(SetPrivateStoreMsgBuy::new, ConnectionState.IN_GAME_STATES),
    PRIVATE_STORE_BUY_BUY_LIST_SEND(RequestPrivateStoreSell::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_25(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_26(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_27(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_28(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_29(DISCARD, ConnectionState.IN_GAME_STATES),
    NOT_USE_30(DISCARD, ConnectionState.IN_GAME_STATES),
    REQUEST_SKILL_COOL_TIME(RequestSkillCoolTime::new, ConnectionState.JOINING_GAME_AND_IN_GAME),
    REQUEST_PACKAGE_SENDABLE_ITEM_LIST(RequestPackageSendableItemList::new, ConnectionState.IN_GAME_STATES),
    REQUEST_PACKAGE_SEND(RequestPackageSend::new, ConnectionState.IN_GAME_STATES),
    BLOCK_PACKET(RequestBlock::new, ConnectionState.IN_GAME_STATES),
    CASTLE_SIEGE_INFO(RequestSiegeInfo::new, ConnectionState.IN_GAME_STATES),
    CASTLE_SIEGE_ATTACKER_LIST(RequestSiegeAttackerList::new, ConnectionState.IN_GAME_STATES),
    CASTLE_SIEGE_DEFENDER_LIST(RequestSiegeDefenderList::new, ConnectionState.IN_GAME_STATES),
    JOIN_CASTLE_SIEGE(RequestJoinSiege::new, ConnectionState.IN_GAME_STATES),
    CONFIRM_CASTLE_SIEGE_WAITING_LIST(RequestConfirmSiegeWaitingList::new, ConnectionState.IN_GAME_STATES),
    SET_CASTLE_SIEGE_TIME(RequestSetCastleSiegeTime::new, ConnectionState.IN_GAME_STATES),
    MULTI_SELL_CHOOSE(MultiSellChoose::new, ConnectionState.IN_GAME_STATES),
    NET_PING(null, ConnectionState.IN_GAME_STATES),
    REMAIN_TIME(null, ConnectionState.IN_GAME_STATES),
    USER_CMD_BYPASS(BypassUserCmd::new, ConnectionState.IN_GAME_STATES),
    SNOOP_QUIT(SnoopQuit::new, ConnectionState.IN_GAME_STATES),
    RECIPE_BOOK_OPEN(RequestRecipeBookOpen::new, ConnectionState.IN_GAME_STATES),
    RECIPE_ITEM_DELETE(RequestRecipeBookDestroy::new, ConnectionState.IN_GAME_STATES),
    RECIPE_ITEM_MAKE_INFO(RequestRecipeItemMakeInfo::new, ConnectionState.IN_GAME_STATES),
    RECIPE_ITEM_MAKE_SELF(RequestRecipeItemMakeSelf::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_31(DISCARD, ConnectionState.IN_GAME_STATES),
    RECIPE_SHOP_MESSAGE_SET(RequestRecipeShopMessageSet::new, ConnectionState.IN_GAME_STATES),
    RECIPE_SHOP_LIST_SET(RequestRecipeShopListSet::new, ConnectionState.IN_GAME_STATES),
    RECIPE_SHOP_MANAGE_QUIT(RequestRecipeShopManageQuit::new, ConnectionState.IN_GAME_STATES),
    RECIPE_SHOP_MANAGE_CANCEL(null, ConnectionState.IN_GAME_STATES),
    RECIPE_SHOP_MAKE_INFO(RequestRecipeShopMakeInfo::new, ConnectionState.IN_GAME_STATES),
    RECIPE_SHOP_MAKE_DO(RequestRecipeShopMakeItem::new, ConnectionState.IN_GAME_STATES),
    RECIPE_SHOP_SELL_LIST(RequestRecipeShopManagePrev::new, ConnectionState.IN_GAME_STATES),
    OBSERVER_END(ObserverReturn::new, ConnectionState.IN_GAME_STATES),
    VOTE_SOCIALITY(null, ConnectionState.IN_GAME_STATES),
    HENNA_ITEM_LIST(RequestHennaItemList::new, ConnectionState.IN_GAME_STATES),
    HENNA_ITEM_INFO(RequestHennaItemInfo::new, ConnectionState.IN_GAME_STATES),
    BUY_SEED(RequestBuySeed::new, ConnectionState.IN_GAME_STATES),
    CONFIRM_DLG(DlgAnswer::new, ConnectionState.IN_GAME_STATES),
    BUY_PREVIEW_LIST(RequestPreviewItem::new, ConnectionState.IN_GAME_STATES),
    SSQ_STATUS(null, ConnectionState.IN_GAME_STATES),
    PETITION_VOTE(RequestPetitionFeedback::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_33(DISCARD, ConnectionState.IN_GAME_STATES),
    GAMEGUARD_REPLY(GameGuardReply::new, ConnectionState.IN_GAME_STATES),
    MANAGE_PLEDGE_POWER(RequestPledgePower::new, ConnectionState.IN_GAME_STATES),
    MAKE_MACRO(RequestMakeMacro::new, ConnectionState.IN_GAME_STATES),
    DELETE_MACRO(RequestDeleteMacro::new, ConnectionState.IN_GAME_STATES),
    NOT_USE_32(DISCARD, ConnectionState.IN_GAME_STATES),
    EX(null, true, ConnectionState.ALL); // This packet has its own connection state checking so we allow all of them

    public static final IncomingPackets[] PACKET_ARRAY = values();

    private final Supplier<ClientPacket> incomingPacketFactory;
    private final EnumSet<ConnectionState> connectionStates;
    private final boolean hasExtension;

    IncomingPackets(Supplier<ClientPacket> incomingPacketFactory, boolean hasExtension, EnumSet<ConnectionState> connectionStates) {
        this.incomingPacketFactory = requireNonNullElse(incomingPacketFactory, NULL_PACKET_SUPLIER);
        this.connectionStates = connectionStates;
        this.hasExtension = hasExtension;
    }

    IncomingPackets(Supplier<ClientPacket> incomingPacketFactory, EnumSet<ConnectionState> connectionStates) {
        this(incomingPacketFactory, false, connectionStates);
    }

    @Override
    public int getPacketId() {
        return ordinal();
    }

    @Override
    public ClientPacket newIncomingPacket() {
        return incomingPacketFactory.get();
    }

    @Override
    public EnumSet<ConnectionState> getConnectionStates() {
        return connectionStates;
    }

    @Override
    public boolean canHandleState(ConnectionState connectionState) {
        return connectionStates.contains(connectionState);
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
        return requireNonNullElse(ExIncomingPackets.PACKET_ARRAY[exPacketId],NULLABLE_PACKET_FACTORY);
    }
}
