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

public class CMatcher {

    public CMatcher(EMatcher exception_matcher) {
        this.sequence_matcher       = null;
        this.minimum_sequence_depth = 0;
        this.maximum_sequence_depth = 0;
        this.exception_matcher      = exception_matcher;
        this.minimum_childs         = 1;
        this.maximum_childs         = -1;
        this.child_matchers         = null;
    }

    public CMatcher(EMatcher exception_matcher, int minimum_childs, int maximum_childs) {
        this.sequence_matcher       = null;
        this.minimum_sequence_depth = 0;
        this.maximum_sequence_depth = 0;
        this.exception_matcher      = exception_matcher;
        this.minimum_childs         = minimum_childs;
        this.maximum_childs         = maximum_childs;
        this.child_matchers         = null;
    }

    public CMatcher(EMatcher sequence_matcher, int minimum_sequence_depth, int maximum_sequence_depth, EMatcher exception_matcher) {
        this.sequence_matcher       = sequence_matcher;
        this.minimum_sequence_depth = minimum_sequence_depth;
        this.maximum_sequence_depth = maximum_sequence_depth;
        this.exception_matcher      = exception_matcher;
        this.minimum_childs         = 1;
        this.maximum_childs         = -1;
        this.child_matchers         = null;
    }

    public CMatcher(EMatcher sequence_matcher, int minimum_sequence_depth, int maximum_sequence_depth, EMatcher exception_matcher, int minimum_childs, int maximum_childs) {
        this.sequence_matcher       = sequence_matcher;
        this.minimum_sequence_depth = minimum_sequence_depth;
        this.maximum_sequence_depth = maximum_sequence_depth;
        this.exception_matcher      = exception_matcher;
        this.minimum_childs         = minimum_childs;
        this.maximum_childs         = maximum_childs;
        this.child_matchers         = null;
    }

    public CMatcher(CMatcher... child_matchers) {
        this.sequence_matcher       = null;
        this.minimum_sequence_depth = 0;
        this.maximum_sequence_depth = 0;
        this.exception_matcher      = null;
        this.minimum_childs         = 1;
        this.maximum_childs         = -1;
        this.child_matchers         = null;
        this.child_matchers = child_matchers;
    }

    public EMatcher sequence_matcher;
    public EMatcher exception_matcher;
    public int minimum_childs;
    public int maximum_childs;
    public int minimum_sequence_depth;
    public int maximum_sequence_depth;
    public CMatcher[] child_matchers;

    public EMatch[] matches(CallContext context, Throwable throwable) {
        return this.matches(context, throwable, 0);
    }

    public EMatch[] matches(CallContext context, Throwable throwable, int depth) {
        if (throwable == null) {
            return null;
        }

        if (    this.maximum_sequence_depth != -1
             && depth > this.maximum_sequence_depth
           ) {
            return null;
        }

        Throwable[] childs = null;
        if (throwable instanceof ExceptionWithMultipleCauses) {
            childs = ((ExceptionWithMultipleCauses) throwable).getCauses(context);
        } else if (throwable.getCause() != null) {
            childs = new Throwable[1];
            childs[0] = throwable.getCause();
        };

        if (childs == null || childs.length == 0) {
            if (this.minimum_childs > 0) {
                return null;
            } else {
                return new EMatch[0];
            }
        }

        if (    childs.length < this.minimum_childs
             || (    this.maximum_childs != -1
                  && childs.length > this.maximum_childs
                )
           ) {
            return null;
        }

        if (this.child_matchers != null) {
            EMatch[] child_matches;
            for (CMatcher child_matcher : this.child_matchers) {
                child_matches = child_matcher.matches(context, throwable);
                if (child_matches != null)  {
                    return child_matches;
                }
            }
            return null;
        }

        if (this.exception_matcher == null) {
            return null;
        }

        ArrayList<EMatch> child_matches = null;
        for (Throwable child : childs) {

            if (this.minimum_sequence_depth <= depth) {
                EMatch child_match = this.exception_matcher.matches(context, child);
                if (child_match != null) {
                    if (child_matches == null) {
                        child_matches = new ArrayList<EMatch>(1);
                    }
                    child_matches.add(child_match);
                    continue;
                }
            }

            if (this.sequence_matcher != null) {
                EMatch child_match = this.sequence_matcher.matches(context, child);
                if (child_match != null) {
                    EMatch[] recursive_matches = this.matches(context, child, depth + 1);
                    if (recursive_matches != null) {
                        child_match.child_matches = recursive_matches;
                        if (child_matches == null) {
                            child_matches = new ArrayList<EMatch>(1);
                        }
                        child_matches.add(child_match);
                        continue;
                    }
                }
            }
        }

        return child_matches == null ? null : child_matches.toArray(new EMatch[0]);
    }
}

