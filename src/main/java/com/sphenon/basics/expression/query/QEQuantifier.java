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

public class QEQuantifier extends QueryExpression implements ContextAware {

    public QEQuantifier(CallContext context) {
        super(context);
    }

    protected QEBoundary lower_limit;

    public QEBoundary getLowerLimit (CallContext context) {
        return this.lower_limit;
    }

    public void setLowerLimit (CallContext context, QEBoundary lower_limit) {
        this.lower_limit = lower_limit;
    }

    protected QEBoundary upper_limit;

    public QEBoundary getUpperLimit (CallContext context) {
        return this.upper_limit;
    }

    public void setUpperLimit (CallContext context, QEBoundary upper_limit) {
        this.upper_limit = upper_limit;
    }

    protected boolean for_all;

    public boolean getForAll (CallContext context) {
        return this.for_all;
    }

    public void setForAll (CallContext context) {
        this.for_all = true;
        this.lower_limit = new QEBoundary(context, 0L  , true);
        this.upper_limit = new QEBoundary(context, null, false);
    }

    protected boolean exists;

    public boolean getExists (CallContext context) {
        return this.exists;
    }

    public void setExists (CallContext context) {
        this.exists = true;
        this.lower_limit = new QEBoundary(context, 1L  , false);
        this.upper_limit = new QEBoundary(context, null, false);
    }

    public boolean isEqualTo(CallContext context, QEQuantifier other) {
        return (    (this.getLowerLimit(context).isEqualTo(context, other.getLowerLimit(context)))
                 && (this.getUpperLimit(context).isEqualTo(context, other.getUpperLimit(context)))
               );
    }

    public String toString(CallContext context) {
        StringBuffer buffer = new StringBuffer();
        this.toLogicalExpressionString(context, buffer);
        return buffer.toString();
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        this.toLogicalExpressionString(context, buffer);
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        if (this.for_all) {
            buffer.append("∀ ");
        }
        if (this.exists) {
            buffer.append("∃ ");
        }
        buffer.append("[");
        lower_limit.toLogicalExpressionString(context, buffer);
        buffer.append("...");
        upper_limit.toLogicalExpressionString(context, buffer);
        buffer.append("]");
    }
}
