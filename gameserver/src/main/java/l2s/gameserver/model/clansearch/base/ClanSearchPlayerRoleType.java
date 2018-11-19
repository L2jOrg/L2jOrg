package l2s.gameserver.model.clansearch.base;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public enum ClanSearchPlayerRoleType
{
	/*0*/ANY(new int[0]),
	/*1*/FIGHTER(new int[] { 0, 1, 4, 7, 18, 19, 22, 31, 35, 44, 45, 47, 53, 54, 56, 123, 124, 125, 126 }),
	/*2*/MYSTIC(new int[] { 49, 50, 38, 39, 42, 25, 26, 29, 10, 11, 15 }),
	/*3*/MELEE_FIGHTER(new int[] { 2, 3, 46, 48, 57, 127, 128, 129, 88, 89, 113, 114, 118, 131, 132, 133, 140, 152, 153, 154, 155, 156, 157 }),
	/*4*/MELEE_FIGHTER2(new int[] { 8, 23, 36, 55, 93, 101, 108, 117, 141, 158, 159, 160, 161 }),
	/*5*/RANGE_FIGHTER(new int[] { 9, 24, 37, 130, 92, 102, 134, 109, 142, 162, 163, 164, 165 }),
	/*6*/DEFENSE_FIGHTER(new int[] { 5, 6, 20, 33, 90, 91, 99, 106, 139, 148, 149, 150, 151 }),
	/*7*/SUPPORT_FIGHTER(new int[] { 21, 34, 135, 100, 107, 136, 144, 171, 172, 173, 174, 175 }),
	/*8*/MAGICIAN(new int[] { 12, 13, 27, 40, 94, 95, 103, 110, 143, 166, 167, 168, 169, 170 }),
	/*9*/HEALER(new int[] { 16, 17, 30, 43, 52, 51, 97, 98, 105, 112, 116, 115, 146, 179, 180, 181 }),
	/*10*/SUMMONER(new int[] { 14, 28, 41, 96, 104, 111, 145, 176, 177, 178 });

	public static final ClanSearchPlayerRoleType[] VALUES = values();

	private final int[] _classIds;

	private ClanSearchPlayerRoleType(int[] classIds)
	{
		_classIds = classIds;
	}

	public static ClanSearchPlayerRoleType valueOf(int value)
	{
		if(value >= VALUES.length)
			return ANY;

		return VALUES[value];
	}

	public boolean isClassRole(int classId)
	{
		if(this == ANY)
			return true;

		for(int roleClassId : _classIds)
		{
			if(roleClassId == classId)
				return true;
		}
		return false;
	}
}