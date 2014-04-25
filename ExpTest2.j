.class public ExpTest2
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
   .limit locals 3
   .field protected i B
   .field protected j I
   .limit stack 1
   aload_0
   isub
   iadd
   putfield ExpTest2/j I


   return
.end method
