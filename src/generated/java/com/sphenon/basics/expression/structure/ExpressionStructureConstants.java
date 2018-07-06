/* Generated By:JavaCC: Do not edit this line. ExpressionStructureConstants.java */

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
package com.sphenon.basics.expression.structure;

public interface ExpressionStructureConstants {

  int EOF = 0;
  int ARG = 1;
  int CMTB = 2;
  int CMTE = 3;
  int ARGB = 4;
  int ARGE = 5;
  int BACKSL = 6;
  int EBTICK = 7;
  int SLASHSTAR = 8;
  int STARSLASH = 9;
  int ANY = 10;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\"`\"",
    "\"/*\"",
    "\"*/\"",
    "\"`{\"",
    "\"}`\"",
    "\"\\\\\\\\\"",
    "\"\\\\`\"",
    "\"\\\\/*\"",
    "\"*\\\\/\"",
    "<ANY>",
  };

}