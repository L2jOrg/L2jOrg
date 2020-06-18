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
package org.l2j.gameserver.model.residences;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.ResidenceDAO;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.world.zone.type.ResidenceZone;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.*;

/**
 * @author xban1x
 * @author JoeAlisson
 */
public abstract class AbstractResidence extends ListenersContainer implements INamable {

    private final IntMap<ResidenceFunctionData> functions = new CHashIntMap<>();
    private final int id;
    private String name;
    private ResidenceZone zone = null;
    private List<SkillLearn> residentialSkills;

    public AbstractResidence(int id) {
        this.id = id;
        initResidentialSkills();
    }

    protected void initResidentialSkills() {
        residentialSkills = SkillTreesData.getInstance().getAvailableResidentialSkills(id);
    }

    public void giveResidentialSkills(Player player) {
        if (!isNullOrEmpty(residentialSkills)) {
            var socialClass = player.getPledgeClass() + 1;
            residentialSkills.stream().filter(s -> checkSocialClass(socialClass, s)).forEach(s ->  player.addSkill(s.getSkill()));
        }
    }

    private boolean checkSocialClass(int socialClass, SkillLearn skillLearn) {
        var skillSocialClass =  skillLearn.getSocialClass();
        return isNull(skillSocialClass) || socialClass >= skillSocialClass.ordinal();
    }

    public void removeResidentialSkills(Player player) {
        if(!isNullOrEmpty(residentialSkills)) {
            residentialSkills.forEach(skill -> player.removeSkill(skill.getSkillId(), false));
        }
    }

    /**
     * Initializes all available functions for the current residence
     */
    protected void initFunctions() {
        getDAO(ResidenceDAO.class).findFunctionsByResidence(id).forEach(function -> {
            function.initResidence(this);
            if(function.getExpiration() <= System.currentTimeMillis() && !function.reactivate()) {
                removeFunction(function);
            } else {
                functions.put(function.getId(), function);
            }
        });
    }

    public void addFunction(int id, int level) {
        addFunction(new ResidenceFunctionData(id, level, this));
    }

    /**
     * Adds new function and removes old if matches same id
     */
    private void addFunction(ResidenceFunctionData function) {
        var functionId = function.getId();
        getDAO(ResidenceDAO.class).saveFunction(functionId, function.getLevel(), function.getExpiration(), id);

        final ResidenceFunctionData old = functions.remove(functionId);
        if (old != null)
            removeFunction(old);

        functions.put(functionId, function);
    }

    public void removeFunction(ResidenceFunctionData function) {
        getDAO(ResidenceDAO.class).deleteFunction(function.getId(), id);
        function.cancelExpiration();
        functions.remove(function.getId());
    }

    public void removeFunctions() {
        getDAO(ResidenceDAO.class).deleteFunctionsByResidence(id);
        functions.values().forEach(ResidenceFunctionData::cancelExpiration);
        functions.clear();
    }

    /**
     * @return {@code true} if function is available, {@code false} otherwise
     */
    public boolean hasFunction(ResidenceFunctionType type) {
        return functions.values().stream().map(ResidenceFunctionData::getTemplate).anyMatch(func -> func.getType() == type);
    }

    /**
     * @return the function template by type, null if not available
     */
    public ResidenceFunctionData getFunction(ResidenceFunctionType type) {
        return functions.values().stream().filter(func -> func.getType() == type).findFirst().orElse(null);
    }

    /**
     * @return the function by id and level, null if not available
     */
    public ResidenceFunctionData getFunction(int id, int level) {
        return computeIfNonNull(functions.get(id), f -> f.getLevel() == level ? f : null);
    }

    /**
     * @return the function by id, null if not available
     */
    public ResidenceFunctionData getFunction(int id) {
        return functions.get(id);
    }

    /**
     * @return level of function, 0 if not available
     */
    public int getFunctionLevel(ResidenceFunctionType type) {
        return zeroIfNullOrElse(getFunction(type), ResidenceFunctionData::getLevel);
    }

    public final int getId() {
        return id;
    }

    @Override
    public final String getName() {
        return name;
    }

    // TODO: Remove it later when both castles and forts are loaded from same table.
    public final void setName(String name) {
        this.name = name;
    }

    public ResidenceZone getResidenceZone() {
        return zone;
    }

    protected void setResidenceZone(ResidenceZone zone) {
        this.zone = zone;
    }

    /**
     * @return all avaible functions
     */
    public Collection<ResidenceFunctionData> getFunctions() {
        return functions.values();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AbstractResidence) && (((AbstractResidence) obj).getId() == getId());
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

    protected abstract void load();

    protected abstract void initResidenceZone();

    public abstract int getOwnerId();
}
