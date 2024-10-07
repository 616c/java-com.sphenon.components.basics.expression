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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.services.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

public class Class_ExpressionEvaluatorService implements ExpressionEvaluatorService {

    public Class_ExpressionEvaluatorService(CallContext context) {
    }

    public void notifyNewConsumer(CallContext context, Consumer consumer) {
    }

    protected ExpressionEvaluatorRegistry registry;

    protected Vector<ExpressionEvaluator> evaluators;

    public Vector<ExpressionEvaluator> getEvaluators (CallContext context) {
        return this.evaluators;
    }

    public void setEvaluators (CallContext context, Vector<ExpressionEvaluator> evaluators) {
        this.evaluators = evaluators;
        this.registry = new ExpressionEvaluatorRegistry(context);
        for (ExpressionEvaluator evaluator : evaluators) {
            this.registry.registerEvaluator(context, evaluator);
        }
    }

    public Object evaluateWithEvaluator(CallContext context, String code, String evaluator_id, Scope current_scope, String actor_id, String session_id, DataSink<Execution> execution_sink) throws EvaluationFailure {
        ExpressionEvaluator evaluator = registry.retrieve(context, evaluator_id, false);
        current_scope = Expression.mergeScopeWithSessionScope(context, current_scope, actor_id, session_id);
        return evaluator.evaluate(context, code, current_scope, execution_sink);
    }

    public ActivityClass parseWithEvaluator(CallContext context, ExpressionSource expression_source, String evaluator_id) throws EvaluationFailure {
        ExpressionEvaluator evaluator = registry.retrieve(context, evaluator_id, false);
        return evaluator.parse(context, expression_source);
    }
}
