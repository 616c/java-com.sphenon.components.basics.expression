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
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.services.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public interface ExpressionEvaluatorService extends Service {

    public Object evaluateWithEvaluator(CallContext context, String code, String evaluator_id, Scope current_scope, String actor_id, String session_id, DataSink<Execution> execution_sink) throws EvaluationFailure;

    public ActivityClass parseWithEvaluator(CallContext context, ExpressionSource expression_source, String evaluator_id) throws EvaluationFailure;
}
