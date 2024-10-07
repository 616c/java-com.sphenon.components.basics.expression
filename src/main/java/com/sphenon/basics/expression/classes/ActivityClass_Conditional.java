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

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

public class ActivityClass_Conditional implements ActivityClass_Block, ContextAware {

    public ActivityClass_Conditional(CallContext context) {
    }

    public ActivityClass_Conditional(CallContext context, Scope scope, com.sphenon.basics.expression.parsed.Expression begin_code, com.sphenon.basics.expression.parsed.Expression before_condition, com.sphenon.basics.expression.parsed.Expression before_code, com.sphenon.basics.expression.parsed.Expression after_code, com.sphenon.basics.expression.parsed.Expression after_condition, com.sphenon.basics.expression.parsed.Expression end_code) {
        this.scope = scope;
        this.begin_code = begin_code;
        this.before_condition = before_condition;
        this.before_code = before_code;
        this.after_code = after_code;
        this.after_condition = after_condition;
        this.end_code = end_code;
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

    protected com.sphenon.basics.expression.parsed.Expression begin_code;

    public com.sphenon.basics.expression.parsed.Expression getBeginCode (CallContext context) {
        return this.begin_code;
    }

    public void setBeginCode (CallContext context, com.sphenon.basics.expression.parsed.Expression begin_code) {
        this.begin_code = begin_code;
    }

    protected com.sphenon.basics.expression.parsed.Expression before_condition;

    public com.sphenon.basics.expression.parsed.Expression getBeforeCondition (CallContext context) {
        return this.before_condition;
    }

    public void setBeforeCondition (CallContext context, com.sphenon.basics.expression.parsed.Expression before_condition) {
        this.before_condition = before_condition;
    }

    protected com.sphenon.basics.expression.parsed.Expression before_code;

    public com.sphenon.basics.expression.parsed.Expression getBeforeCode (CallContext context) {
        return this.before_code;
    }

    public void setBeforeCode (CallContext context, com.sphenon.basics.expression.parsed.Expression before_code) {
        this.before_code = before_code;
    }

    protected com.sphenon.basics.expression.parsed.Expression after_code;

    public com.sphenon.basics.expression.parsed.Expression getAfterCode (CallContext context) {
        return this.after_code;
    }

    public void setAfterCode (CallContext context, com.sphenon.basics.expression.parsed.Expression after_code) {
        this.after_code = after_code;
    }

    protected com.sphenon.basics.expression.parsed.Expression after_condition;

    public com.sphenon.basics.expression.parsed.Expression getAfterCondition (CallContext context) {
        return this.after_condition;
    }

    public void setAfterCondition (CallContext context, com.sphenon.basics.expression.parsed.Expression after_condition) {
        this.after_condition = after_condition;
    }

    protected com.sphenon.basics.expression.parsed.Expression end_code;

    public com.sphenon.basics.expression.parsed.Expression getEndCode (CallContext context) {
        return this.end_code;
    }

    public void setEndCode (CallContext context, com.sphenon.basics.expression.parsed.Expression end_code) {
        this.end_code = end_code;
    }

    // scope here is used in shell loop doEvaluation, since in case
    // of block with immediate execution the scope from the creation
    // is needed, which is stored here
    protected Scope scope;

    public Scope getScope (CallContext context) {
        return this.scope;
    }

    public void setScope (CallContext context, Scope scope) {
        this.scope = scope;
    }

    public Activity instantiate(CallContext context, Scope scope) throws EvaluationFailure {
        Activity_Conditional a = new Activity_Conditional(context, this, scope);
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
        return "conditional";
    }
}
