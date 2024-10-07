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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.message.classes.MessageTextClass;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import java.util.Map;
import java.util.HashMap;

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

            Configuration.setConfigurationDynamicStringEvaluator(context, new com.sphenon.basics.expression.classes.Class_ConfigurationDynamicStringEvaluator(context));

            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Static(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Placeholder(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Process(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_RegularExpression(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Unicode(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_UnicodeJS(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Define(context));
            ExpressionEvaluatorRegistry.registerDynamicStringEvaluator(context, new DynamicStringProcessor_Catch(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Identity(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_ProgramStructure(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Bash(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Scope(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Date(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Expression(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Evaluate(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Control(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Define(context));
            ExpressionEvaluatorRegistry.registerExpressionEvaluator(context, new ExpressionEvaluator_Error(context));
          
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
            ExpressionEvaluatorRegistry.registerDynamicStringProcessor_RegularExpression(context, dsps);
        }
        index = 1;
        while ((dsps = config.get(context, "DynamicStringProcessor_Sequence." + index++, (String) null)) != null) {
            ExpressionEvaluatorRegistry.registerDynamicStringProcessor_Sequence(context, dsps);
        }
    }

    static public void loadExpressionEvaluators(CallContext context, Configuration config) {
        String rel;
        int index = 1;
        while ((rel = config.get(context, "RegularExpressionLibrary." + index++, (String) null)) != null) {
            ExpressionEvaluatorRegistry.registerExpressionEvaluator_RegularExpression(context, rel);
        }
        String ees;
        index = 1;
        while ((ees = config.get(context, "ExpressionEvaluator_Sequence." + index++, (String) null)) != null) {
            ExpressionEvaluatorRegistry.registerExpressionEvaluator_Sequence(context, ees);
        }
    }

    static protected RegularExpression define_abbreviation = new RegularExpression("^([A-Za-z0-9_])=(.*)$");
    static protected RegularExpression insert_abbreviation = new RegularExpression("@([A-Za-z0-9_])@");

    static public void registerExceptionHelpers(CallContext context, Configuration configuration) {
        String helpers = configuration.get(context, "ExceptionHelpers", (String) null);
        if (helpers != null) {
            Map<String,String> abbreviations = new HashMap<String,String>();
            for (String helper : helpers.split("#", -1)) {
                if (helper != null && helper.isEmpty() == false) {
                    String[] dam = define_abbreviation.tryGetMatches(context, helper);
                    if (dam != null) {
                        abbreviations.put(dam[0], dam[1]);
                    } else {
                        String h = insert_abbreviation.replaceAll(context, helper, (ctx, matches, matcher, regexp) -> { return abbreviations.get(matches[0]); } );
                        ExceptionHelpTextRegistry.register(context,
                            com.sphenon.basics.expression.ematcher.ExceptionMatcher.parse(context, h));
                    }
                }
            }
        }
    }
}
