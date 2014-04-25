.class public BSort
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual BSort/<init>()V
return
.end method


.method public sort([I)[I
   .limit locals 4
   .limit stack 6
   .field protected i I
   .field protected done B
   .field protected tmp I
   iconst_0
   istore_2
   iconst_1
   iload_2
   ixor
   iconst_1
   istore_2
   istore_1
   if_icmplt TRUE_LABEL_9
   iconst_0
   goto NEXT_LABEL_10
TRUE_LABEL_9:
   iconst_1
NEXT_LABEL_10:
   if_icmplt TRUE_LABEL_11
   iconst_0
   goto NEXT_LABEL_12
TRUE_LABEL_11:
   iconst_1
NEXT_LABEL_12:
   if_icmplt TRUE_LABEL_13
   iconst_0
   goto NEXT_LABEL_14
TRUE_LABEL_13:
   iconst_1
NEXT_LABEL_14:
   ifeq IF_FALSE_16
IF_TRUE_15:
   goto IF_NEXT_17
IF_FALSE_16:
   aload_0
   getfield BSort/array [I
   iload_1
   arraylength
   istore_3
   iload_1
   aload_0
   getfield BSort/array [I
   iload_1
   iadd
   arraylength
   iastore
   iload_1
   iadd
   iload_3
   iastore
   iconst_0
   istore_2
IF_NEXT_17:
   iload_1
   iadd
   istore_1
   aload_0
   getfield BSort/array [I
   areturn


.end method


.method public rSort([I)[I
   .limit locals 3
   .limit stack 4
   .field protected i I
   .field protected tmp I
   istore_1
   if_icmplt TRUE_LABEL_18
   iconst_0
   goto NEXT_LABEL_19
TRUE_LABEL_18:
   iconst_1
NEXT_LABEL_19:
   aload_0
   getfield BSort/array [I
   iload_1
   arraylength
   istore_2
   iload_1
   aload_0
   getfield BSort/array [I
   aload_0
   getfield BSort/array [I
   arraylength
   iload_1
   isub
   isub
   arraylength
   iastore
   aload_0
   getfield BSort/array [I
   arraylength
   iload_1
   isub
   isub
   iload_2
   iastore
   iload_1
   iadd
   istore_1
   aload_0
   getfield BSort/array [I
   areturn


.end method
