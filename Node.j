.class public Node
.super java/lang/Object
   .field protected _a_0 LNode;
   .field protected _a_1 LNode;
   .field protected _a_2 LNode;
   .field protected _a_3 LNode;
   .field protected parent LNode;
   .field protected numChilds I
   .field protected value I
   .field protected id I


.method public <init>()V
iload_0
invokenonvirtual Node/<init>()V
return
.end method


.method public init4(LNode;LNode;LNode;LNode;II)LNode;
   .limit locals 6
   .limit stack 2
   aload_0
   aload_0
   getfield Node/a_0 LNode;
   putfield Node/_a_0 LNode;
   aload_0
   aload_0
   getfield Node/a_1 LNode;
   putfield Node/_a_1 LNode;
   aload_0
   aload_0
   getfield Node/a_2 LNode;
   putfield Node/_a_2 LNode;
   aload_0
   aload_0
   getfield Node/a_3 LNode;
   putfield Node/_a_3 LNode;
   aload_0
   aload_0
   getfield Node/val I
   putfield Node/value I
   aload_0
   aload_0
   getfield Node/iid I
   putfield Node/id I
   aload_0
   putfield Node/numChilds I
   aload_0
   areturn


.end method


.method public init3(LNode;LNode;LNode;II)LNode;
   .limit locals 5
   .limit stack 2
   aload_0
   aload_0
   getfield Node/a_0 LNode;
   putfield Node/_a_0 LNode;
   aload_0
   aload_0
   getfield Node/a_1 LNode;
   putfield Node/_a_1 LNode;
   aload_0
   aload_0
   getfield Node/a_2 LNode;
   putfield Node/_a_2 LNode;
   aload_0
   aload_0
   getfield Node/val I
   putfield Node/value I
   aload_0
   aload_0
   getfield Node/iid I
   putfield Node/id I
   aload_0
   putfield Node/numChilds I
   aload_0
   areturn


.end method


.method public init2(LNode;LNode;II)LNode;
   .limit locals 4
   .limit stack 2
   aload_0
   aload_0
   getfield Node/a_0 LNode;
   putfield Node/_a_0 LNode;
   aload_0
   aload_0
   getfield Node/a_1 LNode;
   putfield Node/_a_1 LNode;
   aload_0
   aload_0
   getfield Node/val I
   putfield Node/value I
   aload_0
   aload_0
   getfield Node/iid I
   putfield Node/id I
   aload_0
   putfield Node/numChilds I
   aload_0
   areturn


.end method


.method public init1(LNode;II)LNode;
   .limit locals 3
   .limit stack 2
   aload_0
   aload_0
   getfield Node/a_0 LNode;
   putfield Node/_a_0 LNode;
   aload_0
   aload_0
   getfield Node/val I
   putfield Node/value I
   aload_0
   aload_0
   getfield Node/iid I
   putfield Node/id I
   aload_0
   putfield Node/numChilds I
   aload_0
   areturn


.end method


.method public init0(II)LNode;
   .limit locals 2
   .limit stack 2
   aload_0
   aload_0
   getfield Node/val I
   putfield Node/value I
   aload_0
   aload_0
   getfield Node/iid I
   putfield Node/id I
   aload_0
   putfield Node/numChilds I
   aload_0
   areturn


.end method


.method public getVal()I
   .limit locals 0
   .limit stack 1
   aload_0
   getfield Node/value I
   ireturn


.end method


.method public getNode(I)LNode;
   .limit locals 3
   .limit stack 9
   .field protected math LMath;
   .field protected node LNode;
   new 'Math'
   dup
   invokespecial 'Math/<init>()V'
   astore_1
   aload_1
   aload_0
   getfield Node/i I
   invokevirtual equal/equal(II)B
   ifeq IF_FALSE_9
IF_TRUE_8:
   aload_0
   getfield Node/_a_0 LNode;
   astore_2
   goto IF_NEXT_10
IF_FALSE_9:
   aload_1
   aload_0
   getfield Node/i I
   invokevirtual equal/equal(II)B
   ifeq IF_FALSE_12
IF_TRUE_11:
   aload_0
   getfield Node/_a_1 LNode;
   astore_2
   goto IF_NEXT_13
IF_FALSE_12:
   aload_1
   aload_0
   getfield Node/i I
   invokevirtual equal/equal(II)B
   ifeq IF_FALSE_15
IF_TRUE_14:
   aload_0
   getfield Node/_a_2 LNode;
   astore_2
   goto IF_NEXT_16
IF_FALSE_15:
   aload_1
   aload_0
   getfield Node/i I
   invokevirtual equal/equal(II)B
   ifeq IF_FALSE_18
IF_TRUE_17:
   aload_0
   getfield Node/_a_3 LNode;
   astore_2
   goto IF_NEXT_19
IF_FALSE_18:
   aload_0
   astore_2
IF_NEXT_19:
IF_NEXT_16:
IF_NEXT_13:
IF_NEXT_10:
   aload_2
   areturn


.end method


.method public setParent(LNode;)I
   .limit locals 1
   .limit stack 2
   aload_0
   aload_0
   getfield Node/par LNode;
   putfield Node/parent LNode;
   ireturn


.end method


.method public getParent()LNode;
   .limit locals 0
   .limit stack 1
   aload_0
   getfield Node/parent LNode;
   areturn


.end method


.method public getNumChilds()I
   .limit locals 0
   .limit stack 1
   aload_0
   getfield Node/numChilds I
   ireturn


.end method


.method public getId()I
   .limit locals 0
   .limit stack 1
   aload_0
   getfield Node/id I
   ireturn


.end method
