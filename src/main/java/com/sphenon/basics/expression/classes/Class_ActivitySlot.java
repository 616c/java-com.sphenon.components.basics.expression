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

public class Class_ActivitySlot<T> implements ActivitySlot<T> {

    public Class_ActivitySlot(CallContext context, ActivityAttribute attribute, T value) {
        this.attribute = attribute;
        this.value = value;
    }

    protected ActivityAttribute attribute;

    public ActivityAttribute getAttribute (CallContext context) {
        return this.attribute;
    }

    public void setAttribute (CallContext context, ActivityAttribute attribute) {
        this.attribute = attribute;
    }

    protected T value;

    public T getValue (CallContext context) {
        return this.value;
    }

    public void setValue (CallContext context, T value) {
        this.value = value;
    }
}
