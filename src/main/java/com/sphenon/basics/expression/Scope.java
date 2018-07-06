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
import com.sphenon.basics.data.*;

import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

/**
   Serves as an evaluation context for arbitrary expressions.
   Stores variables by name.
*/
public interface Scope {

    public class Result {
        public Result(Object value) { this.value = value; }
        public Object value;
    }

    /**
       Retrieve a variable by name.
    */
    public Object get (CallContext context, String name) throws NoSuchVariable;

    /**
       Retrieve a variable by name.
       If not found, returns null, if found, returns a wrapper with the result.
    */
    public Result tryGetWithNull (CallContext context, String name);

    /**
       Retrieve a variable by name.
       Does not distinguish between "no variable" and "variable with null value".
    */
    public Object tryGet (CallContext context, String name);

    /**
       Retrieve a variable by name, but only within a specific name space.
    */
    public Object get (CallContext context, String name, String search_name_space) throws NoSuchVariable;

    /**
       Retrieve a variable by name, but only within a specific name space.
       If not found, returns null, if found, returns a wrapper with the result.
    */
    public Result tryGetWithNull (CallContext context, String name, String search_name_space);

    /**
       Retrieve a variable by name, but only within a specific name space.
       Does not distinguish between "no variable" and "variable with null value".
    */
    public Object tryGet (CallContext context, String name, String search_name_space);

    /**
       Retrieve a variable by name, but only within a specific name space.
       Pass whether you want, in case of error, an exception, or null.
    */
    public Result doGet (CallContext context, String name, String search_name_space, boolean throw_exception) throws NoSuchVariable;

    /**
       Tells, whether this space is directly modifiable, i.e. whether
       variables can be set here.

       Note: if this space is sealed, but at least one if it's parents is not,
       the set operation will nevertheless succeed by setting the variable in
       that unsealed parent scope.

       @return true, if the scope cannot be modified directly
    */
    public boolean getIsSealed (CallContext context);

    /**
       Stores a variable by name.

       If the scope is sealed and all parents are sealed, too, an exception is
       thrown.
    */
    public void set (CallContext context, String name, Object value);

    /**
       Tries to store a variable by name.

       @return true, if the value could be stored successfully, false, if the
                     scope is sealed and all parents are sealed as well 
    */
    public boolean trySet (CallContext context, String name, Object value);

    /**
       Stores a variable by name in a name space.

       If the scope is sealed and all parents are sealed, too, an exception is
       thrown.
    */
    public void set (CallContext context, String name, String name_space, Object value);

    /**
       Tries to store a variable by name in a name space.

       @return true, if the value could be stored successfully, false, if the
                     scope is sealed and all parents are sealed as well 
    */
    public boolean trySet (CallContext context, String name, String name_space, Object value);

    /**
       Stores a data source of a variable by name. The value is retrieved from
       the data source if and when the variable is accessed.
    */
    public void setOnDemand (CallContext context, String name, DataSource ds);

    /**
       True, if the scope contains a name space of the given name.
    */
    public boolean containsNameSpace (CallContext context, String name_space);

    /**
       Returns the name space of the given name, or otherwise, null
    */
    public Scope getNameSpace (CallContext context, String name_space);

    /**
       Retrieve a variable by name, without context (bean getter), see also
       {@link RootContext) setFallbackCallContext method
    */
    public Object get (String name) throws NoSuchVariable;

    /**
       Retrieve a variable by name, without context (bean getter), see also
       {@link RootContext) setFallbackCallContext method
    */
    public Result tryGetWithNull (String name);

    /**
       Retrieve all variables within this scope and it's parents.
       In some environments this operation may be costly or may not be able
       to return the complete set of variables.

       @return A vector of variable definitions
    */
    public Vector<Variable> getAllVariables(CallContext context);

    /**
       Retrieve all variables within this scope and it's parents that match
       a given regular expression pattern. In some environments this operation
       may be costly or may not be able to return the complete set of variables.

       @param pattern A regular expression that must be matched by each
                      variable name
       @return A vector of variable definitions
    */
    public Vector<Variable> getAllVariables(CallContext context, String pattern);
}
