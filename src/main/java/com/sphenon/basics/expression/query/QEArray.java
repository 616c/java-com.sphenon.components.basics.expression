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

public class QEArray extends QEValue {

    public QEArray(CallContext context) {
        super(context);
    }

    public QEArray(CallContext context, QEValue... elements) {
        super(context);
        this.elements = new ArrayList<QEValue>(Arrays.asList(elements));
    }

    public Object getValue(CallContext context) {
        List<Object> value_list = new ArrayList<Object>();
        for (QEValue qevalue : this.elements) {
            value_list.add(qevalue.getValue(context));
        }
        return value_list;
    }

    protected List<QEValue> elements;

    public List<QEValue> getElements (CallContext context) {
        return this.elements;
    }

    public void setElements (CallContext context, List<QEValue> elements) {
        this.elements = elements;
    }

    public void append (CallContext context, QEValue qeelement) {
        if (this.elements == null) {
            this.elements = new ArrayList<QEValue>();
        }
        this.elements.add(qeelement);
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        boolean first = true;
        buffer.append("[");
        if (this.elements != null) {
            for (QEValue qeelement : this.elements) {
                if (first) { first = false; } else { buffer.append(','); }
                qeelement.toASTString(context, buffer, indent + "  ");
            }
        }
        buffer.append("]");
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        boolean first = true;
        buffer.append("[");
        if (this.elements != null) {
            for (QEValue qeelement : this.elements) {
                if (first) { first = false; } else { buffer.append(','); }
                qeelement.toLogicalExpressionString(context, buffer);
            }
        }
        buffer.append("]");
    }
}
