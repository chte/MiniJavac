.class public helloWorld
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
   .field protected HelloWorld I
   .limit stack 2
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   getfield helloWorld/HelloWorld I
   invokevirtual java/io/PrintStream/println(I)V


   return
.end method
