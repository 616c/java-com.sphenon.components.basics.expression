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
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

public class Activity_Sequence implements Activity, ContextAware {

    public Activity_Sequence(CallContext context, ActivityClass_Sequence activity_class) {
        this.activity_class = activity_class;
    }

    protected ActivityClass_Sequence activity_class;

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

    public Execution execute(CallContext context) {
        Execution_BasicSequence es = Execution_BasicSequence.createExecutionSequence(context, "sequence");
        if (this.childs != null) {

            this.execution_control = new MyExecutionControl(context);

            for (this.current_activity_index = 0;
                 this.current_activity_index < this.childs.size();
                 this.current_activity_index++) {
                
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
                        es.addExecution(context, Execution_Basic.createExecutionFailure(context, CustomaryContext.create((Context)context).createExternalIntervention(context, "Sequence aborted by request")));
                        break;
                    }
                }

                Execution current_execution;
                try {
                    current_execution = this.current_activity.execute(context);
                } catch (Throwable t) {
                    current_execution = Execution_Basic.createExecutionFailure(context, t);
                }
                es.addExecution(context, current_execution);

                this.execution_control.setPoint(context, ExecutionControl.Point.AfterActivity);
                if (ExecutionContext.notifyInterceptor(context, this.execution_control));

                if (current_execution.getProblemState(context).isGreen(context) == false) {
                    break;
                }
            }
        }

        this.data = new Class_ActivityData(context);
//        this.data.addSlot(context, new Class_ActivitySlot(context, this.activity_class.getInterface(context).getAttributes(context).get(0), current));

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
        return "sequence";
    }
}
