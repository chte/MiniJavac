.class public MeanTest
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual MeanTest/<init>()V
return
.end method


.method public run()I
   .limit locals 6
   .limit stack 0
   .field protected t LMeanTest;
   .field protected sum I
   .field protected i I
   .field protected ii I
   .field protected arr [I
   .field protected baa I
   istore_5
   istore_2
   istore_3
   iload_2
   newarray int
   astore_4
   new 'MeanTest'
   dup
   invokespecial 'MeanTest/<init>()V'
   astore_0
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_4
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   iload_1
   iadd
   istore_1
   new 'Mean'
   dup
   invokespecial 'Mean/<init>()V'
   iload_1
   invokevirtual mean/mean(II)I
   istore_1
   getstatic java/lang/System/out Ljava/io/PrintStream;
   iload_1
   invokevirtual java/io/PrintStream/println(I)V
   if_icmplt TRUE_LABEL_482
   iconst_0
   goto NEXT_LABEL_483
TRUE_LABEL_482:
   iconst_1
NEXT_LABEL_483:
   iload_3
   iload_3
   iastore
   iload_3
   iadd
   istore_3
   new 'Mean'
   dup
   invokespecial 'Mean/<init>()V'
   aload_4
   invokevirtual arrayMean/arrayMean([I)I
   istore_5
   ireturn


.end method
