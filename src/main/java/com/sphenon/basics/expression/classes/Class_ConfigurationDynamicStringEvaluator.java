package com.sphenon.basics.expression.classes;

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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.expression.*;

public class Class_ConfigurationDynamicStringEvaluator implements ConfigurationDynamicStringEvaluator {

    protected java.util.Hashtable parameters;

    public Class_ConfigurationDynamicStringEvaluator (CallContext context) {
    }

    public void setParameters (CallContext context, java.util.Hashtable parameters) {
        this.parameters = parameters;
    }

    public String evaluate (CallContext context, String expression) {
        return DynamicString.process(context, expression, null, this.parameters);
    }

    public String evaluate (CallContext context, String expression, java.util.Hashtable parameters) {
        return DynamicString.process(context, expression, null, parameters);
    }
}