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

import java.util.Vector;

public class ActivityClass_Sequence implements ActivityClass_Block, ContextAware {

    public ActivityClass_Sequence(CallContext context) {
    }

    protected String description;

    public String getDescription (CallContext context) {
        return this.description;
    }

    public void setDescription (CallContext context, String description) {
        this.description = description;
    }

    protected Vector<ActivityClass> childs;

    public Vector<ActivityClass> getChilds (CallContext context) {
        return this.childs;
    }

    public void setChilds (CallContext context, Vector<ActivityClass> childs) {
        this.childs = childs;
    }

    public void addChild (CallContext context, ActivityClass child) {
        if (this.childs == null) {
            this.childs = new Vector<ActivityClass>();
        }
        this.childs.add(child);
    }

    public Activity instantiate(CallContext context, Scope scope) throws EvaluationFailure {
        Activity_Sequence a = new Activity_Sequence(context, this);
        if (this.childs != null) {
            for (ActivityClass child : this.childs) {
                a.addChild(context, child.instantiate(context, scope));
            }
        }
        return a;
    }

    static protected Class_ActivityInterface activity_interface;

    public ActivityInterface getInterface (CallContext context) {
        if (activity_interface == null) {
            activity_interface = new Class_ActivityInterface(context);
        }
        return activity_interface;
    }

    public String toString(CallContext context) {
        return this.description != null ? this.description : "sequence";
    }
}
