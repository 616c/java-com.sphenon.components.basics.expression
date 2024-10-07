package com.sphenon.basics.expression.classes;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Vector;

public class Class_ActivityData implements ActivityData {

    public Class_ActivityData(CallContext context) {
    }

    protected Vector<ActivitySlot> slots;

    public Vector<ActivitySlot> getSlots (CallContext context) {
        return this.slots;
    }

    public void addSlot (CallContext context, ActivitySlot slot) {
        if (this.slots == null) {
            this.slots = new Vector<ActivitySlot>();
        }
        this.slots.add(slot);
    }

    public Object getSlotValue(CallContext context, int index) {
        Vector<ActivitySlot> slots = getSlots(context);
        if (slots == null) { return null; }
        ActivitySlot slot = slots.size() > index ? slots.get(index) : null;
        if (slot == null) { return null; }
        return slot.getValue(context);
    }

    public Object getSlotValue(CallContext context, String name) {
        Vector<ActivitySlot> slots = getSlots(context);
        if (slots == null) { return null; }
        for (ActivitySlot slot : slots) {
            if (slot.getAttribute(context).getName(context).equals(name)) {
                return slot.getValue(context);
            }
        }
        return null;
    }
}

