.class public Caller
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual Caller/<init>()V
return
.end method


.method public call()I
   .limit locals 2
   .limit stack 1
   .field protected m LMergesort;
   .field protected a [I
   newarray int
   astore_1
   new 'Mergesort'
   dup
   invokespecial 'Mergesort/<init>()V'
   astore_0
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   iastore
   aload_0
   aload_1
   invokevirtual mergesort/mergesort([I)[I
   astore_1
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   invokevirtual avg/avg(II)I
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   invokevirtual avg/avg(II)I
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   invokevirtual avg/avg(II)I
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
   ireturn


.end method
