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

public class QEPath extends QEValue {

    public QEPath(CallContext context) {
        super(context);
    }

    protected List<QEIdentifier> path;

    public List<QEIdentifier> getPath (CallContext context) {
        return this.path;
    }

    public String getValue(CallContext context) {
        StringBuffer buffer = new StringBuffer();
        toLogicalExpressionString(context, buffer);
        return buffer.toString();
    }

    public void setPath (CallContext context, List<QEIdentifier> path) {
        this.path = path;
    }

    public void append (CallContext context, QEIdentifier qeid) {
        if (this.path == null) {
            this.path = new ArrayList<QEIdentifier>();
        }
        this.path.add(qeid);
    }

    public String getId (CallContext context) {
        return this.path == null || this.path.size() == 0 ? null : this.path.get(0).getId(context);
    }

    public String getTag (CallContext context) {
        return this.path == null || this.path.size() == 0 ? null : this.path.get(0).getTag(context);
    }

    public QEQuantifier getQuantifier (CallContext context) {
        return this.path == null || this.path.size() == 0 ? null : this.path.get(0).getQuantifier(context);
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        if (this.path == null) {
            buffer.append("<null>");
        } else {
            boolean first = true;
            for (QEIdentifier qeid : this.path) {
                if (first) { first = false; } else { buffer.append('.'); }
                qeid.toASTString(context, buffer, indent);
            }
        }
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        if (this.path == null) {
            buffer.append("<null>");
        } else {
            boolean first = true;
            for (QEIdentifier qeid : this.path) {
                if (first) { first = false; } else { buffer.append('.'); }
                qeid.toLogicalExpressionString(context, buffer);
            }
        }
    }
}
