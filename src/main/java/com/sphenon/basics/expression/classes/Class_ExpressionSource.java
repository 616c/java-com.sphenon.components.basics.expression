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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.operations.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;
import com.sphenon.basics.expression.structure.*;

import java.util.Vector;

public class Class_ExpressionSource implements ExpressionSource {

    static public long duration;
    public Class_ExpressionSource(CallContext context, String string, boolean parse) throws EvaluationFailure {
        if (parse == false) {
            this.entries = new Vector<ExpressionSourceEntry>();
            this.entries.add(new Class_ExpressionSourceEntry(context, string));
        } else {

            long start = System.currentTimeMillis();

            try {
                this.entries = ExpressionStructure.parse(context, string);
            } catch (ParseException pe) {
                EvaluationFailure.createAndThrow(context, pe, "Evaluation failed while parsing general structure of '%(code)'", "code", string);
                throw (EvaluationFailure) null;
            }

            long stop = System.currentTimeMillis();
            duration += (stop - start);
        }
    }

    public String getString(CallContext context, ArgumentSerialiser argument_serialiser, Scope scope) throws EvaluationFailure {
        if (this.entries.size() == 1 && this.entries.get(0).getString(context) != null) {
            return this.entries.get(0).getString(context);
        }

        StringBuilder sb = new StringBuilder();
        for (ExpressionSourceEntry ese : this.entries) {
            if (ese.getString(context) != null) {
                sb.append(ese.getString(context));
            } else {
                sb.append(argument_serialiser.serialise(context, ese, scope));
            }
        }
        return sb.toString();
    }

    protected Vector<ExpressionSourceEntry> entries;

    public Vector<ExpressionSourceEntry> getEntries (CallContext context) {
        return this.entries;
    }

    public String toString(CallContext context) {
        if (this.entries.size() == 1 && this.entries.get(0).getString(context) != null) {
            return this.entries.get(0).getString(context);
        }

        StringBuilder sb = new StringBuilder();
        for (ExpressionSourceEntry ese : this.entries) {
            sb.append(ese.toString(context));
        }
        return sb.toString();
    }
}
