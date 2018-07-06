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

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

public class Class_ActivityAttribute implements ActivityAttribute {

    public Class_ActivityAttribute(CallContext context, String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Class_ActivityAttribute(CallContext context, String name, String type, String initial_state, String final_state) {
        this.name = name;
        this.type = type;
        this.initial_state = initial_state;
        this.final_state = final_state;
    }

    protected String name;

    public String getName (CallContext context) {
        return this.name;
    }

    public void setName (CallContext context, String name) {
        this.name = name;
    }

    protected String type;

    public String getType (CallContext context) {
        return this.type;
    }

    public void setType (CallContext context, String type) {
        this.type = type;
    }

    protected String initial_state;

    public String getInitialState (CallContext context) {
        return this.initial_state;
    }

    public void setInitialState (CallContext context, String initial_state) {
        this.initial_state = initial_state;
    }

    protected String final_state;

    public String getFinalState (CallContext context) {
        return this.final_state;
    }

    public void setFinalState (CallContext context, String final_state) {
        this.final_state = final_state;
    }
}
