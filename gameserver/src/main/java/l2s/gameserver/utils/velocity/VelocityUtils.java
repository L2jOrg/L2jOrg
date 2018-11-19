package l2s.gameserver.utils.velocity;

import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.Config;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.slf4j.Slf4jLogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 18:27/25.04.2012
 */
public class VelocityUtils
{
	private static final Logger _log = LoggerFactory.getLogger(VelocityUtils.class);

	public static final Map<String, Object> GLOBAL_VARIABLES = new HashMap<String, Object>();

	public static void init()
	{
		Velocity.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Slf4jLogChute.class.getName());
		Velocity.setProperty(Slf4jLogChute.LOGCHUTE_SLF4J_NAME, VelocityUtils.class.getName());
		Velocity.init();

		Field[] fields = Config.class.getDeclaredFields();
		for(Field f : fields)
			try
			{
				if(!f.isAnnotationPresent(VelocityVariable.class))
					continue;

				GLOBAL_VARIABLES.put(f.getName(), f.get(null));
			}
			catch(IllegalAccessException e)
			{
				throw new Error(e);
			}
	}

	/**
	 * Запускает диалог в Velocity - использует ток указаные переменные
	 * @param text
	 * @param variables
	 * @return
	 */
	private static String evaluate0(String text, Map<String, Object> variables)
	{
		if(variables.isEmpty())
			return text;

		try
		{
			VelocityContext velocityContext = new VelocityContext(variables);
			Writer writer = new StringBuilderWriter(new StringBuilder(text.length() + 32));

			if(!Velocity.evaluate(velocityContext, writer, "", text))
			{
				_log.warn("Fail to evaluate: \n" + text);
				return StringUtils.EMPTY;
			}

			return writer.toString();
		}
		catch(Exception e)
		{}

		return text;
	}

	/**
	 * Запускает диалог в Velocity - использует наряду с указаными переменными - юзает глобальные
	 * @param text
	 * @param variables
	 * @return
	 */
	public static String evaluate(String text, Map<String, Object> variables)
	{
		if(variables != null)
			variables.putAll(GLOBAL_VARIABLES);
		else
			variables = GLOBAL_VARIABLES;

		return evaluate0(text, variables);
	}
}
