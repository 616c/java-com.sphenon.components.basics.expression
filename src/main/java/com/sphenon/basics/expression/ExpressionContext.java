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
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;

import java.util.Map;
import java.util.HashMap;

public class ExpressionContext extends SpecificContext {

    static public ExpressionContext get(Context context) {
        ExpressionContext expression_context = (ExpressionContext) context.getSpecificContext(ExpressionContext.class);
        return expression_context;
    }

    static public ExpressionContext create(Context context) {
        ExpressionContext expression_context = new ExpressionContext(context, false);
        context.setSpecificContext(ExpressionContext.class, expression_context);
        return expression_context;
    }

    protected ExpressionContext (Context context, boolean is_default_singelton) {
        super(context);
        this.scope = null;
    }

    protected Scope scope;

    public void setScope(CallContext context, Scope scope) {
        this.scope = scope;
    }

    public Scope getScope(CallContext context) {
        ExpressionContext expression_context;
        return (this.scope != null ?
                     this.scope
                  : (expression_context = (ExpressionContext) this.getCallContext(ExpressionContext.class)) != null ?
                       expression_context.getScope(context)
                     : null
               );
    }

    protected ExpressionEvaluatorRegistry expression_registry;

    public void setExpressionRegistry(CallContext context, ExpressionEvaluatorRegistry expression_registry) {
        this.expression_registry = expression_registry;
    }

    public ExpressionEvaluatorRegistry getExpressionRegistry(CallContext context) {
        ExpressionContext expression_context;
        return (this.expression_registry != null ?
                     this.expression_registry
                  : (expression_context = (ExpressionContext) this.getCallContext(ExpressionContext.class)) != null ?
                       expression_context.getExpressionRegistry(context)
                     : null
               );
    }

    protected Map<String,Scope> scope_map;

    public void setScopeMap(CallContext context, Map<String,Scope> scope_map) {
        this.scope_map = scope_map;
    }

    public void enableScopeMap(CallContext context) {
        this.scope_map = new HashMap<String,Scope>();
    }

    public Map<String,Scope> getScopeMap(CallContext context) {
        ExpressionContext expression_context;
        return (this.scope_map != null ?
                     this.scope_map
                  : (expression_context = (ExpressionContext) this.getCallContext(ExpressionContext.class)) != null ?
                       expression_context.getScopeMap(context)
                     : null
               );
    }

    public Scope getScopeById(CallContext context, String id) {
        return doGetScopeById(context, id, true);
    }

    public Scope tryGetScopeById(CallContext context, String id) {
        return doGetScopeById(context, id, false);
    }

    protected Scope doGetScopeById(CallContext context, String id, boolean throw_exception) {
        Map<String,Scope> map = this.getScopeMap(context);
        if (map == null) {
            if (throw_exception) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "No scope map in context while looking for scope '%(scope)'", "scope", id);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            } else {
                return null;
            }
        }
        Scope scope = map.get(id);
        if (scope == null) {
            if (throw_exception) {
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "No scope '%(scope)' in context scope map", "scope", id);
                throw (ExceptionPreConditionViolation) null; // compiler insists
            } else {
                return null;
            }
        }
        return scope;
    }

    public void putScopeById(CallContext context, String id, Scope scope) {
        Map<String,Scope> map = this.getScopeMap(context);
        if (map == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "No scope map in context while trying to store scope '%(scope)'", "scope", id);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (map.get(id) != null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Scope '%(scope)' in already defined in context scope map", "scope", id);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        map.put(id, scope);
    }

    public Scope removeScopeById(CallContext context, String id) {
        Map<String,Scope> map = this.getScopeMap(context);
        if (map == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "No scope map in context while trying to remove scope '%(scope)'", "scope", id);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        Scope scope;
        if ((scope = map.get(id)) != null) {
            map.remove(id);
        }
        return scope;
    }

    static public Scope tryGetScope(CallContext context, String id) {
        ExpressionContext ec = ExpressionContext.get((Context) context);
        if (ec == null) {
            return null;
        }
        return ec.tryGetScopeById(context, id);
    }

    static public void putScope(CallContext context, String id, Scope scope) {
        ExpressionContext ec = ExpressionContext.get((Context) context);
        if (ec == null) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "No scope map in context while trying to store scope '%(scope)'", "scope", id);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        ec.putScopeById(context, id, scope);
    }
}
