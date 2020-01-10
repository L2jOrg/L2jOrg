package handlers;

import org.l2j.gameserver.handler.SkillConditionHandler;

import handlers.skillconditionhandlers.*;

/**
 * @author NosBit
 */
public class SkillConditionMasterHandler {
	public static void main(String[] args) {
		SkillConditionHandler.getInstance().registerHandler("BuildAdvanceBase", BuildAdvanceBaseSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("BuildCamp", BuildCampSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanBookmarkAddSlot", CanBookmarkAddSlotSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CannotUseInTransform", CannotUseInTransformSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanSummon", CanSummonSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanSummonCubic", CanSummonCubicSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanSummonPet", CanSummonPetSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanSummonSiegeGolem", CanSummonSiegeGolemSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanTransform", CanTransformSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanUntransform", CanUntransformSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanUseInBattlefield", CanUseInBattlefieldSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CanUseSwoopCannon", CanUseSwoopCannonSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CheckLevel", CheckLevelSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("CheckSex", CheckSexSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("ConsumeBody", ConsumeBodySkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("EnergySaved", EnergySavedSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("EquipArmor", EquipArmorSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("EquipShield", EquipShieldSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("EquipWeapon", EquipWeaponSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("NotFeared", NotFearedSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("NotInUnderwater", NotInUnderwaterSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("Op2hWeapon", Op2hWeaponSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpAlignment", OpAlignmentSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpBlink", OpBlinkSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpCallPc", OpCallPcSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpCanEscape", OpCanEscapeSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpCanNotUseAirship", OpCanNotUseAirshipSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpChangeWeapon", OpChangeWeaponSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpCheckAbnormal", OpCheckAbnormalSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpCheckCastRange", OpCheckCastRangeSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpCheckClassList", OpCheckClassListSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpCheckResidence", OpCheckResidenceSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpEnergyMax", OpEnergyMaxSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpEncumbered", OpEncumberedSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpExistNpc", OpExistNpcSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpHaveSummon", OpHaveSummonSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpHome", OpHomeSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpMainjob", OpMainjobSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpNeedAgathion", OpNeedAgathionSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpNotCursed", OpNotCursedSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpPkcount", OpPkcountSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpPledge", OpPledgeSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpResurrection", OpResurrectionSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpSkillAcquire", OpSkillAcquireSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpSoulMax", OpSoulMaxSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpSweeper", OpSweeperSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpTargetDoor", OpTargetDoorSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpTargetNpc", OpTargetNpcSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpUnlock", OpUnlockSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("OpWyvern", OpWyvernSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("PossessHolything", PossessHolythingSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("RemainCpPer", RemainCpPerSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("RemainHpPer", RemainHpPerSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("RemainMpPer", RemainMpPerSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("SoulSaved", SoulSavedSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("TargetMyParty", TargetMyPartySkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("TargetMyPledge", TargetMyPledgeSkillCondition::new);
		SkillConditionHandler.getInstance().registerHandler("TargetRace", TargetRaceSkillCondition::new);
	}
}
