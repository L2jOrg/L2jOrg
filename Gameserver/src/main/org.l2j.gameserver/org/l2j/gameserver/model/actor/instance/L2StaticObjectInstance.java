/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ai.L2CharacterAI;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.stat.StaticObjStat;
import org.l2j.gameserver.model.actor.status.StaticObjStatus;
import org.l2j.gameserver.model.actor.templates.L2CharTemplate;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.ShowTownMap;
import org.l2j.gameserver.network.serverpackets.StaticObject;

/**
 * Static Object instance.
 *
 * @author godson
 */
public final class L2StaticObjectInstance extends Creature {
    /**
     * The interaction distance of the L2StaticObjectInstance
     */
    public static final int INTERACTION_DISTANCE = 150;

    private final int _staticObjectId;
    private int _meshIndex = 0; // 0 - static objects, alternate static objects
    private int _type = -1; // 0 - map signs, 1 - throne , 2 - arena signs
    private ShowTownMap _map;

    /**
     * @param template
     * @param staticId
     */
    public L2StaticObjectInstance(L2CharTemplate template, int staticId) {
        super(template);
        setInstanceType(InstanceType.L2StaticObjectInstance);
        _staticObjectId = staticId;
    }

    @Override
    protected L2CharacterAI initAI() {
        return null;
    }

    /**
     * Gets the static object ID.
     *
     * @return the static object ID
     */
    @Override
    public int getId() {
        return _staticObjectId;
    }

    @Override
    public final StaticObjStat getStat() {
        return (StaticObjStat) super.getStat();
    }

    @Override
    public void initCharStat() {
        setStat(new StaticObjStat(this));
    }

    @Override
    public final StaticObjStatus getStatus() {
        return (StaticObjStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new StaticObjStatus(this));
    }

    public int getType() {
        return _type;
    }

    public void setType(int type) {
        _type = type;
    }

    public void setMap(String texture, int x, int y) {
        _map = new ShowTownMap("town_map." + texture, x, y);
    }

    public ShowTownMap getMap() {
        return _map;
    }

    @Override
    public final int getLevel() {
        return 1;
    }

    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    /**
     * <B><U> Values </U> :</B>
     * <ul>
     * <li>default textures : 0</li>
     * <li>alternate textures : 1</li>
     * </ul>
     *
     * @return the meshIndex of the object
     */
    public int getMeshIndex() {
        return _meshIndex;
    }

    /**
     * Set the meshIndex of the object.<br>
     * <B><U> Values </U> :</B>
     * <ul>
     * <li>default textures : 0</li>
     * <li>alternate textures : 1</li>
     * </ul>
     *
     * @param meshIndex
     */
    public void setMeshIndex(int meshIndex) {
        _meshIndex = meshIndex;
        broadcastPacket(new StaticObject(this));
    }

    @Override
    public void sendInfo(Player activeChar) {
        activeChar.sendPacket(new StaticObject(this));
    }

    @Override
    public void moveToLocation(int x, int y, int z, int offset) {
    }

    @Override
    public void stopMove(Location loc) {
    }

    @Override
    public void doAutoAttack(Creature target) {
    }

    @Override
    public void doCast(Skill skill) {
    }
}
