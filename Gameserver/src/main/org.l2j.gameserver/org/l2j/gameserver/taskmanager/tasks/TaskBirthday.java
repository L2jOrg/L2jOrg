package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.model.item.container.Mail;
import org.l2j.gameserver.taskmanager.Task;
import org.l2j.gameserver.taskmanager.TaskManager;
import org.l2j.gameserver.taskmanager.TaskManager.ExecutableTask;
import org.l2j.gameserver.taskmanager.TaskType;
import org.l2j.gameserver.util.GameUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Nyaran
 */
public class TaskBirthday extends Task {
    private static final String NAME = "birthday";
    private static final Calendar _today = Calendar.getInstance();
    private int _count = 0;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(ExecutableTask task) {
        final Calendar lastExecDate = Calendar.getInstance();
        final long lastActivation = task.getLastActivation();

        if (lastActivation > 0) {
            lastExecDate.setTimeInMillis(lastActivation);
        }

        final String rangeDate = "[" + GameUtils.getDateString(lastExecDate.getTime()) + "] - [" + GameUtils.getDateString(_today.getTime()) + "]";

        for (; !_today.before(lastExecDate); lastExecDate.add(Calendar.DATE, 1)) {
            checkBirthday(lastExecDate.get(Calendar.YEAR), lastExecDate.get(Calendar.MONTH), lastExecDate.get(Calendar.DATE));
        }

        LOGGER.info("BirthdayManager: {} gifts sent. {}", _count, rangeDate);
    }

    private void checkBirthday(int year, int month, int day) {
        var charactersData = getDAO(PlayerDAO.class).findBirthdayCharacters(year, month, day);
        charactersData.forEach(characterData -> {
            var name = PlayerNameTable.getInstance().getNameById(characterData.getCharId());
            if(isNull(name)) {
                return;
            }

            var age = year - characterData.getCreateDate().getYear();
            var text = Config.ALT_BIRTHDAY_MAIL_TEXT.replace("$c1", name).replace("$s1", String.valueOf(age));
            final Message msg = new Message(characterData.getCharId(), Config.ALT_BIRTHDAY_MAIL_SUBJECT, text, MailType.BIRTHDAY);

            final Mail attachments = msg.createAttachments();
            attachments.addItem("Birthday", Config.ALT_BIRTHDAY_GIFT, 1, null, null);

            MailManager.getInstance().sendMessage(msg);
            _count++;
        });

        // If character birthday is 29-Feb and year isn't leap, send gift on 28-feb
        final GregorianCalendar calendar = new GregorianCalendar();
        if ((month == Calendar.FEBRUARY) && (day == 28) && !calendar.isLeapYear(_today.get(Calendar.YEAR))) {
            checkBirthday(year, month, 29);
        }
    }

    @Override
    public void initializate() {
        TaskManager.addUniqueTask(NAME, TaskType.GLOBAL_TASK, "1", "06:30:00", "");
    }
}
