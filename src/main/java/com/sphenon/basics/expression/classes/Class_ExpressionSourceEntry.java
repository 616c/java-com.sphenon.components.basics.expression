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
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public class Class_ExpressionSourceEntry implements ExpressionSourceEntry {

    public Class_ExpressionSourceEntry(CallContext context, String string) {
        this.string = string;
        this.name = null;
        this.expression = null;
    }

    public Class_ExpressionSourceEntry(CallContext context, String name, String expression) {
        this.string = null;
        this.name = name;
        this.expression = expression;
    }

    protected String string;

    public String getString(CallContext context) {
        return this.string;
    }

    protected String name;

    public String getName (CallContext context) {
        return this.name;
    }

    protected String expression;

    public String getExpression (CallContext context) {
        return this.expression;
    }

    public Object getValue (CallContext context, Scope scope) throws EvaluationFailure {
        return this.expression != null ? new Expression(context, this.expression).evaluate(context, scope) : this.string;
    }

    public String toString(CallContext context) {
        return this.expression != null ? ('◂' + this.expression + '▸') : this.string;
    }
}
