.class public Mergesort
.super java/lang/Object
   .field protected a [I


.method public <init>()V
iload_0
invokenonvirtual Mergesort/<init>()V
return
.end method


.method public mergesort([I)[I
   .limit locals 2
   .limit stack 3
   .field protected tmp I
   aload_0
   aload_0
   getfield Mergesort/ar [I
   putfield Mergesort/a [I
   aload_0
   aload_0
   getfield Mergesort/a [I
   arraylength
   invokevirtual sort/sort(II)I
   istore_1
   aload_0
   aload_0
   getfield Mergesort/a [I
   invokevirtual print/print([I)I
   istore_1
   aload_0
   getfield Mergesort/a [I
   areturn


.end method


.method public sort(II)I
   .limit locals 5
   .limit stack 11
   .field protected len I
   .field protected av I
   .field protected tmp I
   aload_0
   getfield Mergesort/t I
   aload_0
   getfield Mergesort/s I
   isub
   istore_2
   lt TRUE_LABEL_0
   iconst_0
   goto NEXT_LABEL_1
TRUE_LABEL_0:
   iconst_1
NEXT_LABEL_1:
   ifeq IF_FALSE_3
IF_TRUE_2:
   iload_2
   istore_2
   goto IF_NEXT_4
IF_FALSE_3:
   aload_0
   aload_0
   getfield Mergesort/s I
   aload_0
   getfield Mergesort/t I
   invokevirtual avg/avg(II)I
   istore_3
   aload_0
   aload_0
   getfield Mergesort/s I
   iload_3
   invokevirtual sort/sort(II)I
   istore_4
   aload_0
   iload_3
   aload_0
   getfield Mergesort/t I
   invokevirtual sort/sort(II)I
   istore_4
   aload_0
   aload_0
   getfield Mergesort/s I
   iload_3
   aload_0
   getfield Mergesort/t I
   invokevirtual merge/merge(III)I
   istore_4
IF_NEXT_4:
   ireturn


.end method


.method public merge(III)I
   .limit locals 8
   .limit stack 5
   .field protected tmp I
   .field protected i I
   .field protected j I
   .field protected c I
   .field protected ta [I
   aload_0
   getfield Mergesort/t I
   aload_0
   getfield Mergesort/s I
   isub
   newarray int
   astore_7
   istore_6
   aload_0
   getfield Mergesort/s I
   istore_4
   aload_0
   getfield Mergesort/m I
   istore_5
   lt TRUE_LABEL_5
   iconst_0
   goto NEXT_LABEL_6
TRUE_LABEL_5:
   iconst_1
NEXT_LABEL_6:
   lt TRUE_LABEL_7
   iconst_0
   goto NEXT_LABEL_8
TRUE_LABEL_7:
   iconst_1
NEXT_LABEL_8:
   lt TRUE_LABEL_9
   iconst_0
   goto NEXT_LABEL_10
TRUE_LABEL_9:
   iconst_1
NEXT_LABEL_10:
   ifeq IF_FALSE_12
IF_TRUE_11:
   iload_6
   aload_0
   getfield Mergesort/a [I
   iload_4
   arraylength
   iastore
   iload_4
   iadd
   istore_4
   iload_6
   iadd
   istore_6
   goto IF_NEXT_13
IF_FALSE_12:
   iload_6
   aload_0
   getfield Mergesort/a [I
   iload_5
   arraylength
   iastore
   iload_5
   iadd
   istore_5
   iload_6
   iadd
   istore_6
IF_NEXT_13:
   lt TRUE_LABEL_14
   iconst_0
   goto NEXT_LABEL_15
TRUE_LABEL_14:
   iconst_1
NEXT_LABEL_15:
   ifeq IF_FALSE_17
IF_TRUE_16:
   lt TRUE_LABEL_19
   iconst_0
   goto NEXT_LABEL_20
TRUE_LABEL_19:
   iconst_1
NEXT_LABEL_20:
   iload_6
   aload_0
   getfield Mergesort/a [I
   iload_4
   arraylength
   iastore
   iload_4
   iadd
   istore_4
   iload_6
   iadd
   istore_6
   goto IF_NEXT_18
IF_FALSE_17:
   lt TRUE_LABEL_21
   iconst_0
   goto NEXT_LABEL_22
TRUE_LABEL_21:
   iconst_1
NEXT_LABEL_22:
   iload_6
   aload_0
   getfield Mergesort/a [I
   iload_5
   arraylength
   iastore
   iload_5
   iadd
   istore_5
   iload_6
   iadd
   istore_6
IF_NEXT_18:
   istore_6
   lt TRUE_LABEL_23
   iconst_0
   goto NEXT_LABEL_24
TRUE_LABEL_23:
   iconst_1
NEXT_LABEL_24:
   aload_0
   getfield Mergesort/s I
   iload_6
   iadd
   aload_7
   iload_6
   arraylength
   iastore
   iload_6
   iadd
   istore_6
   ireturn


.end method


.method public avg(II)I
   .limit locals 2
   .limit stack 2
   lt TRUE_LABEL_25
   iconst_0
   goto NEXT_LABEL_26
TRUE_LABEL_25:
   iconst_1
NEXT_LABEL_26:
   aload_0
   getfield Mergesort/b I
   ireturn


.end method


.method public print([I)I
   .limit locals 2
   .limit stack 3
   .field protected i I
   istore_1
   lt TRUE_LABEL_27
   iconst_0
   goto NEXT_LABEL_28
TRUE_LABEL_27:
   iconst_1
NEXT_LABEL_28:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   getfield Mergesort/a [I
   iload_1
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   iload_1
   iadd
   istore_1
   ireturn


.end method
