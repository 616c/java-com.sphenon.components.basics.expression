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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

public class ActivityHandler_Expression extends ActivityHandler {

    protected ActivityHandler_Expression(CallContext context, Expression expression, ExpressionSource expression_source, String expression_string, String default_evaluators, String evaluator_id, String actor_id, String location, String session_id, ExpressionEvaluatorRegistry registry) {
        super(context);
        this.expression = expression;
        this.expression_string = expression_string;
        this.expression_source = expression_source;
        this.default_evaluators = default_evaluators;
        this.evaluator_id = evaluator_id;
        this.actor_id = actor_id;
        this.location = location;
        this.session_id = session_id;
        this.registry = registry;
        this.is_valid = true;
    }

    static public ActivityHandler_Expression create(CallContext context, Expression expression) {
        if (expression == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler_Expression, invalid argument 'expression' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return new ActivityHandler_Expression(context, expression, null, null, null, null, null, null, null, null);
    }

    static public ActivityHandler_Expression create(CallContext context, String expression_string) {
        if (expression_string == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler_Expression, invalid argument 'expression_string' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return new ActivityHandler_Expression(context, null, null, expression_string, null, null, null, null, null, null);
    }

    static public ActivityHandler_Expression create(CallContext context, String expression_string, String default_evaluators) {
        if (expression_string == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler_Expression, invalid argument 'expression_string' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return new ActivityHandler_Expression(context, null, null, expression_string, default_evaluators, null, null, null, null, null);
    }

    static public ActivityHandler_Expression createWithEvaluatorId(CallContext context, String expression_string, String evaluator_id, String actor_id, String location, String session_id, ExpressionEvaluatorRegistry registry) {
        if (expression_string == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler_Expression, invalid argument 'expression_string' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (evaluator_id == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler_Expression, invalid argument 'evaluator_id' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return new ActivityHandler_Expression(context, null, null, expression_string, null, evaluator_id, actor_id, location, session_id, registry);
    }

    static public ActivityHandler_Expression createWithEvaluatorId(CallContext context, ExpressionSource expression_source, String evaluator_id, String actor_id, String location, String session_id, ExpressionEvaluatorRegistry registry) {
        if (expression_source == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler_Expression, invalid argument 'expression_source' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (evaluator_id == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "ActivityHandler_Expression, invalid argument 'evaluator_id' (null)");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        return new ActivityHandler_Expression(context, null, expression_source, null, null, evaluator_id, actor_id, location, session_id, registry);
    }

    static public ActivityHandler_Expression createWithEvaluatorId(CallContext context, Object expression_source_or_string, String evaluator_id, String actor_id, String location, String session_id, ExpressionEvaluatorRegistry registry) {
        if (expression_source_or_string instanceof ExpressionSource) {
            return createWithEvaluatorId(context, (ExpressionSource) expression_source_or_string, evaluator_id, actor_id, location, session_id, registry);
        } else {
            return createWithEvaluatorId(context, (String) expression_source_or_string, evaluator_id, actor_id, location, session_id, registry);
        }
    }

    protected Expression expression;

    public Expression getExpression (CallContext context) {
        return this.expression;
    }

    protected String expression_string;

    public String getExpressionString (CallContext context) {
        if (this.expression_string == null && this.expression != null) {
            this.expression_string = this.expression.getExpression(context);
        }
        return this.expression_string;
    }

    protected ExpressionSource expression_source;

    public ExpressionSource getExpressionSource (CallContext context) {
        return this.expression_source;
    }

    protected String default_evaluators;

    public String getDefaultEvaluators (CallContext context) {
        return this.default_evaluators;
    }

    protected String evaluator_id;

    public String getEvaluatorId (CallContext context) {
        return this.evaluator_id;
    }

    protected String actor_id;

    public String getActorId (CallContext context) {
        return this.actor_id;
    }

    protected String location;

    public String getLocation (CallContext context) {
        return this.location;
    }

    protected String session_id;

    public String getSessionId (CallContext context) {
        return this.session_id;
    }

    protected ExpressionEvaluatorRegistry registry;

    public ExpressionEvaluatorRegistry getRegistry (CallContext context) {
        return this.registry;
    }

    protected ExpressionEvaluatorService service;

    public ExpressionEvaluatorService getService (CallContext context) {
        return this.service;
    }

    public ActivityHandler parse(CallContext context) {
        if (this.is_valid) {
            if (this.activity_class == null) {
                try {

                    if (this.location != null && this.location.isEmpty() == false) {
                        if (this.registry != null) {
                            this.is_valid = false;
                            this.execution = Execution_Basic.createExecutionFailure(context, "Invalid combination of arguments in ActivityHandler_Expression, both a location and a registry is provided");
                        }
                        if (this.service != null) {
                            this.is_valid = false;
                            this.execution = Execution_Basic.createExecutionFailure(context, "Invalid combination of arguments in ActivityHandler_Expression, both a location and a service is provided");
                        }
                        this.service = Expression.findExpressionEvaluatorService(context, this.location);
                    }

                    if (this.registry != null && this.service != null) {
                        this.is_valid = false;
                        this.execution = Execution_Basic.createExecutionFailure(context, "Invalid combination of arguments in ActivityHandler_Expression, both a service and a registry is provided");
                    }

                    if (this.expression_string != null && this.expression_source == null && this.evaluator_id == null && this.expression == null) {
                        this.expression = new Expression(context, this.expression_string, this.default_evaluators);
                    }
                    if (this.expression != null && this.expression_source == null && this.evaluator_id == null) {
                        this.activity_class = this.expression.parse(context);
                        if (this.registry != null) {
                            // maybe this check is wrong and we need to
                            // apply the registry to the expression in the
                            // context of space-service-expressions
                            // at present this is just an assertion here
                            this.is_valid = false;
                            this.execution = Execution_Basic.createExecutionFailure(context, "Invalid combination of arguments in ActivityHandler_Expression, both an expression and a registry is provided");
                        }
                        if (this.service != null) {
                            // maybe this check is wrong and we need to
                            // lookup the expression in the context of
                            // space-service-expressions
                            // at present this is just an assertion here
                            this.is_valid = false;
                            this.execution = Execution_Basic.createExecutionFailure(context, "Invalid combination of arguments in ActivityHandler_Expression, both an expression and a service is provided");
                        }
                    } else if (this.expression == null && this.expression_string != null && this.expression_source == null && this.evaluator_id != null) {
                        if (this.service != null) {
                            this.activity_class = this.service.parseWithEvaluator(context, new Class_ExpressionSource(context, this.expression_string, false), this.evaluator_id);
                        } else {
                            this.activity_class = Expression.parseWithEvaluator(context, this.expression_string, this.evaluator_id, false, this.registry);
                        }
                    } else if (this.expression == null && this.expression_string == null && this.expression_source != null && this.evaluator_id != null) {
                        if (this.service != null) {
                            this.activity_class = this.service.parseWithEvaluator(context, this.expression_source, this.evaluator_id);
                        } else {
                            this.activity_class = Expression.parseWithEvaluator(context, this.expression_source, this.evaluator_id, false, this.registry);
                        }
                    } else {
                        this.is_valid = false;
                        this.execution = Execution_Basic.createExecutionFailure(context, "Invalid combination of arguments in ActivityHandler_Expression");
                     }
                } catch (EvaluationFailure ef) {
                    this.is_valid = false;
                    this.execution = Execution_Basic.createExecutionFailure(context, ef);
                }
            }
        }
        return this;
    }

    public ActivityClass getActivityClass (CallContext context) {
        this.parse(context);

        return this.activity_class;
    }

    public ActivityHandler instantiate(CallContext context, Scope current_scope) {
        this.getActivityClass(context);

        boolean got_session_id = (this.session_id != null && this.session_id.isEmpty() == false);
        boolean got_actor_id   = (this.actor_id != null && this.actor_id.isEmpty() == false);

        if (got_session_id || got_actor_id) {
            Class_Scope local_scope = new Class_Scope(context, current_scope);
            if (got_session_id) {
                local_scope.set(context, "SessionId", this.session_id);
            }
            if (got_actor_id) {
                local_scope.set(context, "ActorId", this.actor_id);
            }
            local_scope.setIsSealed(context, true);
            current_scope = local_scope;
        }

        return super.instantiate(context, current_scope);
    }
}
