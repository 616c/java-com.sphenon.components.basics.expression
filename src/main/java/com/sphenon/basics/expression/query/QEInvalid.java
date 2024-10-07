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

public class QEInvalid extends QueryExpression {

    public QEInvalid(CallContext context, Exception exception) {
        super(context);
        this.exception = exception;
    }

    protected Exception exception;

    public Exception getException (CallContext context) {
        return this.exception;
    }

    public void setException (CallContext context, Exception exception) {
        this.exception = exception;
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(this.exception);
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        buffer.append(this.exception);
    }
}
