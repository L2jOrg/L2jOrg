import handlers.actionhandlers.*;
import handlers.actionshifthandlers.*;
import handlers.admincommandhandlers.AdminCoins;
import handlers.conditions.CategoryTypeCondition;
import handlers.conditions.NpcLevelCondition;
import handlers.conditions.PlayerLevelCondition;
import handlers.effecthandlers.*;
import handlers.effecthandlers.stat.*;
import handlers.itemhandlers.TransformationBook;
import handlers.skillconditionhandlers.*;
import instances.sevensigns.SevenSigns;
import quests.tutorial.Q10960_Tutorial.Q10960_Tutorial;
import quests.tutorial.Q201_Tutorial.Q201_Tutorial;
import quests.tutorial.Q202_Tutorial.Q202_Tutorial;
import quests.tutorial.Q203_Tutorial.Q203_Tutorial;
import quests.tutorial.Q204_Tutorial.Q204_Tutorial;
import quests.tutorial.Q205_Tutorial.Q205_Tutorial;
import quests.tutorial.Q206_Tutorial.Q206_Tutorial;

open module org.l2j.scripts {

    requires java.desktop;
    requires java.logging;
    requires java.sql;
    requires io.github.joealisson.primitive;
    requires org.l2j.commons;
    requires org.l2j.gameserver;
    requires org.slf4j;

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
    uses org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
    uses org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
    uses org.l2j.gameserver.model.conditions.ConditionFactory;
    uses org.l2j.gameserver.engine.mission.MissionHandlerFactory;

    provides org.l2j.gameserver.model.conditions.ConditionFactory
            with NpcLevelCondition.Factory,
                CategoryTypeCondition.Factory,
                PlayerLevelCondition.Factory
            ;

    provides org.l2j.gameserver.engine.skill.api.SkillEffectFactory
            with AbnormalShield.Factory,
                AcquireCostume.Factory,
                AcquireRandomCostume.Factory,
                AddHate.Factory,
                AddHuntingTime.Factory,
                AddTeleportBookmarkSlot.Factory,
                StatModify.Factory,
                AttackTrait.Factory,
                Backstab.Factory,
                BaseStatsModify.Factory,
                Betray.Factory,
                BlockAbnormalSlot.Factory,
                BlockAction.Factory,
                BlockActions.Factory,
                BlockChat.Factory,
                BlockControl.Factory,
                BlockEscape.Factory,
                BlockMove.Factory,
                BlockParty.Factory,
                BlockResurrection.Factory,
                BlockSkill.Factory,
                BlockTarget.Factory,
                Bluff.Factory,
                BuffBlock.Factory,
                CallParty.Factory,
                CallPc.Factory,
                CallSkill.Factory,
                CheapShot.Factory,
                ChameleonRest.Factory,
                ChangeFace.Factory,
                ChangeHairColor.Factory,
                ChangeHairStyle.Factory,
                Confuse.Factory,
                ConsumeBody.Factory,
                ConvertItem.Factory,
                Cp.Factory,
                CpHeal.Factory,
                CpHealPercent.Factory,
                StatPositionBased.Factory,
                StatHpBased.Factory,
                DamageBlock.Factory,
                DamageByAttack.Factory,
                DamOverTime.Factory,
                DeathLink.Factory,
                DebuffBlock.Factory,
                DefenceTrait.Factory,
                DeleteHate.Factory,
                DeleteHateOfMe.Factory,
                Detection.Factory,
                DisableTargeting.Factory,
                Disarm.Factory,
                DispelAll.Factory,
                DispelByCategory.Factory,
                DispelBySlot.Factory,
                DispelBySlotMyself.Factory,
                DispelBySlotProbability.Factory,
                EnergyAttack.Factory,
                EnlargeAbnormalSlot.Factory,
                EnlargeSlot.Factory,
                Escape.Factory,
                FakeDeath.Factory,
                FatalBlow.Factory,
                Fear.Factory,
                Feed.Factory,
                Flag.Factory,
                FocusMomentum.Factory,
                FocusMaxMomentum.Factory,
                FocusSouls.Factory,
                GetAgro.Factory,
                GiveClanReputation.Factory,
                GiveSp.Factory,
                GiveXp.Factory,
                GiveExpAndSp.Factory,
                Grow.Factory,
                HairAccessorySet.Factory,
                HeadquarterCreate.Factory,
                Heal.Factory,
                HealOverTime.Factory,
                HealPercent.Factory,
                Hide.Factory,
                Hp.Factory,
                HpByLevel.Factory,
                HpCpHeal.Factory,
                HpDrain.Factory,
                IgnoreDeath.Factory,
                ImmobilePetBuff.Factory,
                KnockBack.Factory,
                Lethal.Factory,
                Lucky.Factory,
                MagicalAttack.Factory,
                MagicalAttackByAbnormal.Factory,
                MagicalAttackByAbnormalSlot.Factory,
                MagicalAttackMp.Factory,
                MagicalAttackRange.Factory,
                MagicalSoulAttack.Factory,
                MagicMpCost.Factory,
                ManaDamOverTime.Factory,
                ManaHeal.Factory,
                ManaHealByLevel.Factory,
                ManaHealOverTime.Factory,
                ManaHealPercent.Factory,
                VitalStatModify.Factory,
                ModifyVital.Factory,
                Mp.Factory,
                MpConsumePerLevel.Factory,
                Mute.Factory,
                NoblesseBless.Factory,
                ObtainSoul.Factory,
                OpenChest.Factory,
                OpenCommonRecipeBook.Factory,
                OpenDoor.Factory,
                OpenDwarfRecipeBook.Factory,
                Passive.Factory,
                PhysicalAttack.Factory,
                PhysicalAttackHpLink.Factory,
                PhysicalAttackMute.Factory,
                PhysicalMute.Factory,
                PhysicalShieldAngleAll.Factory,
                PhysicalSoulAttack.Factory,
                PkCount.Factory,
                PolearmSingleTarget.Factory,
                ProtectionBlessing.Factory,
                PullBack.Factory,
                RandomizeHate.Factory,
                RealDamage.Factory,
                RebalanceHP.Factory,
                ReduceDamage.Factory,
                ReduceDropPenalty.Factory,
                Relax.Factory,
                ResistAbnormalByCategory.Factory,
                ResistDispelByCategory.Factory,
                Restoration.Factory,
                RestorationRandom.Factory,
                Resurrection.Factory,
                ResurrectionSpecial.Factory,
                Reuse.Factory,
                ReuseSkillById.Factory,
                Root.Factory,
                SendSystemMessageToClan.Factory,
                ServitorShare.Factory,
                SilentMove.Factory,
                SkillCritical.Factory,
                SkillEvasion.Factory,
                SkillTurning.Factory,
                SoulBlow.Factory,
                SoulEating.Factory,
                Speed.Factory,
                Spoil.Factory,
                StatByMoveType.Factory,
                StatUp.Factory,
                StatsLinkedEffect.Factory,
                StealAbnormal.Factory,
                Stun.Factory,
                Summon.Factory,
                SummonAgathion.Factory,
                SummonCubic.Factory,
                SummonNpc.Factory,
                SummonPet.Factory,
                Sweeper.Factory,
                TakeCastle.Factory,
                TakeCastleStart.Factory,
                TalismanSlot.Factory,
                TargetCancel.Factory,
                TargetMe.Factory,
                TargetMeProbability.Factory,
                Teleport.Factory,
                TeleportToTarget.Factory,
                TransferDamageToPlayer.Factory,
                TransferHate.Factory,
                Transformation.Factory,
                TriggerSkillByAttack.Factory,
                TriggerSkillByAttacking.Factory,
                TriggerSkillByAvoid.Factory,
                TriggerSkillByChargeShot.Factory,
                TriggerSkillByDamage.Factory,
                TriggerSkillByChangeExp.Factory,
                TriggerSkillByMagicType.Factory,
                TriggerSkillBySkill.Factory,
                TwoHandedBluntBonus.Factory,
                TwoHandedSwordBonus.Factory,
                Unsummon.Factory,
                UnsummonAgathion.Factory,
                VampiricAttack.Factory
            ;

    provides org.l2j.gameserver.engine.skill.api.SkillConditionFactory
            with BuildAdvanceBaseSkillCondition.Factory,
                BuildCampSkillCondition.Factory,
                CanBookmarkAddSlotSkillCondition.Factory,
                CannotUseInTransformSkillCondition.Factory,
                CanSummonCubicSkillCondition.Factory,
                CanSummonSkillCondition.Factory,
                CanSummonPetSkillCondition.Factory,
                CanSummonSiegeGolemSkillCondition.Factory,
                CanTransformSkillCondition.Factory,
                CanUntransformSkillCondition.Factory,
                CanUseInBattlefieldSkillCondition.Factory,
                CanUseSwoopCannonSkillCondition.Factory,
                CheckLevelSkillCondition.Factory,
                CheckRangeSkillCondition.Factory,
                FemaleSkillCondition.Factory,
                ConsumeBodySkillCondition.Factory,
                EnergySavedSkillCondition.Factory,
                EquipArmorSkillCondition.Factory,
                EquipShieldSkillCondition.Factory,
                EquipWeaponSkillCondition.Factory,
                NotFearedSkillCondition.Factory,
                NotInUnderwaterSkillCondition.Factory,
                NonChaoticSkillCondition.Factory,
                OpBlinkSkillCondition.Factory,
                OpCallPcSkillCondition.Factory,
                OpCanEscapeSkillCondition.Factory,
                OpChangeWeaponSkillCondition.Factory,
                OpCheckAbnormalSkillCondition.Factory,
                OpCheckClassListSkillCondition.Factory,
                OpCheckResidenceSkillCondition.Factory,
                OpEnergyMaxSkillCondition.Factory,
                OpEncumberedSkillCondition.Factory,
                OpExistNpcSkillCondition.Factory,
                OpHaveSummonSkillCondition.Factory,
                OpHomeSkillCondition.Factory,
                OpMainjobSkillCondition.Factory,
                OpNeedAgathionSkillCondition.Factory,
                KillerSkillCondition.Factory,
                OpPledgeSkillCondition.Factory,
                OpResurrectionSkillCondition.Factory,
                OpSkillAcquireSkillCondition.Factory,
                OpSoulMaxSkillCondition.Factory,
                OpSweeperSkillCondition.Factory,
                SkillConditionTargetFactory,
                OpUnlockSkillCondition.Factory,
                OpWyvernSkillCondition.Factory,
                PossessHolythingSkillCondition.Factory,
                RemainStatusSkillCondition.Factory,
                SoulSavedSkillCondition.Factory,
                TargetMyPledgeSkillCondition.Factory;

    provides instances.AbstractInstance
        with  instances.MonsterArena.MonsterArena,
            instances.ResidenceOfQueenNebula.ResidenceOfQueenNebula,
            instances.ResidenceOfKingProcella.ResidenceOfKingProcella,
            instances.ResidenceOfKingPetram.ResidenceOfKingPetram,
            instances.ResidenceOfKingIgnis.ResidenceOfKingIgnis,
            instances.GolbergRoom.GolbergRoom,
            instances.LastImperialTomb.LastImperialTomb,
                SevenSigns;



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

            handlers.communityboard.AutoHpMpCp,

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
            ai.areas.DungeonOfAbyss.Tores.Tores,
            ai.areas.DwarvenVillage.Toma.Toma,
            ai.areas.LairOfAntharas.Pytan,
            ai.areas.TalkingIsland.Roxxy,
            ai.areas.TowerOfInsolence.Ateld,
            ai.areas.PlainsOfDion,
            ai.areas.ImperialTomb.FourSepulchers.FourSepulchers,
            ai.areas.CallOfTheSpirits.CallOfTheSpirits,
            ai.areas.Varkas.Althars,
            ai.areas.Ketra.FireCamp,
            ai.areas.GiantCave.Scout,
            ai.areas.GiantCave.Batur,
            ai.areas.GiantCave.EntranceRoom,
            ai.areas.TowerOfInsolence.TowerOfInsolence,
            ai.areas.AligatorIsland.Nos,
            ai.areas.ForestOfTheMirrors.Mirrors,
            ai.areas.TowerOfInsolence.HeavenlyRift.Bomb,
            ai.areas.TowerOfInsolence.HeavenlyRift.DivineAngel,
            ai.areas.TowerOfInsolence.HeavenlyRift.Tower,


            ai.bosses.Antharas.Antharas,
            ai.bosses.Baium.Baium,
            ai.bosses.Core.Core,
            ai.bosses.Orfen.Orfen,
            ai.bosses.QueenAnt.QueenAnt,
            ai.bosses.Zaken.Zaken,
            ai.bosses.LimitBarrier,


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
            ai.others.DimensionalMerchant.DimensionalMerchant,
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
        with handlers.bypasshandlers.NpcViewMod,
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
            handlers.bypasshandlers.UpgradeHandler,
            handlers.bypasshandlers.VoiceCommand,
            handlers.bypasshandlers.Wear,

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
        with handlers.itemhandlers.ItemSkillsTemplate, // skills
            handlers.itemhandlers.ItemSkills, // skills
            handlers.itemhandlers.BeastSoulShot, // skills | not used ?
            handlers.itemhandlers.BeastSpiritShot, //skills | not used ?
            handlers.itemhandlers.BlessedBeastSpiritShot,
            handlers.itemhandlers.BlessedSoulShots, // skills
            handlers.itemhandlers.BlessedSpiritShot, // skills
            handlers.itemhandlers.Book,  // none | not used ?
            handlers.itemhandlers.Bypass, //none | not used ?
            handlers.itemhandlers.Calculator, // none
            handlers.itemhandlers.ChangeAttributeCrystal, // none | not used ?
            handlers.itemhandlers.CharmOfCourage, // none | not used ?
            handlers.itemhandlers.Elixir, // skills | not used ?
            handlers.itemhandlers.EnchantScrolls, // none
            handlers.itemhandlers.EventItem, // skills | not used?
            handlers.itemhandlers.ExtractableItems, // capsuled_items, extract_max, extract_min
            handlers.itemhandlers.FatedSupportBox, // none | not used ?
            handlers.itemhandlers.FishShots, // skills
            handlers.itemhandlers.Harvester, // skills | not used ?
            handlers.itemhandlers.Maps, // none
            handlers.itemhandlers.MercTicket, // none
            handlers.itemhandlers.NicknameColor, // none
            handlers.itemhandlers.PetFood, // skills |  not used ?
            handlers.itemhandlers.Recipes, // none
            handlers.itemhandlers.RollingDice, // none
            handlers.itemhandlers.Seed, // skills | not used ?
            handlers.itemhandlers.SoulShots, // skills
            handlers.itemhandlers.SpecialXMas, // none | not used?
            handlers.itemhandlers.SpiritShot, // skills
            handlers.itemhandlers.SummonItems, // not used ?
                TransformationBook;

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
            handlers.voicedcommandhandlers.Lang,
            handlers.voicedcommandhandlers.ChatAdmin,
            handlers.voicedcommandhandlers.Banking,
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
            handlers.targethandlers.affectobject.NotFriend,
            handlers.targethandlers.affectobject.NotFriendPc,
            handlers.targethandlers.affectobject.Invisible,
            handlers.targethandlers.affectobject.HiddenPlace,
            handlers.targethandlers.affectobject.Friend,
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
        with handlers.playeractions.ActionHandler,
            handlers.playeractions.PrivateStore,
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
            handlers.playeractions.BotReport;

    provides org.l2j.gameserver.model.quest.Quest
        with
             quests.Q00127_FishingSpecialistsRequest.Q00127_FishingSpecialistsRequest,
                Q10960_Tutorial,
                Q201_Tutorial,
                Q202_Tutorial,
                Q203_Tutorial,
                Q204_Tutorial,
                Q205_Tutorial,
                Q206_Tutorial,
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
             quests.Q10866_PunitiveOperationOnTheDevilIsle.Q10866_PunitiveOperationOnTheDevilIsle,
             quests.Q10961_EffectiveTraining.Q10961_EffectiveTraining,
             quests.Q10962_NewHorizons.Q10962_NewHorizons,
             quests.Q10963_ExploringTheAntNest.Q10963_ExploringTheAntNest,
             quests.Q10964_SecretGarden.Q10964_SecretGarden,
             quests.Q10965_DeathMysteries.Q10965_DeathMysteries,
             quests.Q10966_ATripBegins.Q10966_ATripBegins,
             quests.Q10967_CulturedAdventurer.Q10967_CulturedAdventurer,
             quests.Q10981_UnbearableWolvesHowling.Q10981_UnbearableWolvesHowling,
             quests.Q10982_SpiderHunt.Q10982_SpiderHunt,
             quests.Q10983_TroubledForest.Q10983_TroubledForest,
             quests.Q10984_CollectSpiderweb.Q10984_CollectSpiderweb,
             quests.Q10985_CleaningUpTheGround.Q10985_CleaningUpTheGround,
             quests.Q10986_SwampMonster.Q10986_SwampMonster,
             quests.Q10987_PlunderedGraves.Q10987_PlunderedGraves,
             quests.Q10988_Conspiracy.Q10988_Conspiracy,
             quests.Q10989_DangerousPredators.Q10989_DangerousPredators,
             quests.Q10990_PoisonExtraction.Q10990_PoisonExtraction,
             quests.Q00620_FourGoblets.Q00620_FourGoblets;


    provides org.l2j.gameserver.engine.mission.MissionHandlerFactory
        with handlers.mission.LevelMissionHandler.Factory,
            handlers.mission.BossMissionHandler.Factory,
            handlers.mission.ClanMissionHandler.Factory,
            handlers.mission.FishingMissionHandler.Factory,
            handlers.mission.HuntMissionHandler.Factory,
            handlers.mission.LoginMissionHandler.Factory,
            handlers.mission.OlympiadMissionHandler.Factory,
            handlers.mission.QuestMissionHandler.Factory,
            handlers.mission.SiegeMissionHandler.Factory,
            handlers.mission.SpiritMissionHandler.Factory;

}
