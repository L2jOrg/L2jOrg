import handlers.actionhandlers.*;
import handlers.actionshifthandlers.*;
import handlers.admincommandhandlers.AdminCoins;
import handlers.bypasshandlers.EquipmentUpgrade;

open module org.l2j.scripts {

    requires java.logging;
    requires org.l2j.gameserver;
    requires org.l2j.commons;
    requires java.sql;
    requires java.desktop;
    requires org.slf4j;
    requires io.github.joealisson.primitive;

    uses instances.AbstractInstance;
    uses events.ScriptEvent;
    uses ai.AbstractNpcAI;
    uses org.l2j.gameserver.handler.IActionHandler;
    uses org.l2j.gameserver.handler.IActionShiftHandler;
    uses org.l2j.gameserver.handler.IAdminCommandHandler;
    uses org.l2j.gameserver.handler.IBypassHandler;
    uses org.l2j.gameserver.handler.IChatHandler;
    uses org.l2j.gameserver.handler.IParseBoardHandler;
    uses org.l2j.gameserver.handler.IItemHandler;
    uses org.l2j.gameserver.handler.IPunishmentHandler;
    uses org.l2j.gameserver.handler.IUserCommandHandler;
    uses org.l2j.gameserver.handler.IVoicedCommandHandler;
    uses org.l2j.gameserver.handler.ITargetTypeHandler;
    uses org.l2j.gameserver.handler.IAffectObjectHandler;
    uses org.l2j.gameserver.handler.IAffectScopeHandler;
    uses org.l2j.gameserver.handler.IPlayerActionHandler;
    uses org.l2j.gameserver.model.quest.Quest;

    provides instances.AbstractInstance
        with  instances.MonsterArena.MonsterArena;

    provides events.ScriptEvent
        with events.ChefMonkeyEvent.ChefMonkeyEvent,
            events.EveTheFortuneTeller.EveTheFortuneTeller,
            events.HappyHours.HappyHours,
            events.LetterCollector.LetterCollector,
            events.MerrySquashmas.MerrySquashmas,
            events.SquashEvent.SquashEvent,
            events.ThePowerOfLove.ThePowerOfLove,
            events.TotalRecall.TotalRecall,
            events.WatermelonNinja.WatermelonNinja,

            // custom
            custom.events.Elpies.Elpies,
            custom.events.Rabbits.Rabbits,
            custom.events.Race.Race,
            custom.events.TeamVsTeam.TvT;

    provides ai.AbstractNpcAI
        with  ai.areas.CrumaTower.SummonPc,
            ai.areas.DragonValley.CaveMaiden,
            ai.areas.DungeonOfAbyss.SoulTracker.Ingrit,
            ai.areas.DungeonOfAbyss.SoulTracker.Iris,
            ai.areas.DungeonOfAbyss.SoulTracker.Magrit,
            ai.areas.DungeonOfAbyss.SoulTracker.Rosammy,
            ai.areas.DwarvenVillage.Toma.Toma,
            ai.areas.LairOfAntharas.Pytan,
            ai.areas.TalkingIsland.Roxxy,
            ai.areas.TowerOfInsolence.Ateld.Ateld,
            ai.areas.PlainsOfDion,

            ai.bosses.Antharas.Antharas,
            ai.bosses.Baium.Baium,
            ai.bosses.Core.Core,
            ai.bosses.Orfen.Orfen,
            ai.bosses.QueenAnt.QueenAnt,
            ai.bosses.Zaken.Zaken,

            ai.others.ArenaManager.ArenaManager,
            ai.others.CastleBlacksmith.CastleBlacksmith,
            ai.others.CastleChamberlain.CastleChamberlain,
            ai.others.CastleDoorManager.CastleDoorManager,
            ai.others.CastleMercenaryManager.CastleMercenaryManager,
            ai.others.CastleSiegeManager.CastleSiegeManager,
            ai.others.CastleTeleporter.CastleTeleporter,
            ai.others.CastleWarehouse.CastleWarehouse,
            ai.others.ClanHallAuctioneer.ClanHallAuctioneer,
            ai.others.ClanHallDoorManager.ClanHallDoorManager,
            ai.others.ClanHallManager.ClanHallManager,
            ai.others.ClassMaster.ClassMaster,
            ai.others.MonumentOfHeroes.MonumentOfHeroes,
            ai.others.NewbieGuide.NewbieGuide,
            ai.others.OlyBuffer.OlyBuffer,
            ai.others.Proclaimer.Proclaimer,
            ai.others.Servitors.SinEater,
            ai.others.Servitors.TreeOfLife,
            ai.others.Spawns.EilhalderVonHellmann,
            ai.others.Spawns.DayNightSpawns,
            ai.others.Spawns.NoRandomActivity,
            ai.others.SymbolMaker.SymbolMaker,
            ai.others.TeleportToRaceTrack.TeleportToRaceTrack,
            ai.others.TeleportWithCharm.TeleportWithCharm,
            ai.others.ToIVortex.ToIVortex,
            ai.others.WyvernManager.WyvernManager,
            ai.others.DivineBeast,
            ai.others.FairyTrees,
            ai.others.FleeMonsters,
            ai.others.Incarnation,
            ai.others.NonLethalableNpcs,
            ai.others.PolymorphingAngel,
            ai.others.PolymorphingOnAttack,
            ai.others.RandomWalkingGuards,
            ai.others.SeeThroughSilentMove,
            ai.others.SiegeGuards,
            ai.others.TimakOrcTroopLeader,

            village.master.Alliance.Alliance,
            village.master.Clan.Clan,
            village.master.DarkElfChange1.DarkElfChange1,
            village.master.OrcChange2.OrcChange2,
            village.master.OrcChange1.OrcChange1,
            village.master.FirstClassTransferTalk.FirstClassTransferTalk,
            village.master.ElfHumanWizardChange2.ElfHumanWizardChange2,
            village.master.ElfHumanWizardChange1.ElfHumanWizardChange1,
            village.master.ElfHumanFighterChange2.ElfHumanFighterChange2,
            village.master.ElfHumanFighterChange1.ElfHumanFighterChange1,
            village.master.ElfHumanClericChange2.ElfHumanClericChange2,
            village.master.DwarfWarehouseChange2.DwarfWarehouseChange2,
            village.master.DwarfWarehouseChange1.DwarfWarehouseChange1,
            village.master.DwarfBlacksmithChange2.DwarfBlacksmithChange2,
            village.master.DwarfBlacksmithChange1.DwarfBlacksmithChange1,
            village.master.DarkElfChange2.DarkElfChange2,

            //  custom
            custom.NpcLocationInfo.NpcLocationInfo;

    provides org.l2j.gameserver.handler.IActionHandler
        with ArtefactAction,
                DecoyAction,
                DoorAction,
                ItemAction,
                NpcAction,
                PlayerAction,
                PetAction,
                StaticWorldObjectAction,
                SummonAction,
                TrapAction;

    provides org.l2j.gameserver.handler.IActionShiftHandler
        with DoorActionShift,
                ItemActionShift,
                NpcActionShift,
                PlayerActionShift,
                StaticWorldObjectActionShift,
                SummonActionShift;

    provides org.l2j.gameserver.handler.IAdminCommandHandler
        with handlers.admincommandhandlers.AdminHtml,
            handlers.admincommandhandlers.AdminEditChar,
            handlers.admincommandhandlers.AdminShowQuests,
            handlers.admincommandhandlers.AdminAdmin,
            handlers.admincommandhandlers.AdminEnchant,
            handlers.admincommandhandlers.AdminMenu,
            handlers.admincommandhandlers.AdminPathNode,
            handlers.admincommandhandlers.AdminPunishment,
            handlers.admincommandhandlers.AdminQuest,
            handlers.admincommandhandlers.AdminReload,
            handlers.admincommandhandlers.AdminRepairChar,
            handlers.admincommandhandlers.AdminRes,
            handlers.admincommandhandlers.AdminShop,
            handlers.admincommandhandlers.AdminSkill,
            handlers.admincommandhandlers.AdminSpawn,
            handlers.admincommandhandlers.AdminSuperHaste,
            handlers.admincommandhandlers.AdminTeleport,
            handlers.admincommandhandlers.AdminUnblockIp,
            handlers.admincommandhandlers.AdminAnnouncements,
            handlers.admincommandhandlers.AdminBBS,
            handlers.admincommandhandlers.AdminBuffs,
            handlers.admincommandhandlers.AdminCamera,
            handlers.admincommandhandlers.AdminCastle,
            handlers.admincommandhandlers.AdminChangeAccessLevel,
            handlers.admincommandhandlers.AdminClan,
            handlers.admincommandhandlers.AdminClanHall,
            handlers.admincommandhandlers.AdminCreateItem,
            handlers.admincommandhandlers.AdminCursedWeapons,
            handlers.admincommandhandlers.AdminDelete,
            handlers.admincommandhandlers.AdminDestroyItems,
            handlers.admincommandhandlers.AdminDisconnect,
            handlers.admincommandhandlers.AdminDoorControl,
            handlers.admincommandhandlers.AdminEffects,
            handlers.admincommandhandlers.AdminElement,
            handlers.admincommandhandlers.AdminEventEngine,
            handlers.admincommandhandlers.AdminEvents,
            handlers.admincommandhandlers.AdminExpSp,
            handlers.admincommandhandlers.AdminFence,
            handlers.admincommandhandlers.AdminFightCalculator,
            handlers.admincommandhandlers.AdminFortSiege,
            handlers.admincommandhandlers.AdminGeodata,
            handlers.admincommandhandlers.AdminGm,
            handlers.admincommandhandlers.AdminGmChat,
            handlers.admincommandhandlers.AdminGmSpeed,
            handlers.admincommandhandlers.AdminGraciaSeeds,
            handlers.admincommandhandlers.AdminGrandBoss,
            handlers.admincommandhandlers.AdminHeal,
            handlers.admincommandhandlers.AdminHide,
            handlers.admincommandhandlers.AdminHwid,
            handlers.admincommandhandlers.AdminInstance,
            handlers.admincommandhandlers.AdminInstanceZone,
            handlers.admincommandhandlers.AdminInvul,
            handlers.admincommandhandlers.AdminKick,
            handlers.admincommandhandlers.AdminKill,
            handlers.admincommandhandlers.AdminLevel,
            handlers.admincommandhandlers.AdminLogin,
            handlers.admincommandhandlers.AdminManor,
            handlers.admincommandhandlers.AdminMessages,
            handlers.admincommandhandlers.AdminMissingHtmls,
            handlers.admincommandhandlers.AdminMobGroup,
            handlers.admincommandhandlers.AdminOlympiad,
            handlers.admincommandhandlers.AdminPcCafePoints,
            handlers.admincommandhandlers.AdminPcCondOverride,
            handlers.admincommandhandlers.AdminPetition,
            handlers.admincommandhandlers.AdminPForge,
            handlers.admincommandhandlers.AdminPledge,
                AdminCoins,
            handlers.admincommandhandlers.AdminRide,
            handlers.admincommandhandlers.AdminScan,
            handlers.admincommandhandlers.AdminServerInfo,
            handlers.admincommandhandlers.AdminShutdown,
            handlers.admincommandhandlers.AdminSummon,
            handlers.admincommandhandlers.AdminTarget,
            handlers.admincommandhandlers.AdminTargetSay,
            handlers.admincommandhandlers.AdminTest,
            handlers.admincommandhandlers.AdminTransform,
            handlers.admincommandhandlers.AdminVitality,
            handlers.admincommandhandlers.AdminZone,
            handlers.admincommandhandlers.AdminZones;

    provides org.l2j.gameserver.handler.IBypassHandler
        with  handlers.bypasshandlers.NpcViewMod,
            handlers.bypasshandlers.Augment,
            handlers.bypasshandlers.Buy,
            handlers.bypasshandlers.ChatLink,
            handlers.bypasshandlers.ClanWarehouse,
            handlers.bypasshandlers.EnsoulWindow,
            handlers.bypasshandlers.EventEngine,
            handlers.bypasshandlers.Freight,
            handlers.bypasshandlers.ItemAuctionLink,
            handlers.bypasshandlers.Link,
            handlers.bypasshandlers.Multisell,
            handlers.bypasshandlers.Observation,
            handlers.bypasshandlers.PlayerHelp,
            handlers.bypasshandlers.PrivateWarehouse,
            handlers.bypasshandlers.QuestLink,
            handlers.bypasshandlers.ReleaseAttribute,
            handlers.bypasshandlers.SkillList,
            handlers.bypasshandlers.SupportBlessing,
            handlers.bypasshandlers.TerritoryStatus,
            handlers.bypasshandlers.SupportMagic,
            handlers.bypasshandlers.TutorialClose,
            handlers.bypasshandlers.VoiceCommand,
            handlers.bypasshandlers.Wear,
                EquipmentUpgrade,

            //custom
            custom.SellBuff.SellBuff;

    provides org.l2j.gameserver.handler.IChatHandler
        with handlers.chathandlers.ChatAlliance,
            handlers.chathandlers.ChatClan,
            handlers.chathandlers.ChatGeneral,
            handlers.chathandlers.ChatHeroVoice,
            handlers.chathandlers.ChatParty,
            handlers.chathandlers.ChatPartyMatchRoom,
            handlers.chathandlers.ChatPartyRoomAll,
            handlers.chathandlers.ChatPartyRoomCommander,
            handlers.chathandlers.ChatPetition,
            handlers.chathandlers.ChatShout,
            handlers.chathandlers.ChatTrade,
            handlers.chathandlers.ChatWhisper,
            handlers.chathandlers.ChatWorld;

    provides org.l2j.gameserver.handler.IParseBoardHandler
        with handlers.communityboard.FavoriteBoard,
            handlers.communityboard.DropSearchBoard,
            handlers.communityboard.ClanBoard,
            handlers.communityboard.RegionBoard,
            handlers.communityboard.FriendsBoard,
            handlers.communityboard.HomeBoard,
            handlers.communityboard.HomepageBoard,
            handlers.communityboard.MailBoard,
            handlers.communityboard.MemoBoard;

    provides org.l2j.gameserver.handler.IItemHandler
        with handlers.itemhandlers.ItemSkillsTemplate,
            handlers.itemhandlers.ItemSkills,
            handlers.itemhandlers.BeastSoulShot,
            handlers.itemhandlers.BeastSpiritShot,
            handlers.itemhandlers.BlessedSoulShots,
            handlers.itemhandlers.BlessedSpiritShot,
            handlers.itemhandlers.Book,
            handlers.itemhandlers.Bypass,
            handlers.itemhandlers.Calculator,
            handlers.itemhandlers.ChangeAttributeCrystal,
            handlers.itemhandlers.CharmOfCourage,
            handlers.itemhandlers.Elixir,
            handlers.itemhandlers.EnchantAttribute,
            handlers.itemhandlers.EnchantScrolls,
            handlers.itemhandlers.EventItem,
            handlers.itemhandlers.ExtractableItems,
            handlers.itemhandlers.FatedSupportBox,
            handlers.itemhandlers.FishShots,
            handlers.itemhandlers.Harvester,
            handlers.itemhandlers.Maps,
            handlers.itemhandlers.MercTicket,
            handlers.itemhandlers.NicknameColor,
            handlers.itemhandlers.PetFood,
            handlers.itemhandlers.Recipes,
            handlers.itemhandlers.RollingDice,
            handlers.itemhandlers.Seed,
            handlers.itemhandlers.SoulShots,
            handlers.itemhandlers.SpecialXMas,
            handlers.itemhandlers.SpiritShot,
            handlers.itemhandlers.SummonItems;

    provides org.l2j.gameserver.handler.IPunishmentHandler
        with handlers.punishmenthandlers.BanHandler,
            handlers.punishmenthandlers.ChatBanHandler,
            handlers.punishmenthandlers.JailHandler;

    provides org.l2j.gameserver.handler.IUserCommandHandler
        with handlers.usercommandhandlers.ClanWarsList,
            handlers.usercommandhandlers.ChannelDelete,
            handlers.usercommandhandlers.ChannelInfo,
            handlers.usercommandhandlers.ChannelLeave,
            handlers.usercommandhandlers.ClanPenalty,
            handlers.usercommandhandlers.Dismount,
            handlers.usercommandhandlers.Unstuck,
            handlers.usercommandhandlers.Time,
            handlers.usercommandhandlers.SiegeStatus,
            handlers.usercommandhandlers.PartyInfo,
            handlers.usercommandhandlers.OlympiadStat,
            handlers.usercommandhandlers.MyBirthday,
            handlers.usercommandhandlers.Mount,
            handlers.usercommandhandlers.Loc,
            handlers.usercommandhandlers.InstanceZone;

    provides org.l2j.gameserver.handler.IVoicedCommandHandler
        with handlers.voicedcommandhandlers.ExperienceGain,
            handlers.voicedcommandhandlers.SetVCmd,
            handlers.voicedcommandhandlers.CastleVCmd,
            handlers.voicedcommandhandlers.StatsVCmd,
            handlers.voicedcommandhandlers.Lang,
            handlers.voicedcommandhandlers.ChatAdmin,
            handlers.voicedcommandhandlers.Banking,
            handlers.voicedcommandhandlers.AutoPotion,
            handlers.voicedcommandhandlers.ChangePassword,

            //custom
            custom.SellBuff.SellBuff;


    provides org.l2j.gameserver.handler.ITargetTypeHandler
        with handlers.targethandlers.WyvernTarget,
            handlers.targethandlers.Target,
            handlers.targethandlers.Summon,
            handlers.targethandlers.Self,
            handlers.targethandlers.PcBody,
            handlers.targethandlers.OwnerPet,
            handlers.targethandlers.Others,
            handlers.targethandlers.NpcBody,
            handlers.targethandlers.None,
            handlers.targethandlers.MyParty,
            handlers.targethandlers.MyMentor,
            handlers.targethandlers.Item,
            handlers.targethandlers.HolyThing,
            handlers.targethandlers.Ground,
            handlers.targethandlers.FortressFlagpole,
            handlers.targethandlers.Enemy,
            handlers.targethandlers.EnemyOnly,
            handlers.targethandlers.EnemyNot,
            handlers.targethandlers.DoorTreasure,
            handlers.targethandlers.AdvanceBase,
            handlers.targethandlers.Artillery;

    provides org.l2j.gameserver.handler.IAffectObjectHandler
        with handlers.targethandlers.affectobject.All,
            handlers.targethandlers.affectobject.WyvernObject,
            handlers.targethandlers.affectobject.UndeadRealEnemy,
            handlers.targethandlers.affectobject.ObjectDeadNpcBody,
            handlers.targethandlers.affectobject.NotFriendPc,
            handlers.targethandlers.affectobject.Invisible,
            handlers.targethandlers.affectobject.HiddenPlace,
            handlers.targethandlers.affectobject.FriendPc,
            handlers.targethandlers.affectobject.Clan;

    provides org.l2j.gameserver.handler.IAffectScopeHandler
        with handlers.targethandlers.affectscope.BalakasScope,
            handlers.targethandlers.affectscope.WyvernScope,
            handlers.targethandlers.affectscope.SummonExceptMaster,
            handlers.targethandlers.affectscope.StaticObjectScope,
            handlers.targethandlers.affectscope.SquarePB,
            handlers.targethandlers.affectscope.Single,
            handlers.targethandlers.affectscope.RingRange,
            handlers.targethandlers.affectscope.RangeSortByHp,
            handlers.targethandlers.affectscope.PointBlank,
            handlers.targethandlers.affectscope.Pledge,
            handlers.targethandlers.affectscope.PartyPledge,
            handlers.targethandlers.affectscope.FanPB,
            handlers.targethandlers.affectscope.DeadUnion,
            handlers.targethandlers.affectscope.DeadPledge,
            handlers.targethandlers.affectscope.DeadParty,
            handlers.targethandlers.affectscope.Square,
            handlers.targethandlers.affectscope.Fan,
            handlers.targethandlers.affectscope.Party,
            handlers.targethandlers.affectscope.Range,
            handlers.targethandlers.affectscope.DeadPartyPledge;

    provides org.l2j.gameserver.handler.IPlayerActionHandler
        with handlers.playeractions.PrivateStore,
            handlers.playeractions.UnsummonServitor,
            handlers.playeractions.UnsummonPet,
            handlers.playeractions.TeleportBookmark,
            handlers.playeractions.TacticalSignUse,
            handlers.playeractions.TacticalSignTarget,
            handlers.playeractions.SocialAction,
            handlers.playeractions.SitStand,
            handlers.playeractions.ServitorStop,
            handlers.playeractions.ServitorSkillUse,
            handlers.playeractions.ServitorMove,
            handlers.playeractions.ServitorMode,
            handlers.playeractions.ServitorHold,
            handlers.playeractions.ServitorAttack,
            handlers.playeractions.RunWalk,
            handlers.playeractions.Ride,
            handlers.playeractions.PetStop,
            handlers.playeractions.PetSkillUse,
            handlers.playeractions.PetMove,
            handlers.playeractions.PetHold,
            handlers.playeractions.PetAttack,
            handlers.playeractions.InstanceZoneInfo,
            handlers.playeractions.BotReport,
            handlers.playeractions.AirshipAction;

    provides org.l2j.gameserver.model.quest.Quest
        with
             quests.Q00127_FishingSpecialistsRequest.Q00127_FishingSpecialistsRequest,
             quests.Q00255_Tutorial.Q00255_Tutorial,
             quests.Q00300_HuntingLetoLizardman.Q00300_HuntingLetoLizardman,
             quests.Q00326_VanquishRemnants.Q00326_VanquishRemnants,
             quests.Q00327_RecoverTheFarmland.Q00327_RecoverTheFarmland,
             quests.Q00328_SenseForBusiness.Q00328_SenseForBusiness,
             quests.Q00329_CuriosityOfADwarf.Q00329_CuriosityOfADwarf,
             quests.Q00331_ArrowOfVengeance.Q00331_ArrowOfVengeance,
             quests.Q00333_HuntOfTheBlackLion.Q00333_HuntOfTheBlackLion,
             quests.Q00344_1000YearsTheEndOfLamentation.Q00344_1000YearsTheEndOfLamentation,
             quests.Q00354_ConquestOfAlligatorIsland.Q00354_ConquestOfAlligatorIsland,
             quests.Q00355_FamilyHonor.Q00355_FamilyHonor,
             quests.Q00356_DigUpTheSeaOfSpores.Q00356_DigUpTheSeaOfSpores,
             quests.Q00358_IllegitimateChildOfTheGoddess.Q00358_IllegitimateChildOfTheGoddess,
             quests.Q00360_PlunderTheirSupplies.Q00360_PlunderTheirSupplies,
             quests.Q00369_CollectorOfJewels.Q00369_CollectorOfJewels,
             quests.Q00370_AnElderSowsSeeds.Q00370_AnElderSowsSeeds,
             quests.Q00500_BrothersBoundInChains.Q00500_BrothersBoundInChains,
             quests.Q00662_AGameOfCards.Q00662_AGameOfCards,
             quests.Q00933_ExploringTheWestWingOfTheDungeonOfAbyss.Q00933_ExploringTheWestWingOfTheDungeonOfAbyss,
             quests.Q00935_ExploringTheEastWingOfTheDungeonOfAbyss.Q00935_ExploringTheEastWingOfTheDungeonOfAbyss,
             quests.Q10866_PunitiveOperationOnTheDevilIsle.Q10866_PunitiveOperationOnTheDevilIsle;
}