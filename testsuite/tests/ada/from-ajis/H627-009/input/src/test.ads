with Base; use Base;

package Test is

   type A_Type is abstract tagged private;

   function Get_Something
     (V : A_Type; X : A_Returned_Type) return A_Returned_Type is abstract;

private

   type A_Type is abstract tagged record
      null;
   end record;

end Test;
