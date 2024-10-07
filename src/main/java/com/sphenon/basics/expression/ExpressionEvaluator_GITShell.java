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

public class ExpressionEvaluator_GITShell extends ExpressionEvaluator_ShellClient {

    protected String ip;
    protected String port;

    public ExpressionEvaluator_GITShell (CallContext context, String ip, String port) {
        super(context,
              new String[] { "gitshell" },
              "GITShell${actor_id;_}@" + ip + (port == null ? "" : ("(" + port + ")")),
              "ssh" + (port == null ? "" : (" -p " + port)) + " -A -o ServerAliveInterval=120 -t -t ${actor_id;;@}" + ip,
              true,
              false,
              "git>");

        if (ip == null || Patterns.host_address.matches(context, ip) == false) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Invalid IP address '%(ip)'", "ip", ip);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        if (port == null || Patterns.port_number.matches(context, port) == false) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Invalid port '%(port)' (for IP address '%(ip)')", "ip", ip, "port", port);
            throw (ExceptionConfigurationError) null; // compiler insists
        }

        this.ip   = ip;
        this.port = port;
    }
}
