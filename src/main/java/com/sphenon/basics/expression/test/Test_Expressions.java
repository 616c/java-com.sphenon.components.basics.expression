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

public class Test_Expressions extends com.sphenon.basics.testing.classes.TestBase {

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.testing.test.Test_Operation"); };

    public Test_Expressions (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "Expressions";
        }
        return this.id;
    }

    static public class Arguments {
        public Arguments(CallContext context) {
        }
        protected Object[] arguments;
        public void set_ParametersAtOnce(CallContext call_context, String[] names, Object[] values) {
            this.arguments = new Object[names.length * 2];
            for (int i=0, j=0; i<names.length; i++) {
                this.arguments[j++] = names[i];
                this.arguments[j++] = values[i];
            }
        }
        public Object[] getArguments(CallContext context) {
            return this.arguments;
        }
    }

    static public class TestCase {
        public TestCase(CallContext context) {
        }
        protected String e;
        public String getE(CallContext context) {
            return this.e;
        }
        public void setE(CallContext context, String e) {
            this.e = e;
        }
        protected Arguments arguments;
        public Arguments getArguments(CallContext context) {
            return this.arguments;
        }
        public Arguments defaultArguments(CallContext context) {
            return new Arguments(context);
        }
        public void setArguments(CallContext context, Arguments arguments) {
            this.arguments = arguments;
        }
    }

    protected TestCase[] test_cases;

    public TestCase[] getTestCases (CallContext context) {
        return this.test_cases;
    }

    public void setTestCases (CallContext context, TestCase[] test_cases) {
        this.test_cases = test_cases;
    }

    public TestResult perform (CallContext call_context, TestRun test_run) {
        Context context = Context.create(call_context);

        try {

            for (TestCase test_case : getTestCases(context)) {

                Object result = (new Expression(context, test_case.getE(context))).evaluate(context, test_case.getArguments(context).getArguments(context));

                if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "Expression: '%(expression)' ==> '%(class)' : '%(result)'", "expression", test_case.getE(context), "class", result.getClass(), "result", result); }

            }

            return new TestResult_Success(context);
            
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
    }
}
