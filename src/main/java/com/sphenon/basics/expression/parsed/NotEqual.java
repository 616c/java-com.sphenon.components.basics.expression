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

import java.util.Vector;

public class NotEqual extends ExpressionBase {

    protected Expression c1;
    protected Expression c2;
    protected int lower_limit;
    protected int upper_limit;

    public NotEqual (CallContext context, Expression c1, Expression c2) {
        this(context, c1, c2, -1, -1);
    }

    public NotEqual (CallContext context, Expression c1, Expression c2, int lower_limit, int upper_limit) {
        super(context);
        this.c1 = c1;
        this.c2 = c2;
        this.c1.setLHS(context);
        this.c2.setRHS(context);
        this.lower_limit = lower_limit;
        this.upper_limit = upper_limit;
    }

    public Object getValue(CallContext context, Scope scope) throws EvaluationFailure {
        if (c1 instanceof Array) {
            Vector<Expression> elements = ((Array) c1).getElements(context, scope);
            int size = 0;
            int equal = 0;
            int not_equal = 0;
            if (elements != null) {
                for (Expression element : elements) {
                    element.setLHS(context);
                    size++;
                    if (compare(context, element, c2, scope) == true) {
                        equal++;
                    } else {
                        not_equal++;
                    }
                }
            }
            return (    (lower_limit == -1 ? not_equal >= size :
                         lower_limit == -2 ? not_equal >= size && size >= 1 :
                                             not_equal >= lower_limit)
                     && (upper_limit == -1 || upper_limit == -2 ? not_equal <= size : not_equal <= upper_limit)
                   );
        } else {
            return (false == compare(context, c1, c2, scope));
        }
    }

    protected boolean compare(CallContext context, Expression e1, Expression e2, Scope scope) throws EvaluationFailure {
        if (e1 instanceof TypeName && e2 instanceof TypeName) {
            return ((((TypeName) e1).equals(context, ((TypeName) e2))) == false);
        } else if (e1 instanceof Terminal && e2 instanceof Terminal) {
            String s1 = e1.getValue(context, scope).toString();
            String s2 = e2.getValue(context, scope).toString();
            return (s1 == null && s2 == null) || (s1 != null && s2 != null && s1.equals(s2));
        } else {
            return ((e1.isTrue(context, scope) == true) == (e2.isTrue(context, scope) == true));
        }
    }

    public Expression getLeftExpression(CallContext context) {
        return c1;
    }

    public Expression getRightExpression(CallContext context) {
        return c2;
    }
}
