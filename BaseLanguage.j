.class public BaseLanguage
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
   .limit stack 3
   getstatic java/lang/System/out Ljava/io/PrintStream;
   new 'ComputePrimes'
   dup
   invokespecial 'ComputePrimes/<init>()V'
   invokevirtual generatePrimes/generatePrimes(I)I
   invokevirtual java/io/PrintStream/println(I)V


   return
.end method
