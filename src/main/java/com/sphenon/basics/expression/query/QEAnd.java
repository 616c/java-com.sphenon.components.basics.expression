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

public class QEAnd extends QELogical {

    public QEAnd(CallContext context) {
        super(context);
    }

    public QEAnd(CallContext context, QETerm... elements) {
        super(context);
        this.elements = new ArrayList<QETerm>(Arrays.asList(elements));
    }

    public void setElements (CallContext context, List<QETerm> elements) {
        this.elements = elements;
    }

    public void append (CallContext context, QETerm qeelement) {
        if (this.elements == null) {
            this.elements = new ArrayList<QETerm>();
        }
        this.elements.add(qeelement);
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        if (this.elements == null) {
            buffer.append(indent);
            buffer.append("<empty and>");
            buffer.append('\n');
        } else {
            boolean first = true;
            buffer.append(indent);
            buffer.append("(   ");
            for (QETerm qeelement : this.elements) {
                if (first) { first = false; } else { buffer.append(indent).append("  ∧ "); }
                qeelement.toASTString(context, buffer, indent + "    ");
                buffer.append('\n');
            }
            buffer.append(indent);
            buffer.append(")\n");
        }
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        if (this.elements == null) {
            buffer.append("(∧?)");
        } else {
            boolean first = true;
            buffer.append("( ");
            for (QETerm qeelement : this.elements) {
                if (first) { first = false; } else { buffer.append(" ∧ "); }
                qeelement.toLogicalExpressionString(context, buffer);
            }
            buffer.append(" )");
        }
    }
}
