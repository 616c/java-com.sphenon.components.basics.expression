package com.sphenon.basics.expression.factories;

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

public class Factory_ActivityClass {

    public Factory_ActivityClass(CallContext context) {
    }

    protected String expression;

    public String getExpression (CallContext context) {
        return this.expression;
    }

    public void setExpression (CallContext context, String expression) {
        this.expression = expression;
    }

    public ActivityClass create (CallContext context) {
        try {
            return Expression.parse(context, this.expression);
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ef, "Cannot create activity class for expression '%(expression)'", "expression", this.expression);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }
}
