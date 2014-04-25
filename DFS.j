.class public DFS
.super java/lang/Object
   .field protected start LNode;
   .field protected currNode LNode;
   .field protected currVal I
   .field protected value I
   .field protected stack LStack;


.method public <init>()V
iload_0
invokenonvirtual DFS/<init>()V
return
.end method


.method public find(LNode;I)LNode;
   .limit locals 3
   .limit stack 3
   .field protected tmp I
   aload_0
   aload_0
   getfield DFS/startNode LNode;
   putfield DFS/start LNode;
   aload_0
   aload_0
   getfield DFS/start LNode;
   putfield DFS/currNode LNode;
   aload_0
   aload_0
   getfield DFS/val I
   putfield DFS/value I
   aload_0
   new 'Stack'
   dup
   invokespecial 'Stack/<init>()V'
   putfield DFS/stack LStack;
   aload_0
   getfield DFS/stack LStack;
   invokevirtual init/init()I
   istore_2
   aload_0
   invokevirtual DFS/DFS()LNode;
   areturn


.end method


.method public DFS()LNode;
   .limit locals 3
   .limit stack 15
   .field protected found B
   .field protected math LMath;
   .field protected tmp I
   new 'Math'
   dup
   invokespecial 'Math/<init>()V'
   astore_1
   iconst_0
   istore_0
   aload_0
   putfield DFS/currVal I
   iconst_1
   iload_0
   ixor
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   getfield DFS/currNode LNode;
   invokevirtual getId/getId()I
   invokevirtual java/io/PrintStream/println(I)V
   aload_1
   aload_0
   getfield DFS/currNode LNode;
   invokevirtual getVal/getVal()I
   aload_0
   getfield DFS/value I
   invokevirtual equal/equal(II)B
   ifeq IF_FALSE_28
IF_TRUE_27:
   iconst_1
   istore_0
   goto IF_NEXT_29
IF_FALSE_28:
IF_NEXT_29:
   iconst_1
   iload_0
   aload_1
   aload_0
   getfield DFS/currVal I
   aload_0
   getfield DFS/currNode LNode;
   invokevirtual getNumChilds/getNumChilds()I
   invokevirtual equal/equal(II)B
   ixor
   ifeq IF_FALSE_31
IF_TRUE_30:
   aload_0
   getfield DFS/stack LStack;
   invokevirtual isEmpty/isEmpty()B
   ifeq IF_FALSE_34
IF_TRUE_33:
   iconst_1
   istore_0
   goto IF_NEXT_35
IF_FALSE_34:
   aload_0
   aload_0
   getfield DFS/currNode LNode;
   invokevirtual getParent/getParent()LNode;
   putfield DFS/currNode LNode;
   aload_0
   aload_0
   getfield DFS/stack LStack;
   invokevirtual pop/pop()I
   putfield DFS/currVal I
IF_NEXT_35:
   goto IF_NEXT_32
IF_FALSE_31:
   iconst_1
   iload_0
   ixor
   ifeq IF_FALSE_37
IF_TRUE_36:
   aload_0
   getfield DFS/currNode LNode;
   aload_0
   getfield DFS/currVal I
   invokevirtual getNode/getNode(I)LNode;
   aload_0
   getfield DFS/currNode LNode;
   invokevirtual setParent/setParent(LNode;)I
   istore_2
   aload_0
   aload_0
   getfield DFS/currNode LNode;
   aload_0
   getfield DFS/currVal I
   invokevirtual getNode/getNode(I)LNode;
   putfield DFS/currNode LNode;
   aload_0
   aload_0
   getfield DFS/currVal I
   iadd
   putfield DFS/currVal I
   aload_0
   getfield DFS/stack LStack;
   aload_0
   getfield DFS/currVal I
   invokevirtual push/push(I)I
   istore_2
   aload_0
   putfield DFS/currVal I
   goto IF_NEXT_38
IF_FALSE_37:
IF_NEXT_38:
IF_NEXT_32:
   aload_0
   getfield DFS/currNode LNode;
   areturn


.end method
