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

import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.returncodes.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class QEFilters {

    public QEFilters(CallContext context, QEMemberFilter parent) {
        this.parent = parent;
        if (parent != null) {
            parent.setFilters(context, this);
        }
    }

    protected QEMemberFilter parent;

    public QEMemberFilter getParent (CallContext context) {
        return this.parent;
    }

    public String getFilterPath(CallContext context) {
        return this.parent == null ? "" : this.parent.getFilterPath(context);
    }

    protected Map<String,Map<String,QEMemberFilter>> filters;

    public Map<String,Map<String,QEMemberFilter>> getFilters (CallContext context) {
        return this.filters;
    }

    public QEMemberFilter getMemberFilter (CallContext context, String member, String tag) throws InvalidQueryExpression {
        return getMemberFilter(context, member, tag, false);
    }

    public QEMemberFilter getMemberFilter (CallContext context, String member, String tag, boolean create) throws InvalidQueryExpression {
        if (this.filters == null) {
            if (create == false) { return null; }
            this.filters = new HashMap<String,Map<String,QEMemberFilter>>();
        }
        Map<String,QEMemberFilter> member_filters = this.filters.get(member);
        if (member_filters == null) {
            if (create == false) { return null; }
            member_filters = new HashMap<String,QEMemberFilter>();
            this.filters.put(member, member_filters);
        }
        QEMemberFilter member_filter = member_filters.get(tag);
        if (member_filter == null) {
            if (create == false) { return null; }
            member_filter = new QEMemberFilter(context, member, tag, this);
            member_filters.put(tag, member_filter);

            if (tag != null && tag.isEmpty() == false) {
                String defined_path = this.getPathByTag(context, tag);
                String current_path = member_filter.getFilterPath(context);
                if (defined_path != null) {
                    if (defined_path.equals(current_path) == false) {
                        InvalidQueryExpression.createAndThrow(context, "Unexpected: condition node with tag '%(tag)' is redefined with different path: '%(path1)' and '%(path2)'", "tag", tag, "path1", defined_path, "path2", current_path);
                        throw (InvalidQueryExpression) null;
                    }
                } else {
                    this.defineTag (context, tag, current_path);
                }
            }
        } else {
            // well: I guess, this should never be unequal
            String defined_path = member_filter.getFilterPath(context);
            String current_path = this.getFilterPath(context) + "." + member + "#" + tag;
            if (defined_path.equals(current_path) == false) {
                InvalidQueryExpression.createAndThrow(context, "Unexpected: condition node with tag '%(tag)' is redefined with different path: '%(path1)' and '%(path2)'", "tag", tag, "path1", defined_path, "path2", current_path);
                throw (InvalidQueryExpression) null;
            }
        }
        return member_filter;
    }

    public QEMemberFilter addMemberFilterCondition (CallContext context, QECondition condition) throws InvalidQueryExpression {
        String member = condition.getId(context);
        String tag = condition.getTag(context);
        QEMemberFilter member_filter = this.getMemberFilter(context, member, tag, true);

        member_filter.getConditions(context).add(condition);

        return member_filter;
    }

    protected Map<String,String> path_by_tag;

    public String getPathByTag (CallContext context, String tag) {
        if (this.parent != null) {
            return this.parent.getParent(context).getPathByTag (context, tag);
        }
        return this.path_by_tag == null ? null : this.path_by_tag.get(tag);
    }

    public void defineTag (CallContext context, String tag, String path) {
        if (this.parent != null) {
            this.parent.getParent(context).defineTag (context, tag, path);
            return;
        }
        if (this.path_by_tag == null) {
            this.path_by_tag = new HashMap<String,String>();
        }
        this.path_by_tag.put(tag, path);
    }

    protected long next_unique_id = 0;

    public String getNextUniqueId (CallContext context) {
        if (this.parent != null) {
            return this.parent.getParent(context).getNextUniqueId(context);
        }
        return "" + this.next_unique_id++;
    }

    public void toFilterTreeString(CallContext context, StringBuffer buffer, String indent) {
        buffer.append(indent).append("Path: ").append(getFilterPath(context)).append('\n');
        for (String member : filters.keySet()) {
            buffer.append(indent).append(" - ").append(member).append('\n');
            Map<String,QEMemberFilter> member_filters = filters.get(member);
            for (String tag : member_filters.keySet()) {
                QEMemberFilter member_filter = member_filters.get(tag);
                buffer.append(indent).append("   # ").append(tag).append(" / ").append(member_filter.getUniqueId(context)).append('\n');
                member_filter.toFilterTreeString(context, buffer, indent + "     ");
            }
        }
    }

    public interface MemberProcessor {
        public void processMember(CallContext context, QEMemberFilter member_filter, int index, int size) throws InvalidQueryExpression;
    }

    public void foreachMember(CallContext context, MemberProcessor member_processor) throws InvalidQueryExpression {
        for (String member : filters.keySet()) {
            Map<String,QEMemberFilter> member_filters = filters.get(member);
            int size = member_filters.size();
            int index = 0;
            for (String tag : member_filters.keySet()) {
                QEMemberFilter member_filter = member_filters.get(tag);
                member_processor.processMember(context, member_filter, index, size);
                index++;
            }
        }
    }
}
