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

public class QEBoundary extends QueryExpression {

    public QEBoundary(CallContext context, Long quantity, boolean cardinality_based) {
        super(context);
        this.quantity = quantity;
        this.cardinality_based = cardinality_based;
    }

    protected Long quantity;

    public Long getQuantity (CallContext context) {
        return this.quantity;
    }

    public void setQuantity (CallContext context, Long quantity) {
        this.quantity = quantity;
    }

    // 'cardinality' based, in contrast to 'zero' based, i.e. the size of the set
    // see specifically rules for 'ForAll' and 'Exists' in QueryExpressionParser.jj

    protected boolean cardinality_based;

    public boolean getCardinalityBased (CallContext context) {
        return this.cardinality_based;
    }

    public void setCardinalityBased (CallContext context, boolean cardinality_based) {
        this.cardinality_based = cardinality_based;
    }

    public boolean isEqualTo(CallContext context, QEBoundary other) {
        return (    (    (this.getQuantity(context) == null && other.getQuantity(context) == null)
                      || (    this.getQuantity(context) != null && other.getQuantity(context) != null
                           && this.getQuantity(context).equals(other.getQuantity(context))
                         )
                    )
                 && (this.getCardinalityBased(context) ==  other.getCardinalityBased(context))
               );
    }

    protected void toASTString(CallContext context, StringBuffer buffer, String indent) {
        this.toLogicalExpressionString(context, buffer);
    }

    protected void toLogicalExpressionString(CallContext context, StringBuffer buffer) {
        if (this.quantity == null) {
            buffer.append("*");
        } else {
            if (this.cardinality_based) {
                buffer.append("#");
                if (this.quantity > 0) {
                    buffer.append("+");
                }
                if (this.quantity != 0) {
                    buffer.append(this.quantity);
                }
            } else {
                buffer.append(this.quantity);
            }
        }
    }
}
