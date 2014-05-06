package assembler;

import syntaxtree.*;
import symboltree.*;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Bytecode {
  private static String className;
  private static List<String> output;
  private static int labelCount;


  public static void init() {
    output = new ArrayList<String>(2000);
  }

  public static void setClassName(String name) {
    className = name;
  }

  public static String getClassName() {
    return className;
  }

  public static void newline() {
    output.add("\n");
  }

  public static void directive(String s) {
    output.add(s);
  }
  
  public static int stackDepth() {
    return output.size();
  }

  public static void comment(String s){
    output.add(";");
    output.add(";" + s);
    output.add(";");
  }
  public static String label(String l) {
    // if (!output.get(output.size()-1).trim().isEmpty())
    //   newline();
    
    return l + "_" + labelCount++;
  }

  public static void write(String c) {
    output.add(c);
  }

  public static void write(int position, String c) {
    output.add(position, c);
  }

  public static void writeind(String c) {
    output.add("   " + c);
  }

  public static void writeind(int position, String c) {
    output.add(position, "   " + c);
  }

  public static void save() {
    String filename = className + ".j";
    
    try {
      File file = new File(filename);
      FileWriter writer = new FileWriter(file);
      for (String line : output) {
        writer.append(line);
        writer.append('\n');
      }

      writer.close();
    } catch (IOException e) { }
  }

  public static void standardConstructor(String c) {
    directive(".method public <init>()V");
    write(getConstant("aload", 0));
    write("invokespecial " + c + "/<init>()V");
    write("return");
    directive(".end method");
  }

  public static String getConstant(String instr, int c) {
    int lim = instr.equals("iconst") ? 5 : 3;
    return instr + (c >= 0 && c <= lim ? '_' : ' ') + c;
  }


  public static String descriptor(Type t) {
    if (t instanceof IntegerType)
      return "I";
    else if (t instanceof BooleanType)
      return "Z";
    else if (t instanceof LongType)
      return "J";
    else if (t instanceof IntArrayType)
      return "[I";
    else if (t instanceof LongArrayType)
      return "[J";
    else if (t instanceof IdentifierType)
      return "L" + ((IdentifierType)t).s + ";";

    return null;
  }

  private String t(Type t) {
    if (t instanceof IntegerType || t instanceof BooleanType) {
      return "i";
    } else if (t instanceof IdentifierType || t instanceof IntArrayType) {
      return "a";
    } else if (t instanceof VoidType) {
      return "";
    }
    return "";
  }

  public static String store(Type t, String l) {
    if (t instanceof IntegerType || t instanceof BooleanType) {
      return "istore " + l;
    }
    else if (t instanceof LongType) {
      return "lstore " + l;
    }
     else if (t instanceof IdentifierType || t instanceof IntArrayType || t instanceof LongArrayType) {
      return "astore " + l;
    } 
    return "";
  }

  public static String load(Type t, String l) {
    if (t instanceof BooleanType || t instanceof IntegerType) {
      return "iload " + l;
    } 
    else if (t instanceof LongType) {
      return "lload " + l;
    } 
     else if (t instanceof IdentifierType || t instanceof IntArrayType || t instanceof LongArrayType) {
      return "aload " + l;
    } 
    return "";
  }




  public static String getMethodParams(MethodBinding m){
    String d = "(";

    for (VariableBinding vd : m.getParams())
      d += descriptor(vd.getType());

    d += ")" + descriptor(m.getType());
    return d;  
  }

}