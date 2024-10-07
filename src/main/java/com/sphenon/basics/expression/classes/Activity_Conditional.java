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
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.basics.operations.factories.*;
import com.sphenon.basics.data.DataSink;
import com.sphenon.basics.data.DataSinkBase;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

public class Activity_Conditional implements Activity, ContextAware {

    public Activity_Conditional(CallContext context, ActivityClass_Conditional activity_class, Scope scope) {
        this.activity_class = activity_class;
        this.scope = scope;
    }

    protected ActivityClass_Conditional activity_class;
    protected Scope scope;

    protected Vector<Activity> childs;

    public Vector<Activity> getChilds (CallContext context) {
        return this.childs;
    }

    public void setChilds (CallContext context, Vector<Activity> childs) {
        this.childs = childs;
    }

    public void addChild (CallContext context, Activity child) {
        if (this.childs == null) {
            this.childs = new Vector<Activity>();
        }
        this.childs.add(child);
    }

    protected int current_activity_index;
    protected Activity current_activity;

    protected MyExecutionControl execution_control; 

    protected class MyExecutionControl extends Class_ExecutionControl {
        public MyExecutionControl(CallContext context) {
            super(context);
        }
        public String getShortDescription (CallContext context) {
            return ContextAware.ToString.convert(context, current_activity);
        }
    }

    protected boolean isTrue(CallContext context, com.sphenon.basics.expression.parsed.Expression condition, boolean default_value) throws EvaluationFailure {
        Object o = condition == null ? null : condition.getValue(context, this.scope);
        return   o == null              ? default_value
               : (o instanceof Boolean) ? ((Boolean) o)
               : (o instanceof String)  ? ((String) o).equals("true")
               :                          false;
    }

    protected void evaluate(CallContext context, com.sphenon.basics.expression.parsed.Expression code) throws EvaluationFailure {
        Object o = code == null ? null : code.getValue(context, this.scope);
    }

    public Execution execute(CallContext context) {
        return execute(context, null);
    }

    public Execution execute(CallContext context, DataSink<Execution> execution_sink) {
        Execution_BasicSequence es = Factory_Execution.createExecutionSequence(context, "sequence");
        if (execution_sink != null) {
            execution_sink.set(context, es);
        }

        this.execution_control = new MyExecutionControl(context);

        Vector<ActivityClass> child_classes = this.activity_class.getChilds(context);

        try {
            evaluate(context, this.activity_class.getBeginCode(context));
        } catch (EvaluationFailure ef) {
            es.addExecution(context, Factory_Execution.createExecutionFailure(context, ef));
            return es;
        }

        boolean breakable = this.activity_class.getAfterCondition(context) == null;
        // i.e., if there's an after AfterCondition, it's a loop, so 'return/break'
        // refers to this loop; otherwise, it's just a conditional, so 'return/break'
        // refers to the innermost enclosing block

        context = Context.create(context);
        ExecutionContext ec = ExecutionContext.create((Context) context);

        try {
            if (breakable) { ec.beginActiveBlock(context, null); }

            ALL_ITERATIONS : while(true) {

                try {
                    if ( ! isTrue(context, this.activity_class.getBeforeCondition(context), true)) {
                        break ALL_ITERATIONS;
                    }
                } catch (EvaluationFailure ef) {
                    es.addExecution(context, Factory_Execution.createExecutionFailure(context, ef));
                    return es;
                }

                try {
                    evaluate(context, this.activity_class.getBeforeCode(context));
                } catch (EvaluationFailure ef) {
                    es.addExecution(context, Factory_Execution.createExecutionFailure(context, ef));
                    return es;
                }

                int from_index = this.childs == null ? 0 : this.childs.size();
                if (child_classes != null) {
                    for (ActivityClass child_class : child_classes) {
                        try {
                            this.addChild(context, child_class.instantiate(context, this.scope));
                        } catch (EvaluationFailure ef) {
                            es.addExecution(context, Factory_Execution.createExecutionFailure(context, ef));
                            return es;
                        }
                    }
                }
                int to_index = this.childs == null ? 0 : this.childs.size();

                for (this.current_activity_index = from_index; this.current_activity_index < to_index; this.current_activity_index++) {

                    this.current_activity = this.childs.get(this.current_activity_index);

                    this.execution_control.setPoint(context, ExecutionControl.Point.BeforeActivity);
                    if (ExecutionContext.notifyInterceptor(context, this.execution_control)) {
                        if (this.execution_control.getContinuation(context) == ContinuationOption.STEP_OVER) {
                        } else if (this.execution_control.getContinuation(context) == ContinuationOption.STEP_INTO) {
                            // nothing to do
                        } else if (this.execution_control.getContinuation(context) == ContinuationOption.SKIP_STEP) {
                            continue;
                        } else if (this.execution_control.getContinuation(context) == ContinuationOption.PROCEED) {
                        } else if (this.execution_control.getContinuation(context) == ContinuationOption.CANCEL) {
                            es.addExecution(context, Factory_Execution.createExecutionFailure(context, CustomaryContext.create((Context)context).createExternalIntervention(context, "Sequence aborted by request")));
                            break ALL_ITERATIONS;
                        }
                    }

                    Execution current_execution;
                    try {
                        current_execution = this.current_activity.execute(context,
                                                                          execution_sink == null ?
                                                                             null
                                                                           : new DataSinkBase<Execution>() {
                                                                                   public void set(CallContext context, Execution e) {
                                                                                       es.addExecution(context, e);
                                                                                   }
                                                                               }
                                                                          );
                    } catch (Throwable t) {
                        current_execution = Factory_Execution.createExecutionFailure(context, t);
                    }
                    if (execution_sink == null) {
                        es.addExecution(context, current_execution);
                    }

                    this.execution_control.setPoint(context, ExecutionControl.Point.AfterActivity);
                    if (ExecutionContext.notifyInterceptor(context, this.execution_control));

                    if (current_execution.getProblemState(context).isOk(context) == false) {
                        break ALL_ITERATIONS;
                    }

                    if (ec.getBlockIsActive(context) == false) {
                        break ALL_ITERATIONS;
                    }
                }

                try {
                    evaluate(context, this.activity_class.getAfterCode(context));
                } catch (EvaluationFailure ef) {
                    es.addExecution(context, Factory_Execution.createExecutionFailure(context, ef));
                    return es;
                }

                try {
                    if ( ! isTrue(context, this.activity_class.getAfterCondition(context), false)) {
                        break ALL_ITERATIONS;
                    }
                } catch (EvaluationFailure ef) {
                    es.addExecution(context, Factory_Execution.createExecutionFailure(context, ef));
                    return es;
                }
            }

            try {
                evaluate(context, this.activity_class.getEndCode(context));
            } catch (EvaluationFailure ef) {
                es.addExecution(context, Factory_Execution.createExecutionFailure(context, ef));
                return es;
            }

        } finally {
            if (breakable) { ec.finishActiveBlock(context, null); }
        }

        this.data = new Class_ActivityData(context);

        es.close(context);

        return es;
    }

    protected Class_ActivityData data;

    public ActivityData getData(CallContext context) {
        return this.data;
    }

    public String toString(CallContext context) {
        if (this.activity_class != null) {
            return this.activity_class.toString(context);
        }
        return "conditional";
    }
}
