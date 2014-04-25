.class public Swapper
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual Swapper/<init>()V
return
.end method


.method public swap([I)I
   .limit locals 4
   .limit stack 4
   .field protected tmparr [I
   .field protected size I
   .field protected i I
   aload_0
   getfield Swapper/arr [I
   arraylength
   istore_2
   iload_2
   newarray int
   astore_1
   istore_3
   if_icmplt TRUE_LABEL_10
   iconst_0
   goto NEXT_LABEL_11
TRUE_LABEL_10:
   iconst_1
NEXT_LABEL_11:
   iload_3
   aload_0
   getfield Swapper/arr [I
   iload_2
   iload_3
   isub
   isub
   arraylength
   iastore
   iload_3
   iadd
   istore_3
   istore_3
   if_icmplt TRUE_LABEL_12
   iconst_0
   goto NEXT_LABEL_13
TRUE_LABEL_12:
   iconst_1
NEXT_LABEL_13:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   getfield Swapper/arr [I
   iload_3
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   iload_3
   iadd
   istore_3
   ireturn


.end method
