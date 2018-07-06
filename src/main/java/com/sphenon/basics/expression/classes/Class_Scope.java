package com.sphenon.basics.expression.classes;

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
import com.sphenon.basics.data.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import com.sphenon.engines.aggregator.annotations.*;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Class_Scope implements Scope {

    public Class_Scope(CallContext context)                                                                    { this(context, null      , null               ); }
    public Class_Scope(CallContext context, String name_space  )                                               { this(context, name_space, null               ); }
    public Class_Scope(CallContext context, Scope parent       )                                               { this(context, null      , parent             ); }
    public Class_Scope(CallContext context, Map<String,Object> variables)                                      { this(context, null      , null    , variables); }
    public Class_Scope(CallContext context, Object... variables)                                               { this(context, null      , null    , variables); }
    public Class_Scope(CallContext context, Vector<Variable> variables)                                        { this(context, null      , null    , variables); }

    public Class_Scope(CallContext context, boolean is_sealed)                                                 { this(context, null      , is_sealed);           }

    public Class_Scope(CallContext context, String name_space, Scope parent)                                   { this(context, name_space, false, parent);            }
    public Class_Scope(CallContext context, String name_space, Scope parent, Map<String,Object> variables)     { this(context, name_space, false, parent, variables); }
    public Class_Scope(CallContext context, String name_space, Scope parent, Object... variables)              { this(context, name_space, false, parent, variables); }
    public Class_Scope(CallContext context, String name_space, Scope parent, Vector<Variable> variables)       { this(context, name_space, false, parent, variables); }
    public Class_Scope(CallContext context, String name_space, Scope parent, String properties_file)           { this(context, name_space, false, parent, properties_file); }

    public Class_Scope(CallContext context, String name_space, boolean is_sealed)                              { this(context, name_space, is_sealed, null);          }

    public Class_Scope(CallContext context, String name_space, boolean is_sealed, Scope parent) {
        this.name_space = name_space;
        if (parent != null) {
            this.addParent(context, parent);
        }
        this.is_sealed = is_sealed;
    }

    public Class_Scope(CallContext context, String name_space, boolean is_sealed, Scope parent, Map<String,Object> variables) {
        this(context, name_space, is_sealed, parent);
        this.setVariables(context, variables);
    }

    public Class_Scope(CallContext context, String name_space, boolean is_sealed, Scope parent, Object... variables) {
        this(context, name_space, is_sealed, parent);
        this.setVariables(context, variables);
    }

    public Class_Scope(CallContext context, String name_space, boolean is_sealed, Scope parent, Vector<Variable> variables) {
        this(context, name_space, is_sealed, parent);
        this.setVariables(context, variables);
    }

    public Class_Scope(CallContext context, String name_space, boolean is_sealed, Scope parent, String properties_file) {
        this(context, name_space, is_sealed, parent);
        this.setVariables(context, properties_file);
    }

    protected Map<String,Object>     variables;
    protected Map<String,DataSource> on_demand_variables;

    public Object get (CallContext context, String name) throws NoSuchVariable {
        return get(context, name, null);
    }

    public Result tryGetWithNull (CallContext context, String name) {
        return tryGetWithNull(context, name, null);
    }

    public Object tryGet (CallContext context, String name) {
        return tryGet(context, name, null);
    }

    public Object get (CallContext context, String name, String search_name_space) throws NoSuchVariable {
        return doGet(context, name, search_name_space, true).value;
    }

    public Result tryGetWithNull (CallContext context, String name, String search_name_space) {
        try {
            return doGet (context, name, search_name_space, false);
        } catch (NoSuchVariable nsv) {
            // can't happen
            return null;
        }
    }

    public Object tryGet (CallContext context, String name, String search_name_space) {
        try {
            Result result = doGet(context, name, search_name_space, false);
            return (result == null ? null : result.value);
        } catch (NoSuchVariable nsv) {
            // can't happen
            return null;
        }
    }

    protected Result doGetVariable (CallContext context, String name, String search_name_space, boolean sns_opt) {
        return doGetVariable(context, name, search_name_space);
    }

    protected Result doGetVariable (CallContext context, String name, String search_name_space) {
        return null;
    }

    protected Result doGetLocalVariable (CallContext context, String name, String search_name_space, boolean sns_opt) {
        if (    search_name_space == null
             || search_name_space.isEmpty()
             || search_name_space.equals(this.name_space)
             || (sns_opt && (this.name_space == null || this.name_space.isEmpty()))
           ) {
            if (    this.variables != null
                 && this.variables.containsKey(name)
               ) {
                return new Result(this.variables.get(name));
            }
            if (    this.on_demand_variables != null
                 && this.on_demand_variables.containsKey(name)
               ) {
                return new Result(this.on_demand_variables.get(name).getObject(context));
            }
        } 
        return null;
    }

    public Result doGet (CallContext context, String name, String search_name_space, boolean throw_exception) throws NoSuchVariable {
        Result result;

        boolean sns_opt = false;
        if (search_name_space != null && search_name_space.startsWith("?")) {
            sns_opt = true;
            search_name_space = search_name_space.substring(1);
        }

        result = this.doGetLocalVariable(context, name, search_name_space, sns_opt);
        if (result != null) { return result; }

        if (search_name_space != null && search_name_space.isEmpty() == false) {
            result = this.doGetLocalVariable(context, search_name_space + "." + name, null, sns_opt);
            if (result != null) { return result; }
        }

        result = this.doGetVariable(context, name, search_name_space, sns_opt);
        if (result != null) { return result; }

        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            search_name_space = (search_name_space == null || search_name_space.isEmpty() ? "" : (search_name_space + ".")) + name.substring(0, dot);
            name = name.substring(dot + 1);

            result = this.doGetLocalVariable(context, name, search_name_space, false);
            if (result != null) { return result; }

            result = this.doGetVariable(context, name, search_name_space, false);
            if (result != null) { return result; }
        }

        if (this.parents != null) {
            for (Named<Scope> named_parent : this.parents) {
                String parent_name = named_parent.getName(context);
                if (parent_name == null) {
                    result = named_parent.getData(context).doGet(context, name, search_name_space, false);
                } else {
                    String[] snsp = search_name_space.split("\\.",2);
                    if (parent_name.equals(snsp[0])) {
                        result = named_parent.getData(context).doGet(context, name, snsp.length == 1 ? null : snsp[1], false);
                    }
                }
                if (result != null) { return result; }
            }
        }

        if (throw_exception) {
            NoSuchVariable.createAndThrow(context, "Variable '%(name)' is not defined", "name", name);
            throw (NoSuchVariable) null;
        } else {
            return null;
        }
    }

    protected boolean is_sealed;

    public boolean getIsSealed (CallContext context) {
        return this.is_sealed;
    }

    public boolean defaultIsSealed (CallContext context) {
        return false;
    }

    public void setIsSealed (CallContext context, boolean is_sealed) {
        this.is_sealed = is_sealed;
    }

    public void set (CallContext context, String name, Object value) {
        if (this.trySet(context, name, value) == false) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot set variable '%(name)' in this scope, it is sealed and contains no unsealed parents");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    public boolean trySet (CallContext context, String name, Object value) {
        String name_space = null;
        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            name_space = name.substring(0, dot);
            name = name.substring(dot + 1);
        }
        return this.trySet(context, name, name_space, value);
    }

    public void set (CallContext context, String name, String name_space, Object value) {
        if (this.trySet(context, name, name_space, value) == false) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Cannot set variable '%(name)' in name space '%(namespace)' in this scope, it is sealed and contains no unsealed parents", "name", name, "namesspace", name_space);
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    protected boolean doSet(CallContext context, String name, Object value) {
        if (this.is_sealed) {
            return false;
        } else {
            if (this.variables == null) {
                this.variables = new HashMap<String,Object>();
            }
            this.variables.put(name, value);
            return true;
        }
    }

    public boolean trySet (CallContext context, String name, String name_space, Object value) {
        if (name_space == null || name_space.isEmpty()) {
            if (doSet(context, name, value)) {
                return true;
            } else {
                if (this.parents != null) {
                    for (Named<Scope> named_parent : this.parents) {
                        String parent_name = named_parent.getName(context);
                        if (parent_name == null) {
                            if (named_parent.getData(context).trySet(context, name, value)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        if (name_space.equals(this.name_space)) {
            return doSet(context, name, value);
        }
        if (this.parents != null) {
            for (Named<Scope> named_parent : this.parents) {
                String parent_name = named_parent.getName(context);
                if (parent_name == null) {
                    if (named_parent.getData(context).trySet(context, name, name_space, value)) {
                        return true;
                    }
                } else {
                    String[] snsp = name_space.split("\\.",2);
                    if (parent_name.equals(snsp[0])) {
                        if (named_parent.getData(context).trySet(context, name, snsp.length == 1 ? null : snsp[1], value)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean containsNameSpace (CallContext context, String name_space) {
        if (name_space == null || name_space.isEmpty() || name_space.equals(this.name_space)) {
            return true;
        }

        if (this.parents != null) {
            for (Named<Scope> named_parent : this.parents) {
                String parent_name = named_parent.getName(context);
                if (parent_name == null) {
                    if (named_parent.getData(context).containsNameSpace(context, name_space)) {
                        return true;
                    }
                } else {
                    String[] snsp = name_space.split("\\.",2);
                    if (parent_name.equals(snsp[0])) {
                        return named_parent.getData(context).containsNameSpace (context, snsp.length == 1 ? null : snsp[1]);
                    }
                }
            }
        }
        return false;
    }

    public Scope getNameSpace (CallContext context, String name_space) {
        if (name_space == null || name_space.isEmpty() || name_space.equals(this.name_space)) {
            return this;
        }

        if (this.parents != null) {
            for (Named<Scope> named_parent : this.parents) {
                String parent_name = named_parent.getName(context);
                if (parent_name == null) {
                    Scope result = named_parent.getData(context).getNameSpace(context, name_space);
                    if (result != null) {
                        return result;
                    }
                } else {
                    String[] snsp = name_space.split("\\.",2);
                    if (parent_name.equals(snsp[0])) {
                        Scope result = named_parent.getData(context).getNameSpace (context, snsp.length == 1 ? null : snsp[1]);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void setOnDemand (CallContext context, String name, DataSource ds) {
        if (this.on_demand_variables == null) {
            this.on_demand_variables = new HashMap<String,DataSource>();
        }
        this.on_demand_variables.put(name, ds);
    }

    protected Vector<Variable> variables_instances;

    public Vector<Variable> getVariables (CallContext context) {
        return this.variables_instances;
    }

    public Vector<Variable> defaultVariables (CallContext context) {
        return null;
    }

    @OCPIgnore
    public void setVariables (CallContext context, Map<String,Object> variables) {
        this.variables = variables;
    }
 
    public void setVariables (CallContext context, Vector<Variable> variables_instances) {
        this.variables_instances = variables_instances;
        if (variables_instances != null) {
            for (Variable variable : variables_instances) {
                this.setOnDemand(context, variable.getName(context), variable);
            }
        }
    }

    @OCPIgnore
    public void setVariables (CallContext context, Object... variables) {
        this.variables = new HashMap<String,Object>();
        if (variables != null) {
            for (int i=0; i<variables.length; i+=2) {
                this.variables.put((String) variables[i], variables[i+1]);
            }
        }
    }

    @OCPIgnore
    public void setVariables (CallContext context, String properties_file) {
        try {
            Properties properties = new Properties();
            File file = new File(properties_file);
            if (file.exists()) {
                InputStream in = new FileInputStream(file);
                properties.load(in);
                in.close();
            }
            this.setVariables(context, (Map) properties);
        } catch (FileNotFoundException fnfe) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, fnfe, "No such properties file to load into scope '%(file)'", "file", properties_file);
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, ioe, "Error while loading properties file into scope '%(file)'", "file", properties_file);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }

    public Object get (String name) throws NoSuchVariable {
        return get(RootContext.getFallbackCallContext(), name);
    }

    public Result tryGetWithNull (String name) {
        return tryGetWithNull(RootContext.getFallbackCallContext(), name);
    }

    protected Vector<Named<Scope>> parents;

    public Vector<Named<Scope>> getParents (CallContext context) {
        return this.parents;
    }

    public Vector<Named<Scope>> defaultParents (CallContext context) {
        return null;
    }

    public void setParents (CallContext context, Vector<Named<Scope>> parents) {
        this.parents = parents;
    }

    public boolean addParent (CallContext context, Scope parent) {
        return this.addParent(context, parent, null);
    }

    public boolean addParent (CallContext context, Scope parent, String name) {
        if (parent == null) { return true; }
        if ( ! this.is_sealed) {
            if (this.parents == null) {
                this.parents = new Vector<Named<Scope>>();
            }
            this.parents.add(new Named<Scope>(context, name, parent));
            return true;
        } else {
            if (this.parents != null) {
                for (Named<Scope> named_parent : this.parents) {
                    String parent_name = named_parent.getName(context);
                    if (parent_name == null && named_parent.getData(context) instanceof Class_Scope) {
                        if (((Class_Scope) (named_parent.getData(context))).addParent(context, parent, name)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public boolean addParent (CallContext context, Scope parent, String name, String name_space) {
        Scope scope = (name_space == null || name_space.isEmpty() ? this : this.getNameSpace(context, name_space));
        if (scope == null || (scope instanceof Class_Scope) == false) {
            return false;
        }
        return ((Class_Scope) scope).addParent(context, parent, name);
    }

    protected String name_space;

    public String getNameSpace (CallContext context) {
        return this.name_space;
    }

    public String defaultNameSpace (CallContext context) {
        return null;
    }

    public void setNameSpace (CallContext context, String name_space) {
        this.name_space = name_space;
    }

    public Vector<Variable> getAllVariables(CallContext context) {
        return getAllVariables(context, null);
    }

    public Vector<Variable> getAllVariables(CallContext context, String pattern) {
        Vector<Variable> result = new Vector<Variable>();
        Map<String,String> check = new HashMap<String,String>();
        if (this.variables != null) {
            for (String name : this.variables.keySet()) {
                if (pattern == null || name.matches(pattern)) {
                    result.add(new Class_Variable(context, name, this.name_space, this.variables.get(name)));
                    check.put(name, name);
                }
            }
        }
        if (this.on_demand_variables != null) {
            for (String name : this.on_demand_variables.keySet()) {
                if (pattern == null || name.matches(pattern)) {
                    if (check.containsKey(name) == false) {
                        result.add(new Class_Variable(context, name, this.name_space, null, this.on_demand_variables.get(name)));
                        check.put(name, name);
                    }
                }
            }
        }
        if (this.parents != null) {
            for (Named<Scope> named_parent : this.parents) {
                String parent_name = named_parent.getName(context);
                for (Variable variable : named_parent.getData(context).getAllVariables(context)) {
                    String name = variable.getName(context);
                    String vns = variable.getNameSpace(context);
                    if (pattern == null || name.matches(pattern)) {
                        if (parent_name != null && parent_name.isEmpty() == false) {
                            vns = parent_name + (vns == null ? "" : ("." + vns));
                            variable = new Class_Variable(context, name, vns, variable.getValue(context));
                        }
                        name = (vns == null ? "" : (vns + ".")) + name;
                        if (check.containsKey(name) == false) {
                            result.add(variable);
                            check.put(name, name);
                        }
                    }
                }
            }
        }
        return result;
    }
}
