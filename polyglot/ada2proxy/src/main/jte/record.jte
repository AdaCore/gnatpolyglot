@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.Record
@param Record rec
@param Boolean isBody

!{
    String recordName = rec.getFullyQualifiedName();
    String recordAccessName = rec.name.toPascalWithUnderscore() + "_Access";
}

@if(!isBody)
@else
   type ${recordAccessName} is access all ${recordName};
@endif
