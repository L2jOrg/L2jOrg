package l2s.gameserver.model.actor.basestats;

import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;

/**
 * @author Bonux
**/
public class PlayerBaseStats extends PlayableBaseStats
{
	public PlayerBaseStats(Player owner)
	{
		super(owner);
	}

	@Override
	public Player getOwner()
	{
		return (Player) _owner;
	}

	@Override
	public int getINT()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseINT() > 0)
			return getOwner().getTransform().getBaseINT();

		return super.getINT();
	}

	@Override
	public int getSTR()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseSTR() > 0)
			return getOwner().getTransform().getBaseSTR();

		return super.getSTR();
	}

	@Override
	public int getCON()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseCON() > 0)
			return getOwner().getTransform().getBaseCON();

		return super.getCON();
	}

	@Override
	public int getMEN()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMEN() > 0)
			return getOwner().getTransform().getBaseMEN();

		return super.getMEN();
	}

	@Override
	public int getDEX()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseDEX() > 0)
			return getOwner().getTransform().getBaseDEX();

		return super.getDEX();
	}

	@Override
	public int getWIT()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseWIT() > 0)
			return getOwner().getTransform().getBaseWIT();

		return super.getWIT();
	}

	@Override
	public double getHpMax()
	{
		double maxHp;
		if(getOwner().isMounted() && getOwner().getMount().getMaxHpOnRide() > 0)
			maxHp = getOwner().getMount().getMaxHpOnRide();
		else if(getOwner().isTransformed() && getOwner().getTransform().getBaseHpMax(getOwner().getLevel()) > 0)
			maxHp = getOwner().getTransform().getBaseHpMax(getOwner().getLevel());
		else
			maxHp = getOwner().getClassId().getBaseHp(getOwner().getLevel());

		return maxHp;
	}

	@Override
	public double getMpMax()
	{
		double maxMp;
		if(getOwner().isMounted() && getOwner().getMount().getMaxMpOnRide() > 0)
			maxMp = getOwner().getMount().getMaxMpOnRide();
		else if(getOwner().isTransformed() && getOwner().getTransform().getBaseMpMax(getOwner().getLevel()) > 0)
			maxMp = getOwner().getTransform().getBaseMpMax(getOwner().getLevel());
		else
			maxMp = getOwner().getClassId().getBaseMp(getOwner().getLevel());

		return maxMp;
	}

	@Override
	public double getCpMax()
	{
		double maxCp;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseCpMax(getOwner().getLevel()) > 0)
			maxCp = getOwner().getTransform().getBaseCpMax(getOwner().getLevel());
		else
			maxCp = getOwner().getClassId().getBaseCp(getOwner().getLevel());

		return maxCp;
	}

	@Override
	public double getHpReg()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseHpReg(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseHpReg(getOwner().getLevel());

		return super.getHpReg();
	}

	@Override
	public double getMpReg()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMpReg(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseMpReg(getOwner().getLevel());

		return super.getMpReg();
	}

	@Override
	public double getCpReg()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseCpReg(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseCpReg(getOwner().getLevel());

		return super.getCpReg();
	}

	@Override
	public double getPAtk()
	{
		double pAtk;
		if(getOwner().isMounted() && getOwner().getMount().getPAtkOnRide() > 0)
			pAtk = getOwner().getMount().getPAtkOnRide();
		else if(getOwner().isTransformed() && getOwner().getTransform().getBasePAtk() > 0)
			pAtk = getOwner().getTransform().getBasePAtk();
		else
			pAtk = super.getPAtk();

		ItemTemplate weaponTemplate = getOwner().getActiveWeaponTemplate();
		if(weaponTemplate != null)
			pAtk = Math.max(pAtk, weaponTemplate.getPAtk());

		return pAtk;
	}

	@Override
	public double getMAtk()
	{
		double mAtk;
		if(getOwner().isMounted() && getOwner().getMount().getMAtkOnRide() > 0)
			mAtk = getOwner().getMount().getMAtkOnRide();
		else if(getOwner().isTransformed() && getOwner().getTransform().getBaseMAtk() > 0)
			mAtk = getOwner().getTransform().getBaseMAtk();
		else
			mAtk = super.getMAtk();

		ItemTemplate weaponTemplate = getOwner().getActiveWeaponTemplate();
		if(weaponTemplate != null)
			mAtk = Math.max(mAtk, weaponTemplate.getMAtk());

		return mAtk;
	}

	@Override
	public double getPDef()
	{
		double result = 0.;

		double chestPDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseChestDef() > 0)
			chestPDef = getOwner().getTransform().getBaseChestDef();
		else
			chestPDef = getOwner().getTemplate().getBaseChestDef();

		double legsPDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseLegsDef() > 0)
			legsPDef = getOwner().getTransform().getBaseLegsDef();
		else
			legsPDef = getOwner().getTemplate().getBaseLegsDef();

		ItemInstance tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if(tempItem != null)
		{
			if(tempItem.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
			{
				chestPDef = Math.max(chestPDef + legsPDef, tempItem.getTemplate().getPDef());
				legsPDef = 0.;
			}
			else
				chestPDef = Math.max(chestPDef, tempItem.getTemplate().getPDef());
		}

		result += chestPDef;

		if(tempItem == null || tempItem.getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR)
		{
			tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			if(tempItem != null)
				legsPDef = Math.max(legsPDef, tempItem.getTemplate().getPDef());
		}

		result += legsPDef;

		double helmetPDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseHelmetDef() > 0)
			helmetPDef = getOwner().getTransform().getBaseHelmetDef();
		else
			helmetPDef = getOwner().getTemplate().getBaseHelmetDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
		if(tempItem != null)
			helmetPDef = Math.max(helmetPDef, tempItem.getTemplate().getPDef());

		result += helmetPDef;

		double glovesPDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseGlovesDef() > 0)
			glovesPDef = getOwner().getTransform().getBaseGlovesDef();
		else
			glovesPDef = getOwner().getTemplate().getBaseGlovesDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
		if(tempItem != null)
			glovesPDef = Math.max(glovesPDef, tempItem.getTemplate().getPDef());

		result += glovesPDef;

		double bootsPDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseBootsDef() > 0)
			bootsPDef = getOwner().getTransform().getBaseBootsDef();
		else
			bootsPDef = getOwner().getTemplate().getBaseBootsDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);
		if(tempItem != null)
			bootsPDef = Math.max(bootsPDef, tempItem.getTemplate().getPDef());

		result += bootsPDef;

		double pendantPDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBasePendantDef() > 0)
			pendantPDef = getOwner().getTransform().getBasePendantDef();
		else
			pendantPDef = getOwner().getTemplate().getBasePendantDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_PENDANT);
		if(tempItem != null)
			pendantPDef = Math.max(pendantPDef, tempItem.getTemplate().getPDef());

		result += pendantPDef;

		double cloakPDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseCloakDef() > 0)
			cloakPDef = getOwner().getTransform().getBaseCloakDef();
		else
			cloakPDef = getOwner().getTemplate().getBaseCloakDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_BACK);
		if(tempItem != null)
			cloakPDef = Math.max(cloakPDef, tempItem.getTemplate().getPDef());

		result += cloakPDef;

		return result;
	}

	@Override
	public double getMDef()
	{
		double result = 0.;

		double lEarMDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseLEarDef() > 0)
			lEarMDef = getOwner().getTransform().getBaseLEarDef();
		else
			lEarMDef = getOwner().getTemplate().getBaseLEarDef();
		
		ItemInstance tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
		if(tempItem != null)
			lEarMDef = Math.max(lEarMDef, tempItem.getTemplate().getMDef());

		result += lEarMDef;

		double rEarMDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseREarDef() > 0)
			rEarMDef = getOwner().getTransform().getBaseREarDef();
		else
			rEarMDef = getOwner().getTemplate().getBaseREarDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);
		if(tempItem != null)
			rEarMDef = Math.max(rEarMDef, tempItem.getTemplate().getMDef());

		result += rEarMDef;

		double necklaceMDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseNecklaceDef() > 0)
			necklaceMDef = getOwner().getTransform().getBaseNecklaceDef();
		else
			necklaceMDef = getOwner().getTemplate().getBaseNecklaceDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
		if(tempItem != null)
			necklaceMDef = Math.max(necklaceMDef, tempItem.getTemplate().getMDef());

		result += necklaceMDef;

		double lRingMDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseLRingDef() > 0)
			lRingMDef = getOwner().getTransform().getBaseLRingDef();
		else
			lRingMDef = getOwner().getTemplate().getBaseLRingDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
		if(tempItem != null)
			lRingMDef = Math.max(lRingMDef, tempItem.getTemplate().getMDef());

		result += lRingMDef;

		double rRingMDef;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRRingDef() > 0)
			rRingMDef = getOwner().getTransform().getBaseRRingDef();
		else
			rRingMDef = getOwner().getTemplate().getBaseRRingDef();
		
		tempItem = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
		if(tempItem != null)
			rRingMDef = Math.max(rRingMDef, tempItem.getTemplate().getMDef());

		result += rRingMDef;

		return result;
	}

	@Override
	public double getPAtkSpd()
	{
		double pAtkSpd;
		if(getOwner().isMounted() && getOwner().getMount().getAtkSpdOnRide() > 0)
			pAtkSpd = getOwner().calcStat(Stats.BASE_P_ATK_SPD, getOwner().getMount().getAtkSpdOnRide(), null, null);
		else if(getOwner().isTransformed() && getOwner().getTransform().getBasePAtkSpd() > 0)
			pAtkSpd = getOwner().calcStat(Stats.BASE_P_ATK_SPD, getOwner().getTransform().getBasePAtkSpd(), null, null);
		else
			pAtkSpd = super.getPAtkSpd();

		return pAtkSpd;
	}

	@Override
	public double getMAtkSpd()
	{
		double mAtkSpd;
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMAtkSpd() > 0)
			mAtkSpd = getOwner().getTransform().getBaseMAtkSpd();
		else
			mAtkSpd = super.getMAtkSpd();

		return mAtkSpd;
	}

	@Override
	public double getShldDef()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseShldDef() > 0)
			return getOwner().getTransform().getBaseShldDef();

		return super.getShldDef();
	}

	@Override
	public int getAtkRange()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseAtkRange() > 0)
			return getOwner().getTransform().getBaseAtkRange();

		WeaponTemplate weaponTemplate = getOwner().getActiveWeaponTemplate();
		if(weaponTemplate != null)
			return weaponTemplate.getAttackRange();

		return super.getAtkRange();
	}

	@Override
	public int getAttackRadius()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseAtkRange() > 0)
			return getOwner().getTransform().getBaseAttackRadius();

		WeaponTemplate weaponTemplate = getOwner().getActiveWeaponTemplate();
		if(weaponTemplate != null)
			return weaponTemplate.getAttackRadius();

		return super.getAttackRadius();
	}

	@Override
	public int getAttackAngle()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseAtkRange() > 0)
			return getOwner().getTransform().getBaseAttackAngle();

		WeaponTemplate weaponTemplate = getOwner().getActiveWeaponTemplate();
		if(weaponTemplate != null)
			return weaponTemplate.getAttackAngle();

		return super.getAttackAngle();
	}

	@Override
	public double getShldRate()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseShldRate() > 0)
			return getOwner().getTransform().getBaseShldRate();

		return 0.;
		//return super.getShldRate(); TODO: [Bonux] Check.
	}

	@Override
	public double getPCritRate()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBasePCritRate() > 0)
			return getOwner().getTransform().getBasePCritRate();

		return super.getPCritRate();
	}

	@Override
	public double getMCritRate()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMCritRate() > 0)
			return getOwner().getTransform().getBaseMCritRate();

		return super.getMCritRate();
	}

	@Override
	public double getRunSpd()
	{
		if(getOwner().isMounted() && getOwner().getMount().getRunSpdOnRide() > 0)
			return getOwner().getMount().getRunSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRunSpd() > 0)
			return getOwner().getTransform().getBaseRunSpd();

		return super.getRunSpd();
	}

	@Override
	public double getWalkSpd()
	{
		if(getOwner().isMounted() && getOwner().getMount().getWalkSpdOnRide() > 0)
			return getOwner().getMount().getWalkSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseWalkSpd() > 0)
			return getOwner().getTransform().getBaseWalkSpd();

		return super.getWalkSpd();
	}

	@Override
	public double getWaterRunSpd()
	{
		if(getOwner().isMounted() && getOwner().getMount().getWaterRunSpdOnRide() > 0)
			return getOwner().getMount().getWaterRunSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseWaterRunSpd() > 0)
			return getOwner().getTransform().getBaseWaterRunSpd();

		return super.getWaterRunSpd();
	}

	@Override
	public double getWaterWalkSpd()
	{
		if(getOwner().isMounted() && getOwner().getMount().getWaterWalkSpdOnRide() > 0)
			return getOwner().getMount().getWaterWalkSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseWaterWalkSpd() > 0)
			return getOwner().getTransform().getBaseWaterWalkSpd();

		return super.getWaterWalkSpd();
	}

	public double getFlyRunSpd()
	{
		if(getOwner().isMounted() && getOwner().getMount().getWaterRunSpdOnRide() > 0)
			return getOwner().getMount().getWaterRunSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseFlyRunSpd() > 0)
			return getOwner().getTransform().getBaseFlyRunSpd();

		return getOwner().getTemplate().getBaseFlyRunSpd();
	}

	public double getFlyWalkSpd()
	{
		if(getOwner().isMounted() && getOwner().getMount().getFlyWalkSpdOnRide() > 0)
			return getOwner().getMount().getFlyWalkSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseFlyWalkSpd() > 0)
			return getOwner().getTransform().getBaseFlyWalkSpd();

		return getOwner().getTemplate().getBaseFlyWalkSpd();
	}

	public double getRideRunSpd()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRideRunSpd() > 0)
			return getOwner().getTransform().getBaseRideRunSpd();

		return getOwner().getTemplate().getBaseRideRunSpd();
	}

	public double getRideWalkSpd()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRideWalkSpd() > 0)
			return getOwner().getTransform().getBaseRideWalkSpd();

		return getOwner().getTemplate().getBaseRideWalkSpd();
	}

	@Override
	public double getCollisionRadius()
	{
		if(getOwner().isMounted())
		{
			final int mountTemplate = getOwner().getMountNpcId();
			if(mountTemplate != 0)
			{
				final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
				if(mountNpcTemplate != null)
					return mountNpcTemplate.getCollisionRadius();
			}
		}

		if(getOwner().isVisualTransformed() && getOwner().getVisualTransform().getCollisionRadius() > 0)
			return getOwner().getVisualTransform().getCollisionRadius();

		return getOwner().getBaseTemplate().getCollisionRadius();
	}

	@Override
	public double getCollisionHeight()
	{
		if(getOwner().isMounted())
		{
			final int mountTemplate = getOwner().getMountNpcId();
			if(mountTemplate != 0)
			{
				final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
				if(mountNpcTemplate != null)
					return mountNpcTemplate.getCollisionHeight();
			}
		}

		if(getOwner().isVisualTransformed() && getOwner().getVisualTransform().getCollisionHeight() > 0)
			return getOwner().getVisualTransform().getCollisionHeight();

		return getOwner().getBaseTemplate().getCollisionHeight();
	}

	@Override
	public WeaponType getAttackType()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseAttackType() != WeaponType.NONE)
			return getOwner().getTransform().getBaseAttackType();

		return super.getAttackType();
	}

	@Override
	public int getRandDam()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRandDam() > 0)
			return getOwner().getTransform().getBaseRandDam();

		return super.getRandDam();
	}

	public double getBreathBonus()
	{
		return getOwner().getTemplate().getBaseBreathBonus();
	}

	public double getSafeFallHeight()
	{
		return getOwner().getTemplate().getBaseSafeFallHeight();
	}
}