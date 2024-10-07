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

public class QEObject extends QECondition {

    public QEObject(CallContext context) {
        super(context);
    }

    protected QETerm condition;

    public QETerm getCondition (CallContext context) {
        return this.condition;
    }

    public void setCondition (CallContext context, QETerm condition) {
        this.condition = condition;
    }

    protected QEFilters filters;

    public void prepare(CallContext context, QEFilters filters) throws InvalidQueryExpression {
        super.prepare(context, filters);

        if (this.condition != null) {
            this.filters = new QEFilters(context, this.member_filter);
            this.condition.prepare(context, this.filters);
        }
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(indent);
        this.path.toASTString(context, buffer, indent + "  ");
        buffer.append(" {\n");
        this.condition.toASTString(context, buffer, indent + "  ");
        buffer.append(indent);
        buffer.append("}\n");
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        this.getCondition(context).toLogicalExpressionString(context, buffer);
    }

    protected void toFilterTreeString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(indent).append("<object>\n");
    }
}
