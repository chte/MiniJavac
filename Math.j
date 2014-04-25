.class public Math
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual Math/<init>()V
return
.end method


.method public equal(II)B
   .limit locals 3
   .limit stack 4
   .field protected kase B
   iconst_1
   if_icmplt TRUE_LABEL_20
   iconst_0
   goto NEXT_LABEL_21
TRUE_LABEL_20:
   iconst_1
NEXT_LABEL_21:
   iconst_1
   if_icmplt TRUE_LABEL_22
   iconst_0
   goto NEXT_LABEL_23
TRUE_LABEL_22:
   iconst_1
NEXT_LABEL_23:
   ixor
   ixor
   ifeq IF_FALSE_25
IF_TRUE_24:
   iconst_1
   istore_2
   goto IF_NEXT_26
IF_FALSE_25:
   iconst_0
   istore_2
IF_NEXT_26:
   iload_2
   ireturn


.end method
