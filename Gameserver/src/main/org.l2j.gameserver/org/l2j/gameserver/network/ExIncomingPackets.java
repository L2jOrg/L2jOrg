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
import org.l2j.gameserver.network.clientpackets.equipmentupgrade.RequestUpgradeSystemResult;
import org.l2j.gameserver.network.clientpackets.friend.RequestFriendDetailInfo;
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
import org.l2j.gameserver.network.clientpackets.training.NotifyTrainingRoomEnd;
import org.l2j.gameserver.network.clientpackets.vip.ExRequestVipInfo;
import org.l2j.gameserver.network.clientpackets.vip.RequestVipLuckGameInfo;
import org.l2j.gameserver.network.clientpackets.vip.RequestVipProductList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public enum ExIncomingPackets implements PacketFactory {
    REQUEST_GOTO_LOBBY(0x33, RequestGotoLobby::new, ConnectionState.AUTHENTICATED),
    REQUEST_EX_2ND_PASSWORD_CHECK(0xA6, RequestEx2ndPasswordCheck::new, ConnectionState.AUTHENTICATED),
    REQUEST_EX_2ND_PASSWORD_VERIFY(0xA7, RequestEx2ndPasswordVerify::new, ConnectionState.AUTHENTICATED),
    REQUEST_EX_2ND_PASSWORD_REQ(0xA8, RequestEx2ndPasswordReq::new, ConnectionState.AUTHENTICATED),
    REQUEST_CHARACTER_NAME_CREATABLE(0xA9, RequestCharacterNameCreatable::new, ConnectionState.AUTHENTICATED),
    REQUEST_MANOR_LIST(0x01, RequestManorList::new, ConnectionState.JOINING_GAME),
    REQUEST_PROCEDURE_CROP_LIST(0x02, RequestProcureCropList::new, ConnectionState.IN_GAME),
    REQUEST_SET_SEED(0x03, RequestSetSeed::new, ConnectionState.IN_GAME),
    REQUEST_SET_CROP(0x04, RequestSetCrop::new, ConnectionState.IN_GAME),
    REQUEST_WRITE_HERO_WORDS(0x05, RequestWriteHeroWords::new, ConnectionState.IN_GAME),
    REQUEST_EX_ASK_JOIN_MPCC(0x06, RequestExAskJoinMPCC::new, ConnectionState.IN_GAME),
    REQUEST_EX_ACCEPT_JOIN_MPCC(0x07, RequestExAcceptJoinMPCC::new, ConnectionState.IN_GAME),
    REQUEST_EX_OUST_FROM_MPCC(0x08, RequestExOustFromMPCC::new, ConnectionState.IN_GAME),
    REQUEST_OUST_FROM_PARTY_ROOM(0x09, RequestOustFromPartyRoom::new, ConnectionState.IN_GAME),
    REQUEST_DISMISS_PARTY_ROOM(0x0A, RequestDismissPartyRoom::new, ConnectionState.IN_GAME),
    REQUEST_WITHDRAW_PARTY_ROOM(0x0B, RequestWithdrawPartyRoom::new, ConnectionState.IN_GAME),
    REQUEST_CHANGE_PARTY_LEADER(0x0C, RequestChangePartyLeader::new, ConnectionState.IN_GAME),
    REQUEST_AUTO_SOULSHOT(0x0D, RequestAutoSoulShot::new, ConnectionState.IN_GAME),
    REQUEST_EX_ENCHANT_SKILL_INFO(0x0E, RequestExEnchantSkillInfo::new, ConnectionState.IN_GAME),
    REQUEST_EX_ENCHANT_SKILL(0x0F, RequestExEnchantSkill::new, ConnectionState.IN_GAME),
    REQUEST_EX_PLEDGE_CREST_LARGE(0x10, RequestExPledgeCrestLarge::new, ConnectionState.IN_GAME),
    REQUEST_EX_SET_PLEDGE_CREST_LARGE(0x11, RequestExSetPledgeCrestLarge::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_SET_ACADEMY_MASTER(0x12, RequestPledgeSetAcademyMaster::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_POWER_GRADE_LIST(0x13, RequestPledgePowerGradeList::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_MEMBER_POWER_INFO(0x14, RequestPledgeMemberPowerInfo::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_SET_MEMBER_POWER_GRADE(0x15, RequestPledgeSetMemberPowerGrade::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_MEMBER_INFO(0x16, RequestPledgeMemberInfo::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_WAR_LIST(0x17, RequestPledgeWarList::new, ConnectionState.IN_GAME),
    REQUEST_EX_FISH_RANKING(0x18, RequestExFishRanking::new, ConnectionState.IN_GAME),
    REQUEST_PCCAFE_COUPON_USE(0x19, RequestPCCafeCouponUse::new, ConnectionState.IN_GAME),
    REQUEST_SERVER_LOGIN(0x1A, null, ConnectionState.IN_GAME),
    REQUEST_DUEL_START(0x1B, RequestDuelStart::new, ConnectionState.IN_GAME),
    REQUEST_DUAL_ANSWER_START(0x1C, RequestDuelAnswerStart::new, ConnectionState.IN_GAME),
    REQUEST_EX_SET_TUTORIAL(0x1D, null, ConnectionState.IN_GAME),
    REQUEST_EX_RQ_ITEM_LINK(0x1E, RequestExRqItemLink::new, ConnectionState.IN_GAME),
    CANNOT_MOVE_ANYMORE_AIR_SHIP(0x1F, null, ConnectionState.IN_GAME),
    MOVE_TO_LOCATION_IN_AIR_SHIP(0x20, null, ConnectionState.IN_GAME),
    REQUEST_KEY_MAPPING(0x21, RequestKeyMapping::new, ConnectionState.JOINING_GAME),
    REQUEST_SAVE_KEY_MAPPING(0x22, RequestSaveKeyMapping::new, ConnectionState.IN_GAME),
    REQUEST_EX_REMOVE_ITEM_ATTRIBUTE(0x23, RequestExRemoveItemAttribute::new, ConnectionState.IN_GAME),
    REQUEST_SAVE_INVENTORY_ORDER(0x24, RequestSaveInventoryOrder::new, ConnectionState.IN_GAME),
    REQUEST_EXIT_PARTY_MATCHING_WAITING_ROOM(0x25, RequestExitPartyMatchingWaitingRoom::new, ConnectionState.IN_GAME),
    REQUEST_CONFIRM_TARGET_ITEM(0x26, RequestConfirmTargetItem::new, ConnectionState.IN_GAME),
    REQUEST_CONFIRM_REFINER_ITEM(0x27, RequestConfirmRefinerItem::new, ConnectionState.IN_GAME),
    REQUEST_CONFIRM_GEMSTONE(0x28, RequestConfirmGemStone::new, ConnectionState.IN_GAME),
    REQUEST_OLYMPIAD_OBSERVER_END(0x29, RequestOlympiadObserverEnd::new, ConnectionState.IN_GAME),
    REQUEST_CURSED_WEAPON_LIST(0x2A, RequestCursedWeaponList::new, ConnectionState.IN_GAME),
    REQUEST_CURSED_WEAPON_LOCATION(0x2B, RequestCursedWeaponLocation::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_REORGANIZE_MEMBER(0x2C, RequestPledgeReorganizeMember::new, ConnectionState.IN_GAME),
    REQUEST_EX_MPCC_SHOW_PARTY_MEMBERS_INFO(0x2D, RequestExMPCCShowPartyMembersInfo::new, ConnectionState.IN_GAME),
    REQUEST_OLYMPIAD_MATCH_LIST(0x2E, RequestOlympiadMatchList::new, ConnectionState.IN_GAME),
    REQUEST_ASK_JOIN_PARTY_ROOM(0x2F, RequestAskJoinPartyRoom::new, ConnectionState.IN_GAME),
    ANSWER_JOIN_PARTY_ROOM(0x30, AnswerJoinPartyRoom::new, ConnectionState.IN_GAME),
    REQUEST_LIST_PARTY_MATCHING_WAITING_ROOM(0x31, RequestListPartyMatchingWaitingRoom::new, ConnectionState.IN_GAME),
    REQUEST_EX_ENCHANT_ITEM_ATTRIBUTE(0x32, RequestExEnchantItemAttribute::new, ConnectionState.IN_GAME),
    MOVE_TO_LOCATION_AIR_SHIP(0x35, null, ConnectionState.IN_GAME),
    REQUEST_BID_ITEM_AUCTION(0x36, RequestBidItemAuction::new, ConnectionState.IN_GAME),
    REQUEST_INFO_ITEM_AUCTION(0x37, RequestInfoItemAuction::new, ConnectionState.IN_GAME),
    REQUEST_EX_CHANGE_NAME(0x38, RequestExChangeName::new, ConnectionState.IN_GAME),
    REQUEST_ALL_CASTLE_INFO(0x39, RequestAllCastleInfo::new, ConnectionState.IN_GAME),
    REQUEST_ALL_FORTRESS_INFO(0x3A, RequestAllFortressInfo::new, ConnectionState.IN_GAME),
    REQUEST_ALL_AGIT_INGO(0x3B, RequestAllAgitInfo::new, ConnectionState.IN_GAME),
    REQUEST_FORTRESS_SIEGE_INFO(0x3C, RequestFortressSiegeInfo::new, ConnectionState.IN_GAME),
    REQUEST_GET_BOSS_RECORD(0x3D, RequestGetBossRecord::new, ConnectionState.IN_GAME),
    REQUEST_REFINE(0x3E, RequestRefine::new, ConnectionState.IN_GAME),
    REQUEST_CONFIRM_CANCEL_ITEM(0x3F, RequestConfirmCancelItem::new, ConnectionState.IN_GAME),
    REQUEST_REFINE_CANCEL(0x40, RequestRefineCancel::new, ConnectionState.IN_GAME),
    REQUEST_EX_MAGIC_SKILL_USE_GROUND(0x41, RequestExMagicSkillUseGround::new, ConnectionState.IN_GAME),
    REQUEST_DUEL_SURRENDER(0x42, RequestDuelSurrender::new, ConnectionState.IN_GAME),
    REQUEST_EX_ENCHANT_SKILL_INFO_DETAIL(0x43, RequestExEnchantSkillInfoDetail::new, ConnectionState.IN_GAME),
    REQUEST_FORTRESS_MAP_INFO(0x45, RequestFortressMapInfo::new, ConnectionState.IN_GAME),
    REQUEST_PVP_MATCH_RECORD(0x46, RequestPVPMatchRecord::new, ConnectionState.IN_GAME),
    SET_PRIVATE_STORE_WHOLE_MSG(0x47, SetPrivateStoreWholeMsg::new, ConnectionState.IN_GAME),
    REQUEST_DISPEL(0x48, RequestDispel::new, ConnectionState.IN_GAME),
    REQUEST_EX_TRY_TO_PUT_ENCHANT_TARGET_ITEM(0x49, RequestExTryToPutEnchantTargetItem::new, ConnectionState.IN_GAME),
    REQUEST_EX_TRY_TO_PUT_ENCHANT_SUPPORT_ITEM(0x4A, RequestExTryToPutEnchantSupportItem::new, ConnectionState.IN_GAME),
    REQUEST_EX_CANCEL_ENCHANT_ITEM(0x4B, RequestExCancelEnchantItem::new, ConnectionState.IN_GAME),
    REQUEST_CHANGE_NICKNAME_COLOR(0x4C, RequestChangeNicknameColor::new, ConnectionState.IN_GAME),
    REQUEST_RESET_NICKNAME(0x4D, RequestResetNickname::new, ConnectionState.IN_GAME),
    EX_BOOKMARK_PACKET(0x4E, null, true, ConnectionState.IN_GAME),
    REQUEST_WITHDRAW_PREMIUM_ITEM(0x4F, RequestWithDrawPremiumItem::new, ConnectionState.IN_GAME),
    REQUEST_EX_JUMP(0x50, null, ConnectionState.IN_GAME),
    REQUEST_EX_START_SHOW_CRATAE_CUBE_RANK(0x51, RequestStartShowKrateisCubeRank::new, ConnectionState.IN_GAME),
    REQUEST_EX_STOP_SHOW_CRATAE_CUBE_RANK(0x52, RequestStopShowKrateisCubeRank::new, ConnectionState.IN_GAME),
    NOTIFY_START_MINI_GAME(0x53, null, ConnectionState.IN_GAME),
    REQUEST_EX_JOIN_DOMINION_WAR(0x54, null, ConnectionState.IN_GAME),
    REQUEST_EX_DOMINION_INFO(0x55, null, ConnectionState.IN_GAME),
    REQUEST_EX_CLEFT_ENTER(0x56, null, ConnectionState.IN_GAME),
    REQUEST_EX_CUBE_GAME_CHANGE_TEAM(0x57, RequestExCubeGameChangeTeam::new, ConnectionState.IN_GAME),
    END_SCENE_PLAYER(0x58, EndScenePlayer::new, ConnectionState.IN_GAME),
    REQUEST_EX_CUBE_GAME_READY_ANSWER(0x59, RequestExCubeGameReadyAnswer::new, ConnectionState.IN_GAME),
    REQUEST_EX_LIST_MPCC_WAITING(0x5A, RequestExListMpccWaiting::new, ConnectionState.IN_GAME),
    REQUEST_EX_MANAGE_MPCC_ROOM(0x5B, RequestExManageMpccRoom::new, ConnectionState.IN_GAME),
    REQUEST_EX_JOIN_MPCC_ROOM(0x5C, RequestExJoinMpccRoom::new, ConnectionState.IN_GAME),
    REQUEST_EX_OUST_FROM_MPCC_ROOM(0x5D, RequestExOustFromMpccRoom::new, ConnectionState.IN_GAME),
    REQUEST_EX_DISMISS_MPCC_ROOM(0x5E, RequestExDismissMpccRoom::new, ConnectionState.IN_GAME),
    REQUEST_EX_WITHDRAW_MPCC_ROOM(0x5F, RequestExWithdrawMpccRoom::new, ConnectionState.IN_GAME),
    REQUEST_SEED_PHASE(0x60, RequestSeedPhase::new, ConnectionState.IN_GAME),
    REQUEST_EX_MPCC_PARTYMASTER_LIST(0x61, RequestExMpccPartymasterList::new, ConnectionState.IN_GAME),
    REQUEST_POST_ITEM_LIST(0x62, RequestPostItemList::new, ConnectionState.IN_GAME),
    REQUEST_SEND_POST(0x63, RequestSendPost::new, ConnectionState.IN_GAME),
    REQUEST_RECEIVED_POST_LIST(0x64, RequestReceivedPostList::new, ConnectionState.IN_GAME),
    REQUEST_DELETE_RECEIVED_POST(0x65, RequestDeleteReceivedPost::new, ConnectionState.IN_GAME),
    REQUEST_RECEIVED_POST(0x66, RequestReceivedPost::new, ConnectionState.IN_GAME),
    REQUEST_POST_ATTACHMENT(0x67, RequestPostAttachment::new, ConnectionState.IN_GAME),
    REQUEST_REJECT_POST_ATTACHMENT(0x68, RequestRejectPostAttachment::new, ConnectionState.IN_GAME),
    REQUEST_SENT_POST_LIST(0x69, RequestSentPostList::new, ConnectionState.IN_GAME),
    REQUEST_DELETE_SENT_POST(0x6A, RequestDeleteSentPost::new, ConnectionState.IN_GAME),
    REQUEST_SENT_POST(0x6B, RequestSentPost::new, ConnectionState.IN_GAME),
    REQUEST_CANCEL_POST_ATTACHMENT(0x6C, RequestCancelPostAttachment::new, ConnectionState.IN_GAME),
    REQUEST_SHOW_NEW_USER_PETITION(0x6D, null, ConnectionState.IN_GAME),
    REQUEST_SHOW_STEP_TWO(0x6E, null, ConnectionState.IN_GAME),
    REQUEST_SHOW_STEP_THREE(0x6F, null, ConnectionState.IN_GAME),
    EX_CONNECT_TO_RAID_SERVER(0x70, null, ConnectionState.IN_GAME),
    EX_RETURN_FROM_RAID_SERVER(0x71, null, ConnectionState.IN_GAME),
    REQUEST_REFUND_ITEM(0x72, RequestRefundItem::new, ConnectionState.IN_GAME),
    REQUEST_BUI_SELL_UI_CLOSE(0x73, RequestBuySellUIClose::new, ConnectionState.IN_GAME),
    REQUEST_EX_EVENT_MATCH_OBSERVER_END(0x74, null, ConnectionState.IN_GAME),
    REQUEST_PARTY_LOOT_MODIFICATION(0x75, RequestPartyLootModification::new, ConnectionState.IN_GAME),
    ANSWER_PARTY_LOOT_MODIFICATION(0x76, AnswerPartyLootModification::new, ConnectionState.IN_GAME),
    ANSWER_COUPLE_ACTION(0x77, AnswerCoupleAction::new, ConnectionState.IN_GAME),
    BR_EVENT_RANKER_LIST(0x78, BrEventRankerList::new, ConnectionState.IN_GAME),
    REQUEST_ASK_MEMBER_SHIP(0x79, null, ConnectionState.IN_GAME),
    REQUEST_ADD_EXPAND_QUEST_ALARM(0x7A, RequestAddExpandQuestAlarm::new, ConnectionState.IN_GAME),
    REQUEST_VOTE_NEW(0x7B, RequestVoteNew::new, ConnectionState.IN_GAME),
    REQUEST_SHUTTLE_GET_ON(0x7C, RequestShuttleGetOn::new, ConnectionState.IN_GAME),
    REQUEST_SHUTTLE_GET_OFF(0x7D, RequestShuttleGetOff::new, ConnectionState.IN_GAME),
    MOVE_TO_LOCATION_IN_SHUTTLE(0x7E, MoveToLocationInShuttle::new, ConnectionState.IN_GAME),
    CANNOT_MOVE_ANYMORE_IN_SHUTTLE(0x7F, CannotMoveAnymoreInShuttle::new, ConnectionState.IN_GAME),
    REQUEST_AGIT_ACTION(0x80, null, ConnectionState.IN_GAME), // TODO: Implement / HANDLE SWITCH
    REQUEST_EX_ADD_CONTACT_TO_CONTACT_LIST(0x81, RequestExAddContactToContactList::new, ConnectionState.IN_GAME),
    REQUEST_EX_DELETE_CONTACT_FROM_CONTACT_LIST(0x82, RequestExDeleteContactFromContactList::new, ConnectionState.IN_GAME),
    REQUEST_EX_SHOW_CONTACT_LIST(0x83, RequestExShowContactList::new, ConnectionState.IN_GAME),
    REQUEST_EX_FRIEND_LIST_EXTENDED(0x84, RequestExFriendListExtended::new, ConnectionState.IN_GAME),
    REQUEST_EX_OLYMPIAD_MATCH_LIST_REFRESH(0x85, RequestExOlympiadMatchListRefresh::new, ConnectionState.IN_GAME),
    REQUEST_BR_GAME_POINT(0x86, RequestBRGamePoint::new, ConnectionState.IN_GAME),
    REQUEST_BR_PRODUCT_LIST(0x87, RequestBRProductList::new, ConnectionState.IN_GAME),
    REQUEST_BR_PRODUCT_INFO(0x88, RequestBRProductInfo::new, ConnectionState.IN_GAME),
    REQUEST_BR_BUI_PRODUCT(0x89, RequestBRBuyProduct::new, ConnectionState.IN_GAME),
    REQUEST_BR_RECENT_PRODUCT_LIST(0x8A, RequestBRRecentProductList::new, ConnectionState.IN_GAME),
    REQUEST_BR_MINI_GAME_LOAD_SCORES(0x8B, null, ConnectionState.IN_GAME),
    REQUEST_BR_MINI_GAME_INSERT_SCORE(0x8C, null, ConnectionState.IN_GAME),
    REQUEST_EX_BR_LECTURE_MARK(0x8D, null, ConnectionState.IN_GAME),
    REQUEST_CRYSTALLIZE_ESTIMATE(0x8E, RequestCrystallizeEstimate::new, ConnectionState.IN_GAME),
    REQUEST_CRYSTALLIZE_ITEM_CANCEL(0x8F, RequestCrystallizeItemCancel::new, ConnectionState.IN_GAME),
    REQUEST_SCENE_EX_ESCAPE_SCENE(0x90, RequestExEscapeScene::new, ConnectionState.IN_GAME),
    REQUEST_FLY_MOVE(0x91, null, ConnectionState.IN_GAME), // RequestFlyMove - Sayune is not available on classic yet
    REQUEST_SURRENDER_PLEDGE_WAR_EX(0x92, null, ConnectionState.IN_GAME),
    REQUEST_DYNAMIC_QUEST_ACTION(0x93, null, ConnectionState.IN_GAME), // TODO: Implement / HANDLE SWITCH
    REQUEST_FRIEND_DETAIL_INFO(0x94, RequestFriendDetailInfo::new, ConnectionState.IN_GAME),
    REQUEST_UPDATE_FRIEND_MEMO(0x95, null, ConnectionState.IN_GAME),
    REQUEST_UPDATE_BLOCK_MEMO(0x96, null, ConnectionState.IN_GAME),
    REQUEST_INZONE_PARTY_INFO_HISTORY(0x97, null, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_REGISTRABLE_ITEM_LIST(0x98, RequestCommissionRegistrableItemList::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_INFO(0x99, RequestCommissionInfo::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_REGISTER(0x9A, RequestCommissionRegister::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_CANCEL(0x9B, RequestCommissionCancel::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_DELETE(0x9C, RequestCommissionDelete::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_LIST(0x9D, RequestCommissionList::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_BUY_INFO(0x9E, RequestCommissionBuyInfo::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_BUY_ITEM(0x9F, RequestCommissionBuyItem::new, ConnectionState.IN_GAME),
    REQUEST_COMMISSION_REGISTERED_ITEM(0xA0, RequestCommissionRegisteredItem::new, ConnectionState.IN_GAME),
    REQUEST_CALL_TO_CHANGE_CLASS(0xA1, null, ConnectionState.IN_GAME),
    REQUEST_CHANGE_TO_AWAKENED_CLASS(0xA2, RequestChangeToAwakenedClass::new, ConnectionState.IN_GAME),
    REQUEST_WORLD_STATISTICS(0xA3, null, ConnectionState.IN_GAME),
    REQUEST_USER_STATISTICS(0xA4, null, ConnectionState.IN_GAME),
    REQUEST_24HZ_SESSION_ID(0xA5, null, ConnectionState.IN_GAME),
    REQUEST_GOODS_INVENTORY_INFO(0xAA, null, ConnectionState.IN_GAME),
    REQUEST_GOODS_INVENTORY_ITEM(0xAB, null, ConnectionState.IN_GAME),
    REQUEST_FIRST_PLAY_START(0xAC, null, ConnectionState.IN_GAME),
    REQUEST_FLY_MOVE_START(0xAD, null, ConnectionState.IN_GAME), // RequestFlyMoveStart - Sayune is not available on classic yet
    REQUEST_HARDWARE_INFO(0xAE, RequestHardWareInfo::new, ConnectionState.values()),
    SEND_CHANGE_ATTRIBUTE_TARGET_ITEM(0xB0, SendChangeAttributeTargetItem::new, ConnectionState.IN_GAME),
    REQUEST_CHANGE_ATTRIBUTE_ITEM(0xB1, RequestChangeAttributeItem::new, ConnectionState.IN_GAME),
    REQUEST_CHANGE_ATTRIBUTE_CANCEL(0xB2, RequestChangeAttributeCancel::new, ConnectionState.IN_GAME),
    REQUEST_BR_PRESENT_BUY_PRODUCT(0xB3, RequestBRPresentBuyProduct::new, ConnectionState.IN_GAME),
    CONFIRM_MENTEE_ADD(0xB4, ConfirmMenteeAdd::new, ConnectionState.IN_GAME),
    REQUEST_MENTOR_CANCEL(0xB5, RequestMentorCancel::new, ConnectionState.IN_GAME),
    REQUEST_MENTOR_LIST(0xB6, RequestMentorList::new, ConnectionState.IN_GAME),
    REQUEST_MENTEE_ADD(0xB7, RequestMenteeAdd::new, ConnectionState.IN_GAME),
    REQUEST_MENTEE_WAITING_LIST(0xB8, RequestMenteeWaitingList::new, ConnectionState.IN_GAME),
    REQUEST_CLAN_ASK_JOIN_BY_NAME(0xB9, RequestClanAskJoinByName::new, ConnectionState.IN_GAME),
    REQUEST_IN_ZONE_WAITING_TIME(0xBA, RequestInzoneWaitingTime::new, ConnectionState.IN_GAME),
    REQUEST_JOIN_CURIOUS_HOUSE(0xBB, RequestJoinCuriousHouse::new, ConnectionState.IN_GAME),
    REQUEST_CANCEL_CURIOUS_HOUSE(0xBC, RequestCancelCuriousHouse::new, ConnectionState.IN_GAME),
    REQUEST_LEAVE_CURIOUS_HOUSE(0xBD, null, ConnectionState.IN_GAME),
    REQUEST_OBSERVING_LIST_CURIOUS_HOUSE(0xBE, null, ConnectionState.IN_GAME),
    REQUEST_OBSERVING_CURIOUS_HOUSE(0xBF, null, ConnectionState.IN_GAME),
    REQUEST_LEAVE_OBSERVING_CURIOUS_HOUSE(0xC0, null, ConnectionState.IN_GAME),
    REQUEST_CURIOUS_HOUSE_HTML(0xC1, RequestCuriousHouseHtml::new, ConnectionState.IN_GAME),
    REQUEST_CURIOUS_HOUSE_RECORD(0xC2, null, ConnectionState.IN_GAME),
    EX_SYSSTRING(0xC3, null, ConnectionState.IN_GAME),
    REQUEST_EX_TRY_TO_PUT_SHAPE_SHIFTING_TARGET_ITEM(0xC4, null, ConnectionState.IN_GAME), // RequestExTryToPutShapeShiftingTargetItem - Appearance Stone not used on classic
    REQUEST_EX_TRY_TO_PUT_SHAPE_SHIFTING_ENCHANT_SUPPORT_ITEM(0xC5, null, ConnectionState.IN_GAME), // RequestExTryToPutShapeShiftingEnchantSupportItem - Appearance Stone not used on classic
    REQUEST_EX_CANCEL_SHAPE_SHIFTING_ITEM(0xC6, null, ConnectionState.IN_GAME), // RequestExCancelShape_Shifting_Item - Appearance Stone not used on classic
    REQUEST_SHAPE_SHIFTING_ITEM(0xC7, null, ConnectionState.IN_GAME), // RequestShapeShiftingItem - Appearance Stone not used on classic
    NC_GUARD_SEND_DATA_TO_SERVER(0xC8, DISCARD, ConnectionState.IN_GAME),
    REQUEST_EVENT_KALIE_TOKEN(0xC9, null, ConnectionState.IN_GAME),
    REQUEST_SHOW_BEAUTY_LIST(0xCA, RequestShowBeautyList::new, ConnectionState.IN_GAME),
    REQUEST_REGIST_BEAUTY(0xCB, RequestRegistBeauty::new, ConnectionState.IN_GAME),
    REQUEST_SHOW_RESET_SHOP_LIST(0xCD, RequestShowResetShopList::new, ConnectionState.IN_GAME),
    NET_PING(0xCE, null, ConnectionState.IN_GAME),
    REQUEST_BR_ADD_BASKET_PRODUCT_INFO(0xCF, null, ConnectionState.IN_GAME),
    REQUEST_BR_DELETE_BASKET_PRODUCT_INFO(0xD0, null, ConnectionState.IN_GAME),
    REQUEST_EX_EVENT_CAMPAIGN_INFO(0xD2, null, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_RECRUIT_INFO(0xD3, RequestPledgeRecruitInfo::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_RECRUIT_BOARD_SEARCH(0xD4, RequestPledgeRecruitBoardSearch::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_RECRUIT_BOARD_ACCESS(0xD5, RequestPledgeRecruitBoardAccess::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_RECRUIT_BOARD_DETAIL(0xD6, RequestPledgeRecruitBoardDetail::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_WAITING_APPLY(0xD7, RequestPledgeWaitingApply::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_WAITING_APPLIED(0xD8, RequestPledgeWaitingApplied::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_WAITING_LIST(0xD9, RequestPledgeWaitingList::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_WAITING_USER(0xDA, RequestPledgeWaitingUser::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_WAITING_USER_ACCEPT(0xDB, RequestPledgeWaitingUserAccept::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_DRAFT_LIST_SEARCH(0xDC, RequestPledgeDraftListSearch::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_DRAFT_LIST_APPLY(0xDD, RequestPledgeDraftListApply::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_RECRUIT_APPLY_INFO(0xDE, RequestPledgeRecruitApplyInfo::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_JOIN_SYS(0xDF, null, ConnectionState.IN_GAME),
    RESPONSE_PETITION_ALARM(0xE0, null, ConnectionState.IN_GAME),
    NOTIFY_EXIT_BEAUTY_SHOP(0xE1, NotifyExitBeautyShop::new, ConnectionState.IN_GAME),
    REQUEST_REGISTER_XMAS_WISH_CARD(0xE2, null, ConnectionState.IN_GAME),
    REQUEST_EX_ADD_ENCHANT_SCROLL_ITEM(0xE3, RequestExAddEnchantScrollItem::new, ConnectionState.IN_GAME),
    REQUEST_EX_REMOVE_ENCHANT_SUPPORT_ITEM(0xE4, RequestExRemoveEnchantSupportItem::new, ConnectionState.IN_GAME),
    REQUEST_CARD_REWARD(0xE5, null, ConnectionState.IN_GAME),
    REQUEST_DIVIDE_ADENA_START(0xE6, RequestDivideAdenaStart::new, ConnectionState.IN_GAME),
    REQUEST_DIVIDE_ADENA_CANCEL(0xE7, RequestDivideAdenaCancel::new, ConnectionState.IN_GAME),
    REQUEST_DIVIDE_ADENA(0xE8, RequestDivideAdena::new, ConnectionState.IN_GAME),
    REQUEST_ACQUIRE_ABILITY_LIST(0xE9, null, ConnectionState.IN_GAME),
    REQUEST_ABILITY_LIST(0xEA, null, ConnectionState.IN_GAME),
    REQUEST_RESET_ABILITY_POINT(0xEB, null, ConnectionState.IN_GAME),
    REQUEST_CHANGE_ABILITY_POINT(0xEC, null, ConnectionState.IN_GAME),
    REQUEST_STOP_MOVE(0xED, RequestStopMove::new, ConnectionState.IN_GAME),
    REQUEST_ABILITY_WND_OPEN(0xEE, null, ConnectionState.IN_GAME),
    REQUEST_ABILITY_WND_CLOSE(0xEF, null, ConnectionState.IN_GAME),
    REQUEST_LUCKY_GAME_START_INFO(0xF0, RequestLuckyGameStartInfo::new, ConnectionState.IN_GAME),
    REQUEST_LUCKY_GAME_PLAY(0xF1, RequestLuckyGamePlay::new, ConnectionState.IN_GAME),
    NOTIFY_TRAINING_ROOM_END(0xF2, NotifyTrainingRoomEnd::new, ConnectionState.IN_GAME),
    REQUEST_NEW_ENCHANT_PUSH_ONE(0xF3, RequestNewEnchantPushOne::new, ConnectionState.IN_GAME),
    REQUEST_NEW_ENCHANT_REMOVE_ONE(0xF4, RequestNewEnchantRemoveOne::new, ConnectionState.IN_GAME),
    REQUEST_NEW_ENCHANT_PUSH_TWO(0xF5, RequestNewEnchantPushTwo::new, ConnectionState.IN_GAME),
    REQUEST_NEW_ENCHANT_REMOVE_TWO(0xF6, RequestNewEnchantRemoveTwo::new, ConnectionState.IN_GAME),
    REQUEST_NEW_ENCHANT_CLOSE(0xF7, RequestNewEnchantClose::new, ConnectionState.IN_GAME),
    REQUEST_NEW_ENCHANT_TRY(0xF8, RequestNewEnchantTry::new, ConnectionState.IN_GAME),
    REQUEST_NEW_ENCHANT_RETRY_TO_PUT_ITEMS(0xF9, RequestNewEnchantRetryToPutItems::new, ConnectionState.IN_GAME),
    EX_REQUEST_CARD_REWARD_LIST(0xFA, null, ConnectionState.IN_GAME), //UNetworkHandler::ExRequestCardRewardList
    EX_REQUEST_ACCOUNT_ATTENDANCE_INFO(0xFB,null, ConnectionState.IN_GAME), //UNetworkHandler::ExRequestAccountAttendanceInfo
    EX_REQUEST_ACCOUNT_ATTENDANCE_REWARD(0xFC,null, ConnectionState.IN_GAME), //UNetworkHandler::ExRequestAccountAttendanceReward
    REQUEST_TARGET_ACTION_MENU(0xFD, RequestTargetActionMenu::new, ConnectionState.IN_GAME),
    EX_SEND_SELECTED_QUEST_ZONE_ID(0xFE, ExSendSelectedQuestZoneID::new, ConnectionState.IN_GAME),
    REQUEST_ALCHEMY_SKILL_LIST(0xFF, null, ConnectionState.IN_GAME), // RequestAlchemySkillList not exists on Classic
    REQUEST_ALCHEMY_TRY_MIX_CUBE(0x100, null, ConnectionState.IN_GAME),
    REQUEST_ALCHEMY_CONVERSION(0x101, null, ConnectionState.IN_GAME),
    SEND_EXECUTED_UI_EVENTS_COUNT(0x102, null, ConnectionState.IN_GAME),
    EX_SEND_CLIENT_INI(0x103, DISCARD, ConnectionState.AUTHENTICATED),
    REQUEST_EX_AUTO_FISH(0x104, ExRequestAutoFish::new, ConnectionState.IN_GAME),
    REQUEST_VIP_ATTENDANCE_ITEM_LIST(0x105, RequestVipAttendanceItemList::new, ConnectionState.IN_GAME),
    REQUEST_VIP_ATTENDANCE_CHECK(0x106, RequestVipAttendanceCheck::new, ConnectionState.IN_GAME),
    REQUEST_ITEM_ENSOUL(0x107, RequestItemEnsoul::new, ConnectionState.IN_GAME),
    REQUEST_CASTLE_WAR_SEASON_REWARD(0x108, null, ConnectionState.IN_GAME),
    REQUEST_VIP_PRODUCT_LIST(0x109, RequestVipProductList::new, ConnectionState.IN_GAME),
    REQUEST_VIP_LUCKY_GAME_INFO(0x10A, RequestVipLuckGameInfo::new, ConnectionState.IN_GAME),
    REQUEST_VIP_LUCKY_GAME_ITEM_LIST(0x10B, null, ConnectionState.IN_GAME),
    REQUEST_VIP_LUCKY_GAME_BONUS(0x10C, null, ConnectionState.IN_GAME),
    EX_REQUEST_VIP_INFO(0x10D, ExRequestVipInfo::new, ConnectionState.IN_GAME),
    REQUEST_CAPTCHA_ANSWER(0x10E, RequestCaptchaAnswer::new, ConnectionState.IN_GAME),
    REQUEST_REFRESH_CAPTCHA_IMAGE(0x10F, RequestRefreshCaptcha::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_SIGN_IN_FOR_OPEN_JOINING_METHOD(0x110, RequestPledgeSignInForOpenJoiningMethod::new, ConnectionState.IN_GAME),
    EX_REQUEST_MATCH_ARENA(0x111, null, ConnectionState.IN_GAME),
    EX_CONFIRM_MATCH_ARENA(0x112, null, ConnectionState.IN_GAME),
    EX_CANCEL_MATCH_ARENA(0x113, null, ConnectionState.IN_GAME),
    EX_CHANGE_CLASS_ARENA(0x114, null, ConnectionState.IN_GAME),
    EX_CONFIRM_CLASS_ARENA(0x115, null, ConnectionState.IN_GAME),
    REQUEST_OPEN_DECO_NPCUI(0x116, null, ConnectionState.IN_GAME),
    REQUEST_CHECK_AGIT_DECO_AVAILABILITY(0x117, null, ConnectionState.IN_GAME),
    REQUEST_USER_FACTION_INFO(0x118, null, ConnectionState.IN_GAME),
    EX_EXIT_ARENA(0x119, null, ConnectionState.IN_GAME),
    REQUEST_EVENT_BALTHUS_TOKEN(0x11A, null, ConnectionState.IN_GAME),
    REQUEST_PARTY_MATCHING_HISTORY(0x11B, null, ConnectionState.IN_GAME),
    EX_ARENA_CUSTOM_NOTIFICATION(0x11C, null, ConnectionState.IN_GAME),
    REQUEST_TODO_LIST(0x11D, RequestTodoList::new, ConnectionState.IN_GAME, ConnectionState.JOINING_GAME),
    REQUEST_TODO_LIST_HTML(0x11E, null, ConnectionState.IN_GAME),
    REQUEST_ONE_DAY_REWARD_RECEIVE(0x11F, RequestOneDayRewardReceive::new, ConnectionState.IN_GAME),
    REQUEST_QUEUE_TICKET(0x120, null, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_BONUS_OPEN(0x121, RequestPledgeBonusOpen::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_BONUS_REWARD_LIST(0x122, RequestPledgeBonusRewardList::new, ConnectionState.IN_GAME),
    REQUEST_PLEDGE_BONUS_REWARD(0x123, RequestPledgeBonusReward::new, ConnectionState.IN_GAME),
    REQUEST_SSO_AUTHN_TOKEN(0x124, null, ConnectionState.IN_GAME),
    REQUEST_QUEUE_TICKET_LOGIN(0x125, null, ConnectionState.IN_GAME),
    REQUEST_BLOCK_MEMO_INFO(0x126, null, ConnectionState.IN_GAME),
    REQUEST_TRY_EN_SOUL_EXTRACTION(0x127, RequestTryEnSoulExtraction::new, ConnectionState.IN_GAME),
    REQUEST_RAIDBOSS_SPAWN_INFO(0x128, RequestRaidBossSpawnInfo::new, ConnectionState.IN_GAME),
    REQUEST_RAID_SERVER_INFO(0x129, RequestRaidServerInfo::new, ConnectionState.IN_GAME),
    REQUEST_SHOW_AGIT_SIEGE_INFO(0x12A, null, ConnectionState.IN_GAME),
    REQUEST_ITEM_AUCTION_STATUS(0x12B, null, ConnectionState.IN_GAME),
    REQUEST_MONSTER_BOOK_OPEN(0x12C, null, ConnectionState.IN_GAME),
    REQUEST_MONSTER_BOOK_CLOSE(0x12D, null, ConnectionState.IN_GAME),
    REQUEST_MONSTER_BOOK_REWARD(0x12E, null, ConnectionState.IN_GAME),
    EXREQUEST_MATCH_GROUP(0x12F, null, ConnectionState.IN_GAME),
    EXREQUEST_MATCH_GROUP_ASK(0x130, null, ConnectionState.IN_GAME),
    EXREQUEST_MATCH_GROUP_ANSWER(0x131, null, ConnectionState.IN_GAME),
    EXREQUEST_MATCH_GROUP_WITHDRAW(0x132, null, ConnectionState.IN_GAME),
    EXREQUEST_MATCH_GROUP_OUST(0x133, null, ConnectionState.IN_GAME),
    EXREQUEST_MATCH_GROUP_CHANGE_MASTER(0x134, null, ConnectionState.IN_GAME),
    REQUEST_UPGRADE_SYSTEM_RESULT(0x135, RequestUpgradeSystemResult::new, ConnectionState.IN_GAME),
    EX_CARD_UPDOWN_PICK_NUMB(0x136, null, ConnectionState.IN_GAME),
    EX_CARD_UPDOWN_GAME_REWARD_REQUEST(0x137, null, ConnectionState.IN_GAME),
    EX_CARD_UPDOWN_GAME_RETRY(0x138, null, ConnectionState.IN_GAME),
    EX_CARD_UPDOWN_GAME_QUIT(0x139, null, ConnectionState.IN_GAME),
    EX_ARENA_RANK_ALL(0x13A, null, ConnectionState.IN_GAME),
    EX_ARENA_MYRANK(0x13B, null, ConnectionState.IN_GAME),
    EX_SWAP_AGATHION_SLOT_ITEMS(0x13C, null, ConnectionState.IN_GAME),
    EX_PLEDGE_CONTRIBUTION_RANK(0x13D, null, ConnectionState.IN_GAME),
    EX_PLEDGE_CONTRIBUTION_INFO(0x13E, null, ConnectionState.IN_GAME),
    EX_PLEDGE_CONTRIBUTION_REWARD(0x13F, null, ConnectionState.IN_GAME),
    EX_PLEDGE_LEVEL_UP(0x140, null, ConnectionState.IN_GAME),
    EX_PLEDGE_MISSION_INFO(0x141, null, ConnectionState.IN_GAME),
    EX_PLEDGE_MISSION_REWARD(0x142, null, ConnectionState.IN_GAME),
    EX_PLEDGE_MASTERY_INFO(0x143, null, ConnectionState.IN_GAME),
    EX_PLEDGE_MASTERY_SET(0x144, null, ConnectionState.IN_GAME),
    EX_PLEDGE_MASTERY_RESET(0x145, null, ConnectionState.IN_GAME),
    EX_PLEDGE_SKILL_INFO(0x146, null, ConnectionState.IN_GAME),
    EX_PLEDGE_SKILL_ACTIVATE(0x147, null, ConnectionState.IN_GAME),
    EX_PLEDGE_ITEM_LIST(0x148, null, ConnectionState.IN_GAME),
    EX_PLEDGE_ITEM_ACTIVATE(0x149, null, ConnectionState.IN_GAME),
    EX_PLEDGE_ANNOUNCE(0x14A, null, ConnectionState.IN_GAME),
    EX_PLEDGE_ANNOUNCE_SET(0x14B, null, ConnectionState.IN_GAME),
    EX_CREATE_PLEDGE(0x14C, null, ConnectionState.IN_GAME),
    EX_PLEDGE_ITEM_INFO(0x14D, null, ConnectionState.IN_GAME),
    EX_PLEDGE_ITEM_BUY(0x14E, null, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_INFO(0x14F, ExElementalSpiritInfo::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_EXTRACT_INFO(0x150, ExElementalSpiritExtractInfo::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_EXTRACT(0x151, ExElementalSpiritExtract::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO(0x152, ExElementalSpiritEvolutionInfo::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_EVOLUTION(0x153, ExElementalSpiritEvolution::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_SET_TALENT(0x154, ExElementalSpiritSetTalent::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_INIT_TALENT(0x155, ExElementalInitTalent::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_ABSORB_INFO(0x156, ExElementalSpiritAbsorbInfo::new, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_ABSORB(0x157, ExElementalSpiritAbsorb::new, ConnectionState.IN_GAME),
    EX_REQUEST_LOCKED_ITEM(0x158, null, ConnectionState.IN_GAME),
    EX_REQUEST_UNLOCKED_ITEM(0x159, null, ConnectionState.IN_GAME),
    EX_LOCKED_ITEM_CANCEL(0x15A, null, ConnectionState.IN_GAME),
    EX_UNLOCKED_ITEM_CANCEL(0x15B, null, ConnectionState.IN_GAME),
    EX_ELEMENTAL_SPIRIT_CHANGE_TYPE(0x15C, ExElementalSpiritChangeType::new, ConnectionState.IN_GAME),
    REQUEST_BLOCK_LIST_FOR_AD(0x15D, ExRequestBlockListForAD::new, ConnectionState.IN_GAME),
    REQUEST_USER_BAN_INFO(0x15E, null, ConnectionState.IN_GAME),
    EX_INTERACT_MODIFY(0x15F, null, ConnectionState.IN_GAME), // 152
    EX_TRY_ENCHANT_ARTIFACT(0x160, null, ConnectionState.IN_GAME), // 152
    EX_XIGN_CODE(0x161, null, ConnectionState.IN_GAME), // 152
    EX_PURCHASE_LIMIT_SHOP_ITEM_BUY(0x206, RequestPurchaseLimitShopItemBuy::new, ConnectionState.IN_GAME), //UNetworkHandler::RequestExPurchaseLimitShopItemBuy
    EX_OPEN_HTML(0x164, ExOpenHtml::new, ConnectionState.IN_GAME),
    EX_REQUEST_CLASS_CHANGE(0x165, ExRequestClassChange::new, ConnectionState.IN_GAME), // 228
    EX_REQUEST_CLASS_CHANGE_VERIFYING(0x166, null, ConnectionState.IN_GAME),
    EX_REQUEST_TELEPORT(0x167, ExRequestTeleport::new, ConnectionState.IN_GAME),
    EX_COSTUME_USE_ITEM(0x168, ExRequestCostumeUseItem::new, ConnectionState.IN_GAME),
    EX_COSTUME_LIST(0x169, ExRequestCostumeList::new, ConnectionState.IN_GAME),
    EX_COSTUME_COLLECTION_SKILL_ACTIVE(0x16A, ExRequestCostumeCollectSkillActive::new, ConnectionState.IN_GAME),
    EX_COSTUME_EVOLUTION(0x16B, ExRequestCostumeEvolution::new, ConnectionState.IN_GAME),
    EX_COSTUME_EXTRACT(0x16C, ExRequestCostumeExtract::new, ConnectionState.IN_GAME),
    EX_COSTUME_LOCK(0x16D, ExRequestCostumeLock::new, ConnectionState.IN_GAME),
    EX_COSTUME_CHANGE_SHORTCUT(0x16E, null, ConnectionState.IN_GAME),
    EX_ACTIVATE_AUTO_SHORTCUT(0x171, ExRequestActivateAutoShortcut::new, ConnectionState.IN_GAME),
    EX_PAYBACK_LIST(0x175, null, ConnectionState.IN_GAME),
    EX_PAYBACK_GIVE_REWARD(0x176, null, ConnectionState.IN_GAME),
    EX_AUTOPLAY_SETTING(0x177, ExAutoPlaySetting::new, ConnectionState.IN_GAME),
    EX_TIME_RESTRICT_FIELD_LIST(0x17F, ExTimedHuntingZoneList::new, ConnectionState.IN_GAME),
    EX_TIME_RESTRICT_FIELD_USER_ENTER(0x180, ExTimedHuntingZoneEnter::new, ConnectionState.IN_GAME),
    EX_RANKING_CHAR_INFO(0x181, ExRankCharInfo::new, ConnectionState.IN_GAME),
    EX_RANKING_CHAR_HISTORY(0x182, ExRequestRankingCharHistory::new, ConnectionState.IN_GAME),
    EX_RANKING_CHAR_RANKERS(0x183, ExRankingCharRankers::new, ConnectionState.IN_GAME),
    EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_ATTACKER_LIST(0x186,null, ConnectionState.IN_GAME),
    EX_PVP_BOOK_LIST(0x18B, ExRequestPvpBookList::new, ConnectionState.IN_GAME),
    EX_PVP_BOOK_KILLER_LOCATION(0x18C, ExRequestKillerLocation::new, ConnectionState.IN_GAME),
    EX_PVP_BOOK_TELEPORT_TO_KILLER(0x18D, ExTeleportToKiller::new, ConnectionState.IN_GAME),
    EX_LETTER_COLLECTOR_TAKE_REWARD(0x18E, null, ConnectionState.IN_GAME),
    EX_SET_STATUS_BONUS(0x18F, ExSetStatusBonus::new, ConnectionState.IN_GAME),
    EX_RESET_STATUS_BONUS(0x190, ExResetStatusBonus::new, ConnectionState.IN_GAME),
    EX_OLYMPIAD_MY_RANKING_INFO(0x191, ExRequestOlympiadMyRank::new, ConnectionState.IN_GAME),
    EX_OLYMPIAD_RANKING_INFO(0x192, ExRequestOlympiadRanking::new, ConnectionState.IN_GAME),
    EX_OLYMPIAD_HERO_AND_LEGEND_INFO(0x193, ExRequestOlympiadHeroes::new, ConnectionState.IN_GAME);

    public static final ExIncomingPackets[] PACKET_ARRAY;

    static {
        final short maxPacketId = (short) Arrays.stream(values()).mapToInt(ExIncomingPackets::getPacketId).max().orElse(0);
        PACKET_ARRAY = new ExIncomingPackets[maxPacketId + 1];
        for (ExIncomingPackets incomingPacket : values()) {
            PACKET_ARRAY[incomingPacket.getPacketId()] = incomingPacket;
        }
    }

    private final boolean hasExtension;
    private int _packetId;
    private Supplier<ClientPacket> _incomingPacketFactory;
    private Set<ConnectionState> _connectionStates;

    ExIncomingPackets(int packetId, Supplier<ClientPacket> incomingPacketFactory, boolean hasExtension, ConnectionState... connectionStates) {
        // packetId is an unsigned short
        if (packetId > 0xFFFF) {
            throw new IllegalArgumentException("packetId must not be bigger than 0xFFFF");
        }
        _packetId = packetId;
        _incomingPacketFactory = incomingPacketFactory != null ? incomingPacketFactory : () -> null;
        _connectionStates = new HashSet<>(Arrays.asList(connectionStates));
        this.hasExtension = hasExtension;
    }

    ExIncomingPackets(int packetId, Supplier<ClientPacket> incomingPacketFactory, ConnectionState... connectionStates) {
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
    public boolean canHandleState(ConnectionState state) {
        return _connectionStates.contains(state);
    }

    @Override
    public boolean hasExtension() {
        return hasExtension;
    }

    @Override
    public PacketFactory handleExtension(PacketBuffer buffer) {
        if (_packetId == 0x4E) {
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
    public Set<ConnectionState> getConnectionStates() {
        return _connectionStates;
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
