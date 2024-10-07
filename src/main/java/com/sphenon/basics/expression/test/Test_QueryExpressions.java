package com.sphenon.basics.expression.test;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.templates.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.expression.query.*;

import com.sphenon.basics.testing.*;

// [Topic] UI/Retriever Queries; Test0070_QueryExpressions.tsx, QueryExpressionParser.jj, FilterBuilderByQueryExpression.java, GenericFilterBuilder.java, GenericRetrieverFilterBuilder.java, Test_QueryExpressions.java, QueryExpression.java

public class Test_QueryExpressions extends com.sphenon.basics.testing.classes.TestBase {

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.testing.test.Test_Operation"); };

    public Test_QueryExpressions (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "QueryExpressions";
        }
        return this.id;
    }

    protected String[] query_expressions;

    public String[] getQueryExpressions (CallContext context) {
        return this.query_expressions;
    }

    public void setQueryExpressions (CallContext context, String[] query_expressions) {
        this.query_expressions = query_expressions;
    }

    public TestResult perform (CallContext call_context, TestRun test_run) {
        Context context = Context.create(call_context);

        try {

            for (String query_expression : query_expressions) {
                QETerm qe = QueryExpressionParser.parse(context, query_expression);

                if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "QueryExpression: '%(queryexpression)' ==> '%(result)'", "queryexpression", query_expression, "result", "\n" + qe.toASTString(context)); }

                if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "Logical Expression ==> '%(result)'", "result", "\n" + qe.toLogicalExpressionString(context)); }

                if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "Filter Tree ==> '%(result)'", "result", "\n" + qe.toFilterTreeString(context)); }

            }

            return new TestResult_Success(context);
            
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
    }
}
