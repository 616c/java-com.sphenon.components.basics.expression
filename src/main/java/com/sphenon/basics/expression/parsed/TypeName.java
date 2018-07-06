package com.sphenon.basics.expression.parsed;

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

public class TypeName extends Terminal {

    protected String name;

    static public abstract class ETM {
        static public ETM etm;
        abstract public Object get(CallContext context, String name) throws EvaluationFailure;
        abstract public Object get(CallContext context, Object object) throws EvaluationFailure;
        abstract public boolean equals(CallContext context, Object t1, Object t2);
        abstract public boolean isA(CallContext context, Object t1, Object t2);
    }
    protected ETM getETM(CallContext context) throws EvaluationFailure {
        if (ETM.etm == null) {
            EvaluationFailure.createAndThrow(context, "Expression package is not initialised with a TypeManager, cannot perform type operations");
        }
        return ETM.etm;
    }

    public TypeName (CallContext context, String name) {
        super(context);
        this.name = name;
    }

    public Object getValue(CallContext context, Scope scope) throws EvaluationFailure {
        return this.name;
    }

    protected Object t;

    public Object getType(CallContext context) throws EvaluationFailure {
        if (t == null) {
            t = getETM(context).get(context, this.name);
        }
        return t;
    }

    public boolean equals(CallContext context, Expression e2) throws EvaluationFailure {
        TypeName other;
        try { other = (TypeName) e2; } catch (ClassCastException cce) { return false; }
        Object t1 = this.getType(context);
        Object t2 = other.getType(context);
        return getETM(context).equals(context, t1, t2);
    }

    public boolean isA(CallContext context, Expression e2) throws EvaluationFailure {
        TypeName other;
        try { other = (TypeName) e2; } catch (ClassCastException cce) { return false; }
        Object t1 = this.getType(context);
        Object t2 = other.getType(context);
        return getETM(context).isA(context, t1, t2);
    }

    public boolean isBaseOf(CallContext context, Object o) throws EvaluationFailure {
        Object t1 = getETM(context).get(context, o);
        Object t2 = this.getType(context);
        return getETM(context).isA(context, t1, t2);
    }
}
