package handlers.dailymissionhandlers;

import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnElementalSpiritUpgrade;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

public class SpiritDailyMissionHandler extends AbstractDailyMissionHandler {

    private MissionKind kind;
    private final ElementalType type;

    public SpiritDailyMissionHandler(DailyMissionDataHolder holder) {
        super(holder);
        type = getHolder().getParams().getEnum("element", ElementalType.class, ElementalType.NONE);
    }

    @Override
    public void init() {
        kind = getHolder().getParams().getEnum("kind", MissionKind.class, null);

        if(MissionKind.EVOLVE == kind) {
            Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_ELEMENTAL_SPIRIT_UPGRADE, (Consumer<OnElementalSpiritUpgrade>) this::onElementalSpiritUpgrade, this));
        }
    }

    private void onElementalSpiritUpgrade(OnElementalSpiritUpgrade event) {
        final var spirit = event.getSpirit();

        if(ElementalType.of(spirit.getType()) != type) {
            return;
        }

        var missionData = getPlayerEntry(event.getActivePlayer(), true);
        missionData.setProgress(spirit.getStage());
        if(missionData.getProgress() >= getRequiredCompletion()) {
            missionData.setStatus(DailyMissionStatus.AVAILABLE);
            notifyAvailablesReward(event.getActivePlayer());
        }
        storePlayerEntry(missionData);
    }


    private enum MissionKind {
        LEARN,
        EVOLVE
    }
}
