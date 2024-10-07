package com.sphenon.basics.expression;

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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.regex.*;

public class DynamicStringProcessor_Placeholder implements ExpressionEvaluator {

    public DynamicStringProcessor_Placeholder (CallContext context) {
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    static protected RegularExpression ph_regexp = new RegularExpression(
        "\\$\\{"
      +   "(?:"
      +     "(?:"
      +       "([A-Za-z0-9_]+)"               // varname
      +       "([^/${};]+)?"                  // dstail
      +     ")"
      +     "|(?:"
      +       "(\\[[A-Za-z0-9_]+\\])?"        // base
      +       "(?:"
      +         "(?:"
      +           "\"([^${}\"]+)?\""              // loc1
      +         ")"
      +         "|(?:"
      +           "'([^${}']+)?'"                 // loc2
      +         ")"
      +         "|(?:"
      +           "(//[^${};]+)?"                 // loc3
      +         ")"
      +       ")"
      +     ")"
      +   ")"
      +   "(?:"
      +     "/([A-Za-z0-9_]+)"
      +     "/([A-Za-z0-9_]+"
      +       "(?:"
      +         "\\([^${};]+\\)"
      +       ")?"
      +     ")"
      +   ")?"
      +   "(?:"
      +     ";([^;}]*)"
      +     "(?:"
      +       ";([^;}]*)"
      +       "(?:"
      +         ";"
      +         "/([A-Za-z0-9_]+)"
      +         "/([A-Za-z0-9_]+"
      +           "(?:"
      +             "\\([^${};]+\\)"
      +           ")?"
      +         ")"
      +       ")?"
      +     ")?"
      +   ")?"
      + "\\}");

    // ${ varname dstail? | [base]?   "loc1"|'loc2'|//loc3   /rec1/rec2(...) ;prefix ;postfix   ; /rec3/rec4(...) }
    //                                                                       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //                                                                            only applied if non empty

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return new String[] { "p", "placeholder" };
    }

    public Object evaluate(CallContext context, String string, Scope scope, DataSink<Execution> execution_sink) throws EvaluationFailure {
        ExecutionHandler eh = new ExecutionHandler(context, execution_sink, null);
        try{
            Matcher m = ph_regexp.getMatcher(context, string);
            StringBuffer sb = new StringBuffer();
            int last_end = 0;
            while (m.find()) {
                String varname = m.group(1);
                String dstail = m.group(2);
                String base = m.group(3);
                String loc1 = m.group(4);
                String loc2 = m.group(5);
                String loc3 = m.group(6);
                String rec1 = m.group(7);
                String rec2 = m.group(8);
                String pre  = m.group(9);
                String post = m.group(10);
                String rec3 = m.group(11);
                String rec4 = m.group(12);
                String loc = null;
                boolean optional_var = false;
                sb.append(Encoding.recode(context, string.substring(last_end, m.start()), Encoding.URI, Encoding.UTF8));
                last_end = m.end();
                String value = null;
                if (loc1 != null   && loc1.isEmpty() == false  ) { loc = "locator:" + (base == null ? "" : base) + loc1; }
                if (loc2 != null   && loc2.isEmpty() == false  ) { loc = "locator:" + (base == null ? "" : base) + loc2; }
                if (loc3 != null   && loc3.isEmpty() == false  ) { loc = "locator:" + (base == null ? "" : base) + loc3; }
                if (dstail != null && dstail.isEmpty() == false) { if (dstail.equals("?")) { optional_var = true; }
                                                                   else                    { loc = varname + dstail; }
                                                                 }
                if (loc == null) {
                    if (scope == null) {
                        eh.reportAndThrow(context, EvaluationFailure.create(context, "While processing a dynamic string, no scope to lookup variable '%(name)' was provided, string is '%(string)'", "name", varname, "string", string));
                        throw (EvaluationFailure) null;
                    }
                    try {
                        Object ov = scope.get(context, varname);
                        value = ov == null ? null : t.s(context, ov);
                    } catch (NoSuchVariable nsv) {
                        if (optional_var) {
                            value = null;
                        } else {
                            eh.reportAndThrow(context, EvaluationFailure.create(context, "While processing a dynamic string, variable '%(name)' was not defined in current scope, string is '%(string)'", "name", varname, "string", string));
                            throw (EvaluationFailure) null;
                        }
                    }
                } else {
                    value = ContextAware.ToString.convert(context, Expression.evaluate(context, Encoding.recode(context, loc, Encoding.URI, Encoding.UTF8), scope));
                }
                if (rec1 != null && rec2 != null) {
                    value = (value == null ? null : Encoding.recode(context, value, rec1, rec2));
                }
                if (value != null && value.isEmpty() == false) {
                    if (pre != null && pre.isEmpty() == false) { sb.append(Encoding.recode(context, pre, Encoding.URI, Encoding.UTF8)); }
                    if (rec3 != null && rec4 != null) {
                        value = (value == null ? null : Encoding.recode(context, value, rec3, rec4));
                    }
                    sb.append(value);
                    if (post != null && post.isEmpty() == false) { sb.append(Encoding.recode(context, post, Encoding.URI, Encoding.UTF8)); }
                }
            }
            sb.append(Encoding.recode(context, string.substring(last_end, string.length()), Encoding.URI, Encoding.UTF8));

            eh.reportSuccess(context);
            return sb.toString();
        } catch (EvaluationFailure t) {
            eh.handleFinally(context, t);
            throw (EvaluationFailure) null;
        } catch (Throwable t) {
            eh.handleFinally(context, EvaluationFailure.create(context, t, "Unexpected exception"));
            throw (Error) null;
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
