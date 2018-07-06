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

import java.util.regex.*;

// Java:
//      http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
// Unicode Regexps:
//      http://www.unicode.org/reports/tr18/
//      http://www.unicode.org/reports/tr18/#Categories

public class RegularExpression {

    protected Pattern pattern = null;
    protected String regexpstring = null;
    protected String replacement = null;

    static public RegularExpression optinallyCreate(CallContext context, String regexp) {
        if (regexp == null) { return null; }
        return new RegularExpression(context, regexp);
    }

    public RegularExpression (String regexp) {
        init(RootContext.getInitialisationContext(), regexp);
    }

    public RegularExpression (String regexp, String replacement) {
        init(RootContext.getInitialisationContext(), regexp);
        this.replacement = replacement;
    }

    public RegularExpression (CallContext context, String regexp) {
        init(context, regexp);
    }

    public RegularExpression (CallContext context, String regexp, String replacement) {
        init(context, regexp);
        this.replacement = replacement;
    }

    protected void init (CallContext context, String regexp) {
        this.regexpstring = regexp;
        try {
            pattern = Pattern.compile(regexp);
        } catch (PatternSyntaxException pse) {
            CustomaryContext.create(Context.create(context)).throwAssertionProvedFalse(context, pse, "Syntax error in regular expression '%(regexp)'", "regexp", regexp);
            throw (ExceptionAssertionProvedFalse) null; // compiler insists
        }
    }

    public Matcher getMatcher(CallContext context, String text) {
        return pattern.matcher(text);
    }

    public Matcher getMatcher(CallContext context, CharSequence input) {
        return pattern.matcher(input);
    }

    public String replaceAll(CallContext context, String text) {
        return this.getMatcher(context, text).replaceAll(this.replacement);
    }

    public String replaceFirst(CallContext context, String text) {
        return this.getMatcher(context, text).replaceFirst(this.replacement);
    }

    public boolean matches(CallContext context, String text) {
        return pattern.matcher(text).matches();
    }

    public boolean find(CallContext context, String text) {
        return pattern.matcher(text).find();
    }

    public String[] tryGetMatches(CallContext context, String text) {
        if (text == null) { return null; }
        Matcher matcher = pattern.matcher(text);
        if ( ! matcher.find()) { return null; }
        String[] result = new String[matcher.groupCount()];
        for (int g=1; g<=matcher.groupCount(); g++) {
            result[g-1] = matcher.group(g);
        }
        return result;
    }

    public String[] tryGetMatches(CallContext context, Matcher matcher) {
        if ( ! matcher.find()) { return null; }
        String[] result = new String[matcher.groupCount()];
        for (int g=1; g<=matcher.groupCount(); g++) {
            result[g-1] = matcher.group(g);
        }
        return result;
    }

    public String[] getMatches(CallContext context, String text, String help_text) {
        String[] result = tryGetMatches(context, text);
        if (result == null) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Invalid syntax in %(help): '%(string)', expected '%(expected)'", "help", help_text, "string", text, "expected", this.regexpstring);
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        return result;
    }

    public String toString() {
        return this.regexpstring + (this.replacement == null ? "" : (" ==> " + this.replacement));
    }
}
