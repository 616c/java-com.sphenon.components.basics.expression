package com.sphenon.basics.expression;

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

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public class DynamicStringProcessor_Sequence implements ExpressionEvaluator {

    static public DynamicStringProcessor_Sequence createFromString(CallContext context, String specification) {
        String[] parts      = specification.split("=",2);
        String[] ids        = parts[0].split(",");
        String[] processors = parts[1].split(",");
        return new DynamicStringProcessor_Sequence (context, ids, processors);
    }

    public DynamicStringProcessor_Sequence (CallContext context, String[] ids, String[] processors) {
        this.ids        = ids;
        this.processors = processors;
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected String[] ids;
    protected String[] processors;

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return ids;
    }

    public Object evaluate(CallContext context, String string, Scope scope) throws EvaluationFailure{
        for (String processor_id : processors) {

            Object result = Expression.evaluateWithEvaluator(context, string, processor_id, scope, null, null, true, null);
            try {
                string = (String) result;
            } catch (ClassCastException cce) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Result of evaluation of '%(template)' is not a 'String', but a '%(class)'", "template", string, "class", result.getClass().getName());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
        return string;
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
