/* Generated By:JavaCC: Do not edit this line. MiniJavaParser.java */
import java.io.*;
import syntaxtree.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.io.PrintStream;

/**
 * Grammar to parse MiniJava language 
 * @author Christopher Teljstedt and Carl Eriksson 
 */
public class MiniJavaParser implements MiniJavaParserConstants {

public MiniJavaParser (String fileName){
        this(System.in);
    try { ReInit(new FileInputStream(new File(fileName))); }
    catch(Exception e) { e.printStackTrace(); }
}

public static void main(String args[]) {
        MiniJavaParser parser;
  SyntaxTreePrinter stp;
  Program program;
        if (args.length == 0) {
                System.out.println("MiniJavac 1.0:  Reading from standard input . . .");
                parser = new MiniJavaParser(System.in);
        } else if (args.length == 1) {
                System.out.println("MiniJavac 1.0:  Reading from file " + args[0] + " . . .");
                try {
                        parser = new MiniJavaParser(new java.io.FileInputStream(args[0]));
                } catch (java.io.FileNotFoundException e) {
                        System.out.println("MiniJavac 1.0: File " + args[0] + " not found.");
                        return;
                }
  } else if (args.length == 2){
    System.out.println("MiniJavac 1.0:  Reading from file " + args[1] + " . . .");
    try {
      parser = new MiniJavaParser(new java.io.FileInputStream(args[1]));
    } catch (java.io.FileNotFoundException e) {
      System.out.println("MiniJavac 1.0: File " + args[1] + " not found.");
      return;
    }
  }
        else {
                System.out.println("MiniJavac 1.0:  Usage is one of:");
                System.out.println("         java JavaParser < inputfile");
                System.out.println("OR");
                System.out.println("         java JavaParser inputfile");
                return;
        }
        try {
                program = parser.Program();
    stp = new SyntaxTreePrinter(System.out);
    stp.visit(program);

                System.out.println("MiniJavac 1.0: Java program parsed successfully.");
        } catch (ParseException e) {
                System.out.println(e.getMessage());
                System.out.println("MiniJavac 1.0: Encountered errors during parse.");
        }
}

/**********************************************
 * THE MINI JAVA LANGUAGE GRAMMAR STARTS HERE *
 **********************************************/


/*
 * Program structuring syntax follows.
 */

/* Top level production */
  static final public Program Program() throws ParseException {
    trace_call("Program");
    try {
  MainClass mc;
  ClassDeclList cdl;
      /* The first class must be the main class */
        mc = MainClass();
      /* Then zero or more other class declarations */
        cdl = ClassDeclList();
      jj_consume_token(0);
    {if (true) return new Program(mc, cdl);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Program");
    }
  }

  static final public ClassDeclList ClassDeclList() throws ParseException {
    trace_call("ClassDeclList");
    try {
  ClassDecl c;
  ClassDeclList cdl = new ClassDeclList();
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CLASS:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        c = ClassDecl();
                     cdl.addElement(c);
      }
    {if (true) return cdl;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ClassDeclList");
    }
  }

  static final public MainClass MainClass() throws ParseException {
    trace_call("MainClass");
    try {
  Token t;
  Identifier id1;
  Identifier id2;
  VarDecl v;
  VarDeclList vdl = new VarDeclList();
  Statement stmt;
  StatementList sl = new StatementList();
      jj_consume_token(CLASS);
      t = jj_consume_token(IDENTIFIER);
                     id1 = new Identifier(t.image);
      jj_consume_token(LBRACE);
      jj_consume_token(PUBLIC);
      jj_consume_token(STATIC);
      jj_consume_token(VOID);
      jj_consume_token(MAIN);
      jj_consume_token(LPAREN);
      jj_consume_token(STRING);
      jj_consume_token(LBRACKET);
      jj_consume_token(RBRACKET);
      t = jj_consume_token(IDENTIFIER);
                                                                                                 id2 = new Identifier(t.image);
      jj_consume_token(RPAREN);
      jj_consume_token(LBRACE);
      label_2:
      while (true) {
        if (jj_2_1(2147483647)) {
          ;
        } else {
          break label_2;
        }
        v = VarDecl();
                                                         vdl.addElement(v);
      }
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case IF:
        case WHILE:
        case SYSOUT_PRINTLN:
        case LBRACE:
        case IDENTIFIER:
          ;
          break;
        default:
          jj_la1[1] = jj_gen;
          break label_3;
        }
        stmt = Stmt();
                        sl.addElement(stmt);
      }
      jj_consume_token(RBRACE);
      jj_consume_token(RBRACE);
    {if (true) return new MainClass(id1, id2, vdl, sl);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("MainClass");
    }
  }

  static final public ClassDeclSimple ClassDecl() throws ParseException {
    trace_call("ClassDecl");
    try {
  Token t;
  Identifier id;
  VarDecl v;
  VarDeclList vl = new VarDeclList();
  MethodDecl m;
  MethodDeclList ml = new MethodDeclList();
      jj_consume_token(CLASS);
      t = jj_consume_token(IDENTIFIER);
                     id = new Identifier(t.image);
      jj_consume_token(LBRACE);
      label_4:
      while (true) {
        if (jj_2_2(2)) {
          ;
        } else {
          break label_4;
        }
        v = VarDecl();
                                    vl.addElement(v);
      }
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PUBLIC:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_5;
        }
        m = MethodDecl();
                           ml.addElement(m);
      }
      jj_consume_token(RBRACE);
    {if (true) return new ClassDeclSimple(id, vl, ml);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ClassDecl");
    }
  }

/* Variable declaration */
  static final public VarDecl VarDecl() throws ParseException {
    trace_call("VarDecl");
    try {
  Token t;
  Type type;
  Identifier id;
      type = Type();
      t = jj_consume_token(IDENTIFIER);
                                   id=new Identifier(t.image);
      jj_consume_token(SEMICOLON);
    {if (true) return new VarDecl(type, id);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("VarDecl");
    }
  }

  static final public MethodDecl MethodDecl() throws ParseException {
    trace_call("MethodDecl");
    try {
  Token t;
  Type type;
  Identifier id;
  FormalList fl;
  VarDecl v;
  VarDeclList vl = new VarDeclList();
  Statement s;
  StatementList sl = new StatementList();
  Exp e;
      jj_consume_token(PUBLIC);
      type = Type();
      t = jj_consume_token(IDENTIFIER);
                       id = new Identifier(t.image);
      fl = FormalList();
      jj_consume_token(LBRACE);
      label_6:
      while (true) {
        if (jj_2_3(2147483647)) {
          ;
        } else {
          break label_6;
        }
        v = VarDecl();
                                                       vl.addElement(v);
      }
      label_7:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case IF:
        case WHILE:
        case SYSOUT_PRINTLN:
        case LBRACE:
        case IDENTIFIER:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_7;
        }
        s = Stmt();
                   sl.addElement(s);
      }
      jj_consume_token(RETURN);
      e = Exp();
      jj_consume_token(SEMICOLON);
      jj_consume_token(RBRACE);
    {if (true) return new MethodDecl(type, id, fl, vl, sl, e);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("MethodDecl");
    }
  }

  static final public FormalList FormalList() throws ParseException {
    trace_call("FormalList");
    try {
  Token t;
  Type type;
  Identifier id;
  Formal f;
  FormalList fl = new FormalList();
      jj_consume_token(LPAREN);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BOOLEAN:
      case INT:
      case IDENTIFIER:
        type = Type();
        t = jj_consume_token(IDENTIFIER);
                                 id=new Identifier(t.image);
                  f = new Formal(type, id); fl.addElement(f);
        label_8:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case COMMA:
            ;
            break;
          default:
            jj_la1[4] = jj_gen;
            break label_8;
          }
          jj_consume_token(COMMA);
          f = FormalRest();
                                     fl.addElement(f);
        }
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      jj_consume_token(RPAREN);
    {if (true) return fl;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("FormalList");
    }
  }

  static final public Formal FormalRest() throws ParseException {
    trace_call("FormalRest");
    try {
  Token t;
  Type type;
  Identifier id;
      type = Type();
      t = jj_consume_token(IDENTIFIER);
                                 id=new Identifier(t.image);
    {if (true) return new Formal(type, id);}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("FormalRest");
    }
  }

/*
 * Type, name and expression syntax follows.
 */
  static final public Type Type() throws ParseException {
    trace_call("Type");
    try {
  Token t;
  Type type;
      if (jj_2_4(2)) {
        jj_consume_token(INT);
        jj_consume_token(LBRACKET);
        jj_consume_token(RBRACKET);
                                    type=new IntArrayType();
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case INT:
          jj_consume_token(INT);
              type=new IntegerType();
          break;
        case BOOLEAN:
          jj_consume_token(BOOLEAN);
                  type=new BooleanType();
          break;
        case IDENTIFIER:
          t = jj_consume_token(IDENTIFIER);
                       type=new IdentifierType(t.image);
          break;
        default:
          jj_la1[6] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    {if (true) return type;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Type");
    }
  }

  static final public int Op() throws ParseException {
    trace_call("Op");
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SC_AND:
        jj_consume_token(SC_AND);
             {if (true) return SC_AND;}
        break;
      case LT:
        jj_consume_token(LT);
         {if (true) return LT;}
        break;
      case PLUS:
        jj_consume_token(PLUS);
           {if (true) return PLUS;}
        break;
      case MINUS:
        jj_consume_token(MINUS);
            {if (true) return MINUS;}
        break;
      case STAR:
        jj_consume_token(STAR);
           {if (true) return STAR;}
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Op");
    }
  }

/*
 * Stmt syntax follows.
 */
  static final public Statement Stmt() throws ParseException {
    trace_call("Stmt");
    try {
  Token t;
  Exp e1;
  Exp e2;
  Identifier id;
  Statement rs;
  Statement s1;
  Statement s2;
  StatementList sl = new StatementList();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACE:
        jj_consume_token(LBRACE);
        label_9:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case IF:
          case WHILE:
          case SYSOUT_PRINTLN:
          case LBRACE:
          case IDENTIFIER:
            ;
            break;
          default:
            jj_la1[8] = jj_gen;
            break label_9;
          }
          s1 = Stmt();
                             sl.addElement(s1);
        }
        jj_consume_token(RBRACE);
        rs = new Block(sl);
        break;
      case IF:
        jj_consume_token(IF);
        jj_consume_token(LPAREN);
        e1 = Exp();
        jj_consume_token(RPAREN);
        s1 = Stmt();
        jj_consume_token(ELSE);
        s2 = Stmt();
        rs = new If(e1, s1, s2);
        break;
      case WHILE:
        jj_consume_token(WHILE);
        jj_consume_token(LPAREN);
        e1 = Exp();
        jj_consume_token(RPAREN);
        s1 = Stmt();
        rs = new While(e1, s1);
        break;
      case SYSOUT_PRINTLN:
        jj_consume_token(SYSOUT_PRINTLN);
        jj_consume_token(LPAREN);
        e1 = Exp();
        jj_consume_token(RPAREN);
        jj_consume_token(SEMICOLON);
        rs = new Print(e1);
        break;
      default:
        jj_la1[9] = jj_gen;
        if (jj_2_5(2)) {
          t = jj_consume_token(IDENTIFIER);
                         id = new Identifier(t.image);
          jj_consume_token(ASSIGN);
          e1 = Exp();
          jj_consume_token(SEMICOLON);
        rs = new Assign(id,e1);
        } else if (jj_2_6(2)) {
          t = jj_consume_token(IDENTIFIER);
                         id = new Identifier(t.image);
          jj_consume_token(LBRACKET);
          e1 = Exp();
          jj_consume_token(RBRACKET);
          jj_consume_token(ASSIGN);
          e2 = Exp();
          jj_consume_token(SEMICOLON);
        rs = new ArrayAssign(id,e1,e2);
        } else {
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    {if (true) return rs;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Stmt");
    }
  }

/*
 * Exp syntax follows.
 */
  static final public Exp Exp() throws ParseException {
    trace_call("Exp");
    try {
  Token t;
  Exp e;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER_LITERAL:
        t = jj_consume_token(INTEGER_LITERAL);
                               e = new IntegerLiteral(Integer.parseInt(t.image));
        break;
      case TRUE:
        jj_consume_token(TRUE);
                e = new True();
        break;
      case FALSE:
        jj_consume_token(FALSE);
                e = new False();
        break;
      case IDENTIFIER:
        t = jj_consume_token(IDENTIFIER);
                          e = new IdentifierExp(t.image);
        break;
      case THIS:
        jj_consume_token(THIS);
               e = new This();
        break;
      default:
        jj_la1[10] = jj_gen;
        if (jj_2_7(2)) {
          jj_consume_token(NEW);
          jj_consume_token(INT);
          jj_consume_token(LBRACKET);
          e = Exp();
                                         e = new NewArray(e);
          jj_consume_token(RBRACKET);
        } else if (jj_2_8(2)) {
          jj_consume_token(NEW);
          t = jj_consume_token(IDENTIFIER);
                                 e = new NewObject(new Identifier(t.image));
          jj_consume_token(LPAREN);
          jj_consume_token(RPAREN);
        } else {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case NOT:
            jj_consume_token(NOT);
            e = Exp();
                          e = new Not(e);
            break;
          case LPAREN:
            jj_consume_token(LPAREN);
            e = Exp();
            jj_consume_token(RPAREN);
            break;
          default:
            jj_la1[11] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
      }
      label_10:
      while (true) {
        if (jj_2_9(2)) {
          ;
        } else {
          break label_10;
        }
        e = ExpPrim(e);
      }
    {if (true) return e;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("Exp");
    }
  }

  static final public Exp ExpPrim(Exp e1) throws ParseException {
    trace_call("ExpPrim");
    try {
  Token t;
  Exp e2;
  int op;
  Identifier id;
  ExpList el;
      if (jj_2_10(2)) {
        op = Op();
        e2 = Exp();
      switch (op) {
        case SC_AND: {if (true) return new And(e1, e2);}
        case LT: {if (true) return new LessThan(e1, e2);}
        // case SC_OR: return new Or(e1, e2);
        // case GT: return new GreaterThan(e1, e2);
        // case LE: return new LessThanOrEqual(e1, e2);
        // case GE: return new GreaterThanOrEqual(e1, e2);
        // case EQ: return new Equal(e1, e2);
        // case NE: return new NotEqual(e1, e2);
        case PLUS: {if (true) return new Plus(e1, e2);}
        case MINUS: {if (true) return new Minus(e1, e2);}
        case STAR: {if (true) return new Times(e1, e2);}
      }
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LBRACKET:
          jj_consume_token(LBRACKET);
          e2 = Exp();
          jj_consume_token(RBRACKET);
      {if (true) return new ArrayLookup(e1, e2);}
          break;
        default:
          jj_la1[12] = jj_gen;
          if (jj_2_11(2)) {
            jj_consume_token(DOT);
            jj_consume_token(LENGTH);
      {if (true) return new ArrayLength(e1);}
          } else if (jj_2_12(2)) {
            jj_consume_token(DOT);
            t = jj_consume_token(IDENTIFIER);
                              id = new Identifier(t.image);
            jj_consume_token(LPAREN);
            el = ExpList();
            jj_consume_token(RPAREN);
      {if (true) return new Call(e1, id, el);}
          } else {
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
      }
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ExpPrim");
    }
  }

  static final public ExpList ExpList() throws ParseException {
    trace_call("ExpList");
    try {
  Exp e;
  ExpList el = new ExpList();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FALSE:
      case NEW:
      case THIS:
      case TRUE:
      case LPAREN:
      case NOT:
      case INTEGER_LITERAL:
      case IDENTIFIER:
        e = Exp();
                    el.addElement(e);
        label_11:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case COMMA:
            ;
            break;
          default:
            jj_la1[13] = jj_gen;
            break label_11;
          }
          e = ExpRest();
                                                        el.addElement(e);
        }
        break;
      default:
        jj_la1[14] = jj_gen;
        ;
      }
    {if (true) return el;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ExpList");
    }
  }

  static final public Exp ExpRest() throws ParseException {
    trace_call("ExpRest");
    try {
  Exp e;
      jj_consume_token(COMMA);
      e = Exp();
    {if (true) return e;}
    throw new Error("Missing return statement in function");
    } finally {
      trace_return("ExpRest");
    }
  }

  static private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  static private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  static private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  static private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  static private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  static private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  static private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  static private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  static private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  static private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  static private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  static private boolean jj_3_7() {
    if (jj_scan_token(NEW)) return true;
    if (jj_scan_token(INT)) return true;
    return false;
  }

  static private boolean jj_3_2() {
    if (jj_3R_13()) return true;
    return false;
  }

  static private boolean jj_3_12() {
    if (jj_scan_token(DOT)) return true;
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3R_30() {
    if (jj_scan_token(THIS)) return true;
    return false;
  }

  static private boolean jj_3_3() {
    if (jj_3R_12()) return true;
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3R_29() {
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3R_19() {
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3_11() {
    if (jj_scan_token(DOT)) return true;
    if (jj_scan_token(LENGTH)) return true;
    return false;
  }

  static private boolean jj_3R_28() {
    if (jj_scan_token(FALSE)) return true;
    return false;
  }

  static private boolean jj_3R_18() {
    if (jj_scan_token(BOOLEAN)) return true;
    return false;
  }

  static private boolean jj_3R_27() {
    if (jj_scan_token(TRUE)) return true;
    return false;
  }

  static private boolean jj_3R_20() {
    if (jj_scan_token(LBRACKET)) return true;
    if (jj_3R_16()) return true;
    return false;
  }

  static private boolean jj_3R_17() {
    if (jj_scan_token(INT)) return true;
    return false;
  }

  static private boolean jj_3R_26() {
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    return false;
  }

  static private boolean jj_3_1() {
    if (jj_3R_12()) return true;
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3_4() {
    if (jj_scan_token(INT)) return true;
    if (jj_scan_token(LBRACKET)) return true;
    if (jj_scan_token(RBRACKET)) return true;
    return false;
  }

  static private boolean jj_3R_12() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_4()) {
    jj_scanpos = xsp;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) {
    jj_scanpos = xsp;
    if (jj_3R_19()) return true;
    }
    }
    }
    return false;
  }

  static private boolean jj_3R_16() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_26()) {
    jj_scanpos = xsp;
    if (jj_3R_27()) {
    jj_scanpos = xsp;
    if (jj_3R_28()) {
    jj_scanpos = xsp;
    if (jj_3R_29()) {
    jj_scanpos = xsp;
    if (jj_3R_30()) {
    jj_scanpos = xsp;
    if (jj_3_7()) {
    jj_scanpos = xsp;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_3R_31()) {
    jj_scanpos = xsp;
    if (jj_3R_32()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  static private boolean jj_3R_25() {
    if (jj_scan_token(STAR)) return true;
    return false;
  }

  static private boolean jj_3_10() {
    if (jj_3R_15()) return true;
    if (jj_3R_16()) return true;
    return false;
  }

  static private boolean jj_3R_14() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_10()) {
    jj_scanpos = xsp;
    if (jj_3R_20()) {
    jj_scanpos = xsp;
    if (jj_3_11()) {
    jj_scanpos = xsp;
    if (jj_3_12()) return true;
    }
    }
    }
    return false;
  }

  static private boolean jj_3R_24() {
    if (jj_scan_token(MINUS)) return true;
    return false;
  }

  static private boolean jj_3R_23() {
    if (jj_scan_token(PLUS)) return true;
    return false;
  }

  static private boolean jj_3_6() {
    if (jj_scan_token(IDENTIFIER)) return true;
    if (jj_scan_token(LBRACKET)) return true;
    return false;
  }

  static private boolean jj_3_5() {
    if (jj_scan_token(IDENTIFIER)) return true;
    if (jj_scan_token(ASSIGN)) return true;
    return false;
  }

  static private boolean jj_3R_32() {
    if (jj_scan_token(LPAREN)) return true;
    return false;
  }

  static private boolean jj_3_9() {
    if (jj_3R_14()) return true;
    return false;
  }

  static private boolean jj_3R_13() {
    if (jj_3R_12()) return true;
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3R_31() {
    if (jj_scan_token(NOT)) return true;
    return false;
  }

  static private boolean jj_3R_22() {
    if (jj_scan_token(LT)) return true;
    return false;
  }

  static private boolean jj_3_8() {
    if (jj_scan_token(NEW)) return true;
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3R_21() {
    if (jj_scan_token(SC_AND)) return true;
    return false;
  }

  static private boolean jj_3R_15() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_21()) {
    jj_scanpos = xsp;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) {
    jj_scanpos = xsp;
    if (jj_3R_25()) return true;
    }
    }
    }
    }
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public MiniJavaParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[15];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x80,0x9100400,0x4000,0x9100400,0x0,0x840,0x840,0x0,0x9100400,0x9100400,0x60200,0x2000000,0x20000000,0x0,0x2062200,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x10,0x0,0x10,0x1,0x10,0x10,0x1f00,0x10,0x0,0x18,0x4,0x0,0x1,0x1c,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[12];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public MiniJavaParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public MiniJavaParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new MiniJavaParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public MiniJavaParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new MiniJavaParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public MiniJavaParser(MiniJavaParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(MiniJavaParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      trace_token(token, "");
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
      trace_token(token, " (in getNextToken)");
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List jj_expentries = new java.util.ArrayList();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[45];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 15; i++) {
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
    for (int i = 0; i < 45; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  static private int trace_indent = 0;
  static private boolean trace_enabled = true;

/** Enable tracing. */
  static final public void enable_tracing() {
    trace_enabled = true;
  }

/** Disable tracing. */
  static final public void disable_tracing() {
    trace_enabled = false;
  }

  static private void trace_call(String s) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Call:   " + s);
    }
    trace_indent = trace_indent + 2;
  }

  static private void trace_return(String s) {
    trace_indent = trace_indent - 2;
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.println("Return: " + s);
    }
  }

  static private void trace_token(Token t, String where) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.print("Consumed token: <" + tokenImage[t.kind]);
      if (t.kind != 0 && !tokenImage[t.kind].equals("\"" + t.image + "\"")) {
        System.out.print(": \"" + t.image + "\"");
      }
      System.out.println(" at line " + t.beginLine + " column " + t.beginColumn + ">" + where);
    }
  }

  static private void trace_scan(Token t1, int t2) {
    if (trace_enabled) {
      for (int i = 0; i < trace_indent; i++) { System.out.print(" "); }
      System.out.print("Visited token: <" + tokenImage[t1.kind]);
      if (t1.kind != 0 && !tokenImage[t1.kind].equals("\"" + t1.image + "\"")) {
        System.out.print(": \"" + t1.image + "\"");
      }
      System.out.println(" at line " + t1.beginLine + " column " + t1.beginColumn + ">; Expected token: <" + tokenImage[t2] + ">");
    }
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 12; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
