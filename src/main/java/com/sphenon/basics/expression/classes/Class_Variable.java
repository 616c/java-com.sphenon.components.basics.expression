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

import java.util.HashMap;
import java.util.Hashtable;

public class Class_Variable implements Variable {

    public Class_Variable(CallContext context) {
    }

    public Class_Variable(CallContext context, String name, String name_space, Object value) {
        this.name         = name;
        this.name_space   = name_space;
        this.value        = value;
        this.value_source = null;
    }

    public Class_Variable(CallContext context, String name, String name_space, Object value, DataSource value_source) {
        this.name         = name;
        this.name_space   = name_space;
        this.value        = value;
        this.value_source = value_source;
    }

    protected String name;

    public String getName (CallContext context) {
        return this.name;
    }

    public void setName (CallContext context, String name) {
        this.name = name;
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

    protected Object value;
    protected DataSource value_source;

    public Object getValue (CallContext context) {
        return this.value_source == null ? this.value : this.value_source.getObject(context);
    }

    public Object defaultValue (CallContext context) {
        return null;
    }

    public void setValue (CallContext context, Object value) {
        this.value        = value;
        if (value != null) {
            this.value_source = null;
        }
    }

    public DataSource getDataSource (CallContext context) {
        return this.value_source;
    }

    public DataSource defaultDataSource (CallContext context) {
        return null;
    }

    public void setDataSource (CallContext context, DataSource value_source) {
        this.value_source = value_source;
        if (value_source != null) {
            this.value        = null;
        }
    }

    public Object getObject(CallContext context) {
        return this.getValue (context);
    }

    public Object get(CallContext context) {
        return this.getValue (context);
    }
}
