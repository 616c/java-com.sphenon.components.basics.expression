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

import java.util.regex.*;

public class NamedRegularExpressionFilter {

    public NamedRegularExpressionFilter (String name, String include_regexp, String exclude_regexp) {
        init(RootContext.getInitialisationContext(), name, include_regexp, exclude_regexp);
    }

    public NamedRegularExpressionFilter (CallContext context, String name, String include_regexp, String exclude_regexp) {
        init(context, name, include_regexp, exclude_regexp);
    }

    protected String name;

    public String getName (CallContext context) {
        return this.name;
    }

    public void setName (CallContext context, String name) {
        this.name = name;
    }

    protected String include_regexp;

    public String getIncludeRegexp (CallContext context) {
        return this.include_regexp;
    }

    public void setIncludeRegexp (CallContext context, String include_regexp) {
        this.include_regexp = include_regexp;
        this.include_re = include_regexp == null ? null : new RegularExpression(context, include_regexp);
    }

    protected String exclude_regexp;

    public String getExcludeRegexp (CallContext context) {
        return this.exclude_regexp;
    }

    public void setExcludeRegexp (CallContext context, String exclude_regexp) {
        this.exclude_regexp = exclude_regexp;
        this.exclude_re = exclude_regexp == null ? null : new RegularExpression(context, exclude_regexp);
    }

    protected RegularExpression include_re;
    protected RegularExpression exclude_re;

    protected void init (CallContext context, String name, String include_regexp, String exclude_regexp) {
        this.name = name;
        this.setIncludeRegexp(context, include_regexp);
        this.setExcludeRegexp(context, exclude_regexp);
    }

    public boolean matches(CallContext context, String text) {
        return (    (this.include_re == null || (this.include_re.matches(context, text) == true))
                 && (this.exclude_re == null || (this.exclude_re.matches(context, text) == false))
               );
    }

    static public NamedRegularExpressionFilter[] createArray(String... spec) {
        NamedRegularExpressionFilter[] nrefa = new NamedRegularExpressionFilter[spec.length / 3];
        for (int i=0, j=0; i<spec.length; i+=3, j++) {
            nrefa[j] = new NamedRegularExpressionFilter(spec[i], spec[i+1], spec[i+2]);
        }
        return nrefa;
    }
}
