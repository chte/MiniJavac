.class public B
.super java/lang/Object
   .field protected a B


.method public <init>()V
iload_0
invokenonvirtual B/<init>()V
return
.end method


.method public a()LA;
   .limit locals 0
   .limit stack 5
   new 'A'
   dup
   invokespecial 'A/<init>()V'
   new 'A'
   dup
   invokespecial 'A/<init>()V'
   aload_0
   getfield B/a B
   invokevirtual a/a(LA;B)LA;
   new 'A'
   dup
   invokespecial 'A/<init>()V'
   iconst_0
   invokevirtual b/b(LA;B)LB;
   invokevirtual a/a()LA;
   areturn


.end method


.method public bsort()LBSort;
   .limit locals 0
   .limit stack 2
   new 'BSort'
   dup
   invokespecial 'BSort/<init>()V'
   areturn


.end method
