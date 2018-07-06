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

import com.sphenon.basics.expression.bashprompt.*;

import java.io.Writer;
import java.io.OutputStreamWriter;

public class ExpressionEvaluator_SSH extends ExpressionEvaluator_Bash {

    protected String ip;

    public ExpressionEvaluator_SSH (CallContext context, String ip) {
        super(context, null, null, true);
        this.ip = ip;
    }

    public String getId(CallContext context, Scope scope) {
        String actor_id = (String) scope.tryGet(context, "ActorId");
        boolean got_actor_id = (actor_id != null && actor_id.isEmpty() == false);

        return "SSH" + (got_actor_id ? ("_" + actor_id) : "") + "@" + this.ip;
    }

    public String getCommand(CallContext context, Scope scope) {
        String actor_id = (String) scope.tryGet(context, "ActorId");
        boolean got_actor_id = (actor_id != null && actor_id.isEmpty() == false);
        String identity = "";
        if (got_actor_id) {
            identity = " -i ~/.ssh/algwk2005";
        }

        return "ssh" + identity + " -A -o ServerAliveInterval=120 -t -t " + (got_actor_id ? (actor_id + "@") : "") + this.ip + " /bin/bash --noprofile --norc -i";
    }
}
