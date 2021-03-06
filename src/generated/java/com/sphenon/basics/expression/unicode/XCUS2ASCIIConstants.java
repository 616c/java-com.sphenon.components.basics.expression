/* Generated By:JavaCC: Do not edit this line. XCUS2ASCIIConstants.java */

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
package com.sphenon.basics.expression.unicode;

public interface XCUS2ASCIIConstants {

  int EOF = 0;
  int ESCAPE = 1;
  int LNCONT = 2;
  int LNCONTPLUS = 3;
  int CONT = 4;
  int CONTPLUS = 5;
  int LNINDT = 6;
  int SEPARATOR = 7;
  int UNICODE = 8;
  int WS = 9;
  int LN = 10;
  int SELECT = 11;
  int REQUIRE = 12;
  int SCRIPT = 13;
  int PREPROCESS = 14;
  int CMTBEGIN = 15;
  int CMTEND = 16;
  int EXPRBEGIN = 17;
  int EXPREND = 18;
  int OORL = 19;
  int BASH = 20;
  int SHELL = 21;
  int SCOPE = 22;
  int JAVASCRIPT = 23;
  int OPERATION = 24;
  int PROGRAM = 25;
  int PROCESS = 26;
  int PLACEHOLDER = 27;
  int MULTIITER = 28;
  int MULTITIMES = 29;
  int EQUALS = 30;
  int RECODING = 31;
  int CONFIG = 32;
  int OR = 33;
  int AND = 34;
  int ULT = 35;
  int UGT = 36;
  int UNULL = 37;
  int UTRUE = 38;
  int UFALSE = 39;
  int UQUOTE = 40;
  int TIME = 41;
  int FORMAT = 42;
  int SIZE = 43;
  int AVERAGE = 44;
  int PRODUCT = 45;
  int SUM = 46;
  int MINIMUM = 47;
  int MAXIMUM = 48;
  int EXPRB = 49;
  int EXECUTE = 50;
  int BEGINPROC = 51;
  int ENDPROC = 52;
  int BEGINBLK = 53;
  int ENDBLK = 54;
  int CMTB = 55;
  int CMTE = 56;
  int BTICK = 57;
  int BACKSL = 58;
  int NEWLINE = 59;
  int ANY = 60;
  int OOPBRK = 61;
  int OCLBRK = 62;
  int OLT = 63;
  int OGT = 64;
  int OID = 65;
  int OIDENTIFIER = 66;
  int ODELDQB = 67;
  int ODELSQB = 68;
  int ODELBCB = 69;
  int ODELPAB = 70;
  int ODELDQE = 71;
  int ODELSQE = 72;
  int ODELBCE = 73;
  int ODELPAE = 74;
  int OXPATH = 75;
  int OPROPERTY = 76;
  int OXMODEL = 77;
  int OFILE = 78;
  int OSQL = 79;
  int OANY = 80;
  int ELT = 81;
  int EGT = 82;
  int EOPBRK = 83;
  int ECLBRK = 84;
  int EXPRE = 85;
  int EQUOTE = 86;
  int ANYEXPR = 87;

  int DEFAULT = 0;
  int IN_OORL = 1;
  int IN_ODELDQ = 2;
  int IN_ODELSQ = 3;
  int IN_ODELBC = 4;
  int IN_ODELPA = 5;
  int IN_EXPR = 6;

  String[] tokenImage = {
    "<EOF>",
    "<ESCAPE>",
    "\"\\u2026\\n\"",
    "\"\\u2026+\\n\"",
    "\"\\u2026\"",
    "\"\\u2026+\"",
    "\"\\u22ee\"",
    "\"\\u25c8\"",
    "<UNICODE>",
    "<WS>",
    "\"\\n\"",
    "<SELECT>",
    "<REQUIRE>",
    "<SCRIPT>",
    "<PREPROCESS>",
    "\"\\u22b0\"",
    "\"\\u22b1\"",
    "\"\\u25c2\"",
    "\"\\u25b8\"",
    "\"\\u2016\"",
    "\"\\u24b7\"",
    "\"\\u24c8\"",
    "\"\\u24e2\"",
    "\"\\u24bf\"",
    "\"\\u24c4\"",
    "\"\\u24c5\"",
    "\"\\u24df\"",
    "\"\\u2386\"",
    "\"\\u274e\"",
    "\"\\u274c\"",
    "\"\\u2261\"",
    "\"\\u2318\"",
    "\"\\u26a1\"",
    "\"\\u2228\"",
    "\"\\u2227\"",
    "\"\\u227a\"",
    "\"\\u227b\"",
    "\"\\u2400\"",
    "\"\\u2714\"",
    "\"\\u2718\"",
    "\"\\u02ee\"",
    "\"\\u231a(\"",
    "\"\\u233e(\"",
    "\"\\u2317(\"",
    "\"\\u2300(\"",
    "\"\\u220f(\"",
    "\"\\u2211(\"",
    "\"\\u2913(\"",
    "\"\\u2912(\"",
    "\"\\u27e6\"",
    "\"\\u203c\"",
    "\"\\u228f\"",
    "\"\\u2290\"",
    "\"\\u2770\"",
    "\"\\u2771\"",
    "\"/*\"",
    "\"*/\"",
    "\"`\"",
    "\"\\\\\"",
    "\"\\u23ce\"",
    "<ANY>",
    "\"[\"",
    "\"]\"",
    "\"<\"",
    "\">\"",
    "<OID>",
    "<OIDENTIFIER>",
    "\"\\\"\"",
    "\"\\\'\"",
    "\"{\"",
    "\"\\u00a7\"",
    "\"\\\"\"",
    "\"\\\'\"",
    "\"}\"",
    "\"\\u00a7\"",
    "\"\\u24cd\"",
    "\"\\u24c5\"",
    "\"\\u2726\"",
    "\"\\u24bb\"",
    "\"\\u24c8\"",
    "<OANY>",
    "\"<\"",
    "\">\"",
    "\"[\"",
    "\"]\"",
    "\"\\u27e7\"",
    "\"\\u02ee\"",
    "<ANYEXPR>",
  };

}
