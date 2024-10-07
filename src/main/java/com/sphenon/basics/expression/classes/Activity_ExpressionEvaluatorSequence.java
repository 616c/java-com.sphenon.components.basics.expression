package com.sphenon.basics.expression.classes;

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
import com.sphenon.basics.system.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.basics.operations.factories.*;
import com.sphenon.basics.data.DataSink;
import com.sphenon.basics.data.DataSinkBase;
import com.sphenon.basics.monitoring.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

public class Activity_ExpressionEvaluatorSequence implements Activity, ContextAware {

    public Activity_ExpressionEvaluatorSequence(CallContext context, ActivityClass_ExpressionEvaluatorSequence activity_class, Scope scope) {
        this.activity_class = activity_class;
        this.scope = scope;
    }

    protected ActivityClass_ExpressionEvaluatorSequence activity_class;
    protected Scope scope;

    public Execution execute(CallContext context) {
        return execute(context, null);
    }

    public Execution execute(CallContext context, DataSink<Execution> execution_sink) {
        Object current = this.activity_class.getExpression(context);
        ExpressionEvaluatorRegistry registry = this.activity_class.getRegistry(context);

        ExecutionHandler eh = new ExecutionHandler(context, execution_sink, this.activity_class.getDescription(context));

        int s=0;
        for (String[] evaluator : this.activity_class.getEvaluators(context)) {

            String id       = evaluator[0];
            String actor    = evaluator[1];
            String location = evaluator[2];
            String session  = evaluator[3];
            
            if (id == null || id.isEmpty()) { continue; }

            if (id.equals("check")) {
                if (current instanceof Execution) {
                    Execution execution = (Execution) current;
                    ProblemState ps = execution.getProblemState(context);
                    if (execution_sink != null) {
                        execution_sink.set(context, execution);
                    }
                    if (ps.isRed(context)) {
                        return execution;
                    }
                    current = ps == null ? "" : ps.toString();
                } else if (current instanceof Throwable) {
                    Execution execution = Factory_Execution.createExecutionFailure(context, (Throwable) current);
                    if (execution_sink != null) {
                        execution_sink.set(context, execution);
                    }
                    return execution;
                }
            } else if (id.equals("log")) {
                SystemContext.err.stream(context).flush();
                Dumper.dumpToStream(context, null, current, SystemContext.err.stream(context));
                SystemContext.err.stream(context).flush();
            } else {
                try {
                    Scope current_scope = Expression.mergeScopeWithSessionScope(context, this.scope, actor, session);

                    boolean got_location = (location != null && location.isEmpty() == false);

                    ActivityHandler ah = ActivityHandler_Expression.createWithEvaluatorId(context, current, id, actor, location, session, got_location ? null : registry);
                    ah.setExecutionSink(context, eh.createReportingSink(context));

                    if (ah.execute(context, current_scope).isValid(context) == false) {
                        return eh.add(context, Factory_Execution.createExecutionSequence(context, createMessage(context, s), ah.getExecution(context)));
                    }
                    current = ah.getSlots(context).get(0).getValue(context);
                } catch (Throwable t) {
                    Execution execution = Factory_Execution.createExecutionFailure(context, t);
                    if (execution_sink != null) {
                        execution_sink.set(context, execution);
                    }
                    return execution;
                }
            }

            s++;
        }

        this.data = new Class_ActivityData(context);
        this.data.addSlot(context, new Class_ActivitySlot(context, this.activity_class.getInterface(context).getAttributes(context).get(0), current));

        return Factory_Execution.createExecutionSuccess(context);
    }

    protected String createMessage(CallContext context, int s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Processing of expression '");
        sb.append(this.activity_class.getExpression(context));
        sb.append("' failed in sequence at '");
        int si=0;
        for (String[] evaluator : this.activity_class.getEvaluators(context)) {
            if (si != 0) { sb.append(", "); }
            if (si == s) { sb.append(" <<< "); }
            sb.append(evaluator[0]);
            if (si == s) { sb.append(" >>> "); }
        }
        sb.append("'");
        return sb.toString();
    }

    protected Class_ActivityData data;

    public ActivityData getData(CallContext context) {
        return this.data;
    }

    public String toString(CallContext context) {
        if (this.activity_class != null) {
            return this.activity_class.toString(context);
        }
        return "sequence";
    }
}
