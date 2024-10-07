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

public class QENumber extends QEValue {

    public QENumber(CallContext context, String value) {
        super(context);
        this.value = value == null ? 0L : (value.matches("^[0-9]+$") ? ((Object) Long.parseLong(value)) : ((Object) Double.parseDouble(value)));
    }

    public QENumber(CallContext context, Object value) {
        super(context);
        this.value = value;
    }

    protected Object value;

    public Object getValue (CallContext context) {
        return this.value;
    }

    public void setValue (CallContext context, Object value) {
        this.value = value;
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(this.value);
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        buffer.append(this.value);
    }
}
