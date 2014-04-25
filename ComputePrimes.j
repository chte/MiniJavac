.class public ComputePrimes
.super java/lang/Object
   .field protected stop I
   .field protected primes [I
   .field protected number_of_primes I


.method public <init>()V
iload_0
invokenonvirtual ComputePrimes/<init>()V
return
.end method


.method public somePrimes(I)[I
   .limit locals 3
   .limit stack 0
   .field protected n I
   .field protected k I
   istore_1
   istore_2
   aload_0
   putfield ComputePrimes/number_of_primes I
   aload_0
   aload_0
   getfield ComputePrimes/max I
   putfield ComputePrimes/stop I
   aload_0
   aload_0
   getfield ComputePrimes/stop I
   newarray int
   putfield ComputePrimes/primes [I
   iastore
   iastore
   if_icmplt TRUE_LABEL_0
   iconst_0
   goto NEXT_LABEL_1
TRUE_LABEL_0:
   iconst_1
NEXT_LABEL_1:
   aload_0
   iload_1
   aload_0
   getfield ComputePrimes/primes [I
   iload_2
   invokevirtual isPrime/isPrime(I[II)B
   ifeq IF_FALSE_3
IF_TRUE_2:
   iload_2
   iadd
   istore_2
   iload_2
   iload_1
   iastore
   aload_0
   aload_0
   getfield ComputePrimes/number_of_primes I
   iadd
   putfield ComputePrimes/number_of_primes I
   goto IF_NEXT_4
IF_FALSE_3:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   invokevirtual java/io/PrintStream/println(I)V
IF_NEXT_4:
   iload_1
   iadd
   istore_1
   aload_0
   getfield ComputePrimes/primes [I
   areturn


.end method


.method public generatePrimes(I)I
   .limit locals 3
   .limit stack 7
   .field protected ps [I
   .field protected k I
   aload_0
   getfield ComputePrimes/n I
   istore_2
   aload_0
   aload_0
   getfield ComputePrimes/n I
   invokevirtual somePrimes/somePrimes(I)[I
   astore_1
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_0
   getfield ComputePrimes/number_of_primes I
   invokevirtual java/io/PrintStream/println(I)V
   if_icmplt TRUE_LABEL_5
   iconst_0
   goto NEXT_LABEL_6
TRUE_LABEL_5:
   iconst_1
NEXT_LABEL_6:
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload_1
   aload_0
   getfield ComputePrimes/n I
   iload_2
   isub
   arraylength
   invokevirtual java/io/PrintStream/println(I)V
   iload_2
   isub
   istore_2
   aload_0
   aload_0
   getfield ComputePrimes/number_of_primes I
   isub
   putfield ComputePrimes/number_of_primes I
   ireturn


.end method


.method public isPrime(I[II)B
   .limit locals 5
   .limit stack 4
   .field protected ans B
   .field protected tmp I
   istore_4
   iconst_1
   istore_3
   if_icmplt TRUE_LABEL_7
   iconst_0
   goto NEXT_LABEL_8
TRUE_LABEL_7:
   iconst_1
NEXT_LABEL_8:
   aload_0
   aload_0
   getfield ComputePrimes/p I
   aload_0
   getfield ComputePrimes/primes [I
   aload_0
   getfield ComputePrimes/k I
   arraylength
   invokevirtual modulo/modulo(II)I
   istore_4
   if_icmplt TRUE_LABEL_9
   iconst_0
   goto NEXT_LABEL_10
TRUE_LABEL_9:
   iconst_1
NEXT_LABEL_10:
   ifeq IF_FALSE_12
IF_TRUE_11:
   iconst_0
   istore_3
   goto IF_NEXT_13
IF_FALSE_12:
   iconst_1
   istore_3
IF_NEXT_13:
   iconst_1
   ireturn


.end method


.method public modulo(II)I
   .limit locals 2
   .limit stack 3
   if_icmplt TRUE_LABEL_14
   iconst_0
   goto NEXT_LABEL_15
TRUE_LABEL_14:
   iconst_1
NEXT_LABEL_15:
   ifeq IF_FALSE_17
IF_TRUE_16:
   goto IF_NEXT_18
IF_FALSE_17:
   if_icmplt TRUE_LABEL_19
   iconst_0
   goto NEXT_LABEL_20
TRUE_LABEL_19:
   iconst_1
NEXT_LABEL_20:
IF_NEXT_18:
   aload_0
   getfield ComputePrimes/a I
   ireturn


.end method
