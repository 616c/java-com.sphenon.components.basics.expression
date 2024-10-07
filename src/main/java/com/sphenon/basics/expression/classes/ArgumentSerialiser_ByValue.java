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

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public class ArgumentSerialiser_ByValue implements ArgumentSerialiser {

    public ArgumentSerialiser_ByValue(CallContext context, EncodingStep[] steps) {
        this.steps = steps;
    }

    protected EncodingStep[] steps;

    public EncodingStep[] getSteps (CallContext context) {
        return this.steps;
    }

    public String serialise(CallContext context, ExpressionSourceEntry ese, Scope scope) throws EvaluationFailure {
        String name = ese.getName(context);
        Object value = ese.getValue(context, scope);
        String serialised = ContextAware.ToString.convert(context, value);
        if (steps != null) {
            serialised = Encoding.recode(context, serialised, steps);
        }
        return serialised;
    }
}
