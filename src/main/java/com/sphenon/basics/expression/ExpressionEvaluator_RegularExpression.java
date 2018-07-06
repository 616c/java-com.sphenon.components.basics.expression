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

public class ExpressionEvaluator_RegularExpression implements ExpressionEvaluator {

    static public ExpressionEvaluator_RegularExpression createFromString(CallContext context, String specification) {
        String[] parts = specification.split(";");
        String[] ids = parts[0].split(",");
        String[][] regexp_strings = new String[parts.length-1][];
        for (int p=1; p<parts.length; p++) {
            regexp_strings[p-1]    = parts[p].split("=",2);
            regexp_strings[p-1][0] = Encoding.recode(context, regexp_strings[p-1][0], Encoding.URI, Encoding.UTF8);
            regexp_strings[p-1][1] = Encoding.recode(context, regexp_strings[p-1][1], Encoding.URI, Encoding.UTF8);
        }
        return new ExpressionEvaluator_RegularExpression (context, ids, regexp_strings);
    }

    public ExpressionEvaluator_RegularExpression (CallContext context, String[] ids, String[][] regexp_strings) {
        this.ids = ids;
        this.regexps = new RegularExpression[regexp_strings.length];
        int i=0;
        for (String[] rs : regexp_strings) {
            this.regexps[i++] = new RegularExpression(context, rs[0], rs[1]);
        }
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected String[]            ids;
    protected RegularExpression[] regexps;

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return ids;
    }

    public Object evaluate(CallContext context, String string, Scope scope) {
        for (RegularExpression regexp : regexps) {
            string = regexp.replaceAll(context, string);
        }
        return string;
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
