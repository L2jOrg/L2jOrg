package l2s.gameserver.model.actor.basestats;

import l2s.gameserver.model.Creature;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

/**
 * @author Bonux
**/
public class CreatureBaseStats
{
	protected final Creature _owner;

	public CreatureBaseStats(Creature owner)
	{
		_owner = owner;
	}

	public Creature getOwner()
	{
		return _owner;
	}

	public int getINT()
	{
		return getOwner().getTemplate().getBaseINT();
	}

	public int getSTR()
	{
		return getOwner().getTemplate().getBaseSTR();
	}

	public int getCON()
	{
		return getOwner().getTemplate().getBaseCON();
	}

	public int getMEN()
	{
		return getOwner().getTemplate().getBaseMEN();
	}

	public int getDEX()
	{
		return getOwner().getTemplate().getBaseDEX();
	}

	public int getWIT()
	{
		return getOwner().getTemplate().getBaseWIT();
	}

	public double getHpMax()
	{
		return getOwner().getTemplate().getBaseHpMax(getOwner().getLevel());
	}

	public double getMpMax()
	{
		return getOwner().getTemplate().getBaseMpMax(getOwner().getLevel());
	}

	public double getCpMax()
	{
		return getOwner().getTemplate().getBaseCpMax(getOwner().getLevel());
	}

	public double getHpReg()
	{
		return getOwner().getTemplate().getBaseHpReg(getOwner().getLevel());
	}

	public double getMpReg()
	{
		return getOwner().getTemplate().getBaseMpReg(getOwner().getLevel());
	}

	public double getCpReg()
	{
		return getOwner().getTemplate().getBaseCpReg(getOwner().getLevel());
	}

	public double getPAtk()
	{
		return getOwner().getTemplate().getBasePAtk();
	}

	public double getMAtk()
	{
		return getOwner().getTemplate().getBaseMAtk();
	}

	public double getPDef()
	{
		return getOwner().getTemplate().getBasePDef();
	}

	public double getMDef()
	{
		return getOwner().getTemplate().getBaseMDef();
	}

	public double getPAtkSpd()
	{
		return getOwner().getTemplate().getBasePAtkSpd();
	}

	public double getMAtkSpd()
	{
		return getOwner().getTemplate().getBaseMAtkSpd();
	}

	public double getShldDef()
	{
		return getOwner().getTemplate().getBaseShldDef();
	}

	public int getAtkRange()
	{
		return getOwner().getTemplate().getBaseAtkRange();
	}

	public int getAttackRadius()
	{
		return getOwner().getTemplate().getBaseAttackRadius();
	}

	public int getAttackAngle()
	{
		return getOwner().getTemplate().getBaseAttackAngle();
	}

	public double getShldRate()
	{
		return getOwner().getTemplate().getBaseShldRate();
	}

	public double getPCritRate()
	{
		return getOwner().getTemplate().getBasePCritRate();
	}

	public double getMCritRate()
	{
		return getOwner().getTemplate().getBaseMCritRate();
	}

	public double getRunSpd()
	{
		return getOwner().getTemplate().getBaseRunSpd();
	}

	public double getWalkSpd()
	{
		return getOwner().getTemplate().getBaseWalkSpd();
	}

	public double getWaterRunSpd()
	{
		return getOwner().getTemplate().getBaseWaterRunSpd();
	}

	public double getWaterWalkSpd()
	{
		return getOwner().getTemplate().getBaseWaterWalkSpd();
	}

	public int[] getAttributeAttack()
	{
		return getOwner().getTemplate().getBaseAttributeAttack();
	}

	public int[] getAttributeDefence()
	{
		return getOwner().getTemplate().getBaseAttributeDefence();
	}

	public double getCollisionRadius()
	{
		if(getOwner().isVisualTransformed() && getOwner().getVisualTransform().getCollisionRadius() > 0)
			return getOwner().getVisualTransform().getCollisionRadius();
		return getOwner().getTemplate().getCollisionRadius();
	}

	public double getCollisionHeight()
	{
		if(getOwner().isVisualTransformed() && getOwner().getVisualTransform().getCollisionHeight() > 0)
			return getOwner().getVisualTransform().getCollisionHeight();
		return getOwner().getTemplate().getCollisionHeight();
	}

	public WeaponType getAttackType()
	{
		if(getOwner().getActiveWeaponTemplate() != null)
			return getOwner().getActiveWeaponTemplate().getItemType();

		return getOwner().getTemplate().getBaseAttackType();
	}

	public int getPhysicalAbnormalResist()
	{
		return getOwner().getTemplate().getPhysicalAbnormalResist();
	}

	public int getMagicAbnormalResist()
	{
		return getOwner().getTemplate().getMagicAbnormalResist();
	}

	public int getRandDam()
	{
		return getOwner().getTemplate().getBaseRandDam();
	}
}