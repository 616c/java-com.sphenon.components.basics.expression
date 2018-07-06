package com.sphenon.basics.expression.templates;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import java.util.List;
import java.util.ArrayList;

/**
   Represents a (parser) specification of a template instance,
   like "Map<String,String>".

   It is noteworthy, that this class supports a manyfold parametrisation,
   i.e. a template can be instantiated partially by some generics engine A
   and then again instantiated by another generics engine B.

   Therefore, the Arguments attribute of this class is an array of arguments.

   The use case for manyfold parametrisation is: engine B is "JavaGenerics",
   while engine A is a true template preprocessing engine. This is at least
   useful if not necessary in some cases, since JavaGenerics still does
   erase types at runtime and does not support traits and alike.

   Furthermore, a third level of generics might be found in a MDSD environment,
   where generic models are used.
 */
public class TemplateInstance {

    public TemplateInstance (CallContext context, String identifier, TemplateInstanceArguments... arguments) {
        this.identifier = identifier;

        this.number_of_arguments = 0;

        if (arguments == null || arguments.length == 0) {
            this.arguments = null;
        } else {
            this.arguments = new ArrayList<TemplateInstanceArguments>();
            for (TemplateInstanceArguments tia : arguments) {
                if (tia != null) {
                    if (tia.getArguments(context) == null || tia.getArguments(context).size() == 0) {
                        CustomaryContext.create((Context)context).throwPreConditionViolation(context, "TemplateInstance with empty argument list (part)");
                        throw (ExceptionPreConditionViolation) null; // compiler insists
                    }
                    this.number_of_arguments += tia.getArguments(context).size();
                    this.arguments.add(tia);
                }
            }
        }
    }

    protected int number_of_arguments;

    public int getNumberOfArguments (CallContext context) {
        return this.number_of_arguments;
    }

    public void setNumberOfArguments (CallContext context, int number_of_arguments) {
        this.number_of_arguments = number_of_arguments;
    }

    protected String identifier;

    public String getIdentifier (CallContext context) {
        return this.identifier;
    }

    protected List<TemplateInstanceArguments> arguments;

    public List<TemplateInstanceArguments> getArguments (CallContext context) {
        return this.arguments;
    }

    public String getExpressionString(CallContext context) {
        return this.getExpressionString(context, GenericLevel.MODEL_TEMPLATE, true, false);
    }

    public String getExpressionString(CallContext context, GenericLevel generic_level, boolean deep) {
        return this.getExpressionString(context, generic_level, deep, false);
    }

    public String getExpressionString(CallContext context, GenericLevel generic_level, boolean deep, boolean unicode) {
        String expression = this.identifier;
        if (arguments != null) {
            for (TemplateInstanceArguments argument : arguments) {
                if (argument != null) {
                    expression += argument.getExpressionString(context, generic_level, deep, unicode);
                }
            }
        }
        return expression;
    }
}
