package l2s.gameserver.stats;

import l2s.gameserver.Config;

import java.util.NoSuchElementException;

public enum Stats
{
	MAX_HP("maxHp", 0., Double.POSITIVE_INFINITY, 1.),
	MAX_MP("maxMp", 0., Double.POSITIVE_INFINITY, 1.),
	MAX_CP("maxCp", 0., Double.POSITIVE_INFINITY, 1.),

	PLAYER_MAX_HP_LIMIT("max_hp_limit", 0., Double.POSITIVE_INFINITY, 1.),
	PLAYER_MAX_MP_LIMIT("max_mp_limit", 0., Double.POSITIVE_INFINITY, 1.),
	PLAYER_MAX_CP_LIMIT("max_cp_limit", 0., Double.POSITIVE_INFINITY, 1.),

	REGENERATE_HP_RATE("regHp"),
	REGENERATE_CP_RATE("regCp"),
	REGENERATE_MP_RATE("regMp"),

	// Для эффектов типа Seal of Limit
	HP_LIMIT("hpLimit", 1., 100., 100.),
	MP_LIMIT("mpLimit", 1., 100., 100.),
	CP_LIMIT("cpLimit", 1., 100., 100.),

	RUN_SPEED("runSpd"),

	POWER_DEFENCE("pDef"),
	MAGIC_DEFENCE("mDef"),
	POWER_ATTACK("pAtk"),
	MAGIC_ATTACK("mAtk"),
	POWER_ATTACK_SPEED("pAtkSpd"),
	MAGIC_ATTACK_SPEED("mAtkSpd"),

	MAGIC_REUSE_RATE("mReuse"),
	PHYSIC_REUSE_RATE("pReuse"),
	MUSIC_REUSE_RATE("musicReuse"),
	ATK_REUSE("atkReuse"),
	BASE_P_ATK_SPD("basePAtkSpd"),
	BASE_M_ATK_SPD("baseMAtkSpd"),

	P_EVASION_RATE("pEvasRate"),
	M_EVASION_RATE("mEvasRate"),
	P_ACCURACY_COMBAT("pAccCombat"),
	M_ACCURACY_COMBAT("mAccCombat"),

	BASE_P_CRITICAL_RATE("basePCritRate", 0., Double.POSITIVE_INFINITY), // static crit rate. Use it to ADD some crit points. Sample: <add order="0x40" stat="baseCrit" val="27.4" />
	BASE_M_CRITICAL_RATE("baseMCritRate", 0., Double.POSITIVE_INFINITY),

	P_CRITICAL_RATE("pCritRate", 0., Double.POSITIVE_INFINITY, 100.),
	M_CRITICAL_RATE("mCritRate", 0., Double.POSITIVE_INFINITY, 100.),

	P_CRITICAL_DAMAGE_PER("p_critical_damage_per", 0., Double.POSITIVE_INFINITY, 100.),
	P_MAGIC_CRITICAL_DMG_PER("p_magic_critical_dmg_per", 0., Double.POSITIVE_INFINITY, 100.),
	P_CRITICAL_DAMAGE_DIFF("p_critical_damage_diff"),
	P_MAGIC_CRITICAL_DMG_DIFF("p_magic_critical_dmg_diff"),

	P_SKILL_CRITICAL_DAMAGE_PER("p_skill_critical_damage_per", 0., Double.POSITIVE_INFINITY, 100.),
	P_SKILL_CRITICAL_DAMAGE_DIFF("p_skill_critical_damage_diff"),

	INFLICTS_P_DAMAGE_POWER("inflicts_p_damage_power"),
	INFLICTS_M_DAMAGE_POWER("inflicts_m_damage_power"),
	RECEIVE_P_DAMAGE_POWER("receive_p_damage_power"),
	RECEIVE_M_DAMAGE_POWER("receive_m_damage_power"),

	CAST_INTERRUPT("concentration", 0., 100.),
	SHIELD_DEFENCE("sDef"),
	SHIELD_RATE("rShld", 0., 90.),
	SHIELD_ANGLE("shldAngle", 0., 360., 60.),

	POWER_ATTACK_RANGE("pAtkRange", 0., 1500.),
	MAGIC_ATTACK_RANGE("mAtkRange", 0., 1500.),
	P_ATTACK_RADIUS("p_attack_radius", 0., 1500.),
	POLE_ATTACK_ANGLE("poleAngle", 0., 180.),
	ATTACK_TARGETS_COUNT("attack_targets_count"),
	POLE_TARGET_COUNT("poleTargetCount"),

	STAT_STR("STR", 1., 100.),
	STAT_CON("CON", 1., 100.),
	STAT_DEX("DEX", 1., 100.),
	STAT_INT("INT", 1., 100.),
	STAT_WIT("WIT", 1., 100.),
	STAT_MEN("MEN", 1., 100.),

	BREATH("breath"),
	FALL("fall"),
	EXP_LOST("expLost"),

	CANCEL_RESIST("cancelResist", -200., 300.),
	MAGIC_RESIST("magicResist", -200., 300.),
	BLOW_RESIST("blow_resist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),

	CANCEL_POWER("cancelPower", -200., 200.),
	MAGIC_POWER("magicPower", -200., 200.),
	BLOW_POWER("blow_power", -200., 200.),

	RESIST_ABNORMAL_BUFF("resist_abnormal_buff", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	RESIST_ABNORMAL_DEBUFF("resist_abnormal_debuff", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),

	FATALBLOW_RATE("blowRate", 0., 10., 1.),
	SKILL_CRIT_CHANCE_MOD("SkillCritChanceMod", 10., 190., 100.),
	DEATH_VULNERABILITY("deathVuln", 10., 190., 100.),

	P_CRIT_DAMAGE_RECEPTIVE("pCritDamRcpt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100.),
	M_CRIT_DAMAGE_RECEPTIVE("mCritDamRcpt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	P_CRIT_CHANCE_RECEPTIVE("pCritChanceRcpt", 10., 190., 100.),
	M_CRIT_CHANCE_RECEPTIVE("mCritChanceRcpt", 10., 190., 100.),

	DEFENCE_FIRE("defenceFire", -600., 600.),
	DEFENCE_WATER("defenceWater", -600., 600.),
	DEFENCE_WIND("defenceWind", -600., 600.),
	DEFENCE_EARTH("defenceEarth", -600., 600.),
	DEFENCE_HOLY("defenceHoly", -600., 600.),
	DEFENCE_UNHOLY("defenceUnholy", -600., 600.),

	BASE_ELEMENTS_DEFENCE("elements_defence", -600., 600.),

	ATTACK_FIRE("attackFire", 0., Config.ELEMENT_ATTACK_LIMIT),
	ATTACK_WATER("attackWater", 0., Config.ELEMENT_ATTACK_LIMIT),
	ATTACK_WIND("attackWind", 0., Config.ELEMENT_ATTACK_LIMIT),
	ATTACK_EARTH("attackEarth", 0., Config.ELEMENT_ATTACK_LIMIT),
	ATTACK_HOLY("attackHoly", 0., Config.ELEMENT_ATTACK_LIMIT),
	ATTACK_UNHOLY("attackUnholy", 0., Config.ELEMENT_ATTACK_LIMIT),

	ABSORB_DAMAGE_PERCENT("absorbDam", 0., 100., 0.),
	ABSORB_BOW_DAMAGE_PERCENT("absorbBowDam", 0., 100., 0.),
	ABSORB_PSKILL_DAMAGE_PERCENT("absorbPSkillDam", 0., 100., 0.),
	ABSORB_MSKILL_DAMAGE_PERCENT("absorbMSkillDam", 0., 100., 0.),
	ABSORB_DAMAGEMP_PERCENT("absorbDamMp", 0., 100., 0.),

	TRANSFER_TO_SUMMON_DAMAGE_PERCENT("transferPetDam", 0., 100.),
	TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT("transferToEffectorDam", 0., 100.),
	TRANSFER_TO_MP_DAMAGE_PERCENT("p_mp_shield", 0., 100.),

	// Отражение урона с шансом. Урон получает только атакующий.
	REFLECT_AND_BLOCK_DAMAGE_CHANCE("reflectAndBlockDam", 0., Config.REFLECT_AND_BLOCK_DAMAGE_CHANCE_CAP), // Ближний урон без скиллов
	REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE("reflectAndBlockPSkillDam", 0., Config.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE_CAP), // Ближний урон скиллами
	REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE("reflectAndBlockMSkillDam", 0., Config.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE_CAP), // Любой урон магией

	// Отражение урона в процентах. Урон получает и атакующий и цель
	REFLECT_DAMAGE_PERCENT("reflectDam", 0., Config.REFLECT_DAMAGE_PERCENT_CAP), // Ближний урон без скиллов
	REFLECT_BOW_DAMAGE_PERCENT("reflectBowDam", 0., Config.REFLECT_BOW_DAMAGE_PERCENT_CAP), // Урон луком без скиллов
	REFLECT_PSKILL_DAMAGE_PERCENT("reflectPSkillDam", 0., Config.REFLECT_PSKILL_DAMAGE_PERCENT_CAP), // Ближний урон скиллами
	REFLECT_MSKILL_DAMAGE_PERCENT("reflectMSkillDam", 0., Config.REFLECT_MSKILL_DAMAGE_PERCENT_CAP), // Любой урон магией

	REFLECT_PHYSIC_SKILL("reflectPhysicSkill", 0., 60.),
	REFLECT_MAGIC_SKILL("reflectMagicSkill", 0., 60.),

	REFLECT_PHYSIC_DEBUFF("reflectPhysicDebuff", 0., 60.),
	REFLECT_MAGIC_DEBUFF("reflectMagicDebuff", 0., 60.),

	P_SKILL_EVASION("pSkillEvasion", 100., 200.),
	COUNTER_ATTACK("counterAttack", 0., 100.),

	P_SKILL_POWER("p_skill_power"),
	P_SKILL_POWER_STATIC("pSkillPowerStatic"),
	M_SKILL_POWER("mSkillPower"),
	CHARGED_P_SKILL_POWER("charged_p_skill_power"),

	// PvP Dmg bonus
	PVP_PHYS_DMG_BONUS("pvpPhysDmgBonus"),
	PVP_PHYS_SKILL_DMG_BONUS("pvpPhysSkillDmgBonus"),
	PVP_MAGIC_SKILL_DMG_BONUS("pvpMagicSkillDmgBonus"),
	// PvP Def bonus
	PVP_PHYS_DEFENCE_BONUS("pvpPhysDefenceBonus"),
	PVP_PHYS_SKILL_DEFENCE_BONUS("pvpPhysSkillDefenceBonus"),
	PVP_MAGIC_SKILL_DEFENCE_BONUS("pvpMagicSkillDefenceBonus"),

	// PvE Dmg bonus
	PVE_PHYS_DMG_BONUS("pvePhysDmgBonus"),
	PVE_PHYS_SKILL_DMG_BONUS("pvePhysSkillDmgBonus"),
	PVE_MAGIC_SKILL_DMG_BONUS("pveMagicSkillDmgBonus"),
	// PvE Def bonus
	PVE_PHYS_DEFENCE_BONUS("pvePhysDefenceBonus"),
	PVE_PHYS_SKILL_DEFENCE_BONUS("pvePhysSkillDefenceBonus"),
	PVE_MAGIC_SKILL_DEFENCE_BONUS("pveMagicSkillDefenceBonus"),

	HEAL_EFFECTIVNESS("hpEff", 0., 1000.),
	MANAHEAL_EFFECTIVNESS("mpEff", 0., 1000.),
	CPHEAL_EFFECTIVNESS("cpEff", 0., 1000.),
	HEAL_POWER("healPower"),
	MP_MAGIC_SKILL_CONSUME("mpConsum"),
	MP_PHYSICAL_SKILL_CONSUME("mpConsumePhysical"),
	MP_DANCE_SKILL_CONSUME("mpDanceConsume"),

	CHEAP_SHOT("cheap_shot"),

	MAX_LOAD("maxLoad"),
	MAX_NO_PENALTY_LOAD("maxNoPenaltyLoad"),
	INVENTORY_LIMIT("inventoryLimit"),
	STORAGE_LIMIT("storageLimit"),
	TRADE_LIMIT("tradeLimit"),
	COMMON_RECIPE_LIMIT("CommonRecipeLimit"),
	DWARVEN_RECIPE_LIMIT("DwarvenRecipeLimit"),
	BUFF_LIMIT("buffLimit"),
	SOULS_LIMIT("soulsLimit"),
	SOULS_CONSUME_EXP("soulsExp"),
	TALISMANS_LIMIT("talismansLimit", 0., 6.),
	JEWELS_LIMIT("jewels_limit", 0., 6.),
	CUBICS_LIMIT("cubicsLimit", 0., 3., 1.),
	MAX_INCREASED_FORCE("max_increased_force"),

	GRADE_EXPERTISE_LEVEL("gradeExpertiseLevel"),
	EXP_RATE_MULTIPLIER("exp_rate_multiplier"),
	SP_RATE_MULTIPLIER("sp_rate_multiplier"),
	ADENA_RATE_MULTIPLIER("adena_rate_multiplier"),
	DROP_RATE_MULTIPLIER("drop_rate_multiplier"),
	SPOIL_RATE_MULTIPLIER("spoil_rate_multiplier"),

	DROP_CHANCE_MODIFIER("drop_chance_modifier"),
	SPOIL_CHANCE_MODIFIER("spoil_chance_modifier"),

	SKILLS_ELEMENT_ID("skills_element_id", -1., 100., -1.),
	DAMAGE_AGGRO_PERCENT("damageAggroPercent", 0., 300., 0.),
	RECIEVE_DAMAGE_LIMIT("recieveDamageLimit", -1, Double.POSITIVE_INFINITY, -1),
	RECIEVE_DAMAGE_LIMIT_P_SKILL("recieveDamageLimitPSkill", -1, Double.POSITIVE_INFINITY, -1),
	RECIEVE_DAMAGE_LIMIT_M_SKILL("recieveDamageLimitMSkill", -1, Double.POSITIVE_INFINITY, -1),
	KILL_AND_RESTORE_HP("killAndRestoreHp", 0., 100., 0.),
	RESIST_REFLECT_DAM("resistRelectDam", 0., 100., 0.),
	
	BUFF_TIME_MODIFIER("buff_time_modifier", 1., Double.POSITIVE_INFINITY, 1.),
	DEBUFF_TIME_MODIFIER("debuff_time_modifier", 1., Double.POSITIVE_INFINITY, 1.),

	P_SKILL_CRIT_RATE_DEX_DEPENDENCE("p_skill_crit_rate_dex_dependence", 0., 1., 0.),
	SPEED_ON_DEX_DEPENDENCE("speed_on_dex_dependence", 0., 1., 0.),

	ENCHANT_CHANCE_MODIFIER("enchant_chance_modifier", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.),

	SOULSHOT_POWER("soulshot_power", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	SPIRITSHOT_POWER("spiritshot_power", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),

	DAMAGE_BLOCK_RADIUS("damage_block_radius", -1., Double.POSITIVE_INFINITY, -1.),

	DAMAGE_HATE_BONUS("DAMAGE_HATE_BONUS"),

	ShillienProtection("shillienProtection", 0., 1., 0.),
	SacrificialSoul("sacrificialSoul", 0., 1., 0.),
	RestoreHPGiveDamage("restoreHPGiveDamage", 0., 1., 0.),
	MarkOfTrick("MarkOfTrick", 0., 1., 0.),
	DivinityOfEinhasad("DivinityOfEinhasad", 0., 1., 0.),
	BlockFly("blockFly", 0., 1., 0.),

	P_CRIT_RATE_LIMIT("p_crit_rate_limit"),

	ADDITIONAL_EXPERTISE_INDEX("additional_expertise_index"),

	PHYSICAL_ABNORMAL_RESIST("p_physical_abnormal_resist", -100., 100.),
	MAGIC_ABNORMAL_RESIST("p_magic_abnormal_resist", -100., 100.),

	ATTACK_TRAIT_SWORD("attack_trait_sword", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_SWORD("defence_trait_sword", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_BLUNT("attack_trait_blunt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_BLUNT("defence_trait_blunt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DAGGER("attack_trait_dagger", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DAGGER("defence_trait_dagger", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_POLE("attack_trait_pole", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_POLE("defence_trait_pole", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_FIST("attack_trait_fist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_FIST("defence_trait_fist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_BOW("attack_trait_bow", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_BOW("defence_trait_bow", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_ETC("attack_trait_etc", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_ETC("defence_trait_etc", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_POISON("attack_trait_poison", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_POISON("defence_trait_poison", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_HOLD("attack_trait_hold", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_HOLD("defence_trait_hold", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_BLEED("attack_trait_bleed", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_BLEED("defence_trait_bleed", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_SLEEP("attack_trait_sleep", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_SLEEP("defence_trait_sleep", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_SHOCK("attack_trait_shock", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_SHOCK("defence_trait_shock", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DERANGEMENT("attack_trait_derangement", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DERANGEMENT("defence_trait_derangement", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_BUG_WEAKNESS("attack_trait_bug_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_BUG_WEAKNESS("defence_trait_bug_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_ANIMAL_WEAKNESS("attack_trait_animal_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_ANIMAL_WEAKNESS("defence_trait_animal_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_PLANT_WEAKNESS("attack_trait_plant_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_PLANT_WEAKNESS("defence_trait_plant_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_BEAST_WEAKNESS("attack_trait_beast_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_BEAST_WEAKNESS("defence_trait_beast_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DRAGON_WEAKNESS("attack_trait_dragon_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DRAGON_WEAKNESS("defence_trait_dragon_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_PARALYZE("attack_trait_paralyze", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_PARALYZE("defence_trait_paralyze", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DUAL("attack_trait_dual", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DUAL("defence_trait_dual", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DUALFIST("attack_trait_dualfist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DUALFIST("defence_trait_dualfist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_BOSS("attack_trait_boss", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_BOSS("defence_trait_boss", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_GIANT_WEAKNESS("attack_trait_giant_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_GIANT_WEAKNESS("defence_trait_giant_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_CONSTRUCT_WEAKNESS("attack_trait_construct_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_CONSTRUCT_WEAKNESS("defence_trait_construct_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DEATH("attack_trait_death", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DEATH("defence_trait_death", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_VALAKAS("attack_trait_valakas", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_VALAKAS("defence_trait_valakas", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_ROOT_PHYSICALLY("attack_trait_root_physically", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_ROOT_PHYSICALLY("defence_trait_root_physically", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_RAPIER("attack_trait_rapier", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_RAPIER("defence_trait_rapier", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_CROSSBOW("attack_trait_crossbow", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_CROSSBOW("defence_trait_crossbow", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_ANCIENTSWORD("attack_trait_ancientsword", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_ANCIENTSWORD("defence_trait_ancientsword", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_TURN_STONE("attack_trait_turn_stone", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_TURN_STONE("defence_trait_turn_stone", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_GUST("attack_trait_gust", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_GUST("defence_trait_gust", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_PHYSICAL_BLOCKADE("attack_trait_physical_blockade", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_PHYSICAL_BLOCKADE("defence_trait_physical_blockade", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_TARGET("attack_trait_target", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_TARGET("defence_trait_target", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_PHYSICAL_WEAKNESS("attack_trait_physical_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_PHYSICAL_WEAKNESS("defence_trait_physical_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_MAGICAL_WEAKNESS("attack_trait_magical_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_MAGICAL_WEAKNESS("defence_trait_magical_weakness", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DUALDAGGER("attack_trait_dualdagger", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DUALDAGGER("defence_trait_dualdagger", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DUALBLUNT("attack_trait_dualblunt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DUALBLUNT("defence_trait_dualblunt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_KNOCKBACK("attack_trait_knockback", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_KNOCKBACK("defence_trait_knockback", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_KNOCKDOWN("attack_trait_knockdown", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_KNOCKDOWN("defence_trait_knockdown", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_PULL("attack_trait_pull", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_PULL("defence_trait_pull", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_HATE("attack_trait_hate", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_HATE("defence_trait_hate", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_AGGRESSION("attack_trait_aggression", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_AGGRESSION("defence_trait_aggression", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_AIRBIND("attack_trait_airbind", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_AIRBIND("defence_trait_airbind", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DISARM("attack_trait_disarm", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DISARM("defence_trait_disarm", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_DEPORT("attack_trait_deport", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_DEPORT("defence_trait_deport", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_CHANGEBODY("attack_trait_changebody", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_CHANGEBODY("defence_trait_changebody", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ATTACK_TRAIT_TWOHANDCROSSBOW("attack_trait_twohandcrossbow", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_TRAIT_TWOHANDCROSSBOW("defence_trait_twohandcrossbow", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

	public static final Stats[] VALUES = values();
	public static final int NUM_STATS = VALUES.length;

	private final String _value;
	private double _min;
	private double _max;
	private double _init;

	public String getValue()
	{
		return _value;
	}

	public double getInit()
	{
		return _init;
	}

	private Stats(String s)
	{
		this(s, 0., Double.POSITIVE_INFINITY, 0.);
	}

	private Stats(String s, double min, double max)
	{
		this(s, min, max, 0.);
	}

	private Stats(String s, double min, double max, double init)
	{
		_value = s.toUpperCase();
		_min = min;
		_max = max;
		_init = init;
	}

	public double validate(double val)
	{
		if(val < _min)
			return _min;
		if(val > _max)
			return _max;
		return val;
	}

	public static Stats valueOfXml(String name)
	{
		String upperCaseName = name.toUpperCase();
		for(Stats s : VALUES)
			if(s.getValue().equals(upperCaseName))
				return s;

		throw new NoSuchElementException("Unknown name '" + name + "' for enum Stats");
	}

	@Override
	public String toString()
	{
		return _value;
	}
}