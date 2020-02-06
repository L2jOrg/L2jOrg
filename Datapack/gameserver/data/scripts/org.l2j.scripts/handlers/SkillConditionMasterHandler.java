package handlers;

import org.l2j.gameserver.handler.SkillConditionHandler;

import handlers.skillconditionhandlers.*;

/**
 * @author NosBit
 */
public class SkillConditionMasterHandler {
	public static void main(String[] args) {
		var handlers = SkillConditionHandler.getInstance();

		handlers.registerHandler("BuildAdvanceBase", BuildAdvanceBaseSkillCondition::new);
		handlers.registerHandler("BuildCamp", BuildCampSkillCondition::new);
		handlers.registerHandler("CanBookmarkAddSlot", CanBookmarkAddSlotSkillCondition::new);
		handlers.registerHandler("CannotUseInTransform", CannotUseInTransformSkillCondition::new);
		handlers.registerHandler("CanSummon", CanSummonSkillCondition::new);
		handlers.registerHandler("CanSummonCubic", CanSummonCubicSkillCondition::new);
		handlers.registerHandler("CanSummonPet", CanSummonPetSkillCondition::new);
		handlers.registerHandler("CanSummonSiegeGolem", CanSummonSiegeGolemSkillCondition::new);
		handlers.registerHandler("CanTransform", CanTransformSkillCondition::new);
		handlers.registerHandler("CanUntransform", CanUntransformSkillCondition::new);
		handlers.registerHandler("CanUseInBattlefield", CanUseInBattlefieldSkillCondition::new);
		handlers.registerHandler("CanUseSwoopCannon", CanUseSwoopCannonSkillCondition::new);
		handlers.registerHandler("CheckLevel", CheckLevelSkillCondition::new);
		handlers.registerHandler("CheckSex", CheckSexSkillCondition::new);
		handlers.registerHandler("ConsumeBody", ConsumeBodySkillCondition::new);
		handlers.registerHandler("EnergySaved", EnergySavedSkillCondition::new);
		handlers.registerHandler("EquipArmor", EquipArmorSkillCondition::new);
		handlers.registerHandler("EquipShield", EquipShieldSkillCondition::new);
		handlers.registerHandler("EquipWeapon", EquipWeaponSkillCondition::new);
		handlers.registerHandler("NotFeared", NotFearedSkillCondition::new);
		handlers.registerHandler("NotInUnderwater", NotInUnderwaterSkillCondition::new);
		handlers.registerHandler("Op2hWeapon", Op2hWeaponSkillCondition::new);
		handlers.registerHandler("OpAlignment", OpAlignmentSkillCondition::new);
		handlers.registerHandler("OpBlink", OpBlinkSkillCondition::new);
		handlers.registerHandler("OpCallPc", OpCallPcSkillCondition::new);
		handlers.registerHandler("OpCanEscape", OpCanEscapeSkillCondition::new);
		handlers.registerHandler("OpCanNotUseAirship", OpCanNotUseAirshipSkillCondition::new);
		handlers.registerHandler("OpChangeWeapon", OpChangeWeaponSkillCondition::new);
		handlers.registerHandler("OpCheckAbnormal", OpCheckAbnormalSkillCondition::new);
		handlers.registerHandler("OpCheckCastRange", OpCheckCastRangeSkillCondition::new);
		handlers.registerHandler("OpCheckClassList", OpCheckClassListSkillCondition::new);
		handlers.registerHandler("OpCheckResidence", OpCheckResidenceSkillCondition::new);
		handlers.registerHandler("OpEnergyMax", OpEnergyMaxSkillCondition::new);
		handlers.registerHandler("OpEncumbered", OpEncumberedSkillCondition::new);
		handlers.registerHandler("OpExistNpc", OpExistNpcSkillCondition::new);
		handlers.registerHandler("OpHaveSummon", OpHaveSummonSkillCondition::new);
		handlers.registerHandler("OpHome", OpHomeSkillCondition::new);
		handlers.registerHandler("OpMainjob", OpMainjobSkillCondition::new);
		handlers.registerHandler("OpNeedAgathion", OpNeedAgathionSkillCondition::new);
		handlers.registerHandler("OpNotCursed", OpNotCursedSkillCondition::new);
		handlers.registerHandler("OpPkcount", OpPkcountSkillCondition::new);
		handlers.registerHandler("OpPledge", OpPledgeSkillCondition::new);
		handlers.registerHandler("OpResurrection", OpResurrectionSkillCondition::new);
		handlers.registerHandler("OpSkillAcquire", OpSkillAcquireSkillCondition::new);
		handlers.registerHandler("OpSoulMax", OpSoulMaxSkillCondition::new);
		handlers.registerHandler("OpSweeper", OpSweeperSkillCondition::new);
		handlers.registerHandler("OpTargetDoor", OpTargetDoorSkillCondition::new);
		handlers.registerHandler("OpTargetNpc", OpTargetNpcSkillCondition::new);
		handlers.registerHandler("OpUnlock", OpUnlockSkillCondition::new);
		handlers.registerHandler("OpWyvern", OpWyvernSkillCondition::new);
		handlers.registerHandler("PossessHolything", PossessHolythingSkillCondition::new);
		handlers.registerHandler("RemainCpPer", RemainCpPerSkillCondition::new);
		handlers.registerHandler("RemainHpPer", RemainHpPerSkillCondition::new);
		handlers.registerHandler("RemainMpPer", RemainMpPerSkillCondition::new);
		handlers.registerHandler("SoulSaved", SoulSavedSkillCondition::new);
		handlers.registerHandler("TargetMyParty", TargetMyPartySkillCondition::new);
		handlers.registerHandler("TargetMyPledge", TargetMyPledgeSkillCondition::new);
		handlers.registerHandler("TargetRace", TargetRaceSkillCondition::new);
	}
}
