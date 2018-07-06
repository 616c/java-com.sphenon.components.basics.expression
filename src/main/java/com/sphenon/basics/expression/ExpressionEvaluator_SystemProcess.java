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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.system.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.io.InputStream;
import java.util.regex.*;

public class ExpressionEvaluator_SystemProcess implements ExpressionEvaluator {

    public ExpressionEvaluator_SystemProcess (CallContext context, String[] ids, String id, String command) {
        this.ids = ids;
        this.id = id;
        this.command = command;
        this.result_attribute = new Class_ActivityAttribute(context, "Result", "Object", "-", "*");
        this.activity_interface = new Class_ActivityInterface(context);
        this.activity_interface.addAttribute(context, this.result_attribute);
    }

    protected String[] ids;

    public String[] getIds(CallContext context) {
        return ids;
    }

    protected String id;

    public String getId(CallContext context, Scope scope) {
        return id;
    }

    protected String command;

    public String getCommand(CallContext context, Scope scope) {
        return command;
    }

    protected Class_ActivityInterface activity_interface;
    protected ActivityAttribute result_attribute;

    protected void initialiseProcess(CallContext context, SystemProcess sp) throws Throwable {
    }

    static protected boolean debug = false;

    protected void log(CallContext context, String msg) {
        if (this.debug) { System.err.println("SP: " + msg); }
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp) throws Throwable {
        return waitForCompletion(context, sp, null, null, 2000);
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp, String output_response, String error_response, int sleep) throws Throwable {

        Pattern op = (output_response == null ? null : Pattern.compile(output_response));
        Pattern ep = (error_response == null ? null : Pattern.compile(error_response));
        boolean found = false;
        String output;
        String error;
        do {
            this.log(context, "sleeping...");
            Thread.currentThread().sleep(sleep);

            this.log(context, "getting out...");
            output = sp.getProcessOutputAsString(context, true);
            this.log(context, "getting err...");
            error = sp.getProcessErrorAsString(context, true);

            if (op == null && ep == null) {
                found = true;
            } else {
                if (    (op != null && output != null && op.matcher(output).find())
                     || (ep != null && error != null && ep.matcher(error).find())
                   ) {
                    found = true;
                }
            }
        } while (! found);
        
        this.log(context, "checking...");
        int exit_value = 0;
        if (sp.isFinished(context)) {
            this.log(context, "finished.");
            exit_value = sp.getExitValue(context);
        }

        return new Object[] { new Integer(exit_value),
               ( exit_value != 0 ? "" : ("C> " + exit_value + "\n"))
             + (output == null || output.isEmpty() ? "" : ("O> " + output.replaceFirst("\n*$","").replaceAll("\n", "\nO> ") + "\n"))
             + ( error == null ||  error.isEmpty() ? "" : ("E> " +  error.replaceFirst("\n*$","").replaceAll("\n", "\nE> ") + "\n"))
        };
    }

    protected void startProcess(CallContext context, SystemProcess sp) throws Throwable {
        sp.start(context, null, false, false, true, false, false, true, false);
    }

    static protected RegularExpression debug_on_command  = new RegularExpression("«debug on» *");
    static protected RegularExpression debug_off_command = new RegularExpression("«debug off» *");

    static protected RegularExpression response_re = new RegularExpression("^(?:«([^»]+)»)?(.*)$");

    public Object evaluate(CallContext context, String string, Scope scope) throws EvaluationFailure {
        {
            String[] matches;
            if ((matches = debug_on_command.tryGetMatches(context, string)) != null) {
                this.debug = true;
                return null;
            } else if ((matches = debug_off_command.tryGetMatches(context, string)) != null) {
                this.debug = false;
                return null;
            }
        }

        try {
            boolean session = false;
            SystemProcess sp = null;
            String sp_key = "SystemProcess_" + this.getId(context, scope).replace('.','_');

            if (scope != null) {
                sp = (SystemProcess) scope.tryGet(context, sp_key, "session");
            }
            if (sp != null) {
                session = true;
            } else {
                sp = new SystemProcess(context, this.getCommand(context, scope), "/tmp", this.debug);
                this.startProcess(context, sp);
                this.initialiseProcess(context, sp);

                if (scope != null && scope.containsNameSpace(context, "session")) {
                    session = true;
                    scope.set(context, sp_key, "session", sp);
                }     
            }

            String[] parts = string.split("\n");
            for (String part : parts) {
                String[] matches = response_re.tryGetMatches(context, part);
                String[] oem = (matches[0] == null || matches[0].isEmpty() ? null : matches[0].split("/"));
                if (oem != null) {
                    waitForCompletion(context, sp, 
                                      oem != null && oem.length > 0 && oem[0] != null && oem[0].isEmpty() == false ? oem[0] : null,
                                      oem != null && oem.length > 1 && oem[1] != null && oem[1].isEmpty() == false ? oem[1] : null,
                                      oem != null && oem.length > 2 && oem[2] != null && oem[2].isEmpty() == false && oem[2].matches("[0-9]+") ? Integer.parseInt(oem[2]) : 500);
                }

                this.log(context, "-> " + matches[1]);
                sp.getProcessInputAsWriter(context).write(matches[1] + "\n");
                sp.getProcessInputAsWriter(context).flush();
            }

            this.log(context, "waiting...");
            Object[] result = waitForCompletion(context, sp);

            if (session == false) {
                this.log(context, "stopping...");
                sp.stop(context);
                this.log(context, "waiting...");
                sp.wait(context);
                this.log(context, "closing...");
                sp.closeOutputAndErrorIO(context);
                this.log(context, "done.");
            } else {
                this.log(context, "checking...");
                if (sp.isFinished(context)) {
                    this.log(context, "finished.");
                    scope.set(context, sp_key, "session", null);
                }
            }

            this.log(context, "result check...");
            if (((Integer)(result[0])) != 0) {
                EvaluationFailure.createAndThrow(context, "Evaluation failed: exit code '%(code)', console '%(result)', command '%(command)'", "code", result[0], "result", result[1], "command", string);
                throw (EvaluationFailure) null;
            }

            return result[1];
        } catch (Throwable t) {
            EvaluationFailure.createAndThrow(context, t, "Evaluation failure");
            throw (EvaluationFailure) null;
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
