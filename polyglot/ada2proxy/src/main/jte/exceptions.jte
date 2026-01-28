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
      , ${exception.getEnumIdentifier()}
   @endfor
      );
   for Exception_Kind use (
         Anon_Kind => 0
   @for(var exception : exceptions)
      , ${exception.getEnumIdentifier()}
         => ${String.valueOf(exception.getValue())}
   @endfor
      );

   function Identify_Exception (Id : Exception_Id) return Interfaces.C.Int;

@else

@for(var pack : ExceptionCollector.getPackagesToInclude(proxy))
with ${pack.getFullyQualifiedName()};
@endfor

package body Polyglot.Exceptions.${projectName.toPascalWithUnderscore()} is

   function Identify_Exception (Id : Exception_Id) return Interfaces.C.Int is
   begin
      @for(var exception : ExceptionCollector.getExceptions(proxy))
         if Id = Standard.${exception.getFullyQualifiedName()}'Identity then
            return Exception_Kind'Enum_Rep
               (${exception.getEnumIdentifier()});
         end if;
      @endfor
      if Id = Constraint_Error'Identity then
         return Interfaces.C.int(-4);
      elsif Id = Program_Error'Identity then
         return Interfaces.C.int(-3);
      elsif Id = Storage_Error'Identity then
         return Interfaces.C.int(-2);
      elsif Id = Tasking_error'Identity then
         return Interfaces.C.int(-1);
      end if;
      return Exception_Kind'Enum_Rep (Anon_Kind);
   end Identify_Exception;
@endif

end Polyglot.Exceptions.${projectName.toPascalWithUnderscore()};
