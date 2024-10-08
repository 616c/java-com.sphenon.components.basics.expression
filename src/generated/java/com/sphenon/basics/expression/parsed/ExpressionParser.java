/* Generated By:JavaCC: Do not edit this line. ExpressionParser.java */
package com.sphenon.basics.expression.parsed;

import com.sphenon.basics.context.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class ExpressionParser implements ExpressionParserConstants {

  static protected ExpressionParser parser;

  static protected ExpressionParser getParser(CallContext context, String string) throws ParseException {
      if (parser == null) {
          parser = new ExpressionParser(context, string);
      } else {
          parser.ReInit(context, string);
      }
      return parser;
  }

  // used in StateComplexCondition for state expressions
  static synchronized public Expression parse(CallContext context, String string) throws ParseException {
      return getParser(context, string).Expression(context);
  }

  // used in Shell for function invocations
  static synchronized public Expression parseArgumentList(CallContext context, String string) throws ParseException {
      return getParser(context, string).ArgumentListEOF(context);
  }

  // used in Shell for function definitions
  static synchronized public Map parseNamedExpressions(CallContext context, String string) throws ParseException {
      return getParser(context, string).NamedExpressionListEOF(context);
  }

  public ExpressionParser (CallContext context, String expression) {
      this(new BufferedReader(new StringReader(expression)));
  }

  public void ReInit (CallContext context, String expression) {
      ReInit(new BufferedReader(new StringReader(expression)));
  }

  public String getPosition(CallContext context) {
      return   "[line "    + jj_input_stream.getBeginLine()
                           + (jj_input_stream.getBeginLine() != jj_input_stream.getEndLine() ? ("-" + jj_input_stream.getEndLine()) : "")
             + ", column " + jj_input_stream.getBeginColumn()
                           + (jj_input_stream.getBeginColumn() != jj_input_stream.getEndColumn() ? ("-" + jj_input_stream.getEndColumn()) : "")
             + "]";
  }

  final public String DottedIdentifier() throws ParseException {
                              Token token; Token sep = null; String result; String postfix = null;
    token = jj_consume_token(IDENTIFIER);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DOT:
      sep = jj_consume_token(DOT);
      postfix = DottedIdentifier();
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
                                                                    result = token.image + (postfix == null ? "" : (sep.image + postfix));
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public void ArrayOrType(StringBuilder sb) throws ParseException {
                                       String result;
    result = DottedIdentifier();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
                                                sb.append(result);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPBRK:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_1;
      }
      jj_consume_token(OPBRK);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[3] = jj_gen;
        ;
      }
      jj_consume_token(CLBRK);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[4] = jj_gen;
        ;
      }
                                                sb.append("[]");
    }
  }

  final public void TemplateInstanceArguments(StringBuilder sb) throws ParseException {
    TemplateInstance(sb);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_2;
      }
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[6] = jj_gen;
        ;
      }
                             sb.append(',');
      TemplateInstance(sb);
    }
  }

  final public StringBuilder TemplateInstance(StringBuilder sb) throws ParseException {
                                                      String arrayortype;
    if (sb == null) { sb = new StringBuilder(); }
    ArrayOrType(sb);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LT:
      jj_consume_token(LT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[7] = jj_gen;
        ;
      }
                                                  sb.append('<');
      TemplateInstanceArguments(sb);
      jj_consume_token(GT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[8] = jj_gen;
        ;
      }
                                                  sb.append('>');
      break;
    default:
      jj_la1[9] = jj_gen;
      ;
    }
    {if (true) return sb;}
    throw new Error("Missing return statement in function");
  }

  final public String TypeName() throws ParseException {
                      StringBuilder typename;
    jj_consume_token(LT);
    jj_consume_token(LT);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[10] = jj_gen;
      ;
    }
    typename = TemplateInstance(null);
    jj_consume_token(GT);
    jj_consume_token(GT);
    {if (true) return typename.toString();}
    throw new Error("Missing return statement in function");
  }

  final public Expression Terminal(CallContext context) throws ParseException {
                                             Expression c1; Token token; String result;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
      result = DottedIdentifier();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[11] = jj_gen;
        ;
      }
                                               c1 = new Name(context, result);
      break;
    case LT:
      result = TypeName();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
                                       c1 = new TypeName(context, result);
      break;
    case STRING_LITERAL_1:
      token = jj_consume_token(STRING_LITERAL_1);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[13] = jj_gen;
        ;
      }
                                              c1 = new Literal(context, token.image.substring(1,token.image.length()-1).replaceAll("\u005c\u005c\u005c\u005c\u005c"", "\u005c"").replaceAll("\u005c\u005cn","\u005cn").replaceAll("\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c","\u005c\u005c\u005c\u005c"));
      break;
    case STRING_LITERAL_2:
      token = jj_consume_token(STRING_LITERAL_2);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[14] = jj_gen;
        ;
      }
                                              c1 = new Literal(context, token.image.substring(1,token.image.length()-1).replaceAll("\u005c\u005c\u005c\u005c'", "'").replaceAll("\u005c\u005cn","\u005cn").replaceAll("\u005c\u005c\u005c\u005c\u005c\u005c\u005c\u005c","\u005c\u005c\u005c\u005c"));
      break;
    case NUMBER:
      token = jj_consume_token(NUMBER);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[15] = jj_gen;
        ;
      }
                                    c1 = new Number(context, token.image);
      break;
    case BOOLEAN:
      token = jj_consume_token(BOOLEAN);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[16] = jj_gen;
        ;
      }
                                     c1 = new Bool(context, token.image);
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Identifier(CallContext context) throws ParseException {
                                               Expression c1; Token token;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
      token = jj_consume_token(IDENTIFIER);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
                                        c1 = new Identifier(context, token.image);
      break;
    case DOLLAR:
      jj_consume_token(DOLLAR);
      jj_consume_token(OPBRC);
      token = jj_consume_token(IDENTIFIER);
      jj_consume_token(CLBRC);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[19] = jj_gen;
        ;
      }
                                                                 c1 = new Identifier(context, token.image);
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Vector Elements(CallContext context, Vector elements) throws ParseException {
                                                          Expression c1;
    if (elements == null) { elements = new Vector(); }
    c1 = Or(context);
                       elements.add(c1);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMA:
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[21] = jj_gen;
        ;
      }
      Elements(context, elements);
      break;
    default:
      jj_la1[22] = jj_gen;
      ;
    }
    {if (true) return elements;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Array(CallContext context) throws ParseException {
                                          Expression c1 = null; Vector elements;
    jj_consume_token(OPBRK);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[23] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BOOLEAN:
    case IDENTIFIER:
    case STRING_LITERAL_1:
    case STRING_LITERAL_2:
    case NUMBER:
    case OP:
    case OPBRK:
    case LT:
    case TILDE:
    case NOT:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BOOLEAN:
      case IDENTIFIER:
      case STRING_LITERAL_1:
      case STRING_LITERAL_2:
      case NUMBER:
      case OP:
      case OPBRK:
      case LT:
      case NOT:
        elements = Elements(context, null);
                                               c1 = new Array(context, elements);
        break;
      case TILDE:
        jj_consume_token(TILDE);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WS:
          jj_consume_token(WS);
          break;
        default:
          jj_la1[24] = jj_gen;
          ;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case STRING_LITERAL_1:
          token = jj_consume_token(STRING_LITERAL_1);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case WS:
            jj_consume_token(WS);
            break;
          default:
            jj_la1[25] = jj_gen;
            ;
          }
                                                      c1 = new Array(context, token.image.substring(1,token.image.length()-1).replaceAll("\u005c\u005c\u005c"", "\u005c"").replaceAll("\u005c\u005cn","\u005cn").replaceAll("\u005c\u005c\u005c\u005c","\u005c\u005c"));
          break;
        case STRING_LITERAL_2:
          token = jj_consume_token(STRING_LITERAL_2);
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case WS:
            jj_consume_token(WS);
            break;
          default:
            jj_la1[26] = jj_gen;
            ;
          }
                                                      c1 = new Array(context, token.image.substring(1,token.image.length()-1).replaceAll("\u005c\u005c'", "'").replaceAll("\u005c\u005cn","\u005cn").replaceAll("\u005c\u005c\u005c\u005c","\u005c\u005c"));
          break;
        default:
          jj_la1[27] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[28] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[29] = jj_gen;
      ;
    }
    jj_consume_token(CLBRK);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[30] = jj_gen;
      ;
    }
    if (c1 == null) { c1 = new Array(context, new Vector()); }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Structure(CallContext context) throws ParseException {
                                              Expression c1; Expression c2;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BOOLEAN:
    case IDENTIFIER:
    case STRING_LITERAL_1:
    case STRING_LITERAL_2:
    case NUMBER:
    case LT:
      c1 = Terminal(context);
      break;
    case OPBRK:
      c1 = Array(context);
      break;
    default:
      jj_la1[31] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Block(CallContext context) throws ParseException {
                                          Expression c1; Expression c2;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BOOLEAN:
    case IDENTIFIER:
    case STRING_LITERAL_1:
    case STRING_LITERAL_2:
    case NUMBER:
    case OPBRK:
    case LT:
      c1 = Structure(context);
      break;
    case OP:
      jj_consume_token(OP);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[32] = jj_gen;
        ;
      }
      c1 = Or(context);
      jj_consume_token(CL);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[33] = jj_gen;
        ;
      }
      break;
    default:
      jj_la1[34] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Not(CallContext context) throws ParseException {
                                        Expression c1;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[35] = jj_gen;
        ;
      }
      c1 = Block(context);
                                               c1 = new Not(context, c1);
      break;
    case BOOLEAN:
    case IDENTIFIER:
    case STRING_LITERAL_1:
    case STRING_LITERAL_2:
    case NUMBER:
    case OP:
    case OPBRK:
    case LT:
      c1 = Block(context);
      break;
    default:
      jj_la1[36] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Comparison(CallContext context) throws ParseException {
                                               Expression c1; Expression c2; int lower_limit = -1; int upper_limit = -1;
    c1 = Not(context);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPQ:
      jj_consume_token(OPQ);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
        token = jj_consume_token(NUMBER);
                           lower_limit = Integer.parseInt(token.image);
        break;
      case STAR:
        jj_consume_token(STAR);
                           lower_limit = -1;
        break;
      case PLUS:
        jj_consume_token(PLUS);
                           lower_limit = -2;
        break;
      default:
        jj_la1[37] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ELL:
        jj_consume_token(ELL);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NUMBER:
          token = jj_consume_token(NUMBER);
                             upper_limit = Integer.parseInt(token.image);
          break;
        case STAR:
          jj_consume_token(STAR);
                             upper_limit = -1;
          break;
        case PLUS:
          jj_consume_token(PLUS);
                             upper_limit = -2;
          break;
        default:
          jj_la1[38] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[39] = jj_gen;
        ;
      }
      jj_consume_token(CLQ);
      break;
    default:
      jj_la1[40] = jj_gen;
      ;
    }
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQ:
      case NEQ:
      case RE:
      case NRE:
      case ISA:
      case NISA:
        ;
        break;
      default:
        jj_la1[41] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQ:
        jj_consume_token(EQ);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WS:
          jj_consume_token(WS);
          break;
        default:
          jj_la1[42] = jj_gen;
          ;
        }
        c2 = Not(context);
                                                c1 = new Equal(context, c1, c2, lower_limit, upper_limit);
        break;
      case NEQ:
        jj_consume_token(NEQ);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WS:
          jj_consume_token(WS);
          break;
        default:
          jj_la1[43] = jj_gen;
          ;
        }
        c2 = Not(context);
                                                c1 = new NotEqual(context, c1, c2, lower_limit, upper_limit);
        break;
      case RE:
        jj_consume_token(RE);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WS:
          jj_consume_token(WS);
          break;
        default:
          jj_la1[44] = jj_gen;
          ;
        }
        c2 = Not(context);
                                                c1 = new REMatch(context, c1, c2, lower_limit, upper_limit);
        break;
      case NRE:
        jj_consume_token(NRE);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WS:
          jj_consume_token(WS);
          break;
        default:
          jj_la1[45] = jj_gen;
          ;
        }
        c2 = Not(context);
                                                c1 = new NotREMatch(context, c1, c2, lower_limit, upper_limit);
        break;
      case ISA:
        jj_consume_token(ISA);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WS:
          jj_consume_token(WS);
          break;
        default:
          jj_la1[46] = jj_gen;
          ;
        }
        c2 = Not(context);
                                                c1 = new IsA(context, c1, c2, lower_limit, upper_limit);
        break;
      case NISA:
        jj_consume_token(NISA);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case WS:
          jj_consume_token(WS);
          break;
        default:
          jj_la1[47] = jj_gen;
          ;
        }
        c2 = Not(context);
                                                c1 = new NotIsA(context, c1, c2, lower_limit, upper_limit);
        break;
      default:
        jj_la1[48] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression And(CallContext context) throws ParseException {
                                        Expression c1; Expression c2;
    c1 = Comparison(context);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[49] = jj_gen;
        break label_4;
      }
      jj_consume_token(AND);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[50] = jj_gen;
        ;
      }
      c2 = Comparison(context);
                                                  c1 = new AND(context, c1, c2);
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Or(CallContext context) throws ParseException {
                                       Expression c1; Expression c2;
    c1 = And(context);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[51] = jj_gen;
        break label_5;
      }
      jj_consume_token(OR);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[52] = jj_gen;
        ;
      }
      c2 = And(context);
                                          c1 = new OR(context, c1, c2);
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Expression(CallContext context) throws ParseException {
                                               Expression c1;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[53] = jj_gen;
      ;
    }
    c1 = Or(context);
    jj_consume_token(0);
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression Argument(CallContext context) throws ParseException {
                                             Expression c1; Expression c2;
    c1 = Identifier(context);
    jj_consume_token(EQUAL);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[54] = jj_gen;
      ;
    }
    c2 = Or(context);
                                                                   c1 = new Argument(context, c1, c2);
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public void Arguments(CallContext context, Vector arguments) throws ParseException {
                                                          Expression c1;
    c1 = Argument(context);
                             arguments.add(c1);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMA:
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[55] = jj_gen;
        ;
      }
      Arguments(context, arguments);
      break;
    default:
      jj_la1[56] = jj_gen;
      ;
    }
  }

  final public Expression ArgumentList(CallContext context) throws ParseException {
                                                 Expression c1;
    Vector arguments = new Vector();
    c1 = new ArgumentList(context, arguments);
    jj_consume_token(OP);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[57] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
    case DOLLAR:
      Arguments(context, arguments);
      break;
    default:
      jj_la1[58] = jj_gen;
      ;
    }
    jj_consume_token(CL);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[59] = jj_gen;
      ;
    }
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public Expression ArgumentListEOF(CallContext context) throws ParseException {
                                                    Expression c1;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[60] = jj_gen;
      ;
    }
    c1 = ArgumentList(context);
    jj_consume_token(0);
    {if (true) return c1;}
    throw new Error("Missing return statement in function");
  }

  final public void NamedExpressions(CallContext context, Map map) throws ParseException {
                                                        Token token; Expression c1; Expression c2;
    token = jj_consume_token(IDENTIFIER);
    jj_consume_token(COLON);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[61] = jj_gen;
      ;
    }
    c2 = Or(context);
                                                             map.put(token.image, c2);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMA:
      jj_consume_token(COMMA);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WS:
        jj_consume_token(WS);
        break;
      default:
        jj_la1[62] = jj_gen;
        ;
      }
      NamedExpressions(context, map);
      break;
    default:
      jj_la1[63] = jj_gen;
      ;
    }
  }

  final public Map NamedExpressionList(CallContext context) throws ParseException {
                                                 Map map;
    map = new HashMap();
    jj_consume_token(OP);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[64] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
      NamedExpressions(context, map);
      break;
    default:
      jj_la1[65] = jj_gen;
      ;
    }
    jj_consume_token(CL);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[66] = jj_gen;
      ;
    }
    {if (true) return map;}
    throw new Error("Missing return statement in function");
  }

  final public Map NamedExpressionListEOF(CallContext context) throws ParseException {
                                                    Map map;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WS:
      jj_consume_token(WS);
      break;
    default:
      jj_la1[67] = jj_gen;
      ;
    }
    map = NamedExpressionList(context);
    jj_consume_token(0);
    {if (true) return map;}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public ExpressionParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[68];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x80000000,0x0,0x2000,0x0,0x0,0x40000000,0x0,0x0,0x0,0x8000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x803e,0x0,0x0,0x404,0x0,0x40000000,0x0,0x0,0x0,0x0,0x18,0xa13e,0xa13e,0x0,0xa03e,0x0,0x0,0xa13e,0x0,0xa13e,0x20,0x20,0x200000,0x80000,0xfc00000,0x0,0x0,0x0,0x0,0x0,0x0,0xfc00000,0x40,0x0,0x80,0x0,0x0,0x0,0x0,0x40000000,0x0,0x404,0x0,0x0,0x0,0x0,0x40000000,0x0,0x4,0x0,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x10,0x0,0x10,0x10,0x0,0x10,0x10,0x10,0x0,0x10,0x10,0x10,0x10,0x10,0x10,0x10,0x0,0x10,0x10,0x0,0x10,0x0,0x10,0x10,0x10,0x10,0x0,0xc,0xc,0x10,0x0,0x10,0x10,0x0,0x10,0x8,0x3,0x3,0x0,0x0,0x0,0x10,0x10,0x10,0x10,0x10,0x10,0x0,0x0,0x10,0x0,0x10,0x10,0x10,0x10,0x0,0x10,0x0,0x10,0x10,0x10,0x10,0x0,0x10,0x0,0x10,0x10,};
   }

  /** Constructor with InputStream. */
  public ExpressionParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ExpressionParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 68; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 68; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ExpressionParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 68; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 68; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ExpressionParser(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 68; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 68; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[38];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 68; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 38; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
