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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.returncodes.*;

import com.sphenon.basics.expression.classes.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class DynamicString {

    static {
        ExpressionPackageInitialiser.initialise();
    }

    static public String process(CallContext context, String string_template) {
        return new DynamicString(context, string_template).get(context);
    }

    static public String process(CallContext context, String string_template, Scope scope) {
        return new DynamicString(context, string_template).get(context, scope);
    }

    static public String process(CallContext context, String string_template, String default_processors) {
        return new DynamicString(context, string_template, default_processors).get(context);
    }

    static public String process(CallContext context, String string_template, String default_processors, Scope scope) {
        return new DynamicString(context, string_template, default_processors).get(context, scope);
    }

    static public String process(CallContext context, String string_template, String default_processors, Object... arguments) {
        return new DynamicString(context, string_template, default_processors).get(context, arguments);
    }

    static public String process(CallContext context, String string_template, String default_processors, Map<String,Object> arguments) {
        return new DynamicString(context, string_template, default_processors).get(context, arguments);
    }

    protected Expression expression;

    public DynamicString(CallContext context, String string_template) {
        this(context, string_template, null, null, null);
    }

    public DynamicString(CallContext context, String string_template, Scope scope) {
        this(context, string_template, null, null, scope);
    }

    public DynamicString(CallContext context, String string_template, String default_processors) {
        this.expression = new Expression(context, string_template, default_processors, true);
    }

    public DynamicString(CallContext context, String string_template, String default_processors, String default_session) {
        this.expression = new Expression(context, string_template, default_processors, default_session, true);
    }

    public DynamicString(CallContext context, String string_template, String default_processors, String default_session, Scope scope) {
        this.expression = new Expression(context, string_template, default_processors, default_session, true, scope);
    }

    public String getStringTemplate (CallContext context) {
        return this.expression.getExpression(context);
    }

    public String getDefaultProcessors (CallContext context) {
        return this.expression.getDefaultEvaluators(context);
    }

    public void setStringTemplate (CallContext context, String string_template) {
        this.expression.setExpression(context, string_template);
    }

    public void setStringTemplate (CallContext context, String string_template, String default_processors) {
        this.expression.setExpression(context, string_template, default_processors);
    }

    public void setStringTemplate (CallContext context, String string_template, String default_processors, String default_session) {
        this.expression.setExpression(context, string_template, default_processors, default_session);
    }

    public void attachScope(CallContext context, Scope attached_scope) {
        this.expression.attachScope(context, attached_scope);
    }

    protected String convertToString(CallContext context, Object object) {
        try {
            return (String) object;
        } catch (ClassCastException cce) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Result of evaluation of '%(template)' is not a 'String', but a '%(class)'", "template", this.getStringTemplate(context), "class", object.getClass().getName());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String get(CallContext context) {
        try {
            return convertToString(context, this.expression.evaluate(context));
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of '%(template)' failed", "template", this.getStringTemplate(context));
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String get(CallContext context, Scope scope) {
        try {
            return convertToString(context, this.expression.evaluate(context, scope));
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of '%(template)' failed", "template", this.getStringTemplate(context));
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String get(CallContext context, Object... arguments) {
        try {
            return convertToString(context, this.expression.evaluate(context, arguments));
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of '%(template)' failed", "template", this.getStringTemplate(context));
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String get(CallContext context, Map<String,Object> arguments) {
        try {
            return convertToString(context, this.expression.evaluate(context, arguments));
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Evaluation of '%(template)' failed", "template", this.getStringTemplate(context));
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public String toString() {
        return this.expression.toString();
    }
}
