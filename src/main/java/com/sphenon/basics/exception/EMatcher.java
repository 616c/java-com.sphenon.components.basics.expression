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
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class EMatcher implements ExceptionMatcher {

    public EMatcher(Classes include, Classes exclude, CMatcher child_matcher, String message_template) {
        this.include          = (include == null ? null : include.classes);
        this.exclude          = (exclude == null ? null : exclude.classes);
        this.child_matcher    = child_matcher;
        this.message_template = message_template;
        this.message_include  = null;
        this.message_exclude  = null;
    }

    public EMatcher(Classes include, Classes exclude, CMatcher child_matcher, String message_template, String message_include, String message_exclude) {
        this.include          = (include == null ? null : include.classes);
        this.exclude          = (exclude == null ? null : exclude.classes);
        this.child_matcher    = child_matcher;
        this.message_template = message_template;
        this.message_include  = message_include;
        this.message_exclude  = message_exclude;
    }

    public Class[] include;
    public Class[] exclude;
    public CMatcher child_matcher;
    public String message_template;
    public String message_include;
    public String message_exclude;

    public EMatch matches(CallContext context, Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        boolean included = false;
        if (include != null) {
            for (Class c : include) {
                if (c.isInstance(throwable)) { included = true; break; }
            }
            if (included == false) {
                return null;
            }
        }

        boolean excluded = false;
        if (exclude != null) {
            for (Class c : exclude) {
                if (c.isInstance(throwable)) { excluded = true; break; }
            }
            if (excluded == true) {
                return null;
            }
        }

        String match_text = null;
        if (    message_include != null
             || message_exclude != null
           ) {
            ExceptionWithHelpMessage ewhm = (throwable instanceof ExceptionWithHelpMessage ? ((ExceptionWithHelpMessage) throwable) : null);
            match_text = (ewhm == null ? throwable.getMessage() : ewhm.getMessage(false));
        }

        if (message_include != null) {
            if (match_text.matches(message_include) == false) {
                return null;
            }
        }

        if (message_exclude != null) {
            if (match_text.matches(message_exclude) == true) {
                return null;
            }
        }

        EMatch[] child_matches = null;
        if (child_matcher != null) {
            child_matches = child_matcher.matches(context, throwable);
            if (child_matches == null) {
                return null;
            }
        }

        return new EMatch(throwable, this, child_matches);
    }
};
