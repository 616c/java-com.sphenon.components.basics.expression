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

abstract public class QECondition extends QETerm {

    public QECondition(CallContext context) {
        super(context);
    }

    protected QEPath path;

    public QEPath getPath (CallContext context) {
        return this.path;
    }

    public void setPath (CallContext context, QEPath path) {
        this.path = path;
    }

    public String getId (CallContext context) {
        return this.path == null ? null : this.path.getId(context);
    }

    public String getTag (CallContext context) {
        return this.path == null ? null : this.path.getTag(context);
    }

    public QEQuantifier getQuantifier (CallContext context) {
        return this.path == null ? null : this.path.getQuantifier(context);
    }

    protected QEMemberFilter member_filter;

    public String getUniqueId (CallContext context) {
        return this.member_filter.getUniqueId(context);
    }

    public void prepare(CallContext context, QEFilters filters) throws InvalidQueryExpression {
        this.member_filter = filters.addMemberFilterCondition(context, this);
    }

    static public QECondition resolve(CallContext context, QEPath path, QECondition qecondition) throws InvalidQueryExpression {
        if (path == null || path.getPath(context) == null) {
            InvalidQueryExpression.createAndThrow(context, "Unexpected: path node contains 'null' path");
            throw (InvalidQueryExpression) null;
        }

        if (path.getPath(context).size() == 0) {
            InvalidQueryExpression.createAndThrow(context, "Unexpected: path node contains empty path");
            throw (InvalidQueryExpression) null;
        }

        List<QEIdentifier> qeids = path.getPath(context);

        QECondition first = null;
        QEObject    last  = null;

        if (qeids.size() == 1) {

            first = qecondition;
            qecondition.setPath(context, path);

        } else {

            while (qeids.size() > 1) {
                QEObject current = new QEObject(context);

                QEPath qepath = new QEPath(context);
                qepath.append(context, qeids.remove(0));
                current.setPath(context, qepath);

                if (first == null) { first = current; }

                if (last == null)  { last = current; }
                else               { last.setCondition(context, current); }
            }

            QEPath qepath = new QEPath(context);
            qepath.append(context, qeids.remove(0));
            qecondition.setPath(context, qepath);
            
            last.setCondition(context, qecondition);
        }

        return first;
    }

    abstract protected void toFilterTreeString(CallContext context, StringBuffer buffer, String indent);
}
