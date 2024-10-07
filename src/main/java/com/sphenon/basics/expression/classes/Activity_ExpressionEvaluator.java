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
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.basics.operations.factories.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

public class Activity_ExpressionEvaluator implements Activity, ContextAware {

    public Activity_ExpressionEvaluator(CallContext context, ExpressionEvaluator expression_evaluator, ExpressionSource expression_source, ArgumentSerialiser argument_serialiser, Scope scope, ActivityAttribute activity_attribute) {
        this.expression_evaluator         = expression_evaluator;
        this.expression_evaluator_service = null;
        this.expression_source            = expression_source;
        this.argument_serialiser          = argument_serialiser;
        this.scope                        = scope;
        this.activity_attribute           = activity_attribute;
    }

    public Activity_ExpressionEvaluator(CallContext context, ExpressionEvaluatorService expression_evaluator_service, String evaluator_id, ExpressionSource expression_source, ArgumentSerialiser argument_serialiser, Scope scope, ActivityAttribute activity_attribute) {
        this.expression_evaluator         = null;
        this.expression_evaluator_service = expression_evaluator_service;
        this.expression_source            = expression_source;
        this.argument_serialiser          = argument_serialiser;
        this.scope                        = scope;
        this.activity_attribute           = activity_attribute;
        this.evaluator_id                 = evaluator_id;
    }

    protected ExpressionEvaluator        expression_evaluator;

    protected ExpressionEvaluatorService expression_evaluator_service;
    protected String                     evaluator_id;

    protected ExpressionSource           expression_source;

    protected ArgumentSerialiser         argument_serialiser;
    protected Scope                      scope;
    protected ActivityAttribute          activity_attribute;

    public Execution execute(CallContext context) {
        return execute(context, null);
    }

    public Execution execute(CallContext context, DataSink<Execution> execution_sink) {
        try {
            Object result;
            if (    this.argument_serialiser == null
                 && (    this.expression_evaluator != null
                      && this.expression_evaluator instanceof ExpressionSourceAware)) {
                result = ((ExpressionSourceAware) this.expression_evaluator).evaluate(context, this.expression_source, this.scope, execution_sink);
            // gibt's noch nicht, könnte aber
            // } else if (    this.argument_serialiser == null
            //             && (    this.expression_evaluator_service != null
            //                  && this.expression_evaluator_service instanceof ExpressionSourceAwareService)) {
            //     result = ((ExpressionSourceAwareService) this.expression_evaluator_service).evaluateWithEvaluator(context, this.expression_source, evaluator_id, this.scope, execution_sink);
            } else {
                if (this.argument_serialiser == null) {
                    this.argument_serialiser = new ArgumentSerialiser_ByValue(context, null);
                }
                String expression = this.expression_source.getString(context, this.argument_serialiser, this.scope);
                result = this.expression_evaluator_service != null
                          ? this.expression_evaluator_service.evaluateWithEvaluator(context, expression, evaluator_id, this.scope, null, null, execution_sink)
                          : this.expression_evaluator.evaluate(context, expression, this.scope, execution_sink);
            }
            this.data = new Class_ActivityData(context);
            this.data.addSlot(context, new Class_ActivitySlot(context, this.activity_attribute, result));
        } catch (EvaluationFailure ef) {
            return Factory_Execution.createExecutionFailure(context, ef);
        }
        return Factory_Execution.createExecutionSuccess(context);
    }

    protected Class_ActivityData data;

    public ActivityData getData(CallContext context) {
        return this.data;
    }

    public String toString(CallContext context) {
        String[] ids = expression_evaluator.getIds(context);
        return ids[ids.length-1]  + ":" + expression_source.toString(context);
    }
}
