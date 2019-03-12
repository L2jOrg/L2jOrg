/*
 * Copyright 2014 Frank Asseg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.l2j.gameserver.util.exp4j;

import java.util.*;

/**
 * Factory class for {@link Expression} instances. This class is the main API entrypoint. Users should create new {@link Expression} instances using this factory class.
 */
public class ExpressionBuilder {
    private final String expression;

    private final Map<String, Function> userFunctions;

    private final Map<String, Operator> userOperators;

    private final Set<String> variableNames;

    private boolean implicitMultiplication = true;

    /**
     * Create a new ExpressionBuilder instance and initialize it with a given expression string.
     *
     * @param expression the expression to be parsed
     */
    public ExpressionBuilder(String expression) {
        if ((expression == null) || (expression.trim().isEmpty())) {
            throw new IllegalArgumentException("Expression can not be empty");
        }
        this.expression = expression;
        userOperators = new HashMap<>(4);
        userFunctions = new HashMap<>(4);
        variableNames = new HashSet<>(4);
    }

    /**
     * Add a {@link org.l2j.gameserver.util.exp4j.Function} implementation available for use in the expression
     *
     * @param function the custom {@link org.l2j.gameserver.util.exp4j.Function} implementation that should be available for use in the expression.
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder function(Function function) {
        userFunctions.put(function.getName(), function);
        return this;
    }

    /**
     * Add multiple {@link org.l2j.gameserver.util.exp4j.Function} implementations available for use in the expression
     *
     * @param functions the custom {@link org.l2j.gameserver.util.exp4j.Function} implementations
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder functions(Function... functions) {
        for (Function f : functions) {
            userFunctions.put(f.getName(), f);
        }
        return this;
    }

    /**
     * Add multiple {@link org.l2j.gameserver.util.exp4j.Function} implementations available for use in the expression
     *
     * @param functions A {@link List} of custom {@link org.l2j.gameserver.util.exp4j.Function} implementations
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder functions(List<Function> functions) {
        for (Function f : functions) {
            userFunctions.put(f.getName(), f);
        }
        return this;
    }

    /**
     * Declare variable names used in the expression
     *
     * @param variableNames the variables used in the expression
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variables(Set<String> variableNames) {
        this.variableNames.addAll(variableNames);
        return this;
    }

    /**
     * Declare variable names used in the expression
     *
     * @param variableNames the variables used in the expression
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variables(String... variableNames) {
        Collections.addAll(this.variableNames, variableNames);
        return this;
    }

    /**
     * Declare a variable used in the expression
     *
     * @param variableName the variable used in the expression
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variable(String variableName) {
        variableNames.add(variableName);
        return this;
    }

    public ExpressionBuilder implicitMultiplication(boolean enabled) {
        implicitMultiplication = enabled;
        return this;
    }

    /**
     * Add an {@link org.l2j.gameserver.util.exp4j.Operator} which should be available for use in the expression
     *
     * @param operator the custom {@link org.l2j.gameserver.util.exp4j.Operator} to add
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(Operator operator) {
        checkOperatorSymbol(operator);
        userOperators.put(operator.getSymbol(), operator);
        return this;
    }

    private void checkOperatorSymbol(Operator op) {
        String name = op.getSymbol();
        for (char ch : name.toCharArray()) {
            if (!Operator.isAllowedOperatorChar(ch)) {
                throw new IllegalArgumentException("The operator symbol '" + name + "' is invalid");
            }
        }
    }

    /**
     * Add multiple {@link org.l2j.gameserver.util.exp4j.Operator} implementations which should be available for use in the expression
     *
     * @param operators the set of custom {@link org.l2j.gameserver.util.exp4j.Operator} implementations to add
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(Operator... operators) {
        for (Operator o : operators) {
            operator(o);
        }
        return this;
    }

    /**
     * Add multiple {@link org.l2j.gameserver.util.exp4j.Operator} implementations which should be available for use in the expression
     *
     * @param operators the {@link List} of custom {@link org.l2j.gameserver.util.exp4j.Operator} implementations to add
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(List<Operator> operators) {
        for (Operator o : operators) {
            operator(o);
        }
        return this;
    }

    /**
     * Build the {@link Expression} instance using the custom operators and functions set.
     *
     * @return an {@link Expression} instance which can be used to evaluate the result of the expression
     */
    public Expression build() {
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("The expression can not be empty");
        }
        /* set the contants' varibale names */
        variableNames.add("pi");
        variableNames.add("π");
        variableNames.add("e");
        variableNames.add("φ");
        /* Check if there are duplicate vars/functions */
        for (String var : variableNames) {
            if ((Functions.getBuiltinFunction(var) != null) || userFunctions.containsKey(var)) {
                throw new IllegalArgumentException("A variable can not have the same name as a function [" + var + "]");
            }
        }
        return new Expression(ShuntingYard.convertToRPN(expression, userFunctions, userOperators, variableNames, implicitMultiplication), userFunctions.keySet());
    }

}
