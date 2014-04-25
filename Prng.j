.class public Prng
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
   .limit stack 4
   getstatic java/lang/System/out Ljava/io/PrintStream;
   new 'Lehmer'
   dup
   invokespecial 'Lehmer/<init>()V'
   invokevirtual test/test(I)I
   invokevirtual java/io/PrintStream/println(I)V
   getstatic java/lang/System/out Ljava/io/PrintStream;
   new 'FourTap'
   dup
   invokespecial 'FourTap/<init>()V'
   invokevirtual test/test(I)I
   invokevirtual java/io/PrintStream/println(I)V


   return
.end method
