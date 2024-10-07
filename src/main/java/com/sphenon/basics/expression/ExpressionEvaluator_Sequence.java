package com.sphenon.basics.expression;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

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
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public class ExpressionEvaluator_Sequence implements ExpressionEvaluator {

    static public ExpressionEvaluator_Sequence createFromString(CallContext context, String specification) {
        String[] parts      = specification.split("=",2);
        String[] ids        = parts[0].split(",");
        String[] evaluators = parts[1].split(",");
        return new ExpressionEvaluator_Sequence (context, ids, evaluators);
    }

    public ExpressionEvaluator_Sequence (CallContext context, String[] ids, String[] evaluators) {
        this.ids        = ids;
        this.evaluators = evaluators;
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected String[] ids;
    protected String[] evaluators;

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return ids;
    }

    // DELETEME
    // static public int counter = 0;
    // static public int break_at = 0;

    public Object evaluate(CallContext context, String string, Scope scope, DataSink<Execution> execution_sink) throws EvaluationFailure {
        // DELETEME
        // counter++;
        // if (counter == break_at) {
        //     System.err.println("breaking");
        // }
        Object current = string;
        for (String evaluator_id : evaluators) {
            current = Expression.evaluate(context, (String) current, true, evaluator_id, scope, execution_sink);
        }
        return current;
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
