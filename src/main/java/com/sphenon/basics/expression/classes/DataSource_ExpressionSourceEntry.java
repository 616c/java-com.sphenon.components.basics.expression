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
import com.sphenon.basics.data.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

public class DataSource_ExpressionSourceEntry implements DataSource {

    public DataSource_ExpressionSourceEntry(CallContext context, ExpressionSourceEntry ese, Scope scope) {
        this.ese = ese;
        this.scope = scope;
    }

    protected ExpressionSourceEntry ese;
    protected Scope scope;
  
    public Object getObject(CallContext context) {
        try {
            return ese.getValue(context, scope);
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Could not retrieve value from ExpressionSourceEntry");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public Object get(CallContext context) {
        return this.getObject(context);
    }
}
