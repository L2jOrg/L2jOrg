package org.l2j.gameserver.engine.geoscripts;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.GeoUtils;
import org.l2j.gameserver.engine.geoscripts.utils.GeodataUtils;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.commons.geometry.GeometryUtils;
import org.l2j.commons.geometry.Point2D;
import org.l2j.commons.geometry.Point3D;
import org.l2j.commons.geometry.Shape;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.geoscripts.GeoOptimizer.BlockLink;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.Location;


import org.napile.primitive.pair.ByteObjectPair;
import org.napile.primitive.pair.impl.ByteObjectPairImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author:		Diamond
 * @CoAuthor:	DRiN, Bonux
 * @Date:			01/03/2009
 * TODO:
 * 1. Реализовать генерацию слоя 'крыши' GeoControl обьектов.
 * 2. Избавиться от всех short > int
 */
public class GeoEngine
{
	public static enum CeilGeoControlType
	{
		NONE,
		PERIMETER,
		INSIDE
	}

	private static final Logger _log = LoggerFactory.getLogger(GeoEngine.class);

	public static final String L2S_EXTENSION = new String(new byte[] { 0x2E, 0x6C, 0x32, 0x73 } );
	public static final String L2J_EXTENSION = new String(new byte[] { 0x2E, 0x6C, 0x32, 0x6A } );

	public static final byte EAST = 1, WEST = 2, SOUTH = 4, NORTH = 8, NSWE_ALL = 15, NSWE_NONE = 0;

	public static final byte BLOCKTYPE_FLAT = 0;
	public static final byte BLOCKTYPE_COMPLEX = 1;
	public static final byte BLOCKTYPE_MULTILEVEL = 2;

	public static final int BLOCKS_IN_MAP = 256 * 256;

	private static final int DOOR_MAX_Z_DIFF = 256;

	public static final int LINEAR_TERRITORY_CELL_SIZE = getWorldDistance(1);

	public static int MAX_LAYERS = 1; // меньше 1 быть не должно, что бы создавались временные массивы как минимум short[2]

	private static final TIntObjectMap<List<GeoControl>> _activeGeoControls = new TIntObjectHashMap<>();

	/**
	 * Даный массив содержит всю геодату на сервере. <BR>
	 * Первый [] (byte[][][*][]) является блоком геодаты.<BR>
	 * Второй и третий [][] (byte[*][*][][]) являются x и y региона.<BR>
	 * Четвертый [] (byte[][][][*]) является контейнером для всех блоков в регионе.<BR>
	 */
	private static byte[][][][][] geodata = new byte[9999][][][][];
	static {
		geodata[0] = new byte[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][][];
	}

	public static int getMapX(int x)
	{
		return ((x - World.MAP_MIN_X) >> 15) + Config.GEO_X_FIRST;
	}

	public static int getMapY(int y)
	{
		return ((y - World.MAP_MIN_Y) >> 15) + Config.GEO_Y_FIRST;
	}

	public static short getType(int x, int y, int geoIndex)
	{
		return NgetType(getGeoX(x), getGeoY(y), geoIndex);
	}

	public static int correctGeoZ(int x, int y, int z, int geoIndex)
	{
		int correctedZ = getLowerHeight(x, y, z, geoIndex);
		if(correctedZ == Short.MIN_VALUE)
		{
			correctedZ = getUpperHeight(x, y, z, geoIndex);
			if(correctedZ == Short.MAX_VALUE)
				correctedZ = z;
		}
		return correctedZ;
	}

	public static int getLowerHeight(Location loc, int geoIndex)
	{
		return getLowerHeight(loc.getX(), loc.getY(), loc.getZ(), geoIndex);
	}

	public static int getLowerHeight(int x, int y, int z, int geoIndex)
	{
		return NgetLowerHeight(getGeoX(x), getGeoY(y), (short) Math.min(z, Short.MAX_VALUE), geoIndex);
	}

	public static int getUpperHeight(Location loc, int geoIndex)
	{
		return getUpperHeight(loc.getX(), loc.getY(), loc.getZ(), geoIndex);
	}

	public static int getUpperHeight(int x, int y, int z, int geoIndex)
	{
		return NgetUpperHeight(getGeoX(x), getGeoY(y), (short) Math.min(z, Short.MAX_VALUE), geoIndex);
	}

	public static int getLowerNSWE(Location loc, int geoIndex)
	{
		return getLowerNSWE(loc.getX(), loc.getY(), loc.getZ(), geoIndex);
	}

	public static byte getLowerNSWE(int x, int y, int z, int geoIndex)
	{
		return NgetLowerNSWE(getGeoX(x), getGeoY(y), (short) Math.min(z, Short.MAX_VALUE), geoIndex);
	}

	public static int getUpperNSWE(Location loc, int geoIndex)
	{
		return getUpperNSWE(loc.getX(), loc.getY(), loc.getZ(), geoIndex);
	}

	public static byte getUpperNSWE(int x, int y, int z, int geoIndex)
	{
		return NgetUpperNSWE(getGeoX(x), getGeoY(y), (short) Math.min(z, Short.MAX_VALUE), geoIndex);
	}

	public static short[] getLowerHeightAndNSWE(Location loc, int geoIndex)
	{
		return getLowerHeightAndNSWE(loc.getX(), loc.getY(), loc.getZ(), geoIndex);
	}

	public static short[] getLowerHeightAndNSWE(int x, int y, int z, int geoIndex)
	{
		short[] result = new short[2];
		NgetLowerHeightAndNSWE(getGeoX(x), getGeoY(y), (short) Math.min(z, Short.MAX_VALUE), result, geoIndex);
		return result;
	}

	public static short[] getUpperHeightAndNSWE(Location loc, int geoIndex)
	{
		return getUpperHeightAndNSWE(loc.getX(), loc.getY(), loc.getZ(), geoIndex);
	}

	public static short[] getUpperHeightAndNSWE(int x, int y, int z, int geoIndex)
	{
		short[] result = new short[2];
		NgetUpperHeightAndNSWE(getGeoX(x), getGeoY(y), (short) Math.min(z, Short.MAX_VALUE), result, geoIndex);
		return result;
	}

	public static boolean canMoveToCoord(int x, int y, int z, int tx, int ty, int tz, int geoIndex)
	{
		return canMove(x, y, z, tx, ty, tz, false, geoIndex);
	}

	public static Location moveCheck(int x, int y, int z, int tx, int ty, boolean withCollision, boolean backwardMove, boolean returnPrev, int geoIndex)
	{
		int gx = getGeoX(x);
		int gy = getGeoY(y);
		int tgx = getGeoX(tx);
		int tgy = getGeoY(ty);

		Location result = MoveCheck(gx, gy, z, tgx, tgy, withCollision, backwardMove, returnPrev, geoIndex);
		if(result.equals(gx, gy, z))
			return null;

		return result.geo2world();
	}

	public static Location moveCheck(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, false, false, false, geoIndex);
	}

	public static Location moveCheck(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, false, false, returnPrev, geoIndex);
	}

	public static Location moveCheckWithCollision(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, true, false, false, geoIndex);
	}

	public static Location moveCheckWithCollision(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, true, false, returnPrev, geoIndex);
	}

	public static Location moveCheckBackward(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, false, true, false, geoIndex);
	}

	public static Location moveCheckBackward(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, false, true, returnPrev, geoIndex);
	}

	public static Location moveCheckBackwardWithCollision(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, true, true, false, geoIndex);
	}

	public static Location moveCheckBackwardWithCollision(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex)
	{
		return moveCheck(x, y, z, tx, ty, true, true, returnPrev, geoIndex);
	}

	public static Location moveInWaterCheck(Creature actor, int tx, int ty, int tz, int[] limits)
	{
		int gx = getGeoX(actor.getX());
		int gy = getGeoY(actor.getY());
		int z = actor.getZ();
		int h = (int) actor.getTemplate().getCollisionHeight();
		int tgx = getGeoX(tx);
		int tgy = getGeoY(ty);

		/*Location result = canSee(gx, gy, z, tgx, tgy, tz, true, actor.getGeoIndex(), limits[0], limits[1], false);
		if(result.equals(gx, gy, z))
			return null;

		return result.geo2world();*/

		//TODO: Исправить работу того, что выше, и избавиться от того,что ниже.
		return MoveInWaterCheck(gx, gy, z, tgx, tgy, tz, actor.getGeoIndex(), limits[0], limits[1]);
	}

	private static Location MoveInWaterCheck(int x, int y, int z, int tx, int ty, int tz, int geoIndex, int minZ, int maxZ)
	{
		int dx = tx - x;
		int dy = ty - y;
		int dz = tz - z;
		int inc_x = sign(dx);
		int inc_y = sign(dy);
		dx = Math.abs(dx);
		dy = Math.abs(dy);
		if(dx + dy == 0)
			return new Location(x, y, z).geo2world();
		float inc_z_for_x = dx == 0 ? 0 : dz / dx;
		float inc_z_for_y = dy == 0 ? 0 : dz / dy;
		int prev_x;
		int prev_y;
		int prev_z;
		float next_x = x;
		float next_y = y;
		float next_z = z;
		if(dx >= dy) // dy/dx <= 1
		{
			int delta_A = 2 * dy;
			int d = delta_A - dx;
			int delta_B = delta_A - 2 * dx;
			for(int i = 0; i < dx; i++)
			{
				prev_x = x;
				prev_y = y;
				prev_z = z;
				x = (int) next_x;
				y = (int) next_y;
				z = (int) next_z;
				if(d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_z += inc_z_for_x;
					next_y += inc_y;
					next_z += inc_z_for_y;
				}
				else
				{
					d += delta_A;
					next_x += inc_x;
					next_z += inc_z_for_x;
				}

				if(next_z < minZ || next_z >= maxZ || !NLOS_WATER(x, y, z, (int) next_x, (int) next_y, (int) next_z, geoIndex))
					return new Location(prev_x, prev_y, prev_z).geo2world();
			}
		}
		else
		{
			int delta_A = 2 * dx;
			int d = delta_A - dy;
			int delta_B = delta_A - 2 * dy;
			for(int i = 0; i < dy; i++)
			{
				prev_x = x;
				prev_y = y;
				prev_z = z;
				x = (int) next_x;
				y = (int) next_y;
				z = (int) next_z;
				if(d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_z += inc_z_for_x;
					next_y += inc_y;
					next_z += inc_z_for_y;
				}
				else
				{
					d += delta_A;
					next_y += inc_y;
					next_z += inc_z_for_y;
				}

				if(next_z < minZ || next_z >= maxZ || !NLOS_WATER(x, y, z, (int) next_x, (int) next_y, (int) next_z, geoIndex))
					return new Location(prev_x, prev_y, prev_z).geo2world();
			}
		}
		return new Location((int)next_x, (int)next_y, (int)next_z).geo2world();
	}

	private static boolean NLOS_WATER(int x, int y, int z, int next_x, int next_y, int next_z, int geoIndex)
	{
		short[] layers1 = new short[MAX_LAYERS + 1];
		short[] layers2 = new short[MAX_LAYERS + 1];
		NGetLayers(x, y, layers1, geoIndex);
		NGetLayers(next_x, next_y, layers2, geoIndex);

		if(layers1[0] == 0 || layers2[0] == 0)
			return true;

		short h;

		// Находим ближайший к целевой клетке слой
		short z2 = Short.MIN_VALUE;
		for(int i = 1; i <= layers2[0]; i++)
		{
			h = (short) ((short) (layers2[i] & 0x0fff0) >> 1);
			if(Math.abs(next_z - z2) > Math.abs(next_z - h))
				z2 = h;
		}

		// Луч проходит над преградой
		if(next_z + LINEAR_TERRITORY_CELL_SIZE >= z2)
			return true;

		// Либо перед нами стена, либо над нами потолок. Ищем слой пониже, для уточнения
		short z3 = Short.MIN_VALUE;
		for(int i = 1; i <= layers2[0]; i++)
		{
			h = (short) ((short) (layers2[i] & 0x0fff0) >> 1);
			if(h < z2 + Config.MIN_LAYER_HEIGHT && Math.abs(next_z - z3) > Math.abs(next_z - h))
				z3 = h;
		}

		// Ниже нет слоев, значит это стена
		if(z3 == Short.MIN_VALUE)
			return false;

		// Собираем данные о предыдущей клетке, игнорируя верхние слои
		short z1 = Short.MIN_VALUE;
		byte NSWE1 = NSWE_ALL;
		for(int i = 1; i <= layers1[0]; i++)
		{
			h = (short) ((short) (layers1[i] & 0x0fff0) >> 1);
			if(h < z + Config.MIN_LAYER_HEIGHT && Math.abs(z - z1) > Math.abs(z - h))
			{
				z1 = h;
				NSWE1 = (byte) (layers1[i] & 0x0F);
			}
		}

		// Если есть NSWE, то считаем за стену
		return checkNSWE(NSWE1, x, y, next_x, next_y);
	}

	public static Location moveCheckForAI(Location loc1, Location loc2, int geoIndex)
	{
		int gx = getGeoX(loc1.getX());
		int gy = getGeoY(loc1.getY());
		int tgx = getGeoX(loc2.getX());
		int tgy = getGeoY(loc2.getY());

		Location result = MoveCheckForAI(gx, gy, loc1.getZ(), tgx, tgy, geoIndex);
		if(result.equals(gx, gy, loc1.getZ()))
			return null;

		return result.geo2world();
	}

	public static Location moveCheckInAir(Creature actor, int tx, int ty, int tz)
	{
		int gx = getGeoX(actor.getX());
		int gy = getGeoY(actor.getY());
		int z = actor.getZ();
		int h = (int) actor.getActingPlayer().getCollisionHeight();
		int tgx = getGeoX(tx);
		int tgy = getGeoY(ty);

		Location result = canSee(gx, gy, z, tgx, tgy, tz, true, actor.getGeoIndex(), -15000, 15000, false);
		if(result.equals(gx, gy, z))
			return null;

		return result.geo2world();
	}

	public static boolean canSeeTarget(WorldObject actor, WorldObject target)
	{
		if(actor == null || target == null)
			return false;

		if(actor.equals(target))
			return true;

		return canSeeCoord(actor, target.getX(), target.getY(), target.getZ(), (int) target.getActingPlayer().getCollisionHeight(), (int) target.getActingPlayer().getCollisionRadius(), (target.getActingPlayer().isFlying() || target.getActingPlayer().isInWater()));
	}

	public static boolean canSeeCoord(WorldObject actor, int tx, int ty, int tz, boolean tAirOrWater)
	{
		return canSeeCoord(actor, tx, ty, tz, 0, 0, tAirOrWater);
	}

	public static boolean canSeeCoord(WorldObject actor, int tx, int ty, int tz, int th, int tr, boolean tAirOrWater)
	{
		if(actor == null)
			return false;

		return canSeeCoord(actor.getX(), actor.getY(), actor.getZ(), (int) actor.getActingPlayer().getCollisionHeight(), (int) actor.getActingPlayer().getCollisionRadius(), (actor.getActingPlayer().isFlying() || actor.getActingPlayer().isInWater()), tx, ty, tz, th, tr, tAirOrWater, actor.getActingPlayer().getGeoIndex(), GameUtils.isPlayer(actor));
	}

	public static boolean canSeeCoord(int x, int y, int z, boolean airOrWater, int tx, int ty, int tz, boolean tAirOrWater, int geoIndex, boolean debug)
	{
		return canSeeCoord(x, y, z, 0, 0, airOrWater, tx, ty, tz, 0, 0, tAirOrWater, geoIndex, debug);
	}

	private static boolean canSeeCoord(int x, int y, int z, int h, int r, boolean airOrWater, int tx, int ty, int tz, int th, int tr, boolean tAirOrWater, int geoIndex, boolean debug)
	{
		int mx = getGeoX(x);
		int my = getGeoY(y);
		int tmx = getGeoX(tx);
		int tmy = getGeoY(ty);

		if(checkIsInSameGeoCeil(mx, my, z, tmx, tmy, tz, geoIndex))
			return true;

		if(r > 0)
		{
			Point2D n = GeometryUtils.applyOffset(x, y, tx, ty, r + LINEAR_TERRITORY_CELL_SIZE, false);
			mx = getGeoX(n.getX());
			my = getGeoY(n.getY());

			if(checkIsInSameGeoCeil(mx, my, z, tmx, tmy, tz, geoIndex))
				return true;
		}

		if(tr > 0)
		{
			Point2D tn = GeometryUtils.applyOffset(tx, ty, x, y, tr + LINEAR_TERRITORY_CELL_SIZE, false);
			tmx = getGeoX(tn.getX());
			tmy = getGeoY(tn.getY());

			if(checkIsInSameGeoCeil(mx, my, z, tmx, tmy, tz, geoIndex))
				return true;
		}

		int mh = Math.max(0, h - (h % 8) + 8) * 2;
		int mz = Math.min(z + mh, NgetUpperHeight(mx, my, (short) Math.min(z, Short.MAX_VALUE), geoIndex) - Config.MIN_LAYER_HEIGHT);
		//
		int tmh = Math.max(0, th - (th % 8) + 8) * 2;
		int tmz = Math.min(tz + tmh, NgetUpperHeight(tmx, tmy, (short) Math.min(tz, Short.MAX_VALUE), geoIndex) - Config.MIN_LAYER_HEIGHT);
		for(int i = 0; i <= tmh; i += 2)
		{
			// 1. Проверяем видимость лицом персонажа на видимость обьекта от пят до макушки.
			// 2. Проверяем видимость от пят до макушки обьектом на видимость персонажа в лицо.
			if(canSee(mx, my, mz, tmx, tmy, tmz, airOrWater, geoIndex, Integer.MIN_VALUE, Integer.MAX_VALUE, debug).equals(tmx, tmy, tmz) && canSee(tmx, tmy, tmz, mx, my, mz, tAirOrWater, geoIndex, Integer.MIN_VALUE, Integer.MAX_VALUE, false).equals(mx, my, mz))
				return true;
			tmz--;
		}
		return false;
	}

	private static boolean checkIsInSameGeoCeil(int mx, int my, int z, int tmx, int tmy, int tz, int geoIndex)
	{
		if(mx == tmx && my == tmy)
		{
			if(!Config.ALLOW_GEODATA)
				return true;

			int height = NgetLowerHeight(mx, my, (short) Math.min(z, Short.MAX_VALUE), geoIndex);
			int theight = NgetLowerHeight(tmx, tmy, (short) Math.min(tz, Short.MAX_VALUE), geoIndex);
			if(height == theight)
				return true;
		}
		return false;
	}

	public static boolean canMoveWithCollision(int x, int y, int z, int tx, int ty, int tz, int geoIndex)
	{
		return canMove(x, y, z, tx, ty, tz, true, geoIndex);
	}

	/**
	 * @param NSWE
	 * @param x
	 * @param y
	 * @param tx
	 * @param ty
	 *
	 * @return True if NSWE dont block given direction
	 */
	public static boolean checkNSWE(byte NSWE, int x, int y, int tx, int ty)
	{
		if(NSWE == NSWE_ALL)
			return true;
		if(NSWE == NSWE_NONE)
			return false;
		if(tx > x)
		{
			if((NSWE & EAST) == 0)
				return false;
		}
		else if(tx < x)
			if((NSWE & WEST) == 0)
				return false;
		if(ty > y)
		{
			if((NSWE & SOUTH) == 0)
				return false;
		}
		else if(ty < y)
			if((NSWE & NORTH) == 0)
				return false;
		return true;
	}

	public static boolean hasGeo(int x, int y, int geoIndex)
	{
		return getGeoBlockFromGeoCoords(getGeoX(x), getGeoY(y), geoIndex, false) != null;
	}

	public static String geoXYZ2Str(int _x, int _y, int _z)
	{
		return "(" + getWorldX(_x) + " " + getWorldY(_y) + " " + _z + ")";
	}

	public static String NSWE2Str(byte nswe)
	{
		String result = "";
		if((nswe & NORTH) == NORTH)
			result += "N";
		if((nswe & SOUTH) == SOUTH)
			result += "S";
		if((nswe & WEST) == WEST)
			result += "W";
		if((nswe & EAST) == EAST)
			result += "E";
		return result.isEmpty() ? "X" : result;
	}

	private static short FindNearestLowerLayer(short[] layers, int z, boolean regionEdge)
	{
		short h, nearest_layer_h = Short.MIN_VALUE;
		short nearest_layer = Short.MIN_VALUE;
		int zCheck = regionEdge ? z + Config.REGION_EDGE_MAX_Z_DIFF : z;
		for(int i = 1; i <= layers[0]; i++)
		{
			h = (short) ((short) (layers[i] & 0x0fff0) >> 1);
			if(h <= zCheck && nearest_layer_h <= h)
			{
				nearest_layer_h = h;
				nearest_layer = layers[i];
			}
		}
		return nearest_layer;
	}

	private static short[] CheckNoOneLayerInRangeAndFindNearestLowerLayer(short[] layers, int z0, int z1)
	{
		int z_min = Math.min(z0, z1);
		int z_max = Math.max(z0, z1);

		short h, layerid = Short.MIN_VALUE, nearest_layer = Short.MIN_VALUE, nearest_layer_h = Short.MIN_VALUE;
		for(int i = 1; i <= layers[0]; i++)
		{
			h = (short) ((short) (layers[i] & 0x0fff0) >> 1);
			if(z_min < h && h < z_max)
				return new short[]{ Short.MIN_VALUE, Short.MIN_VALUE };

			if(h <= z_max && nearest_layer_h <= h)
			{
				nearest_layer_h = h;
				nearest_layer = layers[i];
				layerid = (short) i;
			}
		}
		return new short[]{ layerid, nearest_layer };
	}

	private static short[] CheckNoOneLayerInRangeAndFindNearestHighestLayer(short[] layers, int z0, int z1)
	{
		int z_min = Math.min(z0, z1);
		int z_max = Math.max(z0, z1);

		short h, layerid = Short.MAX_VALUE, nearest_layer = Short.MAX_VALUE, nearest_layer_h = Short.MAX_VALUE;
		for(int i = layers[0]; i >= 1; i--)
		{
			h = (short) ((short) (layers[i] & 0x0fff0) >> 1);
			if(z_max >= h && h >= z_min)
				return new short[]{ Short.MAX_VALUE, Short.MAX_VALUE };

			if(h > z_min && nearest_layer_h > h)
			{
				nearest_layer_h = h;
				nearest_layer = layers[i];
				layerid = (short) i;
			}
		}
		return new short[]{ layerid, nearest_layer };
	}

	public static boolean canSeeWallCheck(short[] lower_layer, short[] nearest_lower_neighbor, short[] highest_layer, short[] nearest_highest_neighbor, byte directionNSWE, int curr_z, boolean airOrWater, boolean debug)
	{
		if(lower_layer[1] == nearest_highest_neighbor[1] || highest_layer[1] == nearest_lower_neighbor[1])
			return false;

		// Перед нами нет преград.
		if(highest_layer[1] == Short.MAX_VALUE && nearest_highest_neighbor[1] == Short.MAX_VALUE)
			return true;

		short nearest_highest_neighbor_h = (short) ((short) (nearest_highest_neighbor[1] & 0x0fff0) >> 1);
		short nearest_lower_neighbor_h = (short) ((short) (nearest_lower_neighbor[1] & 0x0fff0) >> 1);
		if(nearest_highest_neighbor_h < curr_z || nearest_lower_neighbor_h >= curr_z)
			return false;

		short lower_layer_h = (short) ((short) (lower_layer[1] & 0x0fff0) >> 1);
		short highest_layer_h = (short) ((short) (highest_layer[1] & 0x0fff0) >> 1);

		// Преграда ниже чем высота проверяющего.
		if(curr_z <= highest_layer_h && curr_z > lower_layer_h)
		{
			if(curr_z <= nearest_highest_neighbor_h && curr_z > nearest_lower_neighbor_h)
			{
				int lower_z_diff = nearest_lower_neighbor_h - lower_layer_h;
				if(lower_z_diff > -Config.MAX_Z_DIFF || lower_layer[0] == nearest_lower_neighbor[0] || highest_layer[0] == nearest_highest_neighbor[0])
					return true;
			}
		}

		if(airOrWater)
		{
			//TODO: Пофиксить простреливания потолка (пола) под водой.
			return true;
		}

		byte lowerNSWE = (byte) (lower_layer[1] & 0x0F);
		if((lowerNSWE & directionNSWE) == directionNSWE)
		{
			int lower_z_diff = nearest_lower_neighbor_h - lower_layer_h;
			if(lower_z_diff > -Config.MAX_Z_DIFF || lower_layer[0] == nearest_lower_neighbor[0] || highest_layer[0] == nearest_highest_neighbor[0])
				return true;
		}
		return false;
	}

	/**
	 * проверка видимости
	 * @return возвращает последнюю точку которую видно (в формате геокоординат)
	 * в результате (Location) h является кодом, если >= 0 то успешно достигли последней точки, если меньше то не последней
	 */
	private static Location canSee(int _x, int _y, int _z, int _tx, int _ty, int _tz, boolean airOrWater, int geoIndex, int minZ, int maxZ, boolean debug)
	{
		int diff_x = _tx - _x, diff_y = _ty - _y, diff_z = _tz - _z;
		int dx = Math.abs(diff_x), dy = Math.abs(diff_y), dz = Math.abs(diff_z);

		float steps = Math.max(Math.max(dx, dy), dz);
		int curr_x = _x, curr_y = _y, curr_z = _z;
		short[] curr_layers = new short[MAX_LAYERS + 1];
		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);

		Location result = new Location(_x, _y, _z, -1);

		if(steps == 0)
		{
			short[] layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, curr_z, curr_z + diff_z);
			if(layer[1] != Short.MIN_VALUE)
				result.set(_tx, _ty, _tz, 1);
			return result;
		}

		float step_x = diff_x / steps, step_y = diff_y / steps, step_z = diff_z / steps;
		float half_step_z = step_z / 2.0f;
		float next_x = curr_x, next_y = curr_y, next_z = curr_z;
		int i_next_x, i_next_y, i_next_z, middle_z;
		short[] tmp_layers = new short[MAX_LAYERS + 1];
		short[] src_nearest_lower_layer, dst_nearest_lower_layer, tmp_nearest_lower_layer;
		short[] src_nearest_highest_layer, dst_nearest_highest_layer, tmp_nearest_highest_layer;

		for(int i = 0; i < steps; i++)
		{
			if(curr_layers[0] == 0)
			{
				result.set(_tx, _ty, _tz, 0);
				return result; // Здесь нет геодаты, разрешаем
			}
	
			next_z += step_z;
			i_next_z = (int) (next_z + 0.5f);

			if(i_next_z < minZ || i_next_z >= maxZ)
				return result.setHeading(-10);

			middle_z = (int) (curr_z + half_step_z);

			next_x += step_x;
			i_next_x = (int) (next_x + 0.5f);

			next_y += step_y;
			i_next_y = (int) (next_y + 0.5f);

			src_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, curr_z, middle_z);
			if(src_nearest_lower_layer[1] == Short.MIN_VALUE)
				return result.setHeading(-11); // нет снизу слоя и значит это "пустота", то что за стеной или за колоной

			src_nearest_highest_layer = CheckNoOneLayerInRangeAndFindNearestHighestLayer(curr_layers, curr_z, middle_z);

			NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
			if(curr_layers[0] == 0)
			{
				result.set(_tx, _ty, _tz, 0);
				return result; // Здесь нет геодаты, разрешаем
			}

			dst_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, i_next_z, middle_z);
			if(dst_nearest_lower_layer[1] == Short.MIN_VALUE)
				return result.setHeading(-12); // нет снизу слоя и значит это "пустота", то что за стеной или за колоной

			dst_nearest_highest_layer = CheckNoOneLayerInRangeAndFindNearestHighestLayer(curr_layers, i_next_z, middle_z);

			if(curr_x == i_next_x)
			{
				//движемся по вертикали
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, src_nearest_highest_layer, dst_nearest_highest_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, airOrWater, debug))
					return result.setHeading(-20);
			}
			else if(curr_y == i_next_y)
			{
				//движемся по горизонтали
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, src_nearest_highest_layer, dst_nearest_highest_layer, i_next_x > curr_x ? EAST : WEST, curr_z, airOrWater, debug))
					return result.setHeading(-21);
			}
			else
			{
				//движемся по диагонали
				NGetLayers(curr_x, i_next_y, tmp_layers, geoIndex);
				if(tmp_layers[0] == 0)
				{
					result.set(_tx, _ty, _tz, 0);
					return result; // Здесь нет геодаты, разрешаем
				}

				tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z);
				if(tmp_nearest_lower_layer[1] == Short.MIN_VALUE)
					return result.setHeading(-30); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

				tmp_nearest_highest_layer = CheckNoOneLayerInRangeAndFindNearestHighestLayer(tmp_layers, i_next_z, middle_z);

				if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, src_nearest_highest_layer, tmp_nearest_highest_layer, i_next_x > curr_x ? EAST : WEST, curr_z, airOrWater, debug))
					return result.setHeading(-32);

				if(!canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, tmp_nearest_highest_layer, dst_nearest_highest_layer, i_next_x > curr_x ? EAST : WEST, curr_z, airOrWater, debug))
					return result.setHeading(-34);

				NGetLayers(i_next_x, curr_y, tmp_layers, geoIndex);
				if(tmp_layers[0] == 0)
				{
					result.set(_tx, _ty, _tz, 0);
					return result; // Здесь нет геодаты, разрешаем
				}

				tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z);
				if(tmp_nearest_lower_layer[1] == Short.MIN_VALUE)
					return result.setHeading(-35); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

				tmp_nearest_highest_layer = CheckNoOneLayerInRangeAndFindNearestHighestLayer(tmp_layers, i_next_z, middle_z);

				if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, src_nearest_highest_layer, tmp_nearest_highest_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, airOrWater, debug))
					return result.setHeading(-32);

				if(!canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, tmp_nearest_highest_layer, dst_nearest_highest_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, airOrWater, debug))
					return result.setHeading(-33);
			}

			result.setXYZ(curr_x, curr_y, curr_z);
			curr_x = i_next_x;
			curr_y = i_next_y;
			curr_z = i_next_z;
		}

		result.set(_tx, _ty, _tz, 0xFF);
		return result;
	}

	/**
	 * проверка проходимости по прямой
	 */
	private static boolean canMove(int __x, int __y, int _z, int __tx, int __ty, int _tz, boolean withCollision, int geoIndex)
	{
		int _x = getGeoX(__x);
		int _y = getGeoY(__y);
		int _tx = getGeoX(__tx);
		int _ty = getGeoY(__ty);

		int diff_x = _tx - _x, diff_y = _ty - _y;
		int incx = sign(diff_x), incy = sign(diff_y);
		final boolean overRegionEdge = ((_x >> 11) != (_tx >> 11) || (_y >> 11) != (_ty >> 11));

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;

		int pdx, pdy, es, el;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x, curr_y = _y, curr_z = _z;
		int next_x = curr_x, next_y = curr_y, next_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		if(curr_layers[0] == 0)
			return true;

		for(int i = 0; i < el; i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;
				next_y += incy;
			}
			else
			{
				next_x += pdx;
				next_y += pdy;
			}
			boolean regionEdge = overRegionEdge && ((next_x >> 11) != (curr_x >> 11) || (next_y >> 11) != (curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);
			if((next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, withCollision, regionEdge, geoIndex)) == Integer.MIN_VALUE)
				return false;

			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			curr_x = next_x;
			curr_y = next_y;
			curr_z = next_z;
		}

		int diff_z = curr_z - _tz;
		if(Config.ALLOW_FALL_FROM_WALLS)
			return diff_z < Config.MAX_Z_DIFF ? true : false;

		if(diff_z < 0)
			diff_z = -diff_z;
		return diff_z > Config.MAX_Z_DIFF ? false : true;
	}

	private static Location MoveCheck(int _x, int _y, int _z, int _tx, int _ty, boolean withCollision, boolean backwardMove, boolean returnPrev, int geoIndex)
	{
		int diff_x = _tx - _x, diff_y = _ty - _y;
		int incx = sign(diff_x), incy = sign(diff_y);
		final boolean overRegionEdge = ((_x >> 11) != (_tx >> 11) || (_y >> 11) != (_ty >> 11));

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;

		int pdx, pdy, es, el;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x, curr_y = _y, curr_z = _z;
		int next_x = curr_x, next_y = curr_y, next_z = curr_z;
		int prev_x = curr_x, prev_y = curr_y, prev_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		for(int i = 0; i < el; i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;
				next_y += incy;
			}
			else
			{
				next_x += pdx;
				next_y += pdy;
			}
			boolean regionEdge = overRegionEdge && ((next_x >> 11) != (curr_x >> 11) || (next_y >> 11) != (curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);
			if((next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, withCollision, regionEdge, geoIndex)) == Integer.MIN_VALUE)
				break;
			if(backwardMove && NcanMoveNext(next_x, next_y, next_z, next_layers, curr_x, curr_y, curr_layers, temp_layers, withCollision, regionEdge, geoIndex) == Integer.MIN_VALUE)
				break;

			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			if(returnPrev)
			{
				prev_x = curr_x;
				prev_y = curr_y;
				prev_z = curr_z;
			}

			curr_x = next_x;
			curr_y = next_y;
			curr_z = next_z;
		}

		if(returnPrev)
		{
			curr_x = prev_x;
			curr_y = prev_y;
			curr_z = prev_z;
		}

		return new Location(curr_x, curr_y, curr_z);
	}

	/** Аналогичен CanMove, но возвращает весь пройденный путь. В гео координатах. */
	public static List<Location> MoveList(int __x, int __y, int _z, int __tx, int __ty, int geoIndex, boolean onlyFullPath)
	{
		int _x = getGeoX(__x);
		int _y = getGeoY(__y);
		int _tx = getGeoX(__tx);
		int _ty = getGeoY(__ty);

		int diff_x = _tx - _x, diff_y = _ty - _y;
		int incx = sign(diff_x), incy = sign(diff_y);
		final boolean overRegionEdge = ((_x >> 11) != (_tx >> 11) || (_y >> 11) != (_ty >> 11));

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;

		int pdx, pdy, es, el;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x, curr_y = _y, curr_z = _z;
		int next_x = curr_x, next_y = curr_y, next_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		if(curr_layers[0] == 0)
			return null;

		List<Location> result = new ArrayList<Location>(el + 1);

		result.add(new Location(curr_x, curr_y, curr_z)); // Первая точка

		for(int i = 0; i < el; i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
				next_y += incy;//или сместить влево-вправо, если цикл проходит по y
			}
			else
			{
				next_x += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
				next_y += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
			}
			boolean regionEdge = overRegionEdge && ((next_x >> 11) != (curr_x >> 11) || (next_y >> 11) != (curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);
			if((next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, false, regionEdge, geoIndex)) == Integer.MIN_VALUE)
				if(onlyFullPath)
					return null;
				else
					break;

			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			curr_x = next_x;
			curr_y = next_y;
			curr_z = next_z;

			result.add(new Location(curr_x, curr_y, curr_z));
		}

		return result;
	}

	/**
	 * Используется только для антипаровоза в AI
	 */
	private static Location MoveCheckForAI(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		int dx = tx - x;
		int dy = ty - y;
		int inc_x = sign(dx);
		int inc_y = sign(dy);

		dx = Math.abs(dx);
		dy = Math.abs(dy);
		if(dx + dy < 2 || dx == 2 && dy == 0 || dx == 0 && dy == 2)
			return new Location(x, y, z);

		int prev_x = x;
		int prev_y = y;
		int prev_z = z;
		int next_x = x;
		int next_y = y;
		int next_z = z;
		if(dx >= dy) // dy/dx <= 1
		{
			int delta_A = 2 * dy;
			int d = delta_A - dx;
			int delta_B = delta_A - 2 * dx;
			for(int i = 0; i < dx; i++)
			{
				prev_x = x;
				prev_y = y;
				prev_z = z;
				x = next_x;
				y = next_y;
				z = next_z;
				if(d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_y += inc_y;
				}
				else
				{
					d += delta_A;
					next_x += inc_x;
				}
				next_z = NcanMoveNextForAI(x, y, z, next_x, next_y, geoIndex);
				if(next_z == 0)
					return new Location(prev_x, prev_y, prev_z);
			}
		}
		else
		{
			int delta_A = 2 * dx;
			int d = delta_A - dy;
			int delta_B = delta_A - 2 * dy;
			for(int i = 0; i < dy; i++)
			{
				prev_x = x;
				prev_y = y;
				prev_z = z;
				x = next_x;
				y = next_y;
				z = next_z;
				if(d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_y += inc_y;
				}
				else
				{
					d += delta_A;
					next_y += inc_y;
				}
				next_z = NcanMoveNextForAI(x, y, z, next_x, next_y, geoIndex);
				if(next_z == 0)
					return new Location(prev_x, prev_y, prev_z);
			}
		}
		return new Location(next_x, next_y, next_z);
	}

	private static boolean NcanMoveNextExCheck(int x, int y, int h, int nextx, int nexty, int hexth, short[] temp_layers, boolean regionEdge, int geoIndex)
	{
		NGetLayers(x, y, temp_layers, geoIndex);
		if(temp_layers[0] == 0)
			return true;

		short temp_layer;
		if((temp_layer = FindNearestLowerLayer(temp_layers, h + Config.MIN_LAYER_HEIGHT, regionEdge)) == Short.MIN_VALUE)
			return false;
		short temp_layer_h = (short) ((short) (temp_layer & 0x0fff0) >> 1);
		final int maxDeltaZ = regionEdge ? Config.REGION_EDGE_MAX_Z_DIFF : Config.MAX_Z_DIFF;
		if(Math.abs(temp_layer_h - hexth) >= maxDeltaZ || Math.abs(temp_layer_h - h) >= maxDeltaZ)
			return false;
		return checkNSWE((byte) (temp_layer & 0x0F), x, y, nextx, nexty);
	}

	/**
	 *
	 * @return возвращает высоту следующего блока, либо Integer.MIN_VALUE если двигатся нельзя
	 */
	public static int NcanMoveNext(int x, int y, int z, short[] layers, int next_x, int next_y, short[] next_layers, short[] temp_layers,boolean withCollision, boolean regionEdge,  int geoIndex)
	{
		if(layers[0] == 0 || next_layers[0] == 0)
			return z;

		short layer, next_layer;
		if((layer = FindNearestLowerLayer(layers, z + Config.MIN_LAYER_HEIGHT, regionEdge)) == Short.MIN_VALUE)
			return Integer.MIN_VALUE;

		byte layer_nswe = (byte) (layer & 0x0F);
		if(!checkNSWE(layer_nswe, x, y, next_x, next_y))
			return Integer.MIN_VALUE;

		short layer_h = (short) ((short) (layer & 0x0fff0) >> 1);
		if((next_layer = FindNearestLowerLayer(next_layers, layer_h + Config.MIN_LAYER_HEIGHT, regionEdge)) == Short.MIN_VALUE)
			return Integer.MIN_VALUE;

		short next_layer_h = (short) ((short) (next_layer & 0x0fff0) >> 1);

		// если движение не по диагонали
		if(x == next_x || y == next_y)
		{
			if(withCollision)
			{
				//short[] heightNSWE = temp_layers;
				if(x == next_x)
				{
					NgetLowerHeightAndNSWE(x - 1, y, layer_h, temp_layers, geoIndex);
					if(Math.abs(temp_layers[0] - layer_h) > 15 || !checkNSWE(layer_nswe, x - 1, y, x, y) || !checkNSWE((byte) temp_layers[1], x - 1, y, x - 1, next_y))
						return Integer.MIN_VALUE;

					NgetLowerHeightAndNSWE(x + 1, y, layer_h, temp_layers, geoIndex);
					if(Math.abs(temp_layers[0] - layer_h) > 15 || !checkNSWE(layer_nswe, x + 1, y, x, y) || !checkNSWE((byte) temp_layers[1], x + 1, y, x + 1, next_y))
						return Integer.MIN_VALUE;

					return next_layer_h;
				}

				final int maxDeltaZ = regionEdge ? Config.REGION_EDGE_MAX_Z_DIFF : Config.MAX_Z_DIFF;
				NgetLowerHeightAndNSWE(x, y - 1, layer_h, temp_layers, geoIndex);
				if(Math.abs(temp_layers[0] - layer_h) >= maxDeltaZ || !checkNSWE(layer_nswe, x, y - 1, x, y) || !checkNSWE((byte) temp_layers[1], x, y - 1, next_x, y - 1))
					return Integer.MIN_VALUE;

				NgetLowerHeightAndNSWE(x, y + 1, layer_h, temp_layers, geoIndex);
				if(Math.abs(temp_layers[0] - layer_h) >= maxDeltaZ || !checkNSWE(layer_nswe, x, y + 1, x, y) || !checkNSWE((byte) temp_layers[1], x, y + 1, next_x, y + 1))
					return Integer.MIN_VALUE;
			}

			return next_layer_h;
		}

		if(!NcanMoveNextExCheck(x, next_y, layer_h, next_x, next_y, next_layer_h, temp_layers, regionEdge, geoIndex))
			return Integer.MIN_VALUE;
		if(!NcanMoveNextExCheck(next_x, y, layer_h, next_x, next_y, next_layer_h, temp_layers, regionEdge, geoIndex))
			return Integer.MIN_VALUE;

		//FIXME if(withCollision)

		return next_layer_h;
	}

	/**
	 * Используется только для антипаровоза в AI
	 */
	public static int NcanMoveNextForAI(int x, int y, int z, int next_x, int next_y, int geoIndex)
	{
		short[] layers1 = new short[MAX_LAYERS + 1];
		short[] layers2 = new short[MAX_LAYERS + 1];
		NGetLayers(x, y, layers1, geoIndex);
		NGetLayers(next_x, next_y, layers2, geoIndex);

		if(layers1[0] == 0 || layers2[0] == 0)
			return z == 0 ? 1 : z;

		short h;

		short z1 = Short.MIN_VALUE;
		byte NSWE1 = NSWE_ALL;
		for(int i = 1; i <= layers1[0]; i++)
		{
			h = (short) ((short) (layers1[i] & 0x0fff0) >> 1);
			if(Math.abs(z - z1) > Math.abs(z - h))
			{
				z1 = h;
				NSWE1 = (byte) (layers1[i] & 0x0F);
			}
		}

		if(z1 == Short.MIN_VALUE)
			return 0;

		short z2 = Short.MIN_VALUE;
		byte NSWE2 = NSWE_ALL;
		for(int i = 1; i <= layers2[0]; i++)
		{
			h = (short) ((short) (layers2[i] & 0x0fff0) >> 1);
			if(Math.abs(z - z2) > Math.abs(z - h))
			{
				z2 = h;
				NSWE2 = (byte) (layers2[i] & 0x0F);
			}
		}

		if(z2 == Short.MIN_VALUE)
			return 0;

		if(z1 > z2 && z1 - z2 > Config.MAX_Z_DIFF)
			return 0;

		if(!checkNSWE(NSWE1, x, y, next_x, next_y) || !checkNSWE(NSWE2, next_x, next_y, x, y))
			return 0;

		return z2 == 0 ? 1 : z2;
	}

	/**
	 * в нулевую ячейку кладется длина
	 * @param geoX
	 * @param geoY
	 * @param result
	 */
	public static void NGetLayers(int geoX, int geoY, short[] result, int geoIndex)
	{
		result[0] = 0;
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, false);
		if(block == null)
			return;

		int cellX, cellY;
		int index = 0;
		// Read current block type: 0 - flat, 1 - complex, 2 - multilevel
		byte type = block[index];
		index++;

		switch(type)
		{
			case BLOCKTYPE_FLAT:
				short height = makeShort(block[index + 1], block[index]);
				height = (short) (height & 0x0fff0);
				result[0]++;
				result[1] = (short) ((short) (height << 1) | NSWE_ALL);
				return;
			case BLOCKTYPE_COMPLEX:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				index += (cellX << 3) + cellY << 1;
				height = makeShort(block[index + 1], block[index]);
				result[0]++;
				result[1] = height;
				return;
			case BLOCKTYPE_MULTILEVEL:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				int offset = (cellX << 3) + cellY;
				while(offset > 0)
				{
					byte lc = block[index];
					index += (lc << 1) + 1;
					offset--;
				}
				byte layer_count = block[index];
				index++;
				if(layer_count <= 0 || layer_count > MAX_LAYERS)
					return;
				result[0] = layer_count;
				while(layer_count > 0)
				{
					result[layer_count] = makeShort(block[index + 1], block[index]);
					layer_count--;
					index += 2;
				}
				return;
			default:
				_log.error("GeoEngine: Unknown block type");
				return;
		}
	}

	private static short NgetType(int geoX, int geoY, int geoIndex)
	{
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, false);

		if(block == null)
			return 0;

		return block[0];
	}

	/**
	 * @param geoX позиция геодаты
	 * @param geoY позиция геодаты
	 * @param z координата без изменений
	 *
	 * @return Высота нижнего слоя геодаты от указанных координат
	 */
	public static int NgetLowerHeight(int geoX, int geoY, short z, int geoIndex)
	{
		short[] result = new short[2];
		NgetLowerHeightAndNSWE(geoX, geoY, z, result, geoIndex);
		return result[0];
	}

	/**
	 * @param geoX позиция геодаты
	 * @param geoY позиция геодаты
	 * @param z координата без изменений
	 *
	 * @return NSWE: 0-15
	 */
	public static byte NgetLowerNSWE(int geoX, int geoY, short z, int geoIndex)
	{
		short[] result = new short[2];
		NgetLowerHeightAndNSWE(geoX, geoY, z, result, geoIndex);
		return (byte) result[1];
	}

	public static void NgetLowerHeightAndNSWE(int geoX, int geoY, short z, short[] result, int geoIndex)
	{
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, false);

		if(block == null)
		{
			result[0] = z;
			result[1] = NSWE_ALL;
			return;
		}

		int cellX, cellY, index = 0;
		short layer, height, NSWE = NSWE_ALL;

		// Read current block type: 0 - flat, 1 - complex, 2 - multilevel
		byte type = block[index];
		index++;

		final int z_nearest_lower_limit = Math.min(z + Config.MIN_LAYER_HEIGHT, Short.MAX_VALUE);

		switch(type)
		{
			case BLOCKTYPE_FLAT:
				layer = makeShort(block[index + 1], block[index]);
				height = (short) (layer & 0x0fff0);
				if(height >= z_nearest_lower_limit)
					result[0] = (short) World.MAP_MIN_Z;
				else
					result[0] = height;
				result[1] = NSWE_ALL;
				return;
			case BLOCKTYPE_COMPLEX:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				index += (cellX << 3) + cellY << 1;
				layer = makeShort(block[index + 1], block[index]);
				height = (short) ((short) (layer & 0x0fff0) >> 1); // height / 2
				if(height >= z_nearest_lower_limit)
				{
					result[0] = (short) World.MAP_MIN_Z;
					result[1] = NSWE_ALL;
				}
				else
				{
					result[0] = height;
					result[1] = (short) (layer & 0x0F);
				}
				return;
			case BLOCKTYPE_MULTILEVEL:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				int offset = (cellX << 3) + cellY;
				while(offset > 0)
				{
					byte lc = block[index];
					index += (lc << 1) + 1;
					offset--;
				}
				byte layers = block[index];
				index++;
				if(layers <= 0 || layers > MAX_LAYERS)
				{
					result[0] = z;
					result[1] = NSWE_ALL;
					return;
				}

				short tempz1 = Short.MIN_VALUE;
				short tempz2 = Short.MIN_VALUE;
				int index_nswe1 = 0;
				int index_nswe2 = 0;

				while(layers > 0)
				{
					height = (short) ((short) (makeShort(block[index + 1], block[index]) & 0x0fff0) >> 1); // height / 2
					if(height < z_nearest_lower_limit)
					{
						if(height > tempz1)
						{
							tempz1 = height;
							index_nswe1 = index;
						}
					}
					else if(Math.abs(z - height) < Math.abs(z - tempz2))
					{
						tempz2 = height;
						index_nswe2 = index;
					}

					layers--;
					index += 2;
				}

				if(index_nswe1 > 0)
				{
					NSWE = makeShort(block[index_nswe1 + 1], block[index_nswe1]);
					NSWE = (short) (NSWE & 0x0F);
				}
				else if(index_nswe2 > 0)
				{
					NSWE = makeShort(block[index_nswe2 + 1], block[index_nswe2]);
					NSWE = (short) (NSWE & 0x0F);
				}

				height = tempz1 > Short.MIN_VALUE ? tempz1 : tempz2;
				if(height >= z_nearest_lower_limit)
				{
					result[0] = (short) World.MAP_MIN_Z;
					result[1] = NSWE_ALL;
				}
				else
				{
					result[0] = height;
					result[1] = NSWE;
				}
				return;
			default:
				_log.error("GeoEngine: Unknown block type.");
				result[0] = z;
				result[1] = NSWE_ALL;
				return;
		}
	}

	/**
	 * @param geoX позиция геодаты
	 * @param geoY позиция геодаты
	 * @param z координата без изменений
	 *
	 * @return Высота верхнего слоя геодаты от указанных координат
	 */
	public static int NgetUpperHeight(int geoX, int geoY, short z, int geoIndex)
	{
		short[] result = new short[2];
		NgetUpperHeightAndNSWE(geoX, geoY, z, result, geoIndex);
		return result[0];
	}

	/**
	 * @param geoX позиция геодаты
	 * @param geoY позиция геодаты
	 * @param z координата без изменений
	 *
	 * @return NSWE: 0-15
	 */
	public static byte NgetUpperNSWE(int geoX, int geoY, short z, int geoIndex)
	{
		short[] result = new short[2];
		NgetUpperHeightAndNSWE(geoX, geoY, z, result, geoIndex);
		return (byte) result[1];
	}

	public static void NgetUpperHeightAndNSWE(int geoX, int geoY, short z, short[] result, int geoIndex)
	{
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, false);

		if(block == null)
		{
			result[0] = z;
			result[1] = NSWE_ALL;
			return;
		}

		int cellX, cellY, index = 0;
		short layer, height, NSWE = NSWE_ALL;

		// Read current block type: 0 - flat, 1 - complex, 2 - multilevel
		byte type = block[index];
		index++;

		final int z_nearest_lower_limit = Math.min(z + Config.MIN_LAYER_HEIGHT, Short.MAX_VALUE);

		switch(type)
		{
			case BLOCKTYPE_FLAT:
				layer = makeShort(block[index + 1], block[index]);
				height = (short) (layer & 0x0fff0);
				if(height < z_nearest_lower_limit)
					result[0] = (short) World.MAP_MAX_Z;
				else
					result[0] = height;
				result[1] = NSWE_ALL;
				return;
			case BLOCKTYPE_COMPLEX:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				index += (cellX << 3) + cellY << 1;
				layer = makeShort(block[index + 1], block[index]);
				height = (short) ((short) (layer & 0x0fff0) >> 1); // height / 2
				if(height < z_nearest_lower_limit)
				{
					result[0] = (short) World.MAP_MAX_Z;
					result[1] = NSWE_ALL;
				}
				else
				{
					result[0] = height;
					result[1] = (short) (layer & 0x0F);
				}
				return;
			case BLOCKTYPE_MULTILEVEL:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				int offset = (cellX << 3) + cellY;
				while(offset > 0)
				{
					byte lc = block[index];
					index += (lc << 1) + 1;
					offset--;
				}
				byte layers = block[index];
				index++;
				if(layers <= 0 || layers > MAX_LAYERS)
				{
					result[0] = z;
					result[1] = NSWE_ALL;
					return;
				}

				short tempz1 = Short.MAX_VALUE;
				short tempz2 = Short.MAX_VALUE;
				int index_nswe1 = 0;
				int index_nswe2 = 0;

				while(layers > 0)
				{
					height = (short) ((short) (makeShort(block[index + 1], block[index]) & 0x0fff0) >> 1); // height / 2
					if(height >= z_nearest_lower_limit)
					{
						if(height < tempz1)
						{
							tempz1 = height;
							index_nswe1 = index;
						}
					}
					else if(Math.abs(z - height) > Math.abs(z - tempz2))
					{
						tempz2 = height;
						index_nswe2 = index;
					}

					layers--;
					index += 2;
				}

				if(index_nswe1 > 0)
				{
					NSWE = makeShort(block[index_nswe1 + 1], block[index_nswe1]);
					NSWE = (short) (NSWE & 0x0F);
				}
				else if(index_nswe2 > 0)
				{
					NSWE = makeShort(block[index_nswe2 + 1], block[index_nswe2]);
					NSWE = (short) (NSWE & 0x0F);
				}

				height = tempz1 < Short.MAX_VALUE ? tempz1 : tempz2;
				if(height < z_nearest_lower_limit)
				{
					result[0] = (short) World.MAP_MAX_Z;
					result[1] = NSWE_ALL;
				}
				else
				{
					result[0] = height;
					result[1] = NSWE;
				}
				return;
			default:
				_log.error("GeoEngine: Unknown block type.");
				result[0] = z;
				result[1] = NSWE_ALL;
				return;
		}
	}

	protected static short makeShort(byte b1, byte b0)
	{
		return (short) (b1 << 8 | b0 & 0xff);
	}

	/**
	 * @param geoPos позиция геодаты
	 *
	 * @return Block Index: 0-255
	 */
	protected static int getBlock(int geoPos)
	{
		return (geoPos >> 3) % 256;
	}

	/**
	 * @param geoPos позиция геодаты
	 *
	 * @return Cell Index: 0-7
	 */
	protected static int getCell(int geoPos)
	{
		return geoPos % 8;
	}

	/**
	 * Создает индекс блока геодаты по заданым координатам блока.
	 *
	 * @param blockX блок по geoX
	 * @param blockY блок по geoY
	 *
	 * @return индекс блока
	 */
	protected static int getBlockIndex(int blockX, int blockY)
	{
		return (blockX << 8) + blockY;
	}

	private static byte sign(int x)
	{
		if(x >= 0)
			return +1;
		return -1;
	}

	/**
	 * Возвращает актуальный блок для текущих геокоординат.<BR>
	 * Является заготовкой для возвращения отдельных блоков с дверьми
	 *
	 * @param geoX геокоордината
	 * @param geoY геокоордината
	 *
	 * @return текущий блок геодаты, или null если нет геодаты.
	 */
	private static byte[] getGeoBlockFromGeoCoords(int geoX, int geoY, int geoIndex, boolean loadIfNotExists)
	{
		if(!Config.ALLOW_GEODATA)
			return null;

		int ix = geoX >> 11;
		int iy = geoY >> 11;

		if(ix < 0 || ix >= World.WORLD_SIZE_X || iy < 0 || iy >= World.WORLD_SIZE_Y)
			return null;

		if(loadIfNotExists) {
			synchronized (geodata) {
				byte[][][][] geodataByRegion = geodata[geoIndex];
				if (geodataByRegion == null)
					return null;

				byte[][] region = geodataByRegion[ix][iy];
				if (region == null) {
					if(geoIndex > 0) {
						region = geodata[0][ix][iy];
						if (region == null)
							return null;

						byte[][] newRegion = new byte[region.length][];
						for (int i = 0; i < region.length; i++)
							newRegion[i] = region[i].clone();

						geodata[geoIndex][ix][iy] = newRegion;

						region = newRegion;
					} else
						return null;
				}
				return region[getBlockIndex(getBlock(geoX), getBlock(geoY))];
			}
		}

		byte[][][][] geodataByRegion = geodata[geoIndex];
		if (geodataByRegion == null)
			return null;

		byte[][] region = geodataByRegion[ix][iy];
		if (region == null) {
			if(geoIndex > 0) {
				region = geodata[0][ix][iy];
				if (region == null)
					return null;
			} else
				return null;
		}

		return region[getBlockIndex(getBlock(geoX), getBlock(geoY))];
	}

	/**
	 * Загрузка геодаты в память
	 */
	public static void load()
	{
		if(!Config.ALLOW_GEODATA)
		{
			_log.info("GeoEngine: Disabled.");
			return;
		}

		_log.info("GeoEngine: Loading Geodata...");

		File geoDir = new File(Config.GEODATA_ROOT, "");

		if(!geoDir.exists() || !geoDir.isDirectory())
			throw new RuntimeException("GeoEngine: Files missing, loading aborted.");

		// WARN this code must be obfuscated
		// After the code obfuscation here must be a lot of junk
		// TODO implements some licence checks

		int count = 0;
		for(int rx = Config.GEO_X_FIRST; rx <= Config.GEO_X_LAST; rx++)
			for(int ry = Config.GEO_Y_FIRST; ry <= Config.GEO_Y_LAST; ry++)
			{
				int blobOff;
				File geoFile;
				if((geoFile = new File(geoDir, String.format("%2d_%2d" + L2S_EXTENSION, rx, ry))).exists())
					blobOff = 4;
				else if((geoFile = new File(geoDir, String.format("%2d_%2d" + L2J_EXTENSION, rx, ry))).exists())
					blobOff = 0;
				else
					continue;

				LoadGeodataFile(rx, ry, geoFile, blobOff);

				count++;
			}

		if(count == 0)
			throw new RuntimeException("GeoEngine: Files missing, loading aborted.");

		_log.info("GeoEngine: Loaded " + count + " map(s), max layers: " + MAX_LAYERS);

		if(Config.COMPACT_GEO)
			compact();
	}

	/**
	 * Загрузка региона геодаты.
	 *
	 * @param rx регион x
	 * @param ry регион y
	 */
	public static boolean LoadGeodataFile(int rx, int ry, File geoFile)
	{
		return LoadGeodataFile(rx, ry, geoFile, 0);
	}

	public static boolean LoadGeodataFile(int rx, int ry, File geoFile, int blobOff)
	{
		int ix = rx - Config.GEO_X_FIRST;
		int iy = ry - Config.GEO_Y_FIRST;

		ByteBuffer buff;

		_log.debug("GeoEngine: Loading: " + geoFile.getName());

		try
		{
			FileChannel roChannel = new RandomAccessFile(geoFile, "r").getChannel();
			int size = (int) roChannel.size() - blobOff;
			buff = ByteBuffer.allocate(size);

			if(blobOff > 0)
				buff.limit(blobOff);

			buff.order(ByteOrder.LITTLE_ENDIAN);
			roChannel.read(buff);
			buff.rewind();

			int checkSum = GeoCrypt.decrypt(blobOff, roChannel, buff);
			if(checkSum != 0 || size < BLOCKS_IN_MAP * 3)
				throw new Error("Invalid geodata : " + geoFile.getName() + " with size " + size + " !");
		}
		catch(IOException e)
		{
			throw new Error(e);
		}

		int index = 0, orgIndex, block = 0, floor = 0;

		byte[][] blocks;

		synchronized(geodata)
		{
			if((blocks = geodata[0][ix][iy]) == null)
				geodata[0][ix][iy] = (blocks = new byte[BLOCKS_IN_MAP][]); // 256 * 256 блоков в регионе геодаты
		}

		// Indexing geo files, so we will know where each block starts
		for(block = 0; block < BLOCKS_IN_MAP; block++)
		{
			byte type = buff.get(index);
			index++;

			byte[] geoBlock;
			switch(type)
			{
				case BLOCKTYPE_FLAT:

					// Создаем блок геодаты
					geoBlock = new byte[2 + 1];

					// Читаем нужные даные с геодаты
					geoBlock[0] = type;
					geoBlock[1] = buff.get(index);
					geoBlock[2] = buff.get(index + 1);

					// Увеличиваем индекс
					index += 2;

					// Добавляем блок геодаты
					blocks[block] = geoBlock;
					break;

				case BLOCKTYPE_COMPLEX:

					// Создаем блок геодаты
					geoBlock = new byte[128 + 1];

					// Читаем данные с геодаты
					geoBlock[0] = type;

					buff.position(index);
					buff.get(geoBlock, 1, 128);

					// Увеличиваем индекс
					index += 128;

					// Добавляем блок геодаты
					blocks[block] = geoBlock;
					break;

				case BLOCKTYPE_MULTILEVEL:
					// Оригинальный индекс
					orgIndex = index;

					// Считаем длину блока геодаты
					for(int b = 0; b < 64; b++)
					{
						byte layers = buff.get(index);
						MAX_LAYERS = Math.max(MAX_LAYERS, layers);
						index += (layers << 1) + 1;
						if(layers > floor)
							floor = layers;
					}

					// Получаем длину
					int diff = index - orgIndex;

					// Создаем массив геодаты
					geoBlock = new byte[diff + 1];

					// Читаем даные с геодаты
					geoBlock[0] = type;

					buff.position(orgIndex);
					buff.get(geoBlock, 1, diff);

					// Добавляем блок геодаты
					blocks[block] = geoBlock;
					break;
				default:
					throw new RuntimeException("Invalid geodata: " + rx + "_" + ry + "!");
			}
		}
		return true;
	}

	public static int createGeoIndex()
	{
		if(!Config.ALLOW_GEODATA)
			return 0;

		synchronized(geodata)
		{
			int geoIndex = -1;

			//Ищем свободный блок
			for(int i = 1; i < geodata.length; i++)
			{
				if(geodata[i] == null)
				{
					geoIndex = i;
					break;
				}
			}

			//Свободного блока нет, создаем новый
			if(geoIndex == -1)
			{
				int oldSize = geodata.length;
				byte[][][][][] resizedGeodata = new byte[(geoIndex = oldSize) + 1000][][][][];
				for(int i = 0; i < oldSize; i++)
					resizedGeodata[i] = geodata[i];

				_log.info("Geodata indexes resized from " + oldSize + " to " + resizedGeodata.length);

				geodata = resizedGeodata;
			}
			geodata[geoIndex] = new byte[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][][];
			return geoIndex;
		}
	}

	/**
	 * Освободить занятый рефлектом индекс геодаты.
	 *
	 * @param geoIndex
	 */
	public static void deleteGeoIndex(int geoIndex)
	{
		if(!Config.ALLOW_GEODATA)
			return;

		//Рефлект без геодаты
		if(geoIndex == 0)
			return;

		synchronized(geodata)
		{
			geodata[geoIndex] = null;
		}
	}

	/**
	 * Преобразовывает FLAT блоки в COMPLEX<br>
	 *
	 * @param geoX		 X координата геодаты
	 * @param geoY		 Y координата геодаты
	 * @param geoIndex индекс блока в регионе
	 */
	private static void copyBlock(int geoX, int geoY, int geoIndex)
	{
		// Получение мировых координат
		int ix = geoX >> 11;
		int iy = geoY >> 11;

		if(ix < 0 || ix >= World.WORLD_SIZE_X || iy < 0 || iy >= World.WORLD_SIZE_Y)
			return;

		byte[][] region = geodata[geoIndex][ix][iy];
		if(region == null)
		{
			//_log.info("Geodata at null region while copy block [" + ix + "][" + iy + "]");
			return;
		}

		// Получение индекса блока
		int blockIndex = getBlockIndex(getBlock(geoX), getBlock(geoY));

		byte[] block = region[blockIndex];
		byte blockType = block[0];

		switch(blockType)
		{
			case BLOCKTYPE_FLAT:
				short height = makeShort(block[2], block[1]);
				height &= 0x0fff0;
				height <<= 1;
				height |= NORTH;
				height |= SOUTH;
				height |= WEST;
				height |= EAST;
				byte[] newblock = new byte[129];
				newblock[0] = BLOCKTYPE_COMPLEX;
				for(int i = 1; i < 129; i += 2)
				{
					newblock[i + 1] = (byte) (height >> 8);
					newblock[i] = (byte) (height & 0x00ff);
				}
				region[blockIndex] = newblock;
				break;
		}
	}

	private static boolean checkControlZ(int minZ, int maxZ, int geoZ)
	{
		return minZ <= geoZ && geoZ <= maxZ || Math.abs((minZ + maxZ) / 2 - geoZ) <= DOOR_MAX_Z_DIFF;
	}

	private static boolean checkCellInControl(int geoX, int geoY, Shape shape)
	{
		int worldX = getWorldX(geoX);
		int worldY = getWorldY(geoY);
		return shape.isOnPerimeter(worldX, worldY) || shape.isInside(worldX, worldY);
	}

	public static boolean returnGeoControl(final GeoControl control)
	{
		if(!Config.ALLOW_GEODATA)
			return false;

		synchronized (geodata) {
			final int geoIndex = control.getGeoControlIndex();
			if(geoIndex == 0) {
				_log.warn("GeoEngine: Attempt to return geo control with 0 geoControlIndex!");
				Thread.dumpStack();
				return false;
			}

			final TIntObjectMap<ByteObjectPair<CeilGeoControlType>> around = control.getGeoAround();
			if (around == null) {
				_log.warn("GeoEngine: Attempt to return geo control without applyed geo control!");
				Thread.dumpStack();
				return false;
			}

			final Shape shape = control.getGeoShape();

			boolean result = false;

			short height;
			byte old_nswe;

			for (int geoXY : around.keys()) {
				int geoX = getGeoXFromHash(geoXY);
				int geoY = getGeoYFromHash(geoXY);

				byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, false);
				if (block == null)
					continue;

				List<GeoControl> geoControls = null;
				int hashCode = 0;

				int ix = geoX >> 11;
				int iy = geoY >> 11;
				if (ix >= 0 && ix < World.WORLD_SIZE_X && iy >= 0 && iy < World.WORLD_SIZE_Y) {
					hashCode = makeRegionHashCode(ix, iy, geoIndex);
					geoControls = _activeGeoControls.get(hashCode);
				}

				int cellX = getCell(geoX);
				int cellY = getCell(geoY);

				ByteObjectPair<CeilGeoControlType> aroundInfo;

				int index = 0;
				byte blockType = block[index];

				index++;

				boolean success = false;
				switch (blockType) {
					case BLOCKTYPE_COMPLEX:
						index += (cellX << 3) + cellY << 1;

						// Получаем высоту клетки
						height = makeShort(block[index + 1], block[index]);
						old_nswe = (byte) (height & 0x0F);
						height &= 0xfff0;
						height >>= 1;

						aroundInfo = around.get(geoXY);

						if (aroundInfo.getValue() == CeilGeoControlType.INSIDE) {
							int defaultLowerHeight = NgetLowerHeight(geoX, geoY, height, 0);
							int defaultUpperHeight = NgetUpperHeight(geoX, geoY, height, 0);
							height = (short) defaultLowerHeight;
							if (geoControls != null) {
								for (GeoControl tempControl : geoControls) {
									if (tempControl == control)
										continue;

									Shape tempShape = tempControl.getGeoShape();
									if (tempShape == null)
										continue;

									TIntObjectMap<ByteObjectPair<CeilGeoControlType>> tempGeoAround = tempControl.getGeoAround();
									if (tempGeoAround == null)
										continue;

									ByteObjectPair<CeilGeoControlType> tempAroundInfo = tempGeoAround.get(geoXY);
									if (tempAroundInfo == null)
										continue;

									if (tempAroundInfo.getValue() != CeilGeoControlType.INSIDE)
										continue;

									height = (short) Math.max(height, (Math.min(Math.min(defaultLowerHeight + DOOR_MAX_Z_DIFF, tempShape.getZmax()), defaultUpperHeight - 1)));
								}
							}
						}

						// around
						height <<= 1;
						height &= 0xfff0;
						height |= old_nswe;
						height |= aroundInfo.getKey();

						// Записываем высоту в массив
						block[index + 1] = (byte) (height >> 8);
						block[index] = (byte) (height & 0x00ff);
						success = true;
						break;
					case BLOCKTYPE_MULTILEVEL:
						// Последний валидный индекс для двери
						int neededIndex = -1;

						// Далее следует стандартный механизм получения высоты
						int offset = (cellX << 3) + cellY;
						while (offset > 0) {
							byte lc = block[index];
							index += (lc << 1) + 1;
							offset--;
						}
						byte layers = block[index];
						index++;
						if (layers <= 0 || layers > MAX_LAYERS)
							break;
						short temph = Short.MIN_VALUE;
						old_nswe = NSWE_ALL;
						while (layers > 0) {
							height = makeShort(block[index + 1], block[index]);
							byte tmp_nswe = (byte) (height & 0x0F);
							height &= 0xfff0;
							height >>= 1;
							int z_diff_last = Math.abs(shape.getZmin() - temph);
							int z_diff_curr = Math.abs(shape.getZmin() - height);
							if (z_diff_last > z_diff_curr) {
								old_nswe = tmp_nswe;
								temph = height;
								neededIndex = index;
							}
							layers--;
							index += 2;
						}

						aroundInfo = around.get(geoXY);

						if (aroundInfo.getValue() == CeilGeoControlType.INSIDE) {
							int defaultLowerHeight = NgetLowerHeight(geoX, geoY, temph, 0);
							int defaultUpperHeight = NgetUpperHeight(geoX, geoY, temph, 0);
							temph = (short) defaultLowerHeight;
							if (geoControls != null) {
								for (GeoControl tempControl : geoControls) {
									if (tempControl == control)
										continue;

									Shape tempShape = tempControl.getGeoShape();
									if (tempShape == null)
										continue;

									TIntObjectMap<ByteObjectPair<CeilGeoControlType>> tempGeoAround = tempControl.getGeoAround();
									if (tempGeoAround == null)
										continue;

									ByteObjectPair<CeilGeoControlType> tempAroundInfo = tempGeoAround.get(geoXY);
									if (tempAroundInfo == null)
										continue;

									if (tempAroundInfo.getValue() != CeilGeoControlType.INSIDE)
										continue;

									temph = (short) Math.max(temph, (Math.min(Math.min(defaultLowerHeight + DOOR_MAX_Z_DIFF, tempShape.getZmax()), defaultUpperHeight - 1)));
								}
							}
						}

						// around
						temph <<= 1;
						temph &= 0xfff0;
						temph |= old_nswe;
						temph |= aroundInfo.getKey();

						// записываем высоту
						block[neededIndex + 1] = (byte) (temph >> 8);
						block[neededIndex] = (byte) (temph & 0x00ff);
						success = true;
						break;
				}

				if (success) {
					if (geoControls != null) {
						geoControls.remove(control);
						if (geoControls.isEmpty()) {
							_activeGeoControls.remove(hashCode);
							if (geoIndex != 0)
								geodata[geoIndex][ix][iy] = null;
						}
					}
					result = true;
				}
			}
			return result;
		}
	}

	public static boolean applyGeoControl(final GeoControl control, final int geoIndex)
	{
		if(!Config.ALLOW_GEODATA)
			return false;

		if(geoIndex == 0) {
			_log.warn("GeoEngine: Attempt to apply geo control with 0 geoIndex!");
			Thread.dumpStack();
			return false;
		}

		synchronized (geodata) {
			final Shape shape = control.getGeoShape();
			if (shape == null) {
				_log.warn("GeoEngine: no shape for geo control: " + control);
				return false;
			}

			TIntObjectMap<ByteObjectPair<CeilGeoControlType>> around = control.getGeoAround();

			boolean first_time = around == null;

			if (around == null) {
				around = new TIntObjectHashMap<>();

				TIntSet around_blocks = new TIntHashSet();
				int minX = getGeoX(shape.getXmin());
				int maxX = getGeoX(shape.getXmax());
				int minY = getGeoY(shape.getYmin());
				int maxY = getGeoY(shape.getYmax());
				for (int tmpX = minX; tmpX <= maxX; tmpX++) {
					for (int tmpY = minY; tmpY <= maxY; tmpY++) {
						if (checkCellInControl(tmpX, tmpY, shape))
							around_blocks.add(getGeoXYHash(tmpX, tmpY));
					}
				}

				for (int geoXY : around_blocks.toArray()) {
					if (!control.isHollowGeo())
						around.put(geoXY, new ByteObjectPairImpl<>(NSWE_ALL, CeilGeoControlType.INSIDE));

					int geoX = getGeoXFromHash(geoXY);
					int geoY = getGeoYFromHash(geoXY);
					int aroundN_geoXY = getGeoXYHash(geoX, geoY - 1); // close S
					int aroundS_geoXY = getGeoXYHash(geoX, geoY + 1); // close N
					int aroundW_geoXY = getGeoXYHash(geoX - 1, geoY); // close E
					int aroundE_geoXY = getGeoXYHash(geoX + 1, geoY); // close W

					ByteObjectPair<CeilGeoControlType> _aroundInfo;
					byte _nswe;
					if (!around_blocks.contains(aroundN_geoXY)) {
						if (around.containsKey(aroundN_geoXY)) {
							_aroundInfo = around.remove(aroundN_geoXY);
							_nswe = _aroundInfo.getKey();
						} else
							_nswe = NSWE_NONE;
						_nswe |= SOUTH;
						around.put(aroundN_geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));

						if (control.isHollowGeo()) {
							if (around.containsKey(geoXY)) {
								_aroundInfo = around.remove(geoXY);
								_nswe = _aroundInfo.getKey();
							} else
								_nswe = NSWE_NONE;
							_nswe |= NORTH;
							around.put(geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));
						}
					}
					if (!around_blocks.contains(aroundS_geoXY)) {
						if (around.containsKey(aroundS_geoXY)) {
							_aroundInfo = around.remove(aroundS_geoXY);
							_nswe = _aroundInfo.getKey();
						} else
							_nswe = NSWE_NONE;
						_nswe |= NORTH;
						around.put(aroundS_geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));

						if (control.isHollowGeo()) {
							if (around.containsKey(geoXY)) {
								_aroundInfo = around.remove(geoXY);
								_nswe = _aroundInfo.getKey();
							} else
								_nswe = NSWE_NONE;
							_nswe |= SOUTH;
							around.put(geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));
						}
					}
					if (!around_blocks.contains(aroundW_geoXY)) {
						if (around.containsKey(aroundW_geoXY)) {
							_aroundInfo = around.remove(aroundW_geoXY);
							_nswe = _aroundInfo.getKey();
						} else
							_nswe = NSWE_NONE;
						_nswe |= EAST;
						around.put(aroundW_geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));

						if (control.isHollowGeo()) {
							if (around.containsKey(geoXY)) {
								_aroundInfo = around.remove(geoXY);
								_nswe = _aroundInfo.getKey();
							} else
								_nswe = NSWE_NONE;
							_nswe |= WEST;
							around.put(geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));
						}
					}
					if (!around_blocks.contains(aroundE_geoXY)) {
						if (around.containsKey(aroundE_geoXY)) {
							_aroundInfo = around.remove(aroundE_geoXY);
							_nswe = _aroundInfo.getKey();
						} else
							_nswe = NSWE_NONE;
						_nswe |= WEST;
						around.put(aroundE_geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));

						if (control.isHollowGeo()) {
							if (around.containsKey(geoXY)) {
								_aroundInfo = around.remove(geoXY);
								_nswe = _aroundInfo.getKey();
							} else
								_nswe = NSWE_NONE;
							_nswe |= EAST;
							around.put(geoXY, new ByteObjectPairImpl<>(_nswe, CeilGeoControlType.PERIMETER));
						}
					}
				}
				around_blocks.clear();
				control.setGeoAround(around);
			}

			boolean result = false;

			short height;
			ByteObjectPair<CeilGeoControlType> aroundInfo;
			byte old_nswe, close_nswe;

			final int[] around_keys = around.keys();
			for (int geoXY : around_keys) {
				int geoX = getGeoXFromHash(geoXY);
				int geoY = getGeoYFromHash(geoXY);

				byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, true);
				if (block == null)
					continue;

				// Попытка скопировать блок геодаты, если уже существует, то не скопируется
				if (first_time)
					copyBlock(geoX, geoY, geoIndex);

				int cellX = getCell(geoX);
				int cellY = getCell(geoY);

				int index = 0;
				byte blockType = block[index];

				index++;

				boolean success = false;
				switch (blockType) {
					case BLOCKTYPE_COMPLEX:
						index += (cellX << 3) + cellY << 1;

						// Получаем высоту клетки
						height = makeShort(block[index + 1], block[index]);
						old_nswe = (byte) (height & 0x0F);
						height &= 0xfff0;
						height >>= 1;

						aroundInfo = around.get(geoXY);

						if (aroundInfo.getValue() == CeilGeoControlType.INSIDE) {
							int defaultLowerHeight = NgetLowerHeight(geoX, geoY, height, 0);
							int defaultUpperHeight = NgetUpperHeight(geoX, geoY, height, 0);
							height = (short) Math.max(height, (Math.min(Math.min(defaultLowerHeight + DOOR_MAX_Z_DIFF, shape.getZmax()), defaultUpperHeight - 1)));
							around.put(geoXY, new ByteObjectPairImpl<CeilGeoControlType>(NSWE_NONE, CeilGeoControlType.INSIDE));

							// around
							height <<= 1;
							height &= 0xfff0;
							height |= old_nswe;
						} else if (aroundInfo.getValue() == CeilGeoControlType.PERIMETER) {
							if (first_time) {
								around.remove(geoXY);
								close_nswe = aroundInfo.getKey();
								// подходящий слой не найден
								if (!checkControlZ(shape.getZmin(), shape.getZmax(), height))
									break;
								close_nswe &= old_nswe;
								around.put(geoXY, new ByteObjectPairImpl<CeilGeoControlType>(close_nswe, CeilGeoControlType.PERIMETER));
							} else {
								close_nswe = aroundInfo.getKey();
							}

							// around
							height <<= 1;
							height &= 0xfff0;
							height |= old_nswe;
							height &= ~close_nswe;
						} else
							continue;

						// Записываем высоту в массив
						block[index + 1] = (byte) (height >> 8);
						block[index] = (byte) (height & 0x00ff);
						success = true;
						break;
					case BLOCKTYPE_MULTILEVEL:
						// Последний валидный индекс для двери
						int neededIndex = -1;

						// Далее следует стандартный механизм получения высоты
						int offset = (cellX << 3) + cellY;
						while (offset > 0) {
							byte lc = block[index];
							index += (lc << 1) + 1;
							offset--;
						}
						byte layers = block[index];
						index++;
						if (layers <= 0 || layers > MAX_LAYERS)
							break;
						short temph = Short.MIN_VALUE;
						old_nswe = NSWE_ALL;
						while (layers > 0) {
							height = makeShort(block[index + 1], block[index]);
							byte tmp_nswe = (byte) (height & 0x0F);
							height &= 0xfff0;
							height >>= 1;
							int z_diff_last = Math.abs(shape.getZmin() - temph);
							int z_diff_curr = Math.abs(shape.getZmin() - height);
							if (z_diff_last > z_diff_curr) {
								old_nswe = tmp_nswe;
								temph = height;
								neededIndex = index;
							}
							layers--;
							index += 2;
						}

						aroundInfo = around.get(geoXY);

						if (aroundInfo.getValue() == CeilGeoControlType.INSIDE) {
							int defaultLowerHeight = NgetLowerHeight(geoX, geoY, temph, 0);
							int defaultUpperHeight = NgetUpperHeight(geoX, geoY, temph, 0);
							temph = (short) Math.max(temph, (Math.min(Math.min(defaultLowerHeight + DOOR_MAX_Z_DIFF, shape.getZmax()), defaultUpperHeight - 1)));
							around.put(geoXY, new ByteObjectPairImpl<CeilGeoControlType>(NSWE_NONE, CeilGeoControlType.INSIDE));

							// around
							temph <<= 1;
							temph &= 0xfff0;
							temph |= old_nswe;
						} else if (aroundInfo.getValue() == CeilGeoControlType.PERIMETER) {
							if (first_time) {
								around.remove(geoXY);
								close_nswe = aroundInfo.getKey();
								// подходящий слой не найден
								if (temph == Short.MIN_VALUE || !checkControlZ(shape.getZmin(), shape.getZmax(), temph))
									break;

								close_nswe &= old_nswe;
								around.put(geoXY, new ByteObjectPairImpl<CeilGeoControlType>(close_nswe, aroundInfo.getValue()));
							} else {
								close_nswe = aroundInfo.getKey();
							}

							// around
							temph <<= 1;
							temph &= 0xfff0;
							temph |= old_nswe;
							temph &= ~close_nswe;
						} else
							continue;

						// записываем высоту
						block[neededIndex + 1] = (byte) (temph >> 8);
						block[neededIndex] = (byte) (temph & 0x00ff);
						success = true;
						break;
				}

				if (success) {
					int ix = geoX >> 11;
					int iy = geoY >> 11;
					if (ix >= 0 && ix < World.WORLD_SIZE_X && iy >= 0 && iy < World.WORLD_SIZE_Y) {
						int hashCode = makeRegionHashCode(ix, iy, geoIndex);
						List<GeoControl> geoControls = _activeGeoControls.get(hashCode);
						if (geoControls == null) {
							geoControls = new CopyOnWriteArrayList<>();
							_activeGeoControls.put(hashCode, geoControls);
						}
						geoControls.add(control);
					}
					result = true;
				}
			}
			return result;
		}
	}

	private static int makeRegionHashCode(int x, int y, int index)
	{
		return (x * 100 + y) * 100000 + index;
	}

	/**
	 * загружает заранее сгенерированые карты соовпадений в блоках и благодаря им оптимизирует размещение геодаты в памяти
	 * @return количество оптимизированых блоков
	 */
	public static void compact()
	{
		long total = 0, optimized = 0;
		BlockLink[] links;
		byte[][] link_region;

		for(int mapX = 0; mapX < World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < World.WORLD_SIZE_Y; mapY++)
			{
				if(geodata[0][mapX][mapY] == null)
					continue;
				total += BLOCKS_IN_MAP;
				links = GeoOptimizer.loadBlockMatches("geodata/matches/" + (mapX + Config.GEO_X_FIRST) + "_" + (mapY + Config.GEO_Y_FIRST) + ".matches");
				if(links == null)
					continue;
				for(int i = 0; i < links.length; i++)
				{
					link_region = geodata[0][links[i].linkMapX][links[i].linkMapY];
					if(link_region == null)
						continue;
					link_region[links[i].linkBlockIndex] = geodata[0][mapX][mapY][links[i].blockIndex];
					optimized++;
				}
			}

		_log.info(String.format("GeoEngine: - Compacted %d of %d blocks...", optimized, total));
	}

	/**
	 * сравнение двух байт-массивов
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static boolean equalsData(byte[] a1, byte[] a2)
	{
		if(a1.length != a2.length)
			return false;
		for(int i = 0; i < a1.length; i++)
			if(a1[i] != a2[i])
				return false;
		return true;
	}

	/**
	 * сравнение двух блоков геодаты
	 * @param mapX1
	 * @param mapY1
	 * @param blockIndex1
	 * @param mapX2
	 * @param mapY2
	 * @param blockIndex2
	 * @return
	 */
	public static boolean compareGeoBlocks(int mapX1, int mapY1, int blockIndex1, int mapX2, int mapY2, int blockIndex2)
	{
		return equalsData(geodata[0][mapX1][mapY1][blockIndex1], geodata[0][mapX2][mapY2][blockIndex2]);
	}

	private static void initChecksums()
	{
		_log.info("GeoEngine: - Generating Checksums...");
		new File(Config.GEODATA_ROOT, "checksum").mkdirs();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		GeoOptimizer.checkSums = new int[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][];
		for(int mapX = 0; mapX < World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < World.WORLD_SIZE_Y; mapY++)
				if(geodata[0][mapX][mapY] != null)
					executor.execute((Runnable) new GeoOptimizer.CheckSumLoader(mapX, mapY, geodata[0][mapX][mapY]));
		try
		{
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		}
		catch(InterruptedException e)
		{
			_log.error("", e);
		}
	}

	private static void initBlockMatches(int maxScanRegions)
	{
		_log.info("GeoEngine: Generating Block Matches...");
		new File(Config.GEODATA_ROOT, "matches").mkdirs();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(int mapX = 0; mapX < World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < World.WORLD_SIZE_Y; mapY++)
				if(geodata[0][mapX][mapY] != null && GeoOptimizer.checkSums != null && GeoOptimizer.checkSums[mapX][mapY] != null)
					executor.execute((Runnable) new GeoOptimizer.GeoBlocksMatchFinder(mapX, mapY, maxScanRegions));
		try
		{
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		}
		catch(InterruptedException e)
		{
			_log.error("", e);
		}
	}

	public static void deleteChecksumFiles()
	{
		for(int mapX = 0; mapX < World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < World.WORLD_SIZE_Y; mapY++)
			{
				if(geodata[0][mapX][mapY] == null)
					continue;
				new File(Config.GEODATA_ROOT, "checksum/" + (mapX + Config.GEO_X_FIRST) + "_" + (mapY + Config.GEO_Y_FIRST) + ".crc").delete();
			}
	}

	public static void genBlockMatches(int maxScanRegions)
	{
		initChecksums();
		initBlockMatches(maxScanRegions);
	}

	public static void unload()
	{
		for(int index = 0; index < geodata.length; index++)
		{
			for(int mapX = 0; mapX < World.WORLD_SIZE_X; mapX++)
			{
				for(int mapY = 0; mapY < World.WORLD_SIZE_Y; mapY++)
					geodata[index][mapX][mapY] = null;
			}
		}
	}

	public static int getGeoX(int worldX)
	{
		return getGeoDistance(worldX - World.MAP_MIN_X);
	}

	public static int getGeoY(int worldY)
	{
		return getGeoDistance(worldY - World.MAP_MIN_Y);
	}

	public static int getGeoDistance(int distance)
	{
		return distance >> 4;
	}

	public static int getGeoXYHash(int geoX, int geoY)
	{
		return geoX | (geoY << 16);
	}

	public static int getGeoXFromHash(int hash)
	{
		final int mask = 0b1111111111111111;
		return mask & hash;
	}

	public static int getGeoYFromHash(int hash)
	{
		final int mask = 0b1111111111111111;
		return mask & hash >>> 16;
	}

	/**
	 * A single geodata position represents 16x16 positions in the game world.<br>
	 * That means we add 8 to the calculated world position, to always return the<br>
	 * middle of the 16x16 world sqaure the geo position represents.
	 */
	public static int getWorldX(int geoX)
	{
		return getWorldDistance(geoX) + World.MAP_MIN_X + 8;
	}

	public static int getWorldY(int geoY)
	{
		return getWorldDistance(geoY) + World.MAP_MIN_Y + 8;
	}

	public static int getWorldDistance(int geoDistance)
	{
		return geoDistance << 4;
	}
}