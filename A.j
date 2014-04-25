.class public A
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual A/<init>()V
return
.end method


.method public a(LA;B)LA;
   .limit locals 2
   .limit stack 2
   aload_0
   getfield A/i B
   ifeq IF_FALSE_21
IF_TRUE_20:
   goto IF_NEXT_22
IF_FALSE_21:
IF_NEXT_22:
   aload_0
   getfield A/a LA;
   areturn


.end method


.method public b(LA;B)LB;
   .limit locals 3
   .limit stack 3
   .field protected b LB;
   new 'B'
   dup
   invokespecial 'B/<init>()V'
   astore_2
   aload_0
   getfield A/i B
   ifeq IF_FALSE_24
IF_TRUE_23:
   new 'B'
   dup
   invokespecial 'B/<init>()V'
   astore_2
   goto IF_NEXT_25
IF_FALSE_24:
   aload_2
   astore_2
IF_NEXT_25:
   aload_2
   areturn


.end method
