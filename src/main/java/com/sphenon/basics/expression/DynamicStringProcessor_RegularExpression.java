package com.sphenon.basics.expression;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

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

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.regex.*;

public class DynamicStringProcessor_RegularExpression implements ExpressionEvaluator {

    static public DynamicStringProcessor_RegularExpression createFromString(CallContext context, String specification) {
        String[] parts = specification.split(";");
        String[] ids = parts[0].split(",");
        String[][] regexp_strings = new String[parts.length-1][];
        for (int p=1; p<parts.length; p++) {
            regexp_strings[p-1]    = parts[p].split("=",2);
            regexp_strings[p-1][0] = Encoding.recode(context, regexp_strings[p-1][0], Encoding.URI, Encoding.UTF8);
            regexp_strings[p-1][1] = Encoding.recode(context, regexp_strings[p-1][1], Encoding.URI, Encoding.UTF8);
        }
        return new DynamicStringProcessor_RegularExpression (context, ids, regexp_strings);
    }

    protected DynamicStringProcessor_RegularExpression (CallContext context, String[] ids) {
        this.ids              = ids;
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    public DynamicStringProcessor_RegularExpression (CallContext context, String[] ids, String[][] regexp_strings) {
        this(context, ids);
        this.embedded_regexps = false;
        this.regexps = new RegularExpression[regexp_strings.length];
        int i=0;
        for (String[] rs : regexp_strings) {
            this.regexps[i++] = new RegularExpression(context, rs[0], rs[1]);
        }
    }

    public DynamicStringProcessor_RegularExpression (CallContext context) {
        this(context, new String[] { "regexp" });
        this.embedded_regexps = true;
        this.regexps          = null;
    }

    protected String[]            ids;
    protected RegularExpression[] regexps;
    protected boolean             embedded_regexps;

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return ids;
    }

    public Object evaluate(CallContext context, String string, Scope scope) {
        if (embedded_regexps) {
            if (string != null && string.length() > 0) {
                String hexdel = "\\x" + Encoding.hex[string.charAt(0)];
                String ps = "^"+hexdel+"([^"+hexdel+"]*)"+hexdel+"([^"+hexdel+"]*)"+hexdel+"([^"+hexdel+"]*)"+hexdel+"$";
                Pattern p = Pattern.compile(ps);
                Matcher m = p.matcher(string);
                if (m.find()) {
                    String s = Encoding.recode(context, m.group(1), Encoding.URI, Encoding.UTF8);
                    String pat = Encoding.recode(context, m.group(2), Encoding.URI, Encoding.UTF8);
                    String rep = Encoding.recode(context, m.group(3), Encoding.URI, Encoding.UTF8);
                    RegularExpression re = new RegularExpression(context, pat, rep);
                    string = re.replaceAll(context, s);
                    return string;
                }
            }
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "The string to process with 'regexp' dynamic string processor has invalid format '%(string)', expected 'x[^x]*x[^x]*x', where x is an arbitrary delimiter", "string", string);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        } else {
            for (RegularExpression regexp : regexps) {
                string = regexp.replaceAll(context, string);
            }
            return string;
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
