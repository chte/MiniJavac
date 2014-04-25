.class public FourTap
.super java/lang/Object
   .field protected dummy I
   .field protected index I
   .field protected a I
   .field protected b I
   .field protected c I
   .field protected d I
   .field protected seq [I


.method public <init>()V
iload_0
invokenonvirtual FourTap/<init>()V
return
.end method


.method public init(I)I
   .limit locals 2
   .limit stack 1
   .field protected bootstrap LLehmer;
   aload_0
   putfield FourTap/a I
   aload_0
   putfield FourTap/b I
   aload_0
   putfield FourTap/c I
   aload_0
   putfield FourTap/d I
   aload_0
   aload_0
   getfield FourTap/size I
   aload_0
   getfield FourTap/d I
   iadd
   iadd
   newarray int
   putfield FourTap/seq [I
   new 'Lehmer'
   dup
   invokespecial 'Lehmer/<init>()V'
   astore_1
   aload_0
   aload_1
   invokevirtual init/init()I
   putfield FourTap/dummy I
   aload_0
   aload_0
   getfield FourTap/d I
   putfield FourTap/dummy I
   if_icmplt TRUE_LABEL_4
   iconst_0
   goto NEXT_LABEL_5
TRUE_LABEL_4:
   iconst_1
NEXT_LABEL_5:
   aload_0
   getfield FourTap/dummy I
   aload_1
   invokevirtual lehmer/lehmer(I)I
   iastore
   aload_0
   aload_0
   getfield FourTap/dummy I
   isub
   putfield FourTap/dummy I
   aload_0
   getfield FourTap/d I
   ireturn


.end method


.method public div(II)I
   .limit locals 3
   .limit stack 1
   .field protected current I
   istore_2
   if_icmplt TRUE_LABEL_6
   iconst_0
   goto NEXT_LABEL_7
TRUE_LABEL_6:
   iconst_1
NEXT_LABEL_7:
   iload_2
   iadd
   istore_2
   iload_2
   isub
   ireturn


.end method


.method public mod(II)I
   .limit locals 2
   .limit stack 2
   if_icmplt TRUE_LABEL_8
   iconst_0
   goto NEXT_LABEL_9
TRUE_LABEL_8:
   iconst_1
NEXT_LABEL_9:
   aload_0
   getfield FourTap/a I
   ireturn


.end method


.method public lsb(I)I
   .limit locals 1
   .limit stack 2
   aload_0
   aload_0
   getfield FourTap/i I
   invokevirtual mod/mod(II)I
   ireturn


.end method


.method public xor(II)I
   .limit locals 4
   .limit stack 5
   .field protected result I
   .field protected pow I
   istore_2
   istore_3
   if_icmplt TRUE_LABEL_10
   iconst_0
   goto NEXT_LABEL_11
TRUE_LABEL_10:
   iconst_1
NEXT_LABEL_11:
   iload_3
   aload_0
   aload_0
   aload_0
   getfield FourTap/i I
   invokevirtual lsb/lsb(I)I
   aload_0
   aload_0
   getfield FourTap/j I
   invokevirtual lsb/lsb(I)I
   iadd
   invokevirtual lsb/lsb(I)I
   imul
   iload_2
   iadd
   istore_2
   iload_3
   imul
   istore_3
   iload_2
   ireturn


.end method


.method public xor4(IIII)I
   .limit locals 4
   .limit stack 7
   aload_0
   aload_0
   getfield FourTap/i I
   aload_0
   aload_0
   getfield FourTap/j I
   aload_0
   aload_0
   getfield FourTap/k I
   aload_0
   getfield FourTap/l I
   invokevirtual xor/xor(II)I
   invokevirtual xor/xor(II)I
   invokevirtual xor/xor(II)I
   ireturn


.end method


.method public fourTap()I
   .limit locals 0
   .limit stack 8
   aload_0
   getfield FourTap/index I
   aload_0
   aload_0
   getfield FourTap/seq [I
   aload_0
   getfield FourTap/index I
   aload_0
   getfield FourTap/a I
   isub
   arraylength
   aload_0
   getfield FourTap/seq [I
   aload_0
   getfield FourTap/index I
   aload_0
   getfield FourTap/b I
   isub
   arraylength
   aload_0
   getfield FourTap/seq [I
   aload_0
   getfield FourTap/index I
   aload_0
   getfield FourTap/c I
   isub
   arraylength
   aload_0
   getfield FourTap/seq [I
   aload_0
   getfield FourTap/index I
   aload_0
   getfield FourTap/d I
   isub
   arraylength
   invokevirtual xor4/xor4(IIII)I
   iastore
   aload_0
   aload_0
   getfield FourTap/index I
   iadd
   putfield FourTap/index I
   aload_0
   getfield FourTap/seq [I
   aload_0
   getfield FourTap/index I
   isub
   arraylength
   ireturn


.end method


.method public test(I)I
   .limit locals 1
   .limit stack 4
   aload_0
   aload_0
   aload_0
   getfield FourTap/n I
   invokevirtual init/init(I)I
   putfield FourTap/index I
   if_icmplt TRUE_LABEL_12
   iconst_0
   goto NEXT_LABEL_13
TRUE_LABEL_12:
   iconst_1
NEXT_LABEL_13:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   invokevirtual fourTap/fourTap()I
   invokevirtual java/io/PrintStream/println(I)V
   aload_0
   invokevirtual fourTap/fourTap()I
   ireturn


.end method
