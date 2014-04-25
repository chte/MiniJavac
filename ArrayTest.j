.class public ArrayTest
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual ArrayTest/<init>()V
return
.end method


.method public main(I)I
   .limit locals 5
   .limit stack 3
   .field protected stack [I
   .field protected ifeq [I
   .field protected pop [I
   .field protected a4 [I
   newarray int
   astore_1
   newarray int
   astore_2
   newarray int
   astore_3
   newarray int
   astore_4
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_1
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_2
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_3
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_4
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   newarray int
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   if_icmplt TRUE_LABEL_6
   iconst_0
   goto NEXT_LABEL_7
TRUE_LABEL_6:
   iconst_1
NEXT_LABEL_7:
   aload_0
   getfield ArrayTest/i I
   aload_0
   getfield ArrayTest/i I
   iastore
   if_icmplt TRUE_LABEL_8
   iconst_0
   goto NEXT_LABEL_9
TRUE_LABEL_8:
   iconst_1
NEXT_LABEL_9:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_3
   aload_0
   getfield ArrayTest/i I
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   aload_2
   arraylength
   ireturn


.end method
