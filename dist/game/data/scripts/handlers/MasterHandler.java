/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.handler.ActionHandler;
import com.l2jmobius.gameserver.handler.ActionShiftHandler;
import com.l2jmobius.gameserver.handler.AdminCommandHandler;
import com.l2jmobius.gameserver.handler.AffectObjectHandler;
import com.l2jmobius.gameserver.handler.AffectScopeHandler;
import com.l2jmobius.gameserver.handler.BypassHandler;
import com.l2jmobius.gameserver.handler.ChatHandler;
import com.l2jmobius.gameserver.handler.CommunityBoardHandler;
import com.l2jmobius.gameserver.handler.IHandler;
import com.l2jmobius.gameserver.handler.ItemHandler;
import com.l2jmobius.gameserver.handler.PlayerActionHandler;
import com.l2jmobius.gameserver.handler.PunishmentHandler;
import com.l2jmobius.gameserver.handler.TargetHandler;
import com.l2jmobius.gameserver.handler.UserCommandHandler;
import com.l2jmobius.gameserver.handler.VoicedCommandHandler;
import com.l2jmobius.gameserver.network.telnet.TelnetServer;

import handlers.actionhandlers.L2ArtefactInstanceAction;
import handlers.actionhandlers.L2DecoyAction;
import handlers.actionhandlers.L2DoorInstanceAction;
import handlers.actionhandlers.L2ItemInstanceAction;
import handlers.actionhandlers.L2NpcAction;
import handlers.actionhandlers.L2PcInstanceAction;
import handlers.actionhandlers.L2PetInstanceAction;
import handlers.actionhandlers.L2StaticObjectInstanceAction;
import handlers.actionhandlers.L2SummonAction;
import handlers.actionhandlers.L2TrapAction;
import handlers.actionshifthandlers.L2DoorInstanceActionShift;
import handlers.actionshifthandlers.L2ItemInstanceActionShift;
import handlers.actionshifthandlers.L2NpcActionShift;
import handlers.actionshifthandlers.L2PcInstanceActionShift;
import handlers.actionshifthandlers.L2StaticObjectInstanceActionShift;
import handlers.actionshifthandlers.L2SummonActionShift;
import handlers.admincommandhandlers.AdminAdmin;
import handlers.admincommandhandlers.AdminAnnouncements;
import handlers.admincommandhandlers.AdminBBS;
import handlers.admincommandhandlers.AdminBuffs;
import handlers.admincommandhandlers.AdminCamera;
import handlers.admincommandhandlers.AdminCastle;
import handlers.admincommandhandlers.AdminChangeAccessLevel;
import handlers.admincommandhandlers.AdminClan;
import handlers.admincommandhandlers.AdminClanHall;
import handlers.admincommandhandlers.AdminCreateItem;
import handlers.admincommandhandlers.AdminCursedWeapons;
import handlers.admincommandhandlers.AdminDelete;
import handlers.admincommandhandlers.AdminDisconnect;
import handlers.admincommandhandlers.AdminDoorControl;
import handlers.admincommandhandlers.AdminEditChar;
import handlers.admincommandhandlers.AdminEffects;
import handlers.admincommandhandlers.AdminElement;
import handlers.admincommandhandlers.AdminEnchant;
import handlers.admincommandhandlers.AdminEventEngine;
import handlers.admincommandhandlers.AdminEvents;
import handlers.admincommandhandlers.AdminExpSp;
import handlers.admincommandhandlers.AdminFakePlayers;
import handlers.admincommandhandlers.AdminFence;
import handlers.admincommandhandlers.AdminFightCalculator;
import handlers.admincommandhandlers.AdminFortSiege;
import handlers.admincommandhandlers.AdminGeodata;
import handlers.admincommandhandlers.AdminGm;
import handlers.admincommandhandlers.AdminGmChat;
import handlers.admincommandhandlers.AdminGmSpeed;
import handlers.admincommandhandlers.AdminGraciaSeeds;
import handlers.admincommandhandlers.AdminGrandBoss;
import handlers.admincommandhandlers.AdminHeal;
import handlers.admincommandhandlers.AdminHide;
import handlers.admincommandhandlers.AdminHtml;
import handlers.admincommandhandlers.AdminHwid;
import handlers.admincommandhandlers.AdminInstance;
import handlers.admincommandhandlers.AdminInstanceZone;
import handlers.admincommandhandlers.AdminInvul;
import handlers.admincommandhandlers.AdminKick;
import handlers.admincommandhandlers.AdminKill;
import handlers.admincommandhandlers.AdminLevel;
import handlers.admincommandhandlers.AdminLogin;
import handlers.admincommandhandlers.AdminManor;
import handlers.admincommandhandlers.AdminMenu;
import handlers.admincommandhandlers.AdminMessages;
import handlers.admincommandhandlers.AdminMissingHtmls;
import handlers.admincommandhandlers.AdminMobGroup;
import handlers.admincommandhandlers.AdminOlympiad;
import handlers.admincommandhandlers.AdminPForge;
import handlers.admincommandhandlers.AdminPathNode;
import handlers.admincommandhandlers.AdminPcCafePoints;
import handlers.admincommandhandlers.AdminPcCondOverride;
import handlers.admincommandhandlers.AdminPetition;
import handlers.admincommandhandlers.AdminPledge;
import handlers.admincommandhandlers.AdminPremium;
import handlers.admincommandhandlers.AdminPrimePoints;
import handlers.admincommandhandlers.AdminPunishment;
import handlers.admincommandhandlers.AdminQuest;
import handlers.admincommandhandlers.AdminReload;
import handlers.admincommandhandlers.AdminRepairChar;
import handlers.admincommandhandlers.AdminRes;
import handlers.admincommandhandlers.AdminRide;
import handlers.admincommandhandlers.AdminScan;
import handlers.admincommandhandlers.AdminServerInfo;
import handlers.admincommandhandlers.AdminShop;
import handlers.admincommandhandlers.AdminShowQuests;
import handlers.admincommandhandlers.AdminShutdown;
import handlers.admincommandhandlers.AdminSkill;
import handlers.admincommandhandlers.AdminSpawn;
import handlers.admincommandhandlers.AdminSummon;
import handlers.admincommandhandlers.AdminSuperHaste;
import handlers.admincommandhandlers.AdminTarget;
import handlers.admincommandhandlers.AdminTargetSay;
import handlers.admincommandhandlers.AdminTeleport;
import handlers.admincommandhandlers.AdminTest;
import handlers.admincommandhandlers.AdminTransform;
import handlers.admincommandhandlers.AdminUnblockIp;
import handlers.admincommandhandlers.AdminVitality;
import handlers.admincommandhandlers.AdminZone;
import handlers.admincommandhandlers.AdminZones;
import handlers.bypasshandlers.Augment;
import handlers.bypasshandlers.Buy;
import handlers.bypasshandlers.ChatLink;
import handlers.bypasshandlers.ClanWarehouse;
import handlers.bypasshandlers.EnsoulWindow;
import handlers.bypasshandlers.EventEngine;
import handlers.bypasshandlers.FindPvP;
import handlers.bypasshandlers.Freight;
import handlers.bypasshandlers.ItemAuctionLink;
import handlers.bypasshandlers.Link;
import handlers.bypasshandlers.Multisell;
import handlers.bypasshandlers.NpcViewMod;
import handlers.bypasshandlers.Observation;
import handlers.bypasshandlers.PlayerHelp;
import handlers.bypasshandlers.PrivateWarehouse;
import handlers.bypasshandlers.QuestLink;
import handlers.bypasshandlers.ReleaseAttribute;
import handlers.bypasshandlers.SkillList;
import handlers.bypasshandlers.SupportBlessing;
import handlers.bypasshandlers.SupportMagic;
import handlers.bypasshandlers.TerritoryStatus;
import handlers.bypasshandlers.TutorialClose;
import handlers.bypasshandlers.VoiceCommand;
import handlers.bypasshandlers.Wear;
import handlers.chathandlers.ChatAlliance;
import handlers.chathandlers.ChatClan;
import handlers.chathandlers.ChatGeneral;
import handlers.chathandlers.ChatHeroVoice;
import handlers.chathandlers.ChatParty;
import handlers.chathandlers.ChatPartyMatchRoom;
import handlers.chathandlers.ChatPartyRoomAll;
import handlers.chathandlers.ChatPartyRoomCommander;
import handlers.chathandlers.ChatPetition;
import handlers.chathandlers.ChatShout;
import handlers.chathandlers.ChatTrade;
import handlers.chathandlers.ChatWhisper;
import handlers.chathandlers.ChatWorld;
import handlers.communityboard.ClanBoard;
import handlers.communityboard.DropSearchBoard;
import handlers.communityboard.FavoriteBoard;
import handlers.communityboard.FriendsBoard;
import handlers.communityboard.HomeBoard;
import handlers.communityboard.HomepageBoard;
import handlers.communityboard.MailBoard;
import handlers.communityboard.MemoBoard;
import handlers.communityboard.RegionBoard;
import handlers.itemhandlers.Appearance;
import handlers.itemhandlers.BeastSoulShot;
import handlers.itemhandlers.BeastSpiritShot;
import handlers.itemhandlers.BlessedSoulShots;
import handlers.itemhandlers.BlessedSpiritShot;
import handlers.itemhandlers.Book;
import handlers.itemhandlers.Bypass;
import handlers.itemhandlers.Calculator;
import handlers.itemhandlers.ChangeAttributeCrystal;
import handlers.itemhandlers.CharmOfCourage;
import handlers.itemhandlers.Elixir;
import handlers.itemhandlers.EnchantAttribute;
import handlers.itemhandlers.EnchantScrolls;
import handlers.itemhandlers.EventItem;
import handlers.itemhandlers.ExtractableItems;
import handlers.itemhandlers.FatedSupportBox;
import handlers.itemhandlers.FishShots;
import handlers.itemhandlers.Harvester;
import handlers.itemhandlers.ItemSkills;
import handlers.itemhandlers.ItemSkillsTemplate;
import handlers.itemhandlers.Maps;
import handlers.itemhandlers.MercTicket;
import handlers.itemhandlers.NicknameColor;
import handlers.itemhandlers.PetFood;
import handlers.itemhandlers.Recipes;
import handlers.itemhandlers.RollingDice;
import handlers.itemhandlers.Seed;
import handlers.itemhandlers.SoulShots;
import handlers.itemhandlers.SpecialXMas;
import handlers.itemhandlers.SpiritShot;
import handlers.itemhandlers.SummonItems;
import handlers.playeractions.AirshipAction;
import handlers.playeractions.BotReport;
import handlers.playeractions.InstanceZoneInfo;
import handlers.playeractions.PetAttack;
import handlers.playeractions.PetHold;
import handlers.playeractions.PetMove;
import handlers.playeractions.PetSkillUse;
import handlers.playeractions.PetStop;
import handlers.playeractions.PrivateStore;
import handlers.playeractions.Ride;
import handlers.playeractions.RunWalk;
import handlers.playeractions.ServitorAttack;
import handlers.playeractions.ServitorHold;
import handlers.playeractions.ServitorMode;
import handlers.playeractions.ServitorMove;
import handlers.playeractions.ServitorSkillUse;
import handlers.playeractions.ServitorStop;
import handlers.playeractions.SitStand;
import handlers.playeractions.SocialAction;
import handlers.playeractions.TacticalSignTarget;
import handlers.playeractions.TacticalSignUse;
import handlers.playeractions.TeleportBookmark;
import handlers.playeractions.UnsummonPet;
import handlers.playeractions.UnsummonServitor;
import handlers.punishmenthandlers.BanHandler;
import handlers.punishmenthandlers.ChatBanHandler;
import handlers.punishmenthandlers.JailHandler;
import handlers.targethandlers.AdvanceBase;
import handlers.targethandlers.Artillery;
import handlers.targethandlers.DoorTreasure;
import handlers.targethandlers.Enemy;
import handlers.targethandlers.EnemyNot;
import handlers.targethandlers.EnemyOnly;
import handlers.targethandlers.FortressFlagpole;
import handlers.targethandlers.Ground;
import handlers.targethandlers.HolyThing;
import handlers.targethandlers.Item;
import handlers.targethandlers.MyMentor;
import handlers.targethandlers.MyParty;
import handlers.targethandlers.None;
import handlers.targethandlers.NpcBody;
import handlers.targethandlers.Others;
import handlers.targethandlers.OwnerPet;
import handlers.targethandlers.PcBody;
import handlers.targethandlers.Self;
import handlers.targethandlers.Summon;
import handlers.targethandlers.Target;
import handlers.targethandlers.WyvernTarget;
import handlers.targethandlers.affectobject.All;
import handlers.targethandlers.affectobject.Clan;
import handlers.targethandlers.affectobject.Friend;
import handlers.targethandlers.affectobject.FriendPc;
import handlers.targethandlers.affectobject.HiddenPlace;
import handlers.targethandlers.affectobject.Invisible;
import handlers.targethandlers.affectobject.NotFriend;
import handlers.targethandlers.affectobject.NotFriendPc;
import handlers.targethandlers.affectobject.ObjectDeadNpcBody;
import handlers.targethandlers.affectobject.UndeadRealEnemy;
import handlers.targethandlers.affectobject.WyvernObject;
import handlers.targethandlers.affectscope.BalakasScope;
import handlers.targethandlers.affectscope.DeadParty;
import handlers.targethandlers.affectscope.DeadPartyPledge;
import handlers.targethandlers.affectscope.DeadPledge;
import handlers.targethandlers.affectscope.DeadUnion;
import handlers.targethandlers.affectscope.Fan;
import handlers.targethandlers.affectscope.FanPB;
import handlers.targethandlers.affectscope.Party;
import handlers.targethandlers.affectscope.PartyPledge;
import handlers.targethandlers.affectscope.Pledge;
import handlers.targethandlers.affectscope.PointBlank;
import handlers.targethandlers.affectscope.Range;
import handlers.targethandlers.affectscope.RangeSortByHp;
import handlers.targethandlers.affectscope.RingRange;
import handlers.targethandlers.affectscope.Single;
import handlers.targethandlers.affectscope.Square;
import handlers.targethandlers.affectscope.SquarePB;
import handlers.targethandlers.affectscope.StaticObjectScope;
import handlers.targethandlers.affectscope.SummonExceptMaster;
import handlers.telnethandlers.chat.Announce;
import handlers.telnethandlers.chat.GMChat;
import handlers.telnethandlers.chat.Msg;
import handlers.telnethandlers.player.AccessLevel;
import handlers.telnethandlers.player.Ban;
import handlers.telnethandlers.player.BanChat;
import handlers.telnethandlers.player.Enchant;
import handlers.telnethandlers.player.GMList;
import handlers.telnethandlers.player.Give;
import handlers.telnethandlers.player.Jail;
import handlers.telnethandlers.player.Kick;
import handlers.telnethandlers.player.SendMail;
import handlers.telnethandlers.player.Unban;
import handlers.telnethandlers.player.UnbanChat;
import handlers.telnethandlers.player.Unjail;
import handlers.telnethandlers.server.ForceGC;
import handlers.telnethandlers.server.Memusage;
import handlers.telnethandlers.server.Performance;
import handlers.telnethandlers.server.Purge;
import handlers.telnethandlers.server.Reload;
import handlers.telnethandlers.server.ServerAbort;
import handlers.telnethandlers.server.ServerRestart;
import handlers.telnethandlers.server.ServerShutdown;
import handlers.telnethandlers.server.Status;
import handlers.usercommandhandlers.ChannelDelete;
import handlers.usercommandhandlers.ChannelInfo;
import handlers.usercommandhandlers.ChannelLeave;
import handlers.usercommandhandlers.ClanPenalty;
import handlers.usercommandhandlers.ClanWarsList;
import handlers.usercommandhandlers.Dismount;
import handlers.usercommandhandlers.ExperienceGain;
import handlers.usercommandhandlers.InstanceZone;
import handlers.usercommandhandlers.Loc;
import handlers.usercommandhandlers.Mount;
import handlers.usercommandhandlers.MyBirthday;
import handlers.usercommandhandlers.OlympiadStat;
import handlers.usercommandhandlers.PartyInfo;
import handlers.usercommandhandlers.SiegeStatus;
import handlers.usercommandhandlers.Time;
import handlers.usercommandhandlers.Unstuck;
import handlers.voicedcommandhandlers.AutoPotion;
import handlers.voicedcommandhandlers.Banking;
import handlers.voicedcommandhandlers.ChangePassword;
import handlers.voicedcommandhandlers.ChatAdmin;
import handlers.voicedcommandhandlers.Lang;
import handlers.voicedcommandhandlers.Premium;
import handlers.voicedcommandhandlers.StatsVCmd;

/**
 * Master handler.
 * @author UnAfraid
 */
public class MasterHandler
{
	private static final Logger LOGGER = Logger.getLogger(MasterHandler.class.getName());
	
	private static final IHandler<?, ?>[] LOAD_INSTANCES =
	{
		ActionHandler.getInstance(),
		ActionShiftHandler.getInstance(),
		AdminCommandHandler.getInstance(),
		BypassHandler.getInstance(),
		ChatHandler.getInstance(),
		CommunityBoardHandler.getInstance(),
		ItemHandler.getInstance(),
		PunishmentHandler.getInstance(),
		UserCommandHandler.getInstance(),
		VoicedCommandHandler.getInstance(),
		TargetHandler.getInstance(),
		AffectObjectHandler.getInstance(),
		AffectScopeHandler.getInstance(),
		PlayerActionHandler.getInstance()
	};
	
	private static final Class<?>[][] HANDLERS =
	{
		{
			// Action Handlers
			L2ArtefactInstanceAction.class,
			L2DecoyAction.class,
			L2DoorInstanceAction.class,
			L2ItemInstanceAction.class,
			L2NpcAction.class,
			L2PcInstanceAction.class,
			L2PetInstanceAction.class,
			L2StaticObjectInstanceAction.class,
			L2SummonAction.class,
			L2TrapAction.class,
		},
		{
			// Action Shift Handlers
			L2DoorInstanceActionShift.class,
			L2ItemInstanceActionShift.class,
			L2NpcActionShift.class,
			L2PcInstanceActionShift.class,
			L2StaticObjectInstanceActionShift.class,
			L2SummonActionShift.class,
		},
		{
			// Admin Command Handlers
			AdminAdmin.class,
			AdminAnnouncements.class,
			AdminBBS.class,
			AdminBuffs.class,
			AdminCamera.class,
			AdminChangeAccessLevel.class,
			AdminClan.class,
			AdminClanHall.class,
			AdminCastle.class,
			AdminPcCondOverride.class,
			AdminCreateItem.class,
			AdminCursedWeapons.class,
			AdminDelete.class,
			AdminDisconnect.class,
			AdminDoorControl.class,
			AdminEditChar.class,
			AdminEffects.class,
			AdminElement.class,
			AdminEnchant.class,
			AdminEventEngine.class,
			AdminEvents.class,
			AdminExpSp.class,
			AdminFakePlayers.class,
			AdminFence.class,
			AdminFightCalculator.class,
			AdminFortSiege.class,
			AdminGeodata.class,
			AdminGm.class,
			AdminGmChat.class,
			AdminGmSpeed.class,
			AdminGraciaSeeds.class,
			AdminGrandBoss.class,
			AdminHeal.class,
			AdminHide.class,
			AdminHtml.class,
			AdminHwid.class,
			AdminInstance.class,
			AdminInstanceZone.class,
			AdminInvul.class,
			AdminKick.class,
			AdminKill.class,
			AdminLevel.class,
			AdminLogin.class,
			AdminManor.class,
			AdminMenu.class,
			AdminMessages.class,
			AdminMissingHtmls.class,
			AdminMobGroup.class,
			AdminOlympiad.class,
			AdminPathNode.class,
			AdminPcCafePoints.class,
			AdminPetition.class,
			AdminPForge.class,
			AdminPledge.class,
			AdminZones.class,
			AdminPremium.class,
			AdminPrimePoints.class,
			AdminPunishment.class,
			AdminQuest.class,
			AdminReload.class,
			AdminRepairChar.class,
			AdminRes.class,
			AdminRide.class,
			AdminScan.class,
			AdminServerInfo.class,
			AdminShop.class,
			AdminShowQuests.class,
			AdminShutdown.class,
			AdminSkill.class,
			AdminSpawn.class,
			AdminSummon.class,
			AdminSuperHaste.class,
			AdminTarget.class,
			AdminTargetSay.class,
			AdminTeleport.class,
			AdminTest.class,
			AdminTransform.class,
			AdminUnblockIp.class,
			AdminVitality.class,
			AdminZone.class,
		},
		{
			// Bypass Handlers
			Augment.class,
			Buy.class,
			ChatLink.class,
			ClanWarehouse.class,
			EnsoulWindow.class,
			EventEngine.class,
			FindPvP.class,
			Freight.class,
			ItemAuctionLink.class,
			Link.class,
			Multisell.class,
			NpcViewMod.class,
			Observation.class,
			QuestLink.class,
			PlayerHelp.class,
			PrivateWarehouse.class,
			ReleaseAttribute.class,
			SkillList.class,
			SupportBlessing.class,
			SupportMagic.class,
			TerritoryStatus.class,
			TutorialClose.class,
			VoiceCommand.class,
			Wear.class,
		},
		{
			// Chat Handlers
			ChatGeneral.class,
			ChatAlliance.class,
			ChatClan.class,
			ChatHeroVoice.class,
			ChatParty.class,
			ChatPartyMatchRoom.class,
			ChatPartyRoomAll.class,
			ChatPartyRoomCommander.class,
			ChatPetition.class,
			ChatShout.class,
			ChatWhisper.class,
			ChatTrade.class,
			ChatWorld.class,
		},
		{
			// Community Board
			ClanBoard.class,
			FavoriteBoard.class,
			FriendsBoard.class,
			HomeBoard.class,
			HomepageBoard.class,
			MailBoard.class,
			MemoBoard.class,
			RegionBoard.class,
			DropSearchBoard.class,
		},
		{
			// Item Handlers
			Appearance.class,
			BeastSoulShot.class,
			BeastSpiritShot.class,
			BlessedSoulShots.class,
			BlessedSpiritShot.class,
			Book.class,
			Bypass.class,
			Calculator.class,
			ChangeAttributeCrystal.class,
			CharmOfCourage.class,
			Elixir.class,
			EnchantAttribute.class,
			EnchantScrolls.class,
			EventItem.class,
			ExtractableItems.class,
			FatedSupportBox.class,
			FishShots.class,
			Harvester.class,
			ItemSkills.class,
			ItemSkillsTemplate.class,
			Maps.class,
			MercTicket.class,
			NicknameColor.class,
			PetFood.class,
			Recipes.class,
			RollingDice.class,
			Seed.class,
			SoulShots.class,
			SpecialXMas.class,
			SpiritShot.class,
			SummonItems.class,
		},
		{
			// Punishment Handlers
			BanHandler.class,
			ChatBanHandler.class,
			JailHandler.class,
		},
		{
			// User Command Handlers
			ClanPenalty.class,
			ClanWarsList.class,
			Dismount.class,
			ExperienceGain.class,
			Unstuck.class,
			InstanceZone.class,
			Loc.class,
			Mount.class,
			PartyInfo.class,
			Time.class,
			OlympiadStat.class,
			ChannelLeave.class,
			ChannelDelete.class,
			ChannelInfo.class,
			MyBirthday.class,
			SiegeStatus.class,
		},
		{
			// Voiced Command Handlers
			StatsVCmd.class,
			// TODO: Add configuration options for this voiced commands:
			// CastleVCmd.class,
			// SetVCmd.class,
			Config.BANKING_SYSTEM_ENABLED ? Banking.class : null,
			Config.CHAT_ADMIN ? ChatAdmin.class : null,
			Config.MULTILANG_ENABLE && Config.MULTILANG_VOICED_ALLOW ? Lang.class : null,
			Config.ALLOW_CHANGE_PASSWORD ? ChangePassword.class : null,
			Config.PREMIUM_SYSTEM_ENABLED ? Premium.class : null,
			Config.AUTO_POTIONS_ENABLED ? AutoPotion.class : null,
		},
		{
			// Target Handlers
			AdvanceBase.class,
			Artillery.class,
			DoorTreasure.class,
			Enemy.class,
			EnemyNot.class,
			EnemyOnly.class,
			FortressFlagpole.class,
			Ground.class,
			HolyThing.class,
			Item.class,
			MyMentor.class,
			MyParty.class,
			None.class,
			NpcBody.class,
			Others.class,
			OwnerPet.class,
			PcBody.class,
			Self.class,
			Summon.class,
			Target.class,
			WyvernTarget.class,
		},
		{
			// Affect Objects
			All.class,
			Clan.class,
			Friend.class,
			FriendPc.class,
			HiddenPlace.class,
			Invisible.class,
			NotFriend.class,
			NotFriendPc.class,
			ObjectDeadNpcBody.class,
			UndeadRealEnemy.class,
			WyvernObject.class,
		},
		{
			// Affect Scopes
			BalakasScope.class,
			DeadParty.class,
			DeadPartyPledge.class,
			DeadPledge.class,
			DeadUnion.class,
			Fan.class,
			FanPB.class,
			Party.class,
			PartyPledge.class,
			Pledge.class,
			PointBlank.class,
			Range.class,
			RangeSortByHp.class,
			RingRange.class,
			Single.class,
			Square.class,
			SquarePB.class,
			StaticObjectScope.class,
			SummonExceptMaster.class,
		},
		{
			AirshipAction.class,
			BotReport.class,
			InstanceZoneInfo.class,
			PetAttack.class,
			PetHold.class,
			PetMove.class,
			PetSkillUse.class,
			PetStop.class,
			PrivateStore.class,
			Ride.class,
			RunWalk.class,
			ServitorAttack.class,
			ServitorHold.class,
			ServitorMode.class,
			ServitorMove.class,
			ServitorSkillUse.class,
			ServitorStop.class,
			SitStand.class,
			SocialAction.class,
			TacticalSignTarget.class,
			TacticalSignUse.class,
			TeleportBookmark.class,
			UnsummonPet.class,
			UnsummonServitor.class
		}
	};
	
	public static void main(String[] args)
	{
		LOGGER.info("Loading Handlers...");
		
		final Map<IHandler<?, ?>, Method> registerHandlerMethods = new HashMap<>();
		for (IHandler<?, ?> loadInstance : LOAD_INSTANCES)
		{
			registerHandlerMethods.put(loadInstance, null);
			for (Method method : loadInstance.getClass().getMethods())
			{
				if (method.getName().equals("registerHandler") && !method.isBridge())
				{
					registerHandlerMethods.put(loadInstance, method);
				}
			}
		}
		
		registerHandlerMethods.entrySet().stream().filter(e -> e.getValue() == null).forEach(e ->
		{
			LOGGER.warning("Failed loading handlers of: " + e.getKey().getClass().getSimpleName() + " seems registerHandler function does not exist.");
		});
		
		for (Class<?> classes[] : HANDLERS)
		{
			for (Class<?> c : classes)
			{
				if (c == null)
				{
					continue; // Disabled handler
				}
				
				try
				{
					final Object handler = c.getDeclaredConstructor().newInstance();
					for (Entry<IHandler<?, ?>, Method> entry : registerHandlerMethods.entrySet())
					{
						if ((entry.getValue() != null) && entry.getValue().getParameterTypes()[0].isInstance(handler))
						{
							entry.getValue().invoke(entry.getKey(), handler);
						}
					}
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed loading handler: " + c.getSimpleName(), e);
					continue;
				}
			}
		}
		
		if (Config.TELNET_ENABLED)
		{
			TelnetServer.getInstance().addHandler(new Announce());
			TelnetServer.getInstance().addHandler(new GMChat());
			TelnetServer.getInstance().addHandler(new Msg());
			TelnetServer.getInstance().addHandler(new AccessLevel());
			TelnetServer.getInstance().addHandler(new Ban());
			TelnetServer.getInstance().addHandler(new BanChat());
			TelnetServer.getInstance().addHandler(new Enchant());
			TelnetServer.getInstance().addHandler(new Give());
			TelnetServer.getInstance().addHandler(new GMList());
			TelnetServer.getInstance().addHandler(new Jail());
			TelnetServer.getInstance().addHandler(new Kick());
			TelnetServer.getInstance().addHandler(new Unban());
			TelnetServer.getInstance().addHandler(new UnbanChat());
			TelnetServer.getInstance().addHandler(new Unjail());
			TelnetServer.getInstance().addHandler(new ForceGC());
			TelnetServer.getInstance().addHandler(new Memusage());
			TelnetServer.getInstance().addHandler(new Performance());
			TelnetServer.getInstance().addHandler(new Purge());
			TelnetServer.getInstance().addHandler(new Reload());
			TelnetServer.getInstance().addHandler(new SendMail());
			TelnetServer.getInstance().addHandler(new ServerAbort());
			TelnetServer.getInstance().addHandler(new ServerRestart());
			TelnetServer.getInstance().addHandler(new ServerShutdown());
			TelnetServer.getInstance().addHandler(new Status());
			TelnetServer.getInstance().addHandler(new handlers.telnethandlers.server.Debug());
		}
		
		for (IHandler<?, ?> loadInstance : LOAD_INSTANCES)
		{
			LOGGER.info(loadInstance.getClass().getSimpleName() + ": Loaded " + loadInstance.size() + " Handlers");
		}
		
		LOGGER.info("Handlers Loaded...");
	}
}
