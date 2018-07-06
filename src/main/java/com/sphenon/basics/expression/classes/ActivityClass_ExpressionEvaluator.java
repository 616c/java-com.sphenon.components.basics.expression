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
import com.sphenon.basics.encoding.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

public class ActivityClass_ExpressionEvaluator implements ActivityClass {

    public ActivityClass_ExpressionEvaluator(CallContext context, ExpressionEvaluator expression_evaluator, ExpressionSource expression_source, ActivityInterface activity_interface, ActivityAttribute result_attribute, ArgumentSerialiser argument_serialiser) {
        this.expression_evaluator = expression_evaluator;
        this.expression_source = expression_source;
        this.activity_interface = activity_interface;
        this.result_attribute = result_attribute;
        this.argument_serialiser = argument_serialiser;
    }

    public ActivityClass_ExpressionEvaluator(CallContext context, ExpressionEvaluator expression_evaluator, ExpressionSource expression_source, ActivityInterface activity_interface, ActivityAttribute result_attribute, boolean by_value, EncodingStep[] steps) {
        this(context, expression_evaluator, expression_source, activity_interface, result_attribute, by_value ? new ArgumentSerialiser_ByValue(context, steps) : new ArgumentSerialiser_ByName(context, steps));
    }

    public ActivityClass_ExpressionEvaluator(CallContext context, ExpressionEvaluator expression_evaluator, ExpressionSource expression_source, ActivityInterface activity_interface, ActivityAttribute result_attribute) {
        this(context, expression_evaluator, expression_source, activity_interface, result_attribute, null);
    }

    protected ExpressionEvaluator expression_evaluator;
    protected ExpressionSource expression_source;
    protected ArgumentSerialiser argument_serialiser;
    protected EncodingStep[] steps;

    public ExpressionEvaluator getExpressionEvaluator (CallContext context) {
        return this.expression_evaluator;
    }

    public ExpressionSource getExpressionSource (CallContext context) {
        return this.expression_source;
    }

    public ArgumentSerialiser getArgumentSerialiser (CallContext context) {
        return this.argument_serialiser;
    }

    public Activity instantiate(CallContext context, Scope current_scope) throws EvaluationFailure {

        /* ------------------------------------------------------------------------------ */
        // to be improved
        // session verwaltung könnte auch alternativ oder zusätzlich remote
        // durchgeführt werden wollen/sollen/müssen; ggf. sogar müßten die
        // session identifier aufgebort werden o.ä. um sagen zu können
        // bspw. "bash @remote #localsession" oder "bash @remote #remotesession"

        current_scope = Expression.mergeScopeWithSessionScope(context, current_scope, null, null);
        /* ------------------------------------------------------------------------------ */

        return new Activity_ExpressionEvaluator(context, this.expression_evaluator, this.expression_source, this.argument_serialiser, current_scope, this.result_attribute);
    }

    protected ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public ActivityInterface getInterface (CallContext context) {
        return this.activity_interface;
    }
}
