<%--
vim: filetype=jteada
Template file to generate the exception handling ada files.
--%>

@import com.adacore.polyglot.ada2proxy.ExceptionCollector
@import com.adacore.polyglot.ada2proxy.AdaAPI
@import com.adacore.polyglot.ada2proxy.proxy.AdaProxy
@import com.adacore.polyglot.ada2proxy.proxy.AdaException
@import com.adacore.polyglot.proxy.Name
@import java.util.List
@param AdaAPI api
@param AdaProxy proxy
@param boolean isSource
@param Name projectName

!{
   List<AdaException> exceptions = ExceptionCollector.getExceptions(proxy);
}

@if (!isSource)

with Interfaces.C;

package Polyglot.Exceptions.${projectName.toPascalWithUnderscore()} is

   type Exception_Kind is (
      Anon_Kind
   @for(var exception : exceptions)
      , ${exception.name.toPascalWithUnderscore()}_Kind
   @endfor
      );
   for Exception_Kind use (
         Anon_Kind => 0
   @for(var exception : exceptions)
      , ${exception.name.toPascalWithUnderscore()}_Kind
         => ${String.valueOf(exception.getValue())}
   @endfor
      );

   function Identify_Exception (Id : Exception_Id) return Interfaces.C.Int;

@else

@for(var pack : ExceptionCollector.getPackagesToInclude(proxy))
with ${pack.getFullyQualifiedName()};
use ${pack.getFullyQualifiedName()};
@endfor

package body Polyglot.Exceptions.${projectName.toPascalWithUnderscore()} is

   function Identify_Exception (Id : Exception_Id) return Interfaces.C.Int is
   begin
      @for(var exception : ExceptionCollector.getExceptions(proxy))
         if Id = ${exception.name.toPascalWithUnderscore()}'Identity then
            return Exception_Kind'Enum_Rep
               (${exception.name.toPascalWithUnderscore()}_Kind);
         end if;
      @endfor
      return Exception_Kind'Enum_Rep (Anon_Kind);
   end Identify_Exception;
@endif

end Polyglot.Exceptions.Test;
