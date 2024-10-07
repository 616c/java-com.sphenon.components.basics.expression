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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public class ExpressionEvaluator_Define implements ExpressionEvaluator {

    public ExpressionEvaluator_Define (CallContext context) {
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return new String[] { "define" };
    }

    static protected RegularExpression part_re  = new RegularExpression("^([^|]*)\\|(.*)$");
    static protected RegularExpression define_re  = new RegularExpression("^ *([^= ]+) *= *([^ ](?:.*[^ ])?)$");

    public Object evaluate(CallContext context, String instruction, Scope scope, DataSink<Execution> execution_sink) throws EvaluationFailure {
        ExecutionHandler eh = new ExecutionHandler(context, execution_sink, null);
        try{
            String[] parts = part_re.tryGetMatches(context, instruction);

            if (parts == null) {
                eh.reportAndThrow(context, EvaluationFailure.create(context, "Invalid syntax in 'define' expression, expected '...|...', got '%(instruction)'", "instruction", instruction));
                throw (EvaluationFailure) null;
            } else {
                Scope local_scope = new Class_Scope(context, scope);

                for (String definition : parts[0].split(";",-1)) {
                    String[] def = define_re.tryGetMatches(context, definition);
                     
                    if (def == null) {
                        eh.reportAndThrow(context, EvaluationFailure.create(context, "Invalid definition in 'define' expression, expected '...=...', got '%(definition)'", "definition", definition));
                        throw (EvaluationFailure) null;
                    } else {
                        String value_expression = Encoding.recode(context, def[1], Encoding.URI, Encoding.UTF8);
                        Object value = Expression.evaluate(context, value_expression, scope, execution_sink);

                        local_scope.set(context, def[0], value);
                    }
                }

                Object result = Expression.evaluate(context, parts[1], local_scope, execution_sink);

                eh.reportSuccess(context);
                return result;
            }
        } catch (EvaluationFailure t) {
            eh.handleFinally(context, t);
            throw (EvaluationFailure) null;
        } catch (Throwable t) {
            eh.handleFinally(context, EvaluationFailure.create(context, t, "Unexpected exception"));
            throw (Error) null;
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute, false, null);
    }
}
