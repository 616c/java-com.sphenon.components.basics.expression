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

abstract public class ExpressionBase implements Expression {
    public ExpressionBase(CallContext context) {
    }

    public Boolean isTrue(CallContext context, Scope scope) throws EvaluationFailure {
        Object o = this.getValue(context, scope);
        if (o instanceof Boolean) {
            return ((Boolean) o);
        }
        if (o instanceof String) {
            return (((String) o).isEmpty() == false);
        }
        return false;
    }

    protected boolean is_lhs;
    public void setLHS(CallContext context) {
        this.is_lhs = true;
    }

    protected boolean is_rhs;
    public void setRHS(CallContext context) {
        this.is_rhs = true;
    }

}
