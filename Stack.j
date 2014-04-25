.class public Stack
.super java/lang/Object
   .field protected stack [I
   .field protected size I


.method public <init>()V
iload_0
invokenonvirtual Stack/<init>()V
return
.end method


.method public init()I
   .limit locals 0
   .limit stack 1
   aload_0
   newarray int
   putfield Stack/stack [I
   aload_0
   putfield Stack/size I
   ireturn


.end method


.method public isEmpty()B
   .limit locals 1
   .limit stack 3
   .field protected i B
   new 'Math'
   dup
   invokespecial 'Math/<init>()V'
   aload_0
   getfield Stack/size I
   invokevirtual equal/equal(II)B
   ifeq IF_FALSE_1
IF_TRUE_0:
   iconst_1
   istore_0
   goto IF_NEXT_2
IF_FALSE_1:
   iconst_0
   istore_0
IF_NEXT_2:
   iload_0
   ireturn


.end method


.method public push(I)I
   .limit locals 3
   .limit stack 5
   .field protected i I
   .field protected tmp [I
   istore_1
   new 'Math'
   dup
   invokespecial 'Math/<init>()V'
   aload_0
   getfield Stack/size I
   aload_0
   getfield Stack/stack [I
   arraylength
   invokevirtual equal/equal(II)B
   ifeq IF_FALSE_4
IF_TRUE_3:
   aload_0
   getfield Stack/stack [I
   arraylength
   imul
   newarray int
   astore_2
   if_icmplt TRUE_LABEL_6
   iconst_0
   goto NEXT_LABEL_7
TRUE_LABEL_6:
   iconst_1
NEXT_LABEL_7:
   iload_1
   aload_0
   getfield Stack/stack [I
   iload_1
   arraylength
   iastore
   iload_1
   iadd
   istore_1
   aload_0
   aload_2
   putfield Stack/stack [I
   goto IF_NEXT_5
IF_FALSE_4:
IF_NEXT_5:
   aload_0
   getfield Stack/size I
   aload_0
   getfield Stack/push I
   iastore
   aload_0
   aload_0
   getfield Stack/size I
   iadd
   putfield Stack/size I
   aload_0
   getfield Stack/push I
   ireturn


.end method


.method public pop()I
   .limit locals 0
   .limit stack 2
   aload_0
   aload_0
   getfield Stack/size I
   isub
   putfield Stack/size I
   aload_0
   getfield Stack/stack [I
   aload_0
   getfield Stack/size I
   arraylength
   ireturn


.end method


.method public peek()I
   .limit locals 0
   .limit stack 2
   aload_0
   getfield Stack/stack [I
   aload_0
   getfield Stack/size I
   isub
   arraylength
   ireturn


.end method
