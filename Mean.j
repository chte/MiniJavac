.class public Mean
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual Mean/<init>()V
return
.end method


.method public mean(II)I
   .limit locals 4
   .limit stack 1
   .field protected val I
   .field protected rest I
   istore_2
   if_icmplt TRUE_LABEL_484
   iconst_0
   goto NEXT_LABEL_485
TRUE_LABEL_484:
   iconst_1
NEXT_LABEL_485:
   iload_2
   iadd
   istore_2
   iload_2
   ireturn


.end method


.method public arrayMean([I)I
   .limit locals 5
   .limit stack 2
   .field protected length_ I
   .field protected sum I
   .field protected i I
   .field protected val I
   aload_0
   getfield Mean/arr [I
   arraylength
   istore_1
   istore_2
   istore_3
   if_icmplt TRUE_LABEL_486
   iconst_0
   goto NEXT_LABEL_487
TRUE_LABEL_486:
   iconst_1
NEXT_LABEL_487:
   iload_2
   aload_0
   getfield Mean/arr [I
   iload_3
   arraylength
   iadd
   istore_2
   iload_3
   iadd
   istore_3
   iload_3
   isub
   istore_3
   istore_4
   if_icmplt TRUE_LABEL_488
   iconst_0
   goto NEXT_LABEL_489
TRUE_LABEL_488:
   iconst_1
NEXT_LABEL_489:
   iload_4
   iadd
   istore_4
   iload_4
   ireturn


.end method
