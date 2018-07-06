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

import java.util.Vector;

public class TemplateInstanceArguments {

    public TemplateInstanceArguments (CallContext context, GenericLevel generic_level) {
        this.arguments = new Vector<TemplateInstance>();
        this.generic_level = generic_level;
    }

    protected Vector<TemplateInstance> arguments;
    protected GenericLevel generic_level;

    public Vector<TemplateInstance> getArguments (CallContext context) {
        return this.arguments;
    }

    public GenericLevel getGenericLevel (CallContext context) {
        return this.generic_level;
    }

    public void append(CallContext context, TemplateInstance argument) {
        this.arguments.add(argument);
    }

    public String getExpressionString(CallContext context, GenericLevel result_generic_level, boolean deep, boolean unicode) {
        String expression = "";
        boolean generic = result_generic_level.isGreaterOrEqual(generic_level);

        if (this.arguments != null && this.arguments.size() != 0) {
            boolean first = true;

            expression += (generic ? getOpeningDelimiter(context, unicode) : "_");
            
            for (TemplateInstance arg : this.arguments) {
                expression += (first ? "" : (generic ? "," : "_")) + arg.getExpressionString(context, deep ? result_generic_level : GenericLevel.NONE, deep, unicode);
                first = false;
            }

            expression += (generic ? getClosingDelimiter(context, unicode) : "_");
        }

        return expression;
    }

    public String getOpeningDelimiter(CallContext context, boolean unicode) {
        switch(this.generic_level) {
            case IMPLEMENTATION_LANGUAGE_TEMPLATE : return "<";
            case CODE_GENERATOR_TEMPLATE          : return unicode ? "≤" : "<-";
            case MODEL_TEMPLATE                   : return unicode ? "≦" : "<=";
        }
        return "?";
    }

    public String getClosingDelimiter(CallContext context, boolean unicode) {
        switch(this.generic_level) {
            case IMPLEMENTATION_LANGUAGE_TEMPLATE : return ">";
            case CODE_GENERATOR_TEMPLATE          : return unicode ? "≥" : "->";
            case MODEL_TEMPLATE                   : return unicode ? "≧" : "=>";
        }
        return "?";
    }
}
