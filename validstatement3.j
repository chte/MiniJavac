.class public validstatement3
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
   .limit stack 2
   aload_0
   putfield validstatement3/i I
   if_icmplt TRUE_LABEL_0
   iconst_0
   goto NEXT_LABEL_1
TRUE_LABEL_0:
   iconst_1
NEXT_LABEL_1:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
   aload_0
   aload_0
   getfield validstatement3/i I
   iadd
   putfield validstatement3/i I


   return
.end method
