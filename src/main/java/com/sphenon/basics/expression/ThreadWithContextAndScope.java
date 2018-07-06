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

import com.sphenon.basics.expression.classes.*;

abstract public class ThreadWithContextAndScope extends ThreadWithContext {

    public ThreadWithContextAndScope (ThreadContext thread_context, Scope scope) {
        super(thread_context);
        this.scope = scope;
    }

    protected Scope scope;

    public Scope getScope (CallContext context) {
        if (this.scope == null) {
            this.scope = new Class_Scope(context);
        }
        return this.scope;
    }

    public void setScope (CallContext context, Scope scope) {
        this.scope = scope;
    }
}
