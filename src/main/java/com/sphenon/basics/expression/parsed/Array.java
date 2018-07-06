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

import java.util.Vector;

public class Array extends ExpressionBase {

    protected Vector<Expression> elements;
    protected String pattern;

    public Array (CallContext context, Vector<Expression> elements) {
        super(context);
        this.elements = elements;
        this.pattern = null;
    }

    public Array (CallContext context, String pattern) {
        super(context);
        this.elements = null;
        this.pattern = pattern;
    }

    public Vector<Expression> getElements(CallContext context, Scope scope) {
        if (this.pattern != null && this.elements == null) {
            Vector<Expression> es = new Vector<Expression>();
            Vector<Variable> matching = scope.getAllVariables(context, this.pattern);
            if (matching != null) {
                for (Variable variable : matching) {
                    es.add(new Name(context, variable.getName(context)));
                }
            }
            this.elements = es;
        }
        return this.elements;
    }

    public Object getValue(CallContext context, Scope scope) throws EvaluationFailure {
        Object[] array;
        if (this.pattern != null && this.elements == null) {
            Vector<Variable> matching = scope.getAllVariables(context, this.pattern);
            array = new Object[matching == null ? 0 : matching.size()];
            int i=0;
            if (matching != null) {
                for (Variable variable : matching) {
                    array[i++] = variable.getValue(context);
                }
            }
        } else {
            array = new Object[this.elements == null ? 0 : this.elements.size()];
            int i=0;
            if (this.elements != null) {
                for (Expression element : this.elements) {
                    array[i++] = element.getValue(context, scope);
                }
            }
        }
        return array;
    }
}
