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

public class QENot extends QELogical {

    public QENot(CallContext context, QETerm expression) {
        super(context);
        this.elements = new ArrayList<QETerm>();
        this.elements.add(expression);
    }

    public QETerm getExpression (CallContext context) {
        return this.elements.get(0);
    }

    public void setExpression (CallContext context, QETerm expression) {
        this.elements.set(0, expression);
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(indent);
        buffer.append(indent + " ¬ ");
        this.getExpression(context).toASTString(context, buffer, indent + "  ");
        buffer.append('\n');
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        buffer.append("¬ ");
        this.getExpression(context).toLogicalExpressionString(context, buffer);
    }
}
