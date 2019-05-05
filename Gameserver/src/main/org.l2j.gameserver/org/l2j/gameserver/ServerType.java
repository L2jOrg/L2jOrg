package org.l2j.gameserver;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author VISTALL
 *
 * 21:12/28.06.2011
 */
public enum ServerType
{
	NORMAL,
	RELAX,
	TEST,
	UNK_3,
	RESTRICTED,
	EVENT,
	FREE,
	UNK_7,
	UNK_8,
	NEW,
	CLASSIC;

	private int mask;

	ServerType()
	{
		mask = 1 << ordinal();
	}

	public int getMask() {
		return mask;
	}

	public static int maskOf(String... types) {
		var type = 0;
		for (String t : types) {
			if(isNullOrEmpty(t)){
				continue;
			}
			try {
				type |= ServerType.valueOf(t.trim().toUpperCase()).mask;
			} catch (Exception ignored) {
			}
		}
		return type;
	}

}