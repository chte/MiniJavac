.class public validstatement2
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
   .limit locals 2
   .field protected i I
   .limit stack 1
   aload_0
   putfield validstatement2/i I
   aload_0
   getfield validstatement2/i I
   if_icmplt TRUE_LABEL_0
   iconst_0
   goto NEXT_LABEL_1
TRUE_LABEL_0:
   iconst_1
NEXT_LABEL_1:
   ifeq IF_FALSE_3
IF_TRUE_2:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
   goto IF_NEXT_4
IF_FALSE_3:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
IF_NEXT_4:


   return
.end method
