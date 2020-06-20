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
package handlers.mission;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.OnElementalSpiritUpgrade;
import org.l2j.gameserver.model.events.impl.character.player.OnElementalSpiritLearn;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public class SpiritMissionHandler extends AbstractMissionHandler {

    private final ElementalType type;

    private SpiritMissionHandler(MissionDataHolder holder) {
        super(holder);
        type = getHolder().getParams().getEnum("element", ElementalType.class, ElementalType.NONE);
    }

    @Override
    public void init() {
        MissionKind kind = getHolder().getParams().getEnum("kind", MissionKind.class, null);

        if(MissionKind.EVOLVE == kind) {
            Listeners.players().addListener(new ConsumerEventListener(this, EventType.ON_ELEMENTAL_SPIRIT_UPGRADE, (Consumer<OnElementalSpiritUpgrade>) this::onElementalSpiritUpgrade, this));
        } else if(MissionKind.LEARN == kind)  {
            Listeners.players().addListener(new ConsumerEventListener(this, EventType.ON_ELEMENTAL_SPIRIT_LEARN, (Consumer<OnElementalSpiritLearn>) this::onElementalSpiritLearn, this));
        }
    }

    private void onElementalSpiritLearn(OnElementalSpiritLearn event) {
        var missionData = getPlayerEntry(event.getPlayer(), true);
        missionData.setProgress(1);
        missionData.setStatus(MissionStatus.AVAILABLE);
        notifyAvailablesReward(event.getPlayer());
        storePlayerEntry(missionData);
    }

    private void onElementalSpiritUpgrade(OnElementalSpiritUpgrade event) {
        final var spirit = event.getSpirit();

        if(ElementalType.of(spirit.getType()) != type) {
            return;
        }

        var missionData = getPlayerEntry(event.getPlayer(), true);
        missionData.setProgress(spirit.getStage());
        if(missionData.getProgress() >= getRequiredCompletion()) {
            missionData.setStatus(MissionStatus.AVAILABLE);
            notifyAvailablesReward(event.getPlayer());
        }
        storePlayerEntry(missionData);
    }


    private enum MissionKind {
        LEARN,
        EVOLVE
    }

    public static class Factory implements MissionHandlerFactory {

        @Override
        public AbstractMissionHandler create(MissionDataHolder data) {
            return new SpiritMissionHandler(data);
        }

        @Override
        public String handlerName() {
            return "spirit";
        }
    }
}
