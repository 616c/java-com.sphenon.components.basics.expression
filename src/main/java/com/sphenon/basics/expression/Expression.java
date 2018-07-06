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
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.*;

import java.io.File;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class Expression {

    static {
        ExpressionPackageInitialiser.initialise();
    }

    static protected Map<String,Integer> evaluation_counters_bs;
    static protected Map<Expression,Integer> evaluation_counters_bo;

    static public void dumpEvaluationCounters() {
        for (String key : evaluation_counters_bs.keySet()) {
            System.err.printf("S %09d %s\n", evaluation_counters_bs.get(key), key);
        }
        for (Expression key : evaluation_counters_bo.keySet()) {
            String evaluation = (key.default_evaluators == null ? "" : key.default_evaluators) + "#" + key.expression;
            System.err.printf("O %09d %s\n", evaluation_counters_bo.get(key), evaluation);
        }
    }

    public void logEvaluation(CallContext context) {
        String evaluation = (this.default_evaluators == null ? "" : this.default_evaluators) + "#" + this.expression;
        if (evaluation_counters_bs == null) {
            evaluation_counters_bs = new HashMap<String,Integer>();
        }
        Integer current_bs = evaluation_counters_bs.get(evaluation);
        evaluation_counters_bs.put(evaluation, current_bs == null ? 1 : (current_bs +1));

        if (evaluation_counters_bo == null) {
            evaluation_counters_bo = new HashMap<Expression,Integer>();
        }
        Integer current_bo = evaluation_counters_bo.get(this);
        evaluation_counters_bo.put(this, current_bo == null ? 1 : (current_bo +1));
    }

    static public Object evaluate(CallContext context, String expression) throws EvaluationFailure {
        return new Expression(context, expression).evaluate(context);
    }

    static public Object evaluate(CallContext context, String expression, Scope scope) throws EvaluationFailure {
        return new Expression(context, expression).evaluate(context, scope);
    }

    static public Object evaluate(CallContext context, String expression, String default_evaluators) throws EvaluationFailure {
        return new Expression(context, expression, default_evaluators).evaluate(context);
    }

    static public Object evaluate(CallContext context, String expression, String default_evaluators, Scope scope) throws EvaluationFailure {
        return new Expression(context, expression, default_evaluators).evaluate(context, scope);
    }

    static public Object evaluate(CallContext context, String expression, String default_evaluators, Object... arguments) throws EvaluationFailure {
        return new Expression(context, expression, default_evaluators).evaluate(context, arguments);
    }

    static public Object evaluate(CallContext context, String expression, String default_evaluators, Map<String,Object> arguments) throws EvaluationFailure {
        return new Expression(context, expression, default_evaluators).evaluate(context, arguments);
    }

    static public boolean isTrue(CallContext context, String expression, String default_evaluators, Scope scope) {
        try {
            Object o = evaluate(context, expression, default_evaluators, scope);
            if (o != null && o instanceof Boolean && Boolean.TRUE.equals(o)) {
                return true;
            }
            return false;
        } catch (EvaluationFailure ef) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Invalid expression");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    static public boolean isTrue(CallContext context, String expression, String default_evaluators, Object... arguments) {
        return isTrue(context, expression, default_evaluators, makeScope(context, arguments));
    }

    static public ActivityClass parse(CallContext context, String expression) throws EvaluationFailure {
        return new Expression(context, expression).parse(context);
    }

    static public ActivityClass parse(CallContext context, String expression, String default_evaluators) throws EvaluationFailure {
        return new Expression(context, expression, default_evaluators).parse(context);
    }

    public Expression(CallContext context, String expression) {
        this(context, expression, null, false);
    }

    public Expression(CallContext context, String expression, String default_evaluators) {
        this(context, expression, default_evaluators, false);
    }

    public Expression(CallContext context, String expression, String default_evaluators, boolean is_dynamic_string) {
        this.setExpression(context, expression, default_evaluators);
        this.is_dynamic_string = is_dynamic_string;
        this.caching_enabled = true;
    }

    public Expression(CallContext context, String expression, String default_evaluators, Object... arguments) {
        this(context, expression, default_evaluators, makeScope(context, arguments));
    }

    public Expression(CallContext context, String expression, Scope scope) {
        this(context, expression, null, scope);
    }

    public Expression(CallContext context, String expression, String default_evaluators, Scope scope) {
        this(context, expression, default_evaluators, false);
        this.attachScope(context, scope);
    }

    static public<T> DataSource<T> createDataSource(final CallContext context, final String expression_string, final String default_evaluators, final Object... arguments) {
        return new DataSourceBase<T>() {
            protected Expression expression = new Expression(context, expression_string, default_evaluators, arguments);
            public T get(CallContext context) {
                try {
                    return (T) expression.evaluate(context);
                } catch (EvaluationFailure ef) {
                    CustomaryContext.create((Context)context).throwPreConditionViolation(context, ef, "Expression evaluation in prepared DataSource failed");
                    throw (ExceptionPreConditionViolation) null; // compiler insists
                }
            }
        };
    }

    protected boolean caching_enabled;

    public void disableCaching(CallContext context) {
        this.caching_enabled = false;
    }

    static protected ExpressionEvaluatorServiceLocator expression_evaluator_service_locator;

    static public void setExpressionEvaluatorServiceLocator (CallContext context, ExpressionEvaluatorServiceLocator eesl) {
        expression_evaluator_service_locator = eesl;
    }

    static public ExpressionEvaluatorService findExpressionEvaluatorService (CallContext context, String location) throws EvaluationFailure {
        if (expression_evaluator_service_locator == null) {
            EvaluationFailure.createAndThrow(context, "No expression evaluator service locator available (space package not loaded?)");
            throw (EvaluationFailure) null;
        }

        return expression_evaluator_service_locator.findService(context, location);
    }

    protected String     expression;
    protected String     default_evaluators;
    protected String[][] evaluators;
    protected String     code;
    protected HashMap<String,Object> embedded_arguments;

    public String getExpression (CallContext context) {
        return this.expression;
    }

    public String getDefaultEvaluators (CallContext context) {
        this.prepareExpression(context);
        return this.default_evaluators;
    }

    public String[][] getEvaluators (CallContext context) {
        this.prepareExpression(context);
        return this.evaluators;
    }

    public String getCode (CallContext context) {
        this.prepareExpression(context);
        return this.code;
    }

    static final public String id_char_re = "[A-Za-z0-9_]";
    static final public String id_re       = id_char_re + "+";
    static final public String id_opt_re   = id_char_re + "*";
    static final public String id_opt_g_re = "(" + id_opt_re + ")";
    static final public String actor_re    = id_re;
    static final public String actor_g_re  = "(" + id_re + ")";
    static final public String step_re     = id_re + "(?:=" + id_re + ")?";
    static final public String loc_re      = step_re + "(?:/" + step_re + ")*";
    static final public String loc_g_re    = "(" + loc_re + ")";
    static final public String ssn_re      = id_re;
    static final public String ssn_g_re    = "(" + id_re + ")";
    static final public String eva_re      = id_opt_re   + "(?:%" + actor_re   + ")?" + "(?:@" + loc_re   + ")?" + "(?:#" + ssn_re   + ")?";
    static final public String eva_g_r     = id_opt_g_re + "(?:%" + actor_g_re + ")?" + "(?:@" + loc_g_re + ")?" + "(?:#" + ssn_g_re + ")?";
    static final public String arg_re      = "\\(([^\\)]+)\\)";
    
    static protected RegularExpression template_re = new RegularExpression("^(" + eva_re + "(?:," + eva_re + ")*" + ")" + "(?:" + arg_re + ")?" + ":");
    static protected RegularExpression evaluator_re = new RegularExpression("^" + eva_g_r + "$");

    public String[] setEvaluator (CallContext context, String evaluator_string) {
        String[] em = evaluator_re.tryGetMatches(context, evaluator_string);
        if (em == null) {
            CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "Evaluator string '%(string)' unexpectedly does not match evaluator regexp", "string", evaluator_string);
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
        return em;
    }

    public void setExpression (CallContext context, String expression) {
        this.setExpression (context, expression, null);
    }

    protected boolean expression_prepared;

    public void setExpression (CallContext context, String expression, String default_evaluators) {
        this.expression = expression;
        this.default_evaluators = default_evaluators;
        this.embedded_arguments = null;
        this.expression_prepared = false;
    }

    protected void prepareExpression(CallContext context) {
        if (this.expression_prepared == false && this.expression != null) {
            Matcher m = template_re.getMatcher(context, this.expression);
            boolean dp_empty = (default_evaluators == null || default_evaluators.length() == 0);
            String argument_string = null;
            if (m.find()) {
                String m1 = m.group(1);
                boolean empty = (m1 == null || m1.length() == 0);
                if ( ! empty && ! dp_empty && m1.charAt(0) == ',') {
                    String[] dps = default_evaluators.split(",");
                    String[] ps = m1.split(",");
                    evaluators = new String[ps.length + dps.length][];
                    int i = 0;
                    for (String dp : dps) { evaluators[i++] = setEvaluator(context, dp); }
                    for (String p : ps) { evaluators[i++] = setEvaluator(context, p); }
                } else {
                    String[] ps = (empty ? (dp_empty ? new String[0] : default_evaluators.split(",")) : m1.split(","));
                    evaluators = new String[ps.length][];
                    int i = 0;
                    for (String p : ps) { evaluators[i++] = setEvaluator(context, p); }
                }
                this.code = (empty ? ":" : "") + this.expression.substring(m.end());
                argument_string = m.group(2);
            } else {
                String[] ps;
                String additional_code = null;
                if (! dp_empty) {
                    Matcher md = template_re.getMatcher(context, default_evaluators);
                    if (md.find()) {
                        additional_code = default_evaluators.substring(md.end());
                        default_evaluators = md.group(1);
                        argument_string = md.group(2);
                    }
                    ps = default_evaluators.split(",");
                } else {
                    ps = new String[0];
                }
                evaluators = new String[ps.length][];
                int i = 0;
                for (String p : ps) { evaluators[i++] = setEvaluator(context, p); }
                if (additional_code == null) {
                    this.code = this.expression;
                } else {
                    this.code = additional_code + this.expression;
                }
            }
            if (argument_string != null && argument_string.length() != 0) {
                this.embedded_arguments = new HashMap<String,Object>();
                for (String argument : argument_string.split(",")) {
                    String[] argarr = argument.split("=",2);
                    this.embedded_arguments.put(Encoding.recode(context, argarr[0], Encoding.URI, Encoding.UTF8), Encoding.recode(context, argarr[1], Encoding.URI, Encoding.UTF8));
                }
            }

            this.expression_prepared = true;
        }
    }

    protected Scope attached_scope;

    public void attachScope(CallContext context, Scope attached_scope) {
        this.attached_scope = attached_scope;
    }

    protected boolean is_dynamic_string;

    public boolean getIsDynamicString (CallContext context) {
        return this.is_dynamic_string;
    }

    public Object evaluate(CallContext context) throws EvaluationFailure {
        return evaluate(context, (Scope) null);
    }

    public Object evaluate(CallContext context, Object... arguments) throws EvaluationFailure {
        return this.evaluate(context, makeScope(context, arguments));
    }

    public Object evaluate(CallContext context, Map<String,Object> arguments) throws EvaluationFailure {
        return this.evaluate(context, makeScope(context, arguments));
    }

    static protected class CacheEntry {
        public int count;
        public String expression;
        public String default_evaluators;
    }
    static protected Map<Integer,CacheEntry> expression_cache;
    static public ExpressionJavaCache ejc = null;

    static protected boolean create_expression_cache;
    static protected Boolean load_expression_cache;

    protected boolean doLoadExpressionCache(CallContext context) {
        if (load_expression_cache == null) {
            load_expression_cache = ExpressionPackageInitialiser.getConfiguration(context).get(context, "LoadExpressionCache", false);
        }
        return load_expression_cache;
    }

    protected String[] getExpressionJavaCode(CallContext context) throws EvaluationFailure {
        return null;
    }

    public Object evaluate(CallContext context, Scope scope) throws EvaluationFailure {
        if (scope != null && attached_scope != null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Expression evaluation invoked with scope, but expression is already attached to a scope");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        if (this.caching_enabled && doLoadExpressionCache(context) && ejc != null) {
            try {
                ExpressionJavaCache.Result ejcr = ejc.evaluate(context, this.expression, this.default_evaluators, scope);
                if (ejcr != null) {
                    return ejcr.result;
                }
            } catch (Throwable t) {
                CustomaryContext.create(Context.create(context)).throwConfigurationError(context, t, "Evaluation of translated Expression '%(expression)' failed", "expression", this.expression);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
        }

        this.prepareExpression(context);

        if (this.embedded_arguments != null) {
            scope = new Class_Scope(context, null, true, scope, this.embedded_arguments);
        }
        Object current = code;
        for (String[] evaluator : evaluators) {
            String id       = evaluator[0];
            String actor    = evaluator[1];
            String location = evaluator[2];
            String session  = evaluator[3];

            if (id == null || id.isEmpty()) { continue; }
            
            Scope current_scope = scope != null ? scope : this.attached_scope;

            if (current instanceof ExpressionSource) {
                current = ((ExpressionSource) current).getString(context, new ArgumentSerialiser_ByName(context), current_scope);
            }

            if (location != null && location.isEmpty() == false) {
                ExpressionEvaluatorService ees = findExpressionEvaluatorService(context, location);
                current = ees.evaluateWithEvaluator(context, (String) current, id, current_scope, actor, session);
            } else {
                current = evaluateWithEvaluator(context, (String) current, id, current_scope, actor, session);
            }
        }

        if (this.caching_enabled && create_expression_cache) {
            CacheEntry ce = null;
            Integer hashcode = (this.default_evaluators == null ? 0 : default_evaluators.hashCode()) ^ this.expression.hashCode();
            if (expression_cache == null) {
                expression_cache = new HashMap<Integer,CacheEntry>();
            } else {
                ce = expression_cache.get(hashcode);
            }
            if (ce == null) {
                ce = new CacheEntry();
                ce.expression = this.expression;
                ce.default_evaluators = this.default_evaluators;
                expression_cache.put(hashcode, ce);
            } else {
                if (    ce.expression.equals(this.expression) == false
                     || (    (    ce.default_evaluators == null
                               && this.default_evaluators == null
                             )
                          || (    ce.default_evaluators != null
                               && this.default_evaluators != null
                               && ce.default_evaluators.equals(this.default_evaluators)
                             )
                        ) == false
                   ) {
                    System.err.print("*** WARNING! *** hash code duplicate! (expression cache)\n");
                }
            }
            ce.count++;
        }

        return current;
    }

    public ActivityClass parse(CallContext context) throws EvaluationFailure {
        this.prepareExpression(context);

        Scope scope = this.attached_scope;
        if (this.embedded_arguments != null) {
            scope = new Class_Scope(context, null, true, this.attached_scope, this.embedded_arguments);
        }
        return new ActivityClass_ExpressionEvaluatorSequence(context, evaluators, code, scope, this.getRegistry(context));
    }

    protected ExpressionEvaluatorRegistry registry;

    public ExpressionEvaluatorRegistry getRegistry (CallContext context) {
        if (this.registry == null) {
            this.registry = ExpressionEvaluatorRegistry.getDefaultExpressionRegistry(context);
        }
        return this.registry;
    }

    public void setRegistry (CallContext context, ExpressionEvaluatorRegistry registry) {
        this.registry = registry;
    }


    static public Scope mergeScopeWithSessionScope(CallContext context, Scope current_scope, String actor_id, String session_id) /*throws EvaluationFailure*/ {
        Scope session_scope = null;

        boolean got_session_id = (session_id != null && session_id.isEmpty() == false);
        boolean got_actor_id   = (actor_id != null && actor_id.isEmpty() == false);

        boolean got_session_id_from_scope = false;
        boolean got_actor_id_from_scope   = false;

        if (got_session_id == false && current_scope != null) {
            session_id = (String) current_scope.tryGet(context, "SessionId");
            got_session_id = (session_id != null && session_id.isEmpty() == false);
            got_session_id_from_scope = got_session_id;
        }

        if (got_actor_id == false && current_scope != null) {
            actor_id = (String) current_scope.tryGet(context, "ActorId");
            got_actor_id = (actor_id != null && actor_id.isEmpty() == false);
            got_actor_id_from_scope = got_actor_id;
        }

        if (got_session_id) {
            session_scope = ExpressionContext.tryGetScope(context, session_id);
            if (session_scope == null) {
                session_scope = new Class_Scope(context, "session");
                ExpressionContext.putScope(context, session_id, session_scope);
            }
        }

        Class_Scope local_scope = null;
        if (    (got_session_id && ! got_session_id_from_scope)
             || (got_actor_id && ! got_actor_id_from_scope)
             || (session_scope != null && current_scope != null)
           ) {
            local_scope = new Class_Scope(context);

            if (got_session_id && ! got_session_id_from_scope) {
                local_scope.set(context, "SessionId", session_id);
            }
            if (got_actor_id && ! got_actor_id_from_scope) {
                local_scope.set(context, "ActorId", actor_id);
            }
            if (current_scope != null) {
                local_scope.addParent(context, current_scope);
            }
            if (session_scope != null) {
                local_scope.addParent(context, session_scope);
            }

            local_scope.setIsSealed(context, true);
            current_scope = local_scope;
        }

        if (current_scope == null && session_scope != null) {
            current_scope = session_scope;
        }

        return current_scope;
    }

    public Object evaluateWithEvaluator(CallContext context, String code, String evaluator_id, Scope current_scope, String actor_id, String session_id) throws EvaluationFailure {
        return evaluateWithEvaluator(context, code, evaluator_id, current_scope, actor_id, session_id, this.is_dynamic_string, this.getRegistry(context));
    }

    static public Object evaluateWithEvaluator(CallContext context, String code, String evaluator_id, Scope current_scope, String actor_id, String session_id, boolean is_dynamic_string, ExpressionEvaluatorRegistry registry) throws EvaluationFailure {
        if (registry == null) {
            registry = ExpressionEvaluatorRegistry.getDefaultExpressionRegistry(context);
        }
        ExpressionEvaluator evaluator = registry.retrieve(context, evaluator_id, is_dynamic_string);

        current_scope = mergeScopeWithSessionScope(context, current_scope, actor_id, session_id);

        return evaluator.evaluate(context, code, current_scope);
    }

    static public ActivityClass parseWithEvaluator(CallContext context, String code, String evaluator_id, boolean is_dynamic_string, ExpressionEvaluatorRegistry registry) throws EvaluationFailure {
        return parseWithEvaluator(context, new Class_ExpressionSource(context, code, false), evaluator_id, is_dynamic_string, registry);

    }

    static public ActivityClass parseWithEvaluator(CallContext context, ExpressionSource expression_source, String evaluator_id, boolean is_dynamic_string, ExpressionEvaluatorRegistry registry) throws EvaluationFailure {
        if (registry == null) {
            registry = ExpressionEvaluatorRegistry.getDefaultExpressionRegistry(context);
        }
        ExpressionEvaluator evaluator = registry.retrieve(context, evaluator_id, is_dynamic_string);

        ActivityClass ac = evaluator.parse(context, expression_source);
        return ac;
    }

    static protected Scope makeScope(CallContext context, Object... arguments) {
        if (arguments != null && arguments.length % 2 != 0) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Expression evaluation invoked with odd number of arguments");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        Class_Scope scope = new Class_Scope(context);
        for (int i=0; i<arguments.length; i+=2) {
            try {
                scope.set(context, (String) arguments[i], arguments[i+1]);
            } catch (ClassCastException cce) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Expression evaluation invoked with an agument name which is not a String, but a '%(class)'", "class", arguments[i].getClass());
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
        scope.setIsSealed(context, true);
        return scope;
    }

    static protected Scope makeScope(CallContext context, Map<String,Object> arguments) {
        Class_Scope scope = new Class_Scope(context);
        for (String key : arguments.keySet()) {
            scope.set(context, key, arguments.get(key));
        }
        scope.setIsSealed(context, true);
        return scope;
    }

    public String toString() {
        return super.toString() + " [ " + expression + " ] ";
    }

    static public void dumpExpressions(CallContext context) {
        if (create_expression_cache) {
            for (Integer hashcode : expression_cache.keySet()) {
                CacheEntry ce = expression_cache.get(hashcode);
                System.err.printf("%8s %s %s\n", ce.count, ce.default_evaluators, ce.expression);
            }
        }
    } 

    static protected String cache_file_name;

    static public void saveCacheOnExit(CallContext context) {
        cache_file_name = ExpressionPackageInitialiser.getConfiguration(context).get(context, "ExpressionJavaCacheFile", (String) null);
        java.lang.Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { saveCache(RootContext.getDestructionContext()); } });
        create_expression_cache = true;
    }

    static public void saveCache(CallContext context) {
        if (create_expression_cache) {
            try {
                if (cache_file_name != null) {
                    File f = new File(cache_file_name);
                    f.setWritable(true);
                    FileOutputStream fos = new FileOutputStream(f);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                    BufferedWriter bw = new BufferedWriter(osw);
                    PrintWriter pw = new PrintWriter(bw);
                    
                    pw.print("package com.sphenon.basics.expression;\n");
                    pw.print("\n");
                    pw.print("import com.sphenon.basics.context.*;\n");
                    pw.print("import com.sphenon.basics.context.classes.*;\n");
                    pw.print("import com.sphenon.basics.debug.*;\n");
                    pw.print("import com.sphenon.basics.message.*;\n");
                    pw.print("import com.sphenon.basics.notification.*;\n");
                    pw.print("import com.sphenon.basics.exception.*;\n");
                    pw.print("import com.sphenon.basics.customary.*;\n");
                    pw.print("import com.sphenon.basics.configuration.*;\n");
                    pw.print("import com.sphenon.basics.expression.*;;\n");
                    pw.print("\n");
                    pw.print("import java.util.Hashtable;\n");
                    pw.print("import java.util.Vector;\n");
                    pw.print("import java.util.Map;\n");
                    pw.print("import java.util.HashMap;\n");
                    pw.print("import java.util.Set;\n");
                    pw.print("import java.util.HashSet;\n");
                    pw.print("\n");
                    pw.print("public class ExpressionJavaCacheImpl implements ExpressionJavaCache {\n");
                    pw.print("\n");
                    pw.print("    static protected Map<Integer,Expression> js_expressions;\n");
                    pw.print("    protected Result evaluateJSExpression(CallContext context, Integer key, String expression, Scope scope) throws Throwable {\n");
                    pw.print("        Expression jse = null;\n");
                    pw.print("        if (js_expressions == null) {\n");
                    pw.print("            js_expressions = new HashMap<Integer,Expression>();\n");
                    pw.print("        } else {\n");
                    pw.print("            jse = js_expressions.get(key);\n");
                    pw.print("        }\n");
                    pw.print("        if (jse == null) {\n");
                    pw.print("            jse = new Expression(context, expression, \"js\");\n");
                    pw.print("            jse.disableCaching(context);\n");
                    pw.print("            js_expressions.put(key, jse);\n");
                    pw.print("        }\n");
                    pw.print("        return new Result(jse.evaluate(context, scope));\n");
                    pw.print("    }\n");
                    pw.print("\n");
                    pw.print("    public Result evaluate(CallContext context, String expression, String default_evaluators, Scope scope) throws Throwable {\n");
                    pw.print("        Result result = null;\n");
                    pw.print("        int hashcode = (default_evaluators == null ? 0 : default_evaluators.hashCode()) ^ expression.hashCode();\n");
                    pw.print("        switch (hashcode) {\n");

                    for (Integer hashcode : expression_cache.keySet()) {
                        CacheEntry ce = expression_cache.get(hashcode);
                        if ( ! skipExpression(context, ce.expression, ce.default_evaluators)) {
                            // String hcpfx = hashcode > 0 ? ("" + hashcode) : ("_" + (hashcode * -1));
                            pw.print("            case " + hashcode + ": // " + ce.count + "\n");
                            if ( ! writeConvertedExpressionToJavaCode(context, ce.expression, ce.default_evaluators, pw, hashcode)) {
                                pw.print("               // " + ce.default_evaluators + " " + Encoding.recode(context, ce.expression, Encoding.UTF8, Encoding.JAVA) + "\n");
                                pw.print("                // result = new Result(...);\n");
                            }
                            pw.print("                break;\n");
                        }
                    }

                    pw.print("        }\n");
                    pw.print("        return result;\n");
                    pw.print("    }\n");
                    pw.print("}\n");
                    pw.print("\n");
                    
                    pw.close();
                    bw.close();
                    osw.close();
                    fos.close();
                }
            } catch (FileNotFoundException fnfe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, fnfe, "Cannot write to file '%(filename)'", "filename", cache_file_name);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            } catch (UnsupportedEncodingException uee) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, uee, "Cannot write to file '%(filename)'", "filename", cache_file_name);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            } catch (IOException ioe) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, ioe, "Cannot write to file '%(filename)'", "filename", cache_file_name);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            }
        }
    }

    static protected boolean skipExpression(CallContext context, String expression, String default_evaluators) {
        if (    expression.startsWith("jsppreloc:")
             || expression.startsWith("jsppreoptvar:")
             || (    default_evaluators != null
                  && (    default_evaluators.equals("jsppreloc")
                       || default_evaluators.equals("jsppreoptvar")
                     )
                )
           ) {
            return true;
        }
        return false;
    }

    static protected boolean writeConvertedExpressionToJavaCode(CallContext context, String expression, String default_evaluators, PrintWriter pw, Integer hc) {
        boolean jspp1 = false;
        boolean jspp2 = false;
        boolean js1 = false;
        boolean js2 = false;
        if (    (    default_evaluators != null
                  && (    (jspp1 = default_evaluators.equals("jspp"))
                       || (js1 = default_evaluators.equals("js"))
                     )
                  && template_re.matches(context, expression) == false
                )
             || (jspp2 = expression.startsWith("jspp:"))
             || (js2 = expression.startsWith("js:"))
           ) {
            try {
                if (jspp1 || jspp2) {
                    ExpressionEvaluatorRegistry registry = ExpressionEvaluatorRegistry.getDefaultExpressionRegistry(context);
                    if (jspp2) {
                        expression = expression.substring(5);
                    }
                    expression = (String) evaluateWithEvaluator(context, expression, "jsppreloc", null, null, null, false, registry);
                    expression = (String) evaluateWithEvaluator(context, expression, "jsppreoptvar", null, null, null, false, registry);
                } else {
                    if (js2) {
                        expression = expression.substring(3);
                    }
                }
                pw.print("                result = evaluateJSExpression(context, " + hc + ", "
                                            + "\"" + Encoding.recode(context, expression, Encoding.UTF8, Encoding.JAVA) + "\""
                                            + ", scope);\n");

            } catch (EvaluationFailure ef) {
                System.err.println("During expression cache save, could not pre-evaluate");
                ef.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
