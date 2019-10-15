package handlers.dailymissionhandlers;

import org.l2j.gameserver.data.database.elemental.ElementalType;
import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.model.dailymission.MissionDataHolder;
import org.l2j.gameserver.model.dailymission.MissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnElementalSpiritUpgrade;
import org.l2j.gameserver.model.events.impl.character.player.OnElementalSpiritLearn;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

public class SpiritMissionHandler extends AbstractMissionHandler {

    private final ElementalType type;

    public SpiritMissionHandler(MissionDataHolder holder) {
        super(holder);
        type = getHolder().getParams().getEnum("element", ElementalType.class, ElementalType.NONE);
    }

    @Override
    public void init() {
        MissionKind kind = getHolder().getParams().getEnum("kind", MissionKind.class, null);

        if(MissionKind.EVOLVE == kind) {
            Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_ELEMENTAL_SPIRIT_UPGRADE, (Consumer<OnElementalSpiritUpgrade>) this::onElementalSpiritUpgrade, this));
        } else if(MissionKind.LEARN == kind)  {
            Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_ELEMENTAL_SPIRIT_LEARN, (Consumer<OnElementalSpiritLearn>) this::onElementalSpiritLearn, this));
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
}
