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

import com.sphenon.basics.expression.bashprompt.*;

import java.io.Writer;
import java.io.OutputStreamWriter;

public class ExpressionEvaluator_Bash extends ExpressionEvaluator_SystemProcess {

    public ExpressionEvaluator_Bash (CallContext context) {
        this(context, null, null, false);
    }

    public ExpressionEvaluator_Bash (CallContext context, String id, String shell_command, boolean expect_prompt_at_stdout) {
        super(context, new String[] { "bash" }, id, shell_command);
        this.expect_prompt_at_stdout = expect_prompt_at_stdout;
    }

    public String getId(CallContext context, Scope scope) {
        String actor_id = (scope == null ? null : (String) scope.tryGet(context, "ActorId"));
        boolean got_actor_id = (actor_id != null && actor_id.isEmpty() == false);

        return "bash" + (got_actor_id ? ("_" + actor_id) : "");
    }

    public String getCommand(CallContext context, Scope scope) {
        String actor_id = (scope == null ? null : (String) scope.tryGet(context, "ActorId"));
        boolean got_actor_id = (actor_id != null && actor_id.isEmpty() == false);

        return (got_actor_id ? ("ssh " + actor_id + "@localhost ") : "") + "/bin/bash --noprofile --norc --noediting -i +o history";
    }

    protected boolean expect_prompt_at_stdout;

    protected void startProcess(CallContext context, SystemProcess sp) throws Throwable {
        this.log(context, "starting...");
        if (this.dump) {
            if (expect_prompt_at_stdout) {
                sp.start(context, null, false, true, false, true, false, true, false);
                sp.configure(context, false, true, false, true, false, false);
            } else {
                sp.start(context, null, true, false, true, false, true, false, false);
                sp.configure(context, true, false, false, false, true, false);
            }
        } else {
            if (expect_prompt_at_stdout) {
                sp.start(context, null, false, false, false, false, false, true, false);
                sp.configure(context, false, true, false, false, false, true);
            } else {
                sp.start(context, null, false, false, true, false, false, false, false);
                sp.configure(context, false, false, true, false, true, false);
            }
        }
    }

    public void initialiseProcess(CallContext context, SystemProcess sp) throws Throwable {
        if (expect_prompt_at_stdout) {
            sp.configure(context, false, true, false, false, false, false);
        } else {
            sp.configure(context, false, false, false, false, true, false);
        }

        this.log(context, "-> set...");
        sp.getProcessInputAsWriter(context).write(" set +o history\n");
        this.log(context, "-> export...");
        sp.getProcessInputAsWriter(context).write("export PS1='BashPrompt_''03894725475969372625647595039237256364859590'\n");
        this.log(context, "flushing...");
        sp.getProcessInputAsWriter(context).flush();

        this.log(context, "waiting...");
        this.waitForCompletion(context, sp, true);
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp) throws Throwable {
        return waitForCompletion(context, sp, false);
    }

    protected Object[] waitForCompletion(CallContext context, SystemProcess sp, boolean suppress_console) throws Throwable {
        if (expect_prompt_at_stdout) {
            this.log(context, "waiting for prompt (stdout)...");
            OutputStreamWriter stdout_writer = this.dump && ! suppress_console ? new OutputStreamWriter(System.out) : null;
            String output = BashPromptObserver.waitForPrompt(context, sp.getProcessOutputAsReader(context), ! this.dump || suppress_console, stdout_writer);
            if (stdout_writer != null) { stdout_writer.flush(); }
        } else {
            this.log(context, "waiting for prompt (stderr)...");
            OutputStreamWriter stderr_writer = this.dump && ! suppress_console ? new OutputStreamWriter(System.err) : null;
            String error = BashPromptObserver.waitForPrompt(context, sp.getProcessErrorAsReader(context), ! this.dump || suppress_console, stderr_writer);
            if (stderr_writer != null) { stderr_writer.flush(); }
        }

        this.log(context, "waiting for listeners...");
        sp.waitForListeners(context);

        String error = null;
        String output = null;
        if (expect_prompt_at_stdout) {
            this.log(context, "getting and clearing err...");
            error = sp.getProcessErrorAsString(context, true);
        } else {
            this.log(context, "getting and clearing out...");
            output = sp.getProcessOutputAsString(context, true);
        }

        this.log(context, "checking...");
        int exit_value = 0;
        if (sp.isFinished(context)) {
            this.log(context, "finished.");
            exit_value = sp.getExitValue(context);
        } else {
            if (expect_prompt_at_stdout) {
                sp.configure(context, false, true, false, false, false, true);
            } else {
                sp.configure(context, false, false, true, false, true, false);
            }

            this.log(context, "-> echo...");
            sp.getProcessInputAsWriter(context).write("echo $?\n");
            sp.getProcessInputAsWriter(context).flush();

            String expect_empty_err = null;
            String code_output = null;

            this.log(context, "waiting for prompt...");
            if (expect_prompt_at_stdout) {
                code_output = BashPromptObserver.waitForPrompt(context, sp.getProcessOutputAsReader(context), true, null);
            } else {
                expect_empty_err = BashPromptObserver.waitForPrompt(context, sp.getProcessErrorAsReader(context), true, null);
            }

            int trials = 1;
            do {
                this.log(context, "waiting for listeners...");
                sp.waitForListeners(context);

                if (expect_prompt_at_stdout) {
                    this.log(context, "getting and clearing err...");
                    expect_empty_err = sp.getProcessErrorAsString(context, true);
                } else {
                    this.log(context, "getting and clearing out...");
                    code_output = sp.getProcessOutputAsString(context, true);
                }

                if (expect_empty_err != null && expect_empty_err.isEmpty() == false) {
                    CustomaryContext.create((Context)context).throwEnvironmentFailure(context, "Bash returned non empty stderr from result inspection: '%(stderr)'", "stderr", expect_empty_err);
                    throw (ExceptionEnvironmentFailure) null; // compiler insists
                }

                if (code_output != null && (code_output = code_output.replaceFirst("echo \\$\\?","").replaceAll("[ \n\r\t]+", "")).matches("[0-9]+\n*")) {
                    break;
                } else {
                    if (trials <= 5) {
                        this.log(context, "result not ok yet, trying again...");
                        trials++;
                        Thread.currentThread().sleep(500);
                    } else {
                        CustomaryContext.create((Context)context).throwEnvironmentFailure(context, "Bash returned non integer from result inspection: '%(stdout)'", "stdout", code_output);
                        throw (ExceptionEnvironmentFailure) null; // compiler insists
                    }
                }
            } while (true);

            exit_value = Integer.parseInt(code_output);

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
        }

        return new Object[] { new Integer(exit_value),
               ( exit_value == 0 ? "" : ("C> " + exit_value + "\n"))
             + (output == null || output.isEmpty() ? "" : ("O> " + output.replaceFirst("\n*$","").replaceAll("\n", "\nO> ") + "\n"))
             + ( error == null ||  error.isEmpty() ? "" : ("E> " +  error.replaceFirst("\n*$","").replaceAll("\n", "\nE> ") + "\n"))
        };
    }
}
