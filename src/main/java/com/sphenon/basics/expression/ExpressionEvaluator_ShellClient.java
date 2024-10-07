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

import java.util.Scanner;

import java.io.Reader;
import java.io.Writer;
import java.io.OutputStreamWriter;

public class ExpressionEvaluator_ShellClient extends ExpressionEvaluator_SystemProcess {

    public ExpressionEvaluator_ShellClient (CallContext context) {
        this(context, null, null, null, false, true, null);
    }

    public ExpressionEvaluator_ShellClient (CallContext context, String[] ids, String id_template, String command_template, boolean expect_prompt_at_stdout, boolean expect_prompt_at_stderr, String prompt_regexp) {
        super(context, ids, id_template, command_template);
        this.expect_prompt_at_stdout = expect_prompt_at_stdout;
        this.expect_prompt_at_stderr = expect_prompt_at_stderr;
        this.prompt_regexp = prompt_regexp;
    }

    protected boolean expect_prompt_at_stdout;
    protected boolean expect_prompt_at_stderr;
    protected String prompt_regexp;

    public String getId(CallContext context, Scope scope) {
        String actor_id = (scope == null ? null : (String) scope.tryGet(context, "ActorId"));

        return DynamicString.process(context, id, "placeholder", "actor_id", actor_id);
    }

    public String getCommand(CallContext context, Scope scope) {
        String actor_id = (scope == null ? null : (String) scope.tryGet(context, "ActorId"));

        return DynamicString.process(context, command, "placeholder", "actor_id", actor_id);
    }

    protected void startProcess(CallContext context, SystemProcess sp) throws Throwable {
        this.log(context, "starting...");
        if (this.dump) {
            if (expect_prompt_at_stdout) {
                sp.start(context, null, false, true, false, true, false, true, false);
                sp.configure(context, false, true, false, true, false, false);
            }
            if (expect_prompt_at_stderr) {
                sp.start(context, null, true, false, true, false, true, false, false);
                sp.configure(context, true, false, false, false, true, false);
            }
        } else {
            if (expect_prompt_at_stdout) {
                sp.start(context, null, false, false, false, false, false, true, false);
                sp.configure(context, false, true, false, false, false, true);
            }
            if (expect_prompt_at_stderr) {
                sp.start(context, null, false, false, true, false, false, false, false);
                sp.configure(context, false, false, true, false, true, false);
            }
        }
    }

    public void initialiseProcess(CallContext context, SystemProcess sp) throws Throwable {
        if (expect_prompt_at_stdout) {
            sp.configure(context, false, true, false, false, false, false);
        }
        if (expect_prompt_at_stderr) {
            sp.configure(context, false, false, false, false, true, false);
        }

        this.prepareShell(context, sp);

        this.waitForCompletion(context, sp, true, false);
    }

    protected void prepareShell(CallContext context, SystemProcess sp) {
    }

    protected String[] waitForPrompt(CallContext context, SystemProcess sp, boolean suppress_console, boolean want_result) throws Throwable {
        if (prompt_regexp != null && prompt_regexp.isEmpty() == false) {

            Reader    r       = null;
            TeeReader tr      = null;
            Scanner   scanner = null;

            if (expect_prompt_at_stdout) {
                this.log(context, "waiting for prompt (stdout)...");
                r = sp.getProcessOutputAsReader(context);
            }
            if (expect_prompt_at_stderr) {
                this.log(context, "waiting for prompt (stderr)...");
                r = sp.getProcessErrorAsReader(context);
            }

            if (    (this.dump && ! suppress_console)
                 || want_result
               ) {
                tr = new TeeReader(r, null);
                scanner = new Scanner(tr);
            } else {
                scanner = new Scanner(r);
            }

            scanner.findWithinHorizon(prompt_regexp, 0);

            if (this.dump && ! suppress_console) {
                if (expect_prompt_at_stdout) {
                    System.out.print(tr.getString());
                }
                if (expect_prompt_at_stderr) {
                    System.err.print(tr.getString());
                }
            }

            if (want_result) {
                if (expect_prompt_at_stdout) {
                    return new String[] { tr.getString(), null };
                }
                if (expect_prompt_at_stderr) {
                    return new String[] { null, tr.getString() };
                }
            }

        }

        return null;
    }

    protected int getLastCommandResultCode(CallContext context, SystemProcess sp) throws Throwable {
        return 0;
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp) throws Throwable {
        return waitForCompletion(context, sp, false, false);
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp, boolean suppress_console, boolean want_result) throws Throwable {
        String[] before_prompt = this.waitForPrompt(context, sp, suppress_console, want_result);

        this.log(context, "waiting for listeners...");
        sp.waitForListeners(context);

        String output = (    want_result == false
                          || before_prompt == null
                          || before_prompt.length < 2
                          || before_prompt[0] == null
                        ) ?  null : before_prompt[0];
        String error  = (    want_result == false
                          || before_prompt == null
                          || before_prompt.length < 2
                          || before_prompt[1] == null
                        ) ?  null : before_prompt[1];

        if (expect_prompt_at_stdout) {
            this.log(context, "getting and clearing err...");
            error = (error == null ? "" : error) + sp.getProcessErrorAsString(context, true);
        }
        if (expect_prompt_at_stderr) {
            this.log(context, "getting and clearing out...");
            output = (output == null ? "" : output) + sp.getProcessOutputAsString(context, true);
        }

        int exit_value = 0;

        this.log(context, "checking...");
        if (sp.isFinished(context)) {
            this.log(context, "finished.");
            exit_value = sp.getExitValue(context);
        } else {
            exit_value = this.getLastCommandResultCode(context, sp);
        }

        if (this.dump) {
            if (expect_prompt_at_stdout) {
                sp.configure(context, false, true, false, true, false, false);
            } else {
                sp.configure(context, true, false, false, false, true, false);
            }
        } else {
            if (expect_prompt_at_stdout) {
                sp.configure(context, false, true, false, false, false, true);
            } else {
                sp.configure(context, false, false, true, false, true, false);
            }
        }

        return new Object[] {
            new Integer(exit_value),
               ( exit_value == 0 ? "" : ("C> " + exit_value + "\n"))
             + (output == null || output.isEmpty() ? "" : ("O> " + output.replaceFirst("\n*$","").replaceAll("\n", "\nO> ") + "\n"))
             + ( error == null ||  error.isEmpty() ? "" : ("E> " +  error.replaceFirst("\n*$","").replaceAll("\n", "\nE> ") + "\n")),
             want_result ? output : null,
             want_result ? error  : null
        };
    }
}
