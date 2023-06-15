package org.l2j.gameserver.api.classes;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUI;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author JoeAlisson
 */
public class ClassChangeAPI {

    private static final ClassChangeCheck check = new ClassChangeCheck();


    public static void showClassChangeNotification(Player player) {
        if (canAdvanceToNextClass(player)) {
            player.sendPacket(ExRequestClassChangeUI.STATIC_PACKET);
            check.add(player);
        }
    }

    private static boolean canAdvanceToNextClass(Player player) {
        return (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && player.getLevel() >= 20)
                || (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) && player.getLevel() >= 40)
                || (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && player.getLevel() >= 76);
    }


    private static class ClassChangeCheck {
        private ScheduledFuture<?> task;
        private final Set<Player> players = ConcurrentHashMap.newKeySet();

        public void add(Player player) {
            players.add(player);
            if (task == null || task.isDone()) {
                task = null;
                task = ThreadPool.scheduleAtFixedDelay(this::checkAvailableClassChange, 1, 1, TimeUnit.MINUTES);
            }
        }

        private void checkAvailableClassChange() {
            if (players.isEmpty()) {
                task.cancel(true);
                task = null;
                return;
            }

            var iterator = players.iterator();
            while (iterator.hasNext()) {
                var player = iterator.next();
                if (canAdvanceToNextClass(player)) {
                    player.sendPacket(ExRequestClassChangeUI.STATIC_PACKET);
                } else {
                    iterator.remove();
                }
            }
        }

    }
}
