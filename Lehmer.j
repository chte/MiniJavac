.class public Lehmer
.super java/lang/Object
   .field protected dummy I
   .field protected a I
   .field protected b I
   .field protected c I
   .field protected d I
   .field protected z I


.method public <init>()V
iload_0
invokenonvirtual Lehmer/<init>()V
return
.end method


.method public init()I
   .limit locals 0
   .limit stack 1
   aload_0
   putfield Lehmer/b I
   aload_0
   putfield Lehmer/c I
   aload_0
   getfield Lehmer/c I
   ireturn


.end method
