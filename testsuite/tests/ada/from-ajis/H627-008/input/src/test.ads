with Base;

package Test is

   package Base_Instance is new Base;

   --  This type should either be correctly bound (if generics are supported)
   --  or not bound at all - in any case, generated code should be correct
   --  and print something should be callable.
   subtype A_Type is Base_Instance.T;

   procedure Print_Something;

   function X return A_Type;

end Test;
