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

import com.sphenon.basics.testing.*;

public class Test_ExpressionPerformance extends com.sphenon.basics.testing.classes.TestBase {

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.testing.test.Test_Operation"); };

    public Test_ExpressionPerformance (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "ExpressionPerformance";
        }
        return this.id;
    }

    public TestResult perform (CallContext call_context, TestRun test_run) {
        Context context = Context.create(call_context);

        try {

            long times = 100000;
            String string = "s:Hallo";

            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
             
            return new TestResult_Success(context);
            
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
    }

    static protected void evalOldVersion(CallContext context, long times, String string) throws EvaluationFailure {
        if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "Evaluating (old version, %(times) times '%(expression)'", "times", times, "expression", string); }

        Class_ExpressionSource.duration = 0;

        {   long start = System.currentTimeMillis();
            Object result = null;
            for (long i=0; i<times; i++) {
                result = (new Expression(context, string)).evaluate(context);
            }
            long stop = System.currentTimeMillis();
            if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "Result: '%(result)', %(duration) ms, %(parsing) ms", "result", result, "duration", (stop - start), "parsing", Class_ExpressionSource.duration); }
        }
    }
        
    static protected void evalNewVersion(CallContext context, long times, String string) throws EvaluationFailure {
        if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "Evaluating (new version, %(times) times '%(expression)'", "times", times, "expression", string); }

        Class_ExpressionSource.duration = 0;

        {   long start = System.currentTimeMillis();
            Object result = null;
            for (long i=0; i<times; i++) {
                ActivityClass ac = (new Expression(context, string)).parse(context);
                Activity a = ac.instantiate(context, null);
                Execution e = a.execute(context);
                ActivityData ad = a.getData(context);
                result = ad.getSlots(context).get(0).getValue(context);
            }
            long stop = System.currentTimeMillis();
            if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "Result: '%(result)', %(duration) ms, %(parsing) ms", "result", result, "duration", (stop - start), "parsing", Class_ExpressionSource.duration); }
        }
    }
}
