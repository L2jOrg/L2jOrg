package l2s.gameserver.network.l2.components;

import java.util.NoSuchElementException;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

/**
 * @author VISTALL
 * @date  13:28/01.12.2010
 */
public enum SystemMsg implements IBroadcastPacket
{
	// Message: The server will be coming down in $s1 second(s).  Please find a safe place to log out.
	THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT(1),
	// Message: $s1 is not currently logged in.
	S1_IS_NOT_CURRENTLY_LOGGED_IN(3),
	// Message: You cannot ask yourself to apply to a clan.
	YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN(4),
	// Message: $s1 is not a clan leader.
	S1_IS_NOT_A_CLAN_LEADER(9),
	// Message: $s1 is already a member of another clan.
	S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN(10),
	// Message: Unable to dissolve: your clan has requested to participate in a castle siege.
	UNABLE_TO_DISSOLVE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE(13),
	// Message: Unable to dissolve: your clan owns one or more castles or hideouts.
	UNABLE_TO_DISSOLVE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS(14),
	// Message: You are not in siege.
	YOU_ARE_NOT_IN_SIEGE(16),
	// Message: Your target is out of range.
	YOUR_TARGET_IS_OUT_OF_RANGE(22),
	// Message: Not enough HP.
	NOT_ENOUGH_HP(23),
	// Message: Not enough MP.
	NOT_ENOUGH_MP(24),
	// Message: Rejuvenating HP.
	REJUVENATING_HP(25),
	// Message: Your casting has been interrupted.
	YOUR_CASTING_HAS_BEEN_INTERRUPTED(27),
	// Message: You have obtained $s2 $s1.
	YOU_HAVE_OBTAINED_S2_S1(29),
	// Message: You have obtained $s1.
	YOU_HAVE_OBTAINED_S1(30),
	// Message: You cannot move while sitting.
	YOU_CANNOT_MOVE_WHILE_SITTING(31),
	// Message: Welcome to the World of Lineage II.
	WELCOME_TO_THE_WORLD_OF_LINEAGE_II(34),
	// Message: You hit for $s1 damage.
	YOU_HIT_FOR_S1_DAMAGE(35),
	// Message: You carefully nock an arrow.
	YOU_CAREFULLY_NOCK_AN_ARROW(41),
	// Message: You use $s1.
	YOU_USE_S1(46),
	// Message: You have equipped your $s1.
	YOU_HAVE_EQUIPPED_YOUR_S1(49),
	// Message: Your target cannot be found.
	YOUR_TARGET_CANNOT_BE_FOUND(50),
	// Message: You cannot use this on yourself.
	YOU_CANNOT_USE_THIS_ON_YOURSELF(51),
	// Message: You have earned $s1 adena.
	YOU_HAVE_EARNED_S1_ADENA(52),
	// Message: You have earned $s2 $s1(s).
	YOU_HAVE_EARNED_S2_S1S(53),
	// Message: You have earned $s1.
	YOU_HAVE_EARNED_S1(54),
	// Message: You have failed to pick up $s1 Adena.
	YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA(55),
	// Message: You have failed to pick up $s1.
	YOU_HAVE_FAILED_TO_PICK_UP_S1(56),
	// Message: Nothing happened.
	NOTHING_HAPPENED(61),
	// Message: This name already exists.
	THIS_NAME_ALREADY_EXISTS(79),
	// Message: You may not attack in a peaceful zone.
	YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE(84),
	// Message: You may not attack this target in a peaceful zone.
	YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE(85),
	// Message: Your level has increased!
	YOUR_LEVEL_HAS_INCREASED(96),
	// Message: This item cannot be discarded.
	THIS_ITEM_CANNOT_BE_DISCARDED(98),
	// Message: This item cannot be traded or sold.
	THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD(99),
	// Message: You cannot exit the game while in combat.
	YOU_CANNOT_EXIT_THE_GAME_WHILE_IN_COMBAT(101),
	// Message: You cannot restart while in combat.
	YOU_CANNOT_RESTART_WHILE_IN_COMBAT(102),
	// Message: You cannot change weapons during an attack.
	YOU_CANNOT_CHANGE_WEAPONS_DURING_AN_ATTACK(104),
	// Message: $c1 has been invited to the party.
	C1_HAS_BEEN_INVITED_TO_THE_PARTY(105),
	// Message: Invalid target.
	INVALID_TARGET(109),
	// Message: $s1’s effect can be felt.
	S1S_EFFECT_CAN_BE_FELT(110),
	// Message: Your shield defense has succeeded.
	YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED(111),
	// Message: You have run out of arrows.
	YOU_HAVE_RUN_OUT_OF_ARROWS(112),
	// Message: $s1 cannot be used due to unsuitable terms.
	S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS(113),
	// Message: You have requested a trade with $c1.
	YOU_HAVE_REQUESTED_A_TRADE_WITH_C1(118),
	// Message: $c1 has denied your request to trade.
	C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE(119),
	// Message: You begin trading with $c1.
	YOU_BEGIN_TRADING_WITH_C1(120),
	// Message: $c1 has confirmed the trade.
	C1_HAS_CONFIRMED_THE_TRADE(121),
	// Message: You may no longer adjust items in the trade because the trade has been confirmed.
	YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED(122),
	// Message: Your trade was successful.
	YOUR_TRADE_WAS_SUCCESSFUL(123),
	// Message: $c1 has cancelled the trade.
	C1_HAS_CANCELLED_THE_TRADE(124),
	// Message: You have been disconnected from the server. Please login again.
	YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_(127),
	// Message: Your inventory is full.
	YOUR_INVENTORY_IS_FULL(129),
	// Message: Your warehouse is full.
	YOUR_WAREHOUSE_IS_FULL(130),
	// Message: There are no more items in the shortcut.
	THERE_ARE_NO_MORE_ITEMS_IN_THE_SHORTCUT(137),
	// Message: $c1 has resisted your $s2.
	C1_HAS_RESISTED_YOUR_S2(139),
	// Message: Your skill was deactivated due to lack of MP.
	YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP(140),
	// Message: You are already trading with someone.
	YOU_ARE_ALREADY_TRADING_WITH_SOMEONE(142),
	// Message: That is an incorrect target.
	THAT_IS_AN_INCORRECT_TARGET(144),
	// Message: That player is not online.
	THAT_PLAYER_IS_NOT_ONLINE(145),
	// Message: You cannot use quest items.
	YOU_CANNOT_USE_QUEST_ITEMS(148),
	// Message: You cannot discard something that far away from you.
	YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU(151),
	// Message: You have invited the wrong target.
	YOU_HAVE_INVITED_THE_WRONG_TARGET(152),
	// Message: $c1 is on another task. Please try again later.
	C1_IS_ON_ANOTHER_TASK(153),
	// Message: Only the leader can give out invitations.
	ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS(154),
	// Message: The party is full.
	THE_PARTY_IS_FULL(155),
	// Message: Your attack has failed.
	YOUR_ATTACK_HAS_FAILED(158),
	// Message: $c1 is a member of another party and cannot be invited.
	C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED(160),
	// Message: That player is not currently online.
	THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE(161),
	// Message: You have moved too far away from the warehouse to perform that action.
	YOU_HAVE_MOVED_TOO_FAR_AWAY_FROM_THE_WAREHOUSE_TO_PERFORM_THAT_ACTION(162),
	// Message: You cannot destroy it because the number is incorrect.
	YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT(163),
	// Message: Waiting for another reply.
	WAITING_FOR_ANOTHER_REPLY(164),
	// Message: You cannot add yourself to your own friend list.
	YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST(165),
	// Message: Friend list is not ready yet. Please register again later.
	FRIEND_LIST_IS_NOT_READY_YET(166),
	// Message: $c1 is already on your friend list.
	C1_IS_ALREADY_ON_YOUR_FRIEND_LIST(167),
	// Message: $c1 has sent a friend request.
	C1_HAS_SENT_A_FRIEND_REQUEST(168),
	// Message: Accept friendship 0/1 (1 to accept, 0 to deny)
	ACCEPT_FRIENDSHIP_01_1_TO_ACCEPT_0_TO_DENY(169),
	// Message: The user who requested to become friends is not found in the game.
	THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME(170),
	// Message: $c1 is not on your friend list.
	C1_IS_NOT_ON_YOUR_FRIEND_LIST(171),
	// Message: You lack the funds needed to pay for this transaction.
	YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION(172),
	// Message: That skill has been de-activated as HP was fully recovered.
	THAT_SKILL_HAS_BEEN_DEACTIVATED_AS_HP_WAS_FULLY_RECOVERED(175),
	// Message: That person is in message refusal mode.
	THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE(176),
	// Message: Message refusal mode.
	MESSAGE_REFUSAL_MODE(177),
	// Message: Message acceptance mode.
	MESSAGE_ACCEPTANCE_MODE(178),
	// Message: Cannot see target.
	CANNOT_SEE_TARGET(181),
	// Message: Your clan has been created.
	YOUR_CLAN_HAS_BEEN_CREATED(189),
	// Message: You have failed to create a clan.
	YOU_HAVE_FAILED_TO_CREATE_A_CLAN(190),
	// Message: Clan member $s1 has been expelled.
	CLAN_MEMBER_S1_HAS_BEEN_EXPELLED(191),
	// Message: Clan has dispersed.
	CLAN_HAS_DISPERSED(193),
	// Message: Entered the clan.
	ENTERED_THE_CLAN(195),
	// Message: $s1 declined your clan invitation.
	S1_DECLINED_YOUR_CLAN_INVITATION(196),
	// Message: You have recently been dismissed from a clan.  You are not allowed to join another clan for 24-hours.
	YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN(199),
	// Message: You have withdrawn from the party.
	YOU_HAVE_WITHDRAWN_FROM_THE_PARTY(200),
	YOU_REACHED_LEVEL_86_RELATIONSHIP_WITH_S1_CAME_TO_AN_END(3706),
	// Message: You have offered to become $s1's mentor
	YOU_HAVE_OFFERED_TO_BECOME_S1_MENTOR(3707),
	// Message: You can bond with a new mentee in $s1 day(s) $s2 hour(s) $s3 minute(s).
	YOU_CAN_BOND_WITH_A_NEW_MENTEE_IN_S1_DAYS_S2_HOUR_S3_MINUTE(3713),
	// Message: You have been expelled from the party.
	YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY(202),
	// Message: Incorrect name. Please try again.
	INCORRECT_NAME(204),
	// Message: The size of the image file is inappropriate.  Please adjust to 16x12 pixels.
	THE_SIZE_OF_THE_IMAGE_FILE_IS_INAPPROPRIATE__PLEASE_ADJUST_TO_16X12_PIXELS(209),
	// Message: You are not a clan member and cannot perform this action.
	YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION(212),
	// Message: Your title has been changed.
	YOUR_TITLE_HAS_BEEN_CHANGED(214),
	// Message: A clan war with Clan $s1 has started. The clan that cancels the war first will lose 5,000 Clan Reputation. Any clan that cancels the war will be unable to declare a war for 1 week. If your clan member gets killed by the other clan, XP decreases by 1/4 of the amount that decreases in the hunting ground.
	A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED(215),
	// Message: War with the $s1 clan has ended.
	WAR_WITH_THE_S1_CLAN_HAS_ENDED(216),
	// Message: You have won the war over the $s1 clan!
	YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN(217),
	// Message: You have surrendered to the $s1 clan.
	YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN(218),
	// Message: $s1 has joined the clan.
	S1_HAS_JOINED_THE_CLAN(222),
	// Message: The mentoring relationship with $s1 has been canceled. The mentor cannot obtain another mentee for one week
	THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED(3689),
	// Message: Do you wish to make $s1 your mentor? (Class: $s2 / Level: $s3)
	DO_TOU_WISH_TO_MAKE_S1_YOUR_MENTOR_CLASS_S2_LEVEL_S3(3690),
	// Message: From now on, $s1 will be your mentor
	FROM_NOW_ON_S1_WILL_BE_YOUR_MENTOR(3691),
	// Message: From now on, $s1 will be your mentee.
	FROM_NOW_ON_S1_WILL_BE_YOUR_MENTEE(3692),
	// Message: A mentor can have up to 3 mentees at the same time.
	A_MENTOR_CAN_HAVE_UP_TO_3_MENTEES_AT_THE_SAME_TIME(3693),
	// Message: You must awaken in order to become a mentor.
	YOU_MUST_AWAKEN_IN_ORDER_TO_BECOME_A_MENTOR(3694),
	// Message: Your mentee $s1 has connected.
	YOU_MENTEE_S1_HAS_CONNECTED(3695),
	// Message: Your mentor $s1 has connected
	YOU_MENTOR_S1_HAS_CONNECTED(3696),
	// Message: Your mentee $s1 has disconnected.
	YOU_MENTEE_S1_HAS_DISCONNECTED(3697),
	// Message: Your mentor $s1 has disconnected.
	YOU_MENTOR_S1_HAS_DISCONNECTED(3698),
	// Message: $s1 has declined becoming your mentee
	S1_HAS_DECLINED_BECOMING_YOUR_MENTEE(3699),
	// Message: You cannot become your own mentee.
	YOU_CANNOT_BECOME_YOUR_OWN_MENTEE(3701),
	// Message: $s1 already has a mentor.
	S1_ALREADY_HAS_A_MENTOR(3702),
	// Message: $s1 is above level 86 and cannot become a mentee
	S1_IS_ABOVE_LEVEL_86_AND_CANNOT_BECOME_A_MENTEE(3703),
	// Message: $s1 does not have the item needed to become a mentee.
	S1_DOES_NOT_HAVE_THE_ITEM_NEDEED_TO_BECOME_A_MENTEE(3704),
	// Message: The mentee $s1 reached level 86, so the mentoring relationship was ended. After the mentee's graduation, the mentor cannot obtain another mentee for 5 days.
	THE_MENTEE_S1_HAS_REACHED_LEVEL_86(3705),
	// Message: Invitation can occur only when the mentee is in main class status.
	INVITATION_CAN_OCCUR_ONLY_WHEN_THE_MENTEE_IS_IN_MAIN_CLASS_STATUS(3710),
	// Message: $s1 has withdrawn from the clan.
	S1_HAS_WITHDRAWN_FROM_THE_CLAN(223),
	// Message: Request to end war has been denied.
	REQUEST_TO_END_WAR_HAS_BEEN_DENIED(228),
	// Message: You do not meet the criteria in order to create a clan.
	YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN(229),
	// Message: You must wait 10 days before creating a new clan.
	YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN(230),
	// Message: After a clan member is dismissed from a clan, the clan must wait at least a day before accepting a new member.
	AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER(231),
	// Message: After leaving or having been dismissed from a clan, you must wait at least a day before joining another clan.
	AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN(232),
	// Message: The Academy/Royal Guard/Order of Knights is full and cannot accept new members at this time.
	THE_ACADEMYROYAL_GUARDORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME(233),
	// Message: The target must be a clan member.
	THE_TARGET_MUST_BE_A_CLAN_MEMBER(234),
	// Message: Only the clan leader is enabled.
	ONLY_THE_CLAN_LEADER_IS_ENABLED(236),
	// Message: Not joined in any clan.
	NOT_JOINED_IN_ANY_CLAN(238),
	// Message: A clan leader cannot withdraw from their own clan.
	A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN(239),
	// Message: Select target.
	SELECT_TARGET(242),
	// Message: You have already been at war with the $s1 clan: 5 days must pass before you can challenge this clan again.
	YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_CHALLENGE_THIS_CLAN_AGAIN(247),
	// Message: Clan name is invalid.
	CLAN_NAME_IS_INVALID(261),
	// Message: Clan name's length is incorrect.
	CLAN_NAMES_LENGTH_IS_INCORRECT(262),
	// Message: You have already requested the dissolution of your clan.
	YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN(263),
	// Message: You cannot dissolve a clan while engaged in a war.
	YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR(264),
	// Message: There are no requests to disperse.
	THERE_ARE_NO_REQUESTS_TO_DISPERSE(267),
	// Message: A player can only be granted a title if the clan is level 3 or above.
	A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE(271),
	// Message: A clan crest can only be registered when the clan's skill level is 3 or above.
	A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE(272),
	// Message: Your clan's level has increased.
	YOUR_CLANS_LEVEL_HAS_INCREASED(274),
	// Message: The clan has failed to increase its level.
	THE_CLAN_HAS_FAILED_TO_INCREASE_ITS_LEVEL(275),
	// Message: You do not have the necessary materials or prerequisites to learn this skill.
	YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL(276),
	// Message: You have earned $s1.
	YOU_HAVE_EARNED_S1_SKILL(277),
	// Message: You do not have enough adena.
	YOU_DO_NOT_HAVE_ENOUGH_ADENA(279),
	// Message: You do not have enough SP to learn this skill.
	YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL(278),
	// Message: You have not deposited any items in your warehouse.
	YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE(282),
	// Message: You have entered a combat zone.
	YOU_HAVE_ENTERED_A_COMBAT_ZONE(283),
	// Message: You have left a combat zone.
	YOU_HAVE_LEFT_A_COMBAT_ZONE(284),
	// Message: Clan $s1 has successfully engraved the holy artifact!
	CLAN_S1_HAS_SUCCESSFULLY_ENGRAVED_THE_HOLY_ARTIFACT(285),
	// Message: Your base is being attacked.
	YOUR_BASE_IS_BEING_ATTACKED(286),
	// Message: The opposing clan has started to engrave the holy artifact!
	THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT(287),
	// Message: The castle gate has been destroyed.
	THE_CASTLE_GATE_HAS_BEEN_DESTROYED(288),
	// Message: An outpost or headquarters cannot be built because one already exists.
	AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS(289),
	// Message: You cannot set up a base here.
	YOU_CANNOT_SET_UP_A_BASE_HERE(290),
	// Message: Clan $s1 is victorious over $s2's castle siege!
	CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE(291),
	// Message: $s1 has announced the next castle siege time.
	S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME(292),
	// Message: The registration term for $s1 has ended.
	THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED(293),
	// Message: You cannot summon the encampment because you are not a member of the siege clan involved in the castle / fortress / hideout siege.
	YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE(294),
	// Message: $s1's siege was canceled because there were no clans that participated.
	S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED(295),
	// Message: You have dropped $s1.
	YOU_HAVE_DROPPED_S1(298),
	// Message: $c1 has obtained $s3 $s2.
	C1_HAS_OBTAINED_S3_S2(299),
	// Message: $c1 has obtained $s2.
	C1_HAS_OBTAINED_S2(300),
	// Message: $s2 $s1 has disappeared.
	S2_S1_HAS_DISAPPEARED(301),
	// Message: $s1 has disappeared.
	S1_HAS_DISAPPEARED(302),
	// Message: Clan member $s1 has logged into game.
	CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME(304),
	// Message: The player declined to join your party.
	THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY(305),
	// Message: This door cannot be unlocked.
	THIS_DOOR_CANNOT_BE_UNLOCKED(319),
	// Message: You have failed to unlock the door.
	YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR(320),
	// Message: It is not locked.
	IT_IS_NOT_LOCKED(321),
	// Message: The soulshot you are attempting to use does not match the grade of your equipped weapon.
	THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON(337),
	// Message: You do not have enough soulshots for that.
	YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT(338),
	// Message: Cannot use soulshots.
	CANNOT_USE_SOULSHOTS(339),
	// Message: You do not have enough materials to perform that action.
	YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION(341),
	// Message: Your soulshots are enabled.
	YOUR_SOULSHOTS_ARE_ENABLED(342),
	// Message: Sweeper failed, target not spoiled.
	SWEEPER_FAILED_TARGET_NOT_SPOILED(343),
	// Message: Incorrect item count.
	INCORRECT_ITEM_COUNT(351),
	// Message: Inappropriate enchant conditions.
	INAPPROPRIATE_ENCHANT_CONDITIONS(355),
	// Message: Reject resurrection.
	REJECT_RESURRECTION(356),
	// Message: It has already been spoiled.
	IT_HAS_ALREADY_BEEN_SPOILED(357),
	// Message: $s1 hour(s) until castle siege conclusion.
	S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION(358),
	// Message: $s1 minute(s) until castle siege conclusion.
	S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION(359),
	// Message: This castle siege will end in $s1 second(s)!
	THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS(360),
	// Message: Over-hit!
	OVERHIT(361),
	// Message: You have obtained a +$s1 $s2.
	YOU_HAVE_OBTAINED_A_S1_S2(369),
	// Message: $c1 has obtained +$s2$s3.
	C1_HAS_OBTAINED_S2S3(376),
	// Message: $S1 $S2 disappeared.
	S1_S2_DISAPPEARED(377),
	// Message: $c1 purchased $s2.
	C1_PURCHASED_S2(378),
	// Message: $c1 purchased +$s2$s3.
	C1_PURCHASED_S2S3(379),
	// Message: $c1 purchased $s3 $s2(s).
	C1_PURCHASED_S3_S2S(380),
	// Message: Failed to cancel petition. Please try again later.
	FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER(393),
	// Message: System error.
	SYSTEM_ERROR(399),
	// Message: You do not possess the correct ticket to board the boat.
	YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT(402),
	// Message: Your Create Item level is too low to register this recipe.
	YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE(404),
	// Message: Your petition is being processed.
	YOUR_PETITION_IS_BEING_PROCESSED(407),
	// Message: Time expired.
	TIME_EXPIRED(420),
	// Message: Another person has logged in with the same account.
	ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT(421),
	// Message: You have exceeded the weight limit.
	YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT(422),
	// Message: Does not fit strengthening conditions of the scroll.
	DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL(424),
	// Message: This feature is only available to alliance leaders.
	THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS(464),
	// Message: You are not currently allied with any clans.
	YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS(465),
	// Message: A clan that has withdrawn or been expelled cannot enter into an alliance within one day of withdrawal or expulsion.
	A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION(468),
	// Message: You may not ally with a clan you are currently at war with.  That would be diabolical and treacherous.
	YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_CURRENTLY_AT_WAR_WITH(469),
	// Message: Only the clan leader may apply for withdrawal from the alliance.
	ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE(470),
	// Message: Alliance leaders cannot withdraw.
	ALLIANCE_LEADERS_CANNOT_WITHDRAW(471),
	// Message: $s1 has joined as a friend.
	S1_HAS_JOINED_AS_A_FRIEND(479),
	// Message: ======<Friends List>======
	FRIENDS_LIST(487),
	// Message: ========================
	LINE_490(490),
	// Message: =======<Alliance Information>=======
	ALLIANCE_INFORMATION(491),
	// Message: =====<Clan Information>=====
	CLAN_INFORMATION(496),
	// Message: ------------------------
	LINE_500(500),
	// Message: You already belong to another alliance.
	YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE(502),
	// Message: Only clan leaders may create alliances.
	ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES(504),
	// Message: You cannot create a new alliance within 1 day of dissolution.
	YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_OF_DISSOLUTION(505),
	// Message: Incorrect alliance name.  Please try again.
	INCORRECT_ALLIANCE_NAME__PLEASE_TRY_AGAIN(506),
	// Message: Incorrect length for an alliance name.
	INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME(507),
	// Message: That alliance name already exists.
	THAT_ALLIANCE_NAME_ALREADY_EXISTS(508),
	// Message: You have accepted the alliance.
	YOU_HAVE_ACCEPTED_THE_ALLIANCE(517),
	// Message: You have failed to invite a clan into the alliance.
	YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE(518),
	// Message: You have withdrawn from the alliance.
	YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE(519),
	// Message: You have failed to withdraw from the alliance.
	YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE(520),
	// Message: The alliance has been dissolved.
	THE_ALLIANCE_HAS_BEEN_DISSOLVED(523),
	// Message: You have failed to dissolve the alliance.
	YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE(524),
	// Message: That person has been successfully added to your Friend List
	THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST(525),
	// Message: You have failed to add a friend to your friends list.
	YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST(526),
	// Message: $s1 leader, $s2, has requested an alliance.
	S1_LEADER_S2_HAS_REQUESTED_AN_ALLIANCE(527),
	// Message: Your Spiritshot does not match the weapon's grade.
	YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE(530),
	// Message: You do not have enough Spiritshot for that.
	YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT(531),
	// Message: You may not use Spiritshots.
	YOU_MAY_NOT_USE_SPIRITSHOTS(532),
	// Message: Your spiritshot has been enabled.
	YOUR_SPIRITSHOT_HAS_BEEN_ENABLED(533),
	// Message: Your SP has decreased by $s1.
	YOUR_SP_HAS_DECREASED_BY_S1(538),
	// Message: You already have a pet.
	YOU_ALREADY_HAVE_A_PET(543),
	// Message: Your pet cannot carry this item.
	YOUR_PET_CANNOT_CARRY_THIS_ITEM(544),
	// Message: Your pet cannot carry any more items. Remove some, then try again.
	YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS(545),
	// Message: Your pet cannot carry any more items.
	YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS_(546),
	// Message: Summoning your pet…
	SUMMONING_YOUR_PET(547),
	// Message: Your pet's name can be up to 8 characters in length.
	YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS_IN_LENGTH(548),
	// Message: To create an alliance, your clan must be Level 5 or higher.
	TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER(549),
	// Message: As you are currently schedule for clan dissolution, no alliance can be created.
	AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_NO_ALLIANCE_CAN_BE_CREATED(550),
	// Message: As you are currently schedule for clan dissolution, your clan level cannot be increased.
	AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOUR_CLAN_LEVEL_CANNOT_BE_INCREASED(551),
	// Message: As you are currently schedule for clan dissolution, you cannot register or delete a Clan Crest.
	AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST(552),
	// Message: You cannot disperse the clans in your alliance.
	YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE(554),
	// Message: As your pet is currently out, its summoning item cannot be destroyed.
	AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED(557),
	// Message: You may not crystallize this item. Your crystallization skill level is too low.
	YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM(562),
	// Message: Cubic Summoning failed.
	CUBIC_SUMMONING_FAILED(568),
	// Message: Pets and Servitors are not available at this time.
	PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME(574),
	// Message: You cannot summon during a trade or while using a private store.
	YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE(577),
	// Message: You cannot summon during combat.
	YOU_CANNOT_SUMMON_DURING_COMBAT(578),
	// Message: A pet cannot be unsummoned during battle.
	A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE(579),
	// Message: You may not use multiple pets or servitors at the same time.
	YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME(580),
	// Message: Dead pets cannot be returned to their summoning item.
	DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM(589),
	// Message: Your pet is dead and any attempt you make to give it something goes unrecognized.
	YOUR_PET_IS_DEAD_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED(590),
	// Message: You may not restore a hungry pet.
	YOU_MAY_NOT_RESTORE_A_HUNGRY_PET(594),
	// Message: Your pet is very hungry.
	YOUR_PET_IS_VERY_HUNGRY(595),
	// Message: Your pet ate a little, but is still hungry.
	YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY(596),
	// Message: You may not equip a pet item.
	YOU_MAY_NOT_EQUIP_A_PET_ITEM(600),
	// Message: You may not call forth a pet or summoned creature from this location.
	YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION(604),
	// Message: You can only enter up 128 names in your friends list.
	YOU_CAN_ONLY_ENTER_UP_128_NAMES_IN_YOUR_FRIENDS_LIST(605),
	// Message: The Friend's List of the person you are trying to add is full, so registration is not possible.
	THE_FRIENDS_LIST_OF_THE_PERSON_YOU_ARE_TRYING_TO_ADD_IS_FULL_SO_REGISTRATION_IS_NOT_POSSIBLE(606),
	// Message: You do not have any further skills to learn. Come back when you have reached Level $s1.
	YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1(607),
	// Message: $c1 has obtained $s3 $s2 by using sweeper.
	C1_HAS_OBTAINED_S3_S2_BY_USING_SWEEPER(608),
	// Message: $c1 has obtained $s2 by using sweeper.
	C1_HAS_OBTAINED_S2_BY_USING_SWEEPER(609),
	// Message: The Spoil condition has been activated.
	THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED(612),
	// Message: ======<Ignore List>======
	IGNORE_LIST(613),
	// Message: You have failed to register the user to your Ignore List.
	YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST(615),
	// Message: You have failed to delete the character.
	YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_(616),
	// Message: $s1 has been added to your Ignore List.
	S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST(617),
	// Message: $s1 has been removed from your Ignore List.
	S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST(618),
	// Message: $c1 has placed you on his/her Ignore List.
	C1_HAS_PLACED_YOU_ON_HISHER_IGNORE_LIST(619),
	// Message: The $s1 clan did not respond: war proclamation has been refused.
	THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED(626),
	// Message: You have already been at war with the $s1 clan: 5 days must pass before you can declare war again.
	YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN(628),
	// Message: You have already requested a Castle Siege.
	YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE(638),
	// Message: You are already registered to the attacker side and must cancel your registration before submitting your request.
	YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST(642),
	// Message: You have already registered to the defender side and must cancel your registration before submitting your request.
	YOU_HAVE_ALREADY_REGISTERED_TO_THE_DEFENDER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST(643),
	// Message: You are not yet registered for the castle siege.
	YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE(644),
	// Message: Only clans of level 5 or higher may register for a castle siege.
	ONLY_CLANS_OF_LEVEL_5_OR_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE(645),
	// Message: You do not have the authority to modify the castle defender list.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST(646),
	// Message: You do not have the authority to modify the siege time.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME(647),
	// Message: No more registrations may be accepted for the attacker side.
	NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE(648),
	// Message: No more registrations may be accepted for the defender side.
	NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE(649),
	// Message: You may not summon from your current location.
	YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION(650),
	// Message: You do not have the authority to position mercenaries.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES(653),
	// Message: You do not have the authority to cancel mercenary positioning.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING(654),
	// Message: Mercenaries cannot be positioned here.
	MERCENARIES_CANNOT_BE_POSITIONED_HERE(655),
	// Message: This mercenary cannot be positioned anymore.
	THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE(656),
	// Message: Positioning cannot be done here because the distance between mercenaries is too short.
	POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT(657),
	// Message: This is not a mercenary of a castle that you own and so you cannot cancel its positioning.
	THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POSITIONING(658),
	// Message: This is not the time for siege registration and so registrations cannot be accepted or rejected.
	THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED(659),
	// Message: This is not the time for siege registration and so registration and cancellation cannot be done.
	THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE(660),
	// Message: $s1 adena disappeared.
	S1_ADENA_DISAPPEARED(672),
	// Message: Only a clan leader whose clan is of level 2 or higher is allowed to participate in a clan hall auction.
	ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION(673),
	// Message: It has not yet been seven days since canceling an auction.
	IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION(674),
	// Message: There are no clan halls up for auction.
	THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION(675),
	// Message: Since you have already submitted a bid, you are not allowed to participate in another auction at this time.
	SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME(676),
	// Message: Your bid price must be higher than the minimum price currently being bid.
	YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID(677),
	// Message: You have canceled your bid.
	YOU_HAVE_CANCELED_YOUR_BID(679),
	// Message: There are no priority rights on a sweeper.
	THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER(683),
	// Message: You cannot move while frozen. Please wait.
	YOU_CANNOT_MOVE_WHILE_FROZEN(687),
	// Message: Castle-owning clans are automatically registered on the defending side.
	CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE(688),
	// Message: A clan that owns a castle cannot participate in another siege.
	A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE(689),
	// Message: You cannot register as an attacker because you are in an alliance with the castle-owning clan.
	YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN(690),
	// Message: $s1 clan is already a member of $s2 alliance.
	S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE(691),
	// Message: The other party is frozen. Please wait a moment.
	THE_OTHER_PARTY_IS_FROZEN(692),
	// Message: No packages have arrived.
	NO_PACKAGES_HAVE_ARRIVED(694),
	// Message: You do not have enough required items.
	YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS(701),
	// Message: There are no GMs currently visible in the public list as they may be performing other functions at the moment.
	THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT(702),
	// Message: ======<GM List>======
	GM_LIST(703),
	// Message: GM : $c1
	GM__C1(704),
	// Message: You cannot teleport to a village that is in a siege.
	YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE(707),
	// Message: You do not have the right to use the clan warehouse.
	YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE(709),
	// Message: Only clans of clan level 1 or higher can use a clan warehouse.
	ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE(710),
	// Message: If a base camp does not exist, resurrection is not possible.
	IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE(716),
	// Message: The guardian tower has been destroyed and resurrection is not possible.
	THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE(717),
	// Message: The purchase price is higher than the amount of money that you have and so you cannot open a personal store.
	THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE(720),
	// Message: You cannot apply for dissolution again within seven days after a previous application for dissolution.
	YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICATION_FOR_DISSOLUTION(728),
	// Message: That item cannot be discarded.
	THAT_ITEM_CANNOT_BE_DISCARDED(729),
	// Message: The petition was canceled. You may submit $s1 more petition(s) today.
	THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITIONS_TODAY(736),
	// Message: You have not submitted a petition.
	YOU_HAVE_NOT_SUBMITTED_A_PETITION(738),
	// Message: You are currently not in a petition chat.
	YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT(745),
	// Message: The distance is too far and so the casting has been stopped.
	THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED(748),
	// Message: The effect of $s1 has been removed.
	THE_EFFECT_OF_S1_HAS_BEEN_REMOVED(749),
	// Message: There are no other skills to learn.
	THERE_ARE_NO_OTHER_SKILLS_TO_LEARN(750),
	// Message: You cannot position mercenaries here.
	YOU_CANNOT_POSITION_MERCENARIES_HERE(753),
	// Message: $c1 cannot join the clan because one day has not yet passed since they left another clan.
	C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN(760),
	// Message: $s1 clan cannot join the alliance because one day has not yet passed since they left another alliance.
	S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_ALLIANCE(761),
	// Message: The clan hall which was put up for auction has been awarded to $s1 clan.
	THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN(776),
	// Message: The clan hall which had been put up for auction was not sold and therefore has been re-listed.
	THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED(777),
	// Message: Observation is only possible during a siege.
	OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE(780),
	// Message: Observers cannot participate.
	OBSERVERS_CANNOT_PARTICIPATE(781),
	// Message: Lottery ticket sales have been temporarily suspended.
	LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED(783),
	// Message: Tickets for the current lottery are no longer available.
	TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE(784),
	// Message: The tryouts are finished.
	THE_TRYOUTS_ARE_FINISHED(787),
	// Message: The finals are finished.
	THE_FINALS_ARE_FINISHED(788),
	// Message: The tryouts have begun.
	THE_TRYOUTS_HAVE_BEGUN(789),
	// Message: The finals have begun.
	THE_FINALS_HAVE_BEGUN(790),
	// Message: The final match is about to begin. Line up!
	THE_FINAL_MATCH_IS_ABOUT_TO_BEGIN(791),
	// Message: You are not authorized to do that.
	YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT(794),
	// Message: You may create up to 48 macros.
	YOU_MAY_CREATE_UP_TO_48_MACROS(797),
	// Message: You are too late. The registration period is over.
	YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER(800),
	// Message: The tryouts are about to begin. Line up!
	THE_TRYOUTS_ARE_ABOUT_TO_BEGIN(815),
	// Message: They're off!
	THEYRE_OFF(824),
	// Message: You may not impose a block on a GM.
	YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM(827),
	// Message: You cannot recommend yourself.
	YOU_CANNOT_RECOMMEND_YOURSELF(829),
	// Message: You have recommended $c1. You have $s2 recommendations left.
	YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT(830),
	// Message: You have been recommended by $c1.
	YOU_HAVE_BEEN_RECOMMENDED_BY_C1(831),
	// Message: You are not authorized to make further recommendations at this time. You will receive more recommendation credits each day at 1 p.m.
	YOU_ARE_NOT_AUTHORIZED_TO_MAKE_FURTHER_RECOMMENDATIONS_AT_THIS_TIME(833),
	// Message: You may not throw the dice at this time. Try again later.
	YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME(835),
	// Message: Macro descriptions may contain up to 32 characters.
	MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS(837),
	// Message: Enter the name of the macro.
	ENTER_THE_NAME_OF_THE_MACRO(838),
	// Message: That recipe is already registered.
	THAT_RECIPE_IS_ALREADY_REGISTERED(840),
	// Message: No further recipes may be registered.
	NO_FURTHER_RECIPES_MAY_BE_REGISTERED(841),
	// Message: You are not authorized to register a recipe.
	YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE(842),
	// Message: The siege of $s1 is finished.
	THE_SIEGE_OF_S1_IS_FINISHED(843),
	// Message: The siege to conquer $s1 has begun.
	THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN(844),
	// Message: The deadline to register for the siege of $s1 has passed.
	THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED(845),
	// Message: The siege of $s1 has been canceled due to lack of interest.
	THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST(846),
	// Message: A clan that owns a clan hall may not participate in a clan hall siege.
	A_CLAN_THAT_OWNS_A_CLAN_HALL_MAY_NOT_PARTICIPATE_IN_A_CLAN_HALL_SIEGE(847),
	// Message: The recipe is incorrect.
	THE_RECIPE_IS_INCORRECT(852),
	// Message: $s1 clan has defeated $s2.
	S1_CLAN_HAS_DEFEATED_S2(855),
	// Message: The siege of $s1 has ended in a draw.
	THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW(856),
	// Message: Please register a recipe.
	PLEASE_REGISTER_A_RECIPE(859),
	// Message: The seed has been sown.
	THE_SEED_HAS_BEEN_SOWN(871),
	// Message: This seed may not be sown here.
	THIS_SEED_MAY_NOT_BE_SOWN_HERE(872),
	// Message: The symbol has been added.
	THE_SYMBOL_HAS_BEEN_ADDED(877),
	// Message: The preliminary match of $s1 has ended in a draw.
	THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW(858),
	// Message: The symbol has been deleted.
	THE_SYMBOL_HAS_BEEN_DELETED(878),
	// Message: The manor system is currently under maintenance.
	THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE(879),
	// Message: The transaction is complete.
	THE_TRANSACTION_IS_COMPLETE(880),
	// Message: The manor information has been updated.
	THE_MANOR_INFORMATION_HAS_BEEN_UPDATED(884),
	// Message: The seed was successfully sown.
	THE_SEED_WAS_SUCCESSFULLY_SOWN(889),
	// Message: The seed was not sown.
	THE_SEED_WAS_NOT_SOWN(890),
	// Message: You are not authorized to harvest.
	YOU_ARE_NOT_AUTHORIZED_TO_HARVEST(891),
	// Message: The harvest has failed.
	THE_HARVEST_HAS_FAILED(892),
	// Message: The harvest failed because the seed was not sown.
	THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN(893),
	// Message: The symbol cannot be drawn.
	THE_SYMBOL_CANNOT_BE_DRAWN(899),
	// Message: No slot exists to draw the symbol.
	NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL(900),
	// Message: Lottery tickets are not currently being sold.
	LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD(930),
	// Message: You cannot chat while in observation mode.
	YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE(932),
	// Message: You do not have enough funds in the clan warehouse for the Manor to operate.
	YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE(935),
	// Message: The community server is currently offline.
	THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE(938),
	// Message: You failed to manufacture $s1.
	YOU_FAILED_TO_MANUFACTURE_S1(960),
	// Message: You are now blocking everything.
	YOU_ARE_NOW_BLOCKING_EVERYTHING(961),
	// Message: You are no longer blocking everything.
	YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING(962),
	// Message: Please determine the manufacturing price.
	PLEASE_DETERMINE_THE_MANUFACTURING_PRICE(963),
	// Message: Chatting is prohibited for one minute.
	CHATTING_IS_PROHIBITED_FOR_ONE_MINUTE(964),
	// Message: The chatting prohibition has been removed.
	THE_CHATTING_PROHIBITION_HAS_BEEN_REMOVED(965),
	// Message: Chatting is currently prohibited. If you try to chat before the prohibition is removed, the prohibition time will increase even further.
	CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER(966),
	// Message: Do you accept $c1's party invitation? (Item Distribution: Random including spoil.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_RANDOM_INCLUDING_SPOIL(967),
	// Message: Do you accept $c1's party invitation? (Item Distribution: By Turn.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_BY_TURN(968),
	// Message: Do you accept $c1's party invitation? (Item Distribution: By Turn including spoil.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_BY_TURN_INCLUDING_SPOIL(969),
	// Message: $s2's MP has been drained by $c1.
	S2S_MP_HAS_BEEN_DRAINED_BY_C1(970),
	// Message: The petition can contain up to 800 characters.
	THE_PETITION_CAN_CONTAIN_UP_TO_800_CHARACTERS(971),
	// Message: This pet cannot use this item.
	THIS_PET_CANNOT_USE_THIS_ITEM(972),
	// Message: Please input no more than the number you have.
	PLEASE_INPUT_NO_MORE_THAN_THE_NUMBER_YOU_HAVE(973),
	// Message: The soul crystal succeeded in absorbing a soul.
	THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL(974),
	// Message: The soul crystal was not able to absorb the soul.
	THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL(975),
	// Message: The soul crystal broke because it was not able to endure the soul energy.
	THE_SOUL_CRYSTAL_BROKE_BECAUSE_IT_WAS_NOT_ABLE_TO_ENDURE_THE_SOUL_ENERGY(976),
	// Message: The soul crystal caused resonation and failed at absorbing a soul.
	THE_SOUL_CRYSTAL_CAUSED_RESONATION_AND_FAILED_AT_ABSORBING_A_SOUL(977),
	// Message: The soul crystal is refusing to absorb the soul.
	THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_THE_SOUL(978),
	// Message: The ferry has arrived at Talking Island Harbor.
	THE_FERRY_HAS_ARRIVED_AT_TALKING_ISLAND_HARBOR(979),
	// Message: The ferry will leave for Gludin Harbor after anchoring for ten minutes.
	THE_FERRY_WILL_LEAVE_FOR_GLUDIN_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES(980),
	// Message: The ferry will leave for Gludin Harbor in five minutes.
	THE_FERRY_WILL_LEAVE_FOR_GLUDIN_HARBOR_IN_FIVE_MINUTES(981),
	// Message: The ferry will leave for Gludin Harbor in one minute.
	THE_FERRY_WILL_LEAVE_FOR_GLUDIN_HARBOR_IN_ONE_MINUTE(982),
	// Message: Those wishing to ride the ferry should make haste to get on.
	THOSE_WISHING_TO_RIDE_THE_FERRY_SHOULD_MAKE_HASTE_TO_GET_ON(983),
	// Message: The ferry will be leaving soon for Gludin Harbor.
	THE_FERRY_WILL_BE_LEAVING_SOON_FOR_GLUDIN_HARBOR(984),
	// Message: The ferry is leaving for Gludin Harbor.
	THE_FERRY_IS_LEAVING_FOR_GLUDIN_HARBOR(985),
	// Message: The ferry has arrived at Gludin Harbor.
	THE_FERRY_HAS_ARRIVED_AT_GLUDIN_HARBOR(986),
	// Message: The ferry will leave for Talking Island Harbor after anchoring for ten minutes.
	THE_FERRY_WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES(987),
	// Message: The ferry will leave for Talking Island Harbor in five minutes.
	THE_FERRY_WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_IN_FIVE_MINUTES(988),
	// Message: The ferry will leave for Talking Island Harbor in one minute.
	THE_FERRY_WILL_LEAVE_FOR_TALKING_ISLAND_HARBOR_IN_ONE_MINUTE(989),
	// Message: The ferry will be leaving soon for Talking Island Harbor.
	THE_FERRY_WILL_BE_LEAVING_SOON_FOR_TALKING_ISLAND_HARBOR(990),
	// Message: The ferry is leaving for Talking Island Harbor.
	THE_FERRY_IS_LEAVING_FOR_TALKING_ISLAND_HARBOR(991),
	// Message: The ferry has arrived at Giran Harbor.
	THE_FERRY_HAS_ARRIVED_AT_GIRAN_HARBOR(992),
	// Message: The ferry will leave for Giran Harbor after anchoring for ten minutes.
	THE_FERRY_WILL_LEAVE_FOR_GIRAN_HARBOR_AFTER_ANCHORING_FOR_TEN_MINUTES(993),
	// Message: The ferry will leave for Giran Harbor in five minutes.
	THE_FERRY_WILL_LEAVE_FOR_GIRAN_HARBOR_IN_FIVE_MINUTES(994),
	// Message: The ferry will leave for Giran Harbor in one minute.
	THE_FERRY_WILL_LEAVE_FOR_GIRAN_HARBOR_IN_ONE_MINUTE(995),
	// Message: The ferry will be leaving soon for Giran Harbor.
	THE_FERRY_WILL_BE_LEAVING_SOON_FOR_GIRAN_HARBOR(996),
	// Message: The ferry is leaving for Giran Harbor.
	THE_FERRY_IS_LEAVING_FOR_GIRAN_HARBOR(997),
	// Message: The Innadril pleasure boat has arrived. It will anchor for ten minutes.
	THE_INNADRIL_PLEASURE_BOAT_HAS_ARRIVED_IT_WILL_ANCHOR_FOR_TEN_MINUTES(998),
	// Message: The Innadril pleasure boat will leave in five minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_LEAVE_IN_FIVE_MINUTES(999),
	// Message: The Innadril pleasure boat will leave in one minute.
	THE_INNADRIL_PLEASURE_BOAT_WILL_LEAVE_IN_ONE_MINUTE(1000),
	// Message: The Innadril pleasure boat will be leaving soon.
	THE_INNADRIL_PLEASURE_BOAT_WILL_BE_LEAVING_SOON(1001),
	// Message: The Innadril pleasure boat is leaving.
	THE_INNADRIL_PLEASURE_BOAT_IS_LEAVING(1002),
	// Message: Cannot process a monster race ticket.
	CANNOT_PROCESS_A_MONSTER_RACE_TICKET(1003),
	// Message: You have registered for a clan hall auction.
	YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION(1004),
	// Message: There is not enough Adena in the clan hall warehouse.
	THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE(1005),
	// Message: Your bid has been successfully placed.
	YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED(1006),
	// Message: The preliminary match registration for $s1 has finished.
	THE_PRELIMINARY_MATCH_REGISTRATION_FOR_S1_HAS_FINISHED(1007),
	// Message: A hungry strider cannot be mounted or dismounted.
	A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED(1008),
	// Message: A strider cannot be ridden when dead.
	A_STRIDER_CANNOT_BE_RIDDEN_WHEN_DEAD(1009),
	// Message: A dead strider cannot be ridden.
	A_DEAD_STRIDER_CANNOT_BE_RIDDEN(1010),
	// Message: A strider in battle cannot be ridden.
	A_STRIDER_IN_BATTLE_CANNOT_BE_RIDDEN(1011),
	// Message: A strider cannot be ridden while in battle.
	A_STRIDER_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE(1012),
	// Message: A strider can be ridden only when standing.
	A_STRIDER_CAN_BE_RIDDEN_ONLY_WHEN_STANDING(1013),
	// Message: Your pet gained $s1 XP.
	YOUR_PET_GAINED_S1_XP(1014),
	// Message: Your pet hit for $s1 damage.
	YOUR_PET_HIT_FOR_S1_DAMAGE(1015),
	// Message: Your pet received $s2 damage by $c1.
	YOUR_PET_RECEIVED_S2_DAMAGE_BY_C1(1016),
	// Message: Pet's critical hit!
	PETS_CRITICAL_HIT(1017),
	// Message: Summoned monster's critical hit!
	SUMMONED_MONSTERS_CRITICAL_HIT(1028),
	// Message: <Party Information>
	PARTY_INFORMATION(1030),
	// Message: Looting method: Finders keepers
	LOOTING_METHOD_FINDERS_KEEPERS(1031),
	// Message: Looting method: Random
	LOOTING_METHOD_RANDOM(1032),
	// Message: Looting method: Random including spoil
	LOOTING_METHOD_RANDOM_INCLUDING_SPOIL(1033),
	// Message: Looting method: By turn
	LOOTING_METHOD_BY_TURN(1034),
	// Message: Looting method: By turn including spoil
	LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL(1035),
	// Message: You have exceeded the quantity that can be inputted.
	YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED(1036),
	// Message: Items left at the clan hall warehouse can only be retrieved by the clan leader. Do you want to continue?
	ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER(1039),
	// Message: Monster race payout information is not available while tickets are being sold.
	MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD(1044),
	// Message: Monster race tickets are no longer available.
	MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE(1046),
	// Message: Payment for your clan hall has not been made. Please make payment to your clan warehouse by $s1 tomorrow.
	PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_ME_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW(1051),
	// Message: The clan hall fee is one week overdue; therefore the clan hall ownership has been revoked.
	THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED(1052),
	// Message: It is not possible to resurrect in battlefields where a siege war is taking place.
	IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE(1053),
	// Message: While operating a private store or workshop, you cannot discard, destroy, or trade an item.
	WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM(1065),
	// Message: $s1 HP has been restored.
	S1_HP_HAS_BEEN_RESTORED(1066),
	// Message: $s2 HP has been restored by $c1.
	S2_HP_HAS_BEEN_RESTORED_BY_C1(1067),
	// Message: $s1 MP has been restored.
	S1_MP_HAS_BEEN_RESTORED(1068),
	// Message: $s2 MP has been restored by $c1.
	S2_MP_HAS_BEEN_RESTORED_BY_C1(1069),
	// Message: The bid amount must be higher than the previous bid.
	THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID(1075),
	// Message: Your selected target can no longer receive a recommendation.
	YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION(1188),
	// Message: You cannot leave a clan while engaged in combat.
	YOU_CANNOT_LEAVE_A_CLAN_WHILE_ENGAGED_IN_COMBAT(1116),
	// Message: A clan member may not be dismissed during combat.
	A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT(1117),
	// Message: Progress in a quest is possible only when your inventory's weight and slot count are less than 80 percent of capacity.
	PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY(1118),
	// Message: A private store may not be opened while using a skill.
	A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL(1128),
	// Message: While you are engaged in combat, you cannot operate a private store or private workshop.
	WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(1135),
	// Message: $c1 harvested $s3 $s2(s).
	C1_HARVESTED_S3_S2S(1137),
	// Message: Would you like to open the gate?
	WOULD_YOU_LIKE_TO_OPEN_THE_GATE(1140),
	// Message: Would you like to close the gate?
	WOULD_YOU_LIKE_TO_CLOSE_THE_GATE(1141),
	// Message: Since $s1 already exists nearby, you cannot summon it again.
	SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN(1142),
	// Message: Since you do not have enough items to maintain the servitor's stay, the servitor has disappeared.
	SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_HAS_DISAPPEARED(1143),
	// Message: $c1 created $s2 after receiving $s3 Adena.
	C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA(1146),
	// Message: $c1 created $s2 $s3 at the price of $s4 Adena.
	C1_CREATED_S2_S3_AT_THE_PRICE_OF_S4_ADENA(1148),
	// Message: Your attempt to create $s2 for $c1 at the price of $s3 Adena has failed.
	YOUR_ATTEMPT_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA_HAS_FAILED(1149),
	// Message: $c1 has failed to create $s2 at the price of $s3 Adena.
	C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA(1150),
	// Message: $s2 is sold to $c1 for the price of $s3 Adena.
	S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA(1151),
	// Message: $s2 $s3 have been sold to $c1 for $s4 Adena.
	S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA(1152),
	// Message: $s2 has been purchased from $c1 at the price of $s3 Adena.
	S2_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S3_ADENA(1153),
	// Message: $s3 $s2 has been purchased from $c1 for $s4 Adena.
	S3_S2_HAS_BEEN_PURCHASED_FROM_C1_FOR_S4_ADENA(1154),
	// Message: +$s2$s3 has been sold to $c1 at the price of $s4 Adena.
	S2S3_HAS_BEEN_SOLD_TO_C1_AT_THE_PRICE_OF_S4_ADENA(1155),
	// Message: +$s2$s3 has been purchased from $c1 at the price of $s4 Adena.
	S2S3_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S4_ADENA(1156),
	// Message: The ferry from Talking Island will arrive at Gludin Harbor in approximately 10 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_ARRIVE_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_10_MINUTES(1159),
	// Message: The ferry from Talking Island will be arriving at Gludin Harbor in approximately 5 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_5_MINUTES(1160),
	// Message: The ferry from Talking Island will be arriving at Gludin Harbor in approximately 1 minute.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_1_MINUTE(1161),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 15 minutes.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_15_MINUTES(1162),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 10 minutes.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_10_MINUTES(1163),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 5 minutes.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_5_MINUTES(1164),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 1 minute.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_1_MINUTE(1165),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 20 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_20_MINUTES(1166),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 15 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_15_MINUTES(1167),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 10 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_10_MINUTES(1168),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 5 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_5_MINUTES(1169),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 1 minute.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_1_MINUTE(1170),
	// Message: The Innadril pleasure boat will arrive in approximately 20 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_20_MINUTES(1171),
	// Message: The Innadril pleasure boat will arrive in approximately 15 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_15_MINUTES(1172),
	// Message: The Innadril pleasure boat will arrive in approximately 10 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_10_MINUTES(1173),
	// Message: The Innadril pleasure boat will arrive in approximately 5 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_5_MINUTES(1174),
	// Message: The Innadril pleasure boat will arrive in approximately 1 minute.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_1_MINUTE(1175),
	// Message: The temporary alliance of the Castle Attacker team is in effect. It will be dissolved when the Castle Lord is replaced.
	THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT(1189),
	// Message: The temporary alliance of the Castle Attacker team has been dissolved.
	THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED(1190),
	// Message: A mercenary can be assigned to a position from the beginning of the Seal Validation period until the time when a siege starts.
	A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS(1194),
	// Message: This mercenary cannot be assigned to a position by using the Seal of Strife.
	THIS_MERCENARY_CANNOT_BE_ASSIGNED_TO_A_POSITION_BY_USING_THE_SEAL_OF_STRIFE(1195),
	// Message: Your force has reached maximum capacity.
	YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_(1196),
	// Message: The item has been successfully crystallized.
	THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED(1198),
	// Message: $c1 died and dropped $s3 $s2.
	C1_DIED_AND_DROPPED_S3_S2(1208),
	// Message: Congratulations. Your raid was successful.
	CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL(1209),
	// Message: $c1 has blocked you. You cannot send mail to $c1.
	C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1(1228),
	// Message: You've sent mail.
	YOUVE_SENT_MAIL(1231),
	// Message: The message was not sent.
	THE_MESSAGE_WAS_NOT_SENT(1232),
	// Message: You've got mail.
	YOUVE_GOT_MAIL(1233),
	// Message: $c1 has died and dropped $s2 adena.
	C1_HAS_DIED_AND_DROPPED_S2_ADENA(1246),
	// Message: You are out of feed. Mount status canceled.
	YOU_ARE_OUT_OF_FEED(1248),
	// Message: Seven Signs: Preparations have begun for the next quest event.
	SEVEN_SIGNS_PREPARATIONS_HAVE_BEGUN_FOR_THE_NEXT_QUEST_EVENT(1260),
	// Message: Seven Signs: The quest event period has begun. Speak with a Priest of Dawn or Dusk Priestess if you wish to participate in the event.
	SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN(1261),
	// Message: Seven Signs: Quest event has ended. Results are being tallied.
	SEVEN_SIGNS_QUEST_EVENT_HAS_ENDED(1262),
	// Message: Seven Signs: This is the seal validation period. A new quest event period begins next Monday.
	SEVEN_SIGNS_THIS_IS_THE_SEAL_VALIDATION_PERIOD(1263),
	// Message: The new subclass has been added.
	THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED(1269),
	// Message: You have successfully switched to your subclass.
	YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS(1270),
	// Message: You will participate in the Seven Signs as a member of the Lords of Dawn.
	YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_LORDS_OF_DAWN(1273),
	// Message: You will participate in the Seven Signs as a member of the Revolutionaries of Dusk.
	YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK(1274),
	// Message: You've chosen to fight for the Seal of Avarice during this quest event period.
	YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_AVARICE_DURING_THIS_QUEST_EVENT_PERIOD(1275),
	// Message: You've chosen to fight for the Seal of Gnosis during this quest event period.
	YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_GNOSIS_DURING_THIS_QUEST_EVENT_PERIOD(1276),
	// Message: You've chosen to fight for the Seal of Strife during this quest event period.
	YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_STRIFE_DURING_THIS_QUEST_EVENT_PERIOD(1277),
	// Message: Contribution level has exceeded the limit. You may not continue.
	CONTRIBUTION_LEVEL_HAS_EXCEEDED_THE_LIMIT(1279),
	// Message: Magic Critical Hit!
	MAGIC_CRITICAL_HIT(1280),
	// Message: Your excellent shield defense was a success!
	YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS(1281),
	// Message: Your Fame has been changed to $s1.
	YOUR_FAME_HAS_BEEN_CHANGED_TO_S1(1282),
	// Message: Subclasses may not be created or changed while a skill is in use.
	SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE(1295),
	// Message: You cannot open a Private Store here.
	YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE(1296),
	// Message: You cannot open a Private Workshop here.
	YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE(1297),
	// Message: This may only be used during the quest event period.
	THIS_MAY_ONLY_BE_USED_DURING_THE_QUEST_EVENT_PERIOD(1303),
	// Message: You are no longer trying on equipment.
	YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_(1306),
	// Message: Congratulations - You've completed a class transfer!
	CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER(1308),
	// Message: You are currently blocked from using the Private Store and Private Workshop.
	YOU_ARE_CURRENTLY_BLOCKED_FROM_USING_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP(1329),
	// Message: You are not allowed to chat with a contact while a chatting block is imposed.
	YOU_ARE_NOT_ALLOWED_TO_CHAT_WITH_A_CONTACT_WHILE_A_CHATTING_BLOCK_IS_IMPOSED(1354),
	// Message: You have been blocked from chatting with that contact.
	YOU_HAVE_BEEN_BLOCKED_FROM_CHATTING_WITH_THAT_CONTACT(1357),
	// Message: You can not try those items on at the same time.
	YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME(1368),
	// Message: You are not allowed to dismount in this location.
	YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION(1385),
	// Message: You have exited the party room.
	YOU_HAVE_EXITED_THE_PARTY_ROOM(1391),
	// Message: $c1 has left the party room.
	C1_HAS_LEFT_THE_PARTY_ROOM(1392),
	// Message: You have been ousted from the party room.
	YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM(1393),
	// Message: $c1 has been kicked from the party room.
	C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM(1394),
	// Message: The party room has been disbanded.
	THE_PARTY_ROOM_HAS_BEEN_DISBANDED(1395),
	// Message: The list of party rooms can only be viewed by a person who is not part of a party.
	THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY(1396),
	// Message: The leader of the party room has changed.
	THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED(1397),
	// Message: Slow down, you are already the party leader.
	SLOW_DOWN_YOU_ARE_ALREADY_THE_PARTY_LEADER(1401),
	// Message: You may only transfer party leadership to another member of the party.
	YOU_MAY_ONLY_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_MEMBER_OF_THE_PARTY(1402),
	// Message: $s1 CP has been restored.
	S1_CP_HAS_BEEN_RESTORED(1405),
	// Message: $s2 CP has been restored by $c1.
	S2_CP_HAS_BEEN_RESTORED_BY_C1(1406),
	// Message: You do not meet the requirements to enter that party room.
	YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM(1413),
	// Message: The automatic use of $s1 has been activated.
	THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED(1433),
	// Message: The automatic use of $s1 has been deactivated.
	THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED(1434),
	// Message: You do not have all of the items needed to enchant that skill.
	YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL(1439),
	// Message: You do not have enough SP to enchant that skill.
	YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL(1443),
	// Message: Your previous subclass will be removed and replaced with the new subclass at level 40.  Do you wish to continue?
	YOUR_PREVIOUS_SUBCLASS_WILL_BE_REMOVED_AND_REPLACED_WITH_THE_NEW_SUBCLASS_AT_LEVEL_40(1445),
	// Message: You cannot do that while fishing.
	YOU_CANNOT_DO_THAT_WHILE_FISHING(1447),
	// Message: Only fishing skills may be used at this time.
	ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME(1448),
	// Message: You've got a bite!
	YOUVE_GOT_A_BITE(1449),
	// Message: That fish is more determined than you are - it spit the hook!
	THAT_FISH_IS_MORE_DETERMINED_THAN_YOU_ARE__IT_SPIT_THE_HOOK(1450),
	// Message: Your bait was stolen by that fish!
	YOUR_BAIT_WAS_STOLEN_BY_THAT_FISH(1451),
	// Message: The bait has been lost because the fish got away.
	THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY(1452),
	// Message: You do not have a fishing pole equipped.
	YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED(1453),
	// Message: You must put bait on your hook before you can fish.
	YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH(1454),
	// Message: You cannot fish while under water.
	YOU_CANNOT_FISH_WHILE_UNDER_WATER(1455),
	// Message: You cannot fish while riding as a passenger of a boat - it's against the rules.
	YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT__ITS_AGAINST_THE_RULES(1456),
	// Message: You can't fish here.
	YOU_CANT_FISH_HERE(1457),
	// Message: Your attempt at fishing has been cancelled.
	YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED(1458),
	// Message: You do not have enough bait.
	YOU_DO_NOT_HAVE_ENOUGH_BAIT(1459),
	// Message: You reel your line in and stop fishing.
	YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING(1460),
	// Message: You cast your line and start to fish.
	YOU_CAST_YOUR_LINE_AND_START_TO_FISH(1461),
	// Message: You may only use the Pumping skill while you are fishing.
	YOU_MAY_ONLY_USE_THE_PUMPING_SKILL_WHILE_YOU_ARE_FISHING(1462),
	// Message: You may only use the Reeling skill while you are fishing.
	YOU_MAY_ONLY_USE_THE_REELING_SKILL_WHILE_YOU_ARE_FISHING(1463),
	// Message: The fish has resisted your attempt to bring it in.
	THE_FISH_HAS_RESISTED_YOUR_ATTEMPT_TO_BRING_IT_IN(1464),
	// Message: You caught something!
	YOU_CAUGHT_SOMETHING(1469),
	// Message: You cannot do that while fishing.
	YOU_CANNOT_DO_THAT_WHILE_FISHING_(1470),
	// Message: You cannot do that while fishing.
	YOU_CANNOT_DO_THAT_WHILE_FISHING_2(1471),
	// Message: That is the wrong grade of soulshot for that fishing pole.
	THAT_IS_THE_WRONG_GRADE_OF_SOULSHOT_FOR_THAT_FISHING_POLE(1479),
	// Message: Traded $s2 of $s1 crops.
	TRADED_S2_OF_S1_CROPS(1490),
	// Message: Failed in trading $s2 of $s1 crops.
	FAILED_IN_TRADING_S2_OF_S1_CROPS(1491),
	// Message: Your opponent made haste with their tail between their legs; the match has been cancelled.
	YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED(1493),
	// Message: Your opponent does not meet the requirements to do battle; the match has been cancelled.
	YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED(1494),
	// Message: The match will start in $s1 second(s).
	THE_MATCH_WILL_START_IN_S1_SECONDS(1495),
	// Message: The match has started. Fight!
	THE_MATCH_HAS_STARTED(1496),
	// Message: Congratulations, $c1! You win the match!
	CONGRATULATIONS_C1_YOU_WIN_THE_MATCH(1497),
	// Message: There is no victor; the match ends in a tie.
	THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE(1498),
	// Message: You will be moved back to town in $s1 second(s).
	YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECONDS(1499),
	// Message: $c1 does not meet the participation requirements. A subclass character cannot participate in the Olympiad.
	C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD(1500),
	// Message: $c1 does not meet the participation requirements. Only Noblesse characters can participate in the Olympiad.
	C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_ONLY_NOBLESSE_CHARACTERS_CAN_PARTICIPATE_IN_THE_OLYMPIAD(1501),
	// Message: $c1 is already registered on the match waiting list.
	C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST(1502),
	// Message: You have been registered for the Grand Olympiad waiting list for a class specific match.
	YOU_HAVE_BEEN_REGISTERED_FOR_THE_GRAND_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_SPECIFIC_MATCH(1503),
	// Message: You are currently registered for a 1v1 class irrelevant match.
	YOU_ARE_CURRENTLY_REGISTERED_FOR_A_1V1_CLASS_IRRELEVANT_MATCH(1504),
	// Message: You have been removed from the Grand Olympiad waiting list.
	YOU_HAVE_BEEN_REMOVED_FROM_THE_GRAND_OLYMPIAD_WAITING_LIST(1505),
	// Message: You are not currently registered for the Grand Olympiad.
	YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_GRAND_OLYMPIAD(1506),
	// Message: You cannot equip that item in a Grand Olympiad match.
	YOU_CANNOT_EQUIP_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH(1507),
	// Message: You cannot use that item in a Grand Olympiad match.
	YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH(1508),
	// Message: You cannot use that skill in a Grand Olympiad match.
	YOU_CANNOT_USE_THAT_SKILL_IN_A_GRAND_OLYMPIAD_MATCH(1509),
	// Message: $c1 is making an attempt to resurrect you. If you choose this path, $s2 experience points will be returned to you. Do you want to be resurrected?
	C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU(1510),
	// Message: While a pet is being resurrected, it cannot help in resurrecting its master.
	WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER(1511),
	// Message: Resurrection has already been proposed.
	RESURRECTION_HAS_ALREADY_BEEN_PROPOSED(1513),
	// Message: A pet cannot be resurrected while it's owner is in the process of resurrecting.
	A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING(1515),
	// Message: The target is unavailable for seeding.
	THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING(1516),
	// Message: The Blessed Enchant failed. The enchant value of the item became 0.
	THE_BLESSED_ENCHANT_FAILED(1517),
	// Message: You do not meet the required condition to equip that item.
	YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM(1518),
	// Message: The pet has been killed. If you don't resurrect it within 24 hours, the pet's body will disappear along with all the pet's items.
	THE_PET_HAS_BEEN_KILLED(1519),
	// Message: Your servitor has vanished! You'll need to summon a new one.
	YOUR_SERVITOR_HAS_VANISHED_YOULL_NEED_TO_SUMMON_A_NEW_ONE(1521),
	// Message: You should release your pet or servitor so that it does not fall off of the boat and drown!
	YOU_SHOULD_RELEASE_YOUR_PET_OR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN(1523),
	// Message: Your pet was hungry so it ate $s1.
	YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1(1527),
	// Message: Your clan notice has been saved.
	YOUR_CLAN_NOTICE_HAS_BEEN_SAVED(1556),
	// Message: $s1 has declared a Clan War. Clan War starts in 3 days.
	S1_HAS_DECLARED_A_CLAN_WAR_CLAN_WAR_STARTS_IN_3_DAYS(1561),
	// Message: You have declared a Clan War with $s1. Clan War starts in 3 days.
	YOU_HAVE_DECLARED_A_CLAN_WAR_WITH_S1_CLAN_WAR_STARTS_IN_3_DAYS(1562),
	// Message: A clan war can only be declared if the clan is level 3 or above, and the number of clan members is fifteen or greater.
	A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER(1564),
	// Message: A clan war cannot be declared against a clan that does not exist!
	A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST(1565),
	// Message: The clan, $s1, has decided to stop the war.
	THE_CLAN_S1_HAS_DECIDED_TO_STOP_THE_WAR(1566),
	// Message: The war against $s1 Clan has been stopped.
	THE_WAR_AGAINST_S1_CLAN_HAS_BEEN_STOPPED(1567),
	// Message: The target for declaration is wrong.
	THE_TARGET_FOR_DECLARATION_IS_WRONG(1568),
	// Message: A declaration of Clan War against an allied clan can't be made.
	A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE(1569),
	// Message: A declaration of war against more than 30 Clans can't be made at the same time.
	A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME(1570),
	// Message: ======<Clans You've Declared War On>======
	CLANS_YOUVE_DECLARED_WAR_ON(1571),
	// Message: ======<Clans That Have Declared War On You>======
	CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU(1572),
	// Message: Your pet uses spiritshot.
	COMMAND_CHANNELS_CAN_ONLY_BE_FORMED_BY_A_PARTY_LEADER_WHO_IS_ALSO_THE_LEADER_OF_A_LEVEL_5_CLAN(1575),
	// Message: The Command Channel has been formed.
	THE_COMMAND_CHANNEL_HAS_BEEN_FORMED(1580),
	// Message: The Command Channel has been disbanded.
	THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED(1581),
	// Message: You were dismissed from the Command Channel.
	YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL(1583),
	// Message: $c1's party has been dismissed from the Command Channel.
	C1S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL(1584),
	// Message: You have quit the Command Channel.
	YOU_HAVE_QUIT_THE_COMMAND_CHANNEL(1586),
	// Message: No user has been invited to the Command Channel.
	NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL(1591),
	// Message: You can no longer set up a Command Channel.
	YOU_CAN_NO_LONGER_SET_UP_A_COMMAND_CHANNEL(1592),
	// Message: You do not have authority to invite someone to the Command Channel.
	YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL(1593),
	// Message: $c1's party is already a member of the Command Channel.
	C1S_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL(1594),
	// Message: $s1 has succeeded.
	S1_HAS_SUCCEEDED(1595),
	// Message: $s1 has failed.
	S1_HAS_FAILED(1597),
	// Message: Soulshots and spiritshots are not available for a dead pet or servitor.  Sad, isn't it?
	SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET_OR_SERVITOR(1598),
	// Message: You cannot "observe" while you are in combat!
	YOU_CANNOT_OBSERVE_WHILE_YOU_ARE_IN_COMBAT(1599),
	// Message: Only a party leader can access the Command Channel.
	ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL(1602),
	// Message: Only the Command Channel creator can use the Raid Leader text.
	ONLY_THE_COMMAND_CHANNEL_CREATOR_CAN_USE_THE_RAID_LEADER_TEXT(1603),
	// Message: * Here, you can buy only seeds of $s1 Manor.
	_HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR(1605),
	// Message: Congratulations - You've completed your third-class transfer quest!
	CONGRATULATIONS__YOUVE_COMPLETED_YOUR_THIRDCLASS_TRANSFER_QUEST(1606),
	// Message: $s1 Adena has been withdrawn to pay for purchasing fees.
	S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES(1607),
	// Message: War has already been declared against that clan… but I'll make note that you really don't like them.
	WAR_HAS_ALREADY_BEEN_DECLARED_AGAINST_THAT_CLAN_BUT_ILL_MAKE_NOTE_THAT_YOU_REALLY_DONT_LIKE_THEM(1609),
	// Message: Fool! You cannot declare war against your own clan!
	FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN(1610),
	// Message: =====<War List>=====
	WAR_LIST(1612),
	// Message: You do not have the authority to use the Command Channel.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL(1617),
	// Message: The ferry from Rune Harbor to Gludin Harbor has been delayed.
	THE_FERRY_FROM_RUNE_HARBOR_TO_GLUDIN_HARBOR_HAS_BEEN_DELAYED(1618),
	// Message: The ferry from Gludin Harbor to Rune Harbor has been delayed.
	THE_FERRY_FROM_GLUDIN_HARBOR_TO_RUNE_HARBOR_HAS_BEEN_DELAYED(1619),
	// Message: Welcome to Rune Harbor.
	WELCOME_TO_RUNE_HARBOR(1620),
	// Message: Departure for Gludin Harbor will take place in five minutes!
	DEPARTURE_FOR_GLUDIN_HARBOR_WILL_TAKE_PLACE_IN_FIVE_MINUTES(1621),
	// Message: Departure for Gludin Harbor will take place in one minute!
	DEPARTURE_FOR_GLUDIN_HARBOR_WILL_TAKE_PLACE_IN_ONE_MINUTE(1622),
	// Message: We are now departing for Gludin Harbor. Hold on and enjoy the ride!
	WE_ARE_NOW_DEPARTING_FOR_GLUDIN_HARBOR_HOLD_ON_AND_ENJOY_THE_RIDE(1624),
	// Message: Departure for Rune Harbor will take place after anchoring for ten minutes.
	DEPARTURE_FOR_RUNE_HARBOR_WILL_TAKE_PLACE_AFTER_ANCHORING_FOR_TEN_MINUTES(1625),
	// Message: Departure for Rune Harbor will take place in five minutes!
	DEPARTURE_FOR_RUNE_HARBOR_WILL_TAKE_PLACE_IN_FIVE_MINUTES(1626),
	// Message: Departure for Rune Harbor will take place in one minute!
	DEPARTURE_FOR_RUNE_HARBOR_WILL_TAKE_PLACE_IN_ONE_MINUTE(1627),
	// Message: Make haste!  We will be departing for Gludin Harbor shortly…
	MAKE_HASTE__WE_WILL_BE_DEPARTING_FOR_GLUDIN_HARBOR_SHORTLY(1628),
	// Message: We are now departing for Rune Harbor. Hold on and enjoy the ride!
	WE_ARE_NOW_DEPARTING_FOR_RUNE_HARBOR_HOLD_ON_AND_ENJOY_THE_RIDE(1629),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 15 minutes.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_15_MINUTES(1630),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 10 minutes.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_10_MINUTES(1631),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 5 minutes.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_5_MINUTES(1632),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 1 minute.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_1_MINUTE(1633),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 15 minutes.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_15_MINUTES(1634),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 10 minutes.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_10_MINUTES(1635),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 5 minutes.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_5_MINUTES(1636),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 1 minute.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_1_MINUTE(1637),
	// Message: You cannot fish while using a recipe book, private manufacture or private store.
	YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE(1638),
	// Message: Sharpen your swords, tighten the stitching in your armor, and make haste to a Grand Olympiad Manager!  Battles in the Grand Olympiad Games are now taking place!
	SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_GRAND_OLYMPIAD_MANAGER__BATTLES_IN_THE_GRAND_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE(1641),
	// Message: Much carnage has been left for the cleanup crew of the Olympiad Stadium.  Battles in the Grand Olympiad Games are now over!
	MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM(1642),
	// Message: The Grand Olympiad Games are not currently in progress.
	THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS(1651),
	// Message: You caught something smelly and scary, maybe you should throw it back!?
	YOU_CAUGHT_SOMETHING_SMELLY_AND_SCARY_MAYBE_YOU_SHOULD_THROW_IT_BACK(1655),
	// Message: $c1 has earned $s2 points in the Grand Olympiad Games.
	C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES(1657),
	// Message: $c1 has lost $s2 points in the Grand Olympiad Games.
	C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES(1658),
	// Message: The clan crest was successfully registered.  Remember, only a clan that owns a clan hall or castle can display a crest.
	THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED(1663),
	// Message: Lethal Strike!
	LETHAL_STRIKE(1667),
	// Message: Your lethal strike was successful!
	YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL(1668),
	// Message: There was nothing found inside.
	THERE_WAS_NOTHING_FOUND_INSIDE(1669),
	// Message: Due to your Reeling and/or Pumping skill being three or more levels higher than your Fishing skill, a 50 damage penalty will be applied.
	DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED(1670),
	// Message: For the current Grand Olympiad you have participated in $s1 match(es). $s2 win(s) and $s3 defeat(s). You currently have $s4 Olympiad Point(s).
	FOR_THE_CURRENT_GRAND_OLYMPIAD_YOU_HAVE_PARTICIPATED_IN_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_CURRENTLY_HAVE_S4_OLYMPIAD_POINTS(1673),
	// Message: This command can only be used by a Noblesse.
	THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE(1674),
	// Message: A manor cannot be set up between 4:30 am and 8 pm.
	A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM(1675),
	// Message: A cease-fire during a Clan War can not be called while members of your clan are engaged in battle.
	A_CEASEFIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE(1677),
	// Message: You have not declared a Clan War against the clan $s1.
	YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_AGAINST_THE_CLAN_S1(1678),
	// Message: Only the creator of a command channel can issue a global command.
	ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND(1679),
	// Message: $c1 has declined the channel invitation.
	C1_HAS_DECLINED_THE_CHANNEL_INVITATION(1680),
	// Message: Only the creator of a command channel can use the channel dismiss command.
	ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_USE_THE_CHANNEL_DISMISS_COMMAND(1682),
	// Message: Only a party leader can leave a command channel.
	ONLY_A_PARTY_LEADER_CAN_LEAVE_A_COMMAND_CHANNEL(1683),
	// Message: This area cannot be entered while mounted atop of a Wyvern.  You will be dismounted from your Wyvern if you do not leave!
	THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN(1687),
	// Message: You cannot enchant while operating a Private Store or Private Workshop.
	YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(1688),
	// Message: $c1 is already registered on the class match waiting list.
	C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST(1689),
	// Message: $c1 is already registered on the waiting list for the class irrelevant individual match.
	C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_CLASS_IRRELEVANT_INDIVIDUAL_MATCH(1690),
	// Message: You may not observe a Grand Olympiad Games match while you are on the waiting list.
	YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST(1693),
	// Message: Only a clan leader that is a Noblesse can view the Siege War Status window during a siege war.
	ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR(1694),
	// Message: You cannot dismiss a party member by force.
	YOU_CANNOT_DISMISS_A_PARTY_MEMBER_BY_FORCE(1699),
	// Message: You don't have enough spiritshots needed for a pet/servitor.
	YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR(1700),
	// Message: You don't have enough soulshots needed for a pet/servitor.
	YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PETSERVITOR(1701),
	// Message: You are short of accumulated points.
	YOU_ARE_SHORT_OF_ACCUMULATED_POINTS(1710),
	// Message: You cannot join a Command Channel while teleporting.
	YOU_CANNOT_JOIN_A_COMMAND_CHANNEL_WHILE_TELEPORTING(1729),
	// Message: To join a Clan Academy, characters must be Level 40 or below, not belong another clan and not yet completed their 2nd class transfer.
	TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER(1734),
	// Message: Your clan has already established a Clan Academy.
	YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY(1738),
	// Message: Clan Academy member $s1 has successfully Awakened, obtaining $s2 Clan Reputation.
	CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_AWAKENED_OBTAINING_S2_CLAN_REPUTATION(1748),
	// Message: Congratulations! You will now graduate from the Clan Academy and leave your current clan. As a graduate of the academy, you can immediately join a clan as a regular member without being subject to any penalties.
	CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN(1749),
	// Message: $s2 has been designated as the apprentice of clan member $s1.
	S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1(1755),
	// Message: Your apprentice, $c1, has logged in.
	YOUR_APPRENTICE_C1_HAS_LOGGED_IN(1756),
	// Message: Your apprentice, $c1, has logged out.
	YOUR_APPRENTICE_C1_HAS_LOGGED_OUT(1757),
	// Message: Your sponsor, $c1, has logged in.
	YOUR_SPONSOR_C1_HAS_LOGGED_IN(1758),
	// Message: Your sponsor, $c1, has logged out.
	YOUR_SPONSOR_C1_HAS_LOGGED_OUT(1759),
	// Message: $s2, clan member $c1's apprentice, has been removed.
	S2_CLAN_MEMBER_C1S_APPRENTICE_HAS_BEEN_REMOVED(1763),
	// Message: This item can only be worn by a member of the Clan Academy.
	THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY(1764),
	// Message: Since your clan emerged victorious from the siege, $s1 points have been added to your clan's reputation score.
	SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE(1773),
	// Message: Now that your clan level is above Level 5, it can accumulate clan reputation points.
	NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS(1771),
	// Message: Your clan has failed to defend the castle. $s1 points have been deducted from your clan's reputation score and added to your opponents'.
	YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOU_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENTS(1784),
	// Message: $s1 points have been deducted from the clan's Reputation.
	S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_THE_CLANS_REPUTATION(1787),
	// Message: The clan skill $s1 has been added.
	THE_CLAN_SKILL_S1_HAS_BEEN_ADDED(1788),
	// Message: Since the Clan Reputation Score has dropped to 0 or lower, your clan skill(s) will be de-activated.
	SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DEACTIVATED(1789),
	// Message: The conditions necessary to create a military unit have not been met.
	THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET(1791),
	// Message: The attempt to sell has failed.
	THE_ATTEMPT_TO_SELL_HAS_FAILED(1801),
	// Message: The attempt to trade has failed.
	THE_ATTEMPT_TO_TRADE_HAS_FAILED(1802),
	// Message: The registration period for a clan hall war has ended.
	THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED(1823),
	// Message: You have been registered for a clan hall war.  Please move to the left side of the clan hall's arena and get ready.
	YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR(1824),
	// Message: You have failed in your attempt to register for the clan hall war. Please try again.
	YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR(1825),
	// Message: In $s1 minute(s), the game will begin. All players must hurry and move to the left side of the clan hall's arena.
	IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALLS_ARENA(1826),
	// Message: In $s1 minute(s), the game will begin. All players, please enter the arena now.
	IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW(1827),
	// Message: In $s1 second(s), the game will begin.
	IN_S1_SECONDS_THE_GAME_WILL_BEGIN(1828),
	// Message: $c1 is not allowed to use the party room invite command. Please update the waiting list.
	C1_IS_NOT_ALLOWED_TO_USE_THE_PARTY_ROOM_INVITE_COMMAND(1830),
	// Message: $c1 does not meet the conditions of the party room. Please update the waiting list.
	C1_DOES_NOT_MEET_THE_CONDITIONS_OF_THE_PARTY_ROOM(1831),
	// Message: Only a room leader may invite others to a party room.
	ONLY_A_ROOM_LEADER_MAY_INVITE_OTHERS_TO_A_PARTY_ROOM(1832),
	// Message: The party room is full. No more characters can be invited in.
	THE_PARTY_ROOM_IS_FULL(1834),
	// Message: $s1 is full and cannot accept additional clan members at this time.
	S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME(1835),
	// Message: This clan hall war has been cancelled.  Not enough clans have registered.
	THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED(1841),
	// Message: $c1 wishes to summon you from $s2. Do you accept?
	C1_WISHES_TO_SUMMON_YOU_FROM_S2(1842),
	// Message: $c1 is engaged in combat and cannot be summoned.
	C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED(1843),
	// Message: $c1 is dead at the moment and cannot be summoned.
	C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED(1844),
	// Message: Hero weapons cannot be destroyed.
	HERO_WEAPONS_CANNOT_BE_DESTROYED(1845),
	// Message: Another military unit is already using that name. Please enter a different name.
	ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME(1855),
	// Message: The Clan Reputation Score is too low.
	THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW(1860),
	// Message: The Clan Mark has been deleted.
	THE_CLAN_MARK_HAS_BEEN_DELETED(1861),
	// Message: Clan skills will now be activated since the clan's reputation score is 0 or higher.
	CLAN_SKILLS_WILL_NOW_BE_ACTIVATED_SINCE_THE_CLANS_REPUTATION_SCORE_IS_0_OR_HIGHER(1862),
	// Message: Your pet/servitor is unresponsive and will not obey any orders.
	YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS(1864),
	// Message: The preliminary match will begin in $s1 second(s). Prepare yourself.
	THE_PRELIMINARY_MATCH_WILL_BEGIN_IN_S1_SECONDS(1881),
	// Message: There are no offerings I own or I made a bid for.
	THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR(1883),
	// Message: You may not use items in a private store or private work shop.
	YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP(1891),
	// Message: A sub-class cannot be created or changed while you are over your weight limit.
	A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT(1894),
	// Message: $c1 is currently trading or operating a private store and cannot be summoned.
	C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED(1898),
	// Message: Your target is in an area which blocks summoning.
	YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING(1899),
	// Message: $c1 has entered the party room.
	C1_HAS_ENTERED_THE_PARTY_ROOM(1900),
	// Message: $s1 has sent an invitation to room <$s2>.
	S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2(1901),
	// Message: Incompatible item grade.  This item cannot be used.
	INCOMPATIBLE_ITEM_GRADE(1902),
	// Message: A sub-class may not be created or changed while a servitor or pet is summoned.
	A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED(1904),
	// Message: You cannot summon players who are currently participating in the Grand Olympiad.
	YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD(1911),
	// Message: Your pet is too high level to control.
	YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL(1918),
	// Message: Court Magician: The portal has been created!
	COURT_MAGICIAN_THE_PORTAL_HAS_BEEN_CREATED(1923),
	// Message: There is no opponent to receive your challenge for a duel.
	THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL(1926),
	// Message: $c1 has been challenged to a duel.
	C1_HAS_BEEN_CHALLENGED_TO_A_DUEL(1927),
	// Message: $c1's party has been challenged to a duel.
	C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL(1928),
	// Message: $c1 has accepted your challenge to a duel. The duel will begin in a few moments.
	C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL(1929),
	// Message: You have accepted $c1's challenge a duel. The duel will begin in a few moments.
	YOU_HAVE_ACCEPTED_C1S_CHALLENGE_A_DUEL(1930),
	// Message: $c1 has declined your challenge to a duel.
	C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1931),
	// Message: $c1 has declined your challenge to a duel.
	C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL_(1932),
	// Message: You have accepted $c1's challenge to a party duel. The duel will begin in a few moments.
	YOU_HAVE_ACCEPTED_C1S_CHALLENGE_TO_A_PARTY_DUEL(1933),
	// Message: $s1 has accepted your challenge to duel against their party. The duel will begin in a few moments.
	S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY(1934),
	// Message: $c1 has declined your challenge to a party duel.
	C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_PARTY_DUEL(1935),
	// Message: The opposing party has declined your challenge to a duel.
	THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1936),
	// Message: Since the person you challenged is not currently in a party, they cannot duel against your party.
	SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY(1937),
	// Message: $c1 has challenged you to a duel.
	C1_HAS_CHALLENGED_YOU_TO_A_DUEL(1938),
	// Message: $c1's party has challenged your party to a duel.
	C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL(1939),
	// Message: You are unable to request a duel at this time.
	YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME(1940),
	// Message: In a moment, you will be transported to the site where the duel will take place.
	IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE(1944),
	// Message: The duel will begin in $s1 second(s).
	THE_DUEL_WILL_BEGIN_IN_S1_SECONDS(1945),
	// Message: Let the duel begin!
	LET_THE_DUEL_BEGIN(1949),
	// Message: $c1 has won the duel.
	C1_HAS_WON_THE_DUEL(1950),
	// Message: $c1's party has won the duel.
	C1S_PARTY_HAS_WON_THE_DUEL(1951),
	// Message: The duel has ended in a tie.
	THE_DUEL_HAS_ENDED_IN_A_TIE(1952),
	// Message: Select the item to be augmented.
	SELECT_THE_ITEM_TO_BE_AUGMENTED(1957),
	// Message: Select the catalyst for augmentation.
	SELECT_THE_CATALYST_FOR_AUGMENTATION(1958),
	// Message: This is not a suitable item.
	THIS_IS_NOT_A_SUITABLE_ITEM(1960),
	// Message: Gemstone quantity is incorrect.
	GEMSTONE_QUANTITY_IS_INCORRECT(1961),
	// Message: The item was successfully augmented!
	THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED(1962),
	// Message: Select the item from which you wish to remove augmentation.
	SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION(1963),
	// Message: Augmentation removal can only be done on an augmented item.
	AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM(1964),
	// Message: Augmentation has been successfully removed from your $s1.
	AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1(1965),
	// Message: Only the clan leader may issue commands.
	ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS(1966),
	// Message: Once an item is augmented, it cannot be augmented again.
	ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN(1970),
	// Message: You cannot augment items while a private store or private workshop is in operation.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION(1972),
	// Message: You cannot augment items while dead.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD(1974),
	// Message: You cannot augment items while paralyzed.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED(1976),
	// Message: You cannot augment items while fishing.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING(1977),
	// Message: You cannot augment items while sitting down.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN(1978),
	// Message: S1
	S1(1983),
	// Message: Press the Augment button to begin.
	PRESS_THE_AUGMENT_BUTTON_TO_BEGIN(1984),
	// Message: The ferry has arrived at Primeval Isle.
	THE_FERRY_HAS_ARRIVED_AT_PRIMEVAL_ISLE(1988),
	// Message: The ferry will leave for Rune Harbor after anchoring for three minutes.
	THE_FERRY_WILL_LEAVE_FOR_RUNE_HARBOR_AFTER_ANCHORING_FOR_THREE_MINUTES(1989),
	// Message: Your Death Penalty has been lifted.
	YOUR_DEATH_PENALTY_HAS_BEEN_LIFTED(1917),
	// Message: The ferry is now departing Primeval Isle for Rune Harbor.
	THE_FERRY_IS_NOW_DEPARTING_PRIMEVAL_ISLE_FOR_RUNE_HARBOR(1990),
	// Message: The ferry will leave for Primeval Isle after anchoring for three minutes.
	THE_FERRY_WILL_LEAVE_FOR_PRIMEVAL_ISLE_AFTER_ANCHORING_FOR_THREE_MINUTES(1991),
	// Message: The ferry is now departing Rune Harbor for Primeval Isle.
	THE_FERRY_IS_NOW_DEPARTING_RUNE_HARBOR_FOR_PRIMEVAL_ISLE(1992),
	// Message: The ferry from Primeval Isle to Rune Harbor has been delayed.
	THE_FERRY_FROM_PRIMEVAL_ISLE_TO_RUNE_HARBOR_HAS_BEEN_DELAYED(1993),
	// Message: The ferry from Rune Harbor to Primeval Isle has been delayed.
	THE_FERRY_FROM_RUNE_HARBOR_TO_PRIMEVAL_ISLE_HAS_BEEN_DELAYED(1994),
	// Message: The attack has been blocked.
	THE_ATTACK_HAS_BEEN_BLOCKED(1996),
	// Message: $c1 is performing a counterattack.
	C1_IS_PERFORMING_A_COUNTERATTACK(1997),
	// Message: Augmentation failed due to inappropriate conditions.
	AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS(2001),
	// Message: $c1 cannot duel because $c1 is currently engaged in a private store or manufacture.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE(2017),
	// Message: $c1 cannot duel because $c1 is currently fishing.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING(2018),
	// Message: $c1 cannot duel because $c1's HP or MP is below 50%.
	C1_CANNOT_DUEL_BECAUSE_C1S_HP_OR_MP_IS_BELOW_50(2019),
	// Message: $c1 cannot make a challenge to a duel because $c1 is currently in a duel-prohibited area (Peaceful Zone / Seven Signs Zone / Near Water / Restart Prohibited Area).
	C1_CANNOT_MAKE_A_CHALLENGE_TO_A_DUEL_BECAUSE_C1_IS_CURRENTLY_IN_A_DUELPROHIBITED_AREA_PEACEFUL_ZONE__SEVEN_SIGNS_ZONE__NEAR_WATER__RESTART_PROHIBITED_AREA(2020),
	// Message: $c1 cannot duel because $c1 is currently engaged in battle.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE(2021),
	// Message: $c1 cannot duel because $c1 is already engaged in a duel.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL(2022),
	// Message: $c1 cannot duel because $c1 is in a chaotic state.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_IN_A_CHAOTIC_STATE(2023),
	// Message: $c1 cannot duel because $c1 is participating in the Olympiad.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD(2024),
	// Message: $c1 cannot duel because $c1 is participating in a clan hall war.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR(2025),
	// Message: $c1 cannot duel because $c1 is participating in a siege war.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_SIEGE_WAR(2026),
	// Message: $c1 cannot duel because $c1 is currently riding a boat, steed, or strider.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_STEED_OR_STRIDER(2027),
	// Message: $c1 cannot receive a duel challenge because $c1 is too far away.
	C1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_C1_IS_TOO_FAR_AWAY(2028),
	// Message: A sub-class cannot be created or changed because you have exceeded your inventory limit.
	A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT(2033),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed to trade items and/or Adena.  To unlock all of the features of Lineage II, purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_(2039),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed buy items from private stores. To unlock all of the features of Lineage II, purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____(2046),
	// Message: $s1 clan is trying to display a flag.
	S1_CLAN_IS_TRYING_TO_DISPLAY_A_FLAG(2050),
	// Message: You have blocked $c1.
	YOU_HAVE_BLOCKED_C1(2057),
	// Message: You already polymorphed and cannot polymorph again.
	YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN(2058),
	// Message: You cannot polymorph into the desired form in water.
	YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER(2060),
	// Message: You cannot polymorph when you have summoned a servitor/pet.
	YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITORPET(2062),
	// Message: You cannot polymorph while riding a pet.
	YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET(2063),
	// Message: You cannot polymorph while under the effect of a special skill.
	YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL(2064),
	// Message: That weapon cannot perform any attacks.
	THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS(2066),
	// Message: That weapon cannot use any other skill except the weapon's skill.
	THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL(2067),
	// Message: Untrain of enchant skill was successful. Current level of enchant skill $s1 has been decreased by 1.
	UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL_CURRENT_LEVEL_OF_ENCHANT_SKILL_S1_HAS_BEEN_DECREASED_BY_1(2069),
	// Message: Untrain of enchant skill was successful. Current level of enchant skill $s1 became 0 and enchant skill will be initialized.
	UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL_CURRENT_LEVEL_OF_ENCHANT_SKILL_S1_BECAME_0_AND_ENCHANT_SKILL_WILL_BE_INITIALIZED(2070),
	// Message: It is not an auction period.
	IT_IS_NOT_AN_AUCTION_PERIOD(2075),
	// Message: Bidding is not allowed because the maximum bidding price exceeds 100 billion.
	BIDDING_IS_NOT_ALLOWED_BECAUSE_THE_MAXIMUM_BIDDING_PRICE_EXCEEDS_100_BILLION(2076),
	// Message: Your bid must be higher than the current highest bid.
	YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID(2077),
	// Message: You do not have enough adena for this bid.
	YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID(2078),
	// Message: You have been outbid.
	YOU_HAVE_BEEN_OUTBID(2080),
	// Message: There are no funds presently due to you.
	THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU(2081),
	// Message: Enemy Blood Pledges have intruded into the fortress.
	ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS(2084),
	// Message: Shout and trade chatting cannot be used while possessing a cursed weapon.
	SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON(2085),
	// Message: Search on user $c2 for third-party program use will be completed in $s1 minute(s).
	SEARCH_ON_USER_C2_FOR_THIRDPARTY_PROGRAM_USE_WILL_BE_COMPLETED_IN_S1_MINUTES(2086),
	// Message: A fortress is under attack!
	A_FORTRESS_IS_UNDER_ATTACK(2087),
	// Message: $s1 minute(s) until the fortress battle starts.
	S1_MINUTES_UNTIL_THE_FORTRESS_BATTLE_STARTS(2088),
	// Message: $s1 second(s) until the fortress battle starts.
	S1_SECONDS_UNTIL_THE_FORTRESS_BATTLE_STARTS(2089),
	// Message: The fortress battle $s1 has begun.
	THE_FORTRESS_BATTLE_S1_HAS_BEGUN(2090),
	// Message: $c1 is in a location which cannot be entered, therefore it cannot be processed.
	C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED(2096),
	// Message: $c1's level does not correspond to the requirements for entry.
	C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY(2097),
	// Message: $c1's quest requirement is not sufficient and cannot be entered.
	C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED(2098),
	// Message: $c1's item requirement is not sufficient and cannot be entered.
	C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED(2099),
	// Message: $c1 may not re-enter yet.
	C1_MAY_NOT_REENTER_YET(2100),
	// Message: You are not currently in a party, so you cannot enter.
	YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER(2101),
	// Message: You cannot enter due to the party having exceeded the limit.
	YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT(2102),
	// Message: You cannot enter because you are not associated with the current command channel.
	YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL(2103),
	// Message: The maximum number of instance zones has been exceeded. You cannot enter.
	THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED(2104),
	// Message: You have entered another instance zone, therefore you cannot enter corresponding dungeon.
	YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON(2105),
	// Message: This dungeon will expire in $s1 minute(s). You will be forced out of the dungeon when the time expires.
	THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES(2106),
	// Message: You cannot convert this item.
	YOU_CANNOT_CONVERT_THIS_ITEM(2130),
	// Message: You have bid the highest price and have won the item. The item can be found in your personal warehouse.
	YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM(2131),
	// Message: You cannot add elemental power while operating a Private Store or Private Workshop.
	YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(2143),
	// Message: Please select item to add elemental power.
	PLEASE_SELECT_ITEM_TO_ADD_ELEMENTAL_POWER(2144),
	// Message: Attribute item usage has been cancelled.
	ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED(2145),
	// Message: You have failed to add elemental power.
	YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER(2149),
	// Message: Another elemental power has already been added. This elemental power cannot be added.
	ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED(2150),
	// Message: Your opponent has resistance to magic, the damage was decreased.
	YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED(2151),
	// Message: The target is not a flagpole so a flag cannot be displayed.
	THE_TARGET_IS_NOT_A_FLAGPOLE_SO_A_FLAG_CANNOT_BE_DISPLAYED(2154),
	// Message: A flag is already being displayed, another flag cannot be displayed.
	A_FLAG_IS_ALREADY_BEING_DISPLAYED_ANOTHER_FLAG_CANNOT_BE_DISPLAYED(2155),
	// Message: There are not enough necessary items to use the skill.
	THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL(2156),
	// Message: Force attack is impossible against a temporary allied member during a siege.
	FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE(2158),
	// Message: Bidder exists, the auction time has been extended by 5 minutes.
	BIDDER_EXISTS_THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES(2159),
	// Message: Bidder exists, auction time has been extended by 3 minutes.
	BIDDER_EXISTS_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES(2160),
	// Message: There is not enough space to move, the skill cannot be used.
	THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED(2161),
	// Message: The barracks have been seized.
	THE_BARRACKS_HAVE_BEEN_SEIZED(2164),
	// Message: The barracks function has been restored.
	THE_BARRACKS_FUNCTION_HAS_BEEN_RESTORED(2165),
	// Message: All barracks are occupied.
	ALL_BARRACKS_ARE_OCCUPIED(2166),
	// Message: A malicious skill cannot be used in a peace zone.
	A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE(2167),
	// Message: $c1 has acquired the flag.
	C1_HAS_ACQUIRED_THE_FLAG(2168),
	// Message: Your clan has been registered to $s1's fortress battle.
	YOUR_CLAN_HAS_BEEN_REGISTERED_TO_S1S_FORTRESS_BATTLE(2169),
	// Message: This item cannot be crystallized.
	THIS_ITEM_CANNOT_BE_CRYSTALLIZED(2171),
	// Message: $c1 cannot duel because $c1 is currently polymorphed.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_POLYMORPHED(2174),
	// Message: Party duel cannot be initiated due to a polymorphed party member.
	PARTY_DUEL_CANNOT_BE_INITIATED_DUE_TO_A_POLYMORPHED_PARTY_MEMBER(2175),
	// Message: You cannot polymorph while riding a boat.
	YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT(2182),
	// Message: The fortress battle of $s1 has finished.
	THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED(2183),
	// Message: $s1 is victorious in the fortress battle of $s2.
	S1_IS_VICTORIOUS_IN_THE_FORTRESS_BATTLE_OF_S2(2184),
	// Message: Only a party leader can make the request to enter.
	ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER(2185),
	// Message: Soul cannot be absorbed anymore.
	SOUL_CANNOT_BE_ABSORBED_ANYMORE(2186),
	// Message: The target is located where you cannot charge.
	THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE(2187),
	// Message: You are too far from the NPC for that to work.
	YOU_ARE_TOO_FAR_FROM_THE_NPC_FOR_THAT_TO_WORK(2193),
	// Message: You do not have enough souls.
	YOU_DO_NOT_HAVE_ENOUGH_SOULS(2195),
	// Message: This is an area where you cannot use the mini map. The mini map cannot be opened.
	THIS_IS_AN_AREA_WHERE_YOU_CANNOT_USE_THE_MINI_MAP(2207),
	// Message: You cannot board a ship while you are polymorphed.
	YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED(2213),
	// Message: The ballista has been successfully destroyed. The clan's reputation will be increased.
	THE_BALLISTA_HAS_BEEN_SUCCESSFULLY_DESTROYED(2217),
	// Message: This squad skill has already been acquired.
	THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_ACQUIRED(2219),
	// Message: The previous level skill has not been learned.
	THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED(2220),
	// Message: Not enough bolts.
	NOT_ENOUGH_BOLTS(2226),
	// Message: It is not possible to register for the castle siege side or castle siege of a higher castle in the contract.
	IT_IS_NOT_POSSIBLE_TO_REGISTER_FOR_THE_CASTLE_SIEGE_SIDE_OR_CASTLE_SIEGE_OF_A_HIGHER_CASTLE_IN_THE_CONTRACT(2227),
	// Message: Instance zone time limit:
	INSTANCE_ZONE_TIME_LIMIT(2228),
	// Message: There is no instance zone under a time limit.
	THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT(2229),
	// Message: $s1 will be available for re-use after $s2 hour(s) $s3 minute(s).
	S1_WILL_BE_AVAILABLE_FOR_REUSE_AFTER_S2_HOURS_S3_MINUTES(2230),
	// Message: Siege registration is not possible due to your castle contract.
	SIEGE_REGISTRATION_IS_NOT_POSSIBLE_DUE_TO_YOUR_CASTLE_CONTRACT(2233),
	// Message: You are participating in the siege of $s1. This siege is scheduled for 2 hours.
	YOU_ARE_PARTICIPATING_IN_THE_SIEGE_OF_S1_THIS_SIEGE_IS_SCHEDULED_FOR_2_HOURS(2238),
	// Message: $s1 minute(s) remaining.
	S1_MINUTES_REMAINING(2244),
	// Message: $s1 second(s) remaining.
	S1_SECONDS_REMAINING(2245),
	// Message: The contest will begin in $s1 minute(s).
	THE_CONTEST_WILL_BEGIN_IN_S1_MINUTES(2246),
	// Message: You cannot board an airship while transformed.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED(2247),
	// Message: You cannot board an airship while petrified.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED(2248),
	// Message: You cannot board an airship while dead.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD(2249),
	// Message: You cannot board an airship while fishing.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING(2250),
	// Message: You cannot board an airship while in battle.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE(2251),
	// Message: You cannot board an airship while in a duel.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL(2252),
	// Message: You cannot board an airship while sitting.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING(2253),
	// Message: You cannot board an airship while casting.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING(2254),
	// Message: You cannot board an airship when a cursed weapon is equipped.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHEN_A_CURSED_WEAPON_IS_EQUIPPED(2255),
	// Message: You cannot board an airship while holding a flag.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG(2256),
	// Message: You cannot board an airship while a pet or a servitor is summoned.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED(2257),
	// Message: You have already boarded another airship.
	YOU_HAVE_ALREADY_BOARDED_ANOTHER_AIRSHIP(2258),
	// Message: Your pet's hunger gauge is below 10%. If your pet isn't fed soon, it may run away.
	YOUR_PETS_HUNGER_GAUGE_IS_BELOW_10(2260),
	// Message: $c1 has done $s3 points of damage to $c2.
	C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2(2261),
	// Message: $c1 has received $s3 damage from $c2.
	C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2(2262),
	// Message: $c1 has evaded $c2's attack.
	C1_HAS_EVADED_C2S_ATTACK(2264),
	// Message: $c1's attack went astray.
	C1S_ATTACK_WENT_ASTRAY(2265),
	// Message: $c1 resisted $c2's magic.
	C1_RESISTED_C2S_MAGIC(2269),
	// Message: This skill cannot be learned while in the sub-class state. Please try again after changing to the main class.
	THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE(2273),
	// Message: Damage is decreased because $c1 resisted $c2's magic.
	DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2S_MAGIC(2280),
	// Message: $c1 inflicted $s3 damage on $c2 and $s4 damage on the damage transfer target.
	C1_INFLICTED_S3_DAMAGE_ON_C2_AND_S4_DAMAGE_ON_THE_DAMAGE_TRANSFER_TARGET(2281),
	// Message: You cannot transform while sitting.
	YOU_CANNOT_TRANSFORM_WHILE_SITTING(2283),
	// Message: You cannot wear $s1 because you are not wearing a bracelet.
	YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET(2286),
	// Message: You cannot equip $s1 because you do not have any available slots.
	YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS(2287),
	// Message: Agathion skills can be used only when your Agathion is summoned.
	AGATHION_SKILLS_CAN_BE_USED_ONLY_WHEN_YOUR_AGATHION_IS_SUMMONED(2292),
	// Message: There are $s2 second(s) remaining in $s1's re-use time.
	THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME(2303),
	// Message: There are $s2 minute(s), $s3 second(s) remaining in $s1's re-use time.
	THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME(2304),
	// Message: There are $s2 hour(s), $s3 minute(s), and $s4 second(s) remaining in $s1's re-use time.
	THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME(2305),
	// Message: Your Charm of Courage is trying to resurrect you. Would you like to resurrect now?
	YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU(2306),
	// Message: You do not have a servitor.
	YOU_DO_NOT_HAVE_A_SERVITOR(2311),
	// Message: Your Vitality is at maximum.
	YOUR_VITALITY_IS_AT_MAXIMUM(2314),
	// Message: Your Vitality has increased.
	YOUR_VITALITY_HAS_INCREASED(2315),
	// Message: Your Vitality has decreased.
	YOUR_VITALITY_HAS_DECREASED(2316),
	// Message: Your Vitality is fully exhausted.
	YOUR_VITALITY_IS_FULLY_EXHAUSTED(2317),
	// Message: You have acquired 50 Clan Fame Points.
	YOU_HAVE_ACQUIRED_50_CLAN_FAME_POINTS(2326),
	// Message: You don't have enough reputation to do that.
	YOU_DONT_HAVE_ENOUGH_REPUTATION_TO_DO_THAT(2327),
	// Message: Only clans who are level 4 or above can register for battle at Devastated Castle and Fortress of the Dead.
	ONLY_CLANS_WHO_ARE_LEVEL_4_OR_ABOVE_CAN_REGISTER_FOR_BATTLE_AT_DEVASTATED_CASTLE_AND_FORTRESS_OF_THE_DEAD(2328),
	// Message: You cannot receive the vitamin item because you have exceed your inventory weight/quantity limit.
	YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHTQUANTITY_LIMIT(2333),
	// Message: There are no more vitamin items to be found.
	THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND(2335),
	// Message: CP Siphon!
	CP_SIPHON(2336),
	// Message: Your CP was drained because you were hit with a CP siphon skill.
	YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL(2337),
	// Message: $s1 seconds to game end!
	S1_SECONDS_TO_GAME_END(2347),
	// Message: You cannot use My Teleports during a battle.
	YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE(2348),
	// Message: You cannot use My Teleports while participating a large-scale battle such as a castle siege, fortress siege, or hideout siege.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGESCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_HIDEOUT_SIEGE(2349),
	// Message: You cannot use My Teleports during a duel.
	YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL(2350),
	// Message: You cannot use My Teleports while flying.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING(2351),
	// Message: You cannot use My Teleports while participating in an Olympiad match.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH(2352),
	// Message: You cannot use My Teleports while you are in a petrified or paralyzed state.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_PETRIFIED_OR_PARALYZED_STATE(2353),
	// Message: You cannot use My Teleports while you are dead.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD(2354),
	// Message: You cannot use My Teleports in this area.
	YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA(2355),
	// Message: You cannot use My Teleports underwater.
	YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER(2356),
	// Message: You cannot use My Teleports in an instant zone.
	YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE(2357),
	// Message: You have no space to save the teleport location.
	YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION(2358),
	// Message: You cannot teleport because you do not have a teleport item.
	YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM(2359),
	// Message: Resurrection will take place in the waiting room after $s1 seconds.
	RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS(2370),
	// Message: $c1 was reported as a BOT.
	C1_WAS_REPORTED_AS_A_BOT(2371),
	// Message: End match!
	END_MATCH(2374),
	// Message: You cannot receive a vitamin item during an exchange.
	YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE(2376),
	// Message: You cannot report a character who is in a peace zone or a battleground.
	YOU_CANNOT_REPORT_A_CHARACTER_WHO_IS_IN_A_PEACE_ZONE_OR_A_BATTLEGROUND(2377),
	// Message: You cannot report when a clan war has been declared.
	YOU_CANNOT_REPORT_WHEN_A_CLAN_WAR_HAS_BEEN_DECLARED(2378),
	// Message: You cannot report a character who has not acquired any XP after connecting.
	YOU_CANNOT_REPORT_A_CHARACTER_WHO_HAS_NOT_ACQUIRED_ANY_XP_AFTER_CONNECTING(2379),
	// Message: You cannot report this person again at this time.
	YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME(2380),
	// Message: A party cannot be formed in this area.
	A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA(2388),
	// Message: You have used the Feather of Blessing to resurrect.
	YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT(2391),
	// Message: That pet/servitor skill cannot be used because it is recharging.
	THAT_PET_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING(2396),
	// Message: Instant Zone currently in use: $s1
	INSTANT_ZONE_CURRENTLY_IN_USE_S1(2400),
	// Message: Clan lord $c2, who leads clan $s1, has been declared the lord of the $s3 territory.
	CLAN_LORD_C2_WHO_LEADS_CLAN_S1_HAS_BEEN_DECLARED_THE_LORD_OF_THE_S3_TERRITORY(2401),
	// Message: The Territory War request period has ended.
	THE_TERRITORY_WAR_REQUEST_PERIOD_HAS_ENDED(2402),
	// Message: The Territory War begins in 10 minutes!
	THE_TERRITORY_WAR_BEGINS_IN_10_MINUTES(2403),
	// Message: The Territory War begins in 5 minutes!
	THE_TERRITORY_WAR_BEGINS_IN_5_MINUTES(2404),
	// Message: The Territory War begins in 1 minute!
	THE_TERRITORY_WAR_BEGINS_IN_1_MINUTE(2405),
	// Message: You are currently registered for a 3 vs. 3 class irrelevant team match.
	YOU_ARE_CURRENTLY_REGISTERED_FOR_A_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH(2408),
	// Message: The number of My Teleports slots has been increased.
	THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED(2409),
	// Message: You cannot use My Teleports to reach this area!
	YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA(2410),
	// Message: The collection has failed.
	THE_COLLECTION_HAS_FAILED(2424),
	// Message: The Red Team is victorious.
	THE_RED_TEAM_IS_VICTORIOUS(2427),
	// Message: The Blue Team is victorious.
	THE_BLUE_TEAM_IS_VICTORIOUS(2428),
	// Message: $c1 is already registered on the waiting list for the 3 vs. 3 class irrelevant team match.
	C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH(2440),
	// Message: Only a party leader can request a team match.
	ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH(2441),
	// Message: The request cannot be made because the requirements have not been met. To participate in a team match, you must first form a 3-member party.
	THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET(2442),
	// Message: The battlefield channel has been activated.
	THE_BATTLEFIELD_CHANNEL_HAS_BEEN_ACTIVATED(2445),
	// Message: The battlefield channel has been deactivated.
	THE_BATTLEFIELD_CHANNEL_HAS_BEEN_DEACTIVATED(2446),
	// Message: Five years have passed since this character's creation.
	FIVE_YEARS_HAVE_PASSED_SINCE_THIS_CHARACTERS_CREATION(2447),
	// Message: Your birthday gift has arrived. You can obtain it from the Gatekeeper in any village.
	YOUR_BIRTHDAY_GIFT_HAS_ARRIVED(2448),
	// Message: There are $s1 days until your character's birthday. On that day, you can obtain a special gift from the Gatekeeper in any village.
	THERE_ARE_S1_DAYS_UNTIL_YOUR_CHARACTERS_BIRTHDAY(2449),
	// Message: $c1's birthday is $s3/$s4/$s2.
	C1S_BIRTHDAY_IS_S3S4S2(2450),
	// Message: Your cloak has been unequipped because your armor set is no longer complete.
	YOUR_CLOAK_HAS_BEEN_UNEQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NO_LONGER_COMPLETE(2451),
	// Message: The cloak cannot be equipped because your armor set is not complete.
	THE_CLOAK_CANNOT_BE_EQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NOT_COMPLETE(2453),
	// Message: In order to acquire an airship, the clan's level must be level 5 or higher.
	IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLANS_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER(2456),
	// Message: An airship cannot be summoned because either you have not registered your airship license, or the airship has not yet been summoned.
	AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_EITHER_YOU_HAVE_NOT_REGISTERED_YOUR_AIRSHIP_LICENSE_OR_THE_AIRSHIP_HAS_NOT_YET_BEEN_SUMMONED(2457),
	// Message: Your clan's airship is already being used by another clan member.
	YOUR_CLANS_AIRSHIP_IS_ALREADY_BEING_USED_BY_ANOTHER_CLAN_MEMBER(2458),
	// Message: The Airship Summon License has already been acquired.
	THE_AIRSHIP_SUMMON_LICENSE_HAS_ALREADY_BEEN_ACQUIRED(2459),
	// Message: The clan owned airship already exists.
	THE_CLAN_OWNED_AIRSHIP_ALREADY_EXISTS(2460),
	// Message: An airship cannot be summoned because you don't have enough $s1.
	AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DONT_HAVE_ENOUGH_S1(2462),
	// Message: The airship's fuel (EP) will soon run out.
	THE_AIRSHIPS_FUEL_EP_WILL_SOON_RUN_OUT(2463),
	// Message: The airship's fuel (EP) has run out. The airship's speed will be greatly decreased in this condition.
	THE_AIRSHIPS_FUEL_EP_HAS_RUN_OUT(2464),
	// Message: A pet on auxiliary mode cannot use skills.
	A_PET_ON_AUXILIARY_MODE_CANNOT_USE_SKILLS(2466),
	// Message: You have used a report point on $c1. You have $s2 points remaining on this account.
	YOU_HAVE_USED_A_REPORT_POINT_ON_C1_YOU_HAVE_S2_POINTS_REMAINING_ON_THIS_ACCOUNT(2468),
	// Message: You have used all available points. Points are reset everyday at noon.
	YOU_HAVE_USED_ALL_AVAILABLE_POINTS_POINTS_ARE_RESET_EVERYDAY_AT_NOON(2469),
	// Message: This character cannot make a report. You cannot make a report while located inside a peace zone or a battleground, while you are an opposing clan member during a clan war, or while participating in the Olympiad.
	THIS_CHARACTER_CANNOT_MAKE_A_REPORT_YOU_CANNOT_MAKE_A_REPORT_WHILE_LOCATED_INSIDE_A_PEACE_ZONE_OR_A_BATTLEGROUND_WHILE_YOU_ARE_AN_OPPOSING_CLAN_MEMBER_DURING_A_CLAN_WAR_OR_WHILE_PARTICIPATING_IN_THE_OLYMPIAD(2470),
	// Message: This character cannot make a report. The target has already been reported by either your clan or alliance, or has already been reported from your current IP.
	THIS_CHARACTER_CANNOT_MAKE_A_REPORT_THE_TARGET_HAS_ALREADY_BEEN_REPORTED_BY_EITHER_YOUR_CLAN_OR_ALLIANCE_OR_HAS_ALREADY_BEEN_REPORTED_FROM_YOUR_CURRENT_IP(2471),
	// Message: You have been reported as an illegal program user, so your chatting will be blocked for 10 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_CHATTING_WILL_BE_BLOCKED_FOR_10_MINUTES(2473),
	// Message: You have been reported as an illegal program user, so your party participation will be blocked for 60 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_FOR_60_MINUTES(2474),
	// Message: You have been reported as an illegal program user, so your actions will be restricted for 120 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_120_MINUTES(2477),
	// Message: You have been reported as an illegal program user, so your actions will be restricted for 180 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_180_MINUTES(2478),
	// Message: You have been reported as an illegal program user, so movement is prohibited for 120 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_MOVEMENT_IS_PROHIBITED_FOR_120_MINUTES(2480),
	// Message: $c1 has been reported as an illegal program user and cannot join a party.
	C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY(2482),
	// Message: You have been reported as an illegal program user, so participating in a party is not allowed.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED(2484),
	// Message: Your ship cannot teleport because it does not have enough fuel for the trip.
	YOUR_SHIP_CANNOT_TELEPORT_BECAUSE_IT_DOES_NOT_HAVE_ENOUGH_FUEL_FOR_THE_TRIP(2491),
	// Message: The $s1 ward has been destroyed! $c2 now has the territory ward.
	THE_S1_WARD_HAS_BEEN_DESTROYED_C2_NOW_HAS_THE_TERRITORY_WARD(2750),
	// Message: The character that acquired $s1's ward has been killed.
	THE_CHARACTER_THAT_ACQUIRED_S1S_WARD_HAS_BEEN_KILLED(2751),
	// Message: You cannot enter because you do not meet the requirements.
	YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2706),
	// Message: You cannot register while in possession of a cursed weapon.
	YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON(2708),
	// Message: Applicants for the Olympiad, Underground Coliseum, or Kratei's Cube matches cannot register.
	APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEIS_CUBE_MATCHES_CANNOT_REGISTER(2709),
	// Message: You cannot board because you do not meet the requirements.
	YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2727),
	// Message: You cannot control the helm while transformed.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_TRANSFORMED(2729),
	// Message: You cannot control the helm while you are petrified.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_YOU_ARE_PETRIFIED(2730),
	// Message: You cannot control the helm when you are dead.
	YOU_CANNOT_CONTROL_THE_HELM_WHEN_YOU_ARE_DEAD(2731),
	// Message: You cannot control the helm while fishing.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_FISHING(2732),
	// Message: You cannot control the helm while in a battle.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_BATTLE(2733),
	// Message: You cannot control the helm while in a duel.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_DUEL(2734),
	// Message: You cannot control the helm while in a sitting position.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_SITTING_POSITION(2735),
	// Message: You cannot control the helm while using a skill.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_USING_A_SKILL(2736),
	// Message: You cannot control the helm while a cursed weapon is equipped.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_A_CURSED_WEAPON_IS_EQUIPPED(2737),
	// Message: You cannot control the helm while holding a flag.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_HOLDING_A_FLAG(2738),
	// Message: You cannot control the helm because you do not meet the requirements.
	YOU_CANNOT_CONTROL_THE_HELM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2739),
	// Message: This action is prohibited while steering.
	THIS_ACTION_IS_PROHIBITED_WHILE_STEERING(2740),
	// Message: You have been reported as an illegal program user and cannot report other users.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_REPORT_OTHER_USERS(2748),
	// Message: This type of attack is prohibited when allied troops are the target.
	THIS_TYPE_OF_ATTACK_IS_PROHIBITED_WHEN_ALLIED_TROOPS_ARE_THE_TARGET(2753),
	// Message: You cannot be simultaneously registered for PVP matches such as the Olympiad, Underground Coliseum, Aerial Cleft, Kratei's Cube, and Handy's Block Checkers.
	YOU_CANNOT_BE_SIMULTANEOUSLY_REGISTERED_FOR_PVP_MATCHES_SUCH_AS_THE_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEIS_CUBE_AND_HANDYS_BLOCK_CHECKERS(2754),
	// Message: Another player is probably controlling the target.
	ANOTHER_PLAYER_IS_PROBABLY_CONTROLLING_THE_TARGET(2756),
	// Message: You must target the one you wish to control.
	YOU_MUST_TARGET_THE_ONE_YOU_WISH_TO_CONTROL(2761),
	// Message: You cannot control because you are too far.
	YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR(2762),
	// Message: Only the alliance channel leader can attempt entry.
	ONLY_THE_ALLIANCE_CHANNEL_LEADER_CAN_ATTEMPT_ENTRY(2765),
	// Message: You can make another report in $s1-minute(s). You have $s2 point(s) remaining on this account.
	YOU_CAN_MAKE_ANOTHER_REPORT_IN_S1MINUTES_YOU_HAVE_S2_POINTS_REMAINING_ON_THIS_ACCOUNT(2774),
	// Message: The effect of territory ward is disappearing.
	THE_EFFECT_OF_TERRITORY_WARD_IS_DISAPPEARING(2776),
	// Message: The airship summon license has been entered. Your clan can now summon the airship.
	THE_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_ENTERED(2777),
	// Message: You cannot teleport while in possession of a ward.
	YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD(2778),
	// Message: $c1 has drained you of $s2 HP.
	C1_HAS_DRAINED_YOU_OF_S2_HP(2787),
	// Message: Mercenary participation is requested in $s1 territory.
	MERCENARY_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY(2788),
	// Message: Mercenary participation request is cancelled in $s1 territory.
	MERCENARY_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY(2789),
	// Message: Clan participation is requested in $s1 territory.
	CLAN_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY(2790),
	// Message: Clan participation request is cancelled in $s1 territory.
	CLAN_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY(2791),
	// Message: You must have a minimum of ($s1) people to enter this Instant Zone. Your request for entry is denied.
	YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE(2793),
	// Message: The territory war channel and functions will now be deactivated.
	THE_TERRITORY_WAR_CHANNEL_AND_FUNCTIONS_WILL_NOW_BE_DEACTIVATED(2794),
	// Message: You've already requested a territory war in another territory elsewhere.
	YOUVE_ALREADY_REQUESTED_A_TERRITORY_WAR_IN_ANOTHER_TERRITORY_ELSEWHERE(2795),
	// Message: The clan who owns the territory cannot participate in the territory war as mercenaries.
	THE_CLAN_WHO_OWNS_THE_TERRITORY_CANNOT_PARTICIPATE_IN_THE_TERRITORY_WAR_AS_MERCENARIES(2796),
	// Message: It is not a territory war registration period, so a request cannot be made at this time.
	IT_IS_NOT_A_TERRITORY_WAR_REGISTRATION_PERIOD_SO_A_REQUEST_CANNOT_BE_MADE_AT_THIS_TIME(2797),
	// Message: The territory war will end in $s1-hour(s).
	THE_TERRITORY_WAR_WILL_END_IN_S1HOURS(2798),
	// Message: The territory war will end in $s1-minute(s).
	THE_TERRITORY_WAR_WILL_END_IN_S1MINUTES(2799),
	// Message: $s1-second(s) to the end of territory war!
	S1_SECONDS_TO_THE_END_OF_TERRITORY_WAR(2900),
	// Message: You cannot force attack a member of the same territory.
	YOU_CANNOT_FORCE_ATTACK_A_MEMBER_OF_THE_SAME_TERRITORY(2901),
	// Message: You've acquired the ward. Move quickly to your forces' outpost.
	YOUVE_ACQUIRED_THE_WARD(2902),
	// Message: Territory war has begun.
	TERRITORY_WAR_HAS_BEGUN(2903),
	// Message: Territory war has ended.
	TERRITORY_WAR_HAS_ENDED(2904),
	// Message: You've requested $c1 to be on your Friends List.
	YOUVE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST(2911),
	// Message: Clan $s1 has succeeded in capturing $s2's territory ward.
	CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2S_TERRITORY_WARD(2913),
	// Message: The territory war will begin in 20 minutes! Territory related functions (i.e.: battlefield channel, Disguise Scrolls, Transformations, etc...) can now be used.
	THE_TERRITORY_WAR_WILL_BEGIN_IN_20_MINUTES(2914),
	// Message: This clan member cannot withdraw or be expelled while participating in a territory war.
	THIS_CLAN_MEMBER_CANNOT_WITHDRAW_OR_BE_EXPELLED_WHILE_PARTICIPATING_IN_A_TERRITORY_WAR(2915),
	// Message: Only characters who are level 40 or above who have completed their second class transfer can register in a territory war.
	ONLY_CHARACTERS_WHO_ARE_LEVEL_40_OR_ABOVE_WHO_HAVE_COMPLETED_THEIR_SECOND_CLASS_TRANSFER_CAN_REGISTER_IN_A_TERRITORY_WAR(2918),
	// Message: The disguise scroll cannot be used because it is meant for use in a different territory.
	THE_DISGUISE_SCROLL_CANNOT_BE_USED_BECAUSE_IT_IS_MEANT_FOR_USE_IN_A_DIFFERENT_TERRITORY(2936),
	// Message: A territory owning clan member cannot use a disguise scroll.
	A_TERRITORY_OWNING_CLAN_MEMBER_CANNOT_USE_A_DISGUISE_SCROLL(2937),
	// Message: The disguise scroll cannot be used while you are engaged in a private store or manufacture workshop.
	THE_DISGUISE_SCROLL_CANNOT_BE_USED_WHILE_YOU_ARE_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE_WORKSHOP(2938),
	// Message: A disguise cannot be used when you are in a chaotic state.
	A_DISGUISE_CANNOT_BE_USED_WHEN_YOU_ARE_IN_A_CHAOTIC_STATE(2939),
	// Message: The territory war exclusive disguise and transformation can be used 20 minutes before the start of the territory war to 10 minutes after its end.
	THE_TERRITORY_WAR_EXCLUSIVE_DISGUISE_AND_TRANSFORMATION_CAN_BE_USED_20_MINUTES_BEFORE_THE_START_OF_THE_TERRITORY_WAR_TO_10_MINUTES_AFTER_ITS_END(2955),
	// Message: A character born on February 29 will receive a gift on February 28.
	A_CHARACTER_BORN_ON_FEBRUARY_29_WILL_RECEIVE_A_GIFT_ON_FEBRUARY_28(2957),
	// Message: An Agathion has already been summoned.
	AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED(2958),
	// Message: The previous mail was forwarded less than 1 minute ago and this cannot be forwarded.
	THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED(2969),
	// Message: You cannot forward in a non-peace zone location.
	YOU_CANNOT_FORWARD_IN_A_NONPEACE_ZONE_LOCATION(2970),
	// Message: You cannot forward during an exchange.
	YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE(2971),
	// Message: You cannot forward because the private shop or workshop is in progress.
	YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS(2972),
	// Message: You cannot forward during an item enhancement or attribute enhancement.
	YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT(2973),
	// Message: The item that you're trying to send cannot be forwarded because it isn't proper.
	THE_ITEM_THAT_YOURE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISNT_PROPER(2974),
	// Message: You cannot forward because you don't have enough adena.
	YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA(2975),
	// Message: You cannot receive in a non-peace zone location.
	YOU_CANNOT_RECEIVE_IN_A_NONPEACE_ZONE_LOCATION(2976),
	// Message: You cannot receive during an exchange.
	YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE(2977),
	// Message: You cannot receive because the private shop or workshop is in progress.
	YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS(2978),
	// Message: You cannot receive during an item enhancement or attribute enhancement.
	YOU_CANNOT_RECEIVE_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT(2979),
	// Message: You cannot receive because you don't have enough adena.
	YOU_CANNOT_RECEIVE_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA(2980),
	// Message: You could not receive because your inventory is full.
	YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL(2981),
	// Message: You cannot cancel in a non-peace zone location.
	YOU_CANNOT_CANCEL_IN_A_NONPEACE_ZONE_LOCATION(2982),
	// Message: You cannot cancel during an exchange.
	YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE(2983),
	// Message: You cannot cancel because the private shop or workshop is in progress.
	YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS(2984),
	// Message: You cannot cancel during an item enhancement or attribute enhancement.
	YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT(2985),
	// Message: You could not cancel receipt because your inventory is full.
	YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL(2988),
	// Message: The Command Channel matching room was cancelled.
	THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED(2994),
	// Message: You cannot enter the Command Channel matching room because you do not meet the requirements.
	YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2996),
	// Message: You exited from the Command Channel matching room.
	YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM(2997),
	// Message: You were expelled from the Command Channel matching room.
	YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM(2998),
	// Message: The Command Channel affiliated party's party member cannot use the matching screen.
	THE_COMMAND_CHANNEL_AFFILIATED_PARTYS_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN(2999),
	// Message: The Command Channel matching room was created.
	THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED(3000),
	// Message: The Command Channel matching room information was edited.
	THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED(3001),
	// Message: When the recipient doesn't exist or the character has been deleted, sending mail is not possible.
	WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE(3002),
	// Message: $c1 entered the Command Channel matching room.
	C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM(3003),
	// Message: Shyeed's roar filled with wrath rings throughout the Stakato Nest.
	SHYEEDS_ROAR_FILLED_WITH_WRATH_RINGS_THROUGHOUT_THE_STAKATO_NEST(3007),
	// Message: The mail has arrived.
	THE_MAIL_HAS_ARRIVED(3008),
	// Message: Mail successfully sent.
	MAIL_SUCCESSFULLY_SENT(3009),
	// Message: Mail successfully cancelled.
	MAIL_SUCCESSFULLY_CANCELLED(3011),
	// Message: The Kasha's Eye gives you a strange feeling.
	THE_KASHAS_EYE_GIVES_YOU_A_STRANGE_FEELING(3022),
	// Message: I can feel that the energy being flown in the Kasha's eye is getting stronger rapidly.
	I_CAN_FEEL_THAT_THE_ENERGY_BEING_FLOWN_IN_THE_KASHAS_EYE_IS_GETTING_STRONGER_RAPIDLY(3023),
	// Message: Kasha's eye pitches and tosses like it's about to explode.
	KASHAS_EYE_PITCHES_AND_TOSSES_LIKE_ITS_ABOUT_TO_EXPLODE(3024),
	// Message: You cannot use the skill enhancing function on this level. You can use the corresponding function on levels higher than 76Lv .
	YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL_YOU_CAN_USE_THE_CORRESPONDING_FUNCTION_ON_LEVELS_HIGHER_THAN_76LV_(3026),
	// Message: You cannot use the skill enhancing function in this class. You can use corresponding function when completing the third class change.
	YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_CORRESPONDING_FUNCTION_WHEN_COMPLETING_THE_THIRD_CLASS_CHANGE(3027),
	// Message: You cannot use the skill enhancing function in this class. You can use the skill enhancing function under off-battle status, and cannot use the function while transforming, battling and on-board.
	YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING_FUNCTION_UNDER_OFFBATTLE_STATUS_AND_CANNOT_USE_THE_FUNCTION_WHILE_TRANSFORMING_BATTLING_AND_ONBOARD(3028),
	// Message: $s1 acquired the attached item to your mail.
	S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL(3072),
	// Message: You have acquired $s2 $s1.
	YOU_HAVE_ACQUIRED_S2_S1(3073),
	// Message: A user currently participating in the Olympiad cannot send party and friend invitations.
	A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS(3094),
	// Message: You are no longer protected from aggressive monsters.
	YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS(3108),
	// Message: Mail successfully received.
	MAIL_SUCCESSFULLY_RECEIVED(3012),
	// Message: You cannot send a mail to yourself.
	YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF(3019),
	// Message: The couple action was denied.
	THE_COUPLE_ACTION_WAS_DENIED(3119),
	// Message: The request cannot be completed because the target does not meet location requirements.
	THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS(3120),
	// Message: The couple action was cancelled.
	THE_COUPLE_ACTION_WAS_CANCELLED(3121),
	// Message: The size of the uploaded symbol does not meet the standard requirements.
	THE_SIZE_OF_THE_UPLOADED_SYMBOL_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS(3122),
	// Message: $c1 is already participating in a couple action and cannot be requested for another couple action.
	C1_IS_ALREADY_PARTICIPATING_IN_A_COUPLE_ACTION_AND_CANNOT_BE_REQUESTED_FOR_ANOTHER_COUPLE_ACTION(3126),
	// Message: $c1 is in a chaotic state and cannot be requested for a couple action.
	C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION(3127),
	// Message: The crest was successfully registered.
	THE_CREST_WAS_SUCCESSFULLY_REGISTERED(3140),
	// Message: This item cannot be used because you are already participating in the quest that can be started with this item.
	THIS_ITEM_CANNOT_BE_USED_BECAUSE_YOU_ARE_ALREADY_PARTICIPATING_IN_THE_QUEST_THAT_CAN_BE_STARTED_WITH_THIS_ITEM(3145),
	// Message: You have requested a couple action with $c1.
	YOU_HAVE_REQUESTED_A_COUPLE_ACTION_WITH_C1(3150),
	// Message: $c1 is set to refuse duel requests and cannot receive a duel request.
	C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST(3169),
	// Message: When your pet's hunger gauge is at 0%, you cannot use your pet.
	WHEN_YOUR_PETS_HUNGER_GAUGE_IS_AT_0_YOU_CANNOT_USE_YOUR_PET(3212),
	// Message: Your pet is starving and will not obey until it gets it's food. Feed your pet!
	YOUR_PET_IS_STARVING_AND_WILL_NOT_OBEY_UNTIL_IT_GETS_ITS_FOOD(3213),
	// Message: $s1 was successfully added to your Contact List.
	S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST(3214),
	// Message: The name $s1%  doesn't exist. Please try another name.
	THE_NAME_S1__DOESNT_EXIST(3215),
	// Message: The name already exists on the added list.
	THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST(3216),
	// Message: The name is not currently registered.
	THE_NAME_IS_NOT_CURRENTLY_REGISTERED(3217),
	// Message: $s1 was successfully deleted from your Contact List.
	S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST(3219),
	// Message: You cannot add your own name.
	YOU_CANNOT_ADD_YOUR_OWN_NAME(3221),
	// Message: The maximum number of names (100) has been reached. You cannot register any more.
	THE_MAXIMUM_NUMBER_OF_NAMES_100_HAS_BEEN_REACHED(3222),
	// Message: The maximum matches you can participate in 1 week is 70.
	THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_70(3224),
	// Message: The total number of matches that can be entered in 1 week is 60 class irrelevant individual matches, 30 specific matches, and 10 team matches.
	THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES(3225),
	// Message: You cannot move while speaking to an NPC. One moment please.
	YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC(3226),
	// Message: MP became 0 and the Arcane Shield is disappearing.
	MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING(3256),
	// Message: You have acquired $s1 EXP (Bonus: $s2) and $s3 SP (Bonus: $s4).
	YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4(3259),
	// Message: You have $s1 match(es) remaining that you can participate in this week ($s2 1 vs 1 Class matches, $s3 1 vs 1 matches, & $s4 3 vs 3 Team matches).
	YOU_HAVE_S1_MATCHES_REMAINING_THAT_YOU_CAN_PARTICIPATE_IN_THIS_WEEK_S2_1_VS_1_CLASS_MATCHES_S3_1_VS_1_MATCHES__S4_3_VS_3_TEAM_MATCHES(3261),
	// Message: There are $s2 seconds remaining for $s1's re-use time. It is reset every day at 6:30 AM.
	THERE_ARE_S2_SECONDS_REMAINING_FOR_S1S_REUSE_TIME(3263),
	// Message: There are $s2 minutes $s3 seconds remaining for $s1's re-use time. It is reset every day at 6:30 AM.
	THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1S_REUSE_TIME(3264),
	// Message: There are $s2 hours $s3 minutes $s4 seconds remaining for $s1's re-use time. It is reset every day at 6:30 AM.
	THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1S_REUSE_TIME(3265),
	// Message: $c1 is set to refuse couple actions and cannot be requested for a couple action.
	C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION(3164),
	// Message: Arcane Shield decreased your MP by $s1 instead of HP.
	ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP(3255),
	// Message: The angel Nevit has blessed you from above. You are imbued with full Vitality as well as a Vitality Replenishing effect. And should you die, you will not lose Exp!
	THE_ANGEL_NEVIT_HAS_BLESSED_YOU_FROM_ABOVE_YOU_ARE_IMBUED_WITH_FULL_VITALITY_AS_WELL_AS_A_VITALITY_REPLENISHING_EFFECT(3266),
	// Message: You are starting to feel the effects of Nevit's Advent Blessing.
	YOU_ARE_STARTING_TO_FEEL_THE_EFFECTS_OF_NEVITS_BLESSING(3267),
	// Message: You are further infused with the blessings of Nevit! Continue to battle evil wherever it may lurk.
	YOU_ARE_FURTHER_INFUSED_WITH_THE_BLESSINGS_OF_NEVIT_CONTINUE_TO_BATTLE_EVIL_WHEREVER_IT_MAY_LURK(3268),
	// Message: Nevit's Advent Blessing shines strongly from above. You can almost see his divine aura.
	NEVITS_BLESSING_SHINES_STRONGLY_FROM_ABOVE_YOU_CAN_ALMOST_SEE_HIS_DIVINE_AURA(3269),
	// Message: Nevit's Advent Blessing has ended. Continue your journey and you will surely meet his favor again sometime soon.
	NEVITS_BLESSING_HAS_ENDED_CONTINUE_YOUR_JOURNEY_AND_YOU_WILL_SURELY_MEET_HIS_FAVOR_AGAIN_SOMETIME_SOON(3275),
	// Message: Subclass $s1 has been upgraded to Duel Class $s2. Congratulations!
	SUBCLASS_S1_HAS_BEEN_UPGRADED_TO_DUEL_CLASS_S2_CONGRATULATIONS(3279),
	// Message: The number of graduates of the Clan Academy is $s1. $s2 bonus points have been added to your Clan Reputation.
	THE_NUMBER_OF_GRADUATES_OF_THE_CLAN_ACADEMY_IS_S1_S2_BONUS_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION(3299),
	// Message: Entry to Memo is complete.
	ENTRY_TO_MEMO_IS_COMPLETE(3332),
	// Message: Registration is not available because the corresponding item does not exist.
	REGISTRATION_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST(3361),
	// Message: The item cannot be registered because requirements are not met.
	THE_ITEM_CANNOT_BE_REGISTERED_BECAUSE_REQUIREMENTS_ARE_NOT_MET(3363),
	// Message: Not enough adena for register this item.
	NOT_ENOUGH_ADENA_FOR_REGISTER_THIS_ITEM(3364),
	// Message: Failed to register item.
	FAILED_TO_REGISTER_ITEM(3365),
	// Message: Currently, there are no registered items.
	CURRENTLY_THERE_ARE_NO_REGISTERED_ITEMS(3369),
	// Message: Item Purchase is not available because the corresponding item does not exist.
	ITEM_PURCHASE_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST(3370),
	// Message: Item Purchase has failed.
	ITEM_PURCHASE_HAS_FAILED(3371),
	// Message: You cannot move while dead.
	YOU_CANNOT_MOVE_WHILE_DEAD(3392),
	// Message: You cannot move during combat.
	YOU_CANNOT_MOVE_DURING_COMBAT(3393),
	// Message: You cannot move while in a chaotic state.
	YOU_CANNOT_MOVE_WHILE_IN_A_CHAOTIC_STATE(3404),
	// Message: $s1 already graduated from a Clan Academy, therefore re-joining is not allowed.
	S1_ALREADY_GRADUATED_FROM_A_CLAN_ACADEMY_THEREFORE_REJOINING_IS_NOT_ALLOWED(3420),
	// Message: Congratulations! You will now graduate from the Clan Academy and leave your current clan. As a graduate of the academy, you can immediately join a clan as a regular member without being subject to any penalties.
	CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES(3430),
	// Message: Stopped searching the party.
	STOPPED_SEARCHING_THE_PARTY(3453),
	// Message: $c1 used $s3 on $c2.
	C1_USED_S3_ON_C2(3463),
	// Message: Your bid for the Provisional Clan Hall won.
	YOUR_BID_FOR_THE_PROVISIONAL_CLAN_HALL_WON(3465),
	// Message: Your bid for the Provisional Clan Hall lost.
	YOUR_BID_FOR_THE_PROVISIONAL_CLAN_HALL_LOST(3466),
	// Message: Clan Level requirements for bidding are not met.
	CLAN_LEVEL_REQUIREMENTS_FOR_BIDDING_ARE_NOT_MET(3467),
	// Message: You made a bid at $s1.
	YOU_MADE_A_BID_AT_S1(3468),
	// Message: You already made a bid for the Provisional Clan Hall.
	YOU_ALREADY_MADE_A_BID_FOR_THE_PROVISIONAL_CLAN_HALL(3469),
	// Message: It is not the bidding period for the Provisional Clan Hall.
	IT_IS_NOT_THE_BIDDING_PERIOD_FOR_THE_PROVISIONAL_CLAN_HALL(3470),
	// Message: You cannot make a bid because you don't belong to a clan.
	YOU_CANNOT_MAKE_A_BID_BECAUSE_YOU_DONT_BELONG_TO_A_CLAN(3471),
	// Message: You must have rights to a Clan Hall auction in order to make a bid for Provisional Clan Hall.
	YOU_MUST_HAVE_RIGHTS_TO_A_CLAN_HALL_AUCTION_IN_ORDER_TO_MAKE_A_BID_FOR_PROVISIONAL_CLAN_HALL(3472),
	// Message: Items that cannot be exchanged/dropped/use a private store or that are for a limited period/augmenting cannot be registered.
	ITEMS_THAT_CANNOT_BE_EXCHANGEDDROPPEDUSE_A_PRIVATE_STORE_OR_THAT_ARE_FOR_A_LIMITED_PERIODAUGMENTING_CANNOT_BE_REGISTERED(3480),
	// Message: If the weight is 80% or more and the inventory number is 90% or more, purchase/cancellation is not possible.
	IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASECANCELLATION_IS_NOT_POSSIBLE(3481),
	// Message: Item register was successful.
	ITEM_REGISTER_WAS_SUCCESSFUL(3484),
	// Message: Cancellation of Sale for the item is successful.
	CANCELLATION_OF_SALE_FOR_THE_ITEM_IS_SUCCESSFUL(3485),
	// Message: The item you registered has been sold.
	THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD(3490),
	// Message: $s1 has been sold.
	S1_HAS_BEEN_SOLD(3491),
	// Message: The registration period for the item you registered has expired.
	THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED(3492),
	// Message: The auction house registration period has expired and the corresponding item is being forwarded.
	THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED(3493),
	// Message: You cannot receive a symbol because you are below the required level.
	YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_ARE_BELOW_THE_REQUIRED_LEVEL(3498),
	// Message: You cannot receive a symbol because you don't have enough Adena.
	YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA(3499),
	// Message: You cannot receive a symbol because you don't have enough dye.
	YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_DONT_HAVE_ENOUGH_DYE(3500),
	// Message: You cannot receive a symbol because you don't meet the class requirements.
	YOU_CANNOT_RECEIVE_A_SYMBOL_BECAUSE_YOU_DONT_MEET_THE_CLASS_REQUIREMENTS(3501),
	// Message: A replacement player for $c1 has been found, and an invitation is sent.
	A_REPLACEMENT_PLAYER_FOR_C1_HAS_BEEN_FOUND_AND_AN_INVITATION_IS_SENT(3511),
	// Message: The player who was invited rejected the invitation. Please register again.
	THE_PLAYER_WHO_WAS_INVITED_REJECTED_THE_INVITATION_PLEASE_REGISTER_AGAIN(3512),
	// Message: Waiting list registration is cancelled because the cursed sword is being used or the status is in a chaotic state.
	WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE(3513),
	// Message: Waiting list registration is cancelled because you are in a duel.
	WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_IN_A_DUEL(3514),
	// Message: Waiting list registration is cancelled because you are currently participating in Olympiad.
	WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_CURRENTLY_PARTICIPATING_IN_OLYMPIAD(3515),
	// Message: Waiting list registration is cancelled because you are currently participating in Block Checker/Coliseum/Kratei's Cube.
	WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_CURRENTLY_PARTICIPATING_IN_BLOCK_CHECKERCOLISEUMKRATEIS_CUBE(3516),
	// Message: You cannot register in the waiting list while being inside of a battleground (castle siege/fortress siege/territory war).
	YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_BEING_INSIDE_OF_A_BATTLEGROUND_CASTLE_SIEGEFORTRESS_SIEGETERRITORY_WAR(3517),
	// Message: Waiting list registration is not allowed while the cursed sword is being used or the status is in a chaotic state.
	WAITING_LIST_REGISTRATION_IS_NOT_ALLOWED_WHILE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE(3518),
	// Message: You cannot register in the waiting list during a duel.
	YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_DURING_A_DUEL(3519),
	// Message: You cannot register in the waiting list while participating in Olympiad.
	YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_OLYMPIAD(3520),
	// Message: You cannot register in the waiting list while participating in Block Checker/Coliseum/Kratei's Cube.
	YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_BLOCK_CHECKERCOLISEUMKRATEIS_CUBE(3521),
	// Message: You cannot register in the waiting list while being inside of a battleground (castle siege/fortress siege/territory war).
	YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_BEING_INSIDE_OF_A_BATTLEGROUND_CASTLE_SIEGEFORTRESS_SIEGETERRITORY_WAR2(3522),
	// Message: Looking for a player who will replace the selected party member.
	LOOKING_FOR_A_PLAYER_WHO_WILL_REPLACE_THE_SELECTED_PARTY_MEMBER(3523),
	// Message: Vitality effect is applied. There's $s1 vitality effect left that may be applied until the next cycle.
	VITALITY_EFFECT_IS_APPLIED_THERES_S1_VITALITY_EFFECT_LEFT_THAT_MAY_BE_APPLIED_UNTIL_THE_NEXT_CYCLE(3528),
	// Message: You have successfully purchased $s2 of $s1.
	YOU_HAVE_SUCCESSFULLY_PURCHASED_S2_OF_S1(3530),
	// Message: You are registered on the waiting list.
	YOU_ARE_REGISTERED_ON_THE_WAITING_LIST(3452),
	// Message: You can not change the class due to disruption of the identification.
	YOU_CAN_NOT_CHANGE_CLASS_DUE_TO_DISRUPTION_OF_THE_IDENTIFICATION(3574),
	// Message: You may not use Sayune while pet or summoned pet is out.
	YOU_MAY_NOT_USE_SAYUNE_WHILE_PET_OR_SUMMONED_PET_IS_OUT(3625),
	// Message: The corresponding work cannot be proceeded because the inventory weight/quantity limit has been exceeded.
	THE_CORRESPONDING_WORK_CANNOT_BE_PROCEEDED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED(3646),
	// Message: You cannot use skills in the corresponding region.
	YOU_CANNOT_USE_SKILLS_IN_THE_CORRESPONDING_REGION(3648),
	// Message: You cannot awaken due to weight limits. Please try awaken again after increasing the allowed weight by organizing the inventory.
	YOU_CANNOT_AWAKEN_DUE_TO_WEIGHT_LIMITS_PLEASE_TRY_AWAKEN_AGAIN_AFTER_INCREASING_THE_ALLOWED_WEIGHT_BY_ORGANIZING_THE_INVENTORY(3652),
	// Message: You cannot use Sayune while in a chaotic state.
	YOU_CANNOT_USE_SAYUNE_WHILE_IN_A_CHAOTIC_STATE(3654),
	// Message: You cannot awaken while you're transformed or riding.
	YOU_CANNOT_AWAKEN_WHILE_YOURE_TRANSFORMED_OR_RIDING(3655),
	// Message: You can not change the attribute while operating a private store or private workshop.
	YOU_CAN_NOT_CHANGE_THE_ATTRIBUTE_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(3659),
	// Message: Unable to change the attribute.
	UNABLE_TO_CHANCE_THE_ATTRIBUTE(3661),
	// Message: First, select the attribute you are going to change.
	FIRST__SELECT_THE_ATTRIBUTE_YOU_ARE_GOING_TO_CHANGE(3667),
	// Message: In the item <$s1> attribute <$s2> successfully changed to <$ s3>.
	IN_THE_ITEM_S1_ATTRIBUTE_S2_SUCCESSFULLY_CHANGED_TO_S3(3668),
	// Message: The item for changing an attribute does not exist.
	THE_ITEM_FOR_CHANGING_AN_ATTRIBUTE_DOES_NOT_EXIST(3669),
	// Message: You can not change the class in a state of transformation.
	YOU_CAN_NOT_CHANGE_CLASS_IN_TRANSFORMATION(3677),
	// Message: This territory can not change class.
	THIS_TERRITORY_CAN_NOT_CHANGE_CLASS(3684),
	// Message: Plunder skill has been already used on this target.
	PLUNDER_SKILL_HAS_BEEN_ALREADY_USED_ON_THIS_TARGET(3712),
	// Message: The character and item recipe levels do not match, so it cannot be used normally.
	THE_CHARACTER_AND_ITEM_RECIPE_LEVELS_DO_NOT_MATCH_SO_IT_CANNOT_BE_USED_NORMALLY(3729),
	// Message: You are now on the waiting list. You will automatically be teleported when the tournament starts, and will be removed from the waiting list if you log out. If you cancel registration (within the last minute of entering the arena after signing up) 30 times or more or forfeit after entering the arena 30 times or more during a cycle, you become ineligible for participation in the Ceremony of Chaos until the next cycle.
	YOU_ARE_NOW_ON_THE_WAITING_LIST(3732),
	// Message: Cycle $s1 of the Ceremony of Chaos has begun.
	CYCLE_S1_OF_THE_CEREMONY_OF_CHAOS_HAS_BEGUN(3730),
	// Message: Cycle $s1 of the Ceremony of Chaos has ended.
	CYCLE_S1_OF_THE_CEREMONY_OF_CHAOS_HAS_ENDED(3731),
	// Message: Only characters level 76 or above may participate in the tournament.
	ONLY_CHARACTERS_LEVEL_76_OR_ABOVE_MAY_PARTICIPATE_IN_THE_TOURNAMENT(3733),
	// Message: You will be moved to the arena in $s1 second(s).
	YOU_WILL_BE_MOVED_TO_THE_ARENA_IN_S1_SECONDS(3737),
	// Message: You cannot chat in the Ceremony of Chaos.
	YOU_CANNOT_CHAT_IN_THE_CEREMONY_OF_CHAOS(3741),
	// Message: You cannot open a private store or workshop in the Ceremony of Chaos.
	YOU_CANNOT_OPEN_A_PRIVATE_STORE_OR_WORKSHOP_IN_THE_CEREMONY_OF_CHAOS(3742),
	// Message: In $s1 second(s), you will be moved to where you were before participating in the Ceremony of Chaos.
	IN_S1_SECONDS_YOU_WILL_BE_MOVED_TO_WHERE_YOU_WERE_BEFORE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS(3749),
	// Message: You are on the waiting list for the Ceremony of Chaos.
	YOU_ARE_ON_THE_WAITING_LIST_FOR_THE_CEREMONY_OF_CHAOS(3777),
	// Message: Registration for the Ceremony of Chaos has begun.
	REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_BEGUN(3781),
	// Message: Registration for the Ceremony of Chaos has ended.
	REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_ENDED(3782),
	// Message: The Ceremony of Chaos is not currently open.
	THE_CEREMONY_OF_CHAOS_IS_NOT_CURRENTLY_OPEN(3784),
	// Message: You cannot invite a friend or party while participating in the Ceremony of Chaos.
	YOU_CANNOT_INVITE_A_FRIEND_OR_PARTY_WHILE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS(3789),
	// Message: You have obtained the first Energy of Destruction. You can obtain up to 2 of these a day, and can begin obtaining them again at 6:30am every day.
	YOU_HAVE_OBTAINED_THE_FIRST_ENERGY_OF_DESTRUCTION_YOU_CAN_OBTAIN_UP_TO_2_OF_THESE_A_DAY_AND_CAN_BEGIN_OBTAINING_THEM_AGAIN_AT_630AM_EVERY_DAY(3791),
	// Message: You have obtained the second Energy of Destruction. You can obtain up to 2 of these a day, and can begin obtaining them again at 6:30am every day.
	YOU_HAVE_OBTAINED_THE_SECOND_ENERGY_OF_DESTRUCTION_YOU_CAN_OBTAIN_UP_TO_2_OF_THESE_A_DAY_AND_CAN_BEGIN_OBTAINING_THEM_AGAIN_AT_630AM_EVERY_DAY(3792),
	// Message: You have obtained $s1 Battle Mark(s) during this round of the Ceremony of Chaos.
	YOU_HAVE_OBTAINED_S1_BATTLE_MARKS_DURING_THIS_ROUND_OF_THE_CEREMONY_OF_CHAOS(3794),
	// Message: A victor had been named in the Ceremony of Chaos.
	A_VICTOR_HAD_BEEN_NAMED_IN_THE_CEREMONY_OF_CHAOS(3795),
	// Message: Current Location: $s1, $s2, $s3 (inside the Ceremony of Chaos)
	CURRENT_LOCATION_S1_S2_S3_INSIDE_THE_CEREMONY_OF_CHAOS(3796),
	// Message: Because $c1 was killed by a clan member of $s2, clan fame points decreased by 1.
	BECAUSE_C1_WAS_KILLED_BY_A_CLAN_MEMBER_OF_S2_CLAN_FAME_POINTS_DECREASED_BY_1(3811),
	// Message: Because a clan member of $s1 was killed by $c2, clan fame points increased by 1.
	BECAUSE_A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_C2_CLAN_FAME_POINTS_INCREASED_BY_1(3812),
	// Message: You cannot participate in the Ceremony of Chaos as a flying transformed object.
	YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_AS_A_FLYING_TRANSFORMED_OBJECT(3853),
	// Message: Only the clan leader or someone with rank management authority may register the clan.
	ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN(4031),
	// Message: You may apply for entry after $s1 minute(s) due to cancelling your application.
	YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTES_DUE_TO_CANCELLING_YOUR_APPLICATION(4038),
	// Message: Entry application complete. Use "Entry Application Info" to check or cancel your application. Application is automatically cancelled after 30 days; if you cancel application, you cannot apply again for 5 minutes.
	ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION(4039),
	// Message: Entry application cancelled. You may apply to a new clan after 5 minutes.
	ENTRY_APPLICATION_CANCELLED_YOU_MAY_APPLY_TO_A_NEW_CLAN_AFTER_5_MINUTES(4040),
	// Message: Entered into waiting list. Name is automatically deleted after 30 days. If "Delete from waiting list" is used, you cannot enter names into the waiting list for 5 minutes.
	ENTERED_INTO_WAITING_LIST_NAME_IS_AUTOMATICALLY_DELETED_AFTER_30_DAYS_IF_DELETE_FROM_WAITING_LIST_IS_USED_YOU_CANNOT_ENTER_NAMES_INTO_THE_WAITING_LIST_FOR_5_MINUTES(4043),
	// Message: You cannot use the $s1 skill due to insufficient summon points.
	YOU_CANNOT_USE_THE_S1_SKILL_DUE_TO_INSUFFICIENT_SUMMON_POINTS(4085),
	// Message: This quest cannot be deleted.
	THIS_QUEST_CANNOT_BE_DELETED(4091),
	// Message: Players can Shout after Lv. $s1.
	PLAYERS_CAN_SHOUT_AFTER_LV_S1(4104),
	// Message: Players can use Trade chat after Lv. $s1.
	PLAYERS_CAN_USE_TRADE_CHAT_AFTER_LV_S1(4105),
	// Message: Players can use general chat after Lv. $s1.
	PLAYERS_CAN_USE_GENERAL_CHAT_AFTER_LV_S1(4106),
	// Message: Players can respond to a whisper, but cannot initiate a whisper until Lv. $s1.
	PLAYERS_CAN_RESPOND_TO_A_WHISPER_BUT_CANNOT_INITIATE_A_WHISPER_UNTIL_LV_S1(4107),
	// Message: You cannot use the Beauty Shop while registered in the Ceremony of Chaos.
	YOU_CANNOT_USE_THE_BEAUTY_SHOP_WHILE_REGISTERED_IN_THE_CEREMONY_OF_CHAOS(4126),
	// Message: You cannot use the Beauty Shop while registered in the Olympiad.
	YOU_CANNOT_USE_THE_BEAUTY_SHOP_WHILE_REGISTERED_IN_THE_OLYMPIAD(4127),
	// Message: Adena distribution has started.
	ADENA_DISTRIBUTION_HAS_STARTED(4150),
	// Message: Adena distribution has been cancelled.
	ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED(4151),
	// Message: You cannot proceed as you are not in an alliance or party.
	YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY(4154),
	// Message: You cannot proceed as you are not a party leader.
	YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER(4156),
	// Message: You cannot proceed as there is insufficient Adena.
	YOU_CANNOT_PROCEED_AS_THERE_IS_INSUFFICIENT_ADENA(4157),
	// Message: Hair accessories will no longer be displayed.
	HAIR_ACCESSORIES_WILL_NO_LONGER_BE_DISPLAYED(4167),
	// Message: Hair accessories will be displayed from now on.
	HAIR_ACCESSORIES_WILL_BE_DISPLAYED_FROM_NOW_ON(4168),
	// Message: Point conversion has failed. Please try again.
	POINT_CONVERSION_HAS_FAILED_PLEASE_TRY_AGAIN(4184),
	// Message: You cannot acquire any more Ability Points.
	YOU_CANNOT_ACQUIRE_ANY_MORE_ABILITY_POINTS(4185),
	// Message: You need $s1 SP to convert to1 Ability Point.
	YOU_NEED_S1_SP_TO_CONVERT_TO1_ABILITY_POINT(4186),
	// Message: The selected Ability will be acquired.
	THE_SELECTED_ABILITY_WILL_BE_ACQUIRED(4187),
	// Message: Failed to acquire Ability. Please try again.
	FAILED_TO_ACQUIRE_ABILITY_PLEASE_TRY_AGAIN(4190),
	// Message: $s1 Adena will be consumed and special points will be reset.
	S1_ADENA_WILL_BE_CONSUMED_AND_SPECIAL_POINTS_WILL_BE_RESET(4191),
	// Message: Abilities can be used by Noblesse Lv. 99 or above.
	ABILITIES_CAN_BE_USED_BY_NOBLESSE_LV_99_OR_ABOVE(4195),
	// Message: Please equip the hair accessory and try again.
	PLEASE_EQUIP_THE_HAIR_ACCESSORY_AND_TRY_AGAIN(4199),
	// Message: You consumed $s1 Raid Points.
	YOU_CONSUMED_S1_RAID_POINTS(4209),
	// Message: You have reached the maximum amount of Raid Points, and can acquire no more.
	YOU_HAVE_REACHED_THE_MAXIMUM_AMOUNT_OF_RAID_POINTS_AND_CAN_ACQUIRE_NO_MORE(4210),
	// Message: Not enough Raid Points.
	NOT_ENOUGH_RAID_POINTS(4211),
	// Message: You cannot participate in the Ceremony of Chaos while fishing.
	YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_WHILE_FISHING(4215),
	// Message: You cannot participate in the Olympiad while fishing.
	YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_WHILE_FISHING(4216),
	// Message: This is not a valid combination.
	THIS_IS_NOT_A_VALID_COMBINATION(4221),
	// Message: You can use the world chat $s1 times.
	YOU_CAN_USE_THE_WORLD_CHAT_S1_TIMES(4238),
	// Message: Today you reached the limit of use of the world chat. Reset of the world use the chat is done daily at 6:30 am.
	TODAY_YOU_REACHED_THE_LIMIT_OF_USE_OF_THE_WORLD_CHAT__RESET_OF_THE_WORLD_USE_CHAT_IS_DONE_DAILY_AT_6_30_AM(4239),
	// Message: You can use the world chat with $s1 level.
	YOU_CAN_USE_THE_WORLD_CHAT_WITH_S1_LEVEL(4240),
	// Message: Using a Scroll of Escape can help you speed along your quest.
	USING_A_SCROLL_OF_ESCAPE_CAN_HELP_YOU_SPEED_ALONG_YOUR_QUEST(4243),
	// Message: Lady Luck smiles upon you!
	LADY_LUCK_SMILES_UPON_YOU(4244),
	// Message: Evaded killing blow. Lady Luck watches over you!
	EVADED_KILLING_BLOW_LADY_LUCK_WATCHES_OVER_YOU(4245),
	// Message: You cannot use Alchemy during battle.
	YOU_CANNOT_USE_ALCHEMY_DURING_BATTLE(4270),
	// Message: You cannot use Alchemy while trading or using a private store or shop.
	YOU_CANNOT_USE_ALCHEMY_WHILE_TRADING_OR_USING_A_PRIVATE_STORE_OR_SHOP(4280),
	// Message: You cannot use Alchemy while dead.
	YOU_CANNOT_USE_ALCHEMY_WHILE_DEAD(4281),
	// Message: You cannot use Alchemy while immobile.
	YOU_CANNOT_USE_ALCHEMY_WHILE_IMMOBILE(4282),
	// Message: You cannot change your subclass while registered in the Ceremony of Chaos.
	YOU_CANNOT_CHANGE_YOUR_SUBCLASS_WHILE_REGISTERED_IN_THE_CEREMONY_OF_CHAOS(4299),
	// Message: You can only fish during the paid period.
	YOU_CAN_ONLY_FUSH_DURING_THE_PAID_PERIOD(4303),

	YOU_CAN_REDEEM_YOUR_REWARD_S1_MINUTES_AFTER_LOGGING_IN_S2_MINUTES_LEFT(4321),

	YOU_CAN_REDEEM_YOUR_REWARD_NOW(4322),

	THE_TRADE_WAS_SUCCESSFUL(4358),

	CANNOT_SHOW_BECAUSE_THE_CONDITIONS_ARE_NOT_MET(4361),

	YOU_CANNOT_JOIN_A_CLAN_WHILE_YOU_ARE_IN_THE_TRAINING_CAMP(4371),

	CANNOT_ENTER_SOME_USERS_MAY_NOT_YET_BE_SEATED(4393),

	ONLY_LEVEL_34_CLANS_CAN_BE_REGISTERED_IN_A_CASTLE_SIEGE(4397),
	// Message: The skill has been canceled because you have insufficient Energy.
	THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY(6042),
	// Message: Your energy cannot be replenished because conditions are not met.
	YOUR_ENERGY_CANNOT_BE_REPLENISHED_BECAUSE_CONDITIONS_ARE_NOT_MET(6043),
	// Message: Energy $s1 replenished.
	ENERGY_S1_REPLENISHED(6044),
	// Message: The premium item for this account was provided. If the premium account is terminated, this item will be deleted.
	THE_PREMIUM_ITEM_FOR_THIS_ACCOUNT_WAS_PROVIDED_IF_THE_PREMIUM_ACCOUNT_IS_TERMINATED_THIS_ITEM_WILL_BE_DELETED(6046),
	// Message: The premium item cannot be received because the inventory weight/quantity limit has been exceeded.
	THE_PREMIUM_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED(6047),
	// Message: The premium account has been terminated. The provided premium item was deleted.
	THE_PREMIUM_ACCOUNT_HAS_BEEN_TERMINATED_THE_PROVIDED_PREMIUM_ITEM_WAS_DELETED(6048),
	// Message: Weapon Appearance Modification or Restoration is in progress. Please try again after completing this task.
	WEAPON_APPEARANCE_MODIFICATION_OR_RESTORATION_IS_IN_PROGRESS_PLEASE_TRY_AGAIN_AFTER_COMPLETING_THIS_TASK(6084),
	// Message: This item cannot be modified or restored.
	THIS_ITEM_CANNOT_BE_MODIFIED_OR_RESTORED(6092),
	// Message: This item does not meet requirements.
	THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS(6094),
	// Message: Please select an item to change.
	PLEASE_SELECT_AN_ITEM_TO_CHANGE(6096),
	// Message: You cannot modify as you do not have enough Adena.
	YOU_CANNOT_MODIFY_AS_YOU_DO_NOT_HAVE_ENOUGH_ADENA(6099),
	// Message: You have spent $s1 on a successful appearance modification.
	YOU_HAVE_SPENT_S1_ON_A_SUCCESSFUL_APPEARANCE_MODIFICATION(6100),
	// Message: Item grades do not match.
	ITEM_GRADES_DO_NOT_MATCH(6101),
	// Message: You cannot extract from items that are higher-grade than items to be modified.
	YOU_CANNOT_EXTRACT_FROM_ITEMS_THAT_ARE_HIGHERGRADE_THAN_ITEMS_TO_BE_MODIFIED(6102),
	// Message: You cannot modify or restore No-grade items.
	YOU_CANNOT_MODIFY_OR_RESTORE_NOGRADE_ITEMS(6103),
	// Message: Weapons only.
	WEAPONS_ONLY(6104),
	// Message: Armor only.
	ARMOR_ONLY(6105),
	// Message: You cannot extract from a modified item.
	YOU_CANNOT_EXTRACT_FROM_A_MODIFIED_ITEM(6106),
	// Message: The number of Vitality effects usable during this period has increased by $s1. You can currently use $s2 Vitality items.
	THE_NUMBER_OF_VITALITY_EFFECTS_USABLE_DURING_THIS_PERIOD_HAS_INCREASED_BY_S1_YOU_CAN_CURRENTLY_USE_S2_VITALITY_ITEMS(6111),
	// Message: You have acquired a clan hall of higher value than the Provisional Clan Hall. #The Provisional Clan Hall ownership will automatically be forfeited.
	YOU_HAVE_ACQUIRED_A_CLAN_HALL_OF_HIGHER_VALUE_THAN_THE_PROVISIONAL_CLAN_HALL_THE_PROVISIONAL_CLAN_HALL_OWNERSHIP_WILL_AUTOMATICALLY_BE_FORFEITED(6136),

	YOU_HAVE_COMPLETED_TRAINING_IN_THE_ROYAL_TRAINING_CAMP_AND_OBTAINED_S1_XP_AND_S2_SP(6138),
	// Message: Not enough tickets.
	NOT_ENOUGH_TICKETS(6139),
	// Message: Your inventory is either full or overweight.
	YOUR_INVENTORY_IS_EITHER_FULL_OR_OVERWEIGHT(6140),
	// Message: Congratulations! $c1 has obtained $s2 of $s3 through Fortune Reading.
	CONGRATULATIONS_C1_HAS_OBTAINED_S2_OF_S3_THROUGH_FORTUNE_READING(6141),
	// Message: Congratulations! $c1 has obtained $s2 of $s3 in the Luxury Fortune Reading.
	CONGRATULATIONS_C1_HAS_OBTAINED_S2_OF_S3_IN_THE_LUXURY_FORTUNE_READING(6142),

	YOU_CANNOT_RECEIVE_REWARDS_FOR_TRAINING_IF_YOU_HAVE_TRAINED_FOR_LESS_THAN_1_MINUTE(6154),

	YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP(6156),

	YOU_CANNOT_REQUEST_TO_A_CHARACTER_WHO_IS_ENTERING_THE_TRAINING_CAMP(6157),
	// Message: Round $s1 of Fortune Reading complete.
	ROUND_S1_OF_FORTUNE_READING_COMPLETE(6158),
	// Message: Round $s1 of Luxury Fortune Reading complete.
	ROUND_S1_OF_LUXURY_FORTUNE_READING_COMPLETE(6159),

	CALCULATING_XP_AND_SP_OBTAINED_FROM_TRAINING(6161),

	C1_IS_CURRENTLY_IN_THE_ROYAL_TRAINING_CAMP_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD(6162),

	YOU_CAN_ONLY_BE_REWARDED_AS_THE_CLASS_IN_WHICH_YOU_ENTERED_THE_TRAINING_CAMP(6163),

	ONLY_ONE_CHARACTER_PER_ACCOUNT_MAY_ENTER_AT_ANY_TIME(6166),

	YOU_CANNOT_ENTER_THE_TRAINING_CAMP_WHILE_IN_A_PARTY_OR_USING_THE_AUTOMATIC_REPLACEMENT_SYSTEM(6167),

	YOU_CANNOT_ENTER_THE_TRAINING_CAMP_WITH_A_MOUNT_OR_IN_A_TRANSFORMED_STATE(6168),

	YOU_HAVE_COMPLETED_THE_DAYS_TRAINING(6169),

	LV_S1_OR_ABOVE(6170),

	LV_S1_OR_BELOW(6171),
	// Message: Your Day $s1 Attendance Reward is ready. Click on the rewards icon.
	YOUR_DAY_S1_ATTENDANCE_REWARD_IS_READY_CLICK_ON_THE_REWARDS_ICON(6174),
	// Message: Your Day $s1 PC Café Attendance Reward is ready. Click on the rewards icon.
	YOUR_DAY_S1_PC_CAF_ATTENDANCE_REWARD_IS_READY_CLICK_ON_THE_REWARDS_ICON(6175),
	// Message: You've received your Attendance Reward for Day $s1.
	YOUVE_RECEIVED_YOUR_ATTENDANCE_REWARD_FOR_DAY_S1_(6176),
	// Message: You've received your PC Café Attendance Reward for Day $s1.
	YOUVE_RECEIVED_YOUR_PC_CAF_ATTENDANCE_REWARD_FOR_DAY_S1_(6177),
	// Message: The Attendance Reward cannot be received because the inventory weight/quantity limit has been exceeded.
	THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED(6178),
	// Message: Due to a system error, the Attendance Reward cannot be received. Please try again later by going to Menu > Attendance Check.
	DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU__ATTENDANCE_CHECK(6179),
	// Message: You can no longer receive Attendance Check rewards.
	YOU_CAN_NO_LONGER_RECEIVE_ATTENDANCE_CHECK_REWARDS_(6182),
	// Message: You cannot bookmark this location because you do not have a My Teleport Flag.
	YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG(6501),

	YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1(6826),

	YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1(6827);

	private final L2GameServerPacket _message;
	private final int _id;
	private final byte _size;

	SystemMsg(int i)
	{
		_id = i;

		if(name().contains("S4") || name().contains("C4"))
		{
			_size = 4;
			_message = null;
		}
		else if(name().contains("S3") || name().contains("C3"))
		{
			_size = 3;
			_message = null;
		}
		else if(name().contains("S2") || name().contains("C2"))
		{
			_size = 2;
			_message = null;
		}
		else if(name().contains("S1") || name().contains("C1"))
		{
			_size = 1;
			_message = null;
		}
		else
		{
			_size = 0;
			_message = new SystemMessagePacket(this);
		}
	}

	public int getId()
	{
		return _id;
	}

	public byte size()
	{
		return _size;
	}

	public static SystemMsg valueOf(int id)
	{
		for(SystemMsg m : values())
			if(m.getId() == id)
				return m;

		throw new NoSuchElementException("Not find SystemMsg by id: " + id);
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		if(_message == null)
			throw new NoSuchElementException("Running SystemMsg.packet(Player), but message require arguments: " + name());

		return _message;
	}
}