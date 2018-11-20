/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package org.l2j.commons;


/**
 * This class used to be the starter class, since LS/GS split, it only retains server mode
 */
public class Server
{
	// constants for the server mode
	public static final int MODE_NONE = 0;
	public static final int MODE_GAMESERVER = 1;
	public static final int MODE_LOGINSERVER = 2;
	
	public static int serverMode = MODE_NONE;
	
}
