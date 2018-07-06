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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

public class ActivityHandler {

    protected boolean is_valid;

    public ActivityHandler(CallContext context, ActivityClass activity_class) {
        this.activity_class = activity_class;
        if (this.activity_class == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler, invalid argument 'activity_class' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        this.is_valid = true;
    }

    protected ActivityHandler(CallContext context) {
        this.activity_class = null;
        this.is_valid = false;
    }

    protected ActivityClass activity_class;

    public ActivityClass getActivityClass (CallContext context) {
        return this.activity_class;
    }

    protected Activity activity;

    public Activity getActivity (CallContext context) {
        return this.activity;
    }

    protected Execution execution;

    public Execution getExecution (CallContext context) {
        return this.execution;
    }

    public Vector<ActivityAttribute> getAttributes(CallContext context) {
        if (this.activity_class == null) { return null; }
        ActivityInterface activity_interface = this.activity_class.getInterface(context);
        if (activity_interface == null) { return null; }
        return activity_interface.getAttributes(context);
    }

    public Vector<ActivitySlot> getSlots(CallContext context) {
        if (this.activity == null) { return null; }
        ActivityData activity_data = this.activity.getData(context);
        if (activity_data == null) { return null; }
        return activity_data.getSlots(context);
    }

    public Object getSlotValue(CallContext context, String name) {
        if (this.activity == null) { return null; }
        ActivityData activity_data = this.activity.getData(context);
        if (activity_data == null) { return null; }
        return activity_data.getSlotValue(context, name);
    }

    public boolean isValid(CallContext context) {
        return this.is_valid;
    }

    public ActivityHandler instantiate(CallContext context, Scope scope) {
        if (this.is_valid) {
            try {
                if (this.activity == null) {
                    this.activity = this.activity_class.instantiate(context, scope);
                }
            } catch (EvaluationFailure ef) {
                this.is_valid = false;
                this.execution = Execution_Basic.createExecutionFailure(context, ef);
            }
        }
        return this;
    }

    public ActivityHandler execute(CallContext context, Scope scope) {
        this.instantiate(context, scope);

        if (this.is_valid) {
            if (this.execution == null) {
                this.execution = this.activity.execute(context);
            }

            if (this.execution.getProblemState(context).isOk(context) == false) {
                this.is_valid = false;
            }
        }
        return this;
    }

    public void dumpExecution(CallContext context) {
        if (this.execution != null) {
            Dumper.dump(context, null, this.execution);
        }
    }

    public void dumpData(CallContext context) {
        Vector<ActivitySlot> slots = this.getSlots(context);
        if (slots != null) {
            int size = slots.size();
            for (ActivitySlot slot : slots) {
                Object result = slot.getValue(context);
                
                if (result != null) {
                    String string_result = ContextAware.ToString.convert(context, result);
                    if (string_result.isEmpty() == false) {
                        if (size != 1) {
                            System.err.print(slot.getAttribute(context).getName(context) + " = ");
                        }
                        System.err.print(string_result);
                        if (string_result.charAt(string_result.length()-1) != '\n') {
                            System.err.println("");
                        }
                    }
                }
            }
        }
    }
}
