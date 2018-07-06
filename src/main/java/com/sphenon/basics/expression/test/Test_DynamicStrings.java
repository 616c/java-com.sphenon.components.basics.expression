package com.sphenon.basics.expression.test;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.expression.templates.*;
import com.sphenon.basics.expression.returncodes.*;

import com.sphenon.basics.testing.*;

public class Test_DynamicStrings extends com.sphenon.basics.testing.classes.TestBase {

    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.testing.test.Test_Operation"); };

    public Test_DynamicStrings (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "DynamicStrings";
        }
        return this.id;
    }

    protected String[] dynamic_strings;

    public String[] getDynamicStrings (CallContext context) {
        return this.dynamic_strings;
    }

    public void setDynamicStrings (CallContext context, String[] dynamic_strings) {
        this.dynamic_strings = dynamic_strings;
    }

    static public class Arguments {
        public Arguments(CallContext context) {
        }
        protected Object[] arguments;
        public void set_ParametersAtOnce(CallContext call_context, String[] names, String[] values) {
            this.arguments = new Object[names.length * 2];
            for (int i=0, j=0; i<names.length; i++) {
                this.arguments[j++] = names[i];
                this.arguments[j++] = values[i];
            }
        }
        public Object[] getArguments(CallContext context) {
            return this.arguments;
        }
    }

    protected Arguments arguments;

    public Arguments getArguments (CallContext context) {
        return this.arguments;
    }

    public void setArguments (CallContext context, Arguments arguments) {
        this.arguments = arguments;
    }

    public TestResult perform (CallContext call_context, TestRun test_run) {
        Context context = Context.create(call_context);

        try {

            for (String dynamic_string : dynamic_strings) {
                String result = (new DynamicString(context, dynamic_string)).get(context, this.arguments.getArguments(context));

                if ((notification_level & Notifier.CHECKPOINT) != 0) { NotificationContext.sendCheckpoint(context, "DynamicString: '%(dynamic)' ==> '%(result)'", "dynamic", dynamic_string, "result", result); }
            }

            return new TestResult_Success(context);
            
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
    }

    // -- older static stuff --

    public static void DSTest(CallContext context, String string) {
        System.err.println("Dynamic string: " + string);
        System.err.println("Result        : " + (new DynamicString(context, string)).get(context));
    }

    public static void DSTest(CallContext context, String string, Scope scope) {
        System.err.println("Dynamic string: " + string);
        System.err.println("Result        : " + (new DynamicString(context, string)).get(context, scope));
    }

    public static void DSTest(CallContext context, String string, Object... variables) {
        System.err.println("Dynamic string: " + string);
        System.err.println("Result        : " + (new DynamicString(context, string)).get(context, new Class_Scope(context, variables)));
    }

    public static void ETest(CallContext context, String string) {
        try {
            System.err.println("Expression  : " + string);
            Object result = (new Expression(context, string)).evaluate(context);
            System.err.println("ResultClass : " + result.getClass());
            System.err.println("Result      : " + result);
        } catch (EvaluationFailure ef) {
            System.err.println("Exception: " + ef);
        }
    }

    public static void ETest(CallContext context, String string, Scope scope) {
        try {
            System.err.println("Expression  : " + string);
            Object result = (new Expression(context, string)).evaluate(context, scope);
            System.err.println("ResultClass : " + result.getClass());
            System.err.println("Result      : " + result);
        } catch (EvaluationFailure ef) {
            System.err.println("Exception: " + ef);
        }
    }

    public static void ETest(CallContext context, String string, Object... arguments) {
        try {
            System.err.println("Expression  : " + string);
            Object result = (new Expression(context, string)).evaluate(context, arguments);
            System.err.println("ResultClass : " + result.getClass());
            System.err.println("Result      : " + result);
        } catch (EvaluationFailure ef) {
            System.err.println("Exception: " + ef);
        }
    }

    public static void PETest(CallContext context, String string) {
        ActivityHandler ah = ActivityHandler_Expression.create(context, string);
        if (ah.execute(context, null).isValid(context) == false) {
            ah.dumpExecution(context);
        } else {
            ah.dumpData(context);
        }
    }

    public static void TITest(CallContext context, String string) {
        TemplateInstance ti = null;
        try {
            ti = TemplateInstanceParser.parse(context, string);
        } catch (ParseException pe) {
            System.err.println(pe);
            return;
        }
        System.err.println("Template Instance Expression : " + string);
        System.err.println("Generic MODEL_TEMPLATE                  , deep, ucs  : " + ti.getExpressionString(context, GenericLevel.MODEL_TEMPLATE, true, true));
        System.err.println("Generic CODE_GENERATOR_TEMPLATE         , deep, ucs  : " + ti.getExpressionString(context, GenericLevel.CODE_GENERATOR_TEMPLATE, true, true));
        System.err.println("Generic IMPLEMENTATION_LANGUAGE_TEMPLATE, deep, ucs  : " + ti.getExpressionString(context, GenericLevel.IMPLEMENTATION_LANGUAGE_TEMPLATE, true, true));
        System.err.println("Generic NONE                            , deep, ucs  : " + ti.getExpressionString(context, GenericLevel.NONE, true, true));
        System.err.println("Generic MODEL_TEMPLATE                  , deep       : " + ti.getExpressionString(context, GenericLevel.MODEL_TEMPLATE, true));
        System.err.println("Generic CODE_GENERATOR_TEMPLATE         , deep       : " + ti.getExpressionString(context, GenericLevel.CODE_GENERATOR_TEMPLATE, true));
        System.err.println("Generic IMPLEMENTATION_LANGUAGE_TEMPLATE, deep       : " + ti.getExpressionString(context, GenericLevel.IMPLEMENTATION_LANGUAGE_TEMPLATE, true));
        System.err.println("Generic NONE                            , deep       : " + ti.getExpressionString(context, GenericLevel.NONE, true));
        System.err.println("Generic MODEL_TEMPLATE                  , ucs        : " + ti.getExpressionString(context, GenericLevel.MODEL_TEMPLATE, false, true));
        System.err.println("Generic CODE_GENERATOR_TEMPLATE         , ucs        : " + ti.getExpressionString(context, GenericLevel.CODE_GENERATOR_TEMPLATE, false, true));
        System.err.println("Generic IMPLEMENTATION_LANGUAGE_TEMPLATE, ucs        : " + ti.getExpressionString(context, GenericLevel.IMPLEMENTATION_LANGUAGE_TEMPLATE, false, true));
        System.err.println("Generic NONE                            , ucs        : " + ti.getExpressionString(context, GenericLevel.NONE, false, true));
    }

    public static void main(String[] args) {
        Context context = com.sphenon.basics.context.classes.RootContext.getRootContext ();

        ExpressionPackageInitialiser.initialise(context);

        DSTest(context, "static:Hallo");

        DSTest(context, "s:Hallo, nochmal");

        DSTest(context, "Hallo Welt!");

        DSTest(context, "test1:--aaa--bbb--");

        DSTest(context, "test3:--VIELEA--");

        DSTest(context, ":static, without explicit processor, but with : (colon)");

        DSTest(context, "s:escaped: beginning");

        DSTest(context, "test4:semicolon and equal");

        DSTest(context, "test5:; and =");

        DSTest(context, "p: Hallo, m${x} Fre${y}", "x", "ein", "y", "und");

        DSTest(context, "p(hans=willy,walter=fritz): ${hans} ${walter} ${susi}", "susi", "suess");

        DSTest(context, "p,p: %24%7B${p:%24%7Bwalter%7D}%7D", "walter", "susi", "susi", "suess");

        DSTest(context, "p,process:p(hans=${willy}): %24%7Bhans%7D", "willy", "susi");

        DSTest(context, "regexp:/hxllo/x/a/");

        ETest(context, "test6:hallo");

        ETest(context, "test8:hallo");

        java.util.Map a = new java.util.Hashtable();
        a.put("eins", 1);
        a.put("zwei", 2);
        a.put("drei", 3);
        java.util.Map b = new java.util.Hashtable();
        b.put("eins", 4);
        b.put("zwei", 5);
        b.put("drei", 6);
        java.util.Map c = new java.util.Hashtable();
        c.put("eins", 7);
        c.put("zwei", 8);
        c.put("drei", 9);
        java.util.List d = new java.util.ArrayList();
        d.add(a);
        d.add(b);
        d.add(c);

        // from tests in JS console:
        //
        // a = new java.util.Hashtable(); a.put("eins", 1); a.put("zwei", 2); a.put("drei", 3); b = new java.util.Hashtable(); b.put("eins", 4); b.put("zwei", 5); b.put("drei", 6); c = new java.util.Hashtable(); c.put("eins", 7); c.put("zwei", 8); c.put("drei", 9); d = new java.util.ArrayList(); d.add(a); d.add(b); d.add(c);
        // Expression.evaluate(context, "❎:‖[d]'//Ⓟ/0/eins'", "unicode", "d", d)
        // Expression.evaluate(context, "unicode:‖[d]'//Ⓟ/0/eins'", null, "d", d)
        // Expression.evaluate(context, "unicode,evaluate:‖[d]'//Ⓟ/0/eins'", null, "d", d)
        // oi = Expression.evaluate(context, "unicode,evaluate:❎:ⓢ:d❌‖'//Ⓟ/eins'", null, "d", d)
        // oi = Expression.evaluate(context, "jsppuc:⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/eins'⟧", null, "d", d)
        // i = oi.iterator()
        // i.next()
        // Expression.evaluate(context, 'jsppuc:⟦["e",d]ⓢ:e⟧', null, "d", d)
        // Expression.evaluate(context, "jsppuc:∑(d)", null, "d", d)
        // Expression.evaluate(context, "jsppuc:∑(⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/eins'⟧)", null, "d", d)
        // Expression.evaluate(context, "jsppuc:⌗(⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/eins'⟧)", null, "d", d)

        ETest(context, "unicode:❎:‖[d]'//Ⓟ/0/eins'", "d", d);

        ETest(context, "unicode:‖[d]'//Ⓟ/0/eins'", "d", d);

        // these require more packages initialised...

        // ETest(context, "unicode,evaluate:‖[d]'//Ⓟ/0/eins'", "d", d);

        // ETest(context, "jsppuc:⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/eins'⟧", "d", d);
        // // i = oi.iterator();
        // // i.next();

        // ETest(context, "jsppuc:⟦[\"e\",d]ⓢ:e⟧", "d", d);

        // ETest(context, "jsppuc:∑(d)", "d", d);

        // ETest(context, "jsppuc:∑(⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/eins'⟧)", "d", d);

        // ETest(context, "jsppuc:∑(⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/zwei'⟧)", "d", d);

        // ETest(context, "jsppuc:∑(⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/drei'⟧)", "d", d);

        // ETest(context, "jsppuc:⌗(⟦['e',d]❎:ⓢ:e❌‖'//Ⓟ/eins'⟧)", "d", d);

        TITest(context, "Aaa");
        TITest(context, "Aaa<=Bbb=>");
        TITest(context, "Aaa<=Bbb,Ccc=>");
        TITest(context, "Aaa<=Bbb,Ccc,Ddd=>");
        TITest(context, "Aaa<-Bbb->");
        TITest(context, "Aaa<-Bbb,Ccc->");
        TITest(context, "Aaa<-Bbb,Ccc,Ddd->");
        TITest(context, "Aaa<Bbb>");
        TITest(context, "Aaa<Bbb,Ccc>");
        TITest(context, "Aaa<Bbb,Ccc,Ddd>");
        TITest(context, "Aaa<=Bbb=><-Eee-><Hhh>");
        TITest(context, "Aaa<=Bbb,Ccc=><-Eee,Fff-><Hhh,Iii>");
        TITest(context, "Aaa<=Bbb,Ccc,Ddd=><-Eee,Fff,Ggg-><Hhh,Iii,Jjj>");
        TITest(context, "Aaa");
        TITest(context, "Aaa<Ddd>");
        TITest(context, "Aaa<-Ccc->");
        TITest(context, "Aaa<-Ccc-><Ddd>");
        TITest(context, "Aaa<=Bbb=>");
        TITest(context, "Aaa<=Bbb=><Ddd>");
        TITest(context, "Aaa<=Bbb=><-Ccc->");
        TITest(context, "Aaa<=Bbb=><-Ccc-><Ddd>");
        TITest(context, "Aaa<=Bbb<=Ccc=>=>");
        TITest(context, "Aaa<=Bbb<=Ccc,Ddd=>=>");
        TITest(context, "Aaa<=Bbb<=Ccc=>,Eee<=Fff=>=>");
        TITest(context, "Aaa<=Bbb<=Ccc,Ddd=>,Eee<=Fff,Ggg=>=>");
        TITest(context, "Aaa<-Bbb<-Ccc->->");
        TITest(context, "Aaa<-Bbb<-Ccc,Ddd->->");
        TITest(context, "Aaa<-Bbb<-Ccc->,Eee<-Fff->->");
        TITest(context, "Aaa<-Bbb<-Ccc,Ddd->,Eee<-Fff,Ggg->->");
        TITest(context, "Aaa<Bbb<Ccc>>");
        TITest(context, "Aaa<Bbb<Ccc,Ddd>>");
        TITest(context, "Aaa<Bbb<Ccc>,Eee<Fff>>");
        TITest(context, "Aaa<Bbb<Ccc,Ddd>,Eee<Fff,Ggg>>");
        TITest(context, "Aaa<=Bbb<=Ccc=>,Eee<=Fff=>=><-Hhh<-Iii->,Kkk<-Lll->-><Nnn<Ooo>,Qqq<Rrr>>");
        TITest(context, "Aaa<=Bbb=><-Hhh-><Nnn>");
        TITest(context, "Aaa<=Bbb<=Ccc=>=><-Hhh<-Iii->-><Nnn<Ooo>>");
        TITest(context, "Aaa<=Bbb<=Ccc,Ddd=>,Eee<=Fff,Ggg=>=><-Hhh<-Iii,Jjj->,Kkk<-Lll,Mmm->-><Nnn<Ooo,Ppp>,Qqq<Rrr,Sss>>");
        TITest(context, "Aaa<Bbb<-Ccc<=Ddd=>->>");
        TITest(context, "Aaa<Bbb<-Ccc<=Ddd,Eee=>,Fff<=Ggg,Hhh=>->,Iii<-Jjj<=Kkk,Lll=>,Mmm<=Nnn,Ooo=>->>");

        PETest(context, "s:hallo");
        PETest(context, "s:hallo: `s:welt`");
        PETest(context, "pgm,s:hallo: `s:welt`");

        long times = 100000;
        String string = "s:Hallo";
        try {
            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
            evalOldVersion(context, times, string);
            evalNewVersion(context, times, string);
        } catch (EvaluationFailure ef) {
            System.err.println("Exception: " + ef);
        }


    }

    static protected void evalOldVersion(CallContext context, long times, String string) throws EvaluationFailure {
        System.err.println("Evaluating (old version, " + times + " times) : " + string);

        Class_ExpressionSource.duration = 0;

        {   long start = System.currentTimeMillis();
            Object result = null;
            for (long i=0; i<times; i++) {
                result = (new Expression(context, string)).evaluate(context);
            }
            long stop = System.currentTimeMillis();
            System.err.println("Result      : " + result + " " + (stop - start) + " ms");
            System.err.println("Parsing     : " + Class_ExpressionSource.duration + " ms");
        }
    }
        
    static protected void evalNewVersion(CallContext context, long times, String string) throws EvaluationFailure {
        System.err.println("Evaluating (new version, " + times + " times) : " + string);

        Class_ExpressionSource.duration = 0;

        {   long start = System.currentTimeMillis();
            Object result = null;
            for (long i=0; i<times; i++) {
                ActivityClass ac = (new Expression(context, string)).parse(context);
                Activity a = ac.instantiate(context, null);
                Execution e = a.execute(context);
                ActivityData ad = a.getData(context);
                result = ad.getSlots(context).get(0).getValue(context);
            }
            long stop = System.currentTimeMillis();
            System.err.println("Result      : " + result + " " + (stop - start) + " ms");
            System.err.println("Parsing     : " + Class_ExpressionSource.duration + " ms");
        }
    }
}
