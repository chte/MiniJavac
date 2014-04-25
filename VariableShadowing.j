.class public VariableShadowing
.super java/lang/Object
   .field protected obj LVariableShadowing;
   .field protected n I


.method public <init>()V
iload_0
invokenonvirtual VariableShadowing/<init>()V
return
.end method


.method public f()I
   .limit locals 1
   .limit stack 0
   .field protected n I
   istore_0
   iload_0
   ireturn


.end method


.method public g()LVariableShadowing;
   .limit locals 1
   .limit stack 2
   .field protected VariableShadowing I
   istore_0
   aload_0
   new 'VariableShadowing'
   dup
   invokespecial 'VariableShadowing/<init>()V'
   putfield VariableShadowing/obj LVariableShadowing;
   aload_0
   getfield VariableShadowing/obj LVariableShadowing;
   areturn


.end method


.method public h()I
   .limit locals 1
   .limit stack 0
   .field protected f I
   istore_0
   iload_0
   ireturn


.end method
