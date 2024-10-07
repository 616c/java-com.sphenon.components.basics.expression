package com.sphenon.basics.exception;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.variatives.tplinst.*;
import com.sphenon.basics.variatives.classes.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class EMatch implements ExceptionMatch {

    public EMatch(Throwable throwable, EMatcher exception_matcher, EMatch[] child_matches) {
        this.throwable         = throwable;
        this.exception_matcher = exception_matcher;
        this.child_matches     = child_matches;
    }

    public Throwable throwable;
    public EMatcher exception_matcher;
    public EMatch[] child_matches;

    public Variative_String_ getText(CallContext context) {
        return VariativeStringTrivial.createVariativeStringTrivial(context, this.getPlainText(context));
    }

    protected String[] child_texts;

    protected String[] getChildTexts(CallContext context) {
        if (this.child_texts == null) {
            this.child_texts = new String[child_matches == null ? 0 : child_matches.length];
            if (child_matches != null) {
                int cm = 0;
                for(EMatch child_match : child_matches) {
                    child_texts[cm++] = child_match.getPlainText(context);
                }
            }
        }
        return child_texts;
    }

    public String getPlainText(CallContext context) {
        String tcn = this.throwable.getClass().getName();
        ExceptionWithHelpMessage ewhm = (this.throwable instanceof ExceptionWithHelpMessage ? ((ExceptionWithHelpMessage) this.throwable) : null);
        Scope scope = new Class_Scope(context,
                                      "classname", tcn.replaceFirst(".*\\.", ""),
                                      "fullclass", tcn,
                                      "message"  , (ewhm == null ? this.throwable.getMessage() : ewhm.getMessage(false))
                                     );
        if (ewhm != null) { scope.set(context, "help", ewhm.getHelpMessage()); }
        scope.setOnDemand(context, "causes", new DataSourceBase<String[]>() { public String[] get(CallContext context) {
            return getChildTexts(context);
        } });
        scope.setOnDemand(context, "cause", new DataSourceBase<String>() { public String get(CallContext context) {
            return (getChildTexts(context).length == 0 ? ((String) null) : (getChildTexts(context)[0]));
        } });
        return DynamicString.process(context, this.exception_matcher.message_template, null, scope);
    }
}
