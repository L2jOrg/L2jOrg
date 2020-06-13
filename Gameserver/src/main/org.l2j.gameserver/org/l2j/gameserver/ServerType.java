/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
	BROAD,
	RESTRICTED,
	EVENT,
	FREE,
	UNK_7,
	WORLD,
	NEW,
	CLASSIC,
	ARENA,
	BLOODY;

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