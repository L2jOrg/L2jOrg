package l2s.gameserver.utils.velocity;

import java.lang.annotation.*;

/**
 * @author VISTALL
 * @date 15:06/26.04.2012
 *
 * Аннотация - которая определяет ли будет переменная использоватся в диалогах при парсе Velocity
 *
 * ВНИМАНИЯ: переименовавывать переменную нельзя, пока непроверится датапак
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VelocityVariable
{

}