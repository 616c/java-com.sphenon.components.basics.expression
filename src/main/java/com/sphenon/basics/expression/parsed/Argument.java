package com.sphenon.basics.expression.parsed;

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

public class Argument extends ExpressionBase {

    protected Expression c1;
    protected Expression c2;

    public Argument (CallContext context, Expression c1, Expression c2) {
        super(context);
        this.c1 = c1;
        this.c2 = c2;
    }

    public Object getValue(CallContext context, Scope scope) throws EvaluationFailure {
        Object name  = this.c1.getValue(context, scope);
        if ((name instanceof String) == false) {
            EvaluationFailure.createAndThrow(context, "Identifier '%(name)' is not a string", "name", name);
            throw (EvaluationFailure) null;
        }
        Object value = this.c2.getValue(context, scope);
        scope.trySet(context, (String) name, value);
        return value;
    }

    public Expression getLeftExpression(CallContext context) {
        return c1;
    }

    public Expression getRightExpression(CallContext context) {
        return c2;
    }
}
