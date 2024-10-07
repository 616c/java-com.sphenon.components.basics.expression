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
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.system.*;
import static com.sphenon.basics.system.StringUtilities.nullIfEmpty;
import static com.sphenon.basics.system.StringUtilities.isNotEmpty;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.basics.operations.factories.*;

import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.returncodes.*;

import java.io.InputStream;
import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;

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

    // [DumpBug] - see also below
    // warning! does not work with false and ExpressionEvaluator_Bash
    // since in sp.start(context, null, false, false, true, false, false, false, false);
    //                                                             ^^^^^
    // no error stream will be initialised, but later in
    // BashPromptObserver.waitForPrompt(... sp.getProcessErrorAsReader(context)
    // it is needed -> null pointer
    static protected boolean dump = true;

    protected void log(CallContext context, String msg) {
        if (this.debug) { System.err.println("SP: " + msg); }
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp) throws Throwable {
        return waitForCompletion(context, sp, null, null, 2000, false);
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp, String output_response, String error_response, int sleep, boolean want_result) throws Throwable {

        Pattern op = (output_response == null ? null : Pattern.compile(output_response));
        Pattern ep = (error_response == null ? null : Pattern.compile(error_response));
        boolean found = false;
        String output = "";
        String error = "";
        do {
            this.log(context, "sleeping...");
            Thread.currentThread().sleep(sleep);

            this.log(context, "getting out...");
            output += sp.getProcessOutputAsString(context, true);
            this.log(context, "getting err...");
            error += sp.getProcessErrorAsString(context, true);

            if (op == null && ep == null) {
                found = true;
            } else {
                if (    (op != null && output != null && op.matcher(output).find())
                     || (ep != null && error != null && ep.matcher(error).find())
                   ) {
                    found = true;
                }
            }
        } while ( ! found);

        this.log(context, "checking...");
        int exit_value = 0;
        if (sp.isFinished(context)) {
            this.log(context, "finished.");
            exit_value = sp.getExitValue(context);
        }

        return new Object[] {
            new Integer(exit_value),
               ( exit_value != 0 ? "" : ("C> " + exit_value + "\n"))
             + (output == null || output.isEmpty() ? "" : ("O> " + output.replaceFirst("\n*$","").replaceAll("\n", "\nO> ") + "\n"))
             + ( error == null ||  error.isEmpty() ? "" : ("E> " +  error.replaceFirst("\n*$","").replaceAll("\n", "\nE> ") + "\n")),
            want_result ? output : null,
            want_result ?  error : null
        };
    }

    protected void startProcess(CallContext context, SystemProcess sp) throws Throwable {
        sp.start(context, null, false, false, true, false, false, true, false);
    }

    static protected RegularExpression debug_on_command  = new RegularExpression("«debug on» *");
    static protected RegularExpression debug_off_command = new RegularExpression("«debug off» *");
    static protected RegularExpression dump_on_command  = new RegularExpression("«dump on» *");
    static protected RegularExpression dump_off_command = new RegularExpression("«dump off» *");

    static protected RegularExpression response_re = new RegularExpression
        (   "^(?:«"
          +   "(?<out>[^»/~]+)?"
          +   "(?:/(?<err>[^»/~]+)?)?"
          +   "(?:/(?<wait>[^»/~]+)?)?"
          +   "(?:/(?<skip>---))?"
          +   "(?:~(?<outpos>[^»/!]+)?"
          +     "(?:/(?<outneg>[^»/!]+))?"
          +     "(?:!(?<errpos>[^»/]+)?"
          +       "(?:/(?<errneg>[^»/]+)?)?"
          +     ")?"
          +   ")?"
          + "»)?"
          + "(?<command>.*)$"
        );

    public Object evaluate(CallContext context, String string, Scope scope, DataSink<Execution> execution_sink) throws EvaluationFailure {
        Execution_Basic e = null;
        if (execution_sink != null) {
            e = (Execution_Basic) Factory_Execution.createExecutionInProgress(context, string);
            execution_sink.set(context, e);
        }

        {
            String[] matches;
            if ((matches = debug_on_command.tryGetMatches(context, string)) != null) {
                this.debug = true;
                if (e != null) { e.setSuccess(context); }
                return null;
            } else if ((matches = debug_off_command.tryGetMatches(context, string)) != null) {
                this.debug = false;
                if (e != null) { e.setSuccess(context); }
                return null;
            // [DumpBug] - see above
            // } else if ((matches = dump_on_command.tryGetMatches(context, string)) != null) {
            //     this.dump = true;
            //     if (e != null) { e.setSuccess(context); }
            //     return null;
            // } else if ((matches = dump_off_command.tryGetMatches(context, string)) != null) {
            //     this.dump = false;
            //     if (e != null) { e.setSuccess(context); }
            //     return null;
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

            int exit_code = 0;
            boolean waited = false;
            Object[] result = null;

            String[] parts = string.split("\n");
            for (String part : parts) {
                Map<String,String> named_groups = new HashMap<String,String>();
                String[] matches = response_re.tryGetMatches(context, part, named_groups, "out", "err", "wait", "skip", "outpos", "outneg", "errpos", "errneg", "command");
                boolean check_result = false;
                if (matches != null) {
                    String wait = nullIfEmpty(named_groups.get("wait"));
                    check_result = (    named_groups.containsKey("outpos") || named_groups.containsKey("outneg")
                                     || named_groups.containsKey("errpos") || named_groups.containsKey("errneg")
                                   ) ? true : false;

                    result = waitForCompletion
                        (context, sp, 
                         nullIfEmpty(Encoding.recode(context, named_groups.get("out"), Encoding.URI, Encoding.UTF8)),
                         nullIfEmpty(Encoding.recode(context, named_groups.get("err"), Encoding.URI, Encoding.UTF8)),
                         wait != null && wait.matches("[0-9]+") ? Integer.parseInt(wait) : 500,
                         check_result
                        );
                    waited = true;
                }

                if (check_result) {
                    String outpos = named_groups.get("outpos");
                    if (isNotEmpty(outpos)) {
                        if (((String) result[2]).matches(Encoding.recode(context, outpos, Encoding.URI, Encoding.UTF8)) == false) { exit_code = 1001; break; }
                    }
                    String outneg = named_groups.get("outneg");
                    if (isNotEmpty(outneg)) {
                        if (((String) result[2]).matches(Encoding.recode(context, outneg, Encoding.URI, Encoding.UTF8)) == true ) { exit_code = 1002; break; }
                    }
                    String errpos = named_groups.get("errpos");
                    if (isNotEmpty(errpos)) {
                        if (((String) result[3]).matches(Encoding.recode(context, errpos, Encoding.URI, Encoding.UTF8)) == false) { exit_code = 1003; break; }
                    }
                    String errneg = named_groups.get("errneg");
                    if (isNotEmpty(errneg)) {
                        if (((String) result[3]).matches(Encoding.recode(context, errneg, Encoding.URI, Encoding.UTF8)) == true ) { exit_code = 1004; break; }
                    }
                }

                if (named_groups.containsKey("skip") == false) {
                    String command = named_groups.get("command");
                    this.log(context, "-> " + command);
                    sp.getProcessInputAsWriter(context).write(command + "\n");
                    sp.getProcessInputAsWriter(context).flush();
                    waited = false;
                }
            }

            if (waited == false) {
                this.log(context, "waiting...");
                result = waitForCompletion(context, sp);
            }

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
            if (exit_code != 0) {
                EvaluationFailure.createAndThrow(context, "Evaluation failed: result did not match expected patterns, exit code '%(code)', console '%(result)', command '%(command)'", "code", result[0], "result", result[1], "command", string);
                throw (EvaluationFailure) null;
            }
            if (((Integer)(result[0])) != 0) {
                EvaluationFailure.createAndThrow(context, "Evaluation failed: exit code '%(code)', console '%(result)', command '%(command)'", "code", result[0], "result", result[1], "command", string);
                throw (EvaluationFailure) null;
            }

            if (e != null) { e.setSuccess(context); }

            return result[1];
        } catch (Throwable t) {
            if (e != null) { e.setFailure(context, t); }
            EvaluationFailure.createAndThrow(context, t, "Evaluation failure");
            throw (EvaluationFailure) null;
        }
    }

    public ActivityClass parse(CallContext context, ExpressionSource expression_source) throws EvaluationFailure {
        return new ActivityClass_ExpressionEvaluator(context, this, expression_source, this.activity_interface, this.result_attribute);
    }
}
