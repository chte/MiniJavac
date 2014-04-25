.class public Basic
.super java/lang/Object


;
;standard initializer (calls java.lang.Object's initializer)
;
.method public <init>()V
iload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method


;
;main() - main method follows
;
.method public static main([Ljava/lang/String;)V
   .limit locals 5
   .field protected res I
   .field protected main Lmain;
   .field protected a I
   .field protected b B
   .limit stack 12
   aload_0
   new 'ArrayTest'
   dup
   invokespecial 'ArrayTest/<init>()V'
   invokevirtual main/main(I)I
   putfield Basic/res I
   aload_0
   new 'MeanTest'
   dup
   invokespecial 'MeanTest/<init>()V'
   invokevirtual run/run()I
   putfield Basic/res I
   aload_0
   new 'A'
   dup
   invokespecial 'A/<init>()V'
   invokevirtual A/A()I
   putfield Basic/a I
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   getfield Basic/a I
   invokevirtual java/io/PrintStream/println(I)V
   aload_0
   iconst_1
   iconst_1
   iconst_1
   iconst_1
   iconst_1
   iconst_1
   iconst_1
   iconst_1
   iconst_1
   iconst_1
   ixor
   ixor
   ixor
   ixor
   ixor
   ixor
   ixor
   ixor
   ixor
   putfield Basic/b B
   aload_0
   getfield Basic/b B
   ifeq IF_FALSE_1
IF_TRUE_0:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
   goto IF_NEXT_2
IF_FALSE_1:
   iconst_1
   iconst_1
   ixor
   ifeq IF_FALSE_4
IF_TRUE_3:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
   goto IF_NEXT_5
IF_FALSE_4:
   aload_0
   new 'IntegerTest'
   dup
   invokespecial 'IntegerTest/<init>()V'
   invokevirtual run/run()I
   putfield Basic/res I
IF_NEXT_5:
IF_NEXT_2:


   return
.end method
