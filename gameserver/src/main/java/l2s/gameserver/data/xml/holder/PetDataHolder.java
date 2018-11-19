package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.pet.PetData;

/**
 * @author Bonux
 */
public final class PetDataHolder extends AbstractHolder
{
	public final static int PET_WOLF_ID = 12077;

	public final static int HATCHLING_WIND_ID = 12311;
	public final static int HATCHLING_STAR_ID = 12312;
	public final static int HATCHLING_TWILIGHT_ID = 12313;

	public final static int STRIDER_WIND_ID = 12526;
	public final static int STRIDER_STAR_ID = 12527;
	public final static int STRIDER_TWILIGHT_ID = 12528;

	public final static int RED_STRIDER_WIND_ID = 16038;
	public final static int RED_STRIDER_STAR_ID = 16039;
	public final static int RED_STRIDER_TWILIGHT_ID = 16040;

	public final static int WYVERN_ID = 12621;

	public final static int BABY_BUFFALO_ID = 12780;
	public final static int BABY_KOOKABURRA_ID = 12781;
	public final static int BABY_COUGAR_ID = 12782;

	public final static int IMPROVED_BABY_BUFFALO_ID = 16034;
	public final static int IMPROVED_BABY_KOOKABURRA_ID = 16035;
	public final static int IMPROVED_BABY_COUGAR_ID = 16036;

	public final static int SIN_EATER_ID = 12564;

	public final static int GREAT_WOLF_ID = 16025;
	public final static int WGREAT_WOLF_ID = 16037;
	public final static int FENRIR_WOLF_ID = 16041;
	public final static int WFENRIR_WOLF_ID = 16042;

	public final static int FOX_SHAMAN_ID = 16043;
	public final static int WILD_BEAST_FIGHTER_ID = 16044;
	public final static int WHITE_WEASEL_ID = 16045;
	public final static int FAIRY_PRINCESS_ID = 16046;
	public final static int OWL_MONK_ID = 16050;
	public final static int SPIRIT_SHAMAN_ID = 16051;
	public final static int TOY_KNIGHT_ID = 16052;
	public final static int TURTLE_ASCETIC_ID = 16053;
	public final static int DEINONYCHUS_ID = 16067;
	public final static int GUARDIANS_STRIDER_ID = 16068;
	public static final int ROSE_DESELOPH_ID = 1562;
	public static final int ROSE_HYUM_ID = 1563;
	public static final int ROSE_REKANG_ID = 1564;
	public static final int ROSE_LILIAS_ID = 1565;
	public static final int ROSE_LAPHAM_ID = 1566;
	public static final int ROSE_MAPHUM_ID = 1567;
	public static final int IMPROVED_ROSE_DESELOPH_ID = 1568;
	public static final int IMPROVED_ROSE_HYUM_ID = 1569;
	public static final int IMPROVED_ROSE_REKANG_ID = 1570;
	public static final int IMPROVED_ROSE_LILIAS_ID = 1571;
	public static final int IMPROVED_ROSE_LAPHAM_ID = 1572;
	public static final int IMPROVED_ROSE_MAPHUM_ID = 1573;

	private static final PetDataHolder _instance = new PetDataHolder();

	private static final TIntObjectMap<PetData> _templatesByNpcId = new TIntObjectHashMap<PetData>();
	private static final TIntObjectMap<PetData> _templatesByItemId = new TIntObjectHashMap<PetData>();

	public static PetDataHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(PetData template)
	{
		_templatesByNpcId.put(template.getNpcId(), template);
		_templatesByItemId.put(template.getControlItemId(), template);
	}

	public PetData getTemplateByNpcId(int npcId)
	{
		return _templatesByNpcId.get(npcId);
	}

	public PetData getTemplateByItemId(int itemId)
	{
		return _templatesByItemId.get(itemId);
	}

	public boolean isControlItem(int itemId)
	{
		return _templatesByItemId.containsKey(itemId);
	}

	@Override
	public int size()
	{
		return _templatesByNpcId.size();
	}

	@Override
	public void clear()
	{
		_templatesByNpcId.clear();
		_templatesByItemId.clear();
	}

	public static boolean isWolf(int id)
	{
		return id == PET_WOLF_ID;
	}

	public static boolean isGreatWolf(int id)
	{
		switch(id)
		{
			case GREAT_WOLF_ID:
			case WGREAT_WOLF_ID:
			case FENRIR_WOLF_ID:
			case WFENRIR_WOLF_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isHatchling(int id)
	{
		switch(id)
		{
			case HATCHLING_WIND_ID:
			case HATCHLING_STAR_ID:
			case HATCHLING_TWILIGHT_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isStrider(int id)
	{
		switch(id)
		{
			case STRIDER_WIND_ID:
			case STRIDER_STAR_ID:
			case STRIDER_TWILIGHT_ID:
			case RED_STRIDER_WIND_ID:
			case RED_STRIDER_STAR_ID:
			case RED_STRIDER_TWILIGHT_ID:
			case GUARDIANS_STRIDER_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isBabyPet(int id)
	{
		switch(id)
		{
			case BABY_BUFFALO_ID:
			case BABY_KOOKABURRA_ID:
			case BABY_COUGAR_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isImprovedBabyPet(int id)
	{
		switch(id)
		{
			case IMPROVED_BABY_BUFFALO_ID:
			case IMPROVED_BABY_KOOKABURRA_ID:
			case IMPROVED_BABY_COUGAR_ID:
				return true;
			default:
				return false;
		}
	}

	public static boolean isSpecialPet(int id)
	{
		switch(id)
		{
			case ROSE_DESELOPH_ID:
			case ROSE_HYUM_ID:
			case ROSE_REKANG_ID:
			case ROSE_LILIAS_ID:
			case ROSE_LAPHAM_ID:
			case ROSE_MAPHUM_ID:
			case IMPROVED_ROSE_DESELOPH_ID:
			case IMPROVED_ROSE_HYUM_ID:
			case IMPROVED_ROSE_REKANG_ID:
			case IMPROVED_ROSE_LILIAS_ID:
			case IMPROVED_ROSE_LAPHAM_ID:
			case IMPROVED_ROSE_MAPHUM_ID:
			case FOX_SHAMAN_ID:
			case WILD_BEAST_FIGHTER_ID:
			case WHITE_WEASEL_ID:
			case FAIRY_PRINCESS_ID:
			case OWL_MONK_ID:
			case SPIRIT_SHAMAN_ID:
			case TOY_KNIGHT_ID:
			case TURTLE_ASCETIC_ID:
				return true;
			default:
				return false;
		}
	}
}
