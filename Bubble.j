.class public Bubble
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
   .limit locals 1
   .limit stack 6
   iconst_1
   new 'bool'
   dup
   invokespecial 'bool/<init>()V'
   iconst_0
   invokevirtual bool/bool(B)B
   ixor
   ifeq IF_FALSE_1
IF_TRUE_0:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   new 'DD'
   dup
   invokespecial 'DD/<init>()V'
   invokevirtual DD/DD()I
   invokevirtual java/io/PrintStream/println(I)V
   goto IF_NEXT_2
IF_FALSE_1:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   new 'DD'
   dup
   invokespecial 'DD/<init>()V'
   invokevirtual DD/DD()I
   isub
   invokevirtual java/io/PrintStream/println(I)V
IF_NEXT_2:


   return
.end method
