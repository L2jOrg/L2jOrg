package l2s.gameserver.templates.item;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.handler.items.ItemHandler;
import l2s.gameserver.skills.SkillTrait;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.templates.StatsSet;

public final class WeaponTemplate extends ItemTemplate
{
	private final int _soulShotCount;
	private final int _spiritShotCount;
	private final int _kamaelConvert;
	private final int _rndDam;
	private final int _atkReuse;
	private final int _mpConsume;
	private final int _atkRange;
	private final int _attackRadius;
	private final int _attackAngle;
	private final boolean _isMagicWeapon;
	private int _critical;
	private final int[] _reducedSoulshot;
	private final int[] _reducedSpiritshot;
	private final int[] _reducedMPConsume;
	private TIntSet _availableEnsouls = null;

	public enum WeaponType implements ItemType
	{
		NONE("Shield", SkillTrait.NONE),
		SWORD("Sword", SkillTrait.SWORD),
		BLUNT("Blunt", SkillTrait.BLUNT),
		DAGGER("Dagger", SkillTrait.DAGGER),
		BOW("Bow", SkillTrait.BOW),
		POLE("Pole", SkillTrait.POLE),
		ETC("Etc", SkillTrait.ETC),
		FIST("Fist", SkillTrait.FIST),
		DUAL("Dual Sword", SkillTrait.DUAL),
		DUALFIST("Dual Fist", SkillTrait.DUALFIST),
		BIGSWORD("Big Sword", SkillTrait.SWORD), // Two Handed Swords
		PET("Pet", SkillTrait.FIST),
		ROD("Rod", SkillTrait.NONE),
		BIGBLUNT("Big Blunt", SkillTrait.BLUNT),
		CROSSBOW("Crossbow", SkillTrait.CROSSBOW),
		RAPIER("Rapier", SkillTrait.RAPIER),
		ANCIENTSWORD("Ancient Sword", SkillTrait.ANCIENTSWORD), // Kamael 2h sword
		DUALDAGGER("Dual Dagger", SkillTrait.DUALDAGGER),
		TWOHANDCROSSBOW("Two Hand Crossbow", SkillTrait.TWOHANDCROSSBOW),
		DUALBLUNT("Dual Blunt", SkillTrait.DUALBLUNT),
		MAGIC("Magic", null);

		public final static WeaponType[] VALUES = values();

		private final long _mask;
		private final String _name;
		private final SkillTrait _trait;

		private WeaponType(String name, SkillTrait trait)
		{
			_mask = 1L << (ordinal() + 1000);
			_name = name;
			_trait = trait;
		}

		public long mask()
		{
			return _mask;
		}

		public IItemHandler getHandler()
		{
			return ItemHandler.EQUIPABLE_HANDLER;
		}

		public SkillTrait getTrait()
		{
			return _trait;
		}

		public ExItemType getExType()
		{
			return null;
		}

		@Override
		public String toString()
		{
			return _name;
		}
	}

	public WeaponTemplate(StatsSet set)
	{
		super(set);
		_type = set.getEnum("type", WeaponType.class);
		_soulShotCount = set.getInteger("soulshots", 0);
		_spiritShotCount = set.getInteger("spiritshots", 0);
		_kamaelConvert = set.getInteger("kamael_convert", 0);

		_rndDam = set.getInteger("rnd_dam", 0);
		_atkReuse = set.getInteger("atk_reuse", _type == WeaponType.BOW ? 1500 : (_type == WeaponType.CROSSBOW || _type == WeaponType.TWOHANDCROSSBOW) ? 820 : 0);
		_atkRange = set.getInteger("atk_range", getDefaultAttackRange((WeaponType) _type));

		String[] damageRange = set.getString("damage_range", "").split(";");
		if(damageRange.length >= 4)
		{
			_attackRadius = Integer.parseInt(damageRange[2]);
			_attackAngle = Integer.parseInt(damageRange[3]);
		}
		else if(_type == WeaponType.BOW)
		{
			_attackRadius = 10;
			_attackAngle = 0;
		}
		else if(_type == WeaponType.POLE)
		{
			_attackRadius = 66;
			_attackAngle = 120;
		}
		else
		{
			_attackRadius = 40;
			_attackAngle = 120;
		}

		_mpConsume = set.getInteger("mp_consume", 0);

		_isMagicWeapon = set.getBool("is_magic_weapon", false);

		_reducedSoulshot = set.getIntegerArray("reduced_soulshot", new int[] { 0, _soulShotCount });
		_reducedSpiritshot = set.getIntegerArray("reduced_spiritshot", new int[] { 0, _spiritShotCount });
		_reducedMPConsume = set.getIntegerArray("reduced_mp_consume", new int[] { 0, _mpConsume });

		int[] availableEnsouls = set.getIntegerArray("available_ensouls", new int[0]);
		if (availableEnsouls.length > 0)
			_availableEnsouls = new TIntHashSet(availableEnsouls);

		if(_type == WeaponType.NONE)
		{
			_type1 = TYPE1_SHIELD_ARMOR;
			_type2 = TYPE2_SHIELD_ARMOR;
		}
		else
		{
			_type1 = TYPE1_WEAPON_RING_EARRING_NECKLACE;
			_type2 = TYPE2_WEAPON;
		}

		if(_type == WeaponType.SWORD && !_isMagicWeapon)
			_exType = ExItemType.SWORD;
		else if(_type == WeaponType.SWORD && _isMagicWeapon)
			_exType = ExItemType.MAGIC_SWORD;
		else if(_type == WeaponType.DAGGER)
			_exType = ExItemType.DAGGER;
		else if(_type == WeaponType.RAPIER)
			_exType = ExItemType.RAPIER;
		else if(_type == WeaponType.BIGSWORD)
			_exType = ExItemType.BIG_SWORD;
		else if(_type == WeaponType.ANCIENTSWORD)
			_exType = ExItemType.ANCIENT_SWORD;
		else if(_type == WeaponType.DUAL)
			_exType = ExItemType.DUAL_SWORD;
		else if(_type == WeaponType.DUALDAGGER)
			_exType = ExItemType.DUAL_DAGGER;
		else if(_type == WeaponType.BLUNT && !_isMagicWeapon)
			_exType = ExItemType.BLUNT_WEAPON;
		else if(_type == WeaponType.BLUNT && _isMagicWeapon)
			_exType = ExItemType.MAGIC_BLUNT_WEAPON;
		else if(_type == WeaponType.BIGBLUNT && !_isMagicWeapon)
			_exType = ExItemType.BIG_BLUNT_WEAPON;
		else if(_type == WeaponType.BIGBLUNT && _isMagicWeapon)
			_exType = ExItemType.BIG_MAGIC_BLUNT_WEAPON;
		else if(_type == WeaponType.DUALBLUNT)
			_exType = ExItemType.DUAL_BLUNT_WEAPON;
		else if(_type == WeaponType.BOW)
			_exType = ExItemType.BOW;
		else if(_type == WeaponType.CROSSBOW || _type == WeaponType.TWOHANDCROSSBOW)
			_exType = ExItemType.CROSSBOW;
		else if(_type == WeaponType.DUALFIST)
			_exType = ExItemType.HAND_TO_HAND;
		else if(_type == WeaponType.POLE)
			_exType = ExItemType.POLE;
		else if(_type == WeaponType.ETC || _type == WeaponType.ROD)
			_exType = ExItemType.OTHER_WEAPON;
		else if(_bodyPart == ItemTemplate.SLOT_L_HAND && _type == WeaponType.NONE)
			_exType = ExItemType.SHIELD;

		initEnchantFuncs();
	}

	@Override
	public IItemHandler getHandler()
	{
		return ItemHandler.EQUIPABLE_HANDLER;
	}

	/**
	 * Returns the type of Weapon
	 * @return L2WeaponType
	 */
	@Override
	public WeaponType getItemType()
	{
		return (WeaponType) super.getItemType();
	}

	/**
	 * Returns the ID of the Etc item after applying the mask.
	 * @return int : ID of the Weapon
	 */
	@Override
	public long getItemMask()
	{
		return getItemType().mask();
	}

	/**
	 * Returns the quantity of SoulShot used.
	 * @return int
	 */
	public int getSoulShotCount()
	{
		return _soulShotCount;
	}

	/**
	 * Returns the quatity of SpiritShot used.
	 * @return int
	 */
	public int getSpiritShotCount()
	{
		return _spiritShotCount;
	}

	public int getCritical()
	{
		return _critical;
	}

	/**
	 * Returns the random damage inflicted by the weapon
	 * @return int
	 */
	public int getRandomDamage()
	{
		return _rndDam;
	}

	/**
	 * Return the Attack Reuse Delay of the L2Weapon.<BR><BR>
	 * @return int
	 */
	public int getAttackReuseDelay()
	{
		return _atkReuse;
	}

	/**
	 * Returns the MP consumption with the weapon
	 * @return int
	 */
	public int getMpConsume()
	{
		return _mpConsume;
	}

	public int getAttackRange()
	{
		return _atkRange;
	}

	public int getAttackRadius()
	{
		return _attackRadius;
	}

	public int getAttackAngle()
	{
		return _attackAngle;
	}

	/**
	 * Возвращает разницу между длиной этого оружия и стандартной, то есть x-40
	 */
	public static int getDefaultAttackRange(WeaponType type)
	{
		switch(type)
		{
			case BOW:
				return 460;
			case CROSSBOW:
			case TWOHANDCROSSBOW:
				return 360;
			case POLE:
				return 80;
			default:
				return 0;
		}
	}

	@Override
	public void attachFunc(FuncTemplate f)
	{
		//TODO для параметров set с дп,может считать стат с L2ItemInstance? (VISTALL)
		if(f._stat == Stats.BASE_P_CRITICAL_RATE && f._order == 0x08)
		{
			_critical = (int) Math.round(f._value / 10);
		}
		super.attachFunc(f);
	}

	public int getKamaelConvert()
	{
		return _kamaelConvert;
	}

	@Override
	public boolean isMagicWeapon()
	{
		return _isMagicWeapon;
	}

	public int[] getReducedSoulshot()
	{
		return _reducedSoulshot;
	}

	public int[] getReducedSpiritshot()
	{
		return _reducedSpiritshot;
	}

	public int[] getReducedMPConsume()
	{
		return _reducedMPConsume;
	}

	@Override
	public WeaponFightType getWeaponFightType()
	{
		if(_isMagicWeapon)
			return WeaponFightType.MAGE;
		return WeaponFightType.WARRIOR;
	}

	@Override
	public boolean canBeEnsoul(int ensoulId)
	{
		if(!isWeapon())
			return false;

		if(_availableEnsouls == null)
		{
			if(getGrade().ordinal() < ItemGrade.D.ordinal())
				return false;
		}
		else if(!_availableEnsouls.contains(ensoulId))
			return false;

		return isEnsoulable();
	}
}