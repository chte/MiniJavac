.class public Lehmer
.super java/lang/Object
   .field protected dummy I
   .field protected a I
   .field protected b I
   .field protected c I
   .field protected d I
   .field protected z I


.method public <init>()V
iload_0
invokenonvirtual Lehmer/<init>()V
return
.end method


.method public init()I
   .limit locals 0
   .limit stack 1
   aload_0
   putfield Lehmer/a I
   aload_0
   putfield Lehmer/b I
   aload_0
   putfield Lehmer/c I
   aload_0
   getfield Lehmer/b I
   ireturn


.end method


.method public lehmer(I)I
   .limit locals 1
   .limit stack 5
   aload_0
   aload_0
   aload_0
   getfield Lehmer/a I
   aload_0
   getfield Lehmer/b I
   aload_0
   getfield Lehmer/z I
   imul
   iadd
   aload_0
   getfield Lehmer/c I
   invokevirtual mod/mod(II)I
   putfield Lehmer/z I
   aload_0
   aload_0
   getfield Lehmer/z I
   aload_0
   getfield Lehmer/s I
   invokevirtual mod/mod(II)I
   ireturn


.end method


.method public mod(II)I
   .limit locals 2
   .limit stack 2
   if_icmplt TRUE_LABEL_0
   iconst_0
   goto NEXT_LABEL_1
TRUE_LABEL_0:
   iconst_1
NEXT_LABEL_1:
   aload_0
   getfield Lehmer/a I
   ireturn


.end method


.method public test(I)I
   .limit locals 1
   .limit stack 3
   aload_0
   aload_0
   invokevirtual init/init()I
   putfield Lehmer/z I
   if_icmplt TRUE_LABEL_2
   iconst_0
   goto NEXT_LABEL_3
TRUE_LABEL_2:
   iconst_1
NEXT_LABEL_3:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   invokevirtual lehmer/lehmer(I)I
   invokevirtual java/io/PrintStream/println(I)V
   aload_0
   invokevirtual lehmer/lehmer(I)I
   ireturn


.end method
