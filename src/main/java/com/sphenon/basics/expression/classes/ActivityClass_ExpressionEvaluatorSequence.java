package com.sphenon.basics.expression.classes;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

public class ActivityClass_ExpressionEvaluatorSequence implements ActivityClass, ContextAware {

    public ActivityClass_ExpressionEvaluatorSequence(CallContext context, String[][] evaluators, String expression, Scope base_scope, ExpressionEvaluatorRegistry registry) {
        this.evaluators = evaluators;
        this.expression = expression;
        this.base_scope = base_scope;
        this.registry   = registry;
    }

    protected String description;

    public String getDescription (CallContext context) {
        return this.description;
    }

    public void setDescription (CallContext context, String description) {
        this.description = description;
    }

    protected Scope base_scope;

    protected String[][] evaluators;

    public String[][] getEvaluators (CallContext context) {
        return this.evaluators;
    }

    protected String expression;

    public String getExpression (CallContext context) {
        return this.expression;
    }

    protected ExpressionEvaluatorRegistry registry;

    public ExpressionEvaluatorRegistry getRegistry (CallContext context) {
        return this.registry;
    }

    public Activity instantiate(CallContext context, Scope scope) throws EvaluationFailure {
        if (this.base_scope != null) {
            scope = new Class_Scope(context, null, scope, this.base_scope);
        }
        return new Activity_ExpressionEvaluatorSequence(context, this, scope);
    }

    static protected Class_ActivityInterface activity_interface;
    static protected ActivityAttribute result_attribute;

    public ActivityInterface getInterface (CallContext context) {
        if (activity_interface == null) {
            result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
            activity_interface = new Class_ActivityInterface(context);
            activity_interface.addAttribute(context, result_attribute);
        }
        return activity_interface;
    }

    public String toString(CallContext context) {
        if (this.description == null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String[] evaluator : this.getEvaluators(context)) {
                if (first) { first = false; } else { sb.append(","); }
                if (evaluator[0] != null && evaluator[0].isEmpty() == false) {
                    sb.append(evaluator[0]);
                }
                if (evaluator[1] != null && evaluator[1].isEmpty() == false) {
                    sb.append("%");
                    sb.append(evaluator[1]);
                }
                if (evaluator[2] != null && evaluator[2].isEmpty() == false) {
                    sb.append("@");
                    sb.append(evaluator[2]);
                }
                if (evaluator[3] != null && evaluator[3].isEmpty() == false) {
                    sb.append("#");
                    sb.append(evaluator[3]);
                }
            }
            sb.append(":");
            sb.append(this.expression);
            this.description = sb.toString();
        }
        return this.description;
    }

}
