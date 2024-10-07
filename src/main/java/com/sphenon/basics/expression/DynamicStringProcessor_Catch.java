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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public class DynamicStringProcessor_Catch implements ExpressionEvaluator {

    public DynamicStringProcessor_Catch (CallContext context) {
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return new String[] { "catch" };
    }

    public Object evaluate(CallContext context, String string, Scope scope, DataSink<Execution> execution_sink) throws EvaluationFailure {
        ExecutionHandler eh = new ExecutionHandler(context, execution_sink, null);
        int qm = string.indexOf("|?|");
        String e = string;
        String results = null;
        int cm = -1;
        String true_e = null;
        String false_e = null;
        if (qm != -1) {
            e = string.substring(0, qm);
            results = string.substring(qm + 3);
            cm = results.indexOf("|:|");
            if (cm == -1) {
                true_e = results;
            } else {
                true_e = results.substring(0, cm);
                false_e = results.substring(cm + 3);
            }
        }
        Object result = null;
        try {
            e = Encoding.recode(context, e, Encoding.URI, Encoding.UTF8);
            result = Expression.evaluate(context, e, scope, eh.createReportingSink(context));
        } catch (Throwable t) {
            if (false_e == null || false_e.isEmpty()) {
                return false;
            } else {
                Scope s = new Class_Scope(context, null, scope, "exception", t);
                false_e = Encoding.recode(context, false_e, Encoding.URI, Encoding.UTF8);
                return Expression.evaluate(context, false_e, s, eh.createReportingSink(context));
            }
        } finally {
            eh.reportSuccess(context);
        }

        if (true_e == null || true_e.isEmpty()) {
            return true;
        } else {
            Scope s = new Class_Scope(context, null, scope, "result", result);
            true_e = Encoding.recode(context, true_e, Encoding.URI, Encoding.UTF8);
            return Expression.evaluate(context, true_e, s, eh.createReportingSink(context));
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
