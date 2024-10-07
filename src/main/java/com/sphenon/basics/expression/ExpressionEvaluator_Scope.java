package com.sphenon.basics.expression;

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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

public class ExpressionEvaluator_Scope implements ExpressionEvaluator {

    public ExpressionEvaluator_Scope (CallContext context) {
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    public String[] getIds(CallContext context) {
        return new String[] { "scope" };
    }

    static protected RegularExpression get_instruction  = new RegularExpression("^ *([A-Za-z][A-Za-z0-9_]*(?:[./][A-Za-z0-9_]+)*) *(\\??) *$");
    static protected RegularExpression set_instruction  = new RegularExpression("^ *([A-Za-z][A-Za-z0-9_]*(?:[./][A-Za-z0-9_]+)*) *= *(?:([A-Za-z][A-Za-z0-9_]*)|([0-9]+)|(?:\"((?:[^\\\\\"]|(?:\\\\[\\\\\"]))*)\")|(?:'((?:[^\\\\']|(?:\\\\[\\\\']))*)')) *$");
    static protected RegularExpression add_instruction  = new RegularExpression("^ *add *(?:([A-Za-z][A-Za-z0-9_]*) *: *)?([A-Za-z][A-Za-z0-9_]*) *$");
    static protected RegularExpression load_instruction = new RegularExpression("^ *load *(?:([A-Za-z][A-Za-z0-9_]*) *: *)?(?:([A-Za-z][A-Za-z0-9_]*)|(?:\"([^\"]*)\")|(?:'([^']*)')) *$");
    static protected RegularExpression dump_instruction = new RegularExpression("^ *dump *");

    public Object evaluate(CallContext context, String instruction, Scope scope, DataSink<Execution> execution_sink) throws EvaluationFailure {
        ExecutionHandler eh = new ExecutionHandler(context, execution_sink, null);
        try{
            String[] matches;

            if ((matches = get_instruction.tryGetMatches(context, instruction)) != null) {
                String varname = matches[0];
                boolean optional = (matches.length > 1 && matches[1] != null && matches[1].equals("?"));

                if (scope == null) {
                    if (optional) {
                        eh.reportSuccess(context);
                        return null;
                    }
                    eh.reportAndThrow(context, EvaluationFailure.create(context, "No scope provided while trying to get variable '%(name)'", "name", varname));
                    throw (EvaluationFailure) null;
                }

                try {
                    Object result = optional ? scope.tryGet(context, varname) : scope.get(context, varname);
                    eh.reportSuccess(context);
                    return result;
                } catch (NoSuchVariable nsv) {
                    eh.reportAndThrow(context, EvaluationFailure.create(context, "No such variable '%(name)' in scope", "name", varname));
                    throw (EvaluationFailure) null;
                }
            } else if ((matches = set_instruction.tryGetMatches(context, instruction)) != null) {
                String targetname = matches[0];

                if (scope == null) {
                    eh.reportAndThrow(context, EvaluationFailure.create(context, "No scope provided while trying to set variable '%(name)'", "name", targetname));
                    throw (EvaluationFailure) null;
                }

                Object value = null;

                if (matches[1] != null) {
                    String sourcename = matches[1];
                    if (sourcename.equals("true")) {
                        value = Boolean.TRUE;
                    } else if (sourcename.equals("false")) {
                        value = Boolean.FALSE;
                    } else {
                        try {
                            value = scope.get(context, sourcename);
                        } catch (NoSuchVariable nsv) {
                            eh.reportAndThrow(context, EvaluationFailure.create(context, "No such variable '%(name)' in scope", "name", sourcename));
                            throw (EvaluationFailure) null;
                        }
                    }
                } else if (matches[2] != null) {
                    String number = matches[2];
                    value = Integer.parseInt(number);
                } else if (matches[3] != null) {
                    String string = matches[3].replaceAll("\\\\([\\\\\"])","$1");
                    value = string;
                } else if (matches[4] != null) {
                    String string = matches[4].replaceAll("\\\\([\\\\'])","$1");
                    value = string;
                }
            
                scope.set(context, targetname, value);

                eh.reportSuccess(context);
                return value;
            } else if ((matches = add_instruction.tryGetMatches(context, instruction)) != null) {
                String name_space = matches[0];

                if (scope == null) {
                    eh.reportAndThrow(context, EvaluationFailure.create(context, "No scope provided while trying to add parent"));
                    throw (EvaluationFailure) null;
                }

                Object value = null;

                String sourcename = matches[1];
                try {
                    value = scope.get(context, sourcename);
                } catch (NoSuchVariable nsv) {
                    eh.reportAndThrow(context, EvaluationFailure.create(context, "No such variable '%(name)' in scope", "name", sourcename));
                    throw (EvaluationFailure) null;
                }

                if ((scope instanceof Class_Scope) == false) {
                    eh.reportAndThrow(context, EvaluationFailure.create(context, "Scope is not modifyable while trying to add parent"));
                    throw (EvaluationFailure) null;
                }

                if ((value instanceof Scope) == false) {
                    eh.reportAndThrow(context, EvaluationFailure.create(context, "Value is not a Scope while trying to add parent"));
                    throw (EvaluationFailure) null;
                }
            
                ((Class_Scope) scope).addParent(context, (Scope) value, null, name_space);

                eh.reportSuccess(context);
                return value;
            } else if ((matches = load_instruction.tryGetMatches(context, instruction)) != null) {
                String name_space = matches[0];
                String file_name = null;

                if (matches[1] != null) {
                    String sourcename = matches[1];
                    try {
                        file_name = (String) scope.get(context, sourcename);
                    } catch (NoSuchVariable nsv) {
                        eh.reportAndThrow(context, EvaluationFailure.create(context, "No such variable '%(name)' in scope", "name", sourcename));
                        throw (EvaluationFailure) null;
                    }
                } else if (matches[2] != null) {
                    file_name = matches[2];
                } else if (matches[3] != null) {
                    file_name = matches[3];
                }

                Scope new_scope = new Class_Scope(context, name_space, null, file_name);

                eh.reportSuccess(context);
                return new_scope;
            } else if ((matches = dump_instruction.tryGetMatches(context, instruction)) != null) {
                String dump = Dumper.dumpToString(context, "Scope", scope);
                eh.reportSuccess(context);
                return dump;
            } else {
                eh.reportAndThrow(context, EvaluationFailure.create(context, "Unknown instruction '%(instruction)' in scope expression", "instruction", instruction));
                throw (EvaluationFailure) null;
            }
        } catch (EvaluationFailure t) {
            eh.handleFinally(context, t);
            throw (EvaluationFailure) null;
        } catch (Throwable t) {
            eh.handleFinally(context, EvaluationFailure.create(context, t, "Unexpected exception"));
            throw (Error) null;
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute, false, null);
    }
}
