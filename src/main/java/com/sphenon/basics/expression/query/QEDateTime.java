package com.sphenon.basics.expression.query;

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
import com.sphenon.basics.format.*;

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Date;

public class QEDateTime extends QEValue {

    protected String isodate;
    protected String isotime;
    protected String isozone;

    public QEDateTime(CallContext context, String isodate, String isotime, String isozone) {
        super(context);
        this.isodate = isodate;
        this.isotime = isotime;
        this.isozone = isozone;
        this.date = null;
    }

    public QEDateTime(CallContext context, Date date) {
        super(context);
        this.isodate = null;
        this.isotime = null;
        this.isozone = null;
        this.date = date;
    }

    protected String guessDateFormat(CallContext context, String isodate) {
        if      (isodate.matches("^[0-9]{4}[0-9]{2}[0-9]{2}$"))       { return "yyyyMMdd"; }
        else if (isodate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))     { return "yyyy-MM-dd"; }
        else if (isodate.matches("^[0-9]{4}-[0-9]{2}$"))              { return "yyyy-MM"; }
        else if (isodate.matches("^[0-9]{4}$"))                       { return "yyyy"; }
        else if (isodate.matches("^[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}$")) { return "dd.MM.yyyy"; }
        else if (isodate.matches("^[0-9]{2}\\.[0-9]{4}$"))            { return "MM.yyyy"; }
        else                                                          { return ""; }
    }

    protected String guessTimeFormat(CallContext context, String isotime) {
        if      (isotime.matches("^[0-9]{2}[0-9]{2}[0-9]{2}$"))   { return "HHmmss"; }
        else if (isotime.matches("^[0-9]{2}:[0-9]{2}:[0-9]{2}$")) { return "HH:mm:ss"; }
        else if (isotime.matches("^[0-9]{2}:[0-9]{2}$"))          { return "HH:mm"; }
        else if (isotime.matches("^[0-9]{2}$"))                   { return "HH"; }
        else                                                      { return ""; }
    }

    protected String optionallyAddSeparator(CallContext context, String format) {
        return (format.isEmpty() ? "" : ";") + format;
    }

    protected Date date;

    public Object getValue (CallContext context) {
        if (this.date == null) {
            String zone = isozone == null ? "" : isozone;
            this.date = (Date) ( this.isodate == null || this.isodate.isEmpty()
                                 ? Formatter.parse(context,
                                                   "time:" + optionallyAddSeparator(context, guessTimeFormat(context, this.isotime)),
                                                   this.isotime + zone)
                                 : ( this.isotime == null || this.isotime.isEmpty()
                                     ? Formatter.parse(context,
                                                       "date:" + optionallyAddSeparator(context, guessDateFormat(context, this.isodate)),
                                                       this.isodate + zone)
                                     : Formatter.parse(context,
                                                       "datetime:" + optionallyAddSeparator(context,guessDateFormat(context, this.isodate) + "T" + guessTimeFormat(context, this.isotime)),
                                                       this.isodate + "T" + this.isotime + zone)
                                   )
                               );
        }
        return this.date;
    }

    public void setValue (CallContext context, String isodate, String isotime, String isozone) {
        this.isodate = isodate;
        this.isotime = isotime;
        this.isozone = isozone;
        this.date = null;
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        toLogicalExpressionString(context, buffer);
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        if (this.isodate == null && this.isotime == null && this.isozone == null && this.date != null) {
            buffer.append(this.date.toString());
        }
        if (this.isodate != null && ! this.isodate.isEmpty()) {
            buffer.append(this.isodate);
        }
        if (    this.isodate != null && ! this.isodate.isEmpty()
             && this.isotime != null && ! this.isotime.isEmpty()) {
            buffer.append("T");
        }
        if (this.isotime != null && ! this.isotime.isEmpty()) {
            buffer.append(this.isotime);
        }
        if (this.isozone != null && ! this.isozone.isEmpty()) {
            buffer.append(this.isozone);
        }
    }
}
