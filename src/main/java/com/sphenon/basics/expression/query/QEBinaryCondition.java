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

abstract public class QEBinaryCondition extends QECondition {

    public QEBinaryCondition(CallContext context, QEValue value, String operator) {
        super(context);
        this.value = value;
        this.operator = operator;
    }

    protected String operator;

    public String getOperator (CallContext context) {
        return this.operator;
    }

    protected QEValue value;

    public QEValue getValue (CallContext context) {
        return this.value;
    }

    public void setValue (CallContext context, QEValue value) {
        this.value = value;
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(indent);
        this.path.toASTString(context, buffer, indent + "  ");
        buffer.append(' ' + this.operator + ' ');
        this.value.toASTString(context, buffer, indent + "  ");
        buffer.append('\n');
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        buffer.append(this.getUniqueId(context));
        buffer.append(" " + this.operator + " ");
        this.getValue(context).toLogicalExpressionString(context, buffer);
    }

    protected void toFilterTreeString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(indent).append(this.operator).append(" ");
        this.getValue(context).toLogicalExpressionString(context, buffer);
        buffer.append('\n');
    }
}
