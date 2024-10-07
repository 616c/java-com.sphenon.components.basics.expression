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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.operations.*;

public class Patterns {

    static public String host_address_pattern =
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            "(?:"
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // ~~ IP
        // https://stackoverflow.com/questions/53497/regular-expression-that-matches-valid-ipv6-addresses
        // https://gist.github.com/syzdek/6086792
        // IPv6
          + "(?:"
          + "(?:[0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|"            // 1:2:3:4:5:6:7:8
          + "(?:[0-9a-fA-F]{1,4}:){1,7}:|"                           // 1::                              1:2:3:4:5:6:7::
          + "(?:[0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|"           // 1::8             1:2:3:4:5:6::8  1:2:3:4:5:6::8
          + "(?:[0-9a-fA-F]{1,4}:){1,5}(?::[0-9a-fA-F]{1,4}){1,2}|"  // 1::7:8           1:2:3:4:5::7:8  1:2:3:4:5::8
          + "(?:[0-9a-fA-F]{1,4}:){1,4}(?::[0-9a-fA-F]{1,4}){1,3}|"  // 1::6:7:8         1:2:3:4::6:7:8  1:2:3:4::8
          + "(?:[0-9a-fA-F]{1,4}:){1,3}(?::[0-9a-fA-F]{1,4}){1,4}|"  // 1::5:6:7:8       1:2:3::5:6:7:8  1:2:3::8
          + "(?:[0-9a-fA-F]{1,4}:){1,2}(?::[0-9a-fA-F]{1,4}){1,5}|"  // 1::4:5:6:7:8     1:2::4:5:6:7:8  1:2::8
          + "[0-9a-fA-F]{1,4}:(?:(?::[0-9a-fA-F]{1,4}){1,6})|"       // 1::3:4:5:6:7:8   1::3:4:5:6:7:8  1::8  
          + ":(?:(?::[0-9a-fA-F]{1,4}){1,7}|:)|"                     // ::2:3:4:5:6:7:8  ::2:3:4:5:6:7:8 ::8       ::     
          + "fe80:(?::[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|"       // fe80::7:8%eth0   fe80::7:8%1     (link-local IPv6 addresses with zone index)
          + "::(?:ffff(?::0{1,4}){0,1}:){0,1}"
          + "(?:(?:25[0-5]|(?:2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}"
          + "(?:25[0-5]|(?:2[0-4]|1{0,1}[0-9]){0,1}[0-9])|"          // ::255.255.255.255   ::ffff:255.255.255.255  ::ffff:0:255.255.255.255  (IPv4-mapped IPv6 addresses and IPv4-translated addresses)
          + "(?:[0-9a-fA-F]{1,4}:){1,4}:"
          + "(?:(?:25[0-5]|(?:2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}"
          + "(?:25[0-5]|(?:2[0-4]|1{0,1}[0-9]){0,1}[0-9])"           // 2001:db8:3:4::192.0.2.33  64:ff9b::192.0.2.33 (IPv4-Embedded IPv6 Address)
          + ")"
        // IPv4
          + "|(?:"
          + "(?:(?:25[0-5]|(?:2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(?:25[0-5]|(?:2[0-4]|1{0,1}[0-9]){0,1}[0-9])"
          + ")"
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // ~~ Domain Name (simplified)
        // https://stackoverflow.com/questions/10306690/what-is-a-regular-expression-which-will-match-a-valid-domain-name-without-a-subd/26987741#26987741
          + "|(?:"
          + "(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+[A-Za-z0-9][A-Za-z0-9-]{0,61}[A-Za-z0-9]"
          + ")"
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
          + ")";
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    static public String port_number_pattern     = "(?:[1-6]?[0-9]{1,4})";

    static public String identifier_an_pattern   = "(?:[A-Za-z0-9]+)";                          // alphanumeric
    static public String identifier_anu_pattern  = "(?:[A-Za-z0-9_]+)";                         // alphanumeric underscore
    static public String identifier_anud_pattern = "(?:[A-Za-z0-9_-]+)";                        // alphanumeric underscore dash

    static public String user_at_domain_pattern  =   "(?:"
                                                   + identifier_anud_pattern
                                                   + "@"
                                                   + host_address_pattern
                                                   + ")";

    static public String slash_path_core_pattern = identifier_anu_pattern + "(?:/" + identifier_anu_pattern + ")*";
    static public String slash_path_nn_pattern   =   "(?:"   + slash_path_core_pattern +   ")";   // slash separated path, no        leading and no        trailing slash
    static public String slash_path_no_pattern   =   "(?:"   + slash_path_core_pattern + "/?)";   // slash separated path, no        leading and optional  trailing slash
    static public String slash_path_nm_pattern   =   "(?:"   + slash_path_core_pattern +  "/)";   // slash separated path, no        leading and mandatory trailing slash
    static public String slash_path_on_pattern   =   "(?:/?" + slash_path_core_pattern +   ")";   // slash separated path, optional  leading and no        trailing slash
    static public String slash_path_oo_pattern   =   "(?:/?" + slash_path_core_pattern + "/?)";   // slash separated path, optional  leading and optional  trailing slash
    static public String slash_path_om_pattern   =   "(?:/?" + slash_path_core_pattern +  "/)";   // slash separated path, optional  leading and mandatory trailing slash
    static public String slash_path_mn_pattern   =   "(?:/"  + slash_path_core_pattern +   ")";   // slash separated path, mandatory leading and no        trailing slash
    static public String slash_path_mo_pattern   =   "(?:/"  + slash_path_core_pattern + "/?)";   // slash separated path, mandatory leading and optional  trailing slash
    static public String slash_path_mm_pattern   =   "(?:/"  + slash_path_core_pattern +  "/)";   // slash separated path, mandatory leading and mandatory trailing slash

    static public String dot_path_core_pattern   = identifier_anu_pattern + "(?:\\." + identifier_anu_pattern + ")*";
    static public String dot_path_nn_pattern     =   "(?:"     + dot_path_core_pattern +     ")"; // dot separated path, no        leading and no        trailing dot
    static public String dot_path_no_pattern     =   "(?:"     + dot_path_core_pattern + "\\.?)"; // dot separated path, no        leading and optional  trailing dot
    static public String dot_path_nm_pattern     =   "(?:"     + dot_path_core_pattern +  "\\.)"; // dot separated path, no        leading and mandatory trailing dot
    static public String dot_path_on_pattern     =   "(?:\\.?" + dot_path_core_pattern +     ")"; // dot separated path, optional  leading and no        trailing dot
    static public String dot_path_oo_pattern     =   "(?:\\.?" + dot_path_core_pattern + "\\.?)"; // dot separated path, optional  leading and optional  trailing dot
    static public String dot_path_om_pattern     =   "(?:\\.?" + dot_path_core_pattern +  "\\.)"; // dot separated path, optional  leading and mandatory trailing dot
    static public String dot_path_mn_pattern     =   "(?:\\."  + dot_path_core_pattern +     ")"; // dot separated path, mandatory leading and no        trailing dot
    static public String dot_path_mo_pattern     =   "(?:\\."  + dot_path_core_pattern + "\\.?)"; // dot separated path, mandatory leading and optional  trailing dot
    static public String dot_path_mm_pattern     =   "(?:\\."  + dot_path_core_pattern +  "\\.)"; // dot separated path, mandatory leading and mandatory trailing dot

    static public String path_pattern(String item_pattern, String separator, String end_options) {
        String path_core_pattern  = item_pattern + "(?:" + separator + item_pattern + ")*";
        switch (end_options) {
            case "nn": return "(?:"                   + path_core_pattern +              ")";  // separator separated path, no        leading and no        trailing separator
            case "no": return "(?:"                   + path_core_pattern + separator + "?)";  // separator separated path, no        leading and optional  trailing separator
            case "nm": return "(?:"                   + path_core_pattern + separator +  ")";  // separator separated path, no        leading and mandatory trailing separator
            case "on": return "(?:" + separator + "?" + path_core_pattern +              ")";  // separator separated path, optional  leading and no        trailing separator
            case "oo": return "(?:" + separator + "?" + path_core_pattern + separator + "?)";  // separator separated path, optional  leading and optional  trailing separator
            case "om": return "(?:" + separator + "?" + path_core_pattern + separator +  ")";  // separator separated path, optional  leading and mandatory trailing separator
            case "mn": return "(?:" + separator       + path_core_pattern +              ")";  // separator separated path, mandatory leading and no        trailing separator
            case "mo": return "(?:" + separator       + path_core_pattern + separator + "?)";  // separator separated path, mandatory leading and optional  trailing separator
            case "mm": return "(?:" + separator       + path_core_pattern + separator +  ")";  // separator separated path, mandatory leading and mandatory trailing separator
            default:
                CallContext context = RootContext.getFallbackCallContext();
                CustomaryContext.create((Context)context).throwPreConditionViolation(context, "Invalid end options: '%(options)', expected: '[nom]{2}'", "options", end_options);
                throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    // ================================================================================================

    static public RegularExpression host_address            = new RegularExpression("^" + host_address_pattern + "$");
    static public RegularExpression port_number             = new RegularExpression("^" + port_number_pattern + "$");

    static public RegularExpression identifier_an           = new RegularExpression("^" + identifier_an_pattern + "$");
    static public RegularExpression identifier_anu          = new RegularExpression("^" + identifier_anu_pattern + "$");
    static public RegularExpression identifier_anud         = new RegularExpression("^" + identifier_anud_pattern + "$");

    static public RegularExpression user_at_domain          = new RegularExpression("^" + user_at_domain_pattern + "$");

    static public RegularExpression slash_path_nn           = new RegularExpression("^" + slash_path_nn_pattern + "$");
    static public RegularExpression slash_path_no           = new RegularExpression("^" + slash_path_no_pattern + "$");
    static public RegularExpression slash_path_nm           = new RegularExpression("^" + slash_path_nm_pattern + "$");
    static public RegularExpression slash_path_on           = new RegularExpression("^" + slash_path_on_pattern + "$");
    static public RegularExpression slash_path_oo           = new RegularExpression("^" + slash_path_oo_pattern + "$");
    static public RegularExpression slash_path_om           = new RegularExpression("^" + slash_path_om_pattern + "$");
    static public RegularExpression slash_path_mn           = new RegularExpression("^" + slash_path_mn_pattern + "$");
    static public RegularExpression slash_path_mo           = new RegularExpression("^" + slash_path_mo_pattern + "$");
    static public RegularExpression slash_path_mm           = new RegularExpression("^" + slash_path_mm_pattern + "$");

    static public RegularExpression dot_path_nn             = new RegularExpression("^" + dot_path_nn_pattern + "$");
    static public RegularExpression dot_path_no             = new RegularExpression("^" + dot_path_no_pattern + "$");
    static public RegularExpression dot_path_nm             = new RegularExpression("^" + dot_path_nm_pattern + "$");
    static public RegularExpression dot_path_on             = new RegularExpression("^" + dot_path_on_pattern + "$");
    static public RegularExpression dot_path_oo             = new RegularExpression("^" + dot_path_oo_pattern + "$");
    static public RegularExpression dot_path_om             = new RegularExpression("^" + dot_path_om_pattern + "$");
    static public RegularExpression dot_path_mn             = new RegularExpression("^" + dot_path_mn_pattern + "$");
    static public RegularExpression dot_path_mo             = new RegularExpression("^" + dot_path_mo_pattern + "$");
    static public RegularExpression dot_path_mm             = new RegularExpression("^" + dot_path_mm_pattern + "$");

    static public RegularExpression path(String item_pattern, String separator, String end_options) {
        return new RegularExpression("^" + path_pattern(item_pattern, separator, end_options) + "$");
    }
}
