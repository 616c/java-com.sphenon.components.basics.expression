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

// [Topic] UI/Retriever Queries; Test0070_QueryExpressions.tsx, QueryExpressionParser.jj, FilterBuilderByQueryExpression.java, GenericFilterBuilder.java, GenericRetrieverFilterBuilder.java, Test_QueryExpressions.java, QueryExpression.java

abstract public class QueryExpression {

    public QueryExpression(CallContext context) {
    }

    static public QETerm create(CallContext context, String expression) throws InvalidQueryExpression {
        try {
            return QueryExpressionParser.parse(context, expression);
        } catch (ParseException pe) {
            InvalidQueryExpression.createAndThrow(context, pe, "Could not parse query expression");
            throw (InvalidQueryExpression) null;
        }
    }

    public String toASTString(CallContext context) {
        StringBuffer buffer = new StringBuffer();
        toASTString(context, buffer, "");
        return buffer.toString();
    }

    abstract protected void toASTString(CallContext context, StringBuffer buffer, String indent);

    public String toFilterTreeString(CallContext context) {
        StringBuffer buffer = new StringBuffer();
        return buffer.toString();
    }

    public String toLogicalExpressionString(CallContext context) {
        StringBuffer buffer = new StringBuffer();
        toLogicalExpressionString(context, buffer);
        return buffer.toString();
    }

    abstract protected void toLogicalExpressionString(CallContext context, StringBuffer buffer);
}
