.class public validexpression1
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
   .field protected i B
   .limit stack 3
   aload_0
   iconst_1
   iconst_1
   ixor
   putfield validexpression1/i B


   return
.end method
