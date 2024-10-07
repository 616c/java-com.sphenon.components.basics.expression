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

public class Not extends ExpressionBase {

    protected Expression c1;
    protected int lower_limit;
    protected int upper_limit;

    public Not (CallContext context, Expression c1) {
        this(context, c1, -1, -1);
    }

    // limits are presently not supported - see below

    public Not (CallContext context, Expression c1, int lower_limit, int upper_limit) {
        super(context);
        this.c1 = c1;
        this.c1.setRHS(context);
        this.lower_limit = lower_limit;
        this.upper_limit = upper_limit;
    }

    public Object getValue(CallContext context, Scope scope) throws EvaluationFailure {
        if (c1 instanceof Array) {
            Vector<Expression> elements = ((Array) c1).getElements(context, scope);
            int size = 0;
            int true_ones = 0;
            int false_ones = 0;
            if (elements != null) {
                for (Expression element : elements) {
                    element.setRHS(context);
                    size++;
                    if (element.isTrue(context, scope)) {
                        true_ones++;
                    } else {
                        false_ones++;
                    }
                }
            }

            return false;

            // limits are presently not supported - how to interpret them ?!
            // if this is added, also "Array.isTrue..." should be implemented
            // (it returns always false at the moment

            // return (    (lower_limit == -1 ? not_equal >= size :
            //              lower_limit == -2 ? not_equal >= size && size >= 1 :
            //                                  not_equal >= lower_limit)
            //          && (upper_limit == -1 || upper_limit == -2 ? not_equal <= size : not_equal <= upper_limit)
            //        );
        } else {
            return (false == c1.isTrue(context, scope));
        }
    }

    public Expression getExpression(CallContext context) {
        return c1;
    }
}
