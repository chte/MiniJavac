.class public DepthFirstSearch
.super java/lang/Object


;
;standard initializer (calls java.lang.Object's initializer)
;
.method public <init>()V
iload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method


;
;main() - main method follows
;
.method public static main([Ljava/lang/String;)V
   .limit locals 1
   .limit stack 28
   getstatic java/lang/System/out Ljava/io/PrintStream;
   new 'DFS'
   dup
   invokespecial 'DFS/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   invokevirtual init1/init1(LNode;II)LNode;
   invokevirtual init4/init4(LNode;LNode;LNode;LNode;II)LNode;
   invokevirtual init1/init1(LNode;II)LNode;
   invokevirtual init2/init2(LNode;LNode;II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   invokevirtual init1/init1(LNode;II)LNode;
   invokevirtual init2/init2(LNode;LNode;II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   newarray int
   arraylength
   invokevirtual init0/init0(II)LNode;
   imul
   invokevirtual init2/init2(LNode;LNode;II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   isub
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   isub
   invokevirtual init2/init2(LNode;LNode;II)LNode;
   invokevirtual init3/init3(LNode;LNode;LNode;II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   new 'Node'
   dup
   invokespecial 'Node/<init>()V'
   invokevirtual init0/init0(II)LNode;
   invokevirtual init1/init1(LNode;II)LNode;
   invokevirtual init2/init2(LNode;LNode;II)LNode;
   invokevirtual init2/init2(LNode;LNode;II)LNode;
   invokevirtual init4/init4(LNode;LNode;LNode;LNode;II)LNode;
   invokevirtual find/find(LNode;I)LNode;
   invokevirtual getId/getId()I
   invokevirtual java/io/PrintStream/println(I)V


   return
.end method
