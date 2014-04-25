.class public GCD
.super java/lang/Object


.method public <init>()V
iload_0
invokenonvirtual GCD/<init>()V
return
.end method


.method public gcd(II)I
   .limit locals 3
   .limit stack 3
   .field protected tmp I
   aload_0
   aload_0
   getfield GCD/b I
   invokevirtual isNotZero/isNotZero(I)B
   aload_0
   getfield GCD/b I
   istore_2
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
   aload_0
   getfield GCD/a I
   ireturn


.end method


.method public mod(II)I
   .limit locals 3
   .limit stack 4
   .field protected modden I
   istore_2
   if_icmplt TRUE_LABEL_471
   iconst_0
   goto NEXT_LABEL_472
TRUE_LABEL_471:
   iconst_1
NEXT_LABEL_472:
   ifeq IF_FALSE_474
IF_TRUE_473:
   goto IF_NEXT_475
IF_FALSE_474:
   if_icmplt TRUE_LABEL_476
   iconst_0
   goto NEXT_LABEL_477
TRUE_LABEL_476:
   iconst_1
NEXT_LABEL_477:
   aload_0
   aload_0
   getfield GCD/b I
   invokevirtual isNotZero/isNotZero(I)B
IF_NEXT_475:
   aload_0
   getfield GCD/a I
   ireturn


.end method


.method public isNotZero(I)B
   .limit locals 1
   .limit stack 3
   iconst_1
   if_icmplt TRUE_LABEL_478
   iconst_0
   goto NEXT_LABEL_479
TRUE_LABEL_478:
   iconst_1
NEXT_LABEL_479:
   if_icmplt TRUE_LABEL_480
   iconst_0
   goto NEXT_LABEL_481
TRUE_LABEL_480:
   iconst_1
NEXT_LABEL_481:
   ixor
   ireturn


.end method
