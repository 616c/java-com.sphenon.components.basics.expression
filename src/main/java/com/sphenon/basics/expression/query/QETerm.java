package com.sphenon.basics.expression.query;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

abstract public class QETerm extends QueryExpression {

    public QETerm(CallContext context) {
        super(context);
    }

    protected QEFilters filters;

    public QEFilters getFilters (CallContext context) {
        if (this.filters == null) {
            this.filters = new QEFilters(context, null);
            try {
                this.prepare(context, this.filters);
            } catch (InvalidQueryExpression iqe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, iqe, "Preparation failed");
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
        return this.filters;
    }

    public void setFilters (CallContext context, QEFilters filters) {
        this.filters = filters;
    }

    abstract public void prepare(CallContext context, QEFilters filters) throws InvalidQueryExpression;

    public String toLogicalExpressionString(CallContext context) {
        QEFilters filters = getFilters(context);
        return super.toLogicalExpressionString(context);
    }

    public String toFilterTreeString(CallContext context) {
        QEFilters filters = getFilters(context);
        StringBuffer buffer = new StringBuffer();
        filters.toFilterTreeString(context, buffer, "");
        return buffer.toString();
    }
}
