.class public DD
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual DD/<init>()V
return
.end method


.method public DD()I
   .limit locals 4
   .limit stack 7
   .field protected array [I
   .field protected k I
   .field protected i I
   .field protected bsort LBSort;
   newarray int
   astore_0
   istore_1
   istore_2
   if_icmplt TRUE_LABEL_3
   iconst_0
   goto NEXT_LABEL_4
TRUE_LABEL_3:
   iconst_1
NEXT_LABEL_4:
   iload_2
   iload_1
   iastore
   iload_1
   isub
   istore_1
   iload_2
   iadd
   istore_2
   new 'A'
   dup
   invokespecial 'A/<init>()V'
   new 'A'
   dup
   invokespecial 'A/<init>()V'
   iconst_0
   invokevirtual a/a(LA;B)LA;
   new 'A'
   dup
   invokespecial 'A/<init>()V'
   iconst_0
   invokevirtual b/b(LA;B)LB;
   invokevirtual bsort/bsort()LBSort;
   astore_3
   new 'BSort'
   dup
   invokespecial 'BSort/<init>()V'
   aload_3
   new 'BSort'
   dup
   invokespecial 'BSort/<init>()V'
   aload_3
   aload_0
   invokevirtual sort/sort([I)[I
   invokevirtual rSort/rSort([I)[I
   invokevirtual sort/sort([I)[I
   invokevirtual rSort/rSort([I)[I
   astore_0
   istore_2
   if_icmplt TRUE_LABEL_5
   iconst_0
   goto NEXT_LABEL_6
TRUE_LABEL_5:
   iconst_1
NEXT_LABEL_6:
   if_icmplt TRUE_LABEL_7
   iconst_0
   goto NEXT_LABEL_8
TRUE_LABEL_7:
   iconst_1
NEXT_LABEL_8:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   iload_2
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   iload_2
   iadd
   istore_2
   ireturn


.end method
