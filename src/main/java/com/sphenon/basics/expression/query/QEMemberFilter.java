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

public class QEMemberFilter {

    public QEMemberFilter(CallContext context, String member, String tag, QEFilters parent) {
        this.member = member;
        this.tag = tag;
        this.parent = parent;
    }

    protected QEFilters parent;

    public QEFilters getParent (CallContext context) {
        return this.parent;
    }

    public String getFilterPath(CallContext context) {
        return this.parent.getFilterPath(context) + "." + this.member + "#" + (this.tag == null ? "" : this.tag);
    }

    protected String member;

    public String getMember (CallContext context) {
        return this.member;
    }

    public void setMember (CallContext context, String member) {
        this.member = member;
    }

    protected String tag;

    public String getTag (CallContext context) {
        return this.tag;
    }

    public void setTag (CallContext context, String tag) {
        this.tag = tag;
    }

    protected String unique_id;

    public String getUniqueId (CallContext context) {
        if (this.unique_id == null) {
            if (this.tag != null && this.tag.isEmpty() == false) {
                this.unique_id = "n" + this.tag;
            } else {
                this.unique_id = "i" + this.parent.getNextUniqueId(context);
            }
        }
        return this.unique_id;
    }

    public void setUniqueId (CallContext context, String unique_id) {
        this.unique_id = unique_id;
    }

    protected List<QECondition> conditions;

    public List<QECondition> getConditions (CallContext context) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<QECondition>();
        }
        return this.conditions;
    }

    public void setConditions (CallContext context, List<QECondition> conditions) {
        this.conditions = conditions;
    }

    public void verifyConditions (CallContext context) throws InvalidQueryExpression {
        QECondition c1 = null;
        boolean first = true;
        for (QECondition c2 : conditions) {
            if (first) {
                c1 = c2;
                first = false;
                continue;
            }

            QEQuantifier q1 = c1.getQuantifier(context);
            QEQuantifier q2 = c2.getQuantifier(context);
            if ( false == (    (q1 == null && q2 == null)
                            || (q1 != null && q2 != null && q1.isEqualTo(context, q2))
                          )
               ) {
                InvalidQueryExpression.createAndThrow(context, "Unexpected: condition nodes for member '%(member)' with tag '%(tag)' to be merged contain different quantifiers: '%(q1)' and '%(q2)'", "member", member, "tag", tag, "q1", q1, "q2", q2);
                throw (InvalidQueryExpression) null;
            }

            if (    (c1 instanceof QEObject) == false
                 || (c2 instanceof QEObject) == false
               ) {
                InvalidQueryExpression.createAndThrow(context, "Unexpected: condition nodes for member '%(member)' with tag '%(tag)' to be merged are not both Object nodes", "member", member, "tag", tag);
                throw (InvalidQueryExpression) null;
            }
        }
    }

    protected QEFilters filters;

    public QEFilters getFilters (CallContext context) {
        return this.filters;
    }

    public void setFilters (CallContext context, QEFilters filters) {
        this.filters = filters;
    }

    public void toFilterTreeString(CallContext context, StringBuffer buffer, String indent) {
        if (this.getConditions(context) != null && this.getConditions(context).isEmpty() == false) {
            buffer.append(indent).append("  Conditions:\n");
            for (QECondition condition : this.getConditions(context)) {
                condition.toFilterTreeString(context, buffer, indent + "    ");
            }
        }
        if (this.getFilters(context) != null) {
            buffer.append(indent).append("  Filters:\n");
            filters.toFilterTreeString(context, buffer, indent + "    ");
        }
    }
}
