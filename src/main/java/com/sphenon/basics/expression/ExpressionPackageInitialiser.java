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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.message.classes.MessageTextClass;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

public class ExpressionPackageInitialiser {

    static protected boolean initialised = false;

    static {
        initialise();
    }

    static public void initialise () {
        initialise(RootContext.getRootContext());
    }

    static public void initialise (CallContext context) {
        
        if (initialised == false) {
            initialised = true;

            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Static(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Placeholder(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Process(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_RegularExpression(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Identity(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_ProgramStructure(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Bash(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Scope(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Unicode(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_UnicodeJS(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Date(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Expression(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Evaluate(context));
          
            Configuration.loadDefaultProperties(context, com.sphenon.basics.expression.ExpressionPackageInitialiser.class);

            loadDynamicStringProcessors(context, getConfiguration(context));
            loadExpressionEvaluators(context, getConfiguration(context));

            MessageTextClass.dynamic_string_handler =
                new MessageTextClass.DynamicStringHandler() {
                    public String process(CallContext context, String text) {
                        return DynamicString.process(context, text);
                    }
                };

            if (getConfiguration(context).get(context, "SaveExpressionCacheOnExit", false)) {
                Expression.saveCacheOnExit(context);
            }
        }
    }

    static protected Configuration config;
    static public Configuration getConfiguration (CallContext context) {
        if (config == null) {
            config = Configuration.create(RootContext.getInitialisationContext(), "com.sphenon.basics.expression");
        }
        return config;
    }
    
    static public void loadDynamicStringProcessors(CallContext context, Configuration config) {
        String dsps;
        int index = 1;
        while ((dsps = config.get(context, "RegularExpressionLibrary." + index++, (String) null)) != null) {
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, DynamicStringProcessor_RegularExpression.createFromString(context, dsps));
        }
        index = 1;
        while ((dsps = config.get(context, "DynamicStringProcessor_Sequence." + index++, (String) null)) != null) {
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, DynamicStringProcessor_Sequence.createFromString(context, dsps));
        }
    }

    static public void loadExpressionEvaluators(CallContext context, Configuration config) {
        String rel;
        int index = 1;
        while ((rel = config.get(context, "RegularExpressionLibrary." + index++, (String) null)) != null) {
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, ExpressionEvaluator_RegularExpression.createFromString(context, rel));
        }
        String ees;
        index = 1;
        while ((ees = config.get(context, "ExpressionEvaluator_Sequence." + index++, (String) null)) != null) {
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, ExpressionEvaluator_Sequence.createFromString(context, ees));
        }
    }
}
