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

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

public class ExpressionEvaluatorRegistry {

    static {
        ExpressionPackageInitialiser.initialise();
    }

    static public void registerExpressionEvaluator(CallContext context, ExpressionEvaluator evaluator) {
        getDefaultExpressionRegistry(context).registerEvaluator(context, evaluator);
    }

    static public void registerDynamicStringEvaluator(CallContext context, ExpressionEvaluator processor) {
        getDefaultExpressionRegistry(context).registerProcessor(context, processor);
    }

    static protected ExpressionEvaluatorRegistry default_expression_registry;

    static public ExpressionEvaluatorRegistry getDefaultExpressionRegistry (CallContext context) {
        if (default_expression_registry == null) {
            default_expression_registry = new ExpressionEvaluatorRegistry(context, true);
        }
        return default_expression_registry;
    }

    protected boolean lookup_context;

    public boolean getLookupContext (CallContext context) {
        return this.lookup_context;
    }

    public ExpressionEvaluatorRegistry(CallContext context) {
        this(context, false);
    }

    public ExpressionEvaluatorRegistry(CallContext context, boolean lookup_context) {
        this.lookup_context = lookup_context;
    }

    protected Vector<ExpressionEvaluatorRegistry> imports;

    public Vector<ExpressionEvaluatorRegistry> getImports (CallContext context) {
        return this.imports;
    }

    public void setImports (CallContext context, Vector<ExpressionEvaluatorRegistry> imports) {
        this.imports = imports;
    }

    protected HashMap<String,ExpressionEvaluator> evaluator_registry;

    public void registerEvaluator(CallContext context, ExpressionEvaluator evaluator) {
        if (evaluator_registry == null) {
            evaluator_registry = new HashMap<String,ExpressionEvaluator>();
        }
        for (String id : evaluator.getIds(context)) {
            evaluator_registry.put(id, evaluator);
        }
    }

    public ExpressionEvaluator retrieveEvaluator(CallContext context, String id) {
        if (this.lookup_context) {
            ExpressionEvaluatorRegistry eer = null;
            ExpressionContext ec = ExpressionContext.get((Context) context);
            if (ec != null) {
                eer = ec.getExpressionRegistry(context);
            }
            if (eer != null) {
                return eer.retrieveEvaluator(context, id);
            }
        }

        if (evaluator_registry == null) {
            return null;
        }
        ExpressionEvaluator ee = evaluator_registry.get(id);
        if (ee != null) {
            return ee;
        }
        if (this.imports != null) {
            for (ExpressionEvaluatorRegistry j_import : this.imports) {
                ee = j_import.retrieveEvaluator(context, id);
                if (ee != null) {
                    return ee;
                }
            }
        }
        return null;
    }

    protected HashMap<String,ExpressionEvaluator> processor_registry;

    public void registerProcessor(CallContext context, ExpressionEvaluator processor) {
        if (processor_registry == null) {
            processor_registry = new HashMap<String,ExpressionEvaluator>();
        }
        for (String id : processor.getIds(context)) {
            processor_registry.put(id, processor);
        }
    }

    public ExpressionEvaluator retrieveProcessor(CallContext context, String id) {
        if (this.lookup_context) {
            ExpressionEvaluatorRegistry eer = null;
            ExpressionContext ec = ExpressionContext.get((Context) context);
            if (ec != null) {
                eer = ec.getExpressionRegistry(context);
            }
            if (eer != null) {
                return eer.retrieveProcessor(context, id);
            }
        }

        if (processor_registry == null) {
            return null;
        }
        ExpressionEvaluator ee = processor_registry.get(id);
        if (ee != null) {
            return ee;
        }
        if (this.imports != null) {
            for (ExpressionEvaluatorRegistry j_import : this.imports) {
                ee = j_import.retrieveProcessor(context, id);
                if (ee != null) {
                    return ee;
                }
            }
        }
        return null;
    }

    public ExpressionEvaluator retrieve(CallContext context, String evaluator_id) throws EvaluationFailure {
        return this.retrieve(context, evaluator_id, false);
    }

    public ExpressionEvaluator retrieve(CallContext context, String evaluator_id, boolean is_dynamic_string) throws EvaluationFailure {
        if (evaluator_id == null || evaluator_id.isEmpty()) {
            evaluator_id = "identity";
        }
        ExpressionEvaluator evaluator = is_dynamic_string ? null : this.retrieveEvaluator(context, evaluator_id);
        if (evaluator == null) {
            evaluator = this.retrieveProcessor(context, evaluator_id);
        }

        if (evaluator == null) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "No such evaluator '%(id)'", "id", evaluator_id);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        return evaluator;
    }
}
