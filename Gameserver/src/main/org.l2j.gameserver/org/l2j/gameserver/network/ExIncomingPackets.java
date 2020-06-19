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
import org.l2j.gameserver.network.clientpackets.adenadistribution.RequestDivideAdena;
import org.l2j.gameserver.network.clientpackets.adenadistribution.RequestDivideAdenaCancel;
import org.l2j.gameserver.network.clientpackets.adenadistribution.RequestDivideAdenaStart;
import org.l2j.gameserver.network.clientpackets.attendance.RequestVipAttendanceCheck;
import org.l2j.gameserver.network.clientpackets.attendance.RequestVipAttendanceItemList;
import org.l2j.gameserver.network.clientpackets.attributechange.RequestChangeAttributeCancel;
import org.l2j.gameserver.network.clientpackets.attributechange.RequestChangeAttributeItem;
import org.l2j.gameserver.network.clientpackets.attributechange.SendChangeAttributeTargetItem;
import org.l2j.gameserver.network.clientpackets.autoplay.ExAutoPlaySetting;
import org.l2j.gameserver.network.clientpackets.autoplay.ExRequestActivateAutoShortcut;
import org.l2j.gameserver.network.clientpackets.captcha.RequestCaptchaAnswer;
import org.l2j.gameserver.network.clientpackets.captcha.RequestRefreshCaptcha;
import org.l2j.gameserver.network.clientpackets.ceremonyofchaos.RequestCancelCuriousHouse;
import org.l2j.gameserver.network.clientpackets.ceremonyofchaos.RequestCuriousHouseHtml;
import org.l2j.gameserver.network.clientpackets.ceremonyofchaos.RequestJoinCuriousHouse;
import org.l2j.gameserver.network.clientpackets.commission.*;
import org.l2j.gameserver.network.clientpackets.compound.*;
import org.l2j.gameserver.network.clientpackets.costume.*;
import org.l2j.gameserver.network.clientpackets.crystalization.RequestCrystallizeEstimate;
import org.l2j.gameserver.network.clientpackets.crystalization.RequestCrystallizeItemCancel;
import org.l2j.gameserver.network.clientpackets.elementalspirits.*;
import org.l2j.gameserver.network.clientpackets.ensoul.RequestItemEnsoul;
import org.l2j.gameserver.network.clientpackets.ensoul.RequestTryEnSoulExtraction;
import org.l2j.gameserver.network.clientpackets.friend.RequestFriendDetailInfo;
import org.l2j.gameserver.network.clientpackets.l2coin.RequestPurchaseLimitShopItemList;
import org.l2j.gameserver.network.clientpackets.luckygame.RequestLuckyGamePlay;
import org.l2j.gameserver.network.clientpackets.luckygame.RequestLuckyGameStartInfo;
import org.l2j.gameserver.network.clientpackets.mentoring.*;
import org.l2j.gameserver.network.clientpackets.mission.RequestOneDayRewardReceive;
import org.l2j.gameserver.network.clientpackets.mission.RequestTodoList;
import org.l2j.gameserver.network.clientpackets.olympiad.*;
import org.l2j.gameserver.network.clientpackets.pledgebonus.RequestPledgeBonusOpen;
import org.l2j.gameserver.network.clientpackets.pledgebonus.RequestPledgeBonusReward;
import org.l2j.gameserver.network.clientpackets.pledgebonus.RequestPledgeBonusRewardList;
import org.l2j.gameserver.network.clientpackets.primeshop.*;
import org.l2j.gameserver.network.clientpackets.pvpbook.ExRequestKillerLocation;
import org.l2j.gameserver.network.clientpackets.pvpbook.ExRequestPvpBookList;
import org.l2j.gameserver.network.clientpackets.pvpbook.ExTeleportToKiller;
import org.l2j.gameserver.network.clientpackets.raidbossinfo.RequestRaidBossSpawnInfo;
import org.l2j.gameserver.network.clientpackets.raidbossinfo.RequestRaidServerInfo;
import org.l2j.gameserver.network.clientpackets.rank.ExRankCharInfo;
import org.l2j.gameserver.network.clientpackets.rank.ExRankingCharRankers;
import org.l2j.gameserver.network.clientpackets.rank.ExRequestRankingCharHistory;
import org.l2j.gameserver.network.clientpackets.sessionzones.ExTimedHuntingZoneEnter;
import org.l2j.gameserver.network.clientpackets.sessionzones.ExTimedHuntingZoneList;
import org.l2j.gameserver.network.clientpackets.shuttle.CannotMoveAnymoreInShuttle;
import org.l2j.gameserver.network.clientpackets.shuttle.MoveToLocationInShuttle;
import org.l2j.gameserver.network.clientpackets.shuttle.RequestShuttleGetOff;
import org.l2j.gameserver.network.clientpackets.shuttle.RequestShuttleGetOn;
import org.l2j.gameserver.network.clientpackets.stats.ExResetStatusBonus;
import org.l2j.gameserver.network.clientpackets.stats.ExSetStatusBonus;
import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleport;
import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleportFavoriteList;
import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleportFavoritesAddDel;
import org.l2j.gameserver.network.clientpackets.teleport.ExRequestTeleportFavoritesUIToggle;
import org.l2j.gameserver.network.clientpackets.training.NotifyTrainingRoomEnd;
import org.l2j.gameserver.network.clientpackets.upgrade.ExUpgradeSystemNormalRequest;
import org.l2j.gameserver.network.clientpackets.upgrade.ExUpgradeSystemRequest;
import org.l2j.gameserver.network.clientpackets.vip.ExRequestVipInfo;
import org.l2j.gameserver.network.clientpackets.vip.RequestVipLuckGameInfo;
import org.l2j.gameserver.network.clientpackets.vip.RequestVipProductList;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public enum ExIncomingPackets implements PacketFactory {
    EX_DUMMY(null, ConnectionState.IN_GAME_STATES),
    EX_REQ_MANOR_LIST(RequestManorList::new, ConnectionState.JOINING_GAME_STATES),
    EX_PROCURE_CROP_LIST(RequestProcureCropList::new, ConnectionState.IN_GAME_STATES),
    EX_SET_SEED(RequestSetSeed::new, ConnectionState.IN_GAME_STATES),
    EX_SET_CROP(RequestSetCrop::new, ConnectionState.IN_GAME_STATES),
    EX_WRITE_HERO_WORDS(RequestWriteHeroWords::new, ConnectionState.IN_GAME_STATES),
    EX_ASK_JOIN_MPCC(RequestExAskJoinMPCC::new, ConnectionState.IN_GAME_STATES),
    EX_ACCEPT_JOIN_MPCC(RequestExAcceptJoinMPCC::new, ConnectionState.IN_GAME_STATES),
    EX_OUST_FROM_MPCC(RequestExOustFromMPCC::new, ConnectionState.IN_GAME_STATES),
    EX_OUST_FROM_PARTY_ROOM(RequestOustFromPartyRoom::new, ConnectionState.IN_GAME_STATES),
    EX_DISMISS_PARTY_ROOM(RequestDismissPartyRoom::new, ConnectionState.IN_GAME_STATES),
    EX_WITHDRAW_PARTY_ROOM(RequestWithdrawPartyRoom::new, ConnectionState.IN_GAME_STATES),
    EX_HAND_OVER_PARTY_MASTER(RequestChangePartyLeader::new, ConnectionState.IN_GAME_STATES),
    EX_AUTO_SOULSHOT(RequestAutoSoulShot::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_SKILL_INFO(RequestExEnchantSkillInfo::new, ConnectionState.IN_GAME_STATES),
    EX_REQ_ENCHANT_SKILL(RequestExEnchantSkill::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_EMBLEM(RequestExPledgeCrestLarge::new, ConnectionState.IN_GAME_STATES),
    EX_SET_PLEDGE_EMBLEM(RequestExSetPledgeCrestLarge::new, ConnectionState.IN_GAME_STATES),
    EX_SET_ACADEMY_MASTER(RequestPledgeSetAcademyMaster::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_POWER_GRADE_LIST(RequestPledgePowerGradeList::new, ConnectionState.IN_GAME_STATES),
    EX_VIEW_PLEDGE_POWER(RequestPledgeMemberPowerInfo::new, ConnectionState.IN_GAME_STATES),
    EX_SET_PLEDGE_POWER_GRADE(RequestPledgeSetMemberPowerGrade::new, ConnectionState.IN_GAME_STATES),
    EX_VIEW_PLEDGE_MEMBER_INFO(RequestPledgeMemberInfo::new, ConnectionState.IN_GAME_STATES),
    EX_VIEW_PLEDGE_WARLIST(RequestPledgeWarList::new, ConnectionState.IN_GAME_STATES),
    EX_FISH_RANKING(RequestExFishRanking::new, ConnectionState.IN_GAME_STATES),
    EX_PCCAFE_COUPON_USE(RequestPCCafeCouponUse::new, ConnectionState.IN_GAME_STATES),
    EX_ORC_MOVE(null, ConnectionState.IN_GAME_STATES),
    EX_DUEL_ASK_START(RequestDuelStart::new, ConnectionState.IN_GAME_STATES),
    EX_DUEL_ACCEPT_START(RequestDuelAnswerStart::new, ConnectionState.IN_GAME_STATES),
    EX_SET_TUTORIAL(null, ConnectionState.IN_GAME_STATES),
    EX_RQ_ITEMLINK(RequestExRqItemLink::new, ConnectionState.IN_GAME_STATES),
    EX_CAN_NOT_MOVE_ANYMORE_IN_AIRSHIP(null, ConnectionState.IN_GAME_STATES),
    EX_MOVE_TO_LOCATION_IN_AIRSHIP(null, ConnectionState.IN_GAME_STATES),
    EX_LOAD_UI_SETTING(RequestKeyMapping::new, ConnectionState.JOINING_GAME_AND_IN_GAME),
    EX_SAVE_UI_SETTING(RequestSaveKeyMapping::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_BASE_ATTRIBUTE_CANCEL(RequestExRemoveItemAttribute::new, ConnectionState.IN_GAME_STATES),
    EX_CHANGE_INVENTORY_SLOT(RequestSaveInventoryOrder::new, ConnectionState.IN_GAME_STATES),
    EX_EXIT_PARTY_MATCHING_WAITING_ROOM(RequestExitPartyMatchingWaitingRoom::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_PUT_ITEM_FOR_VARIATION_MAKE(RequestConfirmTargetItem::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_PUT_INTENSIVE_FOR_VARIATION_MAKE(RequestConfirmRefinerItem::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_PUT_COMMISSION_FOR_VARIATION_MAKE(RequestConfirmGemStone::new, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_OBSERVER_END(RequestOlympiadObserverEnd::new, ConnectionState.IN_GAME_STATES),
    EX_CURSED_WEAPON_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_EXISTING_CURSED_WEAPON_LOCATION(null, ConnectionState.IN_GAME_STATES),
    EX_REORGANIZE_PLEDGE_MEMBER(RequestPledgeReorganizeMember::new, ConnectionState.IN_GAME_STATES),
    EX_MPCC_SHOW_PARTY_MEMBERS_INFO(RequestExMPCCShowPartyMembersInfo::new, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_MATCH_LIST(RequestOlympiadMatchList::new, ConnectionState.IN_GAME_STATES),
    EX_ASK_JOIN_PARTY_ROOM(RequestAskJoinPartyRoom::new, ConnectionState.IN_GAME_STATES),
    EX_ANSWER_JOIN_PARTY_ROOM(AnswerJoinPartyRoom::new, ConnectionState.IN_GAME_STATES),
    EX_LIST_PARTY_MATCHING_WAITING_ROOM(RequestListPartyMatchingWaitingRoom::new, ConnectionState.IN_GAME_STATES),
    EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM(RequestExEnchantItemAttribute::new, ConnectionState.IN_GAME_STATES),
    EX_CHARACTER_BACK(RequestGotoLobby::new, ConnectionState.AUTHENTICATED_STATES),
    EX_CANNOT_AIRSHIP_MOVE_ANYMORE(null, ConnectionState.AUTHENTICATED_STATES),
    EX_MOVE_TO_LOCATION_AIRSHIP(null, ConnectionState.IN_GAME_STATES),
    EX_ITEM_AUCTION_BID(RequestBidItemAuction::new, ConnectionState.IN_GAME_STATES),
    EX_ITEM_AUCTION_INFO(RequestInfoItemAuction::new, ConnectionState.IN_GAME_STATES),
    EX_CHANGE_NAME(RequestExChangeName::new, ConnectionState.IN_GAME_STATES),
    EX_SHOW_CASTLE_INFO(RequestAllCastleInfo::new, ConnectionState.IN_GAME_STATES),
    EX_SHOW_FORTRESS_INFO(RequestAllFortressInfo::new, ConnectionState.IN_GAME_STATES),
    EX_SHOW_AGIT_INFO(RequestAllAgitInfo::new, ConnectionState.IN_GAME_STATES),
    EX_SHOW_FORTRESS_SIEGE_INFO(RequestFortressSiegeInfo::new, ConnectionState.IN_GAME_STATES),
    EX_GET_BOSS_RECORD(RequestGetBossRecord::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_MAKE_VARIATION(RequestRefine::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_PUT_ITEM_FOR_VARIATION_CANCEL(RequestConfirmCancelItem::new, ConnectionState.IN_GAME_STATES),
    EX_CLICK_VARIATION_CANCEL_BUTTON(RequestRefineCancel::new, ConnectionState.IN_GAME_STATES),
    EX_MAGIC_SKILL_USE_GROUND(RequestExMagicSkillUseGround::new, ConnectionState.IN_GAME_STATES),
    EX_DUEL_SURRENDER(RequestDuelSurrender::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_SKILL_INFO_DETAIL(RequestExEnchantSkillInfoDetail::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_ANTI_FREE_SERVER(null, ConnectionState.IN_GAME_STATES),
    EX_SHOW_FORTRESS_MAP_INFO(RequestFortressMapInfo::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_PVPMATCH_RECORD(RequestPVPMatchRecord::new, ConnectionState.IN_GAME_STATES),
    EX_PRIVATE_STORE_WHOLE_SET_MSG(SetPrivateStoreWholeMsg::new, ConnectionState.IN_GAME_STATES),
    EX_DISPEL(RequestDispel::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_PUT_ENCHANT_TARGET_ITEM(RequestExTryToPutEnchantTargetItem::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_PUT_ENCHANT_SUPPORT_ITEM(RequestExTryToPutEnchantSupportItem::new, ConnectionState.IN_GAME_STATES),
    EX_CANCEL_ENCHANT_ITEM(RequestExCancelEnchantItem::new, ConnectionState.IN_GAME_STATES),
    EX_CHANGE_NICKNAME_COLOR(RequestChangeNicknameColor::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_RESET_NICKNAME(RequestResetNickname::new, ConnectionState.IN_GAME_STATES),
    EX_USER_BOOKMARK(null, true, ConnectionState.IN_GAME_STATES),
    EX_WITHDRAW_PREMIUM_ITEM(RequestWithDrawPremiumItem::new, ConnectionState.IN_GAME_STATES),
    EX_JUMP(null, ConnectionState.IN_GAME_STATES),
    EX_START_REQUEST_PVPMATCH_CC_RANK(RequestStartShowKrateisCubeRank::new, ConnectionState.IN_GAME_STATES),
    EX_STOP_REQUEST_PVPMATCH_CC_RANK(RequestStopShowKrateisCubeRank::new, ConnectionState.IN_GAME_STATES),
    EX_NOTIFY_START_MINIGAME(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_REGISTER_DOMINION(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_DOMINION_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_CLEFT_ENTER(null, ConnectionState.IN_GAME_STATES),
    EX_BLOCK_UPSET_ENTER(null, ConnectionState.IN_GAME_STATES),
    EX_END_SCENE_PLAYER(EndScenePlayer::new, ConnectionState.IN_GAME_STATES),
    EX_BLOCK_UPSET_VOTE(null, ConnectionState.IN_GAME_STATES),
    EX_LIST_MPCC_WAITING(RequestExListMpccWaiting::new, ConnectionState.IN_GAME_STATES),
    EX_MANAGE_MPCC_ROOM(RequestExManageMpccRoom::new, ConnectionState.IN_GAME_STATES),
    EX_JOIN_MPCC_ROOM(RequestExJoinMpccRoom::new, ConnectionState.IN_GAME_STATES),
    EX_OUST_FROM_MPCC_ROOM(RequestExOustFromMpccRoom::new, ConnectionState.IN_GAME_STATES),
    EX_DISMISS_MPCC_ROOM(RequestExDismissMpccRoom::new, ConnectionState.IN_GAME_STATES),
    EX_WITHDRAW_MPCC_ROOM(RequestExWithdrawMpccRoom::new, ConnectionState.IN_GAME_STATES),
    EX_SEED_PHASE(RequestSeedPhase::new, ConnectionState.IN_GAME_STATES),
    EX_MPCC_PARTYMASTER_LIST(RequestExMpccPartymasterList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_POST_ITEM_LIST(RequestPostItemList::new, ConnectionState.IN_GAME_STATES),
    EX_SEND_POST(RequestSendPost::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_RECEIVED_POST_LIST(RequestReceivedPostList::new, ConnectionState.IN_GAME_STATES),
    EX_DELETE_RECEIVED_POST(RequestDeleteReceivedPost::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_RECEIVED_POST(RequestReceivedPost::new, ConnectionState.IN_GAME_STATES),
    EX_RECEIVE_POST(RequestPostAttachment::new, ConnectionState.IN_GAME_STATES),
    EX_REJECT_POST(RequestRejectPostAttachment::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_SENT_POST_LIST(RequestSentPostList::new, ConnectionState.IN_GAME_STATES),
    EX_DELETE_SENT_POST(RequestDeleteSentPost::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_SENT_POST(RequestSentPost::new, ConnectionState.IN_GAME_STATES),
    EX_CANCEL_SEND_POST(RequestCancelPostAttachment::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_SHOW_PETITION(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_SHOWSTEP_TWO(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_SHOWSTEP_THREE(null, ConnectionState.IN_GAME_STATES),
    EX_CONNECT_TO_RAID_SERVER(null, ConnectionState.IN_GAME_STATES),
    EX_RETURN_FROM_RAID(null, ConnectionState.IN_GAME_STATES),
    EX_REFUND_REQ(RequestRefundItem::new, ConnectionState.IN_GAME_STATES),
    EX_BUY_SELL_UI_CLOSE_REQ(RequestBuySellUIClose::new, ConnectionState.IN_GAME_STATES),
    EX_EVENT_MATCH(null, ConnectionState.IN_GAME_STATES),
    EX_PARTY_LOOTING_MODIFY(RequestPartyLootModification::new, ConnectionState.IN_GAME_STATES),
    EX_PARTY_LOOTING_MODIFY_AGREEMENT(AnswerPartyLootModification::new, ConnectionState.IN_GAME_STATES),
    EX_ANSWER_COUPLE_ACTION(AnswerCoupleAction::new, ConnectionState.IN_GAME_STATES),
    EX_BR_LOAD_EVENT_TOP_RANKERS_REQ(BrEventRankerList::new, ConnectionState.IN_GAME_STATES),
    EX_ASK_MY_MEMBERSHIP(null, ConnectionState.IN_GAME_STATES),
    EX_QUEST_NPC_LOG_LIST(RequestAddExpandQuestAlarm::new, ConnectionState.IN_GAME_STATES),
    EX_VOTE_SYSTEM(RequestVoteNew::new, ConnectionState.IN_GAME_STATES),
    EX_GETON_SHUTTLE(RequestShuttleGetOn::new, ConnectionState.IN_GAME_STATES),
    EX_GETOFF_SHUTTLE(RequestShuttleGetOff::new, ConnectionState.IN_GAME_STATES),
    EX_MOVE_TO_LOCATION_IN_SHUTTLE(MoveToLocationInShuttle::new, ConnectionState.IN_GAME_STATES),
    EX_CAN_NOT_MOVE_ANYMORE_IN_SHUTTLE(CannotMoveAnymoreInShuttle::new, ConnectionState.IN_GAME_STATES),
    EX_AGITAUCTION_CMD(null, ConnectionState.IN_GAME_STATES), // TODO: Implement / HANDLE SWITCH
    EX_ADD_POST_FRIEND(RequestExAddContactToContactList::new, ConnectionState.IN_GAME_STATES),
    EX_DELETE_POST_FRIEND(RequestExDeleteContactFromContactList::new, ConnectionState.IN_GAME_STATES),
    EX_SHOW_POST_FRIEND(RequestExShowContactList::new, ConnectionState.IN_GAME_STATES),
    EX_FRIEND_LIST_FOR_POSTBOX(RequestExFriendListExtended::new, ConnectionState.IN_GAME_STATES),
    EX_GFX_OLYMPIAD(RequestExOlympiadMatchListRefresh::new, ConnectionState.IN_GAME_STATES),
    EX_BR_GAME_POINT_REQ(RequestBRGamePoint::new, ConnectionState.IN_GAME_STATES),
    EX_BR_PRODUCT_LIST_REQ(RequestBRProductList::new, ConnectionState.IN_GAME_STATES),
    EX_BR_PRODUCT_INFO_REQ(RequestBRProductInfo::new, ConnectionState.IN_GAME_STATES),
    EX_BR_BUY_PRODUCT_REQ(RequestBRBuyProduct::new, ConnectionState.IN_GAME_STATES),
    EX_BR_RECENT_PRODUCT_REQ(RequestBRRecentProductList::new, ConnectionState.IN_GAME_STATES),
    EX_BR_MINIGAME_LOAD_SCORES_REQ(null, ConnectionState.IN_GAME_STATES),
    EX_BR_MINIGAME_INSERT_SCORE_REQ(null, ConnectionState.IN_GAME_STATES),
    EX_BR_SET_LECTURE_MARK_REQ(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_CRYSTALITEM_INFO(RequestCrystallizeEstimate::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_CRYSTALITEM_CANCEL(RequestCrystallizeItemCancel::new, ConnectionState.IN_GAME_STATES),
    EX_STOP_SCENE_PLAYER(RequestExEscapeScene::new, ConnectionState.IN_GAME_STATES),
    EX_FLY_MOVE(null, ConnectionState.IN_GAME_STATES), // RequestFlyMove - Sayune is not available on classic yet
    EX_SURRENDER_PLEDGE_WAR(null, ConnectionState.IN_GAME_STATES),
    EX_DYNAMIC_QUEST(null, ConnectionState.IN_GAME_STATES), // TODO: Implement / HANDLE SWITCH
    EX_FRIEND_DETAIL_INFO(RequestFriendDetailInfo::new, ConnectionState.IN_GAME_STATES),
    EX_UPDATE_FRIEND_MEMO(null, ConnectionState.IN_GAME_STATES),
    EX_UPDATE_BLOCK_MEMO(null, ConnectionState.IN_GAME_STATES),
    EX_LOAD_INZONE_PARTY_HISTORY(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_ITEM_LIST(RequestCommissionRegistrableItemList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_INFO(RequestCommissionInfo::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_REGISTER(RequestCommissionRegister::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_CANCEL(RequestCommissionCancel::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_DELETE(RequestCommissionDelete::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_SEARCH(RequestCommissionList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_BUY_INFO(RequestCommissionBuyInfo::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_BUY_ITEM(RequestCommissionBuyItem::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_COMMISSION_REGISTERED_ITEM(RequestCommissionRegisteredItem::new, ConnectionState.IN_GAME_STATES),
    EX_CALL_TO_CHANGE_CLASS(null, ConnectionState.IN_GAME_STATES),
    EX_CHANGE_TO_AWAKENED_CLASS(RequestChangeToAwakenedClass::new, ConnectionState.IN_GAME_STATES),
    EX_NOT_USED_163(DISCARD, ConnectionState.IN_GAME_STATES),
    EX_NOT_USED_164(DISCARD, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_WEB_SESSION_ID(null, ConnectionState.IN_GAME_STATES),
    EX_2ND_PASSWORD_CHECK(RequestEx2ndPasswordCheck::new, ConnectionState.AUTHENTICATED_STATES),
    EX_2ND_PASSWORD_VERIFY(RequestEx2ndPasswordVerify::new, ConnectionState.AUTHENTICATED_STATES),
    EX_2ND_PASSWORD_REQ(RequestEx2ndPasswordReq::new, ConnectionState.AUTHENTICATED_STATES),
    EX_CHECK_CHAR_NAME(RequestCharacterNameCreatable::new, ConnectionState.AUTHENTICATED_STATES),
    EX_REQUEST_GOODS_INVENTORY_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_USE_GOODS_IVENTORY_ITEM(null, ConnectionState.IN_GAME_STATES),
    EX_NOTIFY_PLAY_START(null, ConnectionState.IN_GAME_STATES),
    EX_FLY_MOVE_START(null, ConnectionState.IN_GAME_STATES), // RequestFlyMoveStart - Sayune is not available on classic yet
    EX_USER_HARDWARE_INFO(RequestHardWareInfo::new, ConnectionState.ALL),
    EX_USER_INTERFACE_INFO(null, ConnectionState.ALL),
    EX_CHANGE_ATTRIBUTE_ITEM(RequestChangeAttributeItem::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_CHANGE_ATTRIBUTE(SendChangeAttributeTargetItem::new, ConnectionState.IN_GAME_STATES),
    EX_CHANGE_ATTRIBUTE_CANCEL(RequestChangeAttributeCancel::new, ConnectionState.IN_GAME_STATES),
    EX_BR_BUY_PRODUCT_GIFT_REQ(RequestBRPresentBuyProduct::new, ConnectionState.IN_GAME_STATES),
    EX_MENTOR_ADD(ConfirmMenteeAdd::new, ConnectionState.IN_GAME_STATES),
    EX_MENTOR_CANCEL(RequestMentorCancel::new, ConnectionState.IN_GAME_STATES),
    EX_MENTOR_LIST(RequestMentorList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_MENTOR_ADD(RequestMenteeAdd::new, ConnectionState.IN_GAME_STATES),
    EX_MENTEE_WAITING_LIST(RequestMenteeWaitingList::new, ConnectionState.IN_GAME_STATES),
    EX_JOIN_PLEDGE_BY_NAME(RequestClanAskJoinByName::new, ConnectionState.IN_GAME_STATES),
    EX_INZONE_WAITING_TIME(RequestInzoneWaitingTime::new, ConnectionState.IN_GAME_STATES),
    EX_JOIN_CURIOUS_HOUSE(RequestJoinCuriousHouse::new, ConnectionState.IN_GAME_STATES),
    EX_CANCEL_CURIOUS_HOUSE(RequestCancelCuriousHouse::new, ConnectionState.IN_GAME_STATES),
    EX_LEAVE_CURIOUS_HOUSE(null, ConnectionState.IN_GAME_STATES),
    EX_OBSERVE_LIST_CURIOUS_HOUSE(null, ConnectionState.IN_GAME_STATES),
    EX_OBSERVE_CURIOUS_HOUSE(null, ConnectionState.IN_GAME_STATES),
    EX_EXIT_OBSERVE_CURIOUS_HOUSE(null, ConnectionState.IN_GAME_STATES),
    EX_REQ_CURIOUS_HOUSE_HTML(RequestCuriousHouseHtml::new, ConnectionState.IN_GAME_STATES),
    EX_REQ_CURIOUS_HOUSE_RECORD(null, ConnectionState.IN_GAME_STATES),
    EX_SYS_STRING(null, ConnectionState.IN_GAME_STATES),
    EX_TRY_TO_PUT_SHAPE_SHIFTING_TARGET_ITEM(null, ConnectionState.IN_GAME_STATES), // RequestExTryToPutShapeShiftingTargetItem - Appearance Stone not used on classic
    EX_TRY_TO_PUT_SHAPE_SHIFTING_EXTRACTION_ITEM(null, ConnectionState.IN_GAME_STATES), // RequestExTryToPutShapeShiftingEnchantSupportItem - Appearance Stone not used on classic
    EX_CANCEL_SHAPE_SHIFTING(null, ConnectionState.IN_GAME_STATES), // RequestExCancelShape_Shifting_Item - Appearance Stone not used on classic
    EX_REQUEST_SHAPE_SHIFTING(null, ConnectionState.IN_GAME_STATES), // RequestShapeShiftingItem - Appearance Stone not used on classic
    EX_NCGUARD(DISCARD, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_KALIE_TOKEN(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_SHOW_REGIST_BEAUTY(RequestShowBeautyList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_REGIST_BEAUTY(RequestRegistBeauty::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_SHOW_RESET_BEAUTY(RequestShowResetShopList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_RESET_BEAUTY(null, ConnectionState.IN_GAME_STATES),
    EX_CHECK_SPEEDHACK(null, ConnectionState.IN_GAME_STATES),
    EX_BR_ADD_INTERESTED_PRODUCT(null, ConnectionState.IN_GAME_STATES),
    EX_BR_DELETE_INTERESTED_PRODUCT(null, ConnectionState.IN_GAME_STATES),
    EX_BR_EXIST_NEW_PRODUCT_REQ(null, ConnectionState.IN_GAME_STATES),
    EX_EVENT_CAMPAIGN_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_RECRUIT_INFO(RequestPledgeRecruitInfo::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_RECRUIT_BOARD_SEARCH(RequestPledgeRecruitBoardSearch::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_RECRUIT_BOARD_APPLY(RequestPledgeRecruitBoardAccess::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_RECRUIT_BOARD_DETAIL(RequestPledgeRecruitBoardDetail::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_WAITING_LIST_APPLY(RequestPledgeWaitingApply::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_WAITING_LIST_APPLIED(RequestPledgeWaitingApplied::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_WAITING_LIST(RequestPledgeWaitingList::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_WAITING_USER(RequestPledgeWaitingUser::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_WAITING_USER_ACCEPT(RequestPledgeWaitingUserAccept::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_DRAFT_LIST_SEARCH(RequestPledgeDraftListSearch::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_DRAFT_LIST_APPLY(RequestPledgeDraftListApply::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_RECRUIT_APPLY_INFO(RequestPledgeRecruitApplyInfo::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_JOIN_SYS(null, ConnectionState.IN_GAME_STATES),
    EX_RESPONSE_WEB_PETITION_ALARM(null, ConnectionState.IN_GAME_STATES),
    EX_NOTIFY_EXIT_BEAUTYSHOP(NotifyExitBeautyShop::new, ConnectionState.IN_GAME_STATES),
    EX_EVENT_REGISTER_XMAS_WISHCARD(null, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_SCROLL_ITEM_ADD(RequestExAddEnchantScrollItem::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_SUPPORT_ITEM_REMOVE(RequestExRemoveEnchantSupportItem::new, ConnectionState.IN_GAME_STATES),
    EX_SELECT_CARD_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_DIVIDE_ADENA_START(RequestDivideAdenaStart::new, ConnectionState.IN_GAME_STATES),
    EX_DIVIDE_ADENA_CANCEL(RequestDivideAdenaCancel::new, ConnectionState.IN_GAME_STATES),
    EX_DIVIDE_ADENA(RequestDivideAdena::new, ConnectionState.IN_GAME_STATES),
    EX_ACQUIRE_POTENTIAL_SKILL(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_POTENTIAL_SKILL_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_RESET_POTENTIAL_SKILL(null, ConnectionState.IN_GAME_STATES),
    EX_CHANGE_POTENTIAL_POINT(null, ConnectionState.IN_GAME_STATES),
    EX_STOP_MOVE(RequestStopMove::new, ConnectionState.IN_GAME_STATES),
    EX_ABILITY_WND_OPEN(null, ConnectionState.IN_GAME_STATES),
    EX_ABILITY_WND_CLOSE(null, ConnectionState.IN_GAME_STATES),
    EX_START_LUCKY_GAME(RequestLuckyGameStartInfo::new, ConnectionState.IN_GAME_STATES),
    EX_BETTING_LUCKY_GAME(RequestLuckyGamePlay::new, ConnectionState.IN_GAME_STATES),
    EX_TRAININGZONE_LEAVING(NotifyTrainingRoomEnd::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_ONE(RequestNewEnchantPushOne::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_ONE_REMOVE(RequestNewEnchantRemoveOne::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_TWO(RequestNewEnchantPushTwo::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_TWO_REMOVE(RequestNewEnchantRemoveTwo::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_CLOSE(RequestNewEnchantClose::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_TRY(RequestNewEnchantTry::new, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_RETRY_TO_PUT_ITEMS(RequestNewEnchantRetryToPutItems::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_CARD_REWARD_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_ACCOUNT_ATTENDANCE_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_ACCOUNT_ATTENDANCE_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_TARGET(RequestTargetActionMenu::new, ConnectionState.IN_GAME_STATES),
    EX_SELECTED_QUEST_ZONEID(ExSendSelectedQuestZoneID::new, ConnectionState.IN_GAME_STATES),
    EX_ALCHEMY_SKILL_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_TRY_MIX_CUBE(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_ALCHEMY_CONVERSION(null, ConnectionState.IN_GAME_STATES),
    EX_EXECUTED_UIEVENTS_COUNT(null, ConnectionState.IN_GAME_STATES),
    EX_CLIENT_INI(DISCARD, ConnectionState.AUTHENTICATED_STATES),
    EX_REQUEST_AUTOFISH(ExRequestAutoFish::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_VIP_ATTENDANCE_ITEMLIST(RequestVipAttendanceItemList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_VIP_ATTENDANCE_CHECK(RequestVipAttendanceCheck::new, ConnectionState.IN_GAME_STATES),
    EX_TRY_ENSOUL(RequestItemEnsoul::new, ConnectionState.IN_GAME_STATES),
    EX_CASTLEWAR_SEASON_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_BR_VIP_PRODUCT_LIST_REQ(RequestVipProductList::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_LUCKY_GAME_INFO(RequestVipLuckGameInfo::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_LUCKY_GAME_ITEMLIST(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_LUCKY_GAME_BONUS(null, ConnectionState.IN_GAME_STATES),
    EX_VIP_INFO(ExRequestVipInfo::new, ConnectionState.IN_GAME_STATES),
    EX_CAPTCHA_ANSWER(RequestCaptchaAnswer::new, ConnectionState.IN_GAME_STATES),
    EX_REFRESH_CAPTCHA_IMAGE(RequestRefreshCaptcha::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_SIGNIN(RequestPledgeSignInForOpenJoiningMethod::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_MATCH_ARENA(null, ConnectionState.IN_GAME_STATES),
    EX_CONFIRM_MATCH_ARENA(null, ConnectionState.IN_GAME_STATES),
    EX_CANCEL_MATCH_ARENA(null, ConnectionState.IN_GAME_STATES),
    EX_CHANGE_CLASS_ARENA(null, ConnectionState.IN_GAME_STATES),
    EX_CONFIRM_CLASS_ARENA(null, ConnectionState.IN_GAME_STATES),
    EX_DECO_NPC_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_DECO_NPC_SET(null, ConnectionState.IN_GAME_STATES),
    EX_FACTION_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_EXIT_ARENA(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_BALTHUS_TOKEN(null, ConnectionState.IN_GAME_STATES),
    EX_PARTY_MATCHING_ROOM_HISTORY(null, ConnectionState.IN_GAME_STATES),
    EX_ARENA_CUSTOM_NOTIFICATION(null, ConnectionState.IN_GAME_STATES),
    EX_TODOLIST(RequestTodoList::new, ConnectionState.JOINING_GAME_AND_IN_GAME),
    EX_TODOLIST_HTML(null, ConnectionState.IN_GAME_STATES),
    EX_ONE_DAY_RECEIVE_REWARD(RequestOneDayRewardReceive::new, ConnectionState.IN_GAME_STATES),
    EX_QUEUETICKET(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_BONUS_UI_OPEN(RequestPledgeBonusOpen::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_BONUS_REWARD_LIST(RequestPledgeBonusRewardList::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_BONUS_RECEIVE_REWARD(RequestPledgeBonusReward::new, ConnectionState.IN_GAME_STATES),
    EX_SSO_AUTHNTOKEN_REQ(null, ConnectionState.IN_GAME_STATES),
    EX_QUEUETICKET_LOGIN(null, ConnectionState.IN_GAME_STATES),
    EX_BLOCK_DETAIL_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_TRY_ENSOUL_EXTRACTION(RequestTryEnSoulExtraction::new, ConnectionState.IN_GAME_STATES),
    EX_RAID_BOSS_SPAWN_INFO(RequestRaidBossSpawnInfo::new, ConnectionState.IN_GAME_STATES),
    EX_RAID_SERVER_INFO(RequestRaidServerInfo::new, ConnectionState.IN_GAME_STATES),
    EX_SHOW_AGIT_SIEGE_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_ITEM_AUCTION_STATUS(null, ConnectionState.IN_GAME_STATES),
    EX_MONSTER_BOOK_OPEN(null, ConnectionState.IN_GAME_STATES),
    EX_MONSTER_BOOK_CLOSE(null, ConnectionState.IN_GAME_STATES),
    EX_REQ_MONSTER_BOOK_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_MATCHGROUP(null, ConnectionState.IN_GAME_STATES),
    EX_MATCHGROUP_ASK(null, ConnectionState.IN_GAME_STATES),
    EX_MATCHGROUP_ANSWER(null, ConnectionState.IN_GAME_STATES),
    EX_MATCHGROUP_WITHDRAW(null, ConnectionState.IN_GAME_STATES),
    EX_MATCHGROUP_OUST(null, ConnectionState.IN_GAME_STATES),
    EX_MATCHGROUP_CHANGE_MASTER(null, ConnectionState.IN_GAME_STATES),
    EX_UPGRADE_SYSTEM_REQUEST(ExUpgradeSystemRequest::new, ConnectionState.IN_GAME_STATES),
    EX_CARD_UPDOWN_PICK_NUMB(null, ConnectionState.IN_GAME_STATES),
    EX_CARD_UPDOWN_GAME_REWARD_REQUEST(null, ConnectionState.IN_GAME_STATES),
    EX_CARD_UPDOWN_GAME_RETRY(null, ConnectionState.IN_GAME_STATES),
    EX_CARD_UPDOWN_GAME_QUIT(null, ConnectionState.IN_GAME_STATES),
    EX_ARENA_RANK_ALL(null, ConnectionState.IN_GAME_STATES),
    EX_ARENA_MYRANK(null, ConnectionState.IN_GAME_STATES),
    EX_SWAP_AGATHION_SLOT_ITEMS(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_CONTRIBUTION_RANK(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_CONTRIBUTION_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_CONTRIBUTION_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_LEVEL_UP(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MISSION_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MISSION_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MASTERY_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MASTERY_SET(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MASTERY_RESET(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_SKILL_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_SKILL_ACTIVATE(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_ITEM_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_ITEM_ACTIVATE(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_ANNOUNCE(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_ANNOUNCE_SET(null, ConnectionState.IN_GAME_STATES),
    EX_CREATE_PLEDGE(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_ITEM_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_ITEM_BUY(null, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_INFO(ExElementalSpiritInfo::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_EXTRACT_INFO(ExElementalSpiritExtractInfo::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_EXTRACT(ExElementalSpiritExtract::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO(ExElementalSpiritEvolutionInfo::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_EVOLUTION(ExElementalSpiritEvolution::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_SET_TALENT(ExElementalSpiritSetTalent::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_INIT_TALENT(ExElementalInitTalent::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_ABSORB_INFO(ExElementalSpiritAbsorbInfo::new, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_ABSORB(ExElementalSpiritAbsorb::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_LOCKED_ITEM(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_UNLOCKED_ITEM(null, ConnectionState.IN_GAME_STATES),
    EX_LOCKED_ITEM_CANCEL(null, ConnectionState.IN_GAME_STATES),
    EX_UNLOCKED_ITEM_CANCEL(null, ConnectionState.IN_GAME_STATES),
    EX_ELEMENTAL_SPIRIT_CHANGE_TYPE(ExElementalSpiritChangeType::new, ConnectionState.IN_GAME_STATES),
    EX_BLOCK_PACKET_FOR_AD(ExRequestBlockListForAD::new, ConnectionState.IN_GAME_STATES),
    EX_USER_BAN_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_INTERACT_MODIFY(null, ConnectionState.IN_GAME_STATES),
    EX_TRY_ENCHANT_ARTIFACT(null, ConnectionState.IN_GAME_STATES),
    EX_UPGRADE_SYSTEM_NORMAL_REQUEST(ExUpgradeSystemNormalRequest::new, ConnectionState.IN_GAME_STATES),
    EX_PURCHASE_LIMIT_SHOP_ITEM_LIST(RequestPurchaseLimitShopItemList::new, ConnectionState.IN_GAME_STATES),
    EX_PURCHASE_LIMIT_SHOP_ITEM_BUY(RequestPurchaseLimitShopItemBuy::new, ConnectionState.IN_GAME_STATES),
    EX_OPEN_HTML(ExOpenHtml::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_CLASS_CHANGE(ExRequestClassChange::new, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_CLASS_CHANGE_VERIFYING(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_TELEPORT(ExRequestTeleport::new, ConnectionState.IN_GAME_STATES),
    EX_COSTUME_USE_ITEM(ExRequestCostumeUseItem::new, ConnectionState.IN_GAME_STATES),
    EX_COSTUME_LIST(ExRequestCostumeList::new, ConnectionState.IN_GAME_STATES),
    EX_COSTUME_COLLECTION_SKILL_ACTIVE(ExRequestCostumeCollectSkillActive::new, ConnectionState.IN_GAME_STATES),
    EX_COSTUME_EVOLUTION(ExRequestCostumeEvolution::new, ConnectionState.IN_GAME_STATES),
    EX_COSTUME_EXTRACT(ExRequestCostumeExtract::new, ConnectionState.IN_GAME_STATES),
    EX_COSTUME_LOCK(ExRequestCostumeLock::new, ConnectionState.IN_GAME_STATES),
    EX_COSTUME_CHANGE_SHORTCUT(null, ConnectionState.IN_GAME_STATES),
    EX_MAGICLAMP_GAME_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_MAGICLAMP_GAME_START(null, ConnectionState.IN_GAME_STATES),
    EX_ACTIVATE_AUTO_SHORTCUT(ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES),
    EX_PREMIUM_MANAGER_LINK_HTML(ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES),
    EX_PREMIUM_MANAGER_PASS_CMD_TO_SERVER(ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES),
    EX_ACTIVATED_CURSED_TREASURE_BOX_LOCATION(ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME_STATES),
    EX_PAYBACK_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_PAYBACK_GIVE_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_AUTOPLAY_SETTING(ExAutoPlaySetting::new, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_MATCH_MAKING(null, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_MATCH_MAKING_CANCEL(null, ConnectionState.IN_GAME_STATES),
    EX_FESTIVAL_BM_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_FESTIVAL_BM_GAME(null, ConnectionState.IN_GAME_STATES),
    EX_GACHA_SHOP_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_GACHA_SHOP_GACHA_GROUP(null, ConnectionState.IN_GAME_STATES),
    EX_GACHA_SHOP_GACHA_ITEM(null, ConnectionState.IN_GAME_STATES),
    EX_TIME_RESTRICT_FIELD_LIST(ExTimedHuntingZoneList::new, ConnectionState.IN_GAME_STATES),
    EX_TIME_RESTRICT_FIELD_USER_ENTER(ExTimedHuntingZoneEnter::new, ConnectionState.IN_GAME_STATES),
    EX_RANKING_CHAR_INFO(ExRankCharInfo::new, ConnectionState.IN_GAME_STATES),
    EX_RANKING_CHAR_HISTORY(ExRequestRankingCharHistory::new, ConnectionState.IN_GAME_STATES),
    EX_RANKING_CHAR_RANKERS(ExRankingCharRankers::new, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MERCENARY_RECRUIT_INFO_SET(null, ConnectionState.IN_GAME_STATES),
    EX_MERCENARY_CASTLEWAR_CASTLE_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_ATTACKER_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_DEFENDER_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MERCENARY_MEMBER_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_PLEDGE_MERCENARY_MEMBER_JOIN(null, ConnectionState.IN_GAME_STATES),
    EX_PVPBOOK_LIST(ExRequestPvpBookList::new, ConnectionState.IN_GAME_STATES),
    EX_PVPBOOK_KILLER_LOCATION(ExRequestKillerLocation::new, ConnectionState.IN_GAME_STATES),
    EX_PVPBOOK_TELEPORT_TO_KILLER(ExTeleportToKiller::new, ConnectionState.IN_GAME_STATES),
    EX_LETTER_COLLECTOR_TAKE_REWARD(null, ConnectionState.IN_GAME_STATES),
    EX_SET_STATUS_BONUS(ExSetStatusBonus::new, ConnectionState.IN_GAME_STATES),
    EX_RESET_STATUS_BONUS(ExResetStatusBonus::new, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_MY_RANKING_INFO(ExRequestOlympiadMyRank::new, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_RANKING_INFO(ExRequestOlympiadRanking::new, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_HERO_AND_LEGEND_INFO(ExRequestOlympiadHeroes::new, ConnectionState.IN_GAME_STATES),
    EX_CASTLEWAR_OBSERVER_START(null, ConnectionState.IN_GAME_STATES),
    EX_RAID_TELEPORT_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_TELEPORT_TO_RAID_POSITION(null, ConnectionState.IN_GAME_STATES),
    EX_CRAFT_EXTRACT(null, ConnectionState.IN_GAME_STATES),
    EX_CRAFT_RANDOM_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_CRAFT_RANDOM_LOCK_SLOT(null, ConnectionState.IN_GAME_STATES),
    EX_CRAFT_RANDOM_REFRESH(null, ConnectionState.IN_GAME_STATES),
    EX_CRAFT_RANDOM_MAKE(null, ConnectionState.IN_GAME_STATES),
    EX_MULTI_SELL_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_SAVE_ITEM_ANNOUNCE_SETTING(null, ConnectionState.IN_GAME_STATES),
    EX_OLYMPIAD_UI(null, ConnectionState.IN_GAME_STATES),
    EX_SHARED_POSITION_TELEPORT_UI(null, ConnectionState.IN_GAME_STATES),
    EX_SHARED_POSITION_TELEPORT(null, ConnectionState.IN_GAME_STATES),
    EX_AUTH_RECONNECT(null, ConnectionState.IN_GAME_STATES),
    EX_PET_EQUIP_ITEM(null, ConnectionState.IN_GAME_STATES),
    EX_PET_UNEQUIP_ITEM(null, ConnectionState.IN_GAME_STATES),
    EX_SHOW_HOMUNCULUS_INFO(null, ConnectionState.IN_GAME_STATES),
    EX_HOMUNCULUS_CREATE_START(null, ConnectionState.IN_GAME_STATES),
    EX_HOMUNCULUS_INSERT(null, ConnectionState.IN_GAME_STATES),
    EX_HOMUNCULUS_SUMMON(null, ConnectionState.IN_GAME_STATES),
    EX_DELETE_HOMUNCULUS_DATA(null, ConnectionState.IN_GAME_STATES),
    EX_REQUEST_ACTIVATE_HOMUNCULUS(null, ConnectionState.IN_GAME_STATES),
    EX_HOMUNCULUS_GET_ENCHANT_POINT(null, ConnectionState.IN_GAME_STATES),
    EX_HOMUNCULUS_INIT_POINT(null, ConnectionState.IN_GAME_STATES),
    EX_EVOLVE_PET(null, ConnectionState.IN_GAME_STATES),
    EX_ENCHANT_HOMUNCULUS_SKILL(null, ConnectionState.IN_GAME_STATES),
    EX_HOMUNCULUS_ENCHANT_EXP(null, ConnectionState.IN_GAME_STATES),
    EX_TELEPORT_FAVORITES_LIST(ExRequestTeleportFavoriteList::new, ConnectionState.IN_GAME_STATES),
    EX_TELEPORT_FAVORITES_UI_TOGGLE(ExRequestTeleportFavoritesUIToggle::new, ConnectionState.IN_GAME_STATES),
    EX_TELEPORT_FAVORITES_ADD_DEL(ExRequestTeleportFavoritesAddDel::new, ConnectionState.IN_GAME_STATES),
    EX_ANTIBOT(null, ConnectionState.IN_GAME_STATES),
    EX_DPSVR(null, ConnectionState.IN_GAME_STATES),
    EX_TENPROTECT_DECRYPT_ERROR(null, ConnectionState.IN_GAME_STATES),
    EX_NET_LATENCY(null, ConnectionState.IN_GAME_STATES),
    EX_MABLE_GAME_OPEN(null, ConnectionState.IN_GAME_STATES),
    EX_MABLE_GAME_ROLL_DICE(null, ConnectionState.IN_GAME_STATES),
    EX_MABLE_GAME_POPUP_OK(null, ConnectionState.IN_GAME_STATES),
    EX_MABLE_GAME_RESET(null, ConnectionState.IN_GAME_STATES),
    EX_MABLE_GAME_CLOSE(null, ConnectionState.IN_GAME_STATES),
    EX_RETURN_TO_ORIGIN(null, ConnectionState.IN_GAME_STATES),
    EX_PK_PENALTY_LIST(null, ConnectionState.IN_GAME_STATES),
    EX_PK_PENALTY_LIST_ONLY_LOC(null, ConnectionState.IN_GAME_STATES)
    ;

    static final ExIncomingPackets[] PACKET_ARRAY = values();

    private final boolean hasExtension;
    private final Supplier<ClientPacket> incomingPacketFactory;
    private final EnumSet<ConnectionState> connectionStates;

    ExIncomingPackets(Supplier<ClientPacket> incomingPacketFactory, boolean hasExtension, EnumSet<ConnectionState> connectionStates) {
        this.incomingPacketFactory = Objects.requireNonNullElse(incomingPacketFactory, NULL_PACKET_SUPLIER);
        this.connectionStates = connectionStates;
        this.hasExtension = hasExtension;
    }

    ExIncomingPackets(Supplier<ClientPacket> incomingPacketFactory, EnumSet<ConnectionState> connectionStates) {
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
    public boolean canHandleState(ConnectionState state) {
        return connectionStates.contains(state);
    }

    @Override
    public boolean hasExtension() {
        return hasExtension;
    }

    @Override
    public PacketFactory handleExtension(PacketBuffer buffer) {
        if (EX_USER_BOOKMARK == this) {
            return handleBookMarkPaket(buffer);
        }
        return NULLABLE_PACKET_FACTORY;
    }

    private PacketFactory handleBookMarkPaket(PacketBuffer packet) {
        return switch (packet.readInt()) {
            case 0 -> new DynamicPacketFactory(RequestBookMarkSlotInfo::new);
            case 1 -> new DynamicPacketFactory(RequestSaveBookMarkSlot::new);
            case 2 -> new DynamicPacketFactory(RequestModifyBookMarkSlot::new);
            case 3 -> new DynamicPacketFactory(RequestDeleteBookMarkSlot::new);
            case 4 -> new DynamicPacketFactory(RequestTeleportBookMark::new);
            case 5 -> new DynamicPacketFactory(RequestChangeBookMarkSlot::new);
            default -> NULLABLE_PACKET_FACTORY;
        };
    }

    @Override
    public EnumSet<ConnectionState> getConnectionStates() {
        return connectionStates;
    }


    static class DynamicPacketFactory implements PacketFactory {

        private final Supplier<ClientPacket> supplier;

        DynamicPacketFactory(Supplier<ClientPacket> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean canHandleState(ConnectionState state) {
            return true;
        }

        @Override
        public ClientPacket newIncomingPacket() {
            return supplier.get();
        }
    }
}
